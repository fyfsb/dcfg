package util;

import grammar.Grammar;
import grammar.Token;
import grammar.Type;

import java.util.ArrayList;
import java.util.Comparator;

public class Utils {
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
            if(array.get(i).getType() == Type.UNKNOWN){
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
                if(term.token.getType() == Type.COMPOUND_TERMINAL && term_.token.getType() == Type.NONTERMINAL){
                    if(term.index + term.token.getString().length() > term_.index){
                        terms.remove(term);
                        i--;
                    }
                }
            }
            if(0 < i){
                Term term_ = terms.get(i - 1);
                if(term.token.getType() == Type.COMPOUND_TERMINAL && term_.token.getType() == Type.NONTERMINAL){
                    if(term.index < term_.index + term_.token.getString().length()){
                        terms.remove(term);
                        i--;
                    }
                }
            }
        }
        //separate nonterminals, compoundTerminals and terminals
        int i = 0, j = 0;
        while(i < t.getString().length()){
            if(j < terms.size() && i == terms.get(j).index){
                array.add(terms.get(j).token);
                i += terms.get(j).token.getString().length();
                j++;
            } else {
                array.add(new Token(Character.toString(t.getString().charAt(i)), Type.TERMINAL));
                i++;
            }
        }
        return array;
    }


    /*
    DO NOT DELETE!

//        List<String> compounds = List.of(
//                "typedef", "int", "bool", "char", "uint", "struct", "null", "true", "false",
//                "-1", ">=", "<=", "==", "!=", "&&", "||", "if", "else", "while", "new", "return"
//        );
//
//        ArrayList<Token> compoundTerminalsC0 = new ArrayList<>(compounds.stream().map(Token::compoundFrom).toList());
//
//        Grammar g = new Grammar(compoundTerminalsC0, "C:\\Users\\Student\\Desktop\\CFG\\New\\C0.txt");
//
//        g.print();
//
//        /*
//        for(Production p : g.getProductions()){
//            System.out.println(p.toString());
//        }
//        */


        /*
        typedef K* kp;
        typedef struct { int a, char c } K;
        typedef K[5] karr;
         */


    // c0 -> parse -> derivation tree -> statements -> bookkeeping && codegeneration
//        List<List<String>> structComponentPairs = List.of(List.of("a", "int"), List.of("c", "char"));
//
//        VarType.Builder p = VarType.createPointerTypeBuilder("K", "kp");
//        VarType.Builder s = VarType.createStructTypeBuilder(structComponentPairs, "K");
//        VarType.Builder a = VarType.createArrayTypeBuilder("int", "karr", 10);
//
//        System.out.println("starting...");
//        TypeTable.getInstance().printTable();
//        System.out.println("-------------------------");
//        System.out.println("adding pointer");
//        TypeTable.getInstance().addType(p); // adding pointer type to the type table
//        TypeTable.getInstance().printTable();
//        System.out.println("-------------------------");
//        System.out.println("adding struct");
//        TypeTable.getInstance().addType(s);
//        TypeTable.getInstance().printTable();
//        System.out.println("-------------------------");
//        System.out.println("adding array");
//        TypeTable.getInstance().addType(a);
//        TypeTable.getInstance().printTable();

        /*
        Testing on FuD
        int f(char a, bool b, int c) {
            uint d;
            return
        }
         */

    // Ty Na
    String name = "f";
    String returnType = "int";

    // PaDS


    // from this
//        DTE namePadC = new DTE(new TreeToken(TokenType.Na, "c"), null, null, null);
//        DTE typePadC = new DTE(new TreeToken(TokenType.Ty, "int"), null, null, namePadC);
//        DTE padC = new DTE(new TreeToken(TokenType.VaD, null), null, typePadC, null);
    // to this corresponds to the VaD of parameter `char c`

    // `bool b`
//        DTE namePadB = new DTE(new TreeToken(TokenType.Na, "b"), null, null, null);
//        DTE typePadB = new DTE(new TreeToken(TokenType.Ty, "bool"), null, null, namePadB);
//        DTE padB = new DTE(new TreeToken(TokenType.VaD, null), null, typePadB,
//                new DTE(new TreeToken(TokenType.PaDS, null), null, padC, null)
//        );

//        DTE namePadA = new DTE(new TreeToken(TokenType.Na, "a"), null, null, null);
//        DTE typePadA = new DTE(new TreeToken(TokenType.Ty, "char"), null, null, namePadA);
//        DTE padA = new DTE(new TreeToken(TokenType.VaD, null), null, typePadA,
//                new DTE(new TreeToken(TokenType.PaDS, null), null, padB, null)
//        );

//        DTE pads = new DTE(
//                new TreeToken(TokenType.PaDS, null),
//                null,
//                padA,
//                null
//        );

    // VaDS
//        DTE nameVadD = new DTE(new TreeToken(TokenType.Na, "d"), null, null, null);
//        DTE typeVadD = new DTE(new TreeToken(TokenType.Ty, "uint"), null, null, nameVadD);
//        DTE vadD = new DTE(new TreeToken(TokenType.VaD, null), null, typeVadD, null);
//
//        DTE vads = new DTE(
//                new TreeToken(TokenType.VaDS, null),
//                null,
//                vadD,
//                null
//        );
//
//        System.out.println("PADS");
//        pads.getFlattenedSequence().forEach(System.out::println);
//        System.out.println("\n\nVADS");
//        vads.getFlattenedSequence().forEach(System.out::println);
//
    // body
//        DTE body = new DTE(new TreeToken(TokenType.Body, null), null,
//                new DTE(new TreeToken(TokenType.RSt), null, null, null), null);
//
//        Fun function = new Fun.Builder()
//                .setBody(body)
//                .setName(name)
//                .setReturnType(returnType)
//                .setPads(pads)
//                .setVads(vads)
//                .build();
//
//        FunctionTable.getInstance().addFunction(function);
//        FunctionTable.getInstance().printTable();
//
//     */
}
