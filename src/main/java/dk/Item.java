package dk;

import grammar.Production;
import grammar.Symbol;

import java.util.HashSet;

// Encapsulates the concept of an item / dotted rule explained in the Sipser's book.
public class Item {

    // A ‘Production’ object that represents the production rule for this dotted rule.
    private final Production production;
    // An Integer representing the location of a corresponding dot in the dotted rule.
    private final int dotIndex;
    // A set of ‘Symbol’ objects to represent all the lookahead symbols for this dotted rule.
    private final HashSet<Symbol> lookaheads;

    // Initializes an item with the specified production, dotIndex, and lookaheads
    public Item(Production production, int dotIndex, HashSet<Symbol> lookaheads) {
        this.production = production;
        this.dotIndex = dotIndex;
        this.lookaheads = lookaheads;
    }

    // Returns true if the dotted rule is complete, i.e., if the dot is at the end of the production rule; otherwise, it returns false
    public boolean isComplete() {
        return production.getRight().size() == dotIndex;
    }

    // Merges two sets of lookahead symbols.
    public void addLookaheads(HashSet<Symbol> newLookaheads) {
        lookaheads.addAll(newLookaheads);
    }

    // This function is similar to equals, but it doesn't compare lookaheads. During the lookahead calculation, it's essential to identify identical production rules and dotIndex for merging the lookaheads.
    public boolean sameProductionAndDot(Item item) {
        return dotIndex == item.getDotIndex() && production.equals(item.getProduction());
    }

    // Returns the symbol next to the dot in the production rule if it exists; Otherwise returns null
    public Symbol currentSymbol() {
        if (dotIndex >= production.getRight().size()) return null;
        return production.getRight().get(dotIndex);
    }

    // Returns the symbol after the symbol next to the dot in the production rule if it exists, Otherwise returns null. These functions, are trivial but shortens the code significantly.
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

    // Determines if two ‘Item’ objects are equal in all attributes: production, dotIndex, and lookaheads.
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

    // Returns a hash code of the item.
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
