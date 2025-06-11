package visitor;

import java.util.*;

public class newworklist {
    // Maps to store the IN and OUT environments for each basic block.
    // The environment is a mapping from variable names to their LatticeValue.
    private Map<BB, Map<String, LatticeValue>> inMap = new HashMap<>();
    private Map<BB, Map<String, LatticeValue>> outMap = new HashMap<>();

    // Run the analysis over all methods in the ProgramCFG.
    // We assume that programCFG.classMethodList maps class names to sets of method
    // names,
    // and programCFG.methodBBSet maps each method name to its entry basic block.
    public void analyze(ProgramCFG programCFG) {
        for (String className : programCFG.classMethodList.keySet()) {
            Set<String> methodList = programCFG.classMethodList.get(className);
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
                while (!worklist.isEmpty()) {
                    BB bb = worklist.poll();
                    Map<String, LatticeValue> inEnv = new HashMap<>();
                    for (BB pred : bb.incomingEdges) {
                        Map<String, LatticeValue> predOut = outMap.get(pred);
                        inEnv = mergeEnvs(inEnv, predOut);
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
            LatticeValue v1 = merged.getOrDefault(var, LatticeValue.top);
            LatticeValue v2 = env2.get(var);
            merged.put(var, LatticeValue.merge(v1, v2));
        }
        return merged;
    }

    // Apply the transfer function for a basic block.
    // We assume that the first instruction is a dummy and the real instructions
    // start at index 1.
    private Map<String, LatticeValue> transfer(BB bb, Map<String, LatticeValue> inEnv) {
        // System.out.println("wrklist transfer");
        Map<String, LatticeValue> env = new HashMap<>(inEnv);
        for (int i = 1; i < bb.instructions.size(); i++) {
            Instruction inst = bb.instructions.get(i);
            inst.transfer(env);
        }
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