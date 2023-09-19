package table;

import exceptions.typedef.*;
import exceptions.variable.VariableDuplicateNameException;
import model.VarType;
import model.Variable;
import tree.DTE;

import java.util.*;

import static util.TypeUtils.checkTokenType;

public class TypeTable implements Table {
    private final Map<String, VarType> table;
    private static TypeTable INSTANCE;

    public TypeTable() {
        table = new HashMap<>();
        fillWithPrimitives();
    }

    public static TypeTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TypeTable();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
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

    public void checkTypeDefined(String name) throws TypeNotDefinedException {
        if (getType(name) == VarType.UNDEFINED_TYPE)
            throw new TypeNotDefinedException(name);
    }

    public void addType(VarType.Builder builder) throws TypeDefException {
        // check names distinct
        if (!isNameDistinct(builder.getName()))
            throw new TypeDefNameAlreadyExistsException(builder);

        /*
            Temporarily adding `_` to the type name,
            Current grammar allows using non-primitive types
            in form of <Na>_
         */
        builder.setName(builder.getName() + "_");

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
        for (VarType.Builder.Pair pair : structBuilder.getStructComponentPairs()) {

            checkTypeDefined(pair.typeName());
            VarType type = getType(pair.typeName());
            if (names.contains(pair.name()))
                throw new VariableDuplicateNameException(structBuilder.getName(), pair.name());
            names.add(pair.name());
            components.put(pair.name(), new Variable(pair.name(), 27, type, displacement));
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

    @Override
    public void fillTable(DTE tyds) throws Exception {
        checkTokenType(tyds, "<TyDS>");

        // TyDS -> TyD1, TyD2
        List<DTE> flattenedSequence = tyds.getFlattenedSequence();
        for (DTE tyD : flattenedSequence) {
            readTypeDefinition(tyD);
        }
    }

    private void readTypeDefinition(DTE tyD) throws Exception {
        VarType.Builder builder = VarType.fromDTE(tyD);
        addType(builder);
    }

    public void printTable() {
        System.out.println("\n\n------ TYPE TABLE ------");
        table.forEach((key, value) -> System.out.println("[KEY=" + key + ", VALUE=" + value + "]"));
        System.out.println("----------------------");

    }
}
