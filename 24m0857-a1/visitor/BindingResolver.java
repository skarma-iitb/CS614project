package visitor;

import syntaxtree.*;
import java.util.*;

public class BindingResolver extends DepthFirstVisitor {
    private HashMap<String, HashMap<String, List<String>>> symbolTable;
    private final TreeMap<String, TreeMap<String, List<String>>> outputData;
    private HashMap<String, String> parentClassMap;
    private HashMap<String, HashSet<String>> classMethodMap;
    private HashMap<String, String> objectTypes;
    private HashMap<String, String> variableType;
    private HashMap<String, List<String>> localVariables;
    private String currentClass;
    private String currentMethod;

    public BindingResolver(Symboltable symbolTable) {
        this.parentClassMap = symbolTable.getParentClassMap();
        this.classMethodMap = symbolTable.getClassMethodMap();
        this.objectTypes = symbolTable.getObjectTypes();
        this.symbolTable = symbolTable.getClassTable();
        this.localVariables = new HashMap<>();
        this.variableType = new HashMap<>();
        this.objectTypes = new HashMap<>();
        this.outputData = new TreeMap<>();

    }

    @Override
    public void visit(Goal n) {
        // n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
    }

    @Override
    public void visit(ClassDeclaration n) {
        currentClass = n.f1.f0.toString();
        // System.out.println("Visiting class: " + currentClass);
        outputData.putIfAbsent(currentClass, new TreeMap<>());
        super.visit(n);
        // currentClass = null;
    }

    @Override
    public void visit(ClassExtendsDeclaration n) {

        currentClass = n.f1.f0.toString();
        // System.out.println("Visiting class: " + currentClass);
        outputData.putIfAbsent(currentClass, new TreeMap<>());
        super.visit(n);
        // currentClass = null;
    }

