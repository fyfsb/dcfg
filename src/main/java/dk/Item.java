package dk;

import grammar.Production;
import grammar.Symbol;

import java.util.HashSet;

public class Item {

    private final Production production;
    private final int dotIndex;
    private final HashSet<Symbol> lookaheads;

    public Item(Production production, int dotIndex, HashSet<Symbol> lookaheads) {
        this.production = production;
        this.dotIndex = dotIndex;
        this.lookaheads = lookaheads;
    }

    public boolean isComplete() {
        return production.getRight().size() == dotIndex;
    }

    public void addLookaheads(HashSet<Symbol> newLookaheads) {
        lookaheads.addAll(newLookaheads);
    }

    public boolean sameDottedRule(Item item) {
        return dotIndex == item.getDotIndex() && production.equals(item.getProduction());
    }

    // Returns the symbol next to the dot
    public Symbol currentSymbol() {
        if (dotIndex >= production.getRight().size()) return null;
        return production.getRight().get(dotIndex);
    }

    // Returns the symbol after the symbol next to the dot
    public Symbol nextSymbol() {
        if ((dotIndex + 1) >= production.getRight().size()) return null;
        return production.getRight().get(dotIndex + 1);
    }

    @Override
    public String toString() {

        StringBuilder dottedRuleString = new StringBuilder(production + " " + dotIndex);

        StringBuilder lookaheadSymbolsString = new StringBuilder("  [");
        for (Symbol symbol : lookaheads) {
            lookaheadSymbolsString.append(symbol.getContent()).append(", ");
        }
        // Remove the last comma and space only if lookaheadSymbols is not empty
        if (!lookaheads.isEmpty()) {
            lookaheadSymbolsString.delete(lookaheadSymbolsString.length() - 2, lookaheadSymbolsString.length());
        }
        return dottedRuleString.append(lookaheadSymbolsString).append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Item item = (Item) obj;

        return (dotIndex == item.getDotIndex()) && production.equals(item.getProduction()) && lookaheads.equals(item.getLookaheads());

    }

    @Override
    public int hashCode() {
        int result = production.hashCode();
        result = 31 * result + dotIndex;
        result = 39 * result + lookaheads.hashCode();
        return result;
    }

    public int getDotIndex() {
        return dotIndex;
    }

    public HashSet<Symbol> getLookaheads() {
        return lookaheads;
    }

    public Production getProduction() {
        return production;
    }

}
