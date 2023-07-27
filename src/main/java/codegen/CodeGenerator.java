package codegen;

import config.Configuration;
import config.FunctionCall;
import model.Variable;
import table.MemoryTable;
import tree.DTE;
import tree.TokenType;

import java.util.LinkedList;
import java.util.List;

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
        if (body.token.type != TokenType.Body) {
            throw new IllegalArgumentException("Expected <body>, got " + body.token.type);
        }

        if (body.fson.token.type != TokenType.StS) {
            // TODO()
            throw new IllegalArgumentException("Not implemented yet.");
        } else {
            generateCodeForStatements(body.fson);
        }

    }

    private void generateCodeForStatements(DTE sts) throws Exception {
        if (sts.token.type != TokenType.StS) {
            throw new IllegalArgumentException("Expected StS, got " + sts.token.type);
        }

        List<DTE> statements = sts.getFlattenedSequence();
        for (DTE statement : statements) {
            generateSt(statement);
        }
    }

    private void generateSt(DTE st) throws Exception {
        if (st.token.type != TokenType.St) {
            throw new IllegalArgumentException("Expected St, got " + st.token.type);
        }

        // <id> = <E>
        if (st.fson.bro.token.type == TokenType.EQ) {
            generateAssignment(st.fson, st.fson.bro.bro);
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
        getVarFromId(id, MemoryTable.getInstance().getMemory("gm"), null);

        // dereference
        instructionsList.add(Instruction.lw(2, 2, 0)); // ba -> variable
        instructionsList.add(Instruction.sw(expRegister, 2, 0));

        Configuration.getInstance().freeRegister(expRegister);
    }

    private Variable getVarFromId(DTE id, Variable memory, Integer reg) {
        if (id == null || memory == null || id.fson == null) return null;

        // s.b
        // $2 = ba(s.b)

        // case split id -> Na | id.Na  (only these 2 for now)
        if (id.fson.token.type == TokenType.Na || id.token.type == TokenType.Na) {

            String varName;
            if (id.fson.token.type == TokenType.Na)
                varName = id.fson.getBorderWord();
            else varName = id.getBorderWord();

            Variable bindedVariable = memory.getStructComponent(varName);
            int register = Configuration.getInstance().getFirstFreeRegister();
            int rs = reg == null ? memory.getBaseAddress() : reg;
            int displacement = bindedVariable.getDisplacement();

            instructionsList.add(Instruction.addi(register, rs, displacement)); // addi $2 $27 4
            Configuration.getInstance().freeRegister(register);
            return bindedVariable;
        }

        // otherwise we have id.Na

        DTE structId = id.fson;
        DTE compNa = structId.bro.bro;

        Variable structVar = getVarFromId(structId, memory, null);
        return getVarFromId(compNa, structVar, 2);
    }

    public void printInstructions() {
        System.out.println("\n\n------ GENERATED INSTRUCTIONS ------");
        instructionsList.forEach(System.out::println);
        System.out.println("--------------------------");
    }
}
