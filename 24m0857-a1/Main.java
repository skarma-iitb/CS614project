import syntaxtree.*;
import visitor.*;
import java.io.*;

public class Main {
   public static void main(String[] args) {
      // if (args.length != 1) {
      // System.err.println("Usage: java SymbolTableDriver <JavaSourceFile>");
      // return;
      // }

      try {
         // Parse the input Java file

         InputStream inputStream;

         // If an argument is provided, read from the file
         if (args.length > 0) {
            File inputFile = new File(args[0]);
            if (!inputFile.exists() || !inputFile.isFile()) {
               System.err.println("Error: Input file not found.");
               return;
            }
            inputStream = new FileInputStream(inputFile);
         } else {
            // Read from standard input
            inputStream = System.in;
         }
         A1JavaParser parser = new A1JavaParser(inputStream);
         Goal root = parser.Goal();
         // 1st pass
         Symboltable tempo = new Symboltable();
         root.accept(tempo);
         tempo.printSymbolTable();

         // 2nd pass
         BindingResolver bindingResolver = new BindingResolver(tempo);
         // root.accept(bindingResolver);
         // bindingResolver.printOutput();
         // bindingResolver.printlocal();
         // tempo.printClassMethodMap();
         // tempo.printParentClassMap();
         // tempo.printvariableClass();

         inputStream.close();
      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
         e.printStackTrace();
      }
   }
}
