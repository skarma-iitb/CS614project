package visitor;

import java.util.*;

public class newworklist1 {
    // Maps to store the IN and OUT environments for each basic block.
    // These maps store, for each basic block, its IN and OUT environments.
    // The environment is a mapping from variable names to their LatticeValue.
    private Map<BB, Map<String, LatticeValue>> inMap = new HashMap<>();
    private Map<BB, Map<String, LatticeValue>> outMap = new HashMap<>();

    // Run the analysis over all methods in the ProgramCFG.
    // We assume that programCFG.classMethodList is a mapping from class names to
    // sets of method names,
    // and programCFG.methodBBSet maps each method name to its entry basic block.
    public void analyze(ProgramCFG programCFG) {
        // For every class and method in the CFG:
        for (String className : programCFG.classMethodList.keySet()) {
            Set<String> methodList = programCFG.classMethodList.get(className);
            // System.out.println("inside work list finding method " + methodList);
            for (String methodName : methodList) {
                BB entryBB = programCFG.methodBBSet.get(methodName);
                if (entryBB == null)
                    continue;
                Set<BB> allBBs = collectBBs(entryBB);
                for (BB bb : allBBs) {
                    inMap.put(bb, new HashMap<>());
                    outMap.put(bb, new HashMap<>());
                }
                Queue<BB> worklist = new LinkedList<>(allBBs);
                // System.out.println("print the worklist " + worklist);
                while (!worklist.isEmpty()) {
                    BB bb = worklist.poll();
                    // System.out.println("basic block " + bb);
                    Map<String, LatticeValue> inEnv = new HashMap<>();
                    for (BB pred : bb.incomingEdges) {
                        Map<String, LatticeValue> predOut = outMap.get(pred);
                        inEnv = mergeEnvs(inEnv, predOut);
                        // System.out.println(inEnv + " inside worklist ");
                    }
                    if (!inEnv.equals(inMap.get(bb))) {
                        inMap.put(bb, inEnv);
                    }
                    Map<String, LatticeValue> oldOut = outMap.get(bb);
                    Map<String, LatticeValue> newOut = transfer(bb, inEnv);
                    if (!newOut.equals(oldOut)) {
                        outMap.put(bb, newOut);

                        for (BB succ : bb.outgoingEdges) {
                            if (!worklist.contains(succ)) {
                                worklist.add(succ);
                            }
                        }
                    }
                }
            }
        }
    }

    // Merge two environments using the lattice meet operator.
    // For each variable, the merged value is LatticeValue.merge(v1, v2).
    private Map<String, LatticeValue> mergeEnvs(Map<String, LatticeValue> env1, Map<String, LatticeValue> env2) {
        Map<String, LatticeValue> merged = new HashMap<>(env1);

        for (String var : env2.keySet()) {
            LatticeValue v1 = merged.getOrDefault(var, LatticeValue.T);
            LatticeValue v2 = env2.get(var);
            merged.put(var, LatticeValue.merge(v1, v2));
        }
        return merged;
    }

    // Apply the transfer function for a basic block.
    // We assume that the first instruction is a dummy and the real instructions
    // start at index 1.
    private Map<String, LatticeValue> transfer(BB bb, Map<String, LatticeValue> inEnv) {
        // Start with a copy of the IN environment.
        Map<String, LatticeValue> env = new HashMap<>(inEnv);
        // Process instructions; skip the dummy instruction at index 0.
        for (int i = 1; i < bb.instructions.size(); i++) {
            Instruction inst = bb.instructions.get(i);

            inst.transfer(env);
        }
        // System.out.println(env + " change");
        return env;
    }

    // Collect all basic blocks reachable from the given entry block.
    private Set<BB> collectBBs(BB entry) {
        Set<BB> visited = new HashSet<>();
        Queue<BB> queue = new LinkedList<>();
        queue.add(entry);
        while (!queue.isEmpty()) {
            BB bb = queue.poll();
            if (visited.contains(bb))
                continue;
            visited.add(bb);
            for (BB succ : bb.outgoingEdges) {
                queue.add(succ);
            }
        }
        return visited;
    }

    // Getters to access the final IN and OUT mappings.
    public Map<BB, Map<String, LatticeValue>> getInMap() {
        return inMap;
    }

    public Map<BB, Map<String, LatticeValue>> getOutMap() {
        return outMap;
    }
}
