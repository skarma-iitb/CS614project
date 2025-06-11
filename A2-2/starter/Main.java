import syntaxtree.*;
import visitor.*;
import java.util.*;

import javax.swing.text.html.InlineView;

public class Main {
   public static void main(String[] args) {
      try {
         Node root = new a2java(System.in).Goal();
         CFGGen cfgGen = new CFGGen();
         root.accept(cfgGen);
         ProgramCFG programCFG = cfgGen.getCFG();
         newworklist analysis = new newworklist();
         analysis.analyze(programCFG);
         // // Printing the DOT file
         // BB.printBBDOT(programCFG);

         Inliningvisitor tempo = new Inliningvisitor();
         root.accept(tempo);
         Inliningvisitor2 tempo2 = new Inliningvisitor2(tempo.parentClassMap,
               tempo.classMethodMap);
         root.accept(tempo2);
         // tempo.printClassMethodMap();
         // tempo.printParentClassMap();
         // tempo2.printPossibleMethods();
         HashMap<String, String> possibleMethods = tempo2.getpossibleMethods();
         if (possibleMethods.size() >= 1) {

            Inliningvisitor3 tempo3 = new Inliningvisitor3(possibleMethods);
            HashMap<String, List<String>> methodtable = tempo3.getmethodtable();
            HashMap<String, List<String>> methodtable1 = tempo3.getmethodtable1();
            HashMap<String, String> methodvalue = tempo3.getmethodvalue();
            String s = "fun";
            root.accept(tempo3, s);
            Map<BB, Map<String, LatticeValue>> inMap = analysis.getInMap();
            Map<BB, Map<String, LatticeValue>> outMap = analysis.getOutMap();
            ConstantPropagationVisitor2 cp = new ConstantPropagationVisitor2(outMap, inMap,
                  programCFG, methodtable, methodtable1, methodvalue, possibleMethods);
            root.accept(cp);
            System.out.println(cp.getOutput());
         } else {
            Map<BB, Map<String, LatticeValue>> inMap = analysis.getInMap();
            Map<BB, Map<String, LatticeValue>> outMap = analysis.getOutMap();
            ConstantPropagationVisitor cp = new ConstantPropagationVisitor(outMap, inMap,
                  programCFG);
            root.accept(cp);
            System.out.println(cp.getOutput());
         }
         // tempo3.printMethodTable();
         // tempo3.printMethodvalue();

         // HashMap<String, String> uniqueMap = tempo2.getUniqueMethodMapping();
         // System.out.println("Unique Methods (method -> class):");
         // for (Map.Entry<String, String> entry : uniqueMap.entrySet()) {
         // System.out.println("Method: " + entry.getKey() + " defined in class: " +
         // entry.getValue());
         // }

         // // cp.printMethodBBSet();
         // System.out.println("Constant Propagation Analysis Results:");
         // for (BB bb : inMap.keySet()) {
         // System.out.println("Basic Block: " + bb.name);
         // System.out.println(" IN : " + inMap.get(bb));
         // System.out.println(" OUT: " + outMap.get(bb));
         // System.out.println("-------------------------------");
         // }

         // cpPrinter.printProgram(programCFG);
         // System.out.println(cpPrinter.getOutput());
         // // ConstantPropagationVisitor visitor = new ConstantPropagationVisitor(inMap,
         // outMap);
         // root.accept(visitor);
         // System.out.println(visitor.getOutput());
         // // BB start = analysis.analyze(start);
         // analysis.printResults();
         // visitor.constant();
         // visitor.constant();
         // till here
         // Get the map of identified constants
         // After your analysis
         // analysis.constant();

      } catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}
