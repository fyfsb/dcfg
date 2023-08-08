import codegen.CodeGenerator;
import config.Configuration;
import table.FunctionTable;
import table.MemoryTable;
import table.TypeTable;
import tree.DTE;
import tree.DTEUtils;
import tree.TokenType;

public class Main {

    public static void fillTables(DTE program) throws Exception {
        if (program.token.type != TokenType.Prog) {
            throw new IllegalArgumentException("Expected Prog, got " + program.token.type);
        }

        DTE current = program.fson;
        if (current.token.type == TokenType.TyDS) {
            TypeTable.getInstance().fillTable(current);
            current = current.bro.bro;
        }

        if (current.token.type == TokenType.VaDS) {
            MemoryTable.getInstance().fillTable(current);
            current = current.bro.bro;
        }

        if (current.token.type == TokenType.FuDS) {
            FunctionTable.getInstance().fillTable(current);
        }
    }

    public static void main(String[] args) throws Exception {

        /*
        typedef struct { int a, int b } k;

        k s;
        int f() {
            s.b = 1;
        }
         */

        // get the test program
        DTE dte = DTEUtils.getTestProgram();

        // fill Type table, Memory table and Function table
        fillTables(dte);

        // print tables
        TypeTable.getInstance().printTable();
        MemoryTable.getInstance().printTable();
        FunctionTable.getInstance().printTable();


        // Initializing stack, empty heap, creating a function call for
        // function "f" (later will be for main)
        // setting the target function for CodeGenerator
        Configuration.getInstance().initialize();

        // Generating code for target function
        CodeGenerator.getInstance().generateCode();

        // printing generated instructions.
        CodeGenerator.getInstance().printInstructions();


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
