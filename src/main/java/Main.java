import codegen.CodeGenerator;
import config.Configuration;
import dk.DK1;
import tree.DTE;
import grammar.Grammar;
import table.FunctionTable;
import table.MemoryTable;
import table.TypeTable;
import tree.TokenType;
import util.TypeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void fillTables(DTE program) throws Exception {
        TypeUtils.checkTokenType(program, "<prog>");

        DTE current = program.getFirstSon();
        if (current.getLabel().getContent().equals("<TyDS>")) {
            TypeTable.getInstance().fillTable(current);
            current = current.getBrother().getBrother();
        }

        if (current.isType("<VaDS>")) {
            MemoryTable.getInstance().fillTable(current);
            current = current.getBrother().getBrother();
        }

        if (current.isType("<FuDS>")) {
            FunctionTable.getInstance().fillTable(current);
        }
    }

    public static void main(String[] args) throws Exception {

        String grammarFilePath = "src/main/java/grammar/Grammar.txt";
        String terminalsFilePath = "src/main/java/grammar/Terminals.txt";

        Grammar g = new Grammar(grammarFilePath, terminalsFilePath);

        DK1 dk1 = new DK1(g);

        System.out.println("number of states: " + dk1.getStates().size());
        System.out.println("-----------------------");
        System.out.println("DK1 test passed = " + dk1.Test());
        System.out.println("-----------------------");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("code:");
            String code = reader.readLine();
            try {
                DTE parsedTree = dk1.parseString(code);
//                System.out.println(parsedTree);
                parsedTree.printTree();

                fillTables(parsedTree);

                // id -> id[E] -> id.Na[E]
                TypeTable.getInstance().printTable();
                MemoryTable.getInstance().printTable();
                FunctionTable.getInstance().printTable();

                Configuration.getInstance().initialize();
                CodeGenerator.getInstance().setGrammar(g);
                CodeGenerator.getInstance().generateCode();

                System.out.println("C0: " + code);
                CodeGenerator.getInstance().printInstructions();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                TypeTable.reset();
                MemoryTable.reset();
                FunctionTable.reset();
                Configuration.reset();
                CodeGenerator.reset();
            }
        }

        /*
        typedef struct { int a, int b } k;

        k s;
        int f() {
            s.b = 1;
        }
         */

        // get the test program
//        DTE dte = DTEUtils.getTestProgram();

        // fill Type table, Memory table and Function table
//        fillTables(dte);

        // print tables
//        TypeTable.getInstance().printTable();
//        MemoryTable.getInstance().printTable();
//        FunctionTable.getInstance().printTable();


        // Initializing stack, empty heap, creating a function call for
        // function "f" (later will be for main)
        // setting the target function for CodeGenerator
//        Configuration.getInstance().initialize();

        // Generating code for target function
//        CodeGenerator.getInstance().generateCode();

        // printing generated instructions.
//        CodeGenerator.getInstance().printInstructions();


        /*
        created a structure just to pass a single <prog> DTE
            - fill the type table
            - fill the gm memory table
            - fill all the function table entries
        create some abstractions to pass the tree
            - fseq - flattened sequence DTE -> XS -> X ; XS
            - bw, Na -> DiLeS

        Functions - rda, rd

        Fun / FunctionCall -> st(main, 0, rda)


        // Basic structure for c0 configurations (stack, heaptable, nh, rd, snapshots of the old configurations)


              // Code Generation


         */
    }
}
