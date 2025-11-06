import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        LexicalAnalyzer la = new LexicalAnalyzer("test1.txt");
        la.printAnalysis();
    }
}