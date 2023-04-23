package table;

import exceptions.function.FunctionAlreadyExistsException;
import exceptions.function.FunctionException;
import exceptions.function.FunctionNotFoundException;
import model.Function;

import java.util.HashMap;
import java.util.Map;

public class FunctionTable {
    private final Map<String, Function> table;
    private static FunctionTable INSTANCE;

    private FunctionTable() {
        table = new HashMap<>();
    }

    public static FunctionTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FunctionTable();
        return INSTANCE;
    }

    public Function getFunction(String name) throws FunctionException {
        var function = table.get(name);
        if (function == null)
            throw new FunctionNotFoundException(name);
        return function;
    }

    public void addFunction(Function function) throws FunctionAlreadyExistsException {
        String functionName = function.getName();
        if (table.containsKey(functionName))
            throw new FunctionAlreadyExistsException(functionName);
        table.put(functionName, function);
    }
}
