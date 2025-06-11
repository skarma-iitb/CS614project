package visitor;

import syntaxtree.*;
import java.util.*;

public class Newcpvisitor extends CFGGen implements GJNoArguVisitor<String> {
    private final Map<BB, Map<String, LatticeValue>> inMap;
    private final Map<BB, Map<String, LatticeValue>> outMap;
    private StringBuilder output = new StringBuilder();
    private int indentLevel = 0;
    private BB currentBB;

    public Newcpvisitor(ProgramCFG cfg, Map<BB, Map<String, LatticeValue>> inMap,
            Map<BB, Map<String, LatticeValue>> outMap) {
        this.cfg = cfg;
        this.inMap = inMap;
        this.outMap = outMap;
    }

    private void indent() {
        output.append("    ".repeat(indentLevel));
    }

    private List<BB> getBasicBlocksInOrder(BB entry) {
        List<BB> order = new ArrayList<>();
        Set<BB> visited = new HashSet<>();
        Queue<BB> queue = new LinkedList<>();
        queue.add(entry);
        while (!queue.isEmpty()) {
            BB bb = queue.poll();
            if (bb == null)
                continue;
            if (visited.contains(bb))
                continue;
            visited.add(bb);
            order.add(bb);
            for (BB succ : bb.outgoingEdges) {
                if (succ != null)
                    queue.add(succ);
            }
        }
        return order;
    }

    @Override
    public String visit(Goal n) {
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        return null;
    }

    @Override
    public String visit(MainClass n) {
        // Print the main class header.
        output.append(n.f0.toString()).append(n.f1.f0.tokenImage).append(" {\n");
        indentLevel++;
        indent();
        output.append(n.f3.tokenImage).append(" " + n.f4.tokenImage).append(" " + n.f5.tokenImage)
                .append(" " + n.f6.tokenImage).append(n.f7.tokenImage).append(n.f8.tokenImage).append(n.f9.tokenImage)
                .append(n.f10.tokenImage).append(" " + n.f11.f0.tokenImage)
                .append(n.f12.tokenImage + " {\n");
        indentLevel++;
        n.f14.accept(this); // Variable declarations.
        n.f15.accept(this); // Statements.

        indentLevel--;
        indent();
        output.append("}\n");
        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(ClassDeclaration n) {
        output.append("class ").append(n.f1.f0.tokenImage).append(" {\n");
        indentLevel++;
        cfg.setActiveClass(n.f1.f0.tokenImage);
        n.f3.accept(this); // Variable declarations.
        n.f4.accept(this); // Method declarations.
        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        output.append("class ").append(n.f1.f0.tokenImage)
                .append(" extends ").append(n.f3.f0.tokenImage)
                .append(" {\n");
        indentLevel++;
        cfg.setActiveClass(n.f1.f0.tokenImage + "_" + n.f3.f0.tokenImage);
        n.f5.accept(this); // Variable declarations.
        n.f6.accept(this); // Method declarations.
        indentLevel--;
        output.append("}\n\n");
        return null;
    }

    @Override
    public String visit(MethodDeclaration n) {
        // Print the method header.
        String methodName = n.f2.f0.tokenImage;

        indent();
        output.append(n.f0.tokenImage + " ").append(extractFieldType(n.f1)).append(" ")
                .append(n.f2.f0.toString()).append("(");
        if (n.f4.present()) {
            n.f4.accept(this);
        }
        output.append(") {\n");
        indentLevel++;
        currentBB = cfg.methodBBSet.get(methodName);
        if (currentBB == null) {
            System.err.println("currentBB is null for method: " + methodName);
        } else {
            System.out.println("Entry Basic Block: " + currentBB.getLabel());
        }
        List<BB> bbs = getBasicBlocksInOrder(currentBB);
        System.out.println("Number of basic blocks: " + bbs.size());
        for (BB bb : bbs) {
            currentBB = bb;
            indent();
            // Optionally, print a comment for the basic block (for debugging purposes)
            output.append("// In Basic Block: ").append(bb.getLabel()).append("\n");
            // Iterate over each instruction in this basic block.
            for (Instruction inst : bb.instructions) {
                indent();
                System.out.println("inside instruction ");
                output.append(printInstruction(inst)).append("\n");
            }
        }
        // System.out.println(startBB + " blocks in method");
        // n.f7.accept(this); // Variable declarations
        // n.f8.accept(this); // Statements
        indent();
        output.append("return ").append(n.f10.accept(this)).append(";\n");
        indentLevel--;
        indent();
        output.append("}\n\n");
        return null;
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
        String lhs = n.f0.accept(this);
        String rhs = substituteExpression(n.f2);
        // output.append(lhs).append(" = ").append(rhs).append(";\n");
        return lhs + " = " + rhs + ";";
    }

    @Override
    public String visit(IfStatement n) {
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
            return null;
        } else if (n.f0.choice instanceof IfthenStatement) {
            // Create a new block for the condition.
            BB conditionBB = new BB();
            cfg.addEdgeFromTo(cfg.currentBB, conditionBB);

            // Optionally, push a dummy instruction representing the condition.
            conditionBB.pushInstruction(new Instruction(n)); // n represents the if-statement condition.
            BB thenBB = new BB();
            cfg.addEdgeFromTo(conditionBB, thenBB);
            cfg.setCurrentBB(thenBB);
            IfthenStatement ifthen = (IfthenStatement) n.f0.choice;
            indent();
            output.append(ifthen.f0.tokenImage + " " +
                    ifthen.f1.tokenImage).append(ifthen.f2.f0.toString())
                    .append(ifthen.f3.tokenImage + "{\n");
            indentLevel++;
            ifthen.f4.accept(this);
            indentLevel--;
            indent();
            BB nextBB = new BB();
            cfg.addEdgeFromTo(cfg.currentBB, nextBB);
            cfg.setCurrentBB(nextBB);

            output.append("}\n");
            return null;
        }
        return null;

    }

