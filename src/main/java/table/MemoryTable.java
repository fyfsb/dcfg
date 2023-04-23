package table;

import exceptions.memory.MemoryStructAlreadyExistsException;
import exceptions.memory.MemoryStructException;
import exceptions.memory.MemoryStructIncompatibleTypeException;
import exceptions.memory.MemoryStructNotFoundException;
import model.Variable;

import java.util.HashMap;
import java.util.Map;

import static model.VarType.TypeClass.STRUCT;

public class MemoryTable {
    private final Map<String, Variable> table;
    private static MemoryTable INSTANCE;

    private MemoryTable() {
        table = new HashMap<>();
    }

    public static MemoryTable getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MemoryTable();
        return INSTANCE;
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
}
