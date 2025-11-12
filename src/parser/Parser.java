package parser;

import ast.ASTNode;
import ast.BuildSegmentNode;
import ast.BuildTriangleNode;
import ast.ProgramNode;
import lexer.LexicalAnalyzer;
import lexer.Token;
import lexer.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final LexicalAnalyzer lexer;
    private Token currentToken;

    public Parser(LexicalAnalyzer lexer) throws IOException {
        this.lexer = lexer;
        consume();
    }

    private void consume() throws IOException {
        this.currentToken = lexer.getNextToken();
    }

    private void match(TokenType expectedType) throws Exception {
        if (currentToken == null) {
            throw new Exception("Unexpected eof, expected token: " + expectedType);
        }

        if (currentToken.getType() == expectedType) {
            consume();
        } else {
            throw new Exception("Syntax error, expected token: " + expectedType +
                    ", current token " + currentToken.getType() +
                    " (" + currentToken.getValue() + ") at "
                    + currentToken.getStartLine() + ":" + currentToken.getStartColumn());
        }
    }

    public ProgramNode parse() throws Exception {
        ProgramNode program = parseSentenceList();
        match(TokenType.HASH);
        System.out.println("Syntax analysis completed successfully.");
        return program;
    }

    private ProgramNode parseSentenceList() throws Exception {
        List<ASTNode> sentences = new ArrayList<>();
        while (currentToken != null && !(currentToken.getType() == TokenType.HASH)) {
            sentences.add(parseSentence());
            match(TokenType.SEMICOLON);
        }
        return new ProgramNode(sentences);
    }

    private ASTNode parseSentence() throws Exception {
        if (currentToken == null) {
            throw new Exception("Syntax Error: Unexpected eof, expected a sentence.");
        }
        TokenType type = currentToken.getType();
        if (type == TokenType.KEYWORD_BUILD_TRIANGLE) {
            return parseTriangle();
        } else if (type == TokenType.KEYWORD_BUILD_HEIGHT ||
                type == TokenType.KEYWORD_BUILD_MEDIAN ||
                type == TokenType.KEYWORD_BUILD_BISECTOR) {
            return parseSegment();
        } else {
            throw new Exception("Syntax Error: Unexpected token " + type +
                    " at " + currentToken.getStartLine() + ":" + currentToken.getStartColumn() +
                    ". Expected a command.");
        }
    }

    private BuildTriangleNode parseTriangle() throws Exception {
        match(TokenType.KEYWORD_BUILD_TRIANGLE);
        TokenType type = null;
        if (currentToken.getType() == TokenType.KEYWORD_ISOSCELES ||
                currentToken.getType() == TokenType.KEYWORD_RIGHT) {
            type = parseTriangleType();
        }

        Token p1 = currentToken;
        match(TokenType.POINT);
        Token p2 = currentToken;
        match(TokenType.POINT);
        Token p3 = currentToken;
        match(TokenType.POINT);

        return new BuildTriangleNode(type, p1, p2, p3);
    }

    private TokenType parseTriangleType() throws Exception {
        if (currentToken.getType() == TokenType.KEYWORD_ISOSCELES) {
            match(TokenType.KEYWORD_ISOSCELES);
            return TokenType.KEYWORD_ISOSCELES;
        } else {
            match(TokenType.KEYWORD_RIGHT);
            return TokenType.KEYWORD_RIGHT;
        }
    }

    private BuildSegmentNode parseSegment() throws Exception {
        TokenType segmentType = parseSegmentType();
        Token p1 = currentToken;
        match(TokenType.POINT);
        Token p2 = currentToken;
        match(TokenType.POINT);

        return new BuildSegmentNode(segmentType, p1, p2);
    }

    private TokenType parseSegmentType() throws Exception {
        TokenType type = currentToken.getType();

        switch (type) {
            case TokenType.KEYWORD_BUILD_HEIGHT:
                match(TokenType.KEYWORD_BUILD_HEIGHT);
                break;
            case TokenType.KEYWORD_BUILD_MEDIAN:
                match(TokenType.KEYWORD_BUILD_MEDIAN);
                break;
            case TokenType.KEYWORD_BUILD_BISECTOR:
                match(TokenType.KEYWORD_BUILD_BISECTOR);
                break;
            default:
                throw new Exception("Internal parser.Parser Error: Unexpected segment type " + type);
        }
        return type;
    }
}
