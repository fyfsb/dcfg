package grammar;

import auxiliary.function;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Grammar {
    //Attributes
    private Token start;
    private ArrayList<Token> terminals;
    private ArrayList<Token> compoundTerminals;
    private ArrayList<Token> nonterminals;
    private ArrayList<Production> productions;
    //Constructors
    public Grammar() {
        start = new Token();
        terminals = new ArrayList<>();
        compoundTerminals = new ArrayList<>();
        nonterminals = new ArrayList<>();
        productions = new ArrayList<>();
    }
    public Grammar(ArrayList<Token> compoundTerminals, String filePath){
        //Read the grammar from the text file
        Scanner in = new Scanner(System.in);
        try {
            in = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Add compound terminals
        this.compoundTerminals = compoundTerminals;
        //Add productions
        productions = new ArrayList<>();
        addProductions(in);
        //Add start
        start = productions.get(0).getLeft();
        //Add nonterminals
        nonterminals = new ArrayList<>();
        addNonterminals();
        //Decompose the right side of the productions (translate unknowns to terminals and nonterminals)
        for(Production p : productions){
            function.decomposeUnknownStrings(p.getRight(), this);
        }
        //Add terminals
        terminals = new ArrayList<>();
        addTerminals();
    }
    //Functions
    private void addProductions(Scanner in) {
        while(in.hasNext()) {
            String string = in.nextLine();
            ArrayList<String> parts = function.splitBySubstring(string, " -> ");
            if(parts.size() < 2){
                System.out.println("Grammar.addProductions error parts size < 2");
                return;
            }
            for(String right : function.splitBySubstring(parts.get(1), " | ")) {
                productions.add(new Production(parts.get(0) + " -> " + right));
            }
        }
    }
    private void addNonterminals() {
        HashSet<String> check = new HashSet<>();
        for(Production p : this.productions) {
            if(!check.contains(p.getLeft().getString())) {
                this.nonterminals.add(p.getLeft());
                check.add(p.getLeft().getString());
            }
        }
    }
    private void addTerminals(){
        HashSet<String> check = new HashSet<>();
        for(Production p : this.productions){
            for(Token t : p.getRight()){
                if(t.getType() == Type.terminal && !check.contains(t.getString())){
                    this.terminals.add(t);
                    check.add(t.getString());
                }
            }
        }
    }
    public void print(){
        System.out.println("Start Symbol:");
        System.out.println(this.start.getString());
        System.out.println("Terminals:");
        for(Token t : terminals){
            System.out.println(t.getString());
        }
        System.out.println("Compound Terminals:");
        for(Token t : compoundTerminals){
            System.out.println(t.getString());
        }
        System.out.println("Nonterminals:");
        for(Token t : nonterminals){
            System.out.println(t.getString());
        }
        for(Production p : this.productions){
            System.out.println(p.getLeft() + " -> " + p.getRight());
        }
    }
    //Getters and Setters
    public Token getStart() {
        return start;
    }
    public void setStart(Token start) {
        this.start = start;
    }
    public ArrayList<Token> getTerminals() {
        return terminals;
    }
    public void setTerminals(ArrayList<Token> terminals) {
        this.terminals = terminals;
    }
    public ArrayList<Token> getNonterminals() {
        return nonterminals;
    }
    public void setNonterminals(ArrayList<Token> nonterminals) {
        this.nonterminals = nonterminals;
    }
    public ArrayList<Production> getProductions() {
        return productions;
    }
    public void setProductions(ArrayList<Production> productions) {
        this.productions = productions;
    }
    public ArrayList<Token> getCompoundTerminals() {
        return compoundTerminals;
    }
    public void setCompoundTerminals(ArrayList<Token> compoundTerminals) {
        this.compoundTerminals = compoundTerminals;
    }
}
