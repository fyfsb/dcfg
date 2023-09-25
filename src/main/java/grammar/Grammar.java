package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

// Represents a Context-Free Grammar.
// The main goal of this class is to read grammar information from the text files ('Grammar.txt' and 'Terminal.txt'),
// manipulate (tokenize, pre-process), and store this information in suitable data structures for further use.
public class Grammar {

    // The start symbol of the grammar.
    // By convention, it's the left nonterminal of the first production rule.
    private final Symbol start;
    // The set of terminal symbols.
    private final HashSet<Symbol> terminals;
    // The set of nonterminal symbols.
    private final HashSet<Symbol> nonterminals;
    // The list of production rules.
    private final List<Production> productions;

    // Receives file paths of 'Grammar.txt' and 'Terminals.txt'.
    // Initializes the set of terminals.
    // Initializes the set of nonterminals.
    // Initializes the list of productions.
    // Initializes the start symbol.
    public Grammar(String grammarFilePath, String terminalsFilePath) throws FileNotFoundException {
        terminals = new HashSet<>();
        nonterminals = new HashSet<>();
        productions = new ArrayList<>();

        // Read terminal symbols.
        readTerminals(terminalsFilePath);

        // Read nonterminal symbols.
        readNonterminals(grammarFilePath);

        // Read Productions.
        readProductions(grammarFilePath);

        // Initialize the start symbol.
        start = productions.get(0).getLeft();
    }

    // Reads terminals from 'Terminals.txt'
    // Removes all kinds of white space from a line during the reading process.
    // Add [" ", "\t", "\n"] symbols to the terminals set.
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

    // Reads nonterminals from 'Grammar.txt'
    // Removes all kinds of white space from a line during the reading process.
    // Add left-hand side of "->" to the nonterminals set.
    private void readNonterminals(String grammarFilePath) throws FileNotFoundException {
        Scanner in = new Scanner(new File(grammarFilePath));
        while (in.hasNext()) {
            String str = in.nextLine();
            String withoutWhitespace = str.replaceAll("\\s", "");
            // Nonterminals occur on the left-hand side of the productions.
            String[] parts = withoutWhitespace.split("->");
            nonterminals.add(new Symbol(parts[0], Symbol.Type.Nonterminal));
        }
    }

    // Reads productions from 'Grammar.txt'
    // Multiple productions might be merged by '|' symbol to shorten the record size,
    // we split the line by "->", "|" and save single productions separately.
    private void readProductions(String grammarFilePath) throws FileNotFoundException {
        Scanner in = new Scanner(new File(grammarFilePath));
        while (in.hasNext()) {
            final String str = in.nextLine();
            // Distinguish left and right attributes
            String[] parts = str.split(" -> ");
            Symbol left = new Symbol(parts[0], Symbol.Type.Nonterminal);
            // Decompose merged productions and save separately.
            String[] rightParts = parts[1].split(" \\| ");
            for (String rightStr : rightParts) {
                ArrayList<Symbol> right = stringIntoSymbols(rightStr, this.getTerminals(), this.getNonterminals());
                right = eraseExtraWhitespace(right);
                productions.add(new Production(left, right));
            }
        }
    }

    // Receives a string of symbols, and sets of all terminal and nonterminal symbols.
    // Decomposes the given string into symbols and returns respective ArrayList.
    // Basically this function is a tokenization of a string into the respective ArrayList of 'symbols'.
    public static ArrayList<Symbol> stringIntoSymbols(String str,  HashSet<Symbol> terminals, HashSet<Symbol> nonterminals) {
        ArrayList<Symbol> result = new ArrayList<>();
        while (!str.isEmpty()){
            Symbol symbol = firstSymbolInString(str, terminals, nonterminals);
            result.add(symbol);
            str = str.substring(symbol.length());
        }
        return result;
    }

    // Receives a string of symbols, and sets of all terminal and nonterminal symbols.
    // Returns the longest terminal/nonterminal symbol starting from index = 0.
    public static Symbol firstSymbolInString(String str, HashSet<Symbol> terminals, HashSet<Symbol> nonterminals) throws IllegalArgumentException {
        for (int i = 0; i < str.length(); i++) {
            String subStr = str.substring(0, str.length() - i);

            Symbol nonterminal = new Symbol(subStr, Symbol.Type.Nonterminal);
            if (nonterminals.contains(nonterminal)) {
                return nonterminal;
            }

            Symbol terminal = new Symbol(subStr, Symbol.Type.Terminal);
            if (terminals.contains(terminal)) {
                return terminal;
            }
        }

        throw new IllegalArgumentException("Can't find the first symbol in this string:  " + str);
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

    // Pre-processing no comments yet, before we finalize the implementation of this function.
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

    // Returns a string representation of the entire grammar.
    // Handy for debugging or visual representation.
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