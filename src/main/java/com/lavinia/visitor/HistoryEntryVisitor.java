package com.lavinia.visitor;

import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.delta.VariableTransaction;
import org.metanalysis.core.model.Node;

public abstract class HistoryEntryVisitor {
	public abstract void visit(Node.Type type, TypeTransaction transaction);

	public abstract void visit(Node.Variable variable, VariableTransaction transaction);

	public abstract void visit(Node.Function function, FunctionTransaction transaction);

	public final void visit(Node node, Transaction<?> transaction) {
		// safe to use `instanceof` because the class hierarchy is sealed
		if (node instanceof Node.Type) {
			visit((Node.Type) node, (TypeTransaction) transaction);
		} else if (node instanceof Node.Variable) {
			visit((Node.Variable) node, (VariableTransaction) transaction);
		} else if (node instanceof Node.Function) {
			visit((Node.Function) node, (FunctionTransaction) transaction);
		}
	}
}
