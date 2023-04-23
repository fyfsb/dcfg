package exceptions.function;

public class FunctionNotFoundException extends FunctionException {
    public FunctionNotFoundException(String name) {
        super("Function with name " + name + " not found!");
    }
}
