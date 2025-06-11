package visitor;

import java.util.*;
import syntaxtree.*;

public class prettyprinter extends GJNoArguDepthFirst<String> {
    // Mapping from variable names to the set of object IDs it may point to.
    private Map<String, Map<String, Map<String, Set<String>>>> pointsTo;
    private StringBuilder output1 = new StringBuilder();
    private Map<String, String> typeEnvironment;
    private Map<String, String> objectType;
    private Map<String, String> staticinfo;
    private String staticclass = null;
    private String currentClass;
    private String currentMethod;
    private Boolean isStatic = false;
    private int indentLevel = 0;
    private String methodstatic = null;
    private Map<String, String> chamedthod;
    private Map<String, String> classinfo;
    // Counter to generate fresh object IDs for new allocations.
    private int allocCount;

    public prettyprinter(Map<String, String> staticinfo, Map<String, String> chamedthod,
            Map<String, String> classinfo) {
        pointsTo = new HashMap<>();
        objectType = new HashMap<>();
        typeEnvironment = new HashMap<>();
        this.chamedthod = chamedthod;
        this.classinfo = classinfo;
        allocCount = 0;
        this.staticinfo = staticinfo;
    }

    public void addPointsTo(
            String functionKey,
            String blockKey,
            String variableKey,
            String target) {
        pointsTo
                .computeIfAbsent(functionKey, k -> new HashMap<>())
                .computeIfAbsent(blockKey, k -> new HashMap<>())
                .computeIfAbsent(variableKey, k -> new HashSet<>())
                .add(target);
    }

    // Returns the computed points-to mapping after analysis.
    public Map<String, Map<String, Map<String, Set<String>>>> getPointsTo() {
        return pointsTo;
    }

    /**
     * Checks whether 'value' is contained in the Set<String> at the specified
     * functionKey, blockKey, and varKey. Returns true if found; false otherwise.
     */
    public Set<String> getValuesForInnerKey(String searchKey) {
        // Create a new result set to accumulate all matching values.
        Set<String> result = new HashSet<>();

        // Iterate over the outermost level.
        for (Map<String, Map<String, Set<String>>> midMap : pointsTo.values()) {
            // Iterate over the second-level maps.
            for (Map<String, Set<String>> innerMap : midMap.values()) {
                // Check if the innermost map contains the key 'searchKey'
                if (innerMap.containsKey(searchKey)) {
                    // Add all elements from the found set to the result.
                    result.addAll(innerMap.get(searchKey));
                }
            }
        }
        return result;
    }

    private void indent() {
        output1.append("    ".repeat(indentLevel));
    }

    public String getOutput1() {
        return output1.toString();
    }

