import syntaxtree.*;
import visitor.*;
import java.util.*;

public class Main {
   public static void main(String[] args) {
      try {
         Node root = new a2java(System.in).Goal();
         CFGGen cfgGen = new CFGGen();
         root.accept(cfgGen);
         ProgramCFG programCFG = cfgGen.getCFG();
         newworklist analysis = new newworklist();
         analysis.analyze(programCFG);
         Inliningvisitor tempo = new Inliningvisitor();
         root.accept(tempo);
         Inliningvisitor2 tempo2 = new Inliningvisitor2(tempo.parentClassMap,
               tempo.classMethodMap);
         root.accept(tempo2);
         // BB entryBlock = programCFG.methodBBSet.get(programCFG.mainMethod);
         // System.out.println(programCFG.methodBBSet);
         // BB entryBlock = programCFG.getStartBB();
         // // Step 4: Retrieve and print the constant propagation results for each BB
         Map<BB, Map<String, LatticeValue>> inMap = analysis.getInMap();
         Map<BB, Map<String, LatticeValue>> outMap = analysis.getOutMap();
         ConstantPropagationVisitor cp = new ConstantPropagationVisitor(outMap, inMap,
               programCFG);
         root.accept(cp);
         System.out.println(cp.getOutput());

         // // cp.printMethodBBSet();
         // System.out.println("Constant Propagation Analysis Results:");
         // for (BB bb : inMap.keySet()) {
         // System.out.println("Basic Block: " + bb.name);
         // System.out.println(" IN : " + inMap.get(bb));
         // System.out.println(" OUT: " + outMap.get(bb));
         // System.out.println("-------------------------------");
         // }
         // // Printing the DOT file
         // BB.printBBDOT(programCFG);
         // CCPRinting
         // CPPrintingVisitor printer = new CPPrintingVisitor(outMap, programCFG);
         // root.accept(printer);
         // System.out.println(printer.getOutput());
         // printer.printProgram(programCFG);
         // // For iterating over the program
         // for (String className : programCFG.classMethodList.keySet()) {
         // Set<String> methodList = programCFG.classMethodList.get(className);
         // System.out.println("Class: " + className);
         // for (String methodName : methodList) {
         // System.out.println("Method: " + methodName);
         // BB currentMethodBB = programCFG.methodBBSet.get(methodName);
         // BB.printBB(currentMethodBB);
         // }
         // }
         // my working code
         // ConstantPropagationAnalysis analysis = new ConstantPropagationAnalysis();
         // analysis.analyze(root);
         // Map<String, Integer> resultMap = analysis.getConstantMap();
         // System.out.println("Before Visitor Creation: " + resultMap);
         // new printing visitor
         // Newcpvisitor cpPrinter = new Newcpvisitor(programCFG, inMap, outMap);
         // root.accept(cpPrinter); // Alternatively, you could call
         // System.out.println(cpPrinter.getOutput());

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

         // Map<String, Integer> constants = analyzer.getConstantMap();
         // cfg of own
         // FlowSensitiveConstantPropagation analysis = new
         // FlowSensitiveConstantPropagation();
         // analysis.analyze(root);
         // analysis.printConstants();
         // Print the constants
         // System.out.println("Constant Variables:");
         // if (constants.isEmpty()) {
         // System.out.println("No constant variables found.");
         // } else {
         // for (Map.Entry<String, Integer> entry : constants.entrySet()) {
         // System.out.printf("%s = %d%n", entry.getKey(), entry.getValue());
         // }
         // }
         // Assignment Starts here
         // You can write your own custom visitor(s)
         // a2visitor tempo = new a2visitor();
         // root.accept(tempo);
      } catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}
