package visitor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class lsra {
    public static Map<String, Allocate> lsra(Map<String, LiveInterval> liveRanges, int numRegisters) {
        Map<String, Allocate> allocationMap = new HashMap<>();
        int spillvar = 0;
        if (numRegisters == 0) {
            int spillIndex = 0;
            for (String var : liveRanges.keySet()) {
                allocationMap.put(var, new Allocate(spillIndex++));
                // System.out.println("memory allocation " + spillvar++);
            }
            return allocationMap;
        }
        List<Map.Entry<String, LiveInterval>> intervals = new ArrayList<>(liveRanges.entrySet());
        intervals.sort((e1, e2) -> {
            int cmp = Integer.compare(e1.getValue().start, e2.getValue().start);
            if (cmp == 0) {
                cmp = e1.getKey().compareTo(e2.getKey());
            }
            return cmp;
        });
        List<Map.Entry<String, LiveInterval>> active = new ArrayList<>();
        active.sort((e1, e2) -> {
            int cmp = Integer.compare(e1.getValue().end, e2.getValue().end);
            if (cmp == 0) {
                cmp = e1.getKey().compareTo(e2.getKey());
            }
            return cmp;
        });
        List<String> freeRegs = new ArrayList<>();
        for (int i = 0; i < numRegisters; i++) {
            freeRegs.add("R" + i);
        }
        int nextSpillIndex = 0;
        for (Map.Entry<String, LiveInterval> currentEntry : intervals) {
            String var = currentEntry.getKey();
            LiveInterval currentInterval = currentEntry.getValue();
            Iterator<Map.Entry<String, LiveInterval>> it = active.iterator();
            while (it.hasNext()) {
                Map.Entry<String, LiveInterval> activeEntry = it.next();
                if (activeEntry.getValue().end < currentInterval.start) {
                    Allocate alloc = allocationMap.get(activeEntry.getKey());
                    if (alloc != null && !alloc.spilled) {
                        freeRegs.add(alloc.register);
                    }
                    it.remove();
                }
            }
            if (!freeRegs.isEmpty()) {
                String reg = freeRegs.remove(0);
                allocationMap.put(var, new Allocate(reg));
                active.add(currentEntry);
                active.sort((e1, e2) -> {
                    int cmp = Integer.compare(e1.getValue().end, e2.getValue().end);
                    if (cmp == 0) {
                        cmp = e1.getKey().compareTo(e2.getKey());
                    }
                    return cmp;
                });
            } else {
                Map.Entry<String, LiveInterval> spillCandidate = active.get(active.size() - 1);
                if (spillCandidate.getValue().end > currentInterval.end) {
                    Allocate candidateAlloc = allocationMap.get(spillCandidate.getKey());
                    String candidateReg = candidateAlloc.register;
                    allocationMap.put(spillCandidate.getKey(), new Allocate(nextSpillIndex++));
                    // System.out.println("memory allocation " + spillvar++);
                    active.remove(active.size() - 1);
                    freeRegs.add(candidateReg);
                    String reg = freeRegs.remove(0);
                    allocationMap.put(var, new Allocate(reg));
                    active.add(currentEntry);
                    active.sort((e1, e2) -> {
                        int cmp = Integer.compare(e1.getValue().end, e2.getValue().end);
                        if (cmp == 0) {
                            cmp = e1.getKey().compareTo(e2.getKey());
                        }
                        return cmp;
                    });
                } else {
                    allocationMap.put(var, new Allocate(nextSpillIndex++));
                    // System.out.println("memory allocation " + spillvar++);
                }
            }
        }

        return allocationMap;
    }

}
