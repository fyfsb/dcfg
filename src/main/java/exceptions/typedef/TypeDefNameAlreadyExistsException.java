package exceptions.typedef;

import model.VarType;

public class TypeDefNameAlreadyExistsException extends TypeDefException {
    public TypeDefNameAlreadyExistsException(VarType.Builder varType) {
        super("Type with name '" + varType.getName() + "' already exists!");
    }
}
