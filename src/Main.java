import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            LexicalAnalyzer la = new LexicalAnalyzer("test1.txt");
//            la.printAnalysis();
            Parser parser = new Parser(la);
            parser.parse();
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}