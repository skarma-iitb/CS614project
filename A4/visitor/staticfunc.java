package visitor;

import java.util.*;
import syntaxtree.*;

public class staticfunc extends GJNoArguDepthFirst<String> {
    // private HashMap<String, HashMap<String, String>> objectTypes;
    public HashMap<String, String> classMethod;
    public Map<String, Boolean> call;
    public HashMap<String, HashMap<String, String>> methodthis;
    Map<String, Map<String, String>> thisvar;
    private String currentClass;
    private String currentMethod;
    private String object;
    private String classvar;
    private boolean inmethod = false;
    private int indentLevel = 0;
    public StringBuilder methodBody;
    public List<String> varreplace;

    public staticfunc(Map<String, Map<String, String>> thisvar, Map<String, Boolean> call, List<String> varreplace) {
        // objectTypes = new HashMap<>();
        // this.parentchild = new HashMap<>();
        this.classMethod = new HashMap<>();
        this.call = call;
        this.methodthis = new HashMap<>();
        this.thisvar = thisvar;
        this.varreplace = varreplace;
        this.methodBody = new StringBuilder();

    }

    private void indent() {
        methodBody.append("    ".repeat(indentLevel));
    }

    public Map<String, String> getClassMethod() {
        return classMethod;
    }

    @Override
    public String visit(NodeList n) {
        // String _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
            _count++;
        }
        return "";
    }

    @Override
    public String visit(NodeListOptional n) {
        if (n.present()) {
            // R _ret = null;
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                e.nextElement().accept(this);
                _count++;
            }
            return "";
        } else
            return "";
    }

    @Override
    public String visit(NodeOptional n) {
        if (n.present())
            return n.node.accept(this);
        else
            return "";
    }

    @Override
    public String visit(NodeSequence n) {
        // String _ret = null;
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
            _count++;
        }
        return "";
    }

    @Override
    public String visit(NodeToken n) {
        return "";
    }

    @Override
    public String visit(Goal n) {
        String _ret = null;
        // n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    @Override
    public String visit(TypeDeclaration n) {
        String _ret = null;
        n.f0.accept(this);
        return _ret;
    }

    @Override
    public String visit(ClassDeclaration n) {
        String className = n.f1.f0.toString();
        currentClass = className;
        // System.out.println("ClassName: " + className);
        // classMethod.putIfAbsent(currentClass, new HashSet<>());
        n.f3.accept(this);
        n.f4.accept(this);
        // methodBody.setLength(0);
        n.f5.accept(this);
        // classMethod.put(key, new StringBuilder(methodBody));

        // Clear the global methodBody so it can be reused for the next method
        // declaration
        // methodBody.setLength(0);
        return "";
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        String className = n.f1.f0.toString();
        currentClass = className;
        String parentClass = n.f3.f0.toString();
        // System.out.println("ClassName: " + className);
        // System.out.println("ParentClass: " + parentClass);
        // classMethod.putIfAbsent(currentClass, new HashSet<>());
        // parentchild.putIfAbsent(className, parentClass);
        super.visit(n);
        // methodBody.setLength(0);
        return "";
    }

    @Override
    public String visit(VarDeclaration n) {
        String varName = n.f1.f0.toString();
        String varType = extractFieldType(n.f0);

        String ret = "    " + varType + " " + varName + ";\n";

        if (inmethod) {
            if (varType.equals(currentClass)) {
                return "";
            }
            methodBody.append(ret);
        }
        return "";
    }

    @Override
    public String visit(MethodDeclaration n) {
        // StringBuilder methodBody = new StringBuilder();
        // methodBody.setLength(0);
        String methodName = n.f2.f0.toString();
        String className = currentClass;
        String key = className + "." + methodName;
        currentMethod = methodName;
        if (call.containsKey(currentMethod)) {
            methodBody.append(" " + n.f0.tokenImage + " static ").append(extractFieldType(n.f1)).append(" ")
                    .append(n.f2.f0.toString()).append("(");
            // System.out.println("MethodName: " + methodName);
            // System.out.println("ClassName: " + className);
            Map<String, String> innerMap = thisvar.get(key);
            // if (innerMap != null)
            // methodBody.append(currentClass + " " + innerMap.get(currentClass));
            methodBody.append(currentClass + " th");
            if (n.f4.present()) {
                methodBody.append(", ");
                n.f4.accept(this);
            }
            methodBody.append(") {\n");
            indentLevel++;
            inmethod = true;
            n.f7.accept(this);
            // classMethod.putIfAbsent(key, new StringBuilder());
            // classMethod.get(currentClass).add(methodName);
            // classMethod.(className, methodName);
            inmethod = false;
            n.f8.accept(this);
            indent();
            methodBody.append("return ").append(n.f10.accept(this)).append(";\n");
            n.f12.accept(this);
            indentLevel--;
            indent();
            methodBody.append("}\n");
        }
        // classMethod.put(key, methodBody);
        // methodBody.setLength(0);
        String result = methodBody.toString();

        classMethod.put(key, result);
        // Clear the global methodBody so it can be reused for the next method
        // declaration
        methodBody.setLength(0);
        return "";
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

    // @Override
    // public String visit(FormalParameterList n) {
    // String _ret = n.f0.accept(this) + " " + n.f1.accept(this);
    // return _ret;
    // }

    // @Override
    // public String visit(FormalParameter n) {

    // String type = extractFieldType(n.f0);
    // String varnam = n.f1.f0.toString();
    // String _ret = type + " " + varnam;
    // return _ret;
    // }

    // @Override
    // public String visit(FormalParameterRest n) {

    // String exp = n.f0.tokenImage + " " + n.f1.accept(this);
    // return exp;
    // }
    @Override
    public String visit(FormalParameterList n) {
        String _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    @Override
    public String visit(FormalParameter n) {
        String _ret = null;
        String type = extractFieldType(n.f0);
        String varnam = n.f1.f0.toString();
        methodBody.append(type + " " + varnam);
        return _ret;
    }

    @Override
    public String visit(FormalParameterRest n) {
        String _ret = null;

        String exp = n.f0.tokenImage;
        methodBody.append(exp + " ");
        n.f1.accept(this);
        return _ret;
    }

    @Override
    public String visit(Statement n) {
        n.f0.accept(this);
        return "";
    }

    @Override
    public String visit(AssignmentStatement n) {
        String ret = "";
        String lhs = n.f0.f0.toString();
        String rhs = n.f2.accept(this);
        if (rhs.equals("this")) {
            classvar = lhs;
            return "";
        }
        methodBody.append("   " + lhs + " = " + rhs).append(";\n");
        return ret;
    }

    @Override
    public String visit(PrintStatement n) {
        indent();
        methodBody.append("System.out.println(")
                .append(n.f2.accept(this))
                .append(");\n");
        return null;
    }

    @Override
    public String visit(IfStatement n) {
        if (n.f0.choice instanceof IfthenElseStatement) {

            // System.out.println(outBB2 + " outbb in if ");

            IfthenElseStatement ifthenelse = (IfthenElseStatement) n.f0.choice;
            indent();
            methodBody.append(ifthenelse.f0.tokenImage + " " + ifthenelse.f1.tokenImage)
                    .append(ifthenelse.f2.f0.toString())
                    .append(ifthenelse.f3.tokenImage + " {\n");
            indentLevel++;

            // f0: "if", f1: "(", f2: Identifier(), f3: ")"
            ifthenelse.f0.accept(this);
            ifthenelse.f1.accept(this);
            ifthenelse.f2.accept(this); // Process the condition (an Identifier)
            ifthenelse.f3.accept(this);
            ifthenelse.f4.accept(this); // Visit the then branch.

            // Process the 'else' branch (f6: Statement())
            indentLevel--;
            indent();
            methodBody.append("} ");
            methodBody.append(ifthenelse.f5.tokenImage + " {\n");
            indentLevel++;
            // Set the environment for the else branch.
            ifthenelse.f6.accept(this); // Visit the else branch.
            indentLevel--;
            indent();
            methodBody.append("}\n");
            return null;
        } else if (n.f0.choice instanceof IfthenStatement) {
            IfthenStatement ifthen = (IfthenStatement) n.f0.choice;
            indent();
            methodBody.append(ifthen.f0.tokenImage + " " +
                    ifthen.f1.tokenImage).append(ifthen.f2.f0.toString())
                    .append(ifthen.f3.tokenImage + "{\n");
            indentLevel++;

            // System.out.println(outBB + " before if");

            // System.out.println(currentBB.name + " which side of block ");

            ifthen.f0.accept(this);
            ifthen.f1.accept(this);
            ifthen.f2.accept(this);
            ifthen.f3.accept(this);
            // Set the environment for the then branch.
            ifthen.f4.accept(this); // Visit the then branch.

            indentLevel--;
            indent();
            methodBody.append("}\n");
            return null;
        }
        return null;

    }

    @Override
    public String visit(WhileStatement n) {

        // Process the while loop condition:
        // f0 -> "while", f1 -> "(", f2 -> Identifier(), f3 -> ")"
        n.f0.accept(this); // "while"
        n.f1.accept(this); // "("
        n.f2.accept(this); // the condition Identifier (doesn't change environment)
        n.f3.accept(this); // ")"

        // Process the loop body in a cloned environment.

        // System.out.println(currentBB.name + " while block");
        indent();
        String exp1 = n.f0.tokenImage;
        String exp2 = n.f1.tokenImage;
        String id1 = n.f2.f0.toString();
        String exp3 = n.f3.tokenImage;
        methodBody.append(exp1 + " ").append(exp2).append(id1).append(exp3).append("{\n");
        indentLevel++;
        // Map<String, String> loopEnv = new HashMap<>(envBefore);
        n.f4.accept(this); // Visit the loop body statement.

        indentLevel--;
        indent();
        methodBody.append("}\n");
        // List<BB> outBB1 = new ArrayList<>(currentBB.outgoingEdges);
        // System.out.println(currentBB.name + " while block exit ");
        return "";
    }

    @Override
    public String visit(FieldAssignmentStatement n) {
        String _ret = null;
        String lhs = n.f0.f0.toString() + "." + n.f2.f0.toString();
        String rhsType = n.f4.accept(this);
        if (n.f4 instanceof PrimaryExpression) {
            PrimaryExpression pe = (PrimaryExpression) n.f4;
            if (pe.f0.choice instanceof AllocationExpression) {
                // Only process and store the type if the expression is an allocation.
                // This will call visit(AllocationExpression) and return the class
                // name.
                methodBody.append("   " + lhs + " = " + "new " + rhsType + "()").append(";\n");
                // typeEnvironment.put(lhs, rhsType);
                // System.out.println("Assignment in field: " + lhs + " is assigned type: " +
                // rhsType);
            }
        }

        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        return _ret;
    }

    @Override
    public String visit(ArrayAssignmentStatement n) {
        indent();
        String id = n.f0.f0.toString();
        String exp1 = n.f1.tokenImage;
        String id2 = n.f2.accept(this);
        String exp2 = n.f3.tokenImage;
        String exp3 = n.f4.tokenImage;
        String id3 = (n.f5.accept(this));
        String exp4 = n.f6.tokenImage;
        methodBody.append(id).append(exp1).append(id2).append(exp2).append(" " + exp3 + " ").append(id3)
                .append(exp4 + "\n");
        return null;
    }

    @Override
    public String visit(ThisStoreStatement n) {
        String _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        methodBody.append("   th").append(n.f1.tokenImage).append(n.f2.f0.toString()).append(n.f3.tokenImage)
                .append(n.f4.accept(this)).append(n.f5.tokenImage + "\n");
        return _ret;
    }

    @Override
    public String visit(Expression n) {
        // Delegate to the chosen alternative.
        return n.f0.accept(this);
    }

    @Override
    public String visit(AndExpression n) {
        String left = n.f0.accept(this);
        String andOp = n.f1.accept(this); // Typically "&&"
        String right = n.f2.accept(this);
        // You can decide how to format the result. For now, simply concatenate:
        return left + " " + "&&" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "||"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(OrExpression n) {
        String left = n.f0.accept(this);
        String orOp = n.f1.accept(this); // Typically "||"
        String right = n.f2.accept(this);
        return left + " " + "||" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<="
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n) {
        String left = n.f0.accept(this);
        String cmpOp = n.f1.accept(this); // For example, "<="
        String right = n.f2.accept(this);
        return left + " " + "<=" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "!="
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(neqExpression n) {
        String left = n.f0.accept(this);
        String neqOp = n.f1.accept(this); // For example, "!="
        String right = n.f2.accept(this);
        return left + " " + "!=" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n) {
        String left = n.f0.accept(this);
        String plusOp = n.f1.accept(this); // For example, "+"
        String right = n.f2.accept(this);
        return left + " " + "+" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n) {
        String left = n.f0.accept(this);
        String minusOp = n.f1.accept(this); // For example, "-"
        String right = n.f2.accept(this);
        return left + " " + "-" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n) {
        String left = n.f0.accept(this);
        String timesOp = n.f1.accept(this); // For example, "*"
        String right = n.f2.accept(this);
        return left + " " + "*" + " " + right;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "/"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(DivExpression n) {
        String left = n.f0.accept(this);
        String divOp = n.f1.accept(this); // For example, "/"
        String right = n.f2.accept(this);
        return left + " " + "/" + " " + right;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n) {
        String id = n.f0.accept(this);
        String lbracket = n.f1.accept(this); // "["
        String index = n.f2.accept(this);
        String rbracket = n.f3.accept(this); // "]"
        return id + " [" + index + "]";
    }

    /**
     * f0 -> Identifier()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n) {
        String id = n.f0.accept(this);
        String dot = n.f1.accept(this);
        String length = n.f2.accept(this);
        return id + ".length";
    }

    /**
     * f0 -> "("
     * f1 -> Identifier()
     * f2 -> ")"
     * f3 -> Identifier()
     */
    @Override
    public String visit(TypeCast n) {
        String openParen = n.f0.accept(this); // "("
        String type = n.f1.accept(this);
        String closeParen = n.f2.accept(this); // ")"
        String expr = n.f3.accept(this);
        return "(" + type + ")" + expr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(LoadStatement n) {

        String primary = n.f0.accept(this);
        // String dot = n.f1.accept(this);
        String secondary = n.f2.f0.accept(this);
        if (varreplace.contains(primary)) {
            String ret = "th" + "." + secondary;
            return ret;
        }
        // System.out.println("In LoadStatement" + primary + "." + secondary);
        String ret = primary + "." + secondary;
        return ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ArgList() )?
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n) {
        // Assume that n.f0 is the receiver expression.
        // Here, we extract the receiver name (e.g. "o1").
        String receiverName = n.f0.f0.toString();
        String methodName = n.f2.f0.toString();
        String className = n.f0.f0.toString();
        String info = className + "." + methodName;
        call.putIfAbsent(info, Boolean.TRUE);
        // System.out.println("Receiver: " + receiverName);

        // Look up the receiver's type in our typeEnvironment.
        // String receiverType = typeEnvironment.get(receiverName);
        // For this example, we assume a method 'isMonomorphic' returns true
        // if the type is uniquely known (i.e. not "ambiguous").
        // if (receiverType != null && isMonomorphic(receiverType)) {
        // // If monomorphic, generate the devirtualized (static) call.
        // // You would normally rewrite the AST here.
        // // For demonstration, we print out what the transformation would be:
        // // System.out.println("Transforming virtual call: "
        // // + receiverName + "." + n.f2.f0.toString()
        // // + "() --> Static call: " +
        // ret = receiverType + "." + n.f2.f0.toString() + "(" + "(" + receiverType + ")
        // " + receiverName + ")";

        // // If you are rewriting the AST, you would:
        // // 1. Introduce a new temporary variable (or reuse the receiver) casted to
        // // receiverType.
        // // 2. Replace the call with a static method call like:
        // // receiverType.bar(tempReceiver)
        // } else {
        // If not monomorphic, leave the call as a virtual call.
        // System.out.println("Virtual call remains: "
        String argListString = "";
        if (n.f4.present()) {
            argListString = n.f4.accept(this);// (a,b)
        }
        String exp4 = n.f5.tokenImage;
        String ret = receiverName + "." + n.f2.f0.toString() + "(" +
                argListString + ")";
        // }

        // Visit children to continue traversal.

        return ret;
    }

    @Override
    public String visit(ArgList n) {
        String firstArg = n.f0.f0.toString();
        StringBuilder sb = new StringBuilder(firstArg);
        for (Node argRestNode : n.f1.nodes) {
            sb.append(argRestNode.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(ArgRest n) {
        return ", " + n.f1.f0.toString();
    }

    @Override
    public String visit(PrimaryExpression n) {
        String _ret = n.f0.accept(this);
        return _ret;
    }

    @Override
    public String visit(Identifier n) {
        String _ret = n.f0.tokenImage;
        return _ret;
    }

    @Override
    public String visit(IntegerLiteral n) {
        String _ret = n.f0.tokenImage;
        return _ret;
    }

    @Override
    public String visit(TrueLiteral n) {
        String _ret = n.f0.tokenImage;
        return "true";
    }

    @Override
    public String visit(FalseLiteral n) {
        String _ret = n.f0.tokenImage;
        return "false";
    }

    @Override
    public String visit(ThisExpression n) {
        String _ret = n.f0.tokenImage;
        return "this";
    }

    @Override
    public String visit(NotExpression n) {
        String _ret = n.f0.tokenImage;
        String _ret1 = n.f1.f0.toString();
        return _ret + _ret1;
    }

    @Override
    public String visit(ArrayAllocationExpression n) {
        String _ret = n.f3.f0.toString();
        return "new" + " int [" + _ret + "]";
    }

    @Override
    public String visit(AllocationExpression n) {
        // n.f0 is the literal "new" (we ignore it)
        // n.f1 is the Identifier node (the type name of the allocated object)
        // n.f2 is the literal "(" and n.f3 is the literal ")"

        // Get the type name (this might be used for debugging or type info)
        String typeName = n.f1.f0.toString();

        // Generate a new object ID using a counter.
        // Make sure allocCount is declared in your visitor class.
        String objID = "new " + typeName + "()";
        // typeEnvironment.put(lhs, rhsType);
        // Optionally, you can log or store the type information if needed.
        // For points-to analysis, we return the fresh object ID.
        return objID;
    }

    public void printClassMethodMap() {
        System.out.println("Final classMethodMap:");
        for (String className : classMethod.keySet()) {
            System.out.println(className + " -> " + classMethod.get(className));
        }
    }

    // public void printObjectType() {
    // System.out.println("Final ObjectType:");
    // // for (String className : objectTypes.keySet()) {
    // // System.out.println(className + " -> " + objectTypes.get(className));
    // // }
    // for (Map.Entry<String, HashMap<String, String>> outerEntry :
    // objectTypes.entrySet()) {
    // String outerKey = outerEntry.getKey();
    // HashMap<String, String> innerMap = outerEntry.getValue();
    // System.out.println("Outer Key: " + outerKey);
    // for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
    // System.out.println(" " + innerEntry.getKey() + " : " +
    // innerEntry.getValue());
    // }
    // }
    // }

    // public void printParentClassMap() {
    // System.out.println("Final parentClassMap:");
    // // for (Map.Entry<String, List<String>> entry : parentchild.entrySet()) {
    // // String parent = entry.getKey();
    // // List<String> children = entry.getValue();

    // // System.out.print("Parent: " + parent + " -> Children: ");
    // // for (String child : children) {
    // // System.out.print(child + " ");
    // // }
    // // System.out.println(); // Move to the next line after printing children
    // // }
    // for (String className : parentchild.keySet()) {
    // System.out.println(className + " -> " + classMethod.get(className));
    // }
    // }

    public void printClassMethod() {
        // Check if the map is not null to avoid NullPointerException

        for (Map.Entry<String, String> entry : classMethod.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    public Map<String, String> getInnerPairs(String outerKey) {
        // Check if the outer key exists in the map
        if (methodthis.containsKey(outerKey)) {
            // Return the inner map for the given outer key
            return methodthis.get(outerKey);
        }
        // Optionally, return null or an empty map if the outer key is not found
        return null; // or new HashMap<>();
    }

}
