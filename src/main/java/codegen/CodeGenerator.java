package codegen;

import config.Configuration;
import config.FunctionCall;
import grammar.Grammar;
import model.Fun;
import model.VarReg;
import table.FunctionTable;
import tree.DTE;

import java.util.LinkedList;
import java.util.List;

import static codegen.ConstantEvaluator.evaluateCharacterConstant;
import static codegen.ExpressionEvaluator.evaluateBooleanExpression;
import static codegen.ExpressionEvaluator.evaluateExpression;
import static codegen.IdEvaluator.evaluateId;
import static util.Context.HMAX;
import static util.Context.HPT;
import static util.Logger.log;
import static util.TypeUtils.checkSameTypes;
import static util.TypeUtils.checkTokenType;

public class CodeGenerator {
    private static CodeGenerator INSTANCE = null;
    private FunctionCall program; // st(main, 0)
    private List<String> instructionsList;

    private CodeGenerator() {
    }

    public void addInstruction(String instr) {
        instructionsList.add(instr);
    }

    public Grammar g;

    public void setGrammar(Grammar g) {
        this.g = g;
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

        log("initialized instructions list");
        log("starting generation for `main`");

        generateCodeForFunctionCall(program);
    }

    private void generateCodeForFunctionCall(FunctionCall call) throws Exception {
        DTE body = call.getFunction().getBody();
        checkTokenType(body, "<body>");

        if (!body.getFirstSon().isType("<StS>")) {
            // TODO()
            //throw new IllegalArgumentException("Not implemented yet.");
            log("support for return statements will be added later");
        } else {
            generateStS(body.getFirstSon());
        }

    }

    private void generateStS(DTE sts) throws Exception {
        checkTokenType(sts, "<StS>");

        List<DTE> statements = sts.getFlattenedSequence();
        int counter = 0;
        for (DTE statement : statements) {
            log("Statement #" + counter++ + ": " + statement.getBorderWord());
            generateSt(statement);
            Configuration.getInstance().freeAllRegisters();
        }
    }

    private void generateSt(DTE st) throws Exception {
        checkTokenType(st, "<St>");

        // <id> = <E> | <id> = <BE> | <id> = <CC> | <id> = <Na>(<PaS>?) | <id> = new <Na>*
        if (st.getNthSon(2).isType("=")) {
            DTE id = st.getFirstSon();
            DTE exp = st.getNthSon(3);

            log("Assignment of form <id> = <E> -> \n id: " + id.getBorderWord() + "\n E: " + exp.getBorderWord());
            generateAssignment(id, exp);
        }

        // while <BE> { <StS> }
        else if (st.getFirstSon().isType("while")) {
            generateLoop(st.getFirstSon());
        }

        // if <BE> { <StS> } | if <BE> { <StS> } else { <StS> }
        else if (st.getFirstSon().isType("if")) {
            generateIfStatement(st.getFirstSon());
        }

        // Invalid statement | Unhandled case
        else {
            throw new IllegalArgumentException("Grammar error on \"" + st.getBorderWord() + "\"");
        }
    }

    private void generateAssignment(DTE id, DTE value) throws Exception {
        // <id> = value
        // E -> T -> F -> C -> DiS -> Di -> 1
        VarReg varRegId = evaluateId(id, true);
        VarReg varRegValue;

        // case split for type of value
        if (value.isType("<E>")) {
            varRegValue = evaluateExpression(value);
        } else if (value.isType("<BE>")) {
            varRegValue = evaluateBooleanExpression(value);
        } else if (value.isType("<CC>")) {
            varRegValue = evaluateCharacterConstant(value);
        } else if (value.isType("new")) {
            instructionsList.add(Instruction.sw(HPT, varRegId.register, 0));

            int varSize = varRegId.type.size;
            increaseHeapPointer(varSize);

            return;
        } else { // id = Na() | id = Na(PaS) left
            checkTokenType(value, "<Na>");

            String functionName = value.getBorderWord();
            Fun function = FunctionTable.getInstance().getFunction(functionName);
            /*
            Fill in parameters
             */
            FunctionCall call = Configuration.getInstance().callFunction(function, null);
            generateCodeForFunctionCall(call);

            throw new IllegalArgumentException("not implemented yet");
        }

        checkSameTypes(varRegId.type, varRegValue.type);

        String instr = Instruction.sw(varRegValue.register, varRegId.register, 0);
        instructionsList.add(instr);
        Configuration.getInstance().freeRegister(varRegValue.register);

        Configuration.getInstance().freeRegister(varRegValue.register);
    }

    public void printInstructions() {
        System.out.println("\n\n------ GENERATED INSTRUCTIONS ------");
        instructionsList.forEach(System.out::println);
        System.out.println("--------------------------");
    }

    public void generateLoop(DTE dte) throws Exception {
        checkTokenType(dte, "while");

        // while <E> { <StS> }

        DTE expressionNode = dte.getBrother();
        assert expressionNode != null && expressionNode.getBrother() != null;

        DTE bodyNode = dte.getNthBrother(3);
        assert bodyNode != null;


        int before = instructionsList.size();
        VarReg expression = evaluateBooleanExpression(expressionNode);
        int expressionCodeSize = instructionsList.size() - before;


        // need to add branch jump of size |code(whileBody)| + 2
        before = instructionsList.size();
        generateStS(bodyNode);
        int bodySize = instructionsList.size() - before;

        String instr = Instruction.beqz(expression.register, bodySize + 2);
        instructionsList.add(before, instr);

        // jump back to start of loop
        int jumpBackSize = -(expressionCodeSize + bodySize + 1);
        instructionsList.add(Instruction.blez(0, jumpBackSize));
    }

    public void generateIfStatement(DTE dte) throws Exception {
        checkTokenType(dte, "if");

        if (dte.getSiblingCount() == 9) {
            generateIfElseStatement(dte);
            return;
        }

        DTE ifConditionNode = dte.getBrother();
        DTE ifPart = dte.getNthBrother(3);

        VarReg ifCondition = evaluateBooleanExpression(ifConditionNode);

        int before = instructionsList.size();
        generateStS(ifPart);
        int ifPartSize = instructionsList.size() - before;

        instructionsList.add(before, Instruction.beqz(ifCondition.register, ifPartSize + 1));
    }

    public void generateIfElseStatement(DTE ifElse) throws Exception {
        checkTokenType(ifElse, "if");
        checkTokenType(ifElse.getNthBrother(5), "else");

        DTE ifConditionNode = ifElse.getBrother();
        DTE ifPart = ifElse.getNthBrother(3);
        DTE elsePart = ifElse.getNthBrother(7);

        VarReg ifCondition = evaluateBooleanExpression(ifConditionNode);

        // need to add branch jump of size |code(ifPart)| + 2
        int before = instructionsList.size();
        generateStS(ifPart);
        int ifPartSize = instructionsList.size() - before;

        instructionsList.add(before, Instruction.beqz(ifCondition.register, ifPartSize + 2));

        before = instructionsList.size();
        generateStS(elsePart);
        int elsePartSize = instructionsList.size() - before;

        instructionsList.add(before, Instruction.beq(0, elsePartSize + 1));
    }

    public void increaseHeapPointer(int size) {
        instructionsList.addAll(
                List.of(
                        Instruction.addi(HPT, HPT, size),
                        Instruction.subi(1, HPT, HMAX),
                        Instruction.bltz(1, 4),
                        "macro: gpr(1) = x",
                        Instruction.sysc(),
                        Instruction.addi(1, HPT, -size),
                        Instruction.addi(2, 0, size / 4),
                        "zero(1, 2)"
                ));
    }
}