    @Override
    public String visit(Goal n) {
        String _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    @Override
    public String visit(MainClass n) {

        String mainClassName = n.f1.f0.tokenImage;
        String mainMethodName = n.f6.tokenImage;
        currentMethod = mainMethodName;
        output1.append(n.f0.tokenImage).append(" ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        indent();
        output1.append(n.f3.tokenImage + " ").append(n.f4.tokenImage + " ").append(n.f5.tokenImage + " ")
                .append(n.f6.tokenImage).append(n.f7.tokenImage).append(n.f8.tokenImage).append(n.f9.tokenImage)
                .append(n.f10.tokenImage + " ").append(n.f11.f0.toString()).append(") {\n");
        indentLevel++;
        n.f14.accept(this);
        n.f15.accept(this);
        indentLevel--;
        indent();
        output1.append("}\n");
        indentLevel--;
        output1.append("}\n");
        return null;
    }

    @Override
    public String visit(ClassDeclaration n) {
        currentClass = n.f1.f0.toString();
        output1.append(n.f0.toString() + " ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        n.f3.accept(this);
        n.f4.accept(this);
        indentLevel--;
        // System.err.println(isStatic);
        // System.out.println("cur " + currentClass);
        // System.out.println(classinfo.containsKey(currentClass));
        // System.out.println("stt " + staticclass);
        // System.out.println("eq " + currentClass.equals(staticclass));

        if (classinfo.containsKey(currentClass)) {
            // if (currentClass.equals(staticclass)) {
            String methodin;
            String method = classinfo.get(currentClass);
            String check = currentClass + "." + method;
            // System.out.println(currentClass + " method static " + check);

            // if (staticinfo.containsKey(methodstatic)) {
            if (staticinfo.containsKey(check)) {

                methodin = getStaticInfoValue(check);

                // System.err.println(staticinfo.containsKey(methodstatic));
                output1.append(methodin);
                // output1.append("static " + methodstatic + ";\n");
            }
            isStatic = false;
            // }
        }
        output1.append("}\n");
        currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        currentClass = n.f1.f0.toString();

        output1.append(n.f0.toString() + " ").append(n.f1.f0.toString() + " ").append(n.f2.toString() + " ")
                .append(n.f3.f0.toString()).append(" {\n");
        indentLevel++;

        n.f5.accept(this);
        n.f6.accept(this);

        indentLevel--;
        // System.err.println(staticclass);
        // System.out.println("in ext cur " + currentClass);
        // System.out.println("stt " + staticclass);
        // System.out.println("cur " + currentClass);
        // System.out.println(classinfo.containsKey(currentClass));
        if (classinfo.containsKey(currentClass)) {
            // System.out.println("inside class" + currentClass);
            String method = classinfo.get(currentClass);
            // System.out.println(method);
            String check = currentClass + "." + method;
            if (staticinfo.containsKey(check))
                output1.append(staticinfo.get(check));
            // output1.append("static " + methodstatic + ";\n");
            isStatic = false;
        }
        output1.append("}\n");
        currentClass = null;
        return null;
    }

    @Override
    public String visit(VarDeclaration n) {
        // indent();
        output1.append("   " + extractFieldType(n.f0)).append(" ")
                .append(n.f1.f0.toString()).append(";\n");
        String _ret = "";
        String type = n.f0.accept(this);
        String name = n.f1.f0.toString();

        if (type != null) {
            // System.out.println("In VarDeclaration: " + name + " " + type);
            objectType.putIfAbsent(name, type);
        }

        // addPointsTo(currentClass, currentMethod, name, type);
        n.f2.accept(this);
        return _ret;
    }

    @Override
    public String visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.toString();
        // System.out.println(currentBB.name + "current BB " + currentBB.outgoingEdges);
        output1.append(" " + n.f0.tokenImage + " ").append(extractFieldType(n.f1)).append(" ")
                .append(n.f2.f0.toString()).append("(");
        if (n.f4.present()) {
            n.f4.accept(this);
        }
        output1.append(") {\n");
        indentLevel++;
        n.f7.accept(this);
        int i = 0;
        n.f8.accept(this);

        indent();
        output1.append("return ").append(n.f10.accept(this)).append(";\n");
        n.f12.accept(this);
        indentLevel--;
        indent();
        output1.append("}\n");
        return null;
    }

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
        objectType.putIfAbsent(varnam, type);
        typeEnvironment.put(varnam, type);
        output1.append(type + " " + varnam);
        return _ret;
    }

    @Override
    public String visit(FormalParameterRest n) {
        String _ret = null;

        String exp = n.f0.tokenImage;
        output1.append(exp);
        n.f1.accept(this);
        return _ret;
    }

    @Override
    public String visit(Type n) {
        String _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public String visit(ArrayType n) {
        String _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    @Override
    public String visit(BooleanType n) {
        String _ret = null;
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    @Override
    public String visit(IntegerType n) {
        String _ret = null;
        n.f0.accept(this);
        return _ret;
    }

    @Override
    public String visit(Identifier n) {
        // Simply return the identifier's name.
        return n.f0.toString();
    }

    @Override
    public String visit(Statement n) {
        String _ret = null;
        n.f0.accept(this);
        return _ret;
    }

    @Override
    public String visit(FieldAssignmentStatement n) {
        String _ret = null;
        String lhs = n.f0.f0.toString() + "." + n.f2.f0.toString();
        String rhsType = n.f4.accept(this);
        // System.out.println("In fileds LHS: " + lhs);
        // Object rhsVal = n.f4.accept(this);
        // System.out.println("RHS: " + rhsVal);
        // String newObj = (String) rhsVal;
        // if (rhsVal != null) {
        // addPointsTo(currentClass, currentMethod, lhs, newObj);
        // }
        if (currentMethod != "main") {
            if (n.f4 instanceof PrimaryExpression) {
                PrimaryExpression pe = (PrimaryExpression) n.f4;
                if (pe.f0.choice instanceof AllocationExpression) {
                    // Only process and store the type if the expression is an allocation.
                    // This will call visit(AllocationExpression) and return the class
                    // name.
                    // output1.append(" " + lhs + " = " + "new " + rhsType + "()").append(";\n");
                    typeEnvironment.put(lhs, rhsType);
                    // System.out.println("Assignment in field: " + lhs + " is assigned type: " +
                    // rhsType);
                }
            }
        }

        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        output1.append("   " + lhs + " = " + rhsType).append(";\n");
        return _ret;
    }

    @Override
    public String visit(AssignmentStatement n) {
        // Get LHS identifier.
        String lhs = n.f0.f0.toString();
        // Process the right-hand side.

        // if (n.f2 instanceof Expression) {
        // For a new allocation, generate a fresh object ID.
        // Object rhsVal = n.f2.accept(this);
        // String newObj = (String) rhsVal;
        if (currentMethod != "main") {
            if (n.f2.f0.choice instanceof PrimaryExpression) {
                PrimaryExpression pe = (PrimaryExpression) n.f2.f0.choice;
                if (pe.f0.choice instanceof AllocationExpression) {
                    // Only process and store the type if the expression is an allocation.
                    String objID = n.f2.accept(this); // This will call visit(AllocationExpression) and return the clas
                    // name.
                    String result = objID.replaceAll("new|\\(|\\)|\\s+", "");
                    // System.out.println(result);
                    output1.append("   " + lhs + " = " + objID).append(";\n");
                    typeEnvironment.put(lhs, result);
                    return "";
                    // System.out.println("Assignment: " + lhs + " is assigned type: " + rhsType);
                }

            } else if (n.f2.f0.choice instanceof LoadStatement) {
                // If the right-hand side is an identifier, we can look up its type.
                LoadStatement ls = (LoadStatement) n.f2.f0.choice;
                String rhs1 = ls.f0.accept(this);
                String rhs2 = ls.f2.accept(this);
                String key = rhs1 + "." + rhs2;
                // System.out.println("In AssignmentStatement: " + lhs + " " + key);
                // output1.append(" " + lhs + " = " + key).append(";\n");
                String value = typeEnvironment.get(key);
                if (value != null) {
                    typeEnvironment.put(lhs, value);
                    // System.out.println("Assignment: " + lhs + " is assigned type: " + value);
                }
                output1.append(" " + lhs + " = " + key).append(";\n");
                return "";
            }
        }
        String rhs = n.f2.accept(this);
        output1.append("   " + lhs + " = " + rhs).append(";\n");

        return "";
    }

    @Override
    public String visit(PrintStatement n) {
        indent();
        output1.append("System.out.println(")
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
            output1.append(ifthenelse.f0.tokenImage + " " + ifthenelse.f1.tokenImage)
                    .append(ifthenelse.f2.f0.toString())
                    .append(ifthenelse.f3.tokenImage + " {\n");
            indentLevel++;
            Map<String, String> envBefore = new HashMap<>(typeEnvironment);
            // Process the condition:
            // f0: "if", f1: "(", f2: Identifier(), f3: ")"
            ifthenelse.f0.accept(this);
            ifthenelse.f1.accept(this);
            ifthenelse.f2.accept(this); // Process the condition (an Identifier)
            ifthenelse.f3.accept(this);

            // Process the 'then' branch (f4: Statement())
            Map<String, String> thenEnv = new HashMap<>(envBefore);
            Map<String, String> savedEnv = typeEnvironment; // Save current global environment.
            typeEnvironment = thenEnv; // Set the environment for the then branch.
            ifthenelse.f4.accept(this); // Visit the then branch.
            thenEnv = typeEnvironment; // Capture updated then environment.

            // Process the 'else' branch (f6: Statement())
            indentLevel--;
            indent();
            output1.append("} ");
            output1.append(ifthenelse.f5.tokenImage + " {\n");
            indentLevel++;
            Map<String, String> elseEnv = new HashMap<>(envBefore);
            typeEnvironment = elseEnv; // Set the environment for the else branch.
            ifthenelse.f6.accept(this); // Visit the else branch.
            elseEnv = typeEnvironment; // Capture updated else environment.

            // Merge the environments from the then and else branches.
            typeEnvironment = mergeEnvironments(thenEnv, elseEnv);
            indentLevel--;
            indent();
            output1.append("}\n");
            return null;
        } else if (n.f0.choice instanceof IfthenStatement) {
            IfthenStatement ifthen = (IfthenStatement) n.f0.choice;
            indent();
            output1.append(ifthen.f0.tokenImage + " " +
                    ifthen.f1.tokenImage).append(ifthen.f2.f0.toString())
                    .append(ifthen.f3.tokenImage + "{\n");
            indentLevel++;

            // System.out.println(outBB + " before if");

            // System.out.println(currentBB.name + " which side of block ");
            Map<String, String> envBefore = new HashMap<>(typeEnvironment);
            ifthen.f0.accept(this);
            ifthen.f1.accept(this);
            ifthen.f2.accept(this);
            ifthen.f3.accept(this);
            Map<String, String> thenEnv = new HashMap<>(envBefore);
            typeEnvironment = thenEnv; // Set the environment for the then branch.
            ifthen.f4.accept(this); // Visit the then branch.
            thenEnv = typeEnvironment; // Capture the updated environment from the then branch.
            // Merge the environment from before the if with the then branch environment.

            typeEnvironment = mergeEnvironments(envBefore, thenEnv);

            indentLevel--;
            indent();
            output1.append("}\n");
            return null;
        }
        return null;

    }

    @Override
    public String visit(WhileStatement n) {

        Map<String, String> envBefore = new HashMap<>(typeEnvironment);

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
        output1.append(exp1 + " ").append(exp2).append(id1).append(exp3).append("{\n");
        indentLevel++;
        Map<String, String> loopEnv = new HashMap<>(envBefore);
        typeEnvironment = loopEnv; // Set the environment for the loop body.
        n.f4.accept(this); // Visit the loop body statement.
        loopEnv = typeEnvironment; // Capture any changes made in the loop body.
        typeEnvironment = mergeEnvironments(envBefore, loopEnv);

        indentLevel--;
        indent();
        output1.append("}\n");
        // List<BB> outBB1 = new ArrayList<>(currentBB.outgoingEdges);
        // System.out.println(currentBB.name + " while block exit ");
        return "";
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
        output1.append(id).append(exp1).append(id2).append(exp2).append(" " + exp3 + " ").append(id3)
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
        output1.append("    this").append(n.f1.tokenImage).append(n.f2.f0.toString()).append(n.f3.tokenImage)
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
        // System.out.println("Receiver: " + receiverName);

        // Look up the receiver's type in our typeEnvironment.
        String receiverType = typeEnvironment.get(receiverName);
        // System.out.println(receiverName + " reciever");
        String ret = "";
        // For this example, we assume a method 'isMonomorphic' returns true
        // if the type is uniquely known (i.e. not "ambiguous").
        if (receiverType != null && isMonomorphic(receiverType)) {
            // If monomorphic, generate the devirtualized (static) call.
            // You would normally rewrite the AST here.
            // For demonstration, we print out what the transformation would be:
            // System.out.println("Transforming virtual call: "
            // + receiverName + "." + n.f2.f0.toString()
            // + "() --> Static call: " +
            isStatic = true;
            String part1 = null;
            String part2 = null;
            String resultclass = receiverType.replaceAll("new|\\(|\\)|\\s+", "");
            String key = receiverName + "." + currentClass;
            if (chamedthod.containsKey(key)) {
                String statcclass = chamedthod.get(key);

                int dotIndex = statcclass.indexOf('.');
                part1 = statcclass.substring(0, dotIndex); // "Even1"
                part2 = statcclass.substring(dotIndex + 1); // "foo"
                // System.out.println("part1 " + part1);
                // classinfo.put(statcclass, "yes");
                staticclass = part1;
            }

            String argListString = "";
            // System.out.println(receiverType + " static class");
            methodstatic = part1 + "." + part2;
            ret = resultclass + "." + n.f2.f0.toString() + "(" + "(" + resultclass + ") " + receiverName;
            if (n.f4.present()) {
                argListString = n.f4.accept(this);
                ret += ", " + argListString + ")";
            } else {
                ret += ")";
            }
            // If you are rewriting the AST, you would:
            // 1. Introduce a new temporary variable (or reuse the receiver) casted to
            // receiverType.
            // 2. Replace the call with a static method call like:
            // receiverType.bar(tempReceiver)
        } else {
            // If not monomorphic, leave the call as a virtual call.
            // System.out.println("Virtual call remains: "
            ret = receiverName + "." + n.f2.f0.toString() + "(";
            String argListString = "";
            if (n.f4.present()) {
                argListString = n.f4.accept(this);
                ret += argListString + ")";
            } else {
                ret += ")";
            }
        }

        // Visit children to continue traversal.
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);

        return ret;
    }

    @Override
    public String visit(ArgList n) {
        String firstArg = n.f0.accept(this);
        StringBuilder sb = new StringBuilder(firstArg);
        for (Node argRestNode : n.f1.nodes) {
            sb.append(argRestNode.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(ArgRest n) {
        return ", " + n.f1.accept(this);
    }

    @Override
    public String visit(PrimaryExpression n) {
        String _ret = n.f0.accept(this);
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
        return _ret;
    }

    @Override
    public String visit(FalseLiteral n) {
        String _ret = n.f0.tokenImage;
        return _ret;
    }

    @Override
    public String visit(ThisExpression n) {
        String _ret = n.f0.tokenImage;
        return _ret;
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

        // typeEnvironment.put(lhs, rhsType);
        // Optionally, you can log or store the type information if needed.
        // For points-to analysis, we return the fresh object ID.
        return "new " + typeName + "()";
    }

    // merge functions
    private Map<String, String> mergeEnvironments(Map<String, String> env1, Map<String, String> env2) {
        Map<String, String> merged = new HashMap<>();
        Set<String> keys = new HashSet<>();
        keys.addAll(env1.keySet());
        keys.addAll(env2.keySet());
        for (String key : keys) {
            String t1 = env1.get(key);
            String t2 = env2.get(key);
            if (t1 != null && t1.equals(t2)) {
                merged.put(key, t1);
            } else {
                // If the type differs or is missing in one branch, mark as ambiguous.
                merged.put(key, "ambiguous");
            }
        }
        return merged;
    }

    public void printdetails() {
        for (Map.Entry<String, String> entry : typeEnvironment.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public void printPointsTo() {
        System.out.println("=== Points-to Information ===");
        for (Map.Entry<String, Map<String, Map<String, Set<String>>>> functionEntry : pointsTo.entrySet()) {
            String functionName = functionEntry.getKey();
            Map<String, Map<String, Set<String>>> blockMap = functionEntry.getValue();

            System.out.println("Function: " + functionName);
            for (Map.Entry<String, Map<String, Set<String>>> blockEntry : blockMap.entrySet()) {
                String blockName = blockEntry.getKey();
                Map<String, Set<String>> varMap = blockEntry.getValue();

                System.out.println("  Block: " + blockName);
                for (Map.Entry<String, Set<String>> varEntry : varMap.entrySet()) {
                    String varName = varEntry.getKey();
                    Set<String> targets = varEntry.getValue();

                    // Print the variable -> set-of-targets mapping
                    System.out.println(" " + varName + " -> " + targets);
                }
            }
        }
    }

    private boolean isMonomorphic(String type) {
        return !type.equals("ambiguous");
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }
        if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (typeNode.f0.choice instanceof BooleanType) {
            return "boolean";
        }
        if (typeNode.f0.choice instanceof ArrayType) {
            return "int[]";
        }
        return null;
    }

    public String getStaticInfoValue(String key) {
        // Retrieve the StringBuilder associated with 'key'
        String sb = staticinfo.get(key);

        // Check if it exists, and convert it to a String if not null
        if (sb != null) {
            return sb.toString();
        } else {
            // Return null or an empty string if the key doesn't exist
            return null;
        }
    }

}
