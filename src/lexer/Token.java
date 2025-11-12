package lexer;

public class Token {
    private final TokenType type;
    private final String value;
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    public Token(TokenType type, String value, int startLine, int startColumn, int endLine, int endColumn) {
        this.type = type;
        this.value = value;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    @Override
    public String toString() {
        return '[' + type.name() + " \"" + value + "\" (" + startLine + " " + startColumn + ") (" + endLine + " " + endColumn + ")]";
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }
}
