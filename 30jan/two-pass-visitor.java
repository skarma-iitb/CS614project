// First pass visitor to collect symbols
public class SymbolCollectorVisitor extends GJDepthFirst<Type, SymbolTable> {
    @Override
    public Type visit(ClassDeclaration n, SymbolTable symbolTable) {
        String className = n.f1.f0.toString();
        // Store class info
        symbolTable.addClass(className);
        
        // Visit fields
        for (Node field : n.f3.nodes) {
            Type fieldType = field.accept(this, symbolTable);
            // Store field info with its type
        }
        
        // Visit methods
        for (Node method : n.f4.nodes) {
            Type methodType = method.accept(this, symbolTable);
            // Store method info with return type and parameters
        }
        
        return new ClassType(className);
    }
    
    @Override
    public Type visit(MethodDeclaration n, SymbolTable symbolTable) {
        String methodName = n.f2.f0.toString();
        Type returnType = n.f1.accept(this, symbolTable);
        
        // Create new scope for method
        symbolTable.enterScope();
        
        // Visit parameters
        for (Node param : n.f4.nodes) {
            Type paramType = param.accept(this, symbolTable);
            // Store parameter info
        }
        
        // Visit local variable declarations
        for (Node varDecl : n.f7.nodes) {
            Type varType = varDecl.accept(this, symbolTable);
            // Store variable info
        }
        
        symbolTable.exitScope();
        return returnType;
    }
    
    @Override
    public Type visit(VarDeclaration n, SymbolTable symbolTable) {
        String varName = n.f1.f0.toString();
        Type varType = n.f0.accept(this, symbolTable);
        symbolTable.addVariable(varName, varType);
        return varType;
    }
}

// Second pass visitor to analyze statements
public class StatementAnalyzer extends GJDepthFirst<Void, SymbolTable> {
    private HashMap<String, List<String>> references = new HashMap<>();
    
    @Override
    public Void visit(MessageSend n, SymbolTable symbolTable) {
        String caller = n.f0.f0.toString();
        String methodName = n.f2.f0.toString();
        
        // Resolve caller type using symbol table
        Type callerType = symbolTable.getType(caller);
        
        // Record method call
        String reference = caller + "." + methodName;
        String resolved = callerType + "." + methodName;
        addReference(reference, resolved);
        
        return null;
    }
    
    @Override
    public Void visit(FieldReference n, SymbolTable symbolTable) {
        String object = n.f0.f0.toString();
        String field = n.f2.f0.toString();
        
        // Resolve object type using symbol table
        Type objectType = symbolTable.getType(object);
        
        // Record field reference
        String reference = object + "." + field;
        String resolved = objectType + "." + field;
        addReference(reference, resolved);
        
        return null;
    }
    
    private void addReference(String reference, String resolved) {
        references.computeIfAbsent(reference, k -> new ArrayList<>())
                 .add(resolved);
    }
    
    public void printReferences() {
        // Print references in required format
        references.entrySet().stream()
                 .sorted(Map.Entry.comparingByKey())
                 .forEach(entry -> {
                     System.out.println(entry.getKey() + " = " + entry.getValue());
                 });
    }
}

// Helper class to store symbol information
class SymbolTable {
    private Stack<HashMap<String, Type>> scopes = new Stack<>();
    private HashMap<String, ClassInfo> classes = new HashMap<>();
    
    public void enterScope() {
        scopes.push(new HashMap<>());
    }
    
    public void exitScope() {
        scopes.pop();
    }
    
    public void addClass(String className) {
        classes.put(className, new ClassInfo());
    }
    
    public void addVariable(String name, Type type) {
        scopes.peek().put(name, type);
    }
    
    public Type getType(String name) {
        // Look up type in current and enclosing scopes
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Type type = scopes.get(i).get(name);
            if (type != null) return type;
        }
        return null;
    }
}
