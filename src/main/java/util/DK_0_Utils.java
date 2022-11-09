package util;

import dk_0.Item;
import grammar.Grammar;
import grammar.Production;
import grammar.Token;
import grammar.Type;

import java.util.ArrayList;
import java.util.HashSet;

public class DK_0_Utils {
    //Add new items because of nonterminals - make epsilon moves
    public static void makeEpsilonMoves(ArrayList<Item> items, Grammar g){
        if(items == null || g == null){
            System.out.println("DK_0_Utils.makeEpsilonMoves null exception");
            return;
        }
        ArrayList<Item> newItems = new ArrayList<>();
        //Hash and save items to do not duplicate them
        HashSet<Integer> check = new HashSet<>();
        for(Item i : items){
            check.add(i.toString().hashCode());
        }
        //Add new items
        for(Item i : items){
            Token token;
            if(i.getIndex() < i.getProduction().getRight().size()){
                token = i.getProduction().getRight().get(i.getIndex());
            } else {
                continue;
            }
            if(token.getType() == Type.NONTERMINAL){
                for(Production p : g.getProductions()){
                    if(p.getLeft().getString().equals(token.getString())){
                        Item newItem = new Item(p, 0);
                        if(!check.contains(newItem.toString().hashCode())){
                            newItems.add(newItem);
                            check.add(newItem.hashCode());
                        }
                    }
                }
            }
        }
        if(newItems.size() > 0)makeEpsilonMoves(newItems, g);
        items.addAll(newItems);
    }
    //Check how many dotted rules are complete
    public static int countCompleteRules(ArrayList<Item> items) {
        int count = 0;
        if(items == null) {
            System.out.println("DK_0_Utils.countCompleteRules null exception");
            return count;
        }
        for(Item i : items) {
            if(i.getIndex() == i.getProduction().getRight().size()) {
                count++;
            }
        }
        return count;
    }
    //Return true if a terminal symbol immediately follows the dot
    public static boolean checkTerminalAfterDot(ArrayList<Item> items) {
        if(items == null) {
            System.out.println("DK_0_Utils.checkTerminalAfterDot null exception");
            return false;
        }
        for(Item i : items){
            Token token;
            if(i.getIndex() < i.getProduction().getRight().size()){
                token = i.getProduction().getRight().get(i.getIndex());
                if(token.getType() == Type.TERMINAL){
                    return true;
                }
            }
        }
        return false;
    }
    //Remove completed rule from the items
    public static ArrayList<Item> removeCompletedRule(ArrayList<Item> items){
        for(Item i : items){
            if(i.getProduction().getRight().size() == i.getIndex()){
                items.remove(i);
                return items;
            }
        }
        System.out.println("DK_0_Utils.removeCompletedRule could not find the completed rule");
        return items;
    }
}
