package model;

import exceptions.typedef.TypeNotDefinedException;
import table.TypeTable;
import tree.DTE;

import java.util.List;
import java.util.Map;

import static util.TypeUtils.checkTokenType;

public class VarType {
    public enum TypeClass {
        UINT, INT, CHAR, BOOL, ARRAY, STRUCT, POINTER, UNDEFINED
    }

    public final static VarType UINT_TYPE = new VarType("UINT", 4, TypeClass.UINT, null, null, 0, null);
    public final static VarType INT_TYPE = new VarType("INTEGER", 4, TypeClass.INT, null, null, 0, null);
    public final static VarType CHAR_TYPE = new VarType("CHAR", 4, TypeClass.CHAR, null, null, 0, null);
    public final static VarType BOOL_TYPE = new VarType("BOOL", 4, TypeClass.BOOL, null, null, 0, null);
    public final static VarType UNDEFINED_TYPE = new VarType("UNDEFINED", 0, TypeClass.UNDEFINED, null, null, 0, null);


    public final String name;
    public final int size;
    public final TypeClass typeClass;
    public final String pointerTypeTargetName;
    public final String arrayCompTypeTargetName;
    public final int arraySize;
    public final Map<String, Variable> structComponents;

    private VarType(String name, int size, TypeClass typeClass, String pointerTypeTargetName, String arrayCompTypeTargetName, int arraySize, Map<String, Variable> structComponents) {
        this.name = name;
        this.size = size;
        this.typeClass = typeClass;
        this.pointerTypeTargetName = pointerTypeTargetName;
        this.arrayCompTypeTargetName = arrayCompTypeTargetName;
        this.arraySize = arraySize;
        this.structComponents = structComponents;
    }

    public static Builder createArrayTypeBuilder(String arrayCompTypeTargetName, String name, int arraySize) {
        return new Builder()
                .setTypeClass(TypeClass.ARRAY)
                .setACTargetName(arrayCompTypeTargetName)
                .setName(name).setArraySize(arraySize);
    }

    public static Builder createPointerTypeBuilder(String pTargetName, String name) {
        return new Builder()
                .setTypeClass(TypeClass.POINTER)
                .setSize(4)
                .setPTargetName(pTargetName)
                .setName(name);
    }

    public static Builder createStructTypeBuilder(List<List<String>> compPairs, String name) {
        return new Builder()
                .setStructComponentPairs(compPairs.stream().map(list -> new Builder.Pair(list.get(0), list.get(1))).toList())
                .setName(name)
                .setTypeClass(TypeClass.STRUCT);
    }

    public static Builder fromDTE(DTE tyD) {
        checkTokenType(tyD, "<TyD>");

        VarType.Builder builder;

        DTE typeExpression = tyD.getNthSon(2);
        DTE na = typeExpression.getBrother();

        String name = na.getBorderWord();

        System.out.println("TYPE_EXPRESSION WITH NAME " + name);
        typeExpression.printTree();

        if (typeExpression.getFirstSon().isType("struct")) {
            DTE vads = typeExpression.getNthSon(3);
            List<List<String>> componentPairs = vads.extractComponentPairs();
            builder = VarType.createStructTypeBuilder(componentPairs, name);
        } else if (typeExpression.getNthSon(2).isType("`")) {
            String pTargetName = typeExpression.getFirstSon().getBorderWord();
            builder = VarType.createPointerTypeBuilder(pTargetName, name);
        } else if (typeExpression.getNthSon(2).isType("[")) {
            String arrayCompTypeTargetName = typeExpression.getFirstSon().getBorderWord();
            int arraySize = Integer.parseInt(typeExpression.getNthSon(3).getBorderWord());

            builder = VarType.createArrayTypeBuilder(arrayCompTypeTargetName, name, arraySize);
        } else {
            throw new IllegalArgumentException();
        }

        return builder;
    }

    public VarType getPointerTargetType() throws TypeNotDefinedException {
        return TypeTable.getInstance().getType(pointerTypeTargetName);
    }

    public VarType getArrayCompTargetType() throws TypeNotDefinedException {
        return TypeTable.getInstance().getType(arrayCompTypeTargetName);
    }

    public static class Builder {

        public record Pair(String typeName, String name) {
        }

        private String name;
        private int size;
        private TypeClass typeClass;
        private String pTargetName;
        private String aCTargetName;
        private int arraySize;

        private Map<String, Variable> structComponents;
        private List<Pair> structComponentPairs;

        public VarType build() {
            return new VarType(name, size, typeClass, pTargetName, aCTargetName, arraySize, structComponents);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setTypeClass(TypeClass typeClass) {
            this.typeClass = typeClass;
            return this;
        }


        public Builder setPTargetName(String pTargetName) {
            this.pTargetName = pTargetName;
            return this;
        }

        public Builder setACTargetName(String aCTargetName) {
            this.aCTargetName = aCTargetName;
            return this;
        }

        public Builder setArraySize(int arraySize) {
            this.arraySize = arraySize;
            return this;
        }

        public Builder setStructComponents(Map<String, Variable> components) {
            structComponents = components;
            return this;
        }

        public Builder setStructComponentPairs(List<Pair> structComponentPairs) {
            this.structComponentPairs = structComponentPairs;
            return this;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public TypeClass getTypeClass() {
            return typeClass;
        }

        public int getArraySize() {
            return arraySize;
        }

        public String getpTargetName() {
            return pTargetName;
        }

        public String getaCTargetName() {
            return aCTargetName;
        }

        public List<Pair> getStructComponentPairs() {
            return structComponentPairs;
        }
    }

    @Override
    public String toString() {
        return "VarType(" +
                "name=" + name +
                ", size=" + size +
                ", typeClass=" + typeClass +
                ", pointerTypeTarget=" + pointerTypeTargetName +
                ", arrayComponentTypeTarget=" + arrayCompTypeTargetName +
                ", arraySize=" + arraySize +
                ", structComponents=" + structComponents;
    }
}
