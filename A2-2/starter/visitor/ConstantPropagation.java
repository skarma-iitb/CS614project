package visitor;

import syntaxtree.*;
import java.util.*;

public class ConstantPropagation {
    private Queue<BB> worklist;
    private Set<BB> processed;
    private Map<BB, Map<String, Integer>> blockInStates;
    private Map<BB, Map<String, Integer>> blockOutStates;

    public ConstantPropagation() {
        this.worklist = new LinkedList<>();
        this.processed = new HashSet<>();
        this.blockInStates = new HashMap<>();
        this.blockOutStates = new HashMap<>();
    }

    public void analyze(BB startBlock) {
        // Initialize worklist with start block
        worklist.add(startBlock);

        // Initialize all blocks with empty maps
        initializeStates(startBlock);

        // Process blocks until fixed point
        while (!worklist.isEmpty()) {
            BB block = worklist.poll();
            processed.add(block);

            // Save previous out state for comparison
            Map<String, Integer> prevOutState = new HashMap<>(blockOutStates.getOrDefault(block, new HashMap<>()));

            // Meet operation: merge states from predecessors
            if (!block.incomingEdges.isEmpty()) {
                Map<String, Integer> mergedState = meetOperation(block);
                blockInStates.put(block, mergedState);
            }

            // Process instructions in the block
            Map<String, Integer> currentState = new HashMap<>(blockInStates.getOrDefault(block, new HashMap<>()));

            // Process each instruction
            for (Instruction inst : block.instructions) {
                if (inst.instructionNode != null) {
                    processInstruction(inst.instructionNode, currentState);
                }
            }

            // Update out state
            blockOutStates.put(block, currentState);

            // If state changed, add successors to worklist
            if (!currentState.equals(prevOutState)) {
                for (BB succ : block.outgoingEdges) {
                    if (!worklist.contains(succ)) {
                        worklist.add(succ);
                        processed.remove(succ);
                    }
                }
            }
        }
    }

    private void initializeStates(BB startBlock) {
        Set<BB> visited = new HashSet<>();
        Stack<BB> stack = new Stack<>();
        stack.push(startBlock);

        while (!stack.isEmpty()) {
            BB block = stack.pop();
            if (visited.contains(block))
                continue;
            visited.add(block);

            blockInStates.put(block, new HashMap<>());
            blockOutStates.put(block, new HashMap<>());

            for (BB succ : block.outgoingEdges) {
                stack.push(succ);
            }
        }
    }

    private Map<String, Integer> meetOperation(BB block) {
        Map<String, Integer> result = new HashMap<>();
        boolean first = true;

        for (BB pred : block.incomingEdges) {
            Map<String, Integer> predState = blockOutStates.getOrDefault(pred, new HashMap<>());
            if (first) {
                result.putAll(predState);
                first = false;
            } else {
                // Keep only variables that have the same constant value in all predecessors
                result.keySet().retainAll(predState.keySet());
                Iterator<Map.Entry<String, Integer>> it = result.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> entry = it.next();
                    Integer predValue = predState.get(entry.getKey());
                    if (!entry.getValue().equals(predValue)) {
                        it.remove();
                    }
                }
            }
        }
        return result;
    }

    private void processInstruction(Node inst, Map<String, Integer> state) {
        if (inst instanceof AssignmentStatement) {
            AssignmentStatement assign = (AssignmentStatement) inst;
            String varName = assign.f0.f0.tokenImage;
            Integer value = evaluateExpression(assign.f2, state);
            if (value != null) {
                state.put(varName, value);
            } else {
                state.remove(varName);
            }
        }
        // Add other instruction types as needed
    }

    private Integer evaluateExpression(Expression expr, Map<String, Integer> state) {
        NodeChoice choice = expr.f0;

        if (choice.choice instanceof PrimaryExpression) {
            return evaluatePrimaryExpression((PrimaryExpression) choice.choice, state);
        } else if (choice.choice instanceof PlusExpression) {
            PlusExpression plus = (PlusExpression) choice.choice;
            Integer left = evaluateIdentifier(plus.f0, state);
            Integer right = evaluateIdentifier(plus.f2, state);
            if (left != null && right != null) {
                return left + right;
            }
        } else if (choice.choice instanceof DivExpression) {
            PlusExpression plus = (PlusExpression) choice.choice;
            Integer left = evaluateIdentifier(plus.f0, state);
            Integer right = evaluateIdentifier(plus.f2, state);
            if (left != null && right != null) {
                return left / right;
            }
        } else if (choice.choice instanceof MinusExpression) {
            PlusExpression plus = (PlusExpression) choice.choice;
            Integer left = evaluateIdentifier(plus.f0, state);
            Integer right = evaluateIdentifier(plus.f2, state);
            if (left != null && right != null) {
                return left - right;
            }
        } else if (choice.choice instanceof TimesExpression) {
            PlusExpression plus = (PlusExpression) choice.choice;
            Integer left = evaluateIdentifier(plus.f0, state);
            Integer right = evaluateIdentifier(plus.f2, state);
            if (left != null && right != null) {
                return left * right;
            }
        }
        return null;
    }

    private Integer evaluatePrimaryExpression(PrimaryExpression expr, Map<String, Integer> state) {
        NodeChoice choice = expr.f0;
        if (choice.choice instanceof IntegerLiteral) {
            return Integer.parseInt(((IntegerLiteral) choice.choice).f0.tokenImage);
        } else if (choice.choice instanceof Identifier) {
            String varName = ((Identifier) choice.choice).f0.tokenImage;
            return state.get(varName);
        }
        return null;
    }

    private Integer evaluateIdentifier(Identifier id, Map<String, Integer> state) {
        String name = id.f0.tokenImage;
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return state.get(name);
        }
    }

    public void printResults() {
        System.out.println("Constant Propagation Results:");
        for (Map.Entry<BB, Map<String, Integer>> entry : blockOutStates.entrySet()) {
            System.out.println("\nBlock " + entry.getKey().name + ":");
            Map<String, Integer> constants = entry.getValue();
            if (constants.isEmpty()) {
                System.out.println("  No constants");
            } else {
                for (Map.Entry<String, Integer> constEntry : constants.entrySet()) {
                    System.out.printf("  %s = %d%n", constEntry.getKey(), constEntry.getValue());
                }
            }
        }
    }
}