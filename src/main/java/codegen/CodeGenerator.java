package codegen;

import config.Configuration;
import config.FunctionCall;
import grammar.Grammar;
import grammar.Symbol;
import model.Fun;
import model.VarType;
import model.Variable;
import table.MemoryTable;
import tree.DTE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static util.TypeUtils.checkTokenType;

public class CodeGenerator {
    private static CodeGenerator INSTANCE = null;
    private FunctionCall program; // st(main, 0)
    private List<String> instructionsList;

    private CodeGenerator() {
    }

    private Grammar g;

    public void setGrammar(Grammar g) {
        this.g = g;
    }

    public static CodeGenerator getInstance() {
        if (INSTANCE == null) INSTANCE = new CodeGenerator();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
    }

    public void setProgram(FunctionCall program) {
        this.program = program;
    }

    public void generateCode() throws Exception {
        instructionsList = new LinkedList<>();

        System.out.println("initialized instructions list");
        System.out.println("starting generation for `main`");

        generateCodeForFunctionCall(program);
    }

    private void generateCodeForFunctionCall(FunctionCall call) throws Exception {
        DTE body = call.getFunction().getBody();
        checkTokenType(body, "<body>");

        if (!body.getFirstSon().isType("<StS>")) {
            // TODO()
            //throw new IllegalArgumentException("Not implemented yet.");
            System.out.println("support for return statements will be added later");
        } else {
            generateCodeForStatements(body.getFirstSon());
        }

    }

    private void generateCodeForStatements(DTE sts) throws Exception {
        checkTokenType(sts, "<StS>");

        List<DTE> statements = sts.getFlattenedSequence();
        int counter = 0;
        for (DTE statement : statements) {
            System.out.println("Statement #" + counter++ + ": " + statement.getBorderWord());
            generateSt(statement);
            Configuration.getInstance().freeAllRegisters();
        }
    }

    private void generateSt(DTE st) throws Exception {
        checkTokenType(st, "<St>");

        // <id> = <E>
        if (st.getFirstSon().getBrother().isType("=")) {
            DTE id = st.getFirstSon();
            DTE exp = st.getFirstSon().getBrother().getBrother();

            System.out.println("Assignment of form <id> = <E> -> \n id: " + id.getBorderWord() + "\n E: " + exp.getBorderWord());
            generateAssignment(st.getFirstSon(), st.getFirstSon().getBrother().getBrother());
        }
    }

    private void generateAssignment(DTE id, DTE value) throws Exception {

        // <id> = value
        // E -> T -> F -> C -> DiS -> Di -> 1
        VariableRegister varRegId = evaluateId(id);
        VariableRegister varRegValue;

        // case split for type of value
        if (value.isType("<E>")) {
            varRegValue = evaluateExpression(value);
        } else if (value.isType("<BE>")) {
            varRegValue = evaluateBooleanExpression(value);
        } else if (value.isType("<CC>")) {
            varRegValue = evaluateCharacterConstant(value);
        } else if (value.isType("new")) {
            varRegValue = evaluatePointerAllocation(value);
        } else { // id = Na() | id = Na(PaS) left
            checkTokenType(value, "<Na>");

            //TODO()
            throw new IllegalArgumentException("not implemented yet");
        }

        String instr = Instruction.sw(varRegValue.reg(), varRegId.reg(), 0);
        instructionsList.add(instr);
        Configuration.getInstance().freeRegister(varRegValue.reg());

        Configuration.getInstance().freeRegister(varRegValue.reg());
    }

    public void printInstructions() {
        System.out.println("\n\n------ GENERATED INSTRUCTIONS ------");
        instructionsList.forEach(System.out::println);
        System.out.println("--------------------------");
    }

