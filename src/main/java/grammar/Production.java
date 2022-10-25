package grammar;

import util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Production {
    //Attributes
    private Token left;
    private ArrayList<Token> right;
    //Constructors
    public Production(){
        left = new Token();
        right = new ArrayList<>();
    }
    public Production(Token left, ArrayList<Token> right){
        this.left = left;
        this.right = right;
    }
    public Production(String string){
        //Decompose the production by " -> "
        ArrayList<String> parts = Utils.splitBySubstring(string, " -> ");
        if(parts.size() < 2){
            System.out.println("Production.constructor error ' -> ' not found ");
            return;
        }
        this.left = new Token(parts.get(0), Type.NONTERMINAL);
        this.right = new ArrayList<>(List.of(new Token(parts.get(1), Type.UNKNOWN)));
    }
    //Functions
    public String toString(){
        StringBuilder sb = new StringBuilder(left.getString() + " -> ");
        for(Token t : right){
            sb.append(t.getString());
        }
        return sb.toString();
    }
    //Getters and Setters
    public Token getLeft() {
        return left;
    }
    public void setLeft(Token left) {
        this.left = left;
    }
    public ArrayList<Token> getRight() {
        return right;
    }
    public void setRight(ArrayList<Token> right) {
        this.right = right;
    }
}
