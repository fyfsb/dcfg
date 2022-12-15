package exceptions.typedef;

import typetable.VarType;

public class TypeDefNoStructComponentsException extends TypeDefException {

    public TypeDefNoStructComponentsException(VarType.Builder varType) {
        super("Cannot create struct + '" + varType.getName() + "' with no components");
    }
}
