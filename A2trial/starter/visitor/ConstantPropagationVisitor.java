package visitor;

import syntaxtree.*;
import java.util.Map;

public class ConstantPropagationVisitor extends GJNoArguDepthFirst<Node> {
    public Map<String, Integer> constantMap;
    private StringBuilder output = new StringBuilder();
    private int indentLevel = 0;

    public ConstantPropagationVisitor(Map<String, Integer> constantMap) {
        this.constantMap = constantMap;
        // System.out.println("Inside ConstantPropagationVisitor Constructor:");
        // if (constantMap.isEmpty()) {
        // System.out.println("constantMap is EMPTY.");
        // } else {
        // constantMap.forEach((key, value) -> System.out.println(key + " = " + value));
        // }
    }

    private void indent() {
        output.append("    ".repeat(indentLevel));
    }

    @Override
    public Node visit(MainClass n) {
        output.append("class ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        indent();
        output.append("public static void main(String[] ").append(n.f11.f0.toString()).append(") {\n");
        indentLevel++;

        n.f14.accept(this); // Variable declarations
        n.f15.accept(this); // Statements

        indentLevel--;
        indent();
        output.append("}\n");
        indentLevel--;
        output.append("}\n");
        return n;
    }

    @Override
    public Node visit(ClassDeclaration n) {
        output.append(n.f0.toString() + " ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;

        n.f3.accept(this); // Variable declarations
        n.f4.accept(this); // Method declarations

        indentLevel--;
        output.append("}\n");
        return n;
    }

    @Override
    public Node visit(ClassExtendsDeclaration n) {
        output.append(n.f0.toString() + " ").append(n.f1.f0.toString() + " ").append(n.f2.toString() + " ")
                .append(n.f3.toString()).append(" {\n");
        indentLevel++;

        n.f5.accept(this); // Variable declarations
        n.f6.accept(this); // Method declarations

        indentLevel--;
        output.append("}\n");
        return n;
    }

    @Override
    public Node visit(MethodDeclaration n) {
        // constantMap.clear();
        indent();
        output.append(n.f0.tokenImage + " ").append(extractFieldType(n.f1)).append(" ")
                .append(n.f2.f0.toString()).append("(");

        if (n.f4.present()) {
            n.f4.accept(this);
        }

        output.append(") {\n");
        indentLevel++;

        n.f7.accept(this); // Variable declarations
        n.f8.accept(this); // Statements

        indent();
        output.append("return ").append(getConstantOrIdentifier(n.f10)).append(";\n");

        indentLevel--;
        indent();
        output.append("}\n");
        return n;
    }

    @Override
    public Node visit(FormalParameterList n) {
        Node _ret = null;
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    @Override
    public Node visit(FormalParameter n) {
        Node _ret = null;
        String type = extractFieldType(n.f0);
        String varnam = n.f1.f0.toString();
        output.append(type + " " + varnam);
        return _ret;
    }

    @Override
    public Node visit(FormalParameterRest n) {
        Node _ret = null;

        String exp = n.f0.tokenImage;
        output.append(exp);
        n.f1.accept(this);

        return _ret;
    }

    @Override
    public Node visit(VarDeclaration n) {
        indent();
        output.append(extractFieldType(n.f0)).append(" ")
                .append(n.f1.f0.toString()).append(";\n");
        return n;
    }

    @Override
    public Node visit(AssignmentStatement n) {
        indent();
        String id = n.f0.f0.toString();
        String result = (expressionExtract(n.f2));
        // constantMap.put(id, result);
        output.append(id).append(" = ").append(result).append(";\n");

        return n;
    }

    @Override
    public Node visit(ArrayAssignmentStatement n) {
        indent();
        String id = n.f0.f0.toString();
        String exp1 = n.f1.tokenImage;
        String id2 = n.f2.toString();
        String exp2 = n.f3.tokenImage;
        String exp3 = n.f4.tokenImage;
        String id3 = n.f5.toString();
        String exp4 = n.f6.tokenImage;
        output.append(id).append(exp1).append(id2).append(exp2).append(" " + exp3 + " ").append(id3).append(exp4);
        return n;
    }

    @Override
    public Node visit(PrintStatement n) {
        indent();
        output.append("System.out.println(")
                .append(getConstantOrIdentifier(n.f2))
                .append(");\n");
        return n;
    }

    @Override
    public Node visit(FieldAssignmentStatement n) {
        indent();
        String id1 = n.f0.toString();
        String exp1 = n.f1.tokenImage;
        String id2 = n.f2.toString();
        String exp2 = n.f3.tokenImage;
        String id3 = n.f4.toString();
        String exp3 = n.f5.tokenImage;
        output.append(id1).append(exp1).append(id2).append(" " + exp2 + " ").append(id3).append(exp3 + "\n");
        return n;
    }

    @Override
    public Node visit(WhileStatement n) {
        indent();

        String exp1 = n.f0.tokenImage;
        String exp2 = n.f1.tokenImage;
        String id1 = n.f2.f0.toString();
        String exp3 = n.f3.tokenImage;
        output.append(exp1 + " ").append(exp2).append(id1).append(exp3).append("{\n");
        indentLevel++;
        n.f4.accept(this);
        indentLevel--;
        indent();
        output.append("}\n");
        return n;
    }

    @Override
    public Node visit(IfStatement n) {
        if (n.f0.choice instanceof IfthenElseStatement) {
            IfthenElseStatement ifthenelse = (IfthenElseStatement) n.f0.choice;
            indent();
            output.append(ifthenelse.f0.tokenImage + " " + ifthenelse.f1.tokenImage)
                    .append(ifthenelse.f2.f0.toString())
                    .append(ifthenelse.f3.tokenImage + " {\n");
            indentLevel++;
            ifthenelse.f4.accept(this);
            indentLevel--;
            indent();
            output.append("} ");
            output.append(ifthenelse.f5.tokenImage + " {\n");
            indentLevel++;
            ifthenelse.f6.accept(this);
            indentLevel--;
            indent();
            output.append("}\n");
            return n;
        } else if (n.f0.choice instanceof IfthenStatement) {
            IfthenStatement ifthen = (IfthenStatement) n.f0.choice;
            indent();
            output.append(ifthen.f0.tokenImage + " " +
                    ifthen.f1.tokenImage).append(ifthen.f2.f0.toString())
                    .append(ifthen.f3.tokenImage + "{\n");
            indentLevel++;
            ifthen.f4.accept(this);
            indentLevel--;
            indent();
            output.append("}\n");
            return n;
        }
        return n;

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
        // if (exprType.f0.choice instanceof PlusExpression) {
        // PlusExpression plusExpr = (PlusExpression) exprType.f0.choice;
        // String leftid = getConstantOrIdentifier(plusExpr.f0);
        // String exp = " + ";
        // String rightid = getConstantOrIdentifier(plusExpr.f2);
        // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
        // int foldedValue = Integer.parseInt(leftid) + Integer.parseInt(rightid);
        // return String.valueOf(foldedValue);
        // }
        // return leftid + exp + rightid;
        // }
        if (exprType.f0.choice instanceof PlusExpression) {
            PlusExpression plusExpr = (PlusExpression) exprType.f0.choice;
            String leftid = getConstantOrIdentifier(plusExpr.f0);
            String exp = " - ";
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
            String exp1 = arrlp.f1.tokenImage;
            String rightid = arrlp.f2.f0.toString();
            String exp2 = arrlp.f3.tokenImage;
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

            String exp3 = msg.f5.tokenImage;

            return leftid + exp + rightid + exp2 + exp3;
        }

        return "null";
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
            return id.f0.toString();
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
            String exp4 = arrAlloExp.f3.f0.toString();
            String exp5 = arrAlloExp.f0.tokenImage;

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
        Integer value = constantMap.get(id.f0.toString());
        // System.out.print(value + " value " + id.f0.toString() + "\n`");
        return value != null ? value.toString() : id.f0.toString();
    }

    public void constant() {
        System.out.println("Constant Variables:");
        if (constantMap.isEmpty()) {
            System.out.println("constantMap is EMPTY.");
        } else {
            constantMap.forEach((key, value) -> System.out.println(key + " = " + value));
        }
    }
}
