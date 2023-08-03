package config;

import codegen.CodeGenerator;
import exceptions.memory.MemoryStructException;
import model.Fun;
import model.Variable;
import table.FunctionTable;
import table.MemoryTable;
import tree.DTE;

import java.util.*;

public class Configuration {

    record ConfigurationSnapshot(
            int rd,
            int nh,
            Map<String, Variable> ht,
            Stack<FunctionCall> st,
            DTE pr
    ) {
    }

    private List<ConfigurationSnapshot> configurations;

    private int recursionDepth;
    private int numberOnTheHeap;

    private Variable globalMemory;
    private Map<String, Variable> heapTable;
    private Stack<FunctionCall> stack;
    private DTE programRest;
    private final boolean[] occupiedRegisters = new boolean[32];

    public List<ConfigurationSnapshot> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ConfigurationSnapshot> configurations) {
        this.configurations = configurations;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public void setRecursionDepth(int recursionDepth) {
        this.recursionDepth = recursionDepth;
    }

    public int getNumberOnTheHeap() {
        return numberOnTheHeap;
    }

    public void setNumberOnTheHeap(int numberOnTheHeap) {
        this.numberOnTheHeap = numberOnTheHeap;
    }

    public Variable getGlobalMemory() {
        return globalMemory;
    }

    public void setGlobalMemory(Variable globalMemory) {
        this.globalMemory = globalMemory;
    }

    public Map<String, Variable> getHeapTable() {
        return heapTable;
    }

    public void setHeapTable(Map<String, Variable> heapTable) {
        this.heapTable = heapTable;
    }

    public Stack<FunctionCall> getStack() {
        return stack;
    }

    public void setStack(Stack<FunctionCall> stack) {
        this.stack = stack;
    }

    public DTE getProgramRest() {
        return programRest;
    }

    public void setProgramRest(DTE programRest) {
        this.programRest = programRest;
    }

    private Configuration(Variable globalMemory, Map<String, Variable> heapTable, Stack<FunctionCall> stack) {
        this.globalMemory = globalMemory;
        this.heapTable = heapTable;
        this.stack = stack;

        this.configurations = new ArrayList<>();
    }

    private static Configuration INSTANCE = null;

    public static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Configuration(null, null, null);
        }
        return INSTANCE;
    }

    public FunctionCall top() {
        return stack.peek();
    }

    public Fun currentFunction() {
        return top().getFunction();
    }

    public ConfigurationSnapshot get(int index) {
        if (index < 0 || index > configurations.size()) return null;
        return configurations.get(index);
    }

    private void storeSnapshot() {
        configurations.add(new ConfigurationSnapshot(recursionDepth, numberOnTheHeap, heapTable, stack, programRest));
    }

    public void freeRegister(int index) {
        index--;
        if (index < 0 || index > occupiedRegisters.length) return;
        occupiedRegisters[index] = false;
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
        if (stack != null && !stack.isEmpty()) {
            displacement = currentFunction().getSize() + top().getDisplacement();
        }
        FunctionCall call = new FunctionCall(recursionDepth++, function, resultDestination, displacement);
        stack.add(call);
        return call;
    }

    public void initialize() throws Exception {
        globalMemory = MemoryTable.getInstance().getMemory("gm");
        stack = new Stack<>();
        heapTable = new HashMap<>();


        // main
        Fun mainFunction = FunctionTable.getInstance().getFunction("f");
        FunctionCall mainCall = callFunction(mainFunction, null);

        CodeGenerator.getInstance().setProgram(mainCall);
    }
}
