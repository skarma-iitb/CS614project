package visitor;

import java.util.*;
import syntaxtree.*;

public class newmain {
    public static void optimize(Node root, ProgramCFG cfg) {
        // First pass: analyze constants using CFG
        ConstantAnalyzer analyzer = new ConstantAnalyzer(cfg);
        Map<String, Map<String, ConstantInfo>> constantInfo = analyzer.analyze();

        // Second pass: print with constant propagation
        newvist printer = new newvist(constantInfo);
        root.accept(printer);
        analyzer.printAnalysisResults();
    }
}
