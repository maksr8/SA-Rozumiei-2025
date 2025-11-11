import java.io.IOException;

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

    public void parse() throws Exception {
        parseSentenceList();
        match(TokenType.HASH);
        System.out.println("Syntax analysis completed successfully.");
    }

    private void parseSentenceList() throws Exception {
        while (currentToken != null && !(currentToken.getType() == TokenType.HASH)) {
            parseSentence();
            match(TokenType.SEMICOLON);
        }
    }

    private void parseSentence() throws Exception {
        if (currentToken == null) {
            throw new Exception("Syntax Error: Unexpected eof, expected a sentence.");
        }
        TokenType type = currentToken.getType();
        if (type == TokenType.KEYWORD_BUILD_TRIANGLE) {
            parseTriangle();
        } else if (type == TokenType.KEYWORD_BUILD_HEIGHT ||
                type == TokenType.KEYWORD_BUILD_MEDIAN ||
                type == TokenType.KEYWORD_BUILD_BISECTOR) {
            parseSegment();
        } else {
            throw new Exception("Syntax Error: Unexpected token " + type +
                    " at " + currentToken.getStartLine() + ":" + currentToken.getStartColumn() +
                    ". Expected a command.");
        }
    }

    private void parseTriangle() throws Exception {
        match(TokenType.KEYWORD_BUILD_TRIANGLE);
        System.out.println("Parsing: Found BUILD_TRIANGLE command.");

        if (currentToken.getType() == TokenType.KEYWORD_ISOSCELES ||
                currentToken.getType() == TokenType.KEYWORD_RIGHT) {
            parseTriangleType();
        }

        parseTriangleName();
    }

    private void parseTriangleType() throws Exception {
        if (currentToken.getType() == TokenType.KEYWORD_ISOSCELES) {
            match(TokenType.KEYWORD_ISOSCELES);
            System.out.println("Parsing: Found modifier ISOSCELES.");
        } else {
            match(TokenType.KEYWORD_RIGHT);
            System.out.println("Parsing: Found modifier RIGHT.");
        }
    }

    private void parseTriangleName() throws Exception {
        match(TokenType.POINT);
        match(TokenType.POINT);
        match(TokenType.POINT);
        System.out.println("Parsing: Found triangle name (3 points).");
    }

    private void parseSegment() throws Exception {
        parseSegmentType();
        parseSegmentName();
    }

    private void parseSegmentType() throws Exception {
        TokenType type = currentToken.getType();

        switch (type) {
            case TokenType.KEYWORD_BUILD_HEIGHT:
                match(TokenType.KEYWORD_BUILD_HEIGHT);
                System.out.println("Parsing: Found BUILD_HEIGHT command.");
                break;
            case TokenType.KEYWORD_BUILD_MEDIAN:
                match(TokenType.KEYWORD_BUILD_MEDIAN);
                System.out.println("Parsing: Found BUILD_MEDIAN command.");
                break;
            case TokenType.KEYWORD_BUILD_BISECTOR:
                match(TokenType.KEYWORD_BUILD_BISECTOR);
                System.out.println("Parsing: Found BUILD_BISECTOR command.");
                break;
            default:
                throw new Exception("Internal Parser Error: Unexpected segment type " + type);
        }
    }

    private void parseSegmentName() throws Exception {
        match(TokenType.POINT);
        match(TokenType.POINT);
        System.out.println("Parsing: Found segment name (2 points).");
    }
}
