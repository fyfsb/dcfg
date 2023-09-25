package dk;

import grammar.Grammar;
import grammar.Production;
import grammar.Symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State {

    private final HashSet<Item> items = new HashSet<>();
    private final HashMap<Symbol, State> paths = new HashMap<>();
    private final HashSet<Item> completeItems = new HashSet<>();

    public boolean addItem(Item newItem) {

        for (Item item : items) {
            if (item.sameDottedRule(newItem)) {
                int size = item.getLookaheads().size();
                item.addLookaheads(newItem.getLookaheads());
                return size < item.getLookaheads().size();
            }
        }

        items.add(newItem);
        if (newItem.isComplete()) {
            completeItems.add(newItem);
        }
        return true;
    }

    // Make Epsilon Moves
    public void makeEpsilonMoves(Grammar g) {

        boolean newItems;
        do {
            newItems = false;

            for (Item item : new HashSet<>(items)) { // Copy items to avoid ConcurrentModificationException

                Symbol currentSymbol = item.currentSymbol();

                if (currentSymbol == null) continue;

                if (!currentSymbol.isTerminal()) {

                    Symbol nextSymbol = item.nextSymbol();

                    HashSet<Symbol> lookaheads;
                    if (nextSymbol == null) {
                        lookaheads = new HashSet<>(item.getLookaheads());
                    } else {
                        lookaheads = State.lookaheadsFromSymbol(nextSymbol, new HashSet<>(), g);
                    }

                    for (Production production : g.getProductions()) {
                        if (production.getLeft().equals(currentSymbol)) {
                            Item newItem = new Item(production, 0, lookaheads);
                            newItems = newItems || addItem(newItem);
                        }
                    }
                }
            }

        } while (newItems);
    }

    // Return all the symbols that can be derived from the given symbol
    public static HashSet<Symbol> lookaheadsFromSymbol(Symbol symbol, HashSet<Symbol> symbols, Grammar g) {
        HashSet<Symbol> lookaheads = new HashSet<>();
        symbols.add(symbol);

        if (symbol.isTerminal()) {
            lookaheads.add(symbol);
            return lookaheads;
        } else {
            for (Production production : g.getProductions()) {
                if (production.getLeft().equals(symbol)) {
                    Symbol derivedSymbol = production.getRight().get(0);
                    if (!symbols.contains((derivedSymbol))) {
                        lookaheads.addAll((lookaheadsFromSymbol(derivedSymbol, symbols, g)));
                    }
                }
            }
        }

        return lookaheads;
    }


    // Make Shift Moves
    public void makeShiftMoves(HashSet<State> states, Grammar g) {
        Map<Symbol, Set<Item>> symbolToItemsMap = new HashMap<>();

        // Find all transition symbol possibilities and map their items
        for (Item item : items) {
            Symbol currentSymbol = item.currentSymbol();

            if (currentSymbol == null) continue;

            symbolToItemsMap.computeIfAbsent(currentSymbol, k -> new HashSet<>()).add(item);
        }

        // Make transition paths
        for (Map.Entry<Symbol, Set<Item>> entry : symbolToItemsMap.entrySet()) {
            Symbol transitionSymbol = entry.getKey();
            Set<Item> transitionItems = entry.getValue();
            State transitionState = createTransitionState(transitionItems, states, g);
            paths.put(transitionSymbol, transitionState);
        }
    }

    // Create Transition State
    private State createTransitionState(Set<Item> transitionItems, HashSet<State> states, Grammar g) {
        State transitionState = new State();

        for (Item item : transitionItems) {
            Item newItem = new Item(item.getProduction(), item.getDotIndex() + 1, item.getLookaheads());
            transitionState.addItem(newItem);
        }

        transitionState.makeEpsilonMoves(g);

        for (State state : states) {

            int itemsSize = transitionState.getItems().size();
            if (state.getItems().size() == itemsSize && state.sameItems(transitionState)) {
                return state;
            }
        }
        states.add(transitionState);
        return transitionState;
    }

    public boolean sameItems(State newState) {

        HashSet<Item> items1 = items;
        HashSet<Item> items2 = newState.getItems();

        if (items1.size() != items2.size()) return false;

        for (Item item1 : items1) {
            boolean flag = false;
            for (Item item2 : items2) {
                if (item1.equals(item2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }

        for (Item item2 : items2) {
            boolean flag = false;
            for (Item item1 : items1) {
                if (item2.equals(item1)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }

        return true;
    }

    public String toStringOnlyState() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of Completed Items: ").append(completeItems.size()).append("\n");

        for (Item item : items) {
            sb.append(item).append("\n");
        }
        return sb + "\n";
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Number of Completed Items: ").append(completeItems.size()).append("\n");

        for (Item item : items) {
            sb.append(item).append("\n");
        }

        for (Map.Entry<Symbol, State> entry : paths.entrySet()) {
            Symbol transitionSymbol = entry.getKey();
            State transitionState = entry.getValue();
            sb.append("Transition Symbol: ").append(transitionSymbol).append("\n").append(transitionState.toStringOnlyState()).append("\n");
        }

        return sb + "\n";

    }

    public HashSet<Item> getItems() {
        return items;
    }

    public HashMap<Symbol, State> getPaths() {
        return paths;
    }

    public HashSet<Item> getCompleteItems() {
        return completeItems;
    }

}
