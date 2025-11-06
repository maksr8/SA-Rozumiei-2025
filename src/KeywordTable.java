import java.util.Set;

public class KeywordTable {
    private static final Set<String> KEYWORDS = Set.of(
            "рівнобедрений",
            "прямокутний"
    );
    private static final Set<String> BUILTIN_FUNCTIONS = Set.of(
            "побудувати_трикутник",
            "побудувати_висоту",
            "побудувати_медіану",
            "побудувати_бісектрису"
    );

    public static String classify(String lexeme) {
        if (KEYWORDS.contains(lexeme)) return "KEYWORD";
        if (BUILTIN_FUNCTIONS.contains(lexeme)) return "BUILTIN_FUNC";
        return "DEF_IDENTIFIER";
    }
}