    public VariableRegister evaluateNumberConstant(DTE constant) {
        checkTokenType(constant, "<C>");

        int register = Configuration.getInstance().getFirstFreeRegister();
        String value = constant.getBorderWord();

        int intValue;
        VarType type;

        if (value.charAt(value.length() - 1) != 'u') {
            intValue = Integer.parseInt(value);
            type = VarType.INT_TYPE;
        } else {
            intValue = Integer.parseInt(value.substring(0, value.length() - 1));
            type = VarType.UINT_TYPE;
        }

        String instr = Instruction.addi(register, register, intValue);
        instructionsList.add(instr);

        return new VariableRegister(null, register, type);
    }

    public VariableRegister evaluateCharacterConstant(DTE charConstant) {
        String value = charConstant.getBorderWord();

        List<String> terminals = g.getTerminals().stream().map(Symbol::getContent).filter(term -> term.length() == 1).toList();
        if (!terminals.contains(value)) {
            throw new IllegalArgumentException("Expected char constant, got " + value);
        }

        int register = Configuration.getInstance().getFirstFreeRegister();

        String instr = Instruction.addi(register, register, value.charAt(0));
        instructionsList.add(instr);

        return new VariableRegister(null, register, VarType.CHAR_TYPE);
    }

    public VariableRegister evaluateBooleanConstant(DTE bc) {
        checkTokenType(bc, "<BC>");

        int register = Configuration.getInstance().getFirstFreeRegister();
        int value = bc.getFirstSon().labelContent().equals("true") ? 1 : 0;

        String instr = Instruction.addi(register, register, value);
        instructionsList.add(instr);

        return new VariableRegister(null, register, VarType.BOOL_TYPE);
    }

    public VariableRegister evaluateExpression(DTE expression) throws Exception {
        checkTokenType(expression, "<E>", "<T>", "<F>");

        System.out.println("Evaluating expression:");
        expression.printTree();

        // any of <E>, <T>, <F> token's derivations has at most 3 children.
        DTE first;
        DTE second = null;
        DTE third = null;

        first = expression.getFirstSon();
        if (first != null && first.getBrother() != null) {
            second = first.getBrother();
        }
        if (second != null && second.getBrother() != null) {
            third = second.getBrother();
        }

        if (third != null) {
            // e' binOp e'' OR (E)

            // handle (<E>) separately
            if (first.isType("(")) {
                return evaluateExpression(second);
            }

            VariableRegister leftRegister = evaluateExpression(first);
            VariableRegister rightRegister = evaluateExpression(third);

            return evaluateBinaryOperation(leftRegister.reg(), rightRegister.reg(), second);
        }

        if (second != null) { // only possible case is -F (unary minus on factor)
            System.out.println("Unary minus detected.");

            checkTokenType(first, "-1");

            VariableRegister f = evaluateExpression(second);
            // perform unary minus operation
            String instr = Instruction.sub(f.reg(), 0, f.reg());
            instructionsList.add(instr);

            return f;
        }

        if (first != null) { // T | F | id | C
            checkTokenType(first, "<T>", "<F>", "<id>", "<C>");

            if (first.isType("<T>") || first.isType("<F>")) {
                return evaluateExpression(first);
            }

            if (first.isType("<id>")) {
                return evaluateId(first);
            }

            return evaluateNumberConstant(first);
        }

        throw new Exception("Grammar error");
    }

