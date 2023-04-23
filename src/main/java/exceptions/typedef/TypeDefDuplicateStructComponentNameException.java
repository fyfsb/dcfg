package exceptions.typedef;

import model.VarType;

public class TypeDefDuplicateStructComponentNameException extends TypeDefException {

    public TypeDefDuplicateStructComponentNameException(VarType.Builder varType, String compName) {
        super("Can not create struct '" + varType.getName() + "'! component with name '" + compName + "' already exists!");
    }
}