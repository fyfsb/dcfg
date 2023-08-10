package codegen;

import config.Configuration;
import config.FunctionCall;
import model.Fun;
import model.VarType;
import model.Variable;
import table.MemoryTable;
import tree.DTE;

import java.util.LinkedList;
import java.util.List;

import static util.TypeUtils.checkTokenType;

public class CodeGenerator {
    private static CodeGenerator INSTANCE = null;
    private FunctionCall program; // st(main, 0)
    private List<String> instructionsList;

    private CodeGenerator() {
    }

    public static CodeGenerator getInstance() {
        if (INSTANCE == null) INSTANCE = new CodeGenerator();
        return INSTANCE;
    }

    public void setProgram(FunctionCall program) {
        this.program = program;
    }

    public void generateCode() throws Exception {
        instructionsList = new LinkedList<>();
        generateCodeForFunctionCall(program);
    }

    private void generateCodeForFunctionCall(FunctionCall call) throws Exception {
        DTE body = call.getFunction().getBody();
        checkTokenType(body, "<body>");

        System.out.println("BODY");
        body.printTree();

        if (!body.getFirstSon().isType("<StS>")) {
            // TODO()
            //throw new IllegalArgumentException("Not implemented yet.");
            System.out.println("not implemented yet!");
        } else {
            generateCodeForStatements(body.getFirstSon());
        }

    }

    private void generateCodeForStatements(DTE sts) throws Exception {
        checkTokenType(sts, "<StS>");

        List<DTE> statements = sts.getFlattenedSequence();
        for (DTE statement : statements) {
            generateSt(statement);
        }
    }

    private void generateSt(DTE st) throws Exception {
        checkTokenType(st, "<St>");

        // <id> = <E>
        if (st.getFirstSon().getBrother().isType("=")) {
            generateAssignment(st.getFirstSon(), st.getFirstSon().getBrother().getBrother());
        }
    }

    private void generateAssignment(DTE id, DTE value) throws Exception {

        // <id> = value
        // E -> T -> F -> C -> DiS -> Di -> 1

        // "1" -> 1
        int E = Integer.parseInt(value.getBorderWord());

        int expRegister = Configuration.getInstance().getFirstFreeRegister();
        instructionsList.add(Instruction.addi(expRegister, 0, E));
        // addi $1 $0 1 // $1 = 1


        // s.b = 1
        //
        // st.cf
        VariableRegister varRegId = evaluateId(id);
        VariableRegister varRegValue = evaluateExpression(value);

        String instr = Instruction.sw(varRegValue.reg(), varRegId.reg(), 0);
        instructionsList.add(instr);
        Configuration.getInstance().freeRegister(varRegValue.reg());

//        getVarFromId(id, MemoryTable.getInstance().getMemory("$gm"), null);
//
//        // dereference
//        instructionsList.add(Instruction.lw(2, 2, 0)); // ba -> variable
//        instructionsList.add(Instruction.sw(expRegister, 2, 0));

        Configuration.getInstance().freeRegister(expRegister);
    }

    public void printInstructions() {
        System.out.println("\n\n------ GENERATED INSTRUCTIONS ------");
        instructionsList.forEach(System.out::println);
        System.out.println("--------------------------");
    }

    public VariableRegister evaluateNumberConstant(DTE constant) {
        checkTokenType(constant, "<C>");

        int register = Configuration.getInstance().getFirstFreeRegister();
        int value = Integer.parseInt(constant.getBorderWord());

        String instr = Instruction.addi(register, register, value);
        instructionsList.add(instr);

        return new VariableRegister(null, register, VarType.INT_TYPE);
    }

    public VariableRegister evaluateExpression(DTE expression) throws Exception {
        checkTokenType(expression, "<E>", "<T>", "<F>");

        List<DTE> flattened = expression.getFlattenedSequence();

        switch (flattened.size()) {
            case 3 -> { // e' binOp e'' OR (E)
                // handle (E) separately
                if (flattened.get(0).isType("(")) {
                    return evaluateExpression(flattened.get(1));
                }

                DTE left = flattened.get(0);
                DTE binOp = flattened.get(1);
                DTE right = flattened.get(2);

                VariableRegister leftRegister = evaluateExpression(left);
                VariableRegister rightRegister = evaluateExpression(right);

                return evaluateBinaryOperation(leftRegister.reg(), rightRegister.reg(), binOp);
            }

            case 2 -> { // only possible case is -F (unary minus on factor)
                DTE factor = flattened.get(1);

                VariableRegister f = evaluateExpression(factor);

                // perform unary minus operation
                String instr = Instruction.sub(f.reg(), 0, f.reg());
                instructionsList.add(instr);

                return f;
            }

            case 1 -> { // T | F | id | C
                DTE dte = flattened.get(0);
                checkTokenType(dte, "<T>", "<F>", "<id>", "<C>");

                if (dte.isType("<T>") || dte.isType("<F>")) {
                    return evaluateExpression(dte);
                }

                if (dte.isType("<id>")) {
                    return evaluateId(dte);
                }

                return evaluateNumberConstant(dte);
            }
            default -> throw new Exception("Grammar error");
        }
    }

