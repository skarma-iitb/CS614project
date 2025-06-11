package symboltable;

import java.util.*;

public class SymbolTable {
    private Map<String, List<String>> fields;
    private Map<String, List<String>> methods;
    private Map<String, List<String>> variables;

    public SymbolTable() {
        fields = new HashMap<>();
        methods = new TreeMap<>();
        variables = new HashMap<>();
    }

    public void addField(String className, String fieldDeclaration) {
        String key = className + "." + fieldDeclaration;
        fields.computeIfAbsent(className, k -> new ArrayList<>()).add(fieldDeclaration);
    }

    public void addMethod(String className, String methodDeclaration) {
        String key = className + "." + methodDeclaration;
        methods.computeIfAbsent(className, k -> new ArrayList<>()).add(methodDeclaration);
    }

    public void addVariable(String variableDeclaration) {
        variables.computeIfAbsent(variableDeclaration, k -> new ArrayList<>()).add(variableDeclaration);
    }

    public void addMethodCall(String objectName, String methodName) {
        methods.computeIfAbsent(objectName, k -> new ArrayList<>()).add(methodName);
    }

    public void addFieldAccess(String objectName, String fieldName) {
        fields.computeIfAbsent(objectName, k -> new ArrayList<>()).add(fieldName);
    }

    public Map<String, List<String>> getFields() {
        return fields;
    }

    public Map<String, List<String>> getMethods() {
        return methods;
    }

    public Map<String, List<String>> getVariables() {
        return variables;
    }

    public void printSymbolTable() {
        System.out.println("\nClass Declarations:");
        for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
            Collections.sort(entry.getValue());
            for (String field : entry.getValue()) {
                System.out.println(entry.getKey() + "." + field);
            }
        }

        System.out.println("\nMethod Declarations:");
        for (Map.Entry<String, List<String>> entry : methods.entrySet()) {
            Collections.sort(entry.getValue());
            for (String method : entry.getValue()) {
                System.out.println(entry.getKey() + "." + method);
            }
        }

        System.out.println("\nVariable Declarations:");
        for (String var : variables.keySet()) {
            System.out.println(var);
        }
    }
}