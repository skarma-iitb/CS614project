package visitor;

import syntaxtree.*;
import symboltable.*;

public class SymbolCollectorVisitor extends GJDepthFirst<Type, SymbolTable> {
    @Override
    public Type visit(ClassDeclaration n, SymbolTable symbolTable) {
        String className = n.f1.f0.toString();
        // Store class info
        symbolTable.addClass(className, null);

        // Visit fields
        for (Node field : n.f3.nodes) {
            Type fieldType = field.accept(this, symbolTable);
            // Store field info with its type
        }

        // Visit methods
        for (Node method : n.f4.nodes) {
            Type methodType = method.accept(this, symbolTable);
            // Store method info with return type and parameters
        }

        return new ClassType(className);
    }

    @Override
    public Type visit(MethodDeclaration n, SymbolTable symbolTable) {
        String methodName = n.f2.f0.toString();
        Type returnType = n.f1.accept(this, symbolTable);

        // Create new scope for method
        symbolTable.enterScope();

        // Visit parameters
        for (Node param : n.f4.nodes) {
            Type paramType = param.accept(this, symbolTable);
            // Store parameter info
        }

        // Visit local variable declarations
        for (Node varDecl : n.f7.nodes) {
            Type varType = varDecl.accept(this, symbolTable);
            // Store variable info
        }

        symbolTable.exitScope();
        return returnType;
    }

    @Override
    public Type visit(VarDeclaration n, SymbolTable symbolTable) {
        String varName = n.f1.f0.toString();
        Type varType = n.f0.accept(this, symbolTable);
        symbolTable.addVariable(varName, varType);
        return varType;
    }
}
