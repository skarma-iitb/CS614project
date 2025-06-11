package visitor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import syntaxtree.*;

public class statichelper extends GJDepthFirst<String, String> {
    public Map<String, Map<String, String>> methodthis;
    private String currentClass;
    public Map<String, Boolean> callmethod;
    public HashMap<String, String> CHA;
    public Map<String, String> objectType;
    public HashMap<String, HashSet<String>> methodDetails;
    private String key;

    public statichelper() {
        methodthis = new HashMap<>();
        callmethod = new HashMap<>();
        CHA = new HashMap<>();
        methodDetails = new HashMap<>();
        objectType = new HashMap<>();
    }

    public Map<String, Map<String, String>> getMethodthis() {
        return methodthis;
    }

    public Map<String, Boolean> getMethodcall() {
        return callmethod;
    }

    public HashMap<String, String> getCHA() {
        return CHA;
    }

    public HashMap<String, HashSet<String>> getmethoddetails() {
        return methodDetails;
    }

    @Override
    public String visit(NodeList n, String argu) {
        String _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this, argu);
            _count++;
        }
        return _ret;
    }

    @Override
    public String visit(NodeListOptional n, String argu) {
        if (n.present()) {
            String _ret = null;
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                e.nextElement().accept(this, argu);
                _count++;
            }
            return _ret;
        } else
            return null;
    }

    @Override
    public String visit(NodeOptional n, String argu) {
        if (n.present())
            return n.node.accept(this, argu);
        else
            return null;
    }

    @Override
    public String visit(NodeSequence n, String argu) {
        String _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this, argu);
            _count++;
        }
        return _ret;
    }

    @Override
    public String visit(NodeToken n, String argu) {
        return null;
    }

    @Override
    public String visit(Goal n, String a) {
        String _ret = null;
        // n.f0.accept(this);
        n.f1.accept(this, a);
        // n.f2.accept(this);
        return _ret;
    }

    @Override
    public String visit(ClassDeclaration n, String a) {
        currentClass = n.f1.f0.tokenImage;
        CHA.put(currentClass, null);
        super.visit(n, currentClass);
        // currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, String a) {
        currentClass = n.f1.f0.tokenImage;
        CHA.put(currentClass, n.f3.f0.tokenImage);
        super.visit(n, currentClass);
        // currentClass = null;
        return null;
    }

    @Override
    public String visit(MethodDeclaration n, String a) {
        String methodName = n.f2.f0.toString();
        String className = currentClass;
        methodDetails.putIfAbsent(className, new HashSet<>());
        methodDetails.get(className).add(methodName);
        // System.out.println("Method: " + methodName + " in class: " + className);
        key = className + "." + methodName;
        // System.out.println(key + " statichelper method details");
        super.visit(n, a);
        key = null;
        return "";
    }

    @Override
    public String visit(ThisStoreStatement n, String a) {
        String _ret = null;
        n.f0.accept(this, a);
        n.f1.accept(this, a);
        n.f2.accept(this, a);
        n.f3.accept(this, a);
        n.f4.accept(this, a);
        n.f5.accept(this, a);
        methodthis.computeIfAbsent(key, k -> new HashMap<>())
                .put(currentClass, "th");
        // System.out.println(key + " -> " + "Var: " + currentClass + " Type: th");
        // methodBody.append("this").append(n.f1.tokenImage).append(n.f2.f0.toString()).append(n.f3.tokenImage)
        // .append(n.f4.accept(this)).append(n.f5.tokenImage + "\n");
        return _ret;
    }

    @Override
    public String visit(VarDeclaration n, String a) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);
        if (!(varType.equals("int") || varType.equals("boolean") || varType.equals("int[]"))) {
            methodthis.computeIfAbsent(key, k -> new HashMap<>())
                    .put(varType, varName);
            // System.out.println(key + " -> " + "Var: " + varName + " Type: " + varType);
        }
        objectType.put(varName, varType);
        // System.out.println("vardecl " + varName + " " + varType);
        // String ret = " " + varType + " " + varName + ";\n";
        // if (inmethod) {
        // methodBody.append(ret);
        // }
        return "";
    }

    @Override
    public String visit(AssignmentStatement n, String a) {
        String varname = n.f0.f0.toString();
        String varType = n.f2.accept(this, varname);
        // if(n.f2.choice instanceof PrimaryExpression)
        // System.out.println(" assign " + varname + varType);
        return null;
    }

    @Override
    public String visit(Expression n, String a) {
        String _ret = n.f0.accept(this, a);
        // System.out.println(_ret + " prim expr " + a);
        return _ret;
    }

    @Override
    public String visit(PrimaryExpression n, String a) {
        String _ret = n.f0.accept(this, a);
        // System.out.println(_ret + " prim expr");
        return _ret;
    }

    @Override
    public String visit(AllocationExpression n, String a) {
        // n.f0 is the literal "new" (we ignore it)
        // n.f1 is the Identifier node (the type name of the allocated object)
        // n.f2 is the literal "(" and n.f3 is the literal ")"

        // Get the type name (this might be used for debugging or type info)
        String typeName = n.f1.f0.toString();
        // System.out.println("current class " + currentClass + " " + a + "type in
        // alloc" + typeName);
        objectType.put(a, typeName);
        // System.out.println("alloc " + typeName + " " + a);
        // Generate a new object ID using a counter.
        // Make sure allocCount is declared in your visitor class.

        // typeEnvironment.put(lhs, rhsType);
        // Optionally, you can log or store the type information if needed.
        // For points-to analysis, we return the fresh object ID.
        return typeName;
    }

    @Override
    public String visit(MessageSend n, String a) {
        String _ret = null;
        String methodName = n.f2.f0.toString();
        n.f0.accept(this, a);
        n.f1.accept(this, a);
        n.f2.accept(this, a);
        n.f3.accept(this, a);
        n.f4.accept(this, a);
        n.f5.accept(this, a);

        callmethod.put(methodName, true);
        Set<String> visited = new HashSet<>();
        String receivere = n.f0.f0.toString();
        String receiverType = objectType.get(receivere);
        // resolveMethodBindings(receiverType, methodName, null, visited);
        // System.out.println("Method called: " + methodName + " receiver " +
        // n.f0.f0.toString());
        return _ret;
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof Identifier) {
            // methodthis.putIfAbsent(typeNode.f0.toString(), null);
            return ((Identifier) typeNode.f0.choice).f0.toString();
        } else if (typeNode.f0.choice instanceof ArrayType) {
            return "int[]";
        } else if (typeNode.f0.choice instanceof BooleanType) {
            return "boolean";
        } else if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        return "";
    }

    public void printThisVar() {
        for (Map.Entry<String, Map<String, String>> outerEntry : methodthis.entrySet()) {
            String outerKey = outerEntry.getKey();
            Map<String, String> innerMap = outerEntry.getValue();

            // If you want to handle the case where the inner map is null, add a check:
            if (innerMap != null) {
                for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                    String innerKey = innerEntry.getKey();
                    String value = innerEntry.getValue();

                    System.out.println("Outer key: " + outerKey +
                            ", Inner key: " + innerKey +
                            ", Value: " + value);
                }
            } else {
                System.out.println("Outer key: " + outerKey + " has a null inner map.");
            }
        }
    }

    public void printParentClassMap() {
        System.out.println("Final parentClassMap:");
        for (String className : CHA.keySet()) {
            String parent = CHA.get(className);
            if (parent == null) {
                System.out.println(className + " -> No Parent");
            } else {
                System.out.println(className + " -> " + parent);
            }
        }
    }
}
