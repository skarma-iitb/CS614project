package symboltable;

import java.util.*;

// Class to hold method information
class MethodInfo {
    String returnType;
    List<String> paramTypes;
    Map<String, String> localVars; // variable name -> type
    Map<String, List<String>> references; // reference -> list of resolved references

    public MethodInfo(String returnType) {
        this.returnType = returnType;
        this.paramTypes = new ArrayList<>();
        this.localVars = new HashMap<>();
        this.references = new HashMap<>();
    }

    public void addParameter(String name, String type) {
        paramTypes.add(type);
        localVars.put(name, type);
    }

    public void addLocalVar(String name, String type) {
        localVars.put(name, type);
    }

    public void addReference(String ref, String resolved) {
        references.computeIfAbsent(ref, k -> new ArrayList<>()).add(resolved);
    }
}
