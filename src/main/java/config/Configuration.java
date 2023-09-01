package config;

import exceptions.function.FunctionException;
import model.Fun;
import model.VarReg;
import table.FunctionTable;

import java.util.Stack;

public class Configuration {
    private int recursionDepth;
    private final Stack<FunctionCall> stack;
    private boolean[] occupiedRegisters = new boolean[32];

    private Configuration() {
        this.stack = new Stack<>();
    }

    private static Configuration INSTANCE = null;

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    public FunctionCall top() {
        return stack.peek();
    }

    public void popStack() {
        try {
            stack.pop();
            recursionDepth--;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fun currentFunction() {
        return top().getFunction();
    }

    public void freeRegister(int index) {
        index--;
        if (index < 0 || index > occupiedRegisters.length) return;
        occupiedRegisters[index] = false;
    }

    public void freeAllRegisters(int retained) {
        occupiedRegisters = new boolean[32];
        for (int i = 0; i < retained && i < occupiedRegisters.length; i++) {
            occupiedRegisters[i] = true;
        }
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

    public FunctionCall callFunction(String functionName, VarReg resultDestination) throws FunctionException {
        int displacement = 0;

        if (!stack.isEmpty()) {
            displacement = currentFunction().getSize() + top().getDisplacement() + 4;
        }

        Fun function = FunctionTable.getInstance().getFunction(functionName);
        FunctionCall call = new FunctionCall(recursionDepth++, function, resultDestination, displacement);
        stack.add(call);
        return call;
    }

    public static void reset() {
        INSTANCE = null;
    }
}
