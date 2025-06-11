package visitor;

import java.util.*;

public class SymbolTable {
    private Map<String, Object> constants = new HashMap<>();
    private Map<String, String> methods = new HashMap<>();

    public void addConstant(String name, Object value) {
        constants.put(name, value);
    }

    public Object getConstant(String name) {
        return constants.get(name);
    }

    public void addMethod(String name, String method) {
        methods.put(name, method);
    }

    public String getMethod(String name) {
        return methods.get(name);
    }

    public boolean isConstant(String name) {
        return constants.containsKey(name);
    }
}
