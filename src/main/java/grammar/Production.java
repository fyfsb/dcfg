package grammar;

import java.util.ArrayList;

// Encapsulates the concept of a production rule of a Context-Free Grammar.
// A production dictates how a nonterminal symbol can be replaced by a sequence of terminal and/or nonterminal symbols.
public class Production {

    // The nonterminal symbol on the left side of the production rule.
    private final Symbol left;
    // The ArrayList of 'Symbol' objects representing the sequence of terminal and/or nonterminal symbols on the right side of the production rule.
    private final ArrayList<Symbol> right;

    // Initializes a production with the specified left and right attributes of the production rule.
    public Production(final Symbol left, final ArrayList<Symbol> right) {
        this.left = left;
        this.right = right;
    }

    // Returns a string representation of the production rule.
    // Handy for debugging or visual representation.
    @Override
    public String toString() {
        return this.left + " -> " + this.right;
    }

    // Determines whether two 'Production' objects are identical.
    // Two production rules are equal if their left-hand side nonterminals and right-hand side ArrayLists are equal respectively.
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

    // Returns a hash code of the symbol.
    // Ensuring correct hash code calculation to correctly implement hash sets and hash maps.
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