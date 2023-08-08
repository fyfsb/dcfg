package model;

public class Variable {
    private final String name;
    private int baseAddress;
    private final VarType type;

    private int displacement;

    public Variable(String name, int baseAddress, VarType type, int displacement) {
        this.name = name;
        this.baseAddress = baseAddress;
        this.type = type;
        this.displacement = displacement;
    }

    public String getName() {
        return name;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public VarType getType() {
        return type;
    }

    public void setBaseAddress(int baseAddress) {
        this.baseAddress = baseAddress;
    }

    public void setDisplacement(int displacement) {
        this.displacement = displacement;
    }

    public int getDisplacement() {
        return displacement;
    }

    public Variable getStructComponent(String name) {
        return type.structComponents.getOrDefault(name, null);
    }

    @Override
    public String toString() {
        return "Variable(name=" + name + ",ba=" + baseAddress + ",displ=" + displacement + ",typeClass=" + type.typeClass + ",struct components=" + type.structComponents + ")";
    }
}
