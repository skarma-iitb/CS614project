package visitor;

import syntaxtree.*;
import java.util.*;

public class CPPrintingVisitor extends CFGGen {
    private final Map<BB, Map<String, LatticeValue>> outMap;
    private BB currentBB;
    private StringBuilder output = new StringBuilder();
    private int indentLevel = 0;
    private ProgramCFG cfg;
    public Map<String, BB> methodBBSet;
    private Stack<BB> blockStack = new Stack<>();

    private void indent() {
        output.append("    ".repeat(indentLevel));
    }

    public CPPrintingVisitor(Map<BB, Map<String, LatticeValue>> outMap, ProgramCFG cfg) {
        this.outMap = outMap;
        this.cfg = cfg;
    }

    @Override
    public String visit(MainClass n) {
        String mainClassName = n.f1.f0.tokenImage;
        String mainMethodName = n.f6.tokenImage;
        output.append("class ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        indent();
        output.append("public static void main(String[] ").append(n.f11.f0.toString()).append(") {\n");
        indentLevel++;
        // n.f14.accept(this); // Variable declarations
        // n.f15.accept(this);
        for (String methodName : cfg.methodBBSet.keySet()) {
            BB entryBB = cfg.methodBBSet.get(methodName);
            List<BB> bbs = getBasicBlocksInOrder(entryBB);
            for (BB bb : bbs) {
                currentBB = bb;
                printBasicBlock(bb);
            }
        }
        indentLevel--;
        indent();
        output.append("}\n");
        indentLevel--;
        output.append("}\n");
        return null;
    }

    @Override
    public String visit(ClassDeclaration n) {
        String className = n.f1.f0.tokenImage;

        // Initialize class
        // cfg.setActiveClass(className);

        output.append(n.f0.toString() + " ").append(n.f1.f0.toString()).append(" {\n");
        indentLevel++;
        // n.f3.accept(this); // Variable declarations
        // n.f4.accept(this); // Method declarations
        for (String methodName : cfg.methodBBSet.keySet()) {
            BB entryBB = cfg.methodBBSet.get(methodName);
            List<BB> bbs = getBasicBlocksInOrder(entryBB);
            for (BB bb : bbs) {
                currentBB = bb;
                printBasicBlock(bb);
            }
        }
        indentLevel--;
        output.append("}\n");
        return null;
    }
    // public void printProgram(ProgramCFG cfg) {

    // for (String methodName : cfg.methodBBSet.keySet()) {

    // System.out.println("class " + cfg.getClassName(methodName) + " {");
    // System.out.println(" public int " + methodName + "() {");
    // BB entryBB = cfg.methodBBSet.get(methodName);
    // List<BB> bbs = getBasicBlocksInOrder(entryBB);
    // for (BB bb : bbs) {
    // currentBB = bb;
    // printBasicBlock(bb);
    // }
    // System.out.println(" }");
    // System.out.println("}");
    // System.out.println();
    // }
    // }

    private void printBasicBlock(BB bb) {
        // Optionally, print a comment with the BB label:
        // System.out.println(" // Basic Block " + bb.getLabel());
        for (Instruction inst : bb.instructions) {
            printInstruction(inst);
        }
    }

    private void printInstruction(Instruction inst) {
        Node node = inst.getNode();
        if (node instanceof VarDeclaration) {
            printvar((VarDeclaration) node);
            return;
        }
        if (node instanceof AssignmentStatement) {
            printAssignment((AssignmentStatement) node);
            return;
        }
        if (node instanceof IfthenStatement) {
            // System.out.println("got if ");
            printif((IfthenStatement) node);
            return;
        }
        if (node instanceof IfthenElseStatement) {
            // System.out.println("got if ");
            printifels((IfthenElseStatement) node);
            return;
        }
        if (node instanceof WhileStatement) {
            // System.out.println(" inside the assign of print Indtruction ");
            printwhile((WhileStatement) node);
            return;
        }
        if (node instanceof PrintStatement) {
            // System.out.println(" inside the assign of print Indtruction ");
            printst((PrintStatement) node);
            return;
        }
        if (node instanceof ArrayAssignmentStatement) {
            // System.out.println(" inside the assign of print Indtruction ");
            printArray((ArrayAssignmentStatement) node);
            return;
        }
        if (node instanceof FieldAssignmentStatement) {
            // System.out.println(" inside the assign of print Indtruction ");
            printField((FieldAssignmentStatement) node);
            return;
        }

        return;
    }

    private String printAssignment(AssignmentStatement s) {

        String lhs = s.f0.f0.toString();
        String rhs = substituteExpression(s.f2);
        return lhs + " = " + rhs + ";";
    }

    private String printwhile(WhileStatement ws) {
        StringBuilder sb = new StringBuilder();
        sb.append("while(");
        sb.append(substituteExpression(ws.f2));
        sb.append(") ");
        sb.append(ws.f4.accept(this));
        return sb.toString();
    }

    private String printif(IfthenStatement s) {
        // Node ifNode = s.f0.choice;
        StringBuilder sb = new StringBuilder();

        // } else
        // if (ifNode instanceof IfthenStatement) {
        IfthenStatement its = (IfthenStatement) s;
        sb.append("if(");
        sb.append(substituteExpression(its.f2));
        sb.append(") ");
        // sb.append(its.f4.accept(this));
        // }
        return sb.toString();

    }

    private String printifels(IfthenElseStatement ie) {
        IfthenElseStatement its = (IfthenElseStatement) ie;
        StringBuilder sb = new StringBuilder();
        sb.append("if(");
        sb.append(substituteExpression(its.f2));
        sb.append(") ");
        sb.append(its.f4.accept(this));
        sb.append(" else ");
        sb.append(its.f6.accept(this));
        return sb.toString();
    }

    private String printst(PrintStatement ps) {
        StringBuilder sb = new StringBuilder();
        sb.append("System.out.println(");
        sb.append(substituteExpression(ps.f2));
        sb.append(");");
        return sb.toString();
    }

    private String printArray(ArrayAssignmentStatement aas) {
        StringBuilder sb = new StringBuilder();
        sb.append(aas.f0.toString());
        sb.append("[");
        sb.append(substituteExpression(aas.f2));
        sb.append("] = ");
        sb.append(substituteExpression(aas.f5));
        sb.append(";");
        return sb.toString();
    }

    private String printField(FieldAssignmentStatement fas) {
        StringBuilder sb = new StringBuilder();
        sb.append(fas.f0.toString());
        sb.append(".");
        sb.append(fas.f2.toString());
        sb.append(" = ");
        sb.append(substituteExpression(fas.f4));
        sb.append(";");
        return sb.toString();
    }

    private void printvar(VarDeclaration vd) {
        String varnam = vd.f1.f0.toString();
        String type = typecheck(vd.f0);
        output.append(type + " " + varnam + ";");
        return;
    }

    private String typecheck(Type typeNode) {
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

    private String substituteExpression(Node expr) {
        if (expr instanceof Identifier) {
            Identifier id = (Identifier) expr;
            String varName = id.f0.toString();
            Map<String, LatticeValue> env = outMap.get(currentBB);
            if (env != null && env.containsKey(varName)) {
                LatticeValue lv = env.get(varName);
                if (lv.getState() == LatticeValue.State.CONSTANT) {
                    return lv.getConstant().toString();
                }
            }
            return varName;
        }
        if (expr instanceof Expression) {
            Expression ep = (Expression) expr;
            Node choiceE = ep.f0.choice;
            if (choiceE instanceof PrimaryExpression) {
                PrimaryExpression pe = (PrimaryExpression) choiceE;
                Node choice = pe.f0.choice;
                // System.out.println(" indsie pre expr");
                if (choice instanceof Identifier) {
                    Identifier id = (Identifier) choice;
                    String varName = id.f0.toString();
                    Map<String, LatticeValue> env = outMap.get(currentBB);
                    if (env != null && env.containsKey(varName)) {
                        LatticeValue lv = env.get(varName);
                        if (lv.getState() == LatticeValue.State.CONSTANT) {
                            return lv.getConstant().toString();
                        }
                    }
                    return varName;
                }
                if (choice instanceof IntegerLiteral) {
                    IntegerLiteral id = (IntegerLiteral) choice;
                    String varName = id.f0.toString();
                    Map<String, LatticeValue> env = outMap.get(currentBB);
                    System.out.println(" env " + env);
                    if (env != null && env.containsKey(varName)) {
                        LatticeValue lv = env.get(varName);
                        if (lv.getState() == LatticeValue.State.CONSTANT) {
                            return lv.getConstant().toString();
                        }
                    }
                    return varName;
                }
                if (choice instanceof TrueLiteral) {
                    TrueLiteral id = (TrueLiteral) choice;
                    String varName = id.f0.toString();
                    Map<String, LatticeValue> env = outMap.get(currentBB);
                    System.out.println(" env " + env);
                    if (env != null && env.containsKey(varName)) {
                        LatticeValue lv = env.get(varName);
                        if (lv.getState() == LatticeValue.State.CONSTANT) {
                            return lv.getConstant().toString();
                        }
                    }
                    return varName;
                } else if (choice instanceof TrueLiteral) {
                    TrueLiteral tl = (TrueLiteral) choice;
                    return tl.f0.toString();
                } else if (choice instanceof FalseLiteral) {
                    FalseLiteral fl = (FalseLiteral) choice;
                    return fl.f0.toString();
                } else if (choice instanceof ThisExpression) {
                    ThisExpression te = (ThisExpression) choice;
                    return te.f0.toString();
                } else if (choice instanceof ArrayAllocationExpression) {
                    ArrayAllocationExpression aae = (ArrayAllocationExpression) choice;
                    String idd = substituteExpression(aae.f3);
                    String sts = "new" + " " + "int" + "[" + idd + "]";
                    return sts;
                } else if (choice instanceof AllocationExpression) {
                    AllocationExpression ae = (AllocationExpression) choice;
                    String idd = substituteExpression(ae.f3);
                    String sts = "new" + "(" + idd + ")";

                    return sts;
                } else if (choice instanceof NotExpression) {
                    NotExpression ne = (NotExpression) choice;
                    return "!" + substituteExpression(ne.f1);
                } else {
                    return choice.toString();
                }
            }
        }
        return expr.toString();
    }

    private String indent(String code) {
        String[] lines = code.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("            ").append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a list of basic blocks in an order covering the CFG,
     * using a simple breadth-first traversal.
     */
    private List<BB> getBasicBlocksInOrder(BB entry) {
        List<BB> order = new ArrayList<>();
        Set<BB> visited = new HashSet<>();
        Queue<BB> queue = new LinkedList<>();
        queue.add(entry);
        while (!queue.isEmpty()) {
            BB bb = queue.poll();
            if (visited.contains(bb))
                continue;
            visited.add(bb);
            order.add(bb);
            for (BB succ : bb.outgoingEdges) {
                queue.add(succ);
            }
        }
        return order;
    }

    public String getOutput() {
        return output.toString();
    }
    // Override other visit methods as needed so that the rest of the AST
    // is printed “as is” (or call super) when no constant substitution is needed.
}