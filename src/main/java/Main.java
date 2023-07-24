import exceptions.function.FunctionAlreadyExistsException;
import exceptions.typedef.TypeDefException;
import grammar.TreeToken;
import model.Fun;
import table.FunctionTable;
import tree.DTE;
import table.TypeTable;
import model.VarType;
import tree.TokenType;

import java.util.List;

public class Main {

    public static void main(String[] args) throws TypeDefException, FunctionAlreadyExistsException {

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
        List<List<String>> structComponentPairs = List.of(List.of("a", "int"), List.of("c", "char"));

        VarType.Builder p = VarType.createPointerTypeBuilder("K", "kp");
        VarType.Builder s = VarType.createStructTypeBuilder(structComponentPairs, "K");
        VarType.Builder a = VarType.createArrayTypeBuilder("int", "karr", 10);

        System.out.println("starting...");
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding pointer");
        TypeTable.getInstance().addType(p); // adding pointer type to the type table
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding struct");
        TypeTable.getInstance().addType(s);
        TypeTable.getInstance().printTable();
        System.out.println("-------------------------");
        System.out.println("adding array");
        TypeTable.getInstance().addType(a);
        TypeTable.getInstance().printTable();

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
        DTE namePadC = new DTE(new TreeToken(TokenType.Na, "c"), null, null, null);
        DTE typePadC = new DTE(new TreeToken(TokenType.Ty, "int"), null, null, namePadC);
        DTE padC = new DTE(new TreeToken(TokenType.VaD, null), null, typePadC, null);
        // to this corresponds to the VaD of parameter `char c`

        // `bool b`
        DTE namePadB = new DTE(new TreeToken(TokenType.Na, "b"), null, null, null);
        DTE typePadB = new DTE(new TreeToken(TokenType.Ty, "bool"), null, null, namePadB);
        DTE padB = new DTE(new TreeToken(TokenType.VaD, null), null, typePadB,
                new DTE(new TreeToken(TokenType.PaDS, null), null, padC, null)
        );

        DTE namePadA = new DTE(new TreeToken(TokenType.Na, "a"), null, null, null);
        DTE typePadA = new DTE(new TreeToken(TokenType.Ty, "char"), null, null, namePadA);
        DTE padA = new DTE(new TreeToken(TokenType.VaD, null), null, typePadA,
                new DTE(new TreeToken(TokenType.PaDS, null), null, padB, null)
        );

        DTE pads = new DTE(
                new TreeToken(TokenType.PaDS, null),
                null,
                padA,
                null
        );

        // VaDS
        DTE nameVadD = new DTE(new TreeToken(TokenType.Na, "d"), null, null, null);
        DTE typeVadD = new DTE(new TreeToken(TokenType.Ty, "uint"), null, null, nameVadD);
        DTE vadD = new DTE(new TreeToken(TokenType.VaD, null), null, typeVadD, null);

        DTE vads = new DTE(
                new TreeToken(TokenType.VaDS, null),
                null,
                vadD,
                null
        );

        System.out.println("PADS");
        pads.getFlattenedSequence().forEach(System.out::println);
        System.out.println("\n\nVADS");
        vads.getFlattenedSequence().forEach(System.out::println);

        // body
        DTE body = new DTE(new TreeToken(TokenType.Body, null), null,
                new DTE(new TreeToken(TokenType.RSt), null, null, null), null);

        Fun function = new Fun.Builder()
                .setBody(body)
                .setName(name)
                .setReturnType(returnType)
                .setPads(pads)
                .setVads(vads)
                .build();

        FunctionTable.getInstance().addFunction(function);
        FunctionTable.getInstance().printTable();
    }
}
