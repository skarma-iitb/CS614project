package symboltable;

import java.util.*;

// Main Symbol Table class
public class SymbolTable {
    private Map<String, ClassInfo> classes;
    private Stack<Map<String, String>> scopeStack; // Stack for handling nested scopes
    private String currentClass;
    private String currentMethod;

    public SymbolTable() {
        this.classes = new HashMap<>();
        this.scopeStack = new Stack<>();
        enterScope(); // Global scope
    }

    // Scope management
    public void enterScope() {
        scopeStack.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    // Class management
    public void addClass(String className, String parentClass) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.parentClass = parentClass;
        classes.put(className, classInfo);
        currentClass = className;
    }

    // Field management
    public void addField(String className, String fieldName, String fieldType) {
        ClassInfo classInfo = classes.get(className);
        if (classInfo != null) {
            classInfo.addField(fieldName, fieldType);
        }
    }

    // Method management
    public void addMethod(String className, String methodName, String returnType) {
        ClassInfo classInfo = classes.get(className);
        if (classInfo != null) {
            classInfo.addMethod(methodName, new MethodInfo(returnType));
            currentMethod = methodName;
        }
    }

    // Variable management
    public void addVariable(String name, String type) {
        if (!scopeStack.isEmpty()) {
            scopeStack.peek().put(name, type);
        }

        // If we're in a method, also add to method's local variables
        if (currentClass != null && currentMethod != null) {
            ClassInfo classInfo = classes.get(currentClass);
            if (classInfo != null) {
                MethodInfo methodInfo = classInfo.methods.get(currentMethod);
                if (methodInfo != null) {
                    methodInfo.addLocalVar(name, type);
                }
            }
        }
    }

    // Reference management
    public void addReference(String reference, String resolved) {
        if (currentClass != null && currentMethod != null) {
            ClassInfo classInfo = classes.get(currentClass);
            if (classInfo != null) {
                MethodInfo methodInfo = classInfo.methods.get(currentMethod);
                if (methodInfo != null) {
                    methodInfo.addReference(reference, resolved);
                }
            }
        }
    }

    // Type resolution
    public String resolveType(String name) {
        // Check local scopes first (from top to bottom)
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            String type = scopeStack.get(i).get(name);
            if (type != null)
                return type;
        }

        // Check if it's a field of current class
        if (currentClass != null) {
            ClassInfo classInfo = classes.get(currentClass);
            if (classInfo != null && classInfo.fields.containsKey(name)) {
                return classInfo.fields.get(name);
            }
        }

        return null;
    }

    // Print the symbol table in required format
    public void printSymbolTable() {
        // Sort classes alphabetically
        TreeMap<String, ClassInfo> sortedClasses = new TreeMap<>(classes);

        for (Map.Entry<String, ClassInfo> classEntry : sortedClasses.entrySet()) {
            String className = classEntry.getKey();
            ClassInfo classInfo = classEntry.getValue();

            System.out.println("Class: " + className);
            if (classInfo.parentClass != null) {
                System.out.println("Parent: " + classInfo.parentClass);
            }

            // Print fields
            System.out.println("Fields:");
            TreeMap<String, String> sortedFields = new TreeMap<>(classInfo.fields);
            for (Map.Entry<String, String> field : sortedFields.entrySet()) {
                System.out.println("  " + field.getKey() + ": " + field.getValue());
            }

            // Print methods and their references
            System.out.println("Methods:");
            TreeMap<String, MethodInfo> sortedMethods = new TreeMap<>(classInfo.methods);
            for (Map.Entry<String, MethodInfo> methodEntry : sortedMethods.entrySet()) {
                String methodName = methodEntry.getKey();
                MethodInfo methodInfo = methodEntry.getValue();

                System.out.println("  " + className + "." + methodName + ":");

                // Print references in program order
                for (Map.Entry<String, List<String>> ref : methodInfo.references.entrySet()) {
                    String reference = ref.getKey();
                    List<String> resolvedRefs = ref.getValue();

                    // Sort resolved references for method calls
                    Collections.sort(resolvedRefs);

                    if (resolvedRefs.size() == 1) {
                        System.out.println("    " + reference + " = " + resolvedRefs.get(0));
                    } else {
                        System.out.println("    " + reference + " = [" +
                                String.join(" ", resolvedRefs) + "]");
                    }
                }
            }
            System.out.println();
        }
    }
}
