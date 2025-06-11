package visitor;

import java.util.*;
import syntaxtree.*;

public class helper {
    Map<String, List<String>> info;
    Map<String, MethodAllocationInfo> methodinfo;
    Map<String, Allocate> varinfo;

    public static Map<String, Allocate> helper(Map<String, List<String>> info,
            Map<String, MethodAllocationInfo> methodinfo, Map<String, Allocate> varinfo) {
        // this.info = info;
        // this.allocations = allocations;
        int i;
        Map<String, Allocate> result = new HashMap<String, Allocate>();
        for (String methodName : info.keySet()) {
            MethodAllocationInfo minfo = methodinfo.get(methodName);
            // System.out.println(methodName + " info reg " + minfo.registersUsed + " spill
            // count " + minfo.spilledCount);
            i = 0;
            List<String> variables = info.get(methodName);
            for (String var : variables) {
                Allocate alloc = varinfo.get(var);

                if (alloc != null) {
                    if (alloc.spilled) {
                        int size = minfo.spilledCount;
                        // System.out.println(var + " method " + methodName + " " + alloc.spillIndex);
                        result.put(var, new Allocate(i));
                        if (i < minfo.spilledCount) {
                            i++;
                        }

                    } else {
                        int reg = minfo.registersUsed;
                        // System.out.println(var + " method " + methodName + " " + alloc.register);
                        result.put(var, new Allocate(alloc.register));
                        // if (i < minfo.registersUsed) {
                        // i++;
                        // }

                    }
                }
            }

        }
        return result;

    }

}
