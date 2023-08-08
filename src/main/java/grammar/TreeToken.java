package grammar;


import tree.TokenType;

public class TreeToken {
    public final String value;
    public final TokenType type;

    public TreeToken(TokenType type, String value) {
        this.value = value;
        this.type = type;
    }

    public TreeToken(TokenType type) {
        this.type = type;
        this.value = null;
    }

    @Override
    public String toString() {
        return "Token(type=" + type + ", value=" + value + ")";
    }
}
