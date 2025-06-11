package symboltable;

import java.util.HashMap;
import java.util.Map;

public
// Class to hold class information
class ClassInfo {
    Map<String, String> fields; // field name -> type
    Map<String, MethodInfo> methods; // method name -> method info
    String parentClass;

    public ClassInfo() {
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public void addField(String name, String type) {
        fields.put(name, type);
    }

    public void addMethod(String name, MethodInfo info) {
        methods.put(name, info);
    }
}