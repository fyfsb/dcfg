package util;

import model.VarType;
import tree.DTE;

import java.util.Arrays;

public class TypeUtils {
    public static void checkTokenType(DTE dte, String expected) {
        if (!dte.isType(expected)) {
            throw new IllegalArgumentException("Expected " + expected + ", got " + dte.getLabel().getContent());
        }
    }

    public static void checkTokenType(DTE dte, String... expected) {
        boolean any = Arrays.stream(expected).anyMatch(dte::isType);
        if (!any) {
            throw new IllegalArgumentException("Expected any of " + Arrays.toString(expected) + ", got " + dte.getLabel().getContent());
        }
    }

    public static void checkSameTypes(VarType... expressions) {
        if (expressions.length <= 1) return;
        for (int i = 1; i < expressions.length; i++) {
            if (expressions[i] != expressions[i - 1]) {
                throw new IllegalArgumentException("Types are not same: " + expressions[i].name + ", " + expressions[i - 1].name);
            }
        }
    }

}
