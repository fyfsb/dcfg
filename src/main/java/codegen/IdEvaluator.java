package codegen;

import config.Configuration;
import model.Fun;
import model.VarReg;
import model.Variable;
import table.MemoryTable;
import tree.DTE;

import static codegen.ExpressionEvaluator.evaluateExpression;
import static util.Context.BPT;
import static util.Context.SPT;
import static util.Logger.log;
import static util.TypeUtils.checkTokenType;

public class IdEvaluator {
    private static CodeGenerator cg() {
        return CodeGenerator.getInstance();
    }


    public static VarReg evaluateId(DTE id, boolean lv) throws Exception {
        checkTokenType(id, "<id>");

        log("Evaluating id: " + id.getBorderWord());

        // id -> Na
        if (id.getFirstSon().isType("<Na>")) {
            log("Found <Na>:\n");
            DTE na = id.getFirstSon();
            VarReg result = bindVariableName(na);

            if (!lv) {
                String instr = Instruction.deref(result.register);
                cg().addInstruction(instr);
            }

            return result;
        }

        // id -> id.Na
        if (id.getNthSon(2).isType(".")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedNa = id.getNthSon(3);
//            int struct = evaluateId(flattened.get(0));
            VarReg structReg = evaluateId(nestedId, lv);
            String compName = nestedNa.getBorderWord();

            Variable boundComp = structReg.variable.getStructComponent(compName);

            if (boundComp != null) { // variable in struct
                // let j store base address of struct
                // generated instruction will be
                // addi j j displ(comp, struct)
                int j = structReg.register;
                int displ = boundComp.getDisplacement();

                // create instruction
                String instr = Instruction.addi(j, j, displ);
                // add instruction to the list
                cg().addInstruction(instr);

                VarReg result = new VarReg(boundComp, j);

                if (!lv) {
                    cg().addInstruction(Instruction.deref(result.register));
                }

                return result;
            }
        }

        // id -> id[E]
        if (id.getNthSon(2).isType("[")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedIndex = id.getNthSon(3);

            VarReg array = evaluateId(nestedId, lv);
            VarReg index = evaluateExpression(nestedIndex);

            // gpr(23) = enc(size(t))
            int arrSize = array.type.arraySize;
            // storing encoded size in $23
            cg().addInstruction("macro: gpr(23) = enc(" + arrSize + ", uint)");

            // mul(j', j', 23)
            cg().addInstruction("macro: mul($" + index.register + ", $" + index.register + ", $23)");

            // add j j j'
            String instr = Instruction.add(array.register, array.register, index.register);
            cg().addInstruction(instr);

            Configuration.getInstance().freeRegister(index.register);

            assert array.variable != null;
            VarReg result = new VarReg(array.register, array.variable.getType().getArrayCompTargetType());

            if (!lv) {
                cg().addInstruction(Instruction.deref(result.register));
            }

            return result;
        }

        // id -> id*
        if (id.getNthSon(2).isType("'")) {
            VarReg pointer = evaluateId(id.getFirstSon(), lv);

            // create instruction lw j j 0 ~ deref
            String instr = Instruction.deref(pointer.register);
            cg().addInstruction(instr);

            if (!lv) {
                cg().addInstruction(Instruction.deref(pointer.register));
            }

            return pointer;
        }

        if (id.getNthSon(2).isType("&")) {
            return bindVariableName(id.getFirstSon());
        }

        throw new IllegalArgumentException("Grammar error on \"" + id.getBorderWord() + "\"");
    }

    public static VarReg bindVariableName(DTE na) {
        checkTokenType(na, "<Na>");

        String name = na.getBorderWord();
        Variable bindedVariable = null;


        // try to bind from current function
        Fun cf = Configuration.getInstance().currentFunction();
        Variable cfMemory = cf.getMemoryStruct();
        if (cfMemory != null) {
            bindedVariable = cfMemory.getStructComponent(name);
        }

        if (bindedVariable != null) { // variable contained in function struct
            // base address is loaded into register j
            // with cf=f and bindedVariable=x, we get
            // addi j spt displ(x, $f) - size($f)
            int imm = bindedVariable.getDisplacement() - cf.getSize();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, SPT, imm);
            // add instruction to the list
            cg().addInstruction(instr);

            return new VarReg(bindedVariable, j);
        }

        // try to bind from gm
        try {
            Variable gm = MemoryTable.getInstance().gm();
            bindedVariable = gm.getStructComponent(name);
        } catch (Exception ignored) {
        }

        if (bindedVariable != null) { // variable is in global memory
            // generated command is
            // addi j bpt displ(x, $gm)
            int displ = bindedVariable.getDisplacement();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, BPT, displ);
            // add instruction to the list
            cg().addInstruction(instr);

            return new VarReg(bindedVariable, j);
        }

        throw new IllegalArgumentException("No variable with name " + name + " found.");
    }

}
