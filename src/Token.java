public class Token {
    private final String type;
    private final String value;
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    public Token(String type, String value, int startLine, int startColumn, int endLine, int endColumn) {
        this.type = type;
        this.value = value;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    @Override
    public String toString() {
        return '[' + type + " \"" + value + "\" (" + startLine + " " + startColumn + ") (" + endLine + " " + endColumn + ")]";
    }
}
