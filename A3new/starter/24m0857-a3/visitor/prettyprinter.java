package visitor;

import syntaxtree.*;
import java.util.*;

public class prettyprinter extends GJNoArguDepthFirst<String> {
    Map<String, MethodAllocationInfo> methodDetails;
    Map<String, Allocate> allocation;
    Map<String, String> vartype;
    StringBuilder output;
    String methodname;
    int memoryreq = 0;
    int registers;
    StringBuilder Argulist = new StringBuilder();
    StringBuilder formallist = new StringBuilder();
    boolean registersPrinted;
    int i;

    public prettyprinter(Map<String, Allocate> allo, Map<String, String> vartype,
            Map<String, MethodAllocationInfo> methodDetails, int registers) {
        this.allocation = allo;
        this.vartype = vartype;
        output = new StringBuilder();
        this.registers = registers;
        this.methodDetails = methodDetails;
        for (Allocate alloc : allocation.values()) {
            if (alloc.spilled) {
                memoryreq++;
            }
        }
        // System.out.println("Inside pretty printer: " + registers);
    }

    public String visit(NodeList n) {
        String _ret = "";
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
            _count++;
        }
        return _ret;
    }

    public String visit(NodeListOptional n) {
        if (n.present()) {
            String _ret = "";
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                e.nextElement().accept(this);
                _count++;
            }
            return _ret;
        } else
            return "";
    }

    public String visit(NodeOptional n) {
        if (n.present())
            return n.node.accept(this);
        else
            return "";
    }

    public String visit(NodeSequence n) {
        String _ret = "";
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            e.nextElement().accept(this);
            _count++;
        }
        return _ret;
    }

    public String visit(NodeToken n) {
        return "";
    }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> <REGLIMIT>
     * f1 -> MainClass()
     * f2 -> ( TypeDeclaration() )*
     * f3 -> <EOF>
     */
    @Override
    public String visit(Goal n) {
        String _ret = "";
        output.append(n.f0.toString() + "\n" + "import static a3.Memory.*;" + "\n");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n) {
        String _ret = "";
        output.append(
                n.f0 + " " + n.f1.f0.toString() + " {" + "\n" + " " + n.f3 + " " + n.f4 + " " + n.f5 + " " + n.f6 + n.f7
                        + n.f8 + n.f9 + n.f10 + " " + n.f11.f0.toString() + n.f12 + " " + n.f13 + "\n");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        n.f6.accept(this);
        n.f7.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        n.f10.accept(this);
        n.f11.accept(this);
        n.f12.accept(this);
        n.f13.accept(this);
        registersPrinted = false;
        methodname = n.f6.toString();
        if (methodDetails.containsKey(methodname)) {
            MethodAllocationInfo info = methodDetails.get(methodname);
            for (int i = 0; i < registers; i++) {
                output.append("    Object R" + i + ";" + "\n");
            }
            output.append("    alloca(" + info.spilledCount + ");" + "\n");
            registersPrinted = true;
        }
        n.f14.accept(this);
        // n.f15.accept(this);
        // n.f16.accept(this);
        i = 0;
        n.f15.accept(this);
        output.append(" }" + "\n" + "}" + "\n");
        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     * | ClassExtendsDeclaration()
     */
    @Override
    public String visit(TypeDeclaration n) {
        String _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n) {
        String _ret = "";
        output.append(n.f0 + " " + n.f1.f0.toString() + " " + "{" + "\n");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        i = 0;
        n.f4.accept(this);
        // n.f5.accept(this);
        output.append("\n" + "}" + "\n");
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n) {
        String _ret = "";
        output.append(n.f0 + " " + n.f1.f0.toString() + " " + n.f2 + " " + n.f3.f0.toString() + "{" + "\n");
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        n.f4.accept(this);
        n.f5.accept(this);
        i = 0;
        n.f6.accept(this);
        output.append("\n" + "}" + "\n");
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public String visit(VarDeclaration n) {
        String _ret = null;
        String varname = n.f1.f0.toString();
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);

        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Identifier()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n) {
        StringBuilder _ret = new StringBuilder();
        output.append(
                " " + n.f0.toString() + " " + extractype(n.f1) + " " + n.f2.f0.toString() + "(");
        if (n.f4.present()) {
            output.append(n.f4.accept(this));
        }
        output.append(")" + "{\n");
        methodname = n.f2.f0.toString();
        registersPrinted = false;
        if (methodDetails.containsKey(methodname)) {
            MethodAllocationInfo info = methodDetails.get(methodname);
            // System.out.println(methodname);
            for (int i = 0; i < registers; i++) {
                output.append("  Object R" + i + ";" + "\n");
            }
            output.append("  alloca(" + info.spilledCount + ");" + "\n");
            registersPrinted = true;
            // return _ret.toString();
        }

        n.f7.accept(this);
        // n.f8.accept(this);
        n.f8.accept(this);
        n.f9.accept(this);
        // n.f10.accept(this);
        output.append("  return " + n.f10.accept(this) + ";" + "\n" + " }");
        return _ret.toString();
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    @Override
    public String visit(FormalParameterList n) {
        // String _ret = null;
        formallist.append(n.f0.accept(this));
        if (n.f1.present()) {
            // formallist.append(",");
            n.f1.accept(this);
        }
        return formallist.toString();
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n) {
        StringBuilder _ret = new StringBuilder();
        // System.out.println("inside the formal ");
        _ret.append(extractype(n.f0) + " " + n.f1.f0.toString());
        return _ret.toString();
    }

    private String extractype(Type n) {
        if (n.f0.choice instanceof ArrayType) {
            return "int" + "[" + "]";
        }
        if (n.f0.choice instanceof BooleanType) {
            return "boolean";
        }
        if (n.f0.choice instanceof IntegerType) {
            return "int";
        }
        if (n.f0.choice instanceof FloatType) {
            return "float";
        }
        if (n.f0.choice instanceof Identifier) {
            Identifier id = (Identifier) n.f0.choice;
            return id.f0.toString();
        }
        return "";

    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterRest n) {
        StringBuilder _ret = new StringBuilder();
        formallist.append("," + n.f1.accept(this));
        return _ret.toString();
    }

    /**
     * f0 -> Block()
     * | AssignmentStatement()
     * | ArrayAssignmentStatement()
     * | FieldAssignmentStatement()
     * | IfStatement()
     * | WhileStatement()
     * | PrintStatement()
     * | LivenessQueryStatement()
     */
    @Override
    public String visit(Statement n) {
        String _ret = "";

        n.f0.accept(this);

        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public String visit(Block n) {
        String _ret = "";
        // n.f0.accept(this);
        n.f1.accept(this);
        // n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n) {
        StringBuilder _ret = new StringBuilder();
        String varName = n.f0.f0.toString();
        Allocate alloc = allocation.get(varName);
        if (alloc != null && alloc.spilled) {
            output.append(" store(" + alloc.spillIndex + ",");
            String rhs = n.f2.accept(this);
            output.append(rhs + ");" + "\n");
            // if (alloc != null && alloc.spilled) {
            // MethodAllocationInfo info = methodDetails.get(methodname);
            // int value = info.spilledCount;
            // // System.out.println(methodname + " methods " + info.spilledCount);
            // output.append(" store(" + alloc.spillIndex + ",");
            // if (i < value) {
            // i++;
            // }
            // String rhs = n.f2.accept(this);
            // output.append(rhs + ");" + "\n");
        } else if (alloc != null) {
            // Variable is allocated to a register: generate a register assignment.
            output.append("    " + alloc.register + " = ");
            String rhs = n.f2.accept(this);
            output.append(rhs + ";" + "\n");
        } else {
            _ret.append(n.f0.accept(this) +
                    n.f1.accept(this) +
                    n.f2.accept(this) +
                    n.f3.accept(this));
        }
        return _ret.toString();
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Identifier()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Identifier()
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n) {
        String _ret = "";
        output.append("   " + n.f0.accept(this) + "[" + n.f2.accept(this) + "] = " + n.f5.accept(this) + ";");
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "="
     * f4 -> Identifier()
     * f5 -> ";"
     */
    @Override
    public String visit(FieldAssignmentStatement n) {
        String _ret = "";
        output.append("   " + n.f0.accept(this) + " . " + n.f2.accept(this) + " = " + n.f4.accept(this) + ";");
        return _ret;
    }

    /**
     * f0 -> IfthenElseStatement()
     * | IfthenStatement()
     */
    @Override
    public String visit(IfStatement n) {
        String _ret = "";
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(IfthenStatement n) {
        String _ret = "";
        output.append("  if(" + n.f2.accept(this) + "){\n" + n.f4.accept(this) + " }\n");
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public String visit(IfthenElseStatement n) {
        String _ret = "";
        output.append(
                "  if(" + n.f2.accept(this) + "){\n");
        n.f4.accept(this);
        output.append("  }" + " else { \n");
        n.f6.accept(this);
        output.append(" }\n");
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n) {
        String _ret = null;
        output.append("  while(");
        output.append(n.f2.accept(this));
        output.append("){\n");
        n.f4.accept(this);
        output.append("    }\n");
        // output.append(_ret);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Identifier()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n) {
        String _ret = "    " + "System.out.println(" + n.f2.accept(this) + ")" + ";\n";
        output.append(_ret);
        return _ret;
    }

    /**
     * f0 -> <SCOMMENT1>
     * f1 -> <LIVENESSQUERY>
     * f2 -> <SCOMMENT2>
     */
    @Override
    public String visit(LivenessQueryStatement n) {
        String _ret = " f";
        // n.f0.accept(this) + n.f1.accept(this) + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> OrExpression()
     * | AndExpression()
     * | CompareExpression()
     * | neqExpression()
     * | PlusExpression()
     * | MinusExpression()
     * | TimesExpression()
     * | DivExpression()
     * | ArrayLookup()
     * | ArrayLength()
     * | MessageSend()
     * | PrimaryExpression()
     */
    @Override
    public String visit(Expression n) {
        String _ret = n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "&&"
     * f2 -> Identifier()
     */
    @Override
    public String visit(AndExpression n) {
        String _ret = n.f0.accept(this) + " && " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "||"
     * f2 -> Identifier()
     */
    @Override
    public String visit(OrExpression n) {
        String _ret = n.f0.accept(this) + " || " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "<="
     * f2 -> Identifier()
     */
    @Override
    public String visit(CompareExpression n) {
        String _ret = n.f0.accept(this) + " <= " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "!="
     * f2 -> Identifier()
     */
    @Override
    public String visit(neqExpression n) {
        String _ret = n.f0.accept(this) + " != " +
                n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "+"
     * f2 -> Identifier()
     */
    @Override
    public String visit(PlusExpression n) {
        String _ret = n.f0.accept(this) + " + " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "-"
     * f2 -> Identifier()
     */
    @Override
    public String visit(MinusExpression n) {
        String _ret = n.f0.accept(this) + " - " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "*"
     * f2 -> Identifier()
     */
    @Override
    public String visit(TimesExpression n) {
        String _ret = n.f0.accept(this) + " * " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "/"
     * f2 -> Identifier()
     */
    @Override
    public String visit(DivExpression n) {
        String _ret = n.f0.accept(this) + " / " + n.f2.accept(this);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Identifier()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n) {
        String _ret = n.f0.accept(this) + "[" + n.f2.accept(this) + "]";
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n) {
        String _ret = n.f0.accept(this) + "." + "length";
        return _ret;
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
        // StringBuilder _ret = new StringBuilder();
        Argulist.append(n.f0.accept(this) + "." + n.f2.f0.tokenImage + "(");
        if (n.f4.present()) {
            // Argulist.append(n.f4.accept(this));
            n.f4.accept(this);
        }
        Argulist.append(")");
        return Argulist.toString();
    }

    /**
     * f0 -> Identifier()
     * f1 -> ( ArgRest() )*
     */
    @Override
    public String visit(ArgList n) {
        StringBuilder _ret = new StringBuilder();
        Argulist.append(n.f0.accept(this));
        if (n.f1.present()) {
            // Argulist.append(",");
            n.f1.accept(this);
        }
        return _ret.toString();
    }

    /**
     * f0 -> ","
     * f1 -> Identifier()
     */
    @Override
    public String visit(ArgRest n) {
        String _ret = "";
        n.f0.accept(this);
        Argulist.append("," + n.f1.accept(this));
        // System.out.println("inside argresr " + _ret);
        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     * | FloatLiteral()
     * | TrueLiteral()
     * | FalseLiteral()
     * | Identifier()
     * | ThisExpression()
     * | ArrayAllocationExpression()
     * | AllocationExpression()
     * | NotExpression()
     */
    @Override
    public String visit(PrimaryExpression n) {
        // String _ret = null;
        String rhs = n.f0.accept(this);
        return rhs;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public String visit(IntegerLiteral n) {
        String _ret = (n.f0.tokenImage);
        // n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> <FLOAT_LITERAL>
     */
    @Override
    public String visit(FloatLiteral n) {
        String _ret = (n.f0.tokenImage);
        // n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "true"
     */
    @Override
    public String visit(TrueLiteral n) {
        String _ret = (n.f0.tokenImage);
        // n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "false"
     */
    @Override
    public String visit(FalseLiteral n) {
        String _ret = (n.f0.tokenImage);
        // n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public String visit(Identifier n) {
        String varName = (n.f0.toString());
        StringBuilder sb = new StringBuilder();
        Allocate alloc = allocation.get(varName);
        if (alloc != null && alloc.spilled) {
            if (vartype.containsKey(varName)) {
                String type;
                type = vartype.get(varName);
                sb.append(" (" + "(" + type + ") " + "load(" + alloc.spillIndex + ")" + ")");
            }
        } else if (alloc != null) {
            if (vartype.containsKey(varName)) {
                String type;
                type = vartype.get(varName);
                sb.append("((" + type + ") " + alloc.register + ")");
            }

        }
        return sb.toString();
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n) {
        String _ret = (n.f0.tokenImage);
        // n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Identifier()
     * f4 -> "]"
     */
    @Override
    public String visit(ArrayAllocationExpression n) {
        String _ret = "new " + "int [" +
                n.f3.accept(this) + "]";
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n) {
        // String _ret = ("new " + n.f1.f0.toString() + "()");

        // n.f0.accept(this);
        // n.f1.accept(this);
        // n.f2.accept(this);
        // n.f3.accept(this);
        String _ret = ("new " + n.f1.f0.toString() + "()");
        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Identifier()
     */
    @Override
    public String visit(NotExpression n) {
        String _ret = "!" + n.f1.accept(this);
        return _ret;
    }

    public StringBuilder getoutput() {
        return output;
    }

}
