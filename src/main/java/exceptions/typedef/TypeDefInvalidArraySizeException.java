package exceptions.typedef;

import typetable.VarType;

public class TypeDefInvalidArraySizeException extends TypeDefException {
    public TypeDefInvalidArraySizeException(VarType.Builder varType) {
        super("Error! Can't define array with size " + varType.getArraySize());
    }
}
