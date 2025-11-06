import java.util.Map;

public class KeywordTable {
    private static final Map<String, String> LEXEME_TO_TOKEN_TYPE = Map.ofEntries(
            Map.entry("рівнобедрений", "KEYWORD_ISOSCELES"),
            Map.entry("прямокутний", "KEYWORD_RIGHT"),
            Map.entry("побудувати_трикутник", "KEYWORD_BUILD_TRIANGLE"),
            Map.entry("побудувати_висоту", "KEYWORD_BUILD_HEIGHT"),
            Map.entry("побудувати_медіану", "KEYWORD_BUILD_MEDIAN"),
            Map.entry("побудувати_бісектрису", "KEYWORD_BUILD_BISECTOR")
    );

    public static String classify(String lexeme) {
        return LEXEME_TO_TOKEN_TYPE.getOrDefault(lexeme, "IDENTIFIER");
    }
}
