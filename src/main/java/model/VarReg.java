package model;

public class VarReg {
    public final Variable variable;
    public final int register;
    public final VarType type;

    public VarReg(Variable variable, int register) {
        this.variable = variable;
        this.register = register;
        this.type = variable.getType();
    }

    public VarReg(int register, VarType type) {
        this.register = register;
        this.type = type;
        this.variable = null;
    }
}
