package config;

import codegen.CodeGenerator;
import model.Fun;
import model.Variable;
import table.FunctionTable;

import java.util.Stack;

public class Configuration {
    private int recursionDepth;
    private Stack<FunctionCall> stack;
    private boolean[] occupiedRegisters = new boolean[32];

    private Configuration(Stack<FunctionCall> stack) {
        this.stack = stack;
    }

    private static Configuration INSTANCE = null;

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration(null);
        }
        return INSTANCE;
    }

    public FunctionCall top() {
        return stack.peek();
    }

    public Fun currentFunction() {
        return top().getFunction();
    }

    public void freeRegister(int index) {
        index--;
        if (index < 0 || index > occupiedRegisters.length) return;
        occupiedRegisters[index] = false;
    }

    public void freeAllRegisters() {
        occupiedRegisters = new boolean[32];
    }

    public int getFirstFreeRegister() {
        for (int i = 0; i < occupiedRegisters.length; i++) {
            if (!occupiedRegisters[i]) {
                occupiedRegisters[i] = true;
                return i + 1;
            }
        }
        return -1;
    }

    public FunctionCall callFunction(Fun function, Variable resultDestination) {
        int displacement = 0;

        assert stack != null;
        if (!stack.isEmpty()) {
            displacement = currentFunction().getSize() + top().getDisplacement();
        }

        FunctionCall call = new FunctionCall(recursionDepth++, function, resultDestination, displacement);
        stack.add(call);
        return call;
    }

    public void initialize() throws Exception {
        stack = new Stack<>();

        // main
        Fun mainFunction = FunctionTable.getInstance().getFunction("main");
        FunctionCall mainCall = callFunction(mainFunction, null);

        CodeGenerator.getInstance().setProgram(mainCall);
    }
}
