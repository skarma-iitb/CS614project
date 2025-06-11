import syntaxtree.*;
import visitor.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {

            Node root = new A3Java(System.in).Goal();
            CFGGen cfgGen = new CFGGen();
            root.accept(cfgGen);

            ProgramCFG programCFG = cfgGen.getCFG();
            // BB.printBBDOT(programCFG);

            RunAnalysis ra = new RunAnalysis(programCFG);
            ra.startAnalysisBackward();
            HashMap<Node, Set<String>> resultMap = ra.getResultMap();
            IntervalsCollector rp = new IntervalsCollector(resultMap);
            root.accept(rp);
            Map<String, LiveInterval> range = rp.getResult();
            Map<String, String> vartype = rp.getvartype();
            Map<String, List<String>> varclass = rp.getvarclass();
            // ResultPrinter r = new ResultPrinter(resultMap);
            // root.accept(r);
            int register = rp.getRegister();
            // rp.printLiveIntervals(range);
            // rp.printVarType(vartype);
            // for (Map.Entry<String, List<String>> entry : varclass.entrySet()) {
            // System.out.println("Method: " + entry.getKey());
            // System.out.println("Variables: " + entry.getValue());
            // System.out.println("-------------------");
            // }
            lsra Ls = new lsra();
            Map<String, Allocate> allocation = Ls.lsra(range, register);

            Infocollector infoc = new Infocollector();
            // from this i get details of class like no. of spill and all
            Map<String, MethodAllocationInfo> methodDetails = infoc.Infocollector(varclass, allocation);
            // System.out.println("before helper");
            helper hp = new helper();
            Map<String, Allocate> newallocation = hp.helper(varclass, methodDetails, allocation);
            // System.out.println("Final Allocation:");
            // for (Map.Entry<String, Allocate> entry : newallocation.entrySet()) {
            // System.out.println(" " + entry.getKey() + " : " + entry.getValue());
            // }
            // Print out the results.
            // for (Map.Entry<String, MethodAllocationInfo> entry :
            // methodDetails.entrySet()) {
            // System.out.println(entry.getKey() + " " + entry.getValue());
            // }
            // Infocollector ic = new Infocollector(varclass, newallocation);
            prettyprinter pp = new prettyprinter(newallocation, vartype, methodDetails,
                    register);
            root.accept(pp);
            System.out.println(pp.getoutput());

            // Assignment Starts here
            // You can write your own custom visitor(s)
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}
