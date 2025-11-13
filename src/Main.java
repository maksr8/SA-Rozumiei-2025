import ast.ProgramNode;
import interpreter.Interpreter;
import lexer.LexicalAnalyzer;
import parser.Parser;
import ui.DrawingPanel;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        try {
            LexicalAnalyzer la = new LexicalAnalyzer("test1.txt");
            Parser parser = new Parser(la);
            ProgramNode ast = parser.parse();
            SwingUtilities.invokeLater(() -> {
                try {
                    DrawingPanel panel = new DrawingPanel();
                    Interpreter interpreter = new Interpreter(panel);
                    interpreter.execute(ast);
                } catch (Exception e) {
                    System.err.println("RUNTIME ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("COMPILE ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}