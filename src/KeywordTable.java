import java.util.Map;

public class KeywordTable {
    private static final Map<String, TokenType> LEXEME_TO_TOKEN_TYPE = Map.ofEntries(
            Map.entry("рівнобедрений", TokenType.KEYWORD_ISOSCELES),
            Map.entry("прямокутний", TokenType.KEYWORD_RIGHT),
            Map.entry("побудувати_трикутник", TokenType.KEYWORD_BUILD_TRIANGLE),
            Map.entry("побудувати_висоту", TokenType.KEYWORD_BUILD_HEIGHT),
            Map.entry("побудувати_медіану", TokenType.KEYWORD_BUILD_MEDIAN),
            Map.entry("побудувати_бісектрису", TokenType.KEYWORD_BUILD_BISECTOR)
    );

    public static TokenType classify(String lexeme) {
        return LEXEME_TO_TOKEN_TYPE.getOrDefault(lexeme, TokenType.IDENTIFIER);
    }
}