    @Override
    public String visit(WhileStatement n) {
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
        return null;
    }

    @Override
    public String visit(PrintStatement n) {
        indent();
        output.append("System.out.println(").append(n.f2.accept(this)).append(");\n");
        return null;
    }

    @Override
    public String visit(Identifier n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(IntegerLiteral n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(TrueLiteral n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(FalseLiteral n) {
        return n.f0.tokenImage;
    }

    @Override
    public String visit(PlusExpression n) {
        return n.f0.accept(this) + " + " + n.f2.accept(this);
    }

    @Override
    public String visit(MinusExpression n) {
        return n.f0.accept(this) + " - " + n.f2.accept(this);
    }

    @Override
    public String visit(TimesExpression n) {
        return n.f0.accept(this) + " * " + n.f2.accept(this);
    }

    @Override
    public String visit(DivExpression n) {
        return n.f0.accept(this) + " / " + n.f2.accept(this);
    }

    private String substituteExpression(Expression expr) {
        System.out.println(" substitute ");
        if (expr.f0.choice instanceof PrimaryExpression) {
            return extractPrimaryExpression((PrimaryExpression) expr.f0.choice);
        }
        // if (expr.f0.choice instanceof OrExpression) {
        // OrExpression orExpr = (OrExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(orExpr.f0);
        // String exp = " || ";
        // String rightid = getConstantOrIdentifier(orExpr.f2);
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof AndExpression) {
        // AndExpression andExpr = (AndExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(andExpr.f0);
        // String exp = " && ";
        // String rightid = getConstantOrIdentifier(andExpr.f2);
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof CompareExpression) {
        // CompareExpression compExpr = (CompareExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(compExpr.f0);
        // String exp = " <= ";
        // String rightid = getConstantOrIdentifier(compExpr.f2);
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof neqExpression) {
        // neqExpression neqExpr = (neqExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(neqExpr.f0);
        // String exp = " != ";
        // String rightid = getConstantOrIdentifier(neqExpr.f2);
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof PlusExpression) {
        // PlusExpression plusExpr = (PlusExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(plusExpr.f0);
        // String exp = " + ";
        // String rightid = getConstantOrIdentifier(plusExpr.f2);
        // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
        // int foldedValue = Integer.parseInt(leftid) + Integer.parseInt(rightid);
        // return String.valueOf(foldedValue);
        // }
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof MinusExpression) {
        // MinusExpression minExpr = (MinusExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(minExpr.f0);
        // String exp = " - ";
        // String rightid = getConstantOrIdentifier(minExpr.f2);
        // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
        // int foldedValue = Integer.parseInt(leftid) - Integer.parseInt(rightid);
        // return String.valueOf(foldedValue);
        // }
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof TimesExpression) {
        // TimesExpression timesExpr = (TimesExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(timesExpr.f0);
        // String exp = " * ";
        // String rightid = getConstantOrIdentifier(timesExpr.f2);
        // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
        // int foldedValue = Integer.parseInt(leftid) * Integer.parseInt(rightid);
        // return String.valueOf(foldedValue);
        // }
        // return leftid + exp + rightid;
        // }
        // if (expr.f0.choice instanceof DivExpression) {
        // DivExpression divExpr = (DivExpression) expr.f0.choice;
        // String leftid = getConstantOrIdentifier(divExpr.f0);
        // String exp = " / ";
        // String rightid = getConstantOrIdentifier(divExpr.f2);
        // if (leftid.matches("-?\\d+") && rightid.matches("-?\\d+")) {
        // int foldedValue = Integer.parseInt(leftid) / Integer.parseInt(rightid);
        // return String.valueOf(foldedValue);
        // }
        // return leftid + exp + rightid;
        // }
        if (expr.f0.choice instanceof ArrayLookup) {
            ArrayLookup arrlp = (ArrayLookup) expr.f0.choice;
            String leftid = arrlp.f0.f0.toString();
            String exp1 = arrlp.f1.tokenImage;
            String rightid = arrlp.f2.f0.toString();
            String exp2 = arrlp.f3.tokenImage;
            return leftid + exp1 + rightid + exp2;
        }
        if (expr.f0.choice instanceof ArrayLength) {
            ArrayLength arrlgth = (ArrayLength) expr.f0.choice;
            String leftid = arrlgth.f0.f0.toString();
            String exp = " . ";
            String rightid = arrlgth.f2.toString();
            return leftid + exp + rightid;
        }
        return expr.accept(this);
    }

    /**
     * printInstruction: prints an Instruction node.
     * It extracts the underlying node (from Instruction.instructionNode) and then
     * uses the appropriate visitor method.
     */
    private String printInstruction(Instruction inst) {
        // Get the underlying AST node from the instruction.
        Node node = inst.instructionNode;

        // Dispatch based on the type of the node.
        if (node instanceof AssignmentStatement) {
            return ((AssignmentStatement) node).accept(this);
        } else if (node instanceof IfthenStatement) {
            return ((IfthenStatement) node).accept(this);
        } else if (node instanceof IfthenElseStatement) {
            return ((IfthenElseStatement) node).accept(this);
        } else if (node instanceof WhileStatement) {
            return ((WhileStatement) node).accept(this);
        } else if (node instanceof PrintStatement) {
            return ((PrintStatement) node).accept(this);
        }
        // Fallback: use the node's default string representation.
        return node.toString();
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
        // if (pExpr.f0.choice instanceof Identifier) {
        // Identifier id = (Identifier) pExpr.f0.choice;
        // return id.f0.toString();
        // }
        if (pExpr.f0.choice instanceof Identifier) {
            Identifier id = (Identifier) pExpr.f0.choice;
            String varName = id.f0.tokenImage;
            Map<String, LatticeValue> env = inMap.get(currentBB);
            if (env != null && env.containsKey(varName)) {
                LatticeValue lv = env.get(varName);
                if (lv.getState() == LatticeValue.State.CONSTANT) {
                    System.out.println("value is identifer" + lv.getConstant().toString());
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

    public String getOutput() {
        return output.toString();
    }
}
