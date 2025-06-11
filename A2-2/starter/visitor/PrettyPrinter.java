package visitor;

import syntaxtree.*;
import visitor.*;
import java.util.*;

public class PrettyPrinter extends GJNoArguDepthFirst<String> {
    private int indent = 0;

    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    @Override
    public String visit(NodeList n) {
        String ret = "";
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            ret += e.nextElement().accept(this);
        }
        return ret;
    }

    @Override
    public String visit(NodeListOptional n) {
        if (n.present()) {
            String ret = "";
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                ret += e.nextElement().accept(this);
            }
            return ret;
        }
        return "";
    }

    @Override
    public String visit(NodeOptional n) {
        if (n.present())
            return n.node.accept(this);
        return "";
    }

    @Override
    public String visit(NodeSequence n) {
        String ret = "";
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            ret += e.nextElement().accept(this);
        }
        return ret;
    }

    @Override
    public String visit(NodeToken n) {
        return n.tokenImage;
    }

    @Override
    public String visit(Goal n) {
        return n.f1.accept(this) + n.f2.accept(this);
    }

    @Override
    public String visit(MainClass n) {
        String ret = "class " + n.f1.accept(this) + " {\n";
        indent++;
        ret += getIndent() + "public static void main (String[] " + n.f11.accept(this) + ") {\n";
        indent++;
        ret += n.f14.accept(this);
        ret += n.f15.accept(this);
        indent--;
        ret += getIndent() + "}\n";
        indent--;
        ret += "}\n\n";
        return ret;
    }

    @Override
    public String visit(ClassDeclaration n) {
        String ret = "class " + n.f1.accept(this) + " {\n";
        indent++;
        ret += n.f3.accept(this);
        ret += n.f4.accept(this);
        indent--;
        ret += "}\n";
        return ret;
    }

    @Override
    public String visit(ClassExtendsDeclaration n) {
        String ret = "class " + n.f1.accept(this) + " extends " + n.f3.accept(this) + " {\n";
        indent++;
        ret += n.f5.accept(this);
        ret += n.f6.accept(this);
        indent--;
        ret += "}\n";
        return ret;
    }

    @Override
    public String visit(VarDeclaration n) {
        return getIndent() + n.f0.accept(this) + " " + n.f1.accept(this) + ";\n";
    }

    @Override
    public String visit(MethodDeclaration n) {
        String ret = getIndent() + "public " + n.f1.accept(this) + " " + n.f2.accept(this) + "(";
        if (n.f4.present())
            ret += n.f4.accept(this);
        ret += ") {\n";
        indent++;
        ret += n.f7.accept(this);
        ret += n.f8.accept(this);
        ret += getIndent() + "return " + n.f10.accept(this) + ";\n";
        indent--;
        ret += getIndent() + "}\n\n";
        return ret;
    }

    @Override
    public String visit(Statement n) {
        return getIndent() + n.f0.accept(this);
    }

    @Override
    public String visit(Block n) {
        String ret = "{\n";
        indent++;
        ret += n.f1.accept(this);
        indent--;
        ret += getIndent() + "}\n";
        return ret;
    }

    @Override
    public String visit(AssignmentStatement n) {
        return getIndent() + n.f0.accept(this) + " = " + n.f2.accept(this) + ";\n";
    }

    @Override
    public String visit(ArrayAssignmentStatement n) {
        return getIndent() + n.f0.accept(this) + "[" + n.f2.accept(this) + "] = " +
                n.f5.accept(this) + ";\n";
    }

    @Override
    public String visit(IfStatement n) {
        return n.f0.accept(this);
    }

    @Override
    public String visit(WhileStatement n) {
        String ret = getIndent() + "while (" + n.f2.accept(this) + ") ";
        ret += n.f4.accept(this);
        return ret;
    }

    @Override
    public String visit(PrintStatement n) {
        return getIndent() + "System.out.println(" + n.f2.accept(this) + ");\n";
    }

    @Override
    public String visit(Expression n) {
        return n.f0.accept(this);
    }

    @Override
    public String visit(AndExpression n) {
        return n.f0.accept(this) + " && " + n.f2.accept(this);
    }

    @Override
    public String visit(CompareExpression n) {
        return n.f0.accept(this) + " <= " + n.f2.accept(this);
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
    public String visit(ArrayLookup n) {
        return n.f0.accept(this) + "[" + n.f2.accept(this) + "]";
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
        return "true";
    }

    @Override
    public String visit(FalseLiteral n) {
        return "false";
    }
}