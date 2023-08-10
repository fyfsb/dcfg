package grammar;

import java.util.ArrayList;
import java.util.List;

public class Production {

    private final Symbol left;
    private final ArrayList<Symbol> right;
    public static Production PROG_PROD = new Production(
            new Symbol("<prog>", Symbol.Type.Nonterminal),
            new ArrayList<>(List.of(
                    new Symbol("<FuD>", Symbol.Type.Nonterminal)
            ))
    );

    public Production(final Symbol left, final ArrayList<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    public Production(final String str, Grammar g) {

        String noWhitespace = str.replaceAll("\\s", "");
        String[] parts = noWhitespace.split("->");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid production string. Expected format: 'left -> right'");
        }

        left = new Symbol(parts[0], Symbol.Type.Nonterminal);

        right = new ArrayList<>();
        int index = 0;
        while (index < parts[1].length()) {
            Symbol currentSymbol = Symbol.firstSymbolInString(parts[1].substring(index), g);
            right.add(currentSymbol);
            assert currentSymbol != null;
            index += currentSymbol.length();
        }

        right.add(new Symbol("|", Symbol.Type.Terminal)); // Make decomposition by "|" easier
    }

    @Override
    public String toString() {
        return this.left + " -> " + this.right;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Production production = (Production) obj;

        return left.equals(production.getLeft()) && right.equals(production.getRight());
    }

    @Override
    public int hashCode() {
        int result = right.hashCode();
        result = result * 31 + left.hashCode();
        return result;
    }

    public Symbol getLeft() {
        return left;
    }

    public ArrayList<Symbol> getRight() {
        return right;
    }

}