    public VariableRegister evaluateBinaryOperation(int left, int right, DTE binOp) {
        String instr = switch (binOp.labelContent()) {
            case "+" -> Instruction.add(left, left, right);
            case "-" -> Instruction.sub(left, left, right);

            // TODO(add other operations)
            default -> throw new IllegalArgumentException("Expected binary operator, got " + binOp.labelContent());
        };

        instructionsList.add(instr);
        Configuration.getInstance().freeRegister(right);
        return new VariableRegister(null, left, VarType.INT_TYPE);
    }

    public VariableRegister evaluateId(DTE id) throws Exception {
        checkTokenType(id, "<id>");

        List<DTE> flattened = id.getFlattenedSequence();

        // id -> Na
        if (flattened.get(0).isType("<Na>")) {
            return bindVariableName(id.getFirstSon());
        }

        // id -> id.Na
        if (flattened.get(1).isType(".")) {
//            int struct = evaluateId(flattened.get(0));
            VariableRegister structReg = evaluateId(flattened.get(0));
            String compName = flattened.get(2).getBorderWord();

            Variable boundComp = structReg.var().getStructComponent(compName);

            if (boundComp != null) { // variable in struct
                // let j store base address of struct
                // generated instruction will be
                // addi j j displ(comp, struct)
                int j = structReg.reg();
                int displ = boundComp.getDisplacement();

                // create instruction
                String instr = Instruction.addi(j, j, displ);
                // add instruction to the list
                instructionsList.add(instr);

                return new VariableRegister(boundComp, j);
            }
        }

        // id -> id[E]
        if (flattened.get(1).isType("[")) {
            VariableRegister array = evaluateId(flattened.get(0));
            VariableRegister index = evaluateExpression(flattened.get(2));

            // gpr(23) = enc(size(t)
            int arrSize = array.var().getType().arraySize;
            // storing encoded size in $23
            instructionsList.add("macro: gpr(23) = enc(" + arrSize + ", uint)");

            // mul(j', j', 23)
            instructionsList.add("macro: mul(" + index.reg() + ", " + index.reg() + ", 23");

            // add j j j'
            String instr = Instruction.add(array.reg(), array.reg(), index.reg());
            instructionsList.add(instr);

            Configuration.getInstance().freeRegister(index.reg());
            return new VariableRegister(array.var(), array.reg(), index.type());
        }

        // id -> id*
        if (flattened.get(1).isType("`")) {
            VariableRegister pointer = evaluateId(flattened.get(0));

            // create instruction lw j j 0 ~ deref
            String instr = Instruction.lw(pointer.reg(), pointer.reg(), 0);
            instructionsList.add(instr);

            return null;
        }

        return null;
    }

    public VariableRegister bindVariableName(DTE na) throws Exception {
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
            int spt = 29;
            int imm = bindedVariable.getDisplacement() - cf.getSize();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, spt, imm);
            // add instruction to the list
            instructionsList.add(instr);

            return new VariableRegister(bindedVariable, j);
        }

        // try to bind from gm
        Variable gm = MemoryTable.getInstance().getMemory("$gm");
        bindedVariable = gm.getStructComponent(name);

        if (bindedVariable != null) { // variable is in global memory
            // generated command is
            // addi j bpt displ(x, $gm)
            int bpt = 28;
            int displ = bindedVariable.getDisplacement();
            int j = Configuration.getInstance().getFirstFreeRegister();

            // create instruction
            String instr = Instruction.addi(j, bpt, displ);
            // add instruction to the list
            instructionsList.add(instr);

            return new VariableRegister(bindedVariable, j);
        }

        throw new IllegalArgumentException("No variable with name " + name + " found.");
    }

    public record VariableRegister(Variable var, int reg, VarType type) {
        public VariableRegister(Variable var, int reg) {
            this(var, reg, null);
        }
    }
}