    public VariableRegister evaluateBooleanExpression(DTE be) throws Exception {
        checkTokenType(be, "<BE>", "<BT>", "<BF>");

        DTE first = null;
        DTE second = null;
        DTE third = null;

        first = be.getFirstSon();
        assert first != null;

        if (first.isType("<id>")) {
            return evaluateId(first);
        }

        if (first.isType("<Atom>")) {
            return evaluateAtom(first);
        }

        if (first.isType("<BF>")) {
            return evaluateBooleanExpression(first);
        }

        if (first.isType("<BE>")) { // <BE> || <BT>
            second = first.getBrother();
            checkTokenType(second, "||");

            third = second.getBrother();
            checkTokenType(third, "<BT>");

            VariableRegister left = evaluateBooleanExpression(first);
            VariableRegister right = evaluateBooleanExpression(third);

            String instr = Instruction.or(left.reg(), left.reg(), right.reg());
            instructionsList.add(instr);

            Configuration.getInstance().freeRegister(right.reg());
            return new VariableRegister(null, left.reg(), VarType.BOOL_TYPE);
        }

        if (first.isType("<BT>")) { // Either <BT> or <BT> && <BF>
            if (first.getBrother() == null) return evaluateBooleanExpression(first);

            second = first.getBrother();
            checkTokenType(second, "&&");

            third = second.getBrother();
            checkTokenType(third, "<BF>");

            VariableRegister left = evaluateBooleanExpression(first);
            VariableRegister right = evaluateBooleanExpression(third);

            String instr = Instruction.and(left.reg(), left.reg(), right.reg());
            instructionsList.add(instr);

            Configuration.getInstance().freeRegister(right.reg());
            return new VariableRegister(null, left.reg(), VarType.BOOL_TYPE);
        }

        if (first.isType("!")) {
            second = first.getBrother();
            checkTokenType(second, "<BF>");

            VariableRegister factor = evaluateBooleanExpression(second);
            //TODO()
            throw new IllegalArgumentException();
        }

        if (first.isType("(")) {
            second = first.getBrother();
            checkTokenType(second, "<BE>");

            return evaluateBooleanExpression(second);
        }

        throw new IllegalArgumentException("Grammar error! \"" + be.getBorderWord() + "\"");
    }

    public VariableRegister evaluateAtom(DTE atom) throws Exception {
        checkTokenType(atom, "<Atom>");

        DTE left = null, op = null, right = null;

        left = atom.getFirstSon();
        assert left != null;

        if (left.isType("<BC>")) {
            return evaluateBooleanConstant(left);
        }

        op = left.getBrother();
        assert op != null;
        right = op.getBrother();
        assert right != null;

        checkTokenType(left, "<E>");
        checkTokenType(right, "<E>");

        VariableRegister leftVar = evaluateExpression(left);
        VariableRegister rightVar = evaluateExpression(right);
        String instr;

        // TODO()
        throw new IllegalArgumentException("Not implemented yet");
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

        System.out.println("Evaluating id: " + id.getBorderWord());

        // id -> Na
        if (id.getFirstSon().isType("<Na>")) {
            System.out.println("Found <Na>:\n");
            DTE na = id.getFirstSon();
            na.printTree();
            return bindVariableName(na);
        }

        // id -> id.Na
        if (id.getFirstSon().getBrother().isType(".")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedNa = id.getFirstSon().getBrother().getBrother();
//            int struct = evaluateId(flattened.get(0));
            VariableRegister structReg = evaluateId(nestedId);
            String compName = nestedNa.getBorderWord();

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
        if (id.getFirstSon().getBrother().isType("[")) {
            DTE nestedId = id.getFirstSon();
            DTE nestedIndex = id.getFirstSon().getBrother().getBrother();

            VariableRegister array = evaluateId(nestedId);
            VariableRegister index = evaluateExpression(nestedIndex);

            // gpr(23) = enc(size(t)
            int arrSize = array.var().getType().arraySize;
            // storing encoded size in $23
            instructionsList.add("macro: gpr(23) = enc(" + arrSize + ", uint)");

            // mul(j', j', 23)
            instructionsList.add("macro: mul($" + index.reg() + ", $" + index.reg() + ", $23)");

            // add j j j'
            String instr = Instruction.add(array.reg(), array.reg(), index.reg());
            instructionsList.add(instr);

            Configuration.getInstance().freeRegister(index.reg());
            return new VariableRegister(array.var(), array.reg(), index.type());
        }

        // id -> id*
        if (id.getFirstSon().getBrother().isType("`")) {
            VariableRegister pointer = evaluateId(id.getFirstSon());

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

    public VariableRegister evaluatePointerAllocation(DTE dte) {
        return null;
    }

    public record VariableRegister(Variable var, int reg, VarType type) {
        public VariableRegister(Variable var, int reg) {
            this(var, reg, null);
        }
    }
}
