import syntaxtree.*;
import visitor.*;

import java.io.FileWriter;
import java.util.*;

public class Main {
   public static void main(String[] args) {
      try {

         Node root = new a2java(System.in).Goal();
         CFGGen cfgGen = new CFGGen();
         root.accept(cfgGen);
         ProgramCFG programCFG = cfgGen.getCFG();

         // Printing the DOT file
         // BB.printBBDOT(programCFG);
         newworklist1 analysis = new newworklist1();
         analysis.analyze(programCFG);

         // Step 4: Retrieve and print the constant propagation results for each BB
         Map<BB, Map<String, LatticeValue>> inMap = analysis.getInMap();
         Map<BB, Map<String, LatticeValue>> outMap = analysis.getOutMap();

         System.out.println("Constant Propagation Analysis Results:");
         for (BB bb : inMap.keySet()) {
            System.out.println("Basic Block: " + bb.name);
            System.out.println("   IN : " + inMap.get(bb));
            System.out.println("   OUT: " + outMap.get(bb));
            System.out.println("-------------------------------");
         }
         // For iterating over the program
         // for (String className : programCFG.classMethodList.keySet()) {
         // Set<String> methodList = programCFG.classMethodList.get(className);
         // System.out.println("Class: " + className);
         // for (String methodName : methodList) {
         // System.out.println("Method: " + methodName);
         // BB currentMethodBB = programCFG.methodBBSet.get(methodName);
         // BB.printBB(currentMethodBB);
         // }
         // }

         // Assignment Starts here
         // You can write your own custom visitor(s)
      } catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}
