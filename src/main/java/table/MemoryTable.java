package table;

import config.Configuration;
import exceptions.memory.MemoryStructAlreadyExistsException;
import exceptions.memory.MemoryStructException;
import exceptions.memory.MemoryStructIncompatibleTypeException;
import exceptions.memory.MemoryStructNotFoundException;
import model.VarType;
import model.Variable;
import tree.DTE;
import tree.TokenType;
import util.TypeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.VarType.TypeClass.STRUCT;
import static util.TypeUtils.checkTokenType;

public class MemoryTable implements Table {
    private final Map<String, Variable> table;
    private static MemoryTable INSTANCE;

    public MemoryTable() {
        table = new HashMap<>();
    }

    public static MemoryTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MemoryTable();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
    }

    public Variable getMemory(String name) throws MemoryStructException {
        var memory = table.get(name);
        if (memory == null)
            throw new MemoryStructNotFoundException(name);
        return memory;
    }

    public void addMemory(Variable memoryStruct) throws MemoryStructException {
        if (memoryStruct.getType().typeClass != STRUCT)
            throw new MemoryStructIncompatibleTypeException();
        if (table.containsKey(memoryStruct.getName()))
            throw new MemoryStructAlreadyExistsException(memoryStruct.getName());
        table.put(memoryStruct.getName(), memoryStruct);
    }

    @Override
    public void fillTable(DTE vads) throws Exception {
        checkTokenType(vads, "<VaDS>");

        List<List<String>> componentPairs = vads.extractComponentPairs();
        String name = "$gm";

        VarType.Builder structBuilder = VarType.createStructTypeBuilder(componentPairs, name);
        VarType varType = TypeTable.getInstance().createStructType(structBuilder);

        Variable gm = new Variable(name, 27, varType, 0);
        Configuration.getInstance().setGlobalMemory(gm);
        addMemory(gm);
    }

    public void printTable() {
        System.out.println("\n\n------ Memory TABLE ------");
        table.forEach((key, value) ->
                System.out.println("[KEY=" + key + ", VALUE=" + value + "]")
        );
        System.out.println("--------------------------");

    }
}
