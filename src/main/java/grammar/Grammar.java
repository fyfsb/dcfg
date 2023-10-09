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
            terminals.add(new Symbol(terminal, Symbol.SymbolType.Terminal));
        }
        terminals.add(new Symbol(" ", Symbol.SymbolType.Terminal));
        terminals.add(new Symbol("\t", Symbol.SymbolType.Terminal));
        terminals.add(new Symbol("\n", Symbol.SymbolType.Terminal));
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
            nonterminals.add(new Symbol(parts[0], Symbol.SymbolType.Nonterminal));
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
            Symbol left = new Symbol(parts[0], Symbol.SymbolType.Nonterminal);
            // Decompose merged productions and save separately.
            String[] rightParts = parts[1].split(" \\| ");
            for (String rightStr : rightParts) {
                ArrayList<Symbol> right = stringIntoSymbols(rightStr, this.getTerminals(), this.getNonterminals());
                right = eliminateExtraWhitespace(right);
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

            Symbol nonterminal = new Symbol(subStr, Symbol.SymbolType.Nonterminal);
            if (nonterminals.contains(nonterminal)) {
                return nonterminal;
            }

            Symbol terminal = new Symbol(subStr, Symbol.SymbolType.Terminal);
            if (terminals.contains(terminal)) {
                return terminal;
            }
        }

        throw new IllegalArgumentException("Can't find the first symbol in this string:  " + str);
    }

    // Removes all the extra whitespaces in the given validStringArray as specified below
    // 1) If there are adjacent whitespaces, remove them, and leave only one: [" ", " ", " "] -> [" "]
    // 2) Erase all the tabs and end-lines: ["\n", "\t", ...]
    // 3) Erase all surrounding whitespaces of: , ; ( ) + - * / & | ! < > = [ ]
    public static ArrayList<Symbol> eliminateExtraWhitespace(ArrayList<Symbol> validStringArray) {

        Symbol space = new Symbol(" ", Symbol.SymbolType.Terminal);
        Symbol tab = new Symbol("\t", Symbol.SymbolType.Terminal);
        Symbol endLine = new Symbol("\n", Symbol.SymbolType.Terminal);

        HashSet<Symbol> syntaxSymbols = initializeSyntaxSymbols();


        Predicate<Symbol> isWhitespace = symbol -> symbol.equals(space) || symbol.equals(tab) || symbol.equals(endLine);
        Predicate<Symbol> isSyntaxSymbol = syntaxSymbols::contains;

        ArrayList<Symbol> result = new ArrayList<>();
        // Left to Right loop to remove all the extra whitespace
        for (int i = 0; i < validStringArray.size() - 1; i++) {
            Symbol currentSymbol = validStringArray.get(i);
            Symbol nextSymbol = validStringArray.get(i + 1);

            if (isWhitespace.test(currentSymbol)) {
                if(currentSymbol.equals(tab) || currentSymbol.equals(endLine)) {
                    continue;
                } else {
                    if (isWhitespace.test(nextSymbol)) {
                        continue;
                    }
                }
            }

            if (isWhitespace.test(currentSymbol)) {
                if (isSyntaxSymbol.test(nextSymbol)) {
                    continue;
                }
            }

            if (isWhitespace.test(currentSymbol)) {
                if (i > 0 && isSyntaxSymbol.test(validStringArray.get(i - 1))) {
                    continue;
                }
            }

            result.add(currentSymbol);
        }

        Symbol lastSymbol = validStringArray.get(validStringArray.size() - 1);
        if (!isWhitespace.test(lastSymbol)) {
            result.add(lastSymbol);
        }

        return result;

    }

    private static HashSet<Symbol> initializeSyntaxSymbols() {
        HashSet<Symbol> syntaxSymbols = new HashSet<>();
        syntaxSymbols.add(new Symbol(",", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol(";", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("+", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("-", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("*", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("/", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("&", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("|", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("!", Symbol.SymbolType.Terminal));
        syntaxSymbols.add(new Symbol("=", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol("[", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol("]", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol("(", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol(")", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol("<", Symbol.SymbolType.Terminal));
        //syntaxSymbols.add(new Symbol(">", Symbol.SymbolType.Terminal));
        return syntaxSymbols;
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