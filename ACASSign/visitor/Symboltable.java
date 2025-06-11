package visitor;

import syntaxtree.*;
import java.util.*;

public class Symboltable extends DepthFirstVisitor {
    private HashMap<String, HashMap<String, List<String>>> symbolTable;
    private final HashMap<String, String> parentClassMap;
    private final HashMap<String, HashSet<String>> classMethodMap;
    private HashMap<String, String> objectTypes;
    private String currentClass;
    private String currentMethod;
    private String parentClass;

    public Symboltable() {
        symbolTable = new HashMap<>();
        objectTypes = new HashMap<>();
        this.parentClassMap = new HashMap<>();
        this.classMethodMap = new HashMap<>();
    }

    public HashMap<String, HashMap<String, List<String>>> getClassTable() {
        return symbolTable;
    }

    public HashMap<String, String> getParentClassMap() {
        return this.parentClassMap;
    }

    public HashMap<String, HashSet<String>> getClassMethodMap() {
        return this.classMethodMap;
    }

    public HashMap<String, String> getObjectTypes() {
        return this.objectTypes;
    }

    private void addToSymbolTable(String className, String elementType, String reference) {
        symbolTable.putIfAbsent(className, new HashMap<>());
        symbolTable.get(className).putIfAbsent(elementType, new ArrayList<>());
        if (!symbolTable.get(className).get(elementType).contains(reference)) {
            symbolTable.get(className).get(elementType).add(reference);
        }
    }

    @Override
    public void visit(ClassDeclaration n) {
        currentClass = n.f1.f0.toString();
        symbolTable.putIfAbsent(currentClass, new HashMap<>());
        // System.out.println(currentClass);
        parentClassMap.put(currentClass, null);
        classMethodMap.putIfAbsent(currentClass, new HashSet<>());
        super.visit(n);
        // currentClass = null;

    }

    @Override
    public void visit(ClassExtendsDeclaration n) {
        currentClass = n.f1.f0.toString();
        parentClass = n.f3.f0.toString();
        symbolTable.putIfAbsent(currentClass, new HashMap<>());
        parentClassMap.put(currentClass, parentClass);
        classMethodMap.putIfAbsent(currentClass, new HashSet<>()); // Ensure class exists
        // System.out.println(currentClass + " " + parentClass + " classextend");

        super.visit(n);
        // currentClass = null;

    }

    @Override
    public void visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        addToSymbolTable(currentClass, "methods", currentMethod);
        classMethodMap.putIfAbsent(currentClass, new HashSet<>());
        classMethodMap.get(currentClass).add(currentMethod);
        // System.out.println(currentClass + " " + currentMethod + " methd decal");

        super.visit(n);
    }

    @Override
    public void visit(FormalParameter n) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);
        if (varType == null) {
            return;
        }
        objectTypes.put(varName, varType);
        super.visit(n);
    }

    @Override
    public void visit(VarDeclaration n) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);
        if (varType == null) {
            return;
        }
        objectTypes.put(varName, varType);
        super.visit(n);
    }

    @Override
    public void visit(AssignmentStatement n) {
        if (n.f2.f0.choice instanceof FieldLookup) {
            FieldLookup fieldLookup = (FieldLookup) n.f2.f0.choice;
            String baseObject = fieldLookup.f0.f0.toString();
            String field = fieldLookup.f2.f0.toString();

            if (baseObject != null && objectTypes.containsKey(baseObject)) {
                String objectType = objectTypes.get(baseObject);
                String reference = baseObject + "." + field;
                String resolved = objectType + "." + field;
                addToSymbolTable(currentClass, currentMethod + "_refs", reference + "=" + resolved);
            }
        } else if (n.f2.f0.choice instanceof MessageSend) {
            MessageSend messageSend = (MessageSend) n.f2.f0.choice;
            String baseObject = resolveObjectName(messageSend.f0);
            String field = messageSend.f2.f0.toString();
            if (baseObject != null && baseObject.equals("this")) {
                String reference = baseObject + "." + field;
                String resolved = currentClass + "." + field;
                addToSymbolTable(currentClass, currentMethod + "_refs", reference + "=" + resolved);
            } else if (baseObject != null && objectTypes.containsKey(baseObject)) {
                String objectType = objectTypes.get(baseObject);
                String reference = baseObject + "." + field;
                String resolved = objectType + "." + field;
                addToSymbolTable(currentClass, currentMethod + "_refs", reference + "=" + resolved);
            }
        }
        super.visit(n);
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }
        return null;
    }

    @Override
    public void visit(MessageSend n) {
        String caller = resolveObjectName(n.f0);
        String methodName = n.f2.f0.toString();
        if (objectTypes.containsKey(caller)) {
            String callerType = objectTypes.get(caller);
            String reference = caller + "." + methodName;
            String resolved = callerType + "." + methodName;
            addToSymbolTable(currentClass, currentMethod + "_calls", reference + "=" + resolved);
        }
        super.visit(n);
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
            addToSymbolTable(currentClass, currentMethod + "_refs", reference + "=" + resolved);

        } else {

            String identifierName = n.f0.toString();
            if (objectTypes.containsKey(identifierName)) {
                String type = objectTypes.get(identifierName);
                addToSymbolTable(currentClass, currentMethod + "_refs", identifierName + "=" + type);

            }
        }
        ;

        super.visit(n);
    }

    @Override
    public void visit(MessageSendStatement n) {
        String object = resolveObjectName(n.f0);
        String field = n.f2.f0.toString();
        if (object != null && objectTypes.containsKey(object)) {
            String objectType = objectTypes.get(object);
            String reference = object + "." + field;
            String resolved = objectType + "." + field;
            addToSymbolTable(currentClass, currentMethod + "_refs", reference + "=" + resolved);
        }
        super.visit(n);
    }

    private String resolveObjectName(BaseExpression baseExpression) {
        if (baseExpression.f0.choice instanceof Identifier) {
            return ((Identifier) baseExpression.f0.choice).f0.toString();
        } else if (baseExpression.f0.choice instanceof ThisExpression) {
            return "this";
        }
        return "Unknown";
    }

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

    public void printSymbolTable() {
        for (String className : new TreeSet<>(symbolTable.keySet())) {
            System.out.println("Class: " + className);

            HashMap<String, List<String>> classInfo = symbolTable.get(className);
            List<String> methods = classInfo.getOrDefault("methods", new ArrayList<>());
            for (String method : new TreeSet<>(methods)) {
                System.out.println(className + "." + method + ":");

                String refKey = method + "_refs";
                if (classInfo.containsKey(refKey)) {
                    for (String ref : classInfo.get(refKey)) {
                        System.out.println("  " + ref);
                    }
                }

                // Print method calls
                String callKey = method + "_calls";
                if (classInfo.containsKey(callKey)) {
                    for (String call : classInfo.get(callKey)) {
                        System.out.println("  " + call);
                    }
                }
            }
        }
    }
}