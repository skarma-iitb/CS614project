package visitor;

import syntaxtree.*;

public class JavaCodePrinter<R> extends GJNoArguDepthFirst<String> {

    // Helper method to concatenate child nodes' results
    private String concat(NodeListOptional n) {
        StringBuilder result = new StringBuilder();
        for (Node node : n.nodes) {
            result.append(node.accept(this));
        }
        return result.toString();
    }

    @Override
    public String visit(ClassDeclaration n) {
        String classKeyword = n.f0.accept(this);
        String className = n.f1.accept(this);
        String openBrace = n.f2.accept(this);
        String body = concat(n.f3);
        String closeBrace = n.f4.accept(this);
        String optionalSemicolon = n.f5.accept(this);

        return classKeyword + " " + className + " " + openBrace + "\n" +
                body + "\n" +
                closeBrace + optionalSemicolon + "\n";
    }

    @Override
    public String visit(MethodDeclaration n) {
        String modifiers = n.f0.accept(this);
        String returnType = n.f1.accept(this);
        String methodName = n.f2.accept(this);
        String parameters = n.f4.accept(this);
        String block = n.f7.accept(this);

        return modifiers + returnType + " " + methodName + "(" + parameters + ") " + block;
    }

    @Override
    public String visit(Block n) {
        return "{" + "\n" + concat(n.f1) + "\n" + "}";
    }

    @Override
    public String visit(Statement n) {
        return n.f0.accept(this);
    }

    @Override
    public String visit(PrintStatement n) {
        return "System.out.println(" + n.f2.accept(this) + ");";
    }

    @Override
    public String visit(AssignmentStatement n) {
        return n.f0.accept(this) + " = " + n.f2.accept(this) + ";";
    }

    @Override
    public String visit(Expression n) {
        return n.f0.accept(this);
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
    public String visit(NodeToken n) {
        return n.tokenImage;
    }

}