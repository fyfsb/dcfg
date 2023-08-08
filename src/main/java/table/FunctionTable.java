package table;

import exceptions.function.FunctionAlreadyExistsException;
import exceptions.function.FunctionException;
import exceptions.function.FunctionNotFoundException;
import model.Fun;
import tree.DTE;
import tree.TokenType;

import java.util.HashMap;
import java.util.Map;

public class FunctionTable implements Table {
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
            throw new FunctionAlreadyExistsException(functionName);
        table.put(functionName, function);
    }

    public void printTable() {
        System.out.println("\n\n------ FUNCTION TABLE ------");
        table.forEach((key, value) -> {
            System.out.println("[KEY=" + key + ", VALUE=" + value + "]");
        });
        System.out.println("--------------------------");
    }

    @Override
    public void fillTable(DTE fuds) throws Exception {
        if (fuds.token.type != TokenType.FuDS) {
            throw new IllegalArgumentException("Expected FuDS, got " + fuds.token.type);
        }

        fuds.getFlattenedSequence().stream().map(f -> {
            try {
                return Fun.fromDTE(f);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).forEach(f -> {
            try {
                addFunction(f);
            } catch (Exception e) {
                System.out.println("ALREADY Exists");
            }
        });
    }
}
