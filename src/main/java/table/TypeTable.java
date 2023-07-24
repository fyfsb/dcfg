package table;

import exceptions.typedef.*;
import model.VarType;
import model.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeTable {
    private final Map<String, VarType> table;
    private static TypeTable INSTANCE;

    private TypeTable() {
        table = new HashMap<>();
        fillWithPrimitives();
    }

    public static TypeTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TypeTable();
        return INSTANCE;
    }

    public VarType getType(String name) throws TypeNotDefinedException {
        VarType type = table.getOrDefault(name, VarType.UNDEFINED_TYPE);
        if (type == VarType.UNDEFINED_TYPE) {
            throw new TypeNotDefinedException(name);
        }
        return type;
    }

    private boolean isNameDistinct(String name) {
        return !table.containsKey(name);
    }

    private void checkTypeDefined(String name) throws TypeNotDefinedException {
        if (getType(name) == VarType.UNDEFINED_TYPE)
            throw new TypeNotDefinedException(name);
    }

    public void addType(VarType.Builder builder) throws TypeDefException {
        // check names distinct
        if (!isNameDistinct(builder.getName()))
            throw new TypeDefNameAlreadyExistsException(builder);

        // case split
        switch (builder.getTypeClass()) {
            case ARRAY -> addArrayType(builder);
            case POINTER -> addPointerType(builder);
            case STRUCT -> addStructType(builder);
            default -> throw new TypeDefUnsupportedOperationException();
        }
    }

    private void addArrayType(VarType.Builder arrayBuilder) throws TypeDefException {
        VarType arrayComponentTarget = table.getOrDefault(arrayBuilder.getaCTargetName(), VarType.UNDEFINED_TYPE);
        checkTypeDefined(arrayBuilder.getaCTargetName());
        if (arrayBuilder.getArraySize() <= 0)
            throw new TypeDefInvalidArraySizeException(arrayBuilder);
        VarType type = arrayBuilder.setSize(arrayBuilder.getArraySize() * arrayComponentTarget.size).build();
        table.put(type.name, type);
    }

    private void addPointerType(VarType.Builder pointerBuilder) {
        VarType type = pointerBuilder.build();
        table.put(type.name, type);
    }

    private void addStructType(VarType.Builder structBuilder) throws TypeDefException {
        var type = createStructType(structBuilder);
        table.put(type.name, type);
    }

    public VarType createStructType(VarType.Builder structBuilder) throws TypeDefException {
        if (structBuilder.getStructComponentPairs().isEmpty()) // struct { } x;
            throw new TypeDefNoStructComponentsException(structBuilder);
        Set<String> names = new HashSet<>();
        Map<String, Variable> components = new HashMap<>();
        int displacement = 0; // struct { int a, int c } x;
        System.out.println(structBuilder.getStructComponentPairs());
        for (VarType.Builder.Pair pair : structBuilder.getStructComponentPairs()) {
            System.out.println(pair.name());

            checkTypeDefined(pair.typeName());
            VarType type = getType(pair.typeName());
            if (names.contains(pair.name()))
                throw new TypeDefDuplicateStructComponentNameException(structBuilder, pair.name());
            names.add(pair.name());
            components.put(pair.name(), new Variable(pair.name(), 0, type, displacement));
            displacement += type.size;
        }

        return structBuilder.setSize(displacement).setStructComponents(components).build();
    }

    private void fillWithPrimitives() {
        table.put("uint", VarType.UINT_TYPE);
        table.put("int", VarType.INT_TYPE);
        table.put("char", VarType.CHAR_TYPE);
        table.put("bool", VarType.BOOL_TYPE);
    }

    public void printTable() {
        System.out.println("VarType(name, size, typeClass, pTarget, acTarget, aSize, sComps");
        table.forEach((key, value) -> System.out.println(key + ", " + value.size + ", " + value.typeClass + ", " + value.pointerTypeTargetName +
                ", " + value.arrayCompTypeTargetName + ", " + value.arraySize + ", " + value.structComponents));
    }
}
