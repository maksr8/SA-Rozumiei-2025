package lexer;

import java.util.HashMap;
import java.util.Map;

public enum Transliterator {
    INSTANCE;

    // Map from character code (int) to bitmask of classes
    private final Map<Integer, Integer> classes = new HashMap<>();

    public static final int LATIN_UPPER = 1;
    public static final int LATIN_LOWER = 1 << 1;
    public static final int UNDERSCORE = 1 << 2;
    public static final int DEF_IDENTIFIER = LATIN_UPPER | LATIN_LOWER | UNDERSCORE; //0b10 = 0b0000_0001 = 0b{30 zeros}10
    public static final int DIGIT = 1 << 3;
    public static final int DOT = 1 << 4;
    public static final int LINE_FEED = 1 << 5;
    public static final int CARRIAGE_RETURN = 1 << 6;
    public static final int WHITESPACE = (1 << 7);
    public static final int INVISIBLE = LINE_FEED | CARRIAGE_RETURN;
    public static final int BRACKET = 1 << 8;
    public static final int SLASH = 1 << 9;
    public static final int STAR = 1 << 10;
    public static final int OPERATOR = (1 << 11) | STAR | SLASH;
    public static final int HASH = 1 << 12;
    public static final int SEMICOLON = 1 << 13;
    public static final int UKRAINIAN_LOWER = 1 << 14;
    public static final int UKRAINIAN_UPPER = 1 << 15;
    public static final int UKRAINIAN_IDENTIFIER = UKRAINIAN_LOWER | UKRAINIAN_UPPER | UNDERSCORE;
    public static final int COMMENT = SLASH | STAR;
    public static final int SEPARATOR = WHITESPACE | OPERATOR | BRACKET | SEMICOLON | HASH;


    Transliterator() {
        for (char c = 'A'; c <= 'Z'; c++)
            addClass(c, LATIN_UPPER);

        for (char c = 'a'; c <= 'z'; c++)
            addClass(c, LATIN_LOWER);

        addClass('_', UNDERSCORE);

        for (char c = '0'; c <= '9'; c++)
            addClass(c, DIGIT);

        addClass('.', DOT);

        addClass('\n', LINE_FEED);
        addClass('\r', CARRIAGE_RETURN);

        addClass(' ', WHITESPACE);
        addClass('\t', WHITESPACE);

        addClass('(', BRACKET);
        addClass(')', BRACKET);
        addClass('{', BRACKET);
        addClass('}', BRACKET);

        addClass('/', SLASH);
        addClass('*', STAR);

        addClass('+', OPERATOR);
        addClass('-', OPERATOR);
        addClass('=', OPERATOR);

        addClass('#', HASH);
        addClass(';', SEMICOLON);

        for (char c : "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя".toCharArray())
            addClass(c, UKRAINIAN_LOWER);

        for (char c : "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ".toCharArray())
            addClass(c, UKRAINIAN_UPPER);
    }

    private void addClass(char c, int cls) {
        classes.merge((int) c, cls, (oldCls, newCls) -> oldCls | newCls);
    }

    public int getClasses(int c) {
        return classes.getOrDefault(c, 0);
    }
}