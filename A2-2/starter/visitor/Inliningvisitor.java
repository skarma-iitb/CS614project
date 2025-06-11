package visitor;

import java.util.*;

import syntaxtree.*;

public class Inliningvisitor extends GJNoArguDepthFirst<Void> {
    public HashMap<String, String> parentClassMap;
    public HashMap<String, HashSet<String>> classMethodMap;
    private Map<String, String> parameters = new HashMap<>();
    private String currentClass;
    private String parentClass;
    private String currentMethod;

    public Inliningvisitor() {
        this.parentClassMap = new HashMap<>();
        this.classMethodMap = new HashMap<>();
    }

    @Override
    public Void visit(ClassDeclaration n) {
        currentClass = n.f1.f0.toString();
        // System.out.println("Visiting class: " + currentClass);
        parentClassMap.put(currentClass, null);
        classMethodMap.putIfAbsent(currentClass, new HashSet<>());
        super.visit(n);
        // currentClass = null;
        return null;
    }

    @Override
    public Void visit(ClassExtendsDeclaration n) {
        currentClass = n.f1.f0.toString();
        // System.out.println("Visiting class: " + currentClass);
        currentClass = n.f1.f0.toString();
        parentClass = n.f3.f0.toString();
        // symbolTable.putIfAbsent(currentClass, new HashMap<>());
        parentClassMap.put(currentClass, parentClass);
        classMethodMap.putIfAbsent(currentClass, new HashSet<>());
        super.visit(n);
        // currentClass = null;
        return null;
    }

    @Override
    public Void visit(MethodDeclaration n) {

        currentMethod = n.f2.f0.toString();
        classMethodMap.putIfAbsent(currentClass, new HashSet<>());
        classMethodMap.get(currentClass).add(currentMethod);
        parameters.clear();
        if (n.f4.present()) {
            n.f4.accept(this);
        }
        super.visit(n);
        return null;
    }

    @Override
    public Void visit(FormalParameterList n) {
        n.f0.accept(this);
        n.f1.accept(this);
        return null;
    }

    @Override
    public Void visit(FormalParameter n) {
        String type = extractFieldType(n.f0);
        String name = n.f1.f0.toString();
        parameters.put(name, type);
        return null;
    }

    @Override
    public Void visit(FormalParameterRest n) {
        // n.f0.accept(this);
        n.f1.accept(this);
        return null;
    }

    @Override
    public Void visit(VarDeclaration n) {
        Void _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof BooleanType) {
            return "boolean";
        }
        if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }

        return null;
    }

    // private String resolveObjectName(PrimaryExpression baseExpression) {
    // if (baseExpression.f0.choice instanceof Identifier) {
    // return ((Identifier) baseExpression.f0.choice).f0.toString();
    // } else if (baseExpression.f0.choice instanceof ThisExpression) {
    // return "this";
    // }
    // return null;
    // }

    public void printClassMethodMap() {
        System.out.println("Final classMethodMap:");
        for (String className : classMethodMap.keySet()) {
            System.out.println(className + " -> " + classMethodMap.get(className));
        }
    }

    public void printParentClassMap() {
        System.out.println("Final parentClassMap:");
        for (String className : parentClassMap.keySet()) {
            String parent = parentClassMap.get(className);
            if (parent == null) {
                System.out.println(className + " -> No Parent");
            } else {
                System.out.println(className + " -> " + parent);
            }
        }
    }

    public HashMap<String, String> getParentClassMap() {
        return parentClassMap;
    }

    // Getter for classMethodMap
    public HashMap<String, HashSet<String>> getClassMethodMap() {
        return classMethodMap;
    }

}
