package model;

public class Function {
    private final String name;
    private final VarType returnType;
    private final Variable memoryStruct;
    private final int numParameters;

    public Function(String name,
                    VarType returnType,
                    Variable memoryStruct,
                    int numParameters) {
        this.name = name;
        this.returnType = returnType;
        this.memoryStruct = memoryStruct;
        this.numParameters = numParameters;
    }

    public String getName() {
        return name;
    }

    public VarType getReturnType() {
        return returnType;
    }

    public Variable getMemoryStruct() {
        return memoryStruct;
    }

    public int getNumParameters() {
        return numParameters;
    }
}
