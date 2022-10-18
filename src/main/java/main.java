import auxiliary.function;
import grammar.Grammar;
import grammar.Production;
import grammar.Token;
import grammar.Type;

import java.util.ArrayList;
import java.util.Arrays;

public class main {

    public static void main(String[] args) {

        Type t = Type.compoundTerminal;
        ArrayList<Token> compoundTerminalsC0 = new ArrayList<>(Arrays.asList(
                new Token("typedef", t), new Token("int", t), new Token("bool", t), new Token("char", t),
                new Token("uint", t), new Token("struct", t), new Token("null", t), new Token("true", t),
                new Token("false", t), new Token("-1", t), new Token(">=", t), new Token("<=", t),
                new Token("==", t), new Token("!=", t), new Token("&&", t), new Token("||", t),
                new Token("if", t), new Token("else", t), new Token("while", t), new Token("new", t),
                new Token("return", t)));

        Grammar g = new Grammar(compoundTerminalsC0, "C:\\Users\\Student\\Desktop\\CFG\\New\\C0.txt");

        g.print();

        /*
        for(Production p : g.getProductions()){
            System.out.println(p.toString());
        }
        */

    }
}
