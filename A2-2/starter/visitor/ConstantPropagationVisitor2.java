package visitor;

import syntaxtree.*;

import java.util.*;

public class ConstantPropagationVisitor2 extends GJNoArguDepthFirst<String> {
    private final Map<BB, Map<String, LatticeValue>> inMap;
    private final Map<BB, Map<String, LatticeValue>> outMap;
    private StringBuilder output = new StringBuilder();
    private HashMap<String, String> constantMap = new HashMap<>();
    HashMap<String, String> possibleMethods;
    HashMap<String, String> methodvalue;
    HashMap<String, List<String>> methodtable;
    HashMap<String, List<String>> methodtable1;
    private int indentLevel = 0;
    private BB currentBB;
    private String currentMethod;
    private String currentClass;
    private ProgramCFG cfg;
    public Map<String, BB> methodBBSet;

    public ConstantPropagationVisitor2(
            Map<BB, Map<String, LatticeValue>> outMap,
            Map<BB, Map<String, LatticeValue>> inMap, ProgramCFG cfg, HashMap<String, List<String>> methodtable,
            HashMap<String, List<String>> methodtable1, HashMap<String, String> methodvalue,
            HashMap<String, String> possibleMethods) {
        this.inMap = inMap;
        // this.currentBB = startBb;
        this.cfg = cfg;
        this.outMap = outMap;
        this.methodBBSet = cfg.methodBBSet;
        this.methodtable = methodtable;
        this.possibleMethods = possibleMethods;
        this.methodtable1 = methodtable1;
        this.methodvalue = methodvalue;
    }

    private void indent() {
        output.append("    ".repeat(indentLevel));
    }

