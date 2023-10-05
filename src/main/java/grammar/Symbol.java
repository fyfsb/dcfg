package grammar;

// Encapsulates the concept of symbol, which can either be a terminal or a nonterminal.
public class Symbol {

    public enum SymbolType {
        Terminal, Nonterminal
    }

    // A string representing the actual content of the symbol.
    private final String content;
    // An enum indicating whether the symbol is terminal ar a nonterminal.
    private final SymbolType type;

    // Initializes a symbol with the specified content and type.
    public Symbol(String content, SymbolType type) {
        this.content = content;
        this.type = type;
    }

    // Returns true if the symbol is terminal, returns false otherwise.
    public boolean isTerminal() {
        return type == SymbolType.Terminal;
    }

    // Returns the length of the content string.
    public int length() {
        return content.length();
    }

    // Returns only the content string without type.
    @Override
    public String toString() {
        return content;
    }

    // Determines whether two 'Symbol' objects are identical in both content and type.
    // This is essential for comparison operations, ensuring symbols are uniquely identified by both their content and type.
    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Symbol symbol = (Symbol) obj;
        return type.equals(symbol.getType()) && content.equals(symbol.getContent());
    }

    // Returns a hash code of the symbol.
    // Ensuring correct hash code calculation to correctly implement hash sets and hash maps.
    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + (type == SymbolType.Terminal ? 1 : 0);
        return result;
    }

    public String getContent() {
        return content;
    }

    public SymbolType getType() {
        return type;
    }

}
