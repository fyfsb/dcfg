package tree;

import dk.Item;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static util.Logger.log;

public class DTE {

    private final Symbol label;
    private DTE father;
    private DTE firstSon;
    private DTE brother;

    public DTE(Symbol label) {
        this.label = label;
    }

    public static ArrayList<DTE> updateTheParseTree(ArrayList<DTE> parseTree, Item handle) {

        Symbol parentSymbol = handle.getProduction().getLeft();
        int rightIndex = handle.getDotIndex();
        int leftIndex = rightIndex - handle.getProduction().getRight().size();

        // Make Brothers
        for (int i = leftIndex; i < rightIndex - 1; i++) {
            parseTree.get(i).setBrother(parseTree.get(i + 1));
        }

        // Create father and make connection
        DTE father = new DTE(parentSymbol);
        father.setFirstSon(parseTree.get(leftIndex));
        for (int i = leftIndex; i < rightIndex; i++) {
            parseTree.get(i).setFather(father);
        }

        // Erase Children from the parse tree array and put father in their place
        ArrayList<DTE> newParseTree = new ArrayList<>();

        for (int i = 0; i < leftIndex; i++) {
            newParseTree.add(parseTree.get(i));
        }
        newParseTree.add(father);
        for (int i = rightIndex; i < parseTree.size(); i++) {
            newParseTree.add(parseTree.get(i));
        }

        return newParseTree;
    }

    public boolean isType(String type) {
        return labelContent().equals(type);
    }

    public List<DTE> getFlattenedSequence() {
        if (label == null) return null;
        List<DTE> result = new LinkedList<>();

        // XS -> XS; X
        if (firstSon.brother == null) {
            result.add(firstSon);
        } else {
            result.addAll(firstSon.getFlattenedSequence());
            result.add(firstSon.brother.brother);
        }

        return result;
    }

    public String getBorderWord() {
        if (label.isTerminal()) return labelContent();
        StringBuilder sb = new StringBuilder();
        for (DTE dte = firstSon; dte != null; dte = dte.brother) {
            sb.append(dte.getBorderWord());
        }

        return sb.toString();
    }

    public List<List<String>> extractComponentPairs() {
        List<DTE> flattened = getFlattenedSequence();
        List<List<String>> result = new LinkedList<>();
        for (DTE dte : flattened) {
            log(dte.getBorderWord());
            if (dte.isType(";")) {
                dte = dte.brother;
            }
            String type = dte.getFirstSon().getBorderWord();
            String name = dte.getNthSon(2).getBorderWord();

            result.add(List.of(type, name));
        }
        return result;
    }

    public int getChildrenSize() {
        int result = 0;
        var curr = this;
        //
        while (curr != null && curr.firstSon != null) {
            result++;
            curr = curr.firstSon.brother;
        }
        return result;
    }

    public int getSiblingCount() {
        int result = 0;
        for (DTE curr = this; curr != null; curr = curr.brother) {
            result++;
        }
        return result;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if (label == null) return "";
        if (label.isTerminal()) return labelContent();

        sb.append(labelContent());
        for (DTE dte = firstSon; dte != null; dte = dte.brother) {
            sb.append("\n\t|- ").append(dte);
        }
        return sb.toString();

    }

    public void printTree() {
        printTree(0, false);
    }

    private void printTree(int level, boolean isLast) {
        StringBuilder builder = new StringBuilder();

        builder.append((isLast ? "    " : "│   ").repeat(Math.max(0, level - 1)));
        if (level > 0) {
            builder.append(isLast ? "└── " : "├── ");
        }

        System.out.println(builder + labelContent());
        if (firstSon != null) {
            firstSon.printTree(level + 1, brother == null);
        }

        if (brother != null) {
            brother.printTree(level, false);
        }
    }

    public Symbol getLabel() {
        return label;
    }

    public DTE getFather() {
        return father;
    }

    private void setFather(DTE father) {
        this.father = father;
    }

    public DTE getFirstSon() {
        return firstSon;
    }

    private void setFirstSon(DTE firstSon) {
        this.firstSon = firstSon;
    }

    public DTE getBrother() {
        DTE res = brother;
        if (res == null || !res.isType(" ")) return res;
        return res.getBrother();
    }

    public DTE getNthBrother(int n) {
        DTE result = this;
        for (int i = 0; i < n; i++) {
            result = result.getBrother();
            assert result != null;
        }
        return result;
    }

    public DTE getNthSon(int n) {
        DTE result = getFirstSon();
        assert result != null;
        return result.getNthBrother(n - 1);
    }

    private void setBrother(DTE brother) {
        this.brother = brother;
    }

    public String labelContent() {
        if (label == null) return null;
        return label.getContent();
    }
}