    @Override
    public void visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        // System.out.println(currentClass + "." + currentMethod + ":");
        outputData.get(currentClass).putIfAbsent(currentMethod, new ArrayList<>());
        super.visit(n);
        currentMethod = null;
    }

    @Override
    public void visit(VarDeclaration n) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);
        if (varType != null) {
            objectTypes.put(varName, varType);
            // System.out.println(varName + " vardec " + varType);
        }
        if (currentMethod != null) {
            localVariables.putIfAbsent(currentMethod, new ArrayList<>());
            localVariables.get(currentMethod).add(varName);
            // System.out.println(varName + " local " + currentMethod);
        } else {
            variableType.put(varName, currentClass);
            // System.out.println(varName + " global " + currentClass);
        }
        super.visit(n);
    }

    // @Override
    // public void visit(Identifier n) {
    // if (currentMethod != null) {
    // String varName = n.f0.toString();
    // if (localVariables.contains(varName)) {
    // // Skip local variables (already handled elsewhere)
    // return;
    // }

    // if (variableType.containsKey(varName)) {
    // // If it's a global/class field, store it with class reference
    // String reference = currentClass + "." + varName + " = " +
    // variableType.get(varName) + "." + varName;
    // outputData.get(currentClass).get(currentMethod).add(reference);
    // }
    // }
    // }

    @Override
    public void visit(FormalParameter n) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);
        localVariables.putIfAbsent(currentMethod, new ArrayList<>());
        localVariables.get(currentMethod).add(varName);
        // System.out.println(varName + " local " + currentMethod);
        if (varType != null) {
            objectTypes.put(varName, varType);
            // System.out.println(varName + " formal " + varType);
        }

        super.visit(n);
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof BooleanType) {
            return "boolean";
        }
        if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (typeNode.f0.choice instanceof VoidType) {
            return "void";
        }
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }

        return null;
    }

    @Override
    public void visit(AssignmentStatement n) {
        if (n.f2.f0.choice instanceof FieldLookup) {
            FieldLookup fieldLookup = (FieldLookup) n.f2.f0.choice;
            String baseObject = fieldLookup.f0.f0.toString();
            String field = fieldLookup.f2.f0.toString();

            if (objectTypes.containsKey(baseObject)) {
                String objectType = objectTypes.get(baseObject);
                String reference = baseObject + "." + field;
                String resolved = objectType + "." + field;
                addReference(reference + " = " + resolved);
            }
        }
        if (n.f2.f0.choice instanceof MessageSend) {
            MessageSend messageSend = (MessageSend) n.f2.f0.choice;
            String baseObject = resolveObjectName(messageSend.f0);
            String method = messageSend.f2.f0.toString();
            // System.out.println(baseObject + method + " message");
            if (objectTypes.containsKey(baseObject)) {
                String callerType = objectTypes.get(baseObject);
                String reference = baseObject + "." + method;
                String resolved = "[" + callerType + "." + method + "]";
                addReference(reference + " = " + resolved);
                // System.out.println(reference + " = " + resolved);
            } else {
                String reference = baseObject + "." + method;
                String resolved = "[" + currentClass + "." + method + "]";
                addReference(reference + " = " + resolved);

                // System.out.println(reference + " = " + resolved);
            }
        }
        if (n.f0 instanceof Identifier) {
            Identifier identifier = (Identifier) n.f0;
            String identifierName = identifier.f0.toString();
            // System.out.println(identifierName + " lhs ");
            if (localVariables.containsKey(identifierName)) {
                String objectType = variableType.get(identifierName);
                addReference(identifierName + " = " + objectType);
                // System.out.println(identifierName + " = " + objectType + "." +
                // identifierName);
            }
        }
        if (n.f2.f0.choice instanceof PrimaryExpression) {
            PrimaryExpression pe = (PrimaryExpression) n.f2.f0.choice;
            if (pe.f0.choice instanceof Identifier) {
                Identifier identifier = (Identifier) pe.f0.choice;
                String identifierName = identifier.f0.toString();
                // System.out.println(identifierName + " rhs ");
                if (localVariables.containsKey(identifierName)) {
                    return;
                }
                if (variableType.containsKey(identifierName)) {
                    String objectType = variableType.get(identifierName);
                    addReference(identifierName + " = " + objectType + "." + identifierName);
                }
            }

            super.visit(n);
        }
    }

    @Override
    public void visit(FieldStoreStatement n) {
        String objectName = resolveObjectName(n.f0);

        if (objectName != null) {
            String objectType;
            if (objectName.equals("this")) {
                objectType = currentClass;
            } else {
                objectType = objectTypes.get(objectName);
            }
            String fieldName = n.f2.f0.toString();
            String reference = objectName + "." + fieldName;
            String resolved = objectType + "." + fieldName;
            if (currentClass != null && currentMethod != null) {
                outputData.get(currentClass).get(currentMethod).add(reference + " = " +
                        resolved);
            }
        } else {
            String identifierName = n.f0.toString();
            if (objectTypes.containsKey(identifierName)) {
                String type = objectTypes.get(identifierName);
                if (currentClass != null && currentMethod != null) {
                    outputData.get(currentClass).get(currentMethod).add(identifierName + " = " +
                            type);
                }
            }
        }
        super.visit(n);
    }

    @Override
    public void visit(MessageSendStatement n) {
        String caller = resolveObjectName(n.f0);
        String methodName = n.f2.f0.toString();
        // System.out.println("Processing: " + caller + "." + methodName);

        if (caller != null && objectTypes.containsKey(caller)) {

            String callerType = objectTypes.get(caller);
            // System.out.println(caller + " => " + methodName + " " + callerType);
            TreeSet<String> resolvedMethods = new TreeSet<>();
            Set<String> visited = new HashSet<>();
            // System.out.println("before calling resolve " + visited);
            resolveMethodBindings(callerType, methodName, resolvedMethods, visited);
            // System.out.println("Resolved Methods for " + caller + "." + methodName + " =
            // " + resolvedMethods);
            if (!resolvedMethods.isEmpty()) {
                // Store results in outputData
                if (currentClass != null && currentMethod != null) {
                    outputData.get(currentClass).get(currentMethod)
                            .add(caller + "." + methodName + " = " + resolvedMethods);
                }
            } else {
                // System.out.println("No methods found for: " + caller + "." + methodName);
            }
        } else {
            // System.out.println("Caller not found in objectTypes: " + caller);
        }
        super.visit(n);

    }

    private String resolveObjectName(BaseExpression baseExpression) {
        if (baseExpression.f0.choice instanceof Identifier) {
            return ((Identifier) baseExpression.f0.choice).f0.toString();
        } else if (baseExpression.f0.choice instanceof ThisExpression) {
            return "this";
        }
        return null;
    }

    private void addReference(String reference) {
        if (currentClass != null && currentMethod != null) {
            outputData.get(currentClass).get(currentMethod).add(reference);
        }
        // System.out.println("reff null");
    }

    private void resolveMethodBindings(String className, String methodName, TreeSet<String> resolvedMethods,
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
            // System.out.println("Methods in " + className + ": " +
            // classMethodMap.get(className));
            if (classMethodMap.get(className).contains(methodName)) {
                resolvedMethods.add(className + "." + methodName);
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
                    resolveMethodBindings(parentClass, methodName, resolvedMethods, visited);
                }
            }

        }

        // Traverse Children (avoid cycles)
        for (String childClass : parentClassMap.keySet()) {
            String parent = parentClassMap.get(childClass);
            if (parent != null && parent.equals(className)) {
                // System.out.println("Checking child: " + childClass);
                resolveMethodBindings(childClass, methodName, resolvedMethods, visited);
            }
        }
        visited.remove(className); // Backtrack
    }

    public void visit(IfStatement n) {
        n.f0.accept(this);
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public void visit(IfthenStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public void visit(IfthenElseStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public void visit(WhileStatement n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
    }

    public void printlocal() {

        for (Map.Entry<String, List<String>> entry : localVariables.entrySet()) {
            for (String var : entry.getValue()) {
                System.out.println(entry.getKey() + " " + var);
            }
        }
    }

    public void printOutput() {
        for (String className : outputData.keySet()) {
            TreeMap<String, List<String>> methods = outputData.get(className);
            for (String methodName : methods.keySet()) {
                List<String> references = methods.get(methodName);
                if (!references.isEmpty()) {// those who have atleast ne field or method references
                    System.out.println(className + "." + methodName + ":");
                    for (String ref : references) {
                        System.out.println(ref);
                    }
                }
            }
        }
    }
}
