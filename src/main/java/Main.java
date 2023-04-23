import exceptions.typedef.TypeDefException;
import table.TypeTable;
import model.VarType;

import java.util.List;

public class Main {

    public static void main(String[] args) throws TypeDefException {

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
        typedef K[] karr;
         */


        // c0 -> parse -> derivation tree -> statements -> bookkeeping && codegeneration
        List<List<String>> structComponentPairs = List.of(List.of("a", "int"), List.of("c", "char"));

        VarType.Builder p = VarType.createPointerTypeBuilder("K", "kp");
        VarType.Builder s = VarType.createStructTypeBuilder(structComponentPairs, "K");
        VarType.Builder a = VarType.createArrayTypeBuilder("int", "karr", 10);

        System.out.println("starting...");
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding pointer");
        TypeTable.getInstance().addType(p);
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding struct");
        TypeTable.getInstance().addType(s);
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding array");
        TypeTable.getInstance().addType(a);
        TypeTable.getInstance().printTable();

    }
}