    @Override
    public String visit(MainClass n) {
        String mainClassName = n.f1.f0.tokenImage;
        String mainMethodName = n.f6.tokenImage;
        output.append(n.f0.tokenImage).append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        indent();
        output.append(n.f3.tokenImage + " ").append(n.f4.tokenImage + " ").append(n.f5.tokenImage + " ")
                .append(n.f6.tokenImage).append(n.f7.tokenImage).append(n.f8.tokenImage).append(n.f9.tokenImage)
                .append(n.f10.tokenImage + " ").append(n.f11.f0.toString()).append(") {\n");
        indentLevel++;
        n.f14.accept(this);
        n.f15.accept(this);
        indentLevel--;
        indent();
        output.append("}\n");
        indentLevel--;
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(ClassDeclaration n) {
        currentClass = n.f1.f0.tokenImage;
        output.append(n.f0.toString() + " ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        n.f3.accept(this);
        n.f4.accept(this);
        indentLevel--;
        output.append("}\n");
        currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        currentClass = n.f1.f0.tokenImage + "_" + n.f3.f0.tokenImage;
        output.append(n.f0.toString() + " ").append(n.f1.f0.toString() + " ").append(n.f2.toString() + " ")
                .append(n.f3.f0.toString()).append(" {\n");
        indentLevel++;

        n.f5.accept(this);
        n.f6.accept(this);

        indentLevel--;
        output.append("}\n");
        currentClass = null;
        return null;
    }

    @Override
    public String visit(MethodDeclaration n) {
        currentMethod = n.f2.f0.tokenImage;
        currentBB = cfg.methodBBSet.get(currentClass + "_" + currentMethod);
        // System.out.println(currentBB.name + "current BB " + currentBB.outgoingEdges);
        output.append(n.f0.tokenImage + " ").append(extractFieldType(n.f1)).append(" ")
                .append(n.f2.f0.toString()).append("(");
        if (n.f4.present()) {
            n.f4.accept(this);
        }
        output.append(") {\n");
        indentLevel++;
        n.f7.accept(this);
        if (possibleMethods.size() >= 1 && currentClass == possibleMethods.get("bar") && currentMethod != "bar") {
            for (List<String> list : methodtable.values()) {
                for (String item : list) {
                    indent();
                    output.append(item + "\n");
                }
            }
        }

        n.f8.accept(this);
        if (possibleMethods.size() >= 1 && currentClass == possibleMethods.get("bar") && currentMethod != "bar") {
            for (List<String> list : methodtable1.values()) {
                for (String item : list) {
                    indent();
                    output.append(item + "\n");
                }
            }
            indent();
            output.append("return ").append(methodvalue.get("bar")).append(";\n");
            indentLevel--;
            indent();
            output.append("}\n");
            return null;
        }

        indent();
        output.append("return ").append(getConstantOrIdentifier(n.f10)).append(";\n");
        indentLevel--;
        indent();
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(Statement n) {
        String _ret = null;
        n.f0.accept(this);
        return _ret;
    }

    @Override
    public String visit(Block n) {
        List<BB> outBB = new ArrayList<>(currentBB.outgoingEdges);
        // System.out.println(outBB.size() + " size of out of " + currentBB.name);
        currentBB = outBB.get(0);
        // System.out.println(currentBB.name + " block of while");
        n.f1.accept(this);
        List<BB> outBB2 = new ArrayList<>(currentBB.outgoingEdges);
        currentBB = outBB2.get(0);
        // System.out.println("after accept" + currentBB.name);
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
        output.append(type + " " + varnam);
        return _ret;
    }

    @Override
    public String visit(FormalParameterRest n) {
        String _ret = null;

        String exp = n.f0.tokenImage;
        output.append(exp);
        n.f1.accept(this);
        return _ret;
    }

    @Override
    public String visit(VarDeclaration n) {
        indent();
        output.append(extractFieldType(n.f0)).append(" ")
                .append(n.f1.f0.toString()).append(";\n");
        return null;
    }

    @Override
    public String visit(AssignmentStatement n) {
        indent();
        String id = n.f0.f0.toString();
        String result = (expressionExtract(n.f2));
        // System.out.println("assignment " + id + result);
        output.append(id).append(" = ").append(result).append(";\n");
        return null;
    }

    @Override
    public String visit(ArrayAssignmentStatement n) {
        indent();
        String id = n.f0.f0.toString();
        String exp1 = n.f1.tokenImage;
        String id2 = n.f2.toString();
        String exp2 = n.f3.tokenImage;
        String exp3 = n.f4.tokenImage;
        String id3 = getConstantOrIdentifier(n.f5);
        String exp4 = n.f6.tokenImage;
        output.append(id).append(exp1).append(id2).append(exp2).append(" " + exp3 + " ").append(id3).append(exp4);
        return null;
    }

    @Override
    public String visit(PrintStatement n) {
        indent();
        output.append("System.out.println(")
                .append(getConstantOrIdentifier(n.f2))
                .append(");\n");
        return null;
    }

    @Override
    public String visit(FieldAssignmentStatement n) {
        indent();
        String id1 = n.f0.f0.toString();
        String exp1 = n.f1.tokenImage;
        String id2 = n.f2.f0.toString();
        String exp2 = n.f3.tokenImage;
        String id3 = getConstantOrIdentifier(n.f4);
        String exp3 = n.f5.tokenImage;
        output.append(id1).append(exp1).append(id2).append(" " + exp2 + " ").append(id3).append(exp3 + "\n");
        return null;
    }

    @Override
    public String visit(WhileStatement n) {
        List<BB> outBB2 = new ArrayList<>(currentBB.outgoingEdges);
        BB conditBb = outBB2.get(0);
        currentBB = conditBb;
        // System.out.println(currentBB.name + " while block");
        indent();
        String exp1 = n.f0.tokenImage;
        String exp2 = n.f1.tokenImage;
        String id1 = n.f2.f0.toString();
        String exp3 = n.f3.tokenImage;
        output.append(exp1 + " ").append(exp2).append(id1).append(exp3).append("{\n");
        indentLevel++;
        List<BB> outBB = new ArrayList<>(conditBb.outgoingEdges);
        currentBB = outBB.get(0);
        // System.out.println(currentBB.name + " while block true ");
        n.f4.accept(this);
        indentLevel--;
        indent();
        output.append("}\n");
        // List<BB> outBB1 = new ArrayList<>(currentBB.outgoingEdges);
        currentBB = outBB.get(1);
        // System.out.println(currentBB.name + " while block exit ");
        return null;
    }

    @Override
    public String visit(IfStatement n) {
        if (n.f0.choice instanceof IfthenElseStatement) {
            List<BB> outBB2 = new ArrayList<>(currentBB.outgoingEdges);
            BB conditionBB = outBB2.get(0);
            // System.out.println(outBB2 + " outbb in if ");
            currentBB = conditionBB;
            IfthenElseStatement ifthenelse = (IfthenElseStatement) n.f0.choice;
            indent();
            output.append(ifthenelse.f0.tokenImage + " " + ifthenelse.f1.tokenImage)
                    .append(ifthenelse.f2.f0.toString())
                    .append(ifthenelse.f3.tokenImage + " {\n");
            indentLevel++;
            List<BB> outBB = new ArrayList<>(conditionBB.outgoingEdges);
            currentBB = outBB.get(0);
            ifthenelse.f4.accept(this);
            indentLevel--;
            indent();
            output.append("} ");
            output.append(ifthenelse.f5.tokenImage + " {\n");
            indentLevel++;
            currentBB = outBB.get(1);
            ifthenelse.f6.accept(this);
            List<BB> outBB3 = new ArrayList<>(currentBB.outgoingEdges);
            currentBB = outBB3.get(0);
            indentLevel--;
            indent();
            output.append("}\n");
            return null;
        } else if (n.f0.choice instanceof IfthenStatement) {
            List<BB> outBB2 = new ArrayList<>(currentBB.outgoingEdges);
            // System.out.println(currentBB.name + " " + currentBB.outgoingEdges + " inside
            // if");
            BB conditionBB = outBB2.get(0);
            currentBB = conditionBB;
            // System.out.println("if block child " + currentBB.name +
            // conditionBB.outgoingEdges.size());
            IfthenStatement ifthen = (IfthenStatement) n.f0.choice;
            indent();
            output.append(ifthen.f0.tokenImage + " " +
                    ifthen.f1.tokenImage).append(ifthen.f2.f0.toString())
                    .append(ifthen.f3.tokenImage + "{\n");
            indentLevel++;
            List<BB> outBB = new ArrayList<>(conditionBB.outgoingEdges);
            // System.out.println(outBB + " before if");
            currentBB = outBB.get(1);
            // System.out.println(currentBB.name + " which side of block ");
            ifthen.f4.accept(this);
            currentBB = outBB.get(0);
            indentLevel--;
            indent();
            output.append("}\n");
            return null;
        }
        return null;

    }

    public String getOutput() {
        return output.toString();
    }

    private String extractFieldType(Type typeNode) {
        if (typeNode.f0.choice instanceof Identifier) {
            return ((Identifier) typeNode.f0.choice).f0.toString();
        }
        if (typeNode.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (typeNode.f0.choice instanceof BooleanType) {
            return "Boolean";
        }
        if (typeNode.f0.choice instanceof ArrayType) {
            return "int[]";
        }
        return null;
    }

    private String expressionExtract(Expression exprType) {
        if (exprType.f0.choice instanceof OrExpression) {
            OrExpression orExpr = (OrExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(orExpr.f0);
            String exp = " || ";
            String rightid = getConstantOrIdentifier(orExpr.f2);
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof AndExpression) {
            AndExpression andExpr = (AndExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(andExpr.f0);
            String exp = " && ";
            String rightid = getConstantOrIdentifier(andExpr.f2);
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof CompareExpression) {
            CompareExpression compExpr = (CompareExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(compExpr.f0);
            String exp = " <= ";
            String rightid = getConstantOrIdentifier(compExpr.f2);
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof neqExpression) {
            neqExpression neqExpr = (neqExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(neqExpr.f0);
            String exp = " != ";
            String rightid = getConstantOrIdentifier(neqExpr.f2);
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof PlusExpression) {
            PlusExpression plusExpr = (PlusExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(plusExpr.f0);
            String exp = " + ";
            String rightid = getConstantOrIdentifier(plusExpr.f2);
            if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
                int foldedValue = Integer.parseInt(leftid) + Integer.parseInt(rightid);
                return String.valueOf(foldedValue);
            }
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof MinusExpression) {
            MinusExpression minExpr = (MinusExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(minExpr.f0);
            String exp = " - ";
            String rightid = getConstantOrIdentifier(minExpr.f2);
            if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
                int foldedValue = Integer.parseInt(leftid) - Integer.parseInt(rightid);
                return String.valueOf(foldedValue);
            }
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof TimesExpression) {
            TimesExpression timesExpr = (TimesExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(timesExpr.f0);
            String exp = " * ";
            String rightid = getConstantOrIdentifier(timesExpr.f2);
            if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
                int foldedValue = Integer.parseInt(leftid) * Integer.parseInt(rightid);
                return String.valueOf(foldedValue);
            }
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof DivExpression) {
            DivExpression divExpr = (DivExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(divExpr.f0);
            String exp = " / ";
            String rightid = getConstantOrIdentifier(divExpr.f2);
            if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
                int foldedValue = Integer.parseInt(leftid) / Integer.parseInt(rightid);
                return String.valueOf(foldedValue);
            }
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof ArrayLookup) {
            ArrayLookup arrlp = (ArrayLookup) exprType.f0.choice;
            String leftid = arrlp.f0.f0.toString();
            String exp1 = "[";
            String rightid = getConstantOrIdentifier(arrlp.f2);
            String exp2 = "]";
            return leftid + exp1 + rightid + exp2;
        }
        if (exprType.f0.choice instanceof ArrayLength) {
            ArrayLength arrlgth = (ArrayLength) exprType.f0.choice;
            String leftid = arrlgth.f0.f0.toString();
            String exp = " . ";
            String rightid = arrlgth.f2.toString();
            return leftid + exp + rightid;
        }
        if (exprType.f0.choice instanceof PrimaryExpression) {
            return extractPrimaryExpression((PrimaryExpression) exprType.f0.choice);
        }
        if (exprType.f0.choice instanceof MessageSend) {
            MessageSend msg = (MessageSend) exprType.f0.choice;
            String leftid = msg.f0.f0.toString();
            String exp = ".";
            String rightid = msg.f2.f0.toString();
            String exp2 = msg.f3.tokenImage;
            String argListString = "";
            if (msg.f4.present()) {
                argListString = msg.f4.accept(this);// (a,b)
            }
            String exp4 = msg.f5.tokenImage;
            // System.out.print("msg send " + leftid + methodvalue.get(leftid));
            if (possibleMethods.containsKey(rightid)) {
                return methodvalue.get(rightid);
            }
            return leftid + exp + rightid + exp2 + argListString + exp4;
        }

        return "null";
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

    public String visit(ArgRest n) {
        return ", " + n.f1.f0.toString();
    }

    private String extractPrimaryExpression(PrimaryExpression pExpr) {

        if (pExpr.f0.choice instanceof IntegerLiteral) {
            IntegerLiteral integer = (IntegerLiteral) pExpr.f0.choice;
            return integer.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof TrueLiteral) {
            TrueLiteral T = (TrueLiteral) pExpr.f0.choice;
            return T.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof FalseLiteral) {
            FalseLiteral F = (FalseLiteral) pExpr.f0.choice;
            return F.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof Identifier) {
            Identifier id = (Identifier) pExpr.f0.choice;
            String varName = id.f0.tokenImage;
            Map<String, LatticeValue> env = outMap.get(currentBB);
            if (env != null && env.containsKey(varName)) {
                LatticeValue lv = env.get(varName);
                if (lv.getState() == LatticeValue.State.CONSTANT) {
                    return lv.getConstant().toString();
                }
            }
            return varName;
        }
        if (pExpr.f0.choice instanceof ThisExpression) {
            IntegerLiteral ths = (IntegerLiteral) pExpr.f0.choice;
            return ths.f0.tokenImage;
        }
        if (pExpr.f0.choice instanceof ArrayAllocationExpression) {
            ArrayAllocationExpression arrAlloExp = (ArrayAllocationExpression) pExpr.f0.choice;
            String exp1 = arrAlloExp.f0.tokenImage;
            String exp2 = arrAlloExp.f1.tokenImage;
            String exp3 = arrAlloExp.f2.tokenImage;
            String exp4 = getConstantOrIdentifier(arrAlloExp.f3);
            String exp5 = arrAlloExp.f4.tokenImage;

            return exp1 + " " + exp2 + exp3 + exp4 + exp5;
        }
        if (pExpr.f0.choice instanceof AllocationExpression) {
            AllocationExpression arrallo = (AllocationExpression) pExpr.f0.choice;
            String exp1 = arrallo.f0.tokenImage;
            String exp2 = arrallo.f1.f0.toString();
            String exp3 = arrallo.f2.tokenImage;
            String exp4 = arrallo.f3.tokenImage;
            return exp1 + " " + exp2 + exp3 + exp4;
        }
        if (pExpr.f0.choice instanceof NotExpression) {
            NotExpression integer = (NotExpression) pExpr.f0.choice;
            return integer.f0.tokenImage + integer.f1.f0.toString();
        }

        return "";
    }

    private String getConstantOrIdentifier(Identifier id) {
        String varName = id.f0.toString();
        Map<String, LatticeValue> env = outMap.get(currentBB);
        // System.out.println(env + " env in my ");
        if (env != null && env.containsKey(varName)) {
            LatticeValue lv = env.get(varName);
            if (lv.getState() == LatticeValue.State.CONSTANT) {
                // if (constantMap.containsKey(varName)) {
                // if (lv.getConstant().toString() == constantMap.get(varName)) {
                // return lv.getConstant().toString();
                // } else
                // return constantMap.get(varName).toString();
                // }
                return lv.getConstant().toString();
            }
            // return constantMap.get(varName).toString();
            // return varName;
        }
        return varName;
    } // System.out.print(value + " value " + id.f0.toString() + "\n`");
}
