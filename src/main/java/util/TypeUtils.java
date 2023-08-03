package util;

import tree.DTE;
import tree.TokenType;

import java.util.Arrays;

public class TypeUtils {
    public static void checkTokenType(DTE dte, TokenType expected) {
        if (dte.token.type != expected) {
            throw new IllegalArgumentException("Expected " + expected + ", got " + dte.token.type);
        }
    }

    public static void checkTokenType(DTE dte, TokenType... expected) {
        boolean any = Arrays.stream(expected).anyMatch(exp -> dte.token.type == exp);
        if (!any) {
            throw new IllegalArgumentException("Expected any of " + Arrays.toString(expected) + ", got " + dte.token.type);
        }
    }
}
