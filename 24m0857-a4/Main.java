import syntaxtree.*;
import visitor.*;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      try {
         Node root = new a4javaout(System.in).Goal();
         // Object o = root.accept(new GJNoArguDepthFirst());
         // System.out.println("Program Parsed Sucessfully.");
         statichelper vst = new statichelper();
         String vt = null;
         root.accept(vst, vt);
         Map<String, Map<String, String>> thisvar = new HashMap<>();
         thisvar = vst.getMethodthis();
         HashMap<String, String> cha;
         cha = vst.getCHA();
         HashMap<String, HashSet<String>> med;
         med = vst.getmethoddetails();
         // vst.printParentClassMap();
         CHA hp = new CHA(cha, med);
         root.accept(hp, vt);
         // vst.printThisVar();
         Map<String, Boolean> callmethod = new HashMap<>();
         Map<String, String> chamedthod = new HashMap<>();
         chamedthod = hp.getResolvecall();
         callmethod = vst.getMethodcall();
         List<String> varreplace = hp.getvarreplace();
         staticfunc visitor = new staticfunc(thisvar, callmethod, varreplace);
         root.accept(visitor);
         // visitor.printClassMethodMap();
         Map<String, String> staticinfo = visitor.getClassMethod();

         idfa vis = new idfa(staticinfo, chamedthod);
         root.accept(vis);
         Map<String, String> classinfo = vis.getclassinfo();

         prettyprinter printer = new prettyprinter(staticinfo, chamedthod, classinfo);
         root.accept(printer);
         String out = printer.getOutput1();
         System.out.println(out);
         // vis.printdetails();
         // visitor.printClassMethodMap();
         // visitor.printObjectType();
         // visitor.printParentClassMap();
         // Call your visitor here
      } catch (

      ParseException e) {
         System.out.println(e.toString());
      }
   }
}
