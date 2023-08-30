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
import java.io.InputStreamReader;

import static util.Context.DEBUG;
import static util.Logger.log;

public class Main {

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

        Grammar g = new Grammar(grammarFilePath, terminalsFilePath);

        DK1 dk1 = new DK1(g);

        log("number of states: " + dk1.getStates().size());
        log("-----------------------");
        log("DK1 test passed = " + dk1.Test());
        log("-----------------------");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DEBUG = false;
        while (true) {
            System.out.println("code:");
            String code = reader.readLine();
            try {
                DTE parsedTree = dk1.parseString(code);

                fillTables(parsedTree);

                Configuration.getInstance().initialize();
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
        }
    }
}
