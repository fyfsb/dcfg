package dk_0;

import grammar.Production;

public class Item {
    //Attributes
    private Production production;
    private int index;
    //Constructors
    public Item(){
        production = new Production();
        index = 0;
    }
    public Item(Production production, int index){
        this.production = production;
        this.index = index;
    }
    //Functions
    public String toString(){
        return production.toString() + ", " + index;
    }
    //Getters and Setters
    public Production getProduction() {
        return production;
    }
    public void setProduction(Production production) {
        this.production = production;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
