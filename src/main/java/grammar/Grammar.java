package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class Grammar {

    // The start symbol by convention is the top left symbol of the grammar
    private final Symbol start;
    private final HashSet<Symbol> terminals;
    private final HashSet<Symbol> nonterminals;
    private final List<Production> productions;

    public Grammar(String grammarFilePath, String terminalsFilePath) throws FileNotFoundException {
        terminals = new HashSet<>();
        nonterminals = new HashSet<>();
        productions = new ArrayList<>();

        // Read terminal symbols
        readTerminals(terminalsFilePath);

        // Read nonterminal symbols
        readNonterminals(grammarFilePath);

        // Read Productions
        readProductions(grammarFilePath);

        // Initialize the start symbol
        start = productions.get(0).getLeft();
    }

    private void readTerminals(String terminalsFilePath) throws FileNotFoundException {
        Scanner in = new Scanner(new File(terminalsFilePath));
        while (in.hasNext()) {
            final String terminal = in.nextLine().replaceAll("\\s", "");
            terminals.add(new Symbol(terminal, Symbol.Type.Terminal));
        }
        terminals.add(new Symbol(" ", Symbol.Type.Terminal));
        terminals.add(new Symbol("\t", Symbol.Type.Terminal));
        terminals.add(new Symbol("\n", Symbol.Type.Terminal));
    }

    private void readNonterminals(String grammarFilePath) throws FileNotFoundException {
        Scanner in = new Scanner(new File(grammarFilePath));
        while (in.hasNext()) {
            String str = in.nextLine();
            String withoutWhitespace = str.replaceAll("\\s", "");
            // Nonterminals occur on the left hand side of the productions
            String[] parts = withoutWhitespace.split("->");
            nonterminals.add(new Symbol(parts[0], Symbol.Type.Nonterminal));
        }
    }

    private void readProductions(String grammarFilePath) throws FileNotFoundException {
        Scanner in = new Scanner(new File(grammarFilePath));
        while (in.hasNext()) {
            final String str = in.nextLine();

            String[] parts = str.split(" -> ");
            Symbol left = new Symbol(parts[0], Symbol.Type.Nonterminal);

            String[] rightParts = parts[1].split(" \\| ");
            for (String rightStr : rightParts) {
                ArrayList<Symbol> right = stringIntoSymbols(rightStr, this);
                right = eraseExtraWhitespace(right);
                productions.add(new Production(left, right));
            }
        }
    }

    public static ArrayList<Symbol> stringIntoSymbols(String str, Grammar g) {
        ArrayList<Symbol> result = new ArrayList<>();
        while (!str.isEmpty()){
            Symbol symbol = Symbol.firstSymbolInString(str, g);
            result.add(symbol);
            str = str.substring(symbol.length());
        }
        return result;
    }

    // There are 4 cases when we erase the whitespace from the array
    // 1) if we have multiple whitespaces together we leave only one: [" ", " ", " "] -> [" "]
    // 2) The start and the end should not be the whitespace
    // 3) erase all other kinds of the Whitespaces: ["\n", "\t", ...]
    // 4) if the previous symbol is [";", "(", ")", "{", "}"]
    // 5) --- not yet implemented: remove whitespace after "," in the parameter declaration
    // 6) --- not yet implemented: remove whitespace before and after "="
    // 7) --- not yet implemented: remove whitespace before and after "+" and other operations
    // 8) --- fix the error: struct {} s; it removes whitespace between } and variable name
    public static ArrayList<Symbol> eraseExtraWhitespace(ArrayList<Symbol> array) {

        ArrayList<Symbol> result = new ArrayList<>();

        Symbol space = new Symbol(" ", Symbol.Type.Terminal);
        Symbol tab = new Symbol("\t", Symbol.Type.Terminal);
        Symbol endline = new Symbol("\n", Symbol.Type.Terminal);

        Symbol semicolon = new Symbol(";", Symbol.Type.Terminal);
        Symbol bracket1 = new Symbol("(", Symbol.Type.Terminal);
        Symbol bracket2 = new Symbol(")", Symbol.Type.Terminal);
        Symbol curlyBracket1 = new Symbol("{", Symbol.Type.Terminal);
        Symbol curlyBracket2 = new Symbol("}", Symbol.Type.Terminal);


        Predicate<Symbol> isWhitespace = symbol -> symbol.equals(space) || symbol.equals(tab) || symbol.equals(endline);
        Predicate<Symbol> isPredefinedSymbol = symbol -> symbol.equals(semicolon) || symbol.equals(bracket1)
                || symbol.equals(bracket2) || symbol.equals(curlyBracket1) || symbol.equals(curlyBracket2);
        Predicate<Symbol> isOpeningBracket = symbol -> symbol.equals(bracket1) || symbol.equals(curlyBracket1);

        for (int i = 0; i < array.size(); i++) {
            Symbol currentSymbol = array.get(i);
            if (i > 0) {
                if (isWhitespace.test(currentSymbol)) {
                    if (currentSymbol.equals(tab) || currentSymbol.equals(endline)) {
                        continue;
                    }
                    if (i == array.size() || isWhitespace.test(array.get(i - 1)) || isPredefinedSymbol.test(array.get(i - 1))) {
                        continue;
                    }
                }

                if (isWhitespace.test(currentSymbol)) {
                    if ((i + 1) < array.size() && isOpeningBracket.test(array.get(i + 1))) {
                        continue;
                    }
                }

                result.add(currentSymbol);

            } else {
                if (!isWhitespace.test(currentSymbol)) {
                    result.add(currentSymbol);
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append("Start:\n").append(this.start).append("\n\n");

        output.append("Terminals:\n").append(terminals).append("\n\n");

        output.append("Nonterminals:\n").append(nonterminals).append("\n\n");

        output.append("Productions:\n");
        for (Production p : this.productions) {
            output.append(p).append("\n");
        }

        return output.toString();
    }

    public Symbol getStart() {
        return start;
    }

    public HashSet<Symbol> getTerminals() {
        return terminals;
    }

    public HashSet<Symbol> getNonterminals() {
        return nonterminals;
    }

    public List<Production> getProductions() {
        return productions;
    }
}