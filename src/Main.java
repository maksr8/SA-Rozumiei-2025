import ast.ProgramNode;
import lexer.LexicalAnalyzer;
import parser.Parser;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            LexicalAnalyzer la = new LexicalAnalyzer("test1.txt");
//            la.printAnalysis();
            Parser parser = new Parser(la);
            ProgramNode ast = parser.parse();
            System.out.println("AST created: " + ast.toString());
            System.out.println("Found " + ast.getSentences().size() + " sentences.");
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}