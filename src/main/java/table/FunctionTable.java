package table;

import exceptions.function.FunctionAlreadyExistsException;
import exceptions.function.FunctionException;
import exceptions.function.FunctionNotFoundException;
import model.Fun;

import java.util.HashMap;
import java.util.Map;

public class FunctionTable {
    private final Map<String, Fun> table;
    private static FunctionTable INSTANCE;

    private FunctionTable() {
        table = new HashMap<>();
    }

    public static FunctionTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FunctionTable();
        return INSTANCE;
    }

    public Fun getFunction(String name) throws FunctionException {
        var function = table.get(name);
        if (function == null)
            throw new FunctionNotFoundException(name);
        return function;
    }

    public void addFunction(Fun function) throws FunctionAlreadyExistsException {
        String functionName = function.getName();
        if (table.containsKey(functionName))
            // int f(), int f(int a)
            throw new FunctionAlreadyExistsException(functionName);
        table.put(functionName, function);
    }

    public void printTable() {
        System.out.println("\n\n------ FUNCTION TABLE ------");
        table.forEach((key, value) -> {
            System.out.println("$" + key);
            System.out.println("name = " + value.getName());
            System.out.println("return type = " + value.getReturnType().name);
            System.out.println("p = " + value.getNumParameters());
            System.out.println("memory struct = " + value.getMemoryStruct());
            System.out.println("body = " + value.getBody());
            System.out.println("----------------------");
        });
    }
}
