package ast;

import lexer.Token;
import lexer.TokenType;

public class BuildTriangleNode implements ASTNode {
    private final Token p1, p2, p3;
    private final TokenType type;

    public BuildTriangleNode(TokenType type, Token p1, Token p2, Token p3) {
        this.type = type;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public TokenType getTriangleType() {
        return type;
    }
    public Token getP1() {
        return p1;
    }
    public Token getP2() {
        return p2;
    }
    public Token getP3() {
        return p3;
    }
}
