package visitor;

import java.util.*;
import syntaxtree.*;

public class Inliningvisitor2 extends GJNoArguDepthFirst<Void> {
    private HashMap<String, String> parentClassMap;
    private HashMap<String, HashSet<String>> classMethodMap;
    public HashMap<String, String> possibleMethods;
    ArrayList<String> aar = new ArrayList<>();
    private String currentClass;
    private String parentClass;
    private String currentMethod;

    public Inliningvisitor2(HashMap<String, String> parentClassMap, HashMap<String, HashSet<String>> classMethodMap) {
        this.parentClassMap = parentClassMap;
        this.classMethodMap = classMethodMap;
        this.possibleMethods = new HashMap<>();

    }

    public HashMap<String, String> getpossibleMethods() {
        return possibleMethods;
    }

    public ArrayList<String> getMethodsinline() {
        return aar;
    }

    @Override
    public Void visit(ClassDeclaration n) {
        currentClass = n.f1.f0.toString();
        parentClassMap.put(currentClass, null);
        super.visit(n);
        // currentClass = null;
        return null;
    }

    @Override
    public Void visit(ClassExtendsDeclaration n) {
        currentClass = n.f1.f0.toString();
        parentClass = n.f3.f0.toString();
        super.visit(n);
        return null;
    }

    @Override
    public Void visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        super.visit(n);
        return null;
    }

    @Override
    public Void visit(MessageSend n) {
        Void _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        Set<String> visited = new HashSet<>();
        String methodinline = n.f2.f0.toString();
        aar.add(methodinline);
        resolveMethodBindings(currentClass, methodinline, possibleMethods, visited);
        return _ret;
    }

    private void resolveMethodBindings(String className, String methodName,
            HashMap<String, String> possibleMethods,
            Set<String> visited) {
        // System.out.println(visited);
        if (visited.contains(className)) {
            // System.out.println("Cycle detected! Skipping class: " + className);
            return;
        }
        // className);
        visited.add(className); // Mark as visited
        // System.out.println("Looking for method: " + methodName + " in class: " +
        // className + " invist " + visited);
        Boolean found = false;

        if (classMethodMap.containsKey(className)) {
            // System.out.println("looking in " + className + "for method " +
            // methodName);
            if (classMethodMap.get(className).contains(methodName)) {
                // possibleMethods.put(className, methodName);
                if (possibleMethods.containsKey(methodName)) {
                    // If the method already exists, remove it.
                    possibleMethods.remove(methodName);
                    return;
                } else {
                    // Otherwise, add the new method.
                    possibleMethods.put(methodName, className);
                    // System.out.println("Added method \"" + methodName + "\" defined in class \""
                    // + className + "\".");
                }
                // System.out.println("Found method: " + className + "." + methodName);
                found = true;
            }
        }

        if (found == false) {
            // System.out.println("false inside parent");
            if (parentClassMap.containsKey(className)) {
                String parentClass = parentClassMap.get(className);
                // System.out.println("checking");
                if (parentClass != null) {
                    // System.out.println("Checking parent: " + parentClass);
                    resolveMethodBindings(parentClass, methodName, possibleMethods, visited);
                }
            }

        }
        for (String childClass : parentClassMap.keySet()) {
            String parent = parentClassMap.get(childClass);
            if (parent != null && parent.equals(className)) {
                // System.out.println("Checking child: " + childClass);
                resolveMethodBindings(childClass, methodName, possibleMethods, visited);
            }
        }
        visited.remove(className); // Backtrack
    }

    public void printPossibleMethods() {
        if (possibleMethods == null || possibleMethods.isEmpty()) {
            System.out.println("No methods available.");
            return;
        }
        System.out.println("Possible Methods:");
        for (Map.Entry<String, String> entry : possibleMethods.entrySet()) {
            System.out.println("Method: " + entry.getKey() + " -> Description: " + entry.getValue());
        }

    }
}
