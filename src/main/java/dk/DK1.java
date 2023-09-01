package dk;

import tree.DTE;
import grammar.Grammar;
import grammar.Production;
import grammar.Symbol;
import util.Logger;

import java.util.*;

import static util.Logger.log;

public class DK1 {

    private final State start;
    private final HashSet<State> states;
    private final Grammar g;

    public DK1(Grammar g) {
        // Initialize the Grammar
        this.g = g;

        // Initialize the Start State
        start = new State();
        for (Production production : g.getProductions()) {
            if (production.getLeft().equals(g.getStart())) {
                start.addItem(new Item(production, 0, g.getTerminals()));
            }
        }

        // Make Epsilon Moves from the Start State
        start.makeEpsilonMoves(g);

        // Put the start state in the states
        states = new HashSet<>();
        states.add(start);

        // Make Transitions and Find all States
        Queue<State> queue = new LinkedList<>();
        HashSet<State> queueCheck = new HashSet<>();
        queue.add(start);
        queueCheck.add(start);

        while (!queue.isEmpty()) {
            State currentState = queue.remove();
            currentState.makeShiftMoves(states, g);

            if (states.size() % 100 == 0) {
                log(queue.size() + " " + states.size());
            }

            for (Map.Entry<Symbol, State> entry : currentState.getPaths().entrySet()) {
                State newState = entry.getValue();

                boolean flag = false;
                for (State state : queueCheck) {
                    if (state.getItems().size() == newState.getItems().size() && state.sameItems(newState)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;

                queue.add(newState);
                queueCheck.add(newState);
            }
        }
    }

    // DK1 Test
    public boolean Test() {
        for (State state : states) {
            for (Item R1 : state.getItems()) {
                for (Item R2 : state.getItems()) {
                    if (!R1.isComplete()) break;
                    if (R1.equals(R2)) continue;
                    if (R2.isComplete()) {
                        for (Symbol symbol : R1.getLookaheads()) {
                            if (R2.getLookaheads().contains(symbol)) {
                                log("Rejecting State Condition_1: ");
                                log(R1);
                                log(R2);
                                log(state.toStringOnlyState());
                                return false;
                            }
                        }
                    } else {
                        Symbol currentSymbol = R2.currentSymbol();

                        if (R1.getLookaheads().contains(currentSymbol)) {
                            log("Rejecting State Condition_2: ");
                            log(R1);
                            log(R2);
                            log(state.toStringOnlyState());
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public DTE parseString(String validString) {

        String str = validString.replaceAll("\\s", "");

        ArrayList<Symbol> validStringArray = new ArrayList<>();
        int index = 0;
        while (index < str.length()) {
            Symbol currentSymbol = Symbol.firstSymbolInString(str.substring(index), g);
            validStringArray.add(currentSymbol);
            assert currentSymbol != null;
            index += currentSymbol.length();
        }

        // Initialize The parse tree with the validStringArray and after the parsing only the root will be in the array
        ArrayList<DTE> parseTree = new ArrayList<>();

        for (Symbol symbol : validStringArray) {
            parseTree.add(new DTE(symbol));
        }

        // Parsing Process
        Item handle;

        while (!validStringArray.get(0).equals(g.getStart())) {
            handle = findHandle(validStringArray);

            log(validStringArray + "     [handle: " + handle +"]");

            // Update the valid String Array
            Production hProd = Production.PROG_PROD;
            if (handle.getProduction() != null) {
                hProd = handle.getProduction();
            }
            validStringArray = makeReduction(validStringArray, hProd, handle.getDotIndex());

            // Update the parse Tree
            parseTree = DTE.updateTheParseTree(parseTree, handle);
            /*
            for (DerivationTreeElement Element : parseTree) {
                System.out.print(Element.getLabel() + ", ");
            }
            log();
            */
        }

        log(validStringArray);

        return parseTree.get(0);

    }

    public Item findHandle(ArrayList<Symbol> validStringArray) {

        State currentState = start;
        int dotIndex = 0;
        Symbol currentSymbol = validStringArray.get(dotIndex);

        while (currentState != null) {

            //log("\n");
            //log(currentSymbol);
            //log(currentState.toStringOnlyState());

            // If complete Item Check if it is the handle
            if (!currentState.getCompleteItems().isEmpty()) {
                for (Item item : currentState.getCompleteItems()) {
                    Symbol lookahead = validStringArray.size() > dotIndex ? validStringArray.get(dotIndex) : null;

                    if (lookahead != null && !item.getLookaheads().contains(lookahead)) continue;
                    return new Item(item.getProduction(), dotIndex, new HashSet<>());
                }
            }

            // Make transition
            boolean madeTransition = false;
            for (Map.Entry<Symbol, State> entry : currentState.getPaths().entrySet()) {
                Symbol transitionSymbol = entry.getKey();
                State transitionState = entry.getValue();

                if (transitionSymbol.equals(currentSymbol)) {
                    dotIndex++;
                    currentState = transitionState;
                    currentSymbol = validStringArray.size() > dotIndex ? validStringArray.get(dotIndex) : null;
                    madeTransition = true;
                    break;
                }
            }

            if (!madeTransition) {
                return null;
            }
        }

        return null;
    }

    public ArrayList<Symbol> makeReduction(ArrayList<Symbol> validStringArray, Production handle, int dotIndex) {

        int handleIndex = dotIndex - handle.getRight().size();

        // If the find Handle, make a reduction
        if (handleIndex != -1) {
            ArrayList<Symbol> newValidStringArray = new ArrayList<>();

            for (int i = 0; i < handleIndex; i++) {
                newValidStringArray.add(validStringArray.get(i));
            }
            newValidStringArray.add(handle.getLeft());
            for (int i = handleIndex + handle.getRight().size(); i < validStringArray.size(); i++) {
                newValidStringArray.add(validStringArray.get(i));
            }

            return newValidStringArray;
        }

        return null;
    }

    public State getStart() {
        return start;
    }

    public HashSet<State> getStates() {
        return states;
    }

    public Grammar getG() {
        return g;
    }

}
