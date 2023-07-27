package tree;


import grammar.TreeToken;
import org.junit.Test;

import java.util.List;

public class DTETest {
    private final DTE namePadC = new DTE(new TreeToken(TokenType.Na, "c"), null, null, null);
    private final DTE typePadC = new DTE(new TreeToken(TokenType.Ty, "int"), null, null, namePadC);
    private final DTE padC = new DTE(new TreeToken(TokenType.VaD, null), null, typePadC, null);
    // to this corresponds to the VaD of parameter `char c`

    // `bool b`
    private final DTE namePadB = new DTE(new TreeToken(TokenType.Na, "b"), null, null, null);
    private final DTE typePadB = new DTE(new TreeToken(TokenType.Ty, "bool"), null, null, namePadB);
    private final DTE padB = new DTE(new TreeToken(TokenType.VaD, null), null, typePadB,
            new DTE(new TreeToken(TokenType.PaDS, null), null, padC, null)
    );

    private final DTE namePadA = new DTE(new TreeToken(TokenType.Na, "a"), null, null, null);
    private final DTE typePadA = new DTE(new TreeToken(TokenType.Ty, "char"), null, null, namePadA);
    private final DTE padA = new DTE(new TreeToken(TokenType.VaD, null), null, typePadA,
            new DTE(new TreeToken(TokenType.PaDS, null), null, padB, null)
    );

    private final DTE pads = new DTE(
            new TreeToken(TokenType.PaDS, null),
            null,
            padA,
            null
    );

    @Test
    public void getChildrenSize() {
        assert pads.getChildrenSize() == 3;
    }

    @Test
    public void getFlattenedSequence() {
        List<DTE> actual = List.of(padA, padB, padC);
        assert pads.getFlattenedSequence().equals(actual);
    }

    @Test
    public void getBorderWord() {
        String actual = "charaboolbintc";
        assert actual.equals(pads.getBorderWord());
    }
}