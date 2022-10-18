package grammar;

public class Token {
    //Attributes
    private String string;
    private Type type;
    //Constructors
    public Token(){
        string = "";
        type = Type.unknown;
    }
    public Token(String string, Type type){
        this.string = string;
        this.type = type;
    }
    //Functions
    public String toString(){
        return type.toString() + ": " + string;
    }
    //Getters and Setters
    public String getString() {
        return string;
    }
    public void setString(String string) {
        this.string = string;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
