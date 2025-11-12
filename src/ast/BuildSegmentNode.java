package ast;

import lexer.Token;
import lexer.TokenType;

public class BuildSegmentNode implements ASTNode {

    private final TokenType segmentType;
    private final Token p1, p2;

    public BuildSegmentNode(TokenType segmentType, Token p1, Token p2) {
        this.segmentType = segmentType;
        this.p1 = p1;
        this.p2 = p2;
    }

    public TokenType getSegmentType() {
        return segmentType;
    }
    public Token getP1() {
        return p1;
    }
    public Token getP2() {
        return p2;
    }
}
