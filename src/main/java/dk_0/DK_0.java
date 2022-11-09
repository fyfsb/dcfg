package dk_0;

import grammar.Grammar;
import grammar.Production;
import grammar.Token;
import util.DK_0_Utils;
import util.Utils;

import java.util.ArrayList;
import java.util.HashSet;

public class DK_0 {
    //DK_0 test
    public static boolean test(Grammar g){
        ArrayList<Item> items = new ArrayList<>();
        for(Production p : g.getProductions()){
            if(p.getLeft().getString().equals(g.getStart().getString())){
                items.add(new Item(p, 0));
            }
        }
        DK_0_Utils.makeEpsilonMoves(items, g);
        //Avoid cycles by generating the same states
        HashSet<Integer> states = new HashSet<>();
        return check(items, g, states);
    }
    //Check whether the DK_0 test fails or not
    private static boolean check(ArrayList<Item> items, Grammar g, HashSet<Integer> states){
        states.add(items.toString().hashCode());
        int count = DK_0_Utils.countCompleteRules(items);
        if(count > 1){
            System.out.println("Accept State: " + items + " " + "false");
            return false;
        }
        //Check if a terminal symbol immediately follows the dot
        if(count == 1){
            boolean flag = !DK_0_Utils.checkTerminalAfterDot(items);
            if(flag){
                if(items.size() > 1){
                    proceed(DK_0_Utils.removeCompletedRule(items), g, states);
                } else {
                    System.out.println("Accept State: " + items + " " + "true");
                    return true;
                }
            } else {
                System.out.println("Accept State: " + items + " " + "false");
                return false;
            }
        }
        return proceed(items, g, states);
    }
    //Move the dot forward
    private static boolean proceed(ArrayList<Item> items, Grammar g, HashSet<Integer> states){
        boolean aggregate = true;
        ArrayList<Item> localItems;
        for(Token step : Utils.possibleSteps(items)){
            localItems = new ArrayList<>();
            for(Item i : items){
                if(step.getString().equals(i.getProduction().getRight().get(i.getIndex()).getString())){
                    localItems.add(new Item(i.getProduction(), i.getIndex() + 1));
                }
            }
            DK_0_Utils.makeEpsilonMoves(localItems, g);
            if(localItems.size() > 0){
                if(!states.contains(localItems.toString().hashCode())){
                    aggregate = aggregate & check(localItems, g, states);
                }
            }
        }
        return aggregate;
    }
}
