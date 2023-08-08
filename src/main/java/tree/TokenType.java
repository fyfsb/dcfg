package tree;

public enum TokenType {
    // Non-terminals
    Atom,
    BC,
    BE,
    BF,
    BT,
    Body,
    C,
    CC,
    Di,
    DiLe,
    DiLeS,
    DiS,
    E,
    F,
    FuD,
    FuDS,
    Id,
    Le,
    Na,
    Pa,
    PaDS,
    PaS,
    Prog,
    RSt,
    St,
    StS,
    T,
    TE,
    Ty,
    TyD,
    TyDS,
    VaD,
    VaDS,

    // Terminals
    // brackets
    L_BRACKET, R_BRACKET, L_PAREN, R_PAREN, L_CURLY_BRACE, R_CURLY_BRACE,

    // reserved words
    TRUE, FALSE, IF, ELSE, WHILE, RETURN,

    // binary operators
    EQ, DOUBLE_EQ, LESS, GREATER, LEQ, GEQ, BINARY_MINUS, MUL, DIV, AND, OR, ADD,

    // unary operators
    UNARY_MINUS, POINTER_DEREF, STRUCT_DOT, ADDRESS_OF, NOT,
    // types
    TYPEDEF, INT, BOOL, CHAR, UINT, STRUCT,

    SEMI_COLON
}
