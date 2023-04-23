package exceptions.memory;

public class MemoryStructIncompatibleTypeException extends MemoryStructException{
    public MemoryStructIncompatibleTypeException() {
        super("Only struct types allowed as memory");
    }
}
