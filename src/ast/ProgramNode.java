package ast;

import java.util.List;

public class ProgramNode implements ASTNode {

    private final List<ASTNode> sentences;

    public ProgramNode(List<ASTNode> sentences) {
        this.sentences = sentences;
    }

    public List<ASTNode> getSentences() {
        return sentences;
    }
}
