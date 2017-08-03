package com.lavinia.visitor;

import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;

abstract class CodeVisitor extends GenericVisitor {
    public abstract void visit(SourceFile sourceFile);
    public abstract void visit(Node.Type type);
    public abstract void visit(Node.Variable variable);
    public abstract void visit(Node.Function function);
    
    public final void visit(Node node) {
        // safe to use `instanceof` because the class hierarchy is sealed
        if (node instanceof Node.Type) {
            visit((Node.Type) node);
        } else if (node instanceof Node.Variable) {
            visit((Node.Variable) node);
        } else if (node instanceof Node.Function) {
            visit((Node.Function) node);
        }
    }
}