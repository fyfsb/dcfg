package grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Grammar {

    private Symbol start;
    private final HashSet<Symbol> terminals;
    private final HashSet<Symbol> nonterminals;
    private final List<Production> productions;

    public Grammar(String grammarFilePath, String terminalsFilePath) throws FileNotFoundException {
        terminals = new HashSet<>();
        nonterminals = new HashSet<>();
        productions = new ArrayList<>();

        // Read Terminals
        try (Scanner in = new Scanner(new File(terminalsFilePath))) {
            readTerminals(in);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Terminals file not found: " + terminalsFilePath);
        }

        //Read Nonterminals
        try (Scanner in = new Scanner(new File(grammarFilePath))) {
            readNonterminals(in);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Grammar file for Nonterminals not found: " + grammarFilePath);
        }

        // Read Productions + Initialize The Start Symbol
        try (Scanner in = new Scanner(new File(grammarFilePath))) {
            readProductions(in);

            if (!productions.isEmpty()) {
                start = productions.get(0).getLeft();
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Grammar file not found: " + grammarFilePath);
        }

    }

    private void readTerminals(Scanner in) {
        while (in.hasNext()) {
            final String terminal = in.nextLine().replaceAll("\\s", "");
            terminals.add(new Symbol(terminal, Symbol.Type.Terminal));
        }
        terminals.add(new Symbol("|", Symbol.Type.Terminal));
    }

    private void readNonterminals(Scanner in) {
        while (in.hasNext()) {
            String str = in.nextLine();
            String noWhitespace = str.replaceAll("\\s", "");
            String[] parts = noWhitespace.split("->");
            nonterminals.add(new Symbol(parts[0], Symbol.Type.Nonterminal));
        }
    }

    private void readProductions(Scanner in) {
        while (in.hasNext()) {
            final Production production = new Production(in.nextLine(), this);

            ArrayList<Symbol> right = new ArrayList<>(); // Split Production.right by "|"
            for (Symbol symbol : production.getRight()) {
                if (symbol.equals(new Symbol("|", Symbol.Type.Terminal))) {
                    productions.add(new Production(production.getLeft(), right));
                    right = new ArrayList<>();
                } else {
                    right.add(symbol);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append("Start:\n");
        output.append(this.start).append("\n");

        output.append("Terminals:\n");
        for (Symbol s : this.terminals) {
            output.append(s).append("\n");
        }

        output.append("Nonterminals:\n");
        for (Symbol s : this.nonterminals) {
            output.append(s).append("\n");
        }

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
