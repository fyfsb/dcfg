package exceptions.memory;

public class MemoryStructNotFoundException extends MemoryStructException {
    public MemoryStructNotFoundException(String name) {
        super("Memory struct " + name + " not found!");
    }
}
