import dk_0.DK_0;
import grammar.Grammar;
import grammar.Token;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<String> compounds = List.of(
                "typedef", "int", "bool", "char", "uint", "struct", "null", "true", "false",
                "-1", ">=", "<=", "==", "!=", "&&", "||", "if", "else", "while", "new", "return"
        );

        ArrayList<Token> compoundTerminalsC0 = new ArrayList<>(compounds.stream().map(Token::compoundFrom).toList());

        Grammar g = new Grammar(compoundTerminalsC0, "C:\\Users\\Student\\Desktop\\CFG\\New\\C0.txt");

        g.print();

        System.out.println();
        System.out.println();

        System.out.println(DK_0.test(g));

        /*
        for(Production p : g.getProductions()){
            System.out.println(p.toString());
        }
        */

    }
}
