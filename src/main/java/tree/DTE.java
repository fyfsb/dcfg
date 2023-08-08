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

    public DTE(TreeToken token, DTE fson) {
        this.token = token;
        this.fson = fson;
    }

    public DTE(TreeToken token) {
        this.token = token;
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

    // XS -> X_1 ; (XS -> X_2 ; (..))
    // flattened = [X_1, X_2, ... X_n]
    public List<DTE> getFlattenedSequence() {
        if (token == null) return null;
        List<DTE> result = new LinkedList<>();

        // XS -> XS; X
        if (fson.bro == null) {
            result.add(fson);
        } else {
            result.addAll(fson.getFlattenedSequence());
            result.add(fson.bro.bro);
        }

        return result;
    }

    public String getBorderWord() {
        if (fson == null) return token.value;
        StringBuilder sb = new StringBuilder();
        for (DTE dte = fson; dte != null; dte = dte.bro) {
            sb.append(dte.getBorderWord());
        }
        return sb.toString();
    }

    public List<List<String>> extractComponentPairs() {
        List<DTE> flattened = getFlattenedSequence();
        List<List<String>> result = new LinkedList<>();
        for (DTE dte : flattened) {
            String type = dte.fson.token.value;
            String name = dte.fson.bro.token.value;

            assert type != null;
            assert name != null;
            result.add(List.of(type, name));
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
