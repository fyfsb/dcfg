package exceptions.typedef;

import typetable.VarType;

public class TypeNotDefinedException extends TypeDefException {
    public TypeNotDefinedException(String name) {
        super("Type with name '" + name + "' has not been defined!");
    }
}
