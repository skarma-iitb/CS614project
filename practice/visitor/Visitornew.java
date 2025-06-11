package visitor;

import symboltable.SymbolTable; // Import SymbolTable from the symboltable package
import syntaxtree.*;
import visitor.GJNoArguDepthFirst; // Import the GJNoArguDepthFirst from the correct visitor package
import java.util.*;

public class Visitornew extends GJNoArguDepthFirst<Void> {
    private SymbolTable symbolTable = new SymbolTable();

    @Override
    public Void visit(ClassDeclaration n) {
        String className = n.f1.accept(this).toString(); // Assuming f1 represents the class name
        symbolTable.addField(className, "class"); // Assuming each class has an entry for demonstration
        return visit(n); // Visit inner nodes
    }

    @Override
    public Void visit(MethodDeclaration n) {
        String className = n.f0.accept(this).toString(); // Assuming f0 represents the class name
        String methodName = n.f2.accept(this).toString(); // Assuming f2 represents the method name
        symbolTable.addMethod(className, methodName);
        return visit(n); // Visit inner nodes
    }

    @Override
    public Void visit(VarDeclaration n) {
        String className = n.f0.accept(this).toString(); // Assuming f0 represents the class name
        String fieldName = n.f1.accept(this).toString(); // Assuming f1 represents the field name
        String type = n.f0.accept(this).toString(); // Assuming f0 represents the type
        symbolTable.addField(className, type + " " + fieldName);
        return visit(n); // Visit inner nodes
    }

    @Override
    public Void visit(MessageSend n) {
        String objectName = n.f0.accept(this).toString(); // Assuming f0 represents the object name
        String methodName = n.f2.accept(this).toString(); // Assuming f2 represents the method name
        symbolTable.addMethodCall(objectName, methodName);
        return visit(n); // Visit inner nodes
    }

    @Override
    public Void visit(FieldStoreStatement n) {
        String objectName = n.f0.accept(this).toString(); // Assuming f0 represents the object name
        String fieldName = n.f2.accept(this).toString(); // Assuming f2 represents the field name
        symbolTable.addFieldAccess(objectName, fieldName);
        return 
        visit(n); // Visit inner nodes
    }

    // New method to return the SymbolTable
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}