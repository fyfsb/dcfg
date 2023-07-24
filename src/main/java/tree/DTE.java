package tree;

import grammar.TreeToken;

import java.util.LinkedList;
import java.util.List;

public class DTE {
    public TreeToken token;
    public DTE father;
    public DTE fson;
    public DTE bro;

    public DTE(TreeToken token, DTE father, DTE fson, DTE bro) {
        this.token = token;
        this.father = father;
        this.fson = fson;
        this.bro = bro;
    }

    public int getChildrenSize() {
        int result = 0;
        var curr = this;
        while (curr != null && curr.fson != null) {
            result++;
            curr = curr.fson.bro;
        }
        return result;
    }

    public List<DTE> getFlattenedSequence() {
        if (token == null) return null;
        List<DTE> result = new LinkedList<>();

        for (DTE iter = this; iter != null; iter = iter.fson.bro) {
            result.add(iter.fson);
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DTE(token=")
                .append(token)
                .append(", father=").append(father)
                .append(", fson=").append(fson)
                .append(", bro=").append(bro)
                .append(")");
        return sb.toString();
    }
}
