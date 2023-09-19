package grammar;

import java.util.ArrayList;

public class Production {

    private final Symbol left;
    private final ArrayList<Symbol> right;

    public Production(final Symbol left, final ArrayList<Symbol> right) {
        this.left = left;
        this.right = right;
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