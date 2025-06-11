package visitor;

import syntaxtree.*;

public class AnnotatedNode {
    private Node node;
    private BB basicBlock;

    public AnnotatedNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setBasicBlock(BB bb) {
        this.basicBlock = bb;
    }

    public BB getBasicBlock() {
        return basicBlock;
    }
}