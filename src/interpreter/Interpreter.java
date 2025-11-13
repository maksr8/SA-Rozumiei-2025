package interpreter;

import ast.*;
import lexer.TokenType;
import semantics.SymbolTable;
import ui.DrawingPanel;
import java.util.List;

public class Interpreter {

    private final SymbolTable symbolTable;
    private final DrawingPanel drawingPanel;

    public Interpreter(DrawingPanel panel) {
        this.symbolTable = new SymbolTable();
        this.drawingPanel = panel;
    }

    public void execute(ProgramNode program) throws Exception {
        System.out.println("Interpreter: Execution started.");
        List<ASTNode> sentences = program.getSentences();

        for (ASTNode sentence : sentences) {
            executeSentence(sentence);
        }
        System.out.println("Interpreter: Execution finished.");
    }

    private void executeSentence(ASTNode sentence) throws Exception {
        if (sentence instanceof BuildTriangleNode node) {
            executeTriangle(node);
        } else if (sentence instanceof BuildSegmentNode node) {
            executeSegment(node);
        } else {
            throw new Exception("Interpreter Error: Unknown AST node " + sentence.getClass().getName());
        }
    }

    private void executeTriangle(BuildTriangleNode node) throws Exception {
        String p1Name = node.getP1().getValue();
        String p2Name = node.getP2().getValue();
        String p3Name = node.getP3().getValue();
        TokenType type = node.getTriangleType();

        model.Point[] points = symbolTable.defineTriangle(p1Name, p2Name, p3Name, type);
        model.Point p1 = points[0];
        model.Point p2 = points[1];
        model.Point p3 = points[2];

        drawingPanel.addSegment(p1, p2);
        drawingPanel.addSegment(p2, p3);
        drawingPanel.addSegment(p3, p1);
        drawingPanel.addPoint(p1);
        drawingPanel.addPoint(p2);
        drawingPanel.addPoint(p3);
    }

    private void executeSegment(BuildSegmentNode node) throws Exception {
        String p1Name = node.getP1().getValue();
        String p2Name = node.getP2().getValue();
        TokenType type = node.getSegmentType();

        model.Point[] points = symbolTable.defineSegment(p1Name, p2Name, type);

        if (points == null) {
            return;
        }

        model.Point p1 = points[0];
        model.Point p2 = points[1];

        drawingPanel.addSegment(p1, p2);
        drawingPanel.addPoint(p2);
    }
}