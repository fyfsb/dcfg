package tree;

import grammar.TreeToken;

public class DTEUtils {
    public static DTE terminal(TokenType type) {
        return new DTE(new TreeToken(type));
    }

    public static DTE getVaD(String type, String name) {
        DTE na = new DTE(new TreeToken(TokenType.Na, name));
        DTE ty = new DTE(new TreeToken(TokenType.Ty, type), null, null, na);

        return new DTE(new TreeToken(TokenType.VaD, null), ty);
    }

    public static DTE getTestProgram() {
        // Type Expression

        // struct { int a; int b }
        DTE struct = terminal(TokenType.STRUCT);


        // VaDS int a; int b
        DTE componentA = getVaD("int", "a");
        DTE strSemiColon = terminal(TokenType.SEMI_COLON);
        DTE componentB = getVaD("int", "b");

        componentA.bro = strSemiColon;

        strSemiColon.bro = new DTE(new TreeToken(TokenType.VaDS), componentB);

        DTE strVaDS = new DTE(new TreeToken(TokenType.VaDS), componentA);

        DTE strLeftCurlyBrace = terminal(TokenType.L_CURLY_BRACE);
        DTE strRightCurlyBrace = terminal(TokenType.R_CURLY_BRACE);

        // building "struct { strVaDS }"
        struct.bro = strLeftCurlyBrace;
        strLeftCurlyBrace.bro = strVaDS;
        strVaDS.bro = strRightCurlyBrace;

        // wrap in TE
        DTE typeExpression = new DTE(new TreeToken(TokenType.TE), struct);

        // connect typedef with TE and Na, wrap in TyD, then in TyDS.
        DTE typedef = terminal(TokenType.TYPEDEF);
        typedef.bro = typeExpression;

        // build Na
        DTE nameLetter = new DTE(new TreeToken(TokenType.Le, "k"));
        typeExpression.bro = new DTE(new TreeToken(TokenType.Na), nameLetter);

        // wrap in TyD
        DTE tyD = new DTE(new TreeToken(TokenType.TyD), typedef);

        // wrap in TyDS
        DTE tyDS = new DTE(new TreeToken(TokenType.TyDS), tyD);


        // Building VaD "k s"
        DTE variableDeclaration = getVaD("k", "s");
        // wrap inside VaDS and connect with TyDS
        DTE vaDS = new DTE(new TreeToken(TokenType.VaDS), variableDeclaration);

        // semi-colon connecting TyDS; VaDS
        DTE semiColon1 = terminal(TokenType.SEMI_COLON);
        tyDS.bro = semiColon1;
        semiColon1.bro = vaDS;

        // Building FuD
        DTE returnType = new DTE(new TreeToken(TokenType.Ty, "int"));
        DTE funName = new DTE(new TreeToken(TokenType.Na), new DTE(new TreeToken(TokenType.Le, "f")));

        returnType.bro = funName;

        DTE leftParen = terminal(TokenType.L_PAREN);
        DTE rightParen = terminal(TokenType.R_PAREN);

        funName.bro = leftParen;
        leftParen.bro = rightParen;

        DTE funLeftCurlyBrace = terminal(TokenType.L_CURLY_BRACE);
        DTE funRightCurlyBrace = terminal(TokenType.R_CURLY_BRACE);

        rightParen.bro = funLeftCurlyBrace;

        // Building body
        DTE body = new DTE(new TreeToken(TokenType.Body));
        funLeftCurlyBrace.bro = body;
        body.bro = funRightCurlyBrace;

        // building StS of body, then setting as fson
        // StS -> St -> s.b = 1

        // starting from "1": E -> T -> F -> C -> DiS -> Di -> 1
        DTE one = new DTE(new TreeToken(TokenType.Di, "1"));
        DTE diS = new DTE(new TreeToken(TokenType.DiS), one);
        DTE c = new DTE(new TreeToken(TokenType.C), diS);
        DTE f = new DTE(new TreeToken(TokenType.T), c);
        DTE t = new DTE(new TreeToken(TokenType.F), f);
        DTE e = new DTE(new TreeToken(TokenType.E), t);

        // left hand side <id> -> id.Na ... -> s.b
        DTE structDot = terminal(TokenType.STRUCT_DOT);

        // Na -> Le -> b
        DTE strCompB = new DTE(new TreeToken(TokenType.Na), new DTE(new TreeToken(TokenType.Le, "b")));

        // id -> Na -> Le -> s
        DTE structNameId = new DTE(new TreeToken(TokenType.Id), new DTE(new TreeToken(TokenType.Na), new DTE(new TreeToken(TokenType.Le, "s"))));

        structNameId.bro = structDot;
        structDot.bro = strCompB;

        // wrap in <id>
        DTE leftHandSide = new DTE(new TreeToken(TokenType.Id), structNameId);

        DTE assignmentEquality = terminal(TokenType.EQ);

        // connect <id> = <E>
        leftHandSide.bro = assignmentEquality;
        assignmentEquality.bro = e;

        // wrap in St
        DTE st = new DTE(new TreeToken(TokenType.St), leftHandSide);

        // wrap in StS
        DTE stS = new DTE(new TreeToken(TokenType.StS), st);

        // set as a fson of <body>
        body.fson = stS;

        // building FuD
        DTE fuD = new DTE(new TreeToken(TokenType.FuD), returnType);

        // wrapping in FuDS
        DTE fuDS = new DTE(new TreeToken(TokenType.FuDS), fuD);

        // connecting VaDS ; FuDS
        DTE semiColon2 = terminal(TokenType.SEMI_COLON);
        vaDS.bro = semiColon2;
        semiColon2.bro = fuDS;

        // building a program
        return new DTE(new TreeToken(TokenType.Prog), tyDS);
    }
}
