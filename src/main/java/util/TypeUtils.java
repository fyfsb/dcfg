package util;

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
}
