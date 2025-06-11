package visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import syntaxtree.MainClass;

public class Infocollector {
    Map<String, List<String>> info;
    Map<String, Allocate> allocations;

    public static Map<String, MethodAllocationInfo> Infocollector(Map<String, List<String>> info,
            Map<String, Allocate> allocations) {

        Map<String, MethodAllocationInfo> result = new HashMap<>();

        // Iterate over each method in the info map.
        for (String methodName : info.keySet()) {
            List<String> variables = info.get(methodName);
            // Use a Set to count unique registers.
            Set<String> usedRegisters = new HashSet<>();
            int spilledCount = 0;
            for (String var : variables) {
                Allocate alloc = allocations.get(var);
                if (alloc != null) {
                    if (alloc.spilled) {
                        spilledCount++;
                    } else {
                        usedRegisters.add(alloc.register);
                    }
                }
            }
            // Store the computed details in the result map.
            result.put(methodName, new MethodAllocationInfo(usedRegisters.size(), spilledCount));
        }

        return result;
    }

}
