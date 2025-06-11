package visitor;

import syntaxtree.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Instruction {
    public Node instructionNode;
    Set<String> inSet;
    Set<String> outSet;

    public Instruction(Node instruction) {
        instructionNode = instruction;

        inSet = new HashSet<>();
        outSet = new HashSet<>();
    }

    public Set<String> getInSet() {
        if (instructionNode == null) {
            return new HashSet<>();
        }
        return inSet;
    }

    public Set<String> getOutSet() {
        if (instructionNode == null) {
            return new HashSet<>();
        }
        return outSet;
    }

    // Add a transfer method for constant propagation.
    public void transfer(Map<String, LatticeValue> env) {
        TransferHelper.transferInstruction(this, env);
    }

    public Node getNode() {
        return instructionNode;
    }
}
