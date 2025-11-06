import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class LexicalAnalyzer {
    private enum State {
        START,
        POINT,
        DEF_IDENTIFIER,
        UKRAINIAN_IDENTIFIER,
        INTEGER,
        FLOAT,
        COMMENT,
        SINGLE_LINE_COMMENT,
        MULTI_LINE_COMMENT,
        OPERATOR,
        BRACKET,
        HASH,
        SEMICOLON,
        ERROR
    }

    private final static int INIT_LINE = 1;
    private final static int INIT_COLUMN = 1;

    private int startLine = 0;
    private int startColumn = 0;
    private int line = INIT_LINE;
    private int column = INIT_COLUMN;
    private final PushbackReader reader;
    private State state = State.START;
    private final StringBuilder token = new StringBuilder();
    private boolean unreaded = false;
    private boolean multiLineCommentEnd = false;

    public LexicalAnalyzer(String filepath) throws FileNotFoundException {
        reader = new PushbackReader(new FileReader(filepath));
    }

    public void printAnalysis() {
        Token t;
        try {
            while ((t = getNextToken()) != null) {
                System.out.println(t);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Token getNextToken() throws IOException {
        token.setLength(0);
        state = State.START;

        int c = 0;

        while(true) {
            c = reader.read();
            char ch = (char) c; //for debugging purposes
            if(ch == ' '){
                int debug = 0;
            }

            //handle end of file
            if (c == -1) {
                if (state == State.START) {
                    return null;
                } else {
                    return formToken();
                }
            }

            // normalize CR/CRLF
            if (c == '\r') {
                int next = reader.read();
                if (next != '\n') {
                    reader.unread(next);
                }
                c = '\n';
            }

            Token t = switch (state) {
                case START -> handleCharForSTART(c);
                case POINT -> handleOneCharSeparatedAfter(c);
                case HASH, SEMICOLON -> handleOneSeparatorChar(c);
                case UKRAINIAN_IDENTIFIER -> handleCharForUKRAINIAN_IDENTIFIER(c);
                case DEF_IDENTIFIER -> handleCharForDEF_IDENTIFIER(c);
                case INTEGER -> handleCharForINTEGER(c);
                case FLOAT -> handleCharForFLOAT(c);
                case COMMENT -> handleCharForCOMMENT(c);
                case SINGLE_LINE_COMMENT -> handleCharForSINGLE_LINE_COMMENT(c);
                case MULTI_LINE_COMMENT -> handleCharForMULTI_LINE_COMMENT(c);
                case OPERATOR -> handleCharForOPERATOR(c);
                case BRACKET -> handleCharForBRACKET(c);
                case ERROR -> handleCharForERROR(c);
            };

            //update coordinates
            if (!unreaded) {
                if (c == '\n') {
                    line++;
                    column = INIT_COLUMN;
                } else if ((Transliterator.INSTANCE.getClasses(c) & Transliterator.INVISIBLE) == 0){
                    column++;
                }
            }
            unreaded = false;

            if(t != null)
                return t;
        }

    }

    private Token handleCharForBRACKET(int c) {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.BRACKET) != 0) {
            token.append(ch);
            return formTokenNextColEnd();
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForUKRAINIAN_IDENTIFIER(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.UKRAINIAN_IDENTIFIER) != 0) {
            token.append(ch);
        }
        else if ((cls & Transliterator.SEPARATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            String lexeme = KeywordTable.classify(token.toString());
            if (lexeme.equals("IDENTIFIER")) {
                return formToken();
            } else {
                return formToken(lexeme);
            }
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForDEF_IDENTIFIER(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.DEF_IDENTIFIER) != 0) {
            token.append(ch);
        }
        else if ((cls & Transliterator.DIGIT) != 0) {
            token.append(ch);
        }
        else if ((cls & Transliterator.SEPARATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            String lexeme = KeywordTable.classify(token.toString());
            if (lexeme.equals("IDENTIFIER")) {
                return formToken();
            } else {
                return formToken(lexeme);
            }
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForMULTI_LINE_COMMENT(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.STAR) != 0) {
            int next = reader.read();
            if (((Transliterator.INSTANCE.getClasses(next) & Transliterator.SLASH) != 0)) {
                multiLineCommentEnd = true;
            }
            reader.unread(next);
            token.append(ch);
        }
        else if (multiLineCommentEnd && (cls & Transliterator.SLASH) != 0){
            multiLineCommentEnd = false;
            token.append(ch);
            return formTokenNextColEnd();
        }
        else {
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForSINGLE_LINE_COMMENT(int c) {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.LINE_FEED) == 0) {
            token.append(ch);
        }
        else {
            return formToken();
        }

        return null;
    }

    private Token handleCharForOPERATOR(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.SLASH) != 0) {
            int next = reader.read();
            if (((Transliterator.INSTANCE.getClasses(next) & Transliterator.COMMENT) != 0)) {
                state = State.COMMENT;
            }
            else {
                reader.unread(next);
                token.append(ch);
                return formToken();
            }
            reader.unread(next);
            token.append(ch);
        }
        else {
            token.append(ch);
            return formTokenNextColEnd();
        }

        return null;
    }

    private Token handleCharForCOMMENT(int c) {
        //when first time here
        //we have a slash at the beginning
        //and / or * right now as the second symbol
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.SLASH) != 0) {
            state = State.SINGLE_LINE_COMMENT;
            token.append(ch);
        }
        else if ((cls & Transliterator.STAR) != 0){
            state = State.MULTI_LINE_COMMENT;
            token.append(ch);
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForERROR(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.SEPARATOR) == 0) {
            token.append(ch);
        }
        else {
            unreaded = true;
            reader.unread(c);
            return formToken();
        }

        return null;
    }

    private Token handleCharForFLOAT(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.DIGIT) != 0) {
            token.append(ch);
        }
        else if ((cls & Transliterator.DOT) != 0) {
            state = State.ERROR;
            token.append(ch);
        }
        else if ((cls & Transliterator.SEPARATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            return formToken();
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleCharForINTEGER(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.DIGIT) != 0) {
            token.append(ch);
        }
        else if ((cls & Transliterator.DOT) != 0) { //make unread and pass to float?
            int next = reader.read();
            if (next == -1 || ((Transliterator.INSTANCE.getClasses(next) & Transliterator.DIGIT) == 0)) {
                state = State.ERROR;
            }
            else {
                state = State.FLOAT;
            }
            reader.unread(next);
            token.append(ch);
        }
        else if ((cls & Transliterator.SEPARATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            return formToken();
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleOneCharSeparatedAfter(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.SEPARATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            return formToken();
        }
        else {
            state = State.ERROR;
            token.append(ch);
        }

        return null;
    }

    private Token handleOneSeparatorChar(int c) throws IOException {
        unreaded = true;
        reader.unread(c);
        return formToken();
    }

    private Token handleCharForSTART(int c) throws IOException {
        int cls = Transliterator.INSTANCE.getClasses(c);
        char ch = (char) c;

        if ((cls & Transliterator.LATIN_UPPER) != 0) {
            state = State.POINT;
            token.append(ch);
        }
        else if ((cls & Transliterator.HASH) != 0) {
            state = State.HASH;
            token.append(ch);
        }
        else if ((cls & Transliterator.SEMICOLON) != 0) {
            state = State.SEMICOLON;
            token.append(ch);
        }
        else if ((cls & Transliterator.UKRAINIAN_IDENTIFIER) != 0) {
            state = State.UKRAINIAN_IDENTIFIER;
            token.append(ch);
        }
        else if ((cls & Transliterator.DEF_IDENTIFIER) != 0) {
            state = State.DEF_IDENTIFIER;
            token.append(ch);
        }
        else if ((cls & Transliterator.DIGIT) != 0) {
            state = State.INTEGER;
            token.append(ch);
        }
        else if ((cls & Transliterator.DOT) != 0) {
            state = State.FLOAT;
            token.append(ch);
        }
        else if ((cls & Transliterator.OPERATOR) != 0) {
            unreaded = true;
            reader.unread(c);
            state = State.OPERATOR;
        }
        else if ((cls & Transliterator.BRACKET) != 0) {
            unreaded = true;
            reader.unread(c);
            state = State.BRACKET;
        }
        else if (cls == 0) {
            state = State.ERROR;
            token.append(ch);
        }

        if (state != State.START) {
            startLine = line;
            startColumn = column;
        }
        return null;
    }

    // for cases when we form token when meet separator
    private Token formToken() {
        return new Token(state.toString(), token.toString(), startLine, startColumn, line, column);
    }

    // for cases when we form token right after appending the last symbol
    private Token formTokenNextColEnd() {
        return new Token(state.toString(), token.toString(), startLine, startColumn, line, column+1);
    }

    private Token formToken(String lexeme) {
        return new Token(lexeme, token.toString(), startLine, startColumn, line, column);
    }

}
