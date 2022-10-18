package auxiliary;

import grammar.Grammar;
import grammar.Token;
import grammar.Type;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedMap;

public class function {
    //Checks whether a string equals another string starting from the index "ind"
    public static boolean isSubstringFromInd(String string, int ind, String substring) {
        if(string == null || substring == null) {
            System.out.println("Auxiliary.Function.isSubstring null exception");
            return false;
        }
        if(string.length() - ind < substring.length()) {
            return false;
        }
        for(int i = 0; i < substring.length(); i++) {
            if(string.charAt(ind + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    //Splits the string by the substring and place the parts in a ArrayList
    public static ArrayList<String> splitBySubstring(String string, String substring){
        if(string == null || substring == null) {
            System.out.println("Auxiliary.Function.splitBySubstring null exception");
            return new ArrayList<>();
        }
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int ind = 0; ind < string.length(); ind++) {
            if(isSubstringFromInd(string, ind, substring)) {
                if(sb.length() != 0) {
                    parts.add(sb.toString());
                }
                sb.setLength(0);
                ind = ind + substring.length() - 1;
            } else {
                sb.append(string.charAt(ind));
            }
        }
        if(sb.length() != 0) {
            parts.add(sb.toString());
        }
        return parts;
    }
    //If the token is unknown, then decompose
    public static void decomposeUnknownStrings(ArrayList<Token> array, Grammar g){
        for(int i = 0; i < array.size(); i++){
            if(array.get(i).getType() == Type.unknown){
                ArrayList<Token> newArray = decomposeToken(array.get(i), g);
                array.remove(i);
                for(Token t : newArray){
                    array.add(i, t);
                    i++;
                }
            }
        }
    }
    //
    public static ArrayList<Token> decomposeToken(Token t, Grammar g){
        ArrayList<Token> array = new ArrayList<>();
        record Term(
                Token token,
                int index
        ){}
        ArrayList<Term> terms = new ArrayList<>();
        //Detect nonterminals
        for(Token nonterminal : g.getNonterminals()){
            for(int i = 0; i < t.getString().length(); i++){
                if(isSubstringFromInd(t.getString(), i, nonterminal.getString())){
                    terms.add(new Term(nonterminal, i));
                }
            }
        }
        //Detect compound terminals
        for(Token compoundTerminal : g.getCompoundTerminals()){
            for(int i = 0; i < t.getString().length(); i++){
                if(isSubstringFromInd(t.getString(), i, compoundTerminal.getString())){
                    terms.add(new Term(compoundTerminal, i));
                }
            }
        }
        terms.sort(Comparator.comparing(Term::index));
        //Avoid overlapping (For example <E>==<E>: >=, ==, <= are recognized as compoundTerminals
        for(int i = 0; i < terms.size(); i++){
            Term term = terms.get(i);
            if(i + 1 < terms.size()){
                Term term_ = terms.get(i + 1);
                if(term.token.getType() == Type.compoundTerminal && term_.token.getType() == Type.nonterminal){
                    if(term.index + term.token.getString().length() > term_.index){
                        terms.remove(term);
                        i--;
                    }
                }
            }
            if(0 < i){
                Term term_ = terms.get(i - 1);
                if(term.token.getType() == Type.compoundTerminal && term_.token.getType() == Type.nonterminal){
                    if(term.index < term_.index + term_.token.getString().length()){
                        terms.remove(term);
                        i--;
                    }
                }
            }
        }
        //separate nonterminals, compoundTerminals and terminals
        int i = 0, j = 0;
        StringBuilder sb = new StringBuilder();
        while(i < t.getString().length()){
            if(j < terms.size() && i == terms.get(j).index){
                for(int ind = 0; ind < sb.length(); ind++){
                    array.add(new Token(Character.toString(sb.charAt(ind)), Type.terminal));
                }
                sb.setLength(0);
                array.add(terms.get(j).token);
                i += terms.get(j).token.getString().length();
                j++;
            } else {
                sb.append(t.getString().charAt(i));
                i++;
            }
        }
        for(int ind = 0; ind < sb.length(); ind++){
            array.add(new Token(Character.toString(sb.charAt(ind)), Type.terminal));
        }
        return array;
    }
}
