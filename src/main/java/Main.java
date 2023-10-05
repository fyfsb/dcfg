import codegen.CodeGenerator;
import config.Configuration;
import dk.DK1;
import grammar.Grammar;
import table.FunctionTable;
import table.MemoryTable;
import table.TypeTable;
import tree.DTE;
import util.TypeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

import static util.Context.DEBUG;
import static util.Logger.log;

public class Main {

    /*
    Some Test Program Codes:

    bool b; bool c; int main() {b=(bool)c||false;return 1}~
    char c; int main() {c=t; return 1}~
    int x; int main() {x=-14; return 1}~
    int x; int main(){x=2; if true {x=4}else{x=9};return 3}~
    typedef int[6] arr; arr a;int main() {a[0]=5;return 1}~

     */

    public static void fillTables(DTE program) throws Exception {
        TypeUtils.checkTokenType(program, "<prog>");

        DTE current = program.getFirstSon();
        if (current.isType("<TyDS>")) {
            TypeTable.getInstance().fillTable(current);
            current = current.getNthBrother(2);
        }

        if (current.isType("<VaDS>")) {
            MemoryTable.getInstance().fillTable(current);
            current = current.getNthBrother(2);
        }

        if (current.isType("<FuDS>")) {
            FunctionTable.getInstance().fillTable(current);
        }

        if (DEBUG) {
            TypeTable.getInstance().printTable();
            MemoryTable.getInstance().printTable();
            FunctionTable.getInstance().printTable();
        }
    }

    public static void main(String[] args) throws Exception {

        String grammarFilePath = "src/main/java/grammar/Grammar.txt";
        String terminalsFilePath = "src/main/java/grammar/Terminals.txt";

        //String grammarFilePath = "C:/Users/Student/Desktop/CFG_1/Example1/Grammar.txt";
        //String terminalsFilePath = "C:/Users/Student/Desktop/CFG_1/Example1/Terminals.txt";


        DEBUG = true;


        Grammar g = new Grammar(grammarFilePath, terminalsFilePath);

        System.out.println(g);

        DK1 dk1 = new DK1(g);

        log("number of states: " + dk1.getStates().size());
        log("-----------------------");
        log("DK1 test passed = " + dk1.dk1Test());
        log("-----------------------");
        log("\n");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("code:");
            String path = reader.readLine();
            File file = new File(path);
            Scanner sc = new Scanner(file);
            StringBuilder code = new StringBuilder();

            while (sc.hasNext()) {
                code.append(sc.nextLine()).append("\n");
            }

            try {
                DTE parsedTree = dk1.parseString(code.toString());

                // Print the ParsedTree
                log("The Parse Tree: ");
                parsedTree.printTree();

                fillTables(parsedTree);

                CodeGenerator.getInstance().setGrammar(g);
                CodeGenerator.getInstance().generateCode();

                System.out.println("C0: " + code);
                CodeGenerator.getInstance().printInstructions();

            } catch (Exception e) {
                e.printStackTrace();
            }

            TypeTable.reset();
            MemoryTable.reset();
            FunctionTable.reset();
            Configuration.reset();
            CodeGenerator.reset();
        }

    }
}
