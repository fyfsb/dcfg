package dk;

import tree.DTE;
import grammar.Grammar;
import grammar.Production;
import grammar.Symbol;

import java.util.*;

import static util.Logger.log;

public class DK1 {

    // A ‘State’ object representing the start state of the automaton.
    private final State start;

    // A set of ‘State’ objects representing all the states of the automaton.
    private final HashSet<State> states;

    // A ‘Grammar’ object representing the input CFG.
    private final Grammar g;

    // Creates the DK_1 automaton for the given CFG, grammar.
    public DK1(Grammar grammar) {
        // Initialize the Grammar
        this.g = grammar;

        // Initialize the Start State
        start = new State();
        for (Production production : grammar.getProductions()) {
            if (production.getLeft().equals(grammar.getStart())) {
                start.addItem(new Item(production, 0, grammar.getTerminals()));
            }
        }

        // Make Epsilon Moves from the Start State
        start.makeEpsilonMoves(grammar);

        // Put the start state in the states
        states = new HashSet<>();
        states.add(start);

        // Make Transitions and Find all States
        Queue<State> queue = new LinkedList<>();
        Queue<State> queueCheck = new LinkedList<>();
        queue.add(start);
        queueCheck.add(start);

        // Variable completionPercentage to display the progress of the automaton creation
        int completionPercentage = 0;

        while (!queue.isEmpty()) {
            State currentState = queue.remove();
            currentState.makeShiftMoves(states, grammar);

            if ((int) (states.size() / 28.07) > completionPercentage) {
                completionPercentage = (int) (states.size() / 28.07);
                log("DK1 Automaton Progress: " + completionPercentage + "%");
            }

            for (Map.Entry<Symbol, State> entry : currentState.getTransitionFunction().entrySet()) {
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

    // Returns true if the ‘Grammar’ is LR(1), false otherwise.
    public boolean dk1Test() {
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

    // Returns a derivation tree for the given valid string.
    public DTE parseString(String validString) {

        ArrayList<Symbol> validStringArray = Grammar.stringIntoSymbols(validString, g.getTerminals(), g.getNonterminals());
        validStringArray = Grammar.eleminateExtraWhitespace(validStringArray);

        // Initialize The parse tree with the validStringArray and after the parsing only the root will be in the array
        ArrayList<DTE> parseTree = new ArrayList<>();

        for (Symbol symbol : validStringArray) {
            parseTree.add(new DTE(symbol));
        }

        // Parsing Process
        Item handle;

        while (!validStringArray.get(0).equals(g.getStart())) {
            handle = findHandle(validStringArray);

            log(validStringArray + "     [handle: " + handle.getProduction() +"]");

            validStringArray = makeReduction(validStringArray, handle.getProduction(), handle.getDotIndex());

            // Update the parse Tree
            parseTree = DTE.updateTheParseTree(parseTree, handle);
        }

        log(validStringArray);

        return parseTree.get(0);

    }

    // Returns the handle for the given valid string.
    public Item findHandle(ArrayList<Symbol> validStringArray) {

        State currentState = start;
        int dotIndex = 0;
        Symbol currentSymbol = validStringArray.get(dotIndex);

        while (currentState != null) {

            //Remove
            //log(currentState);
            //log("\n");

            // If complete Item Check if it is the handle
            if (!currentState.getCompleteItems().isEmpty()) {
                Symbol lookahead = validStringArray.size() > dotIndex ? validStringArray.get(dotIndex) : null;

                for (Item item : currentState.getCompleteItems()) {
                    if (lookahead != null && !item.getLookaheads().contains(lookahead)) continue;
                    return new Item(item.getProduction(), dotIndex, new HashSet<>());
                }
            }

            // Make transition
            boolean madeTransition = false;
            for (Map.Entry<Symbol, State> entry : currentState.getTransitionFunction().entrySet()) {
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

    // Makes a reduction of the valid string based on the provided handle.
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("The DK1 Automaton with ").append(states.size()).append("state : ").append("\n\n");
        for (State state : states) {
            sb.append(state.toStringOnlyState());
        }
        return sb.toString();
    }

    public State getStart() {
        return start;
    }

    public HashSet<State> getStates() {
        return states;
    }

    public Grammar getGrammar() {
        return g;
    }

}
