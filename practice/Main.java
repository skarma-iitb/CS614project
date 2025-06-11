import syntaxtree.*;
import visitor.*;
import syntaxtree.*;
import visitor.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Parse the Java file
            A1JavaParser parser = new A1JavaParser(System.in); // Change to file input if needed
            Node root = parser.Goal();
            System.out.println("Program parsed successfully");

            // Use the Visitornew to traverse the AST
            Visitornew visitor = new Visitornew();
            root.accept(visitor); // Accept the visitor to extract symbol information

            // Print the symbol table
            visitor.getSymbolTable().printSymbolTable(); // Output the collected symbols
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}
