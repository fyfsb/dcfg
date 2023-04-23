package exceptions.function;

public class FunctionAlreadyExistsException extends FunctionException {
    public FunctionAlreadyExistsException(String name) {
        super("Function with name " + name + " already exists!");
    }
}
