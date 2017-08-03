package com.lavinia.visitor;

import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.NodeSetEdit.Change;

abstract class NodeSetEditVisitor extends GenericVisitor {
	public abstract void visit(NodeSetEdit.Add add);

	public abstract void visit(NodeSetEdit.Remove remove);

	public abstract void visit(NodeSetEdit.Change<?> change);

	public final void visit(NodeSetEdit nodeSetEdit) {
		// safe to use `instanceof` because the class hierarchy is sealed
		if (nodeSetEdit instanceof NodeSetEdit.Add) {
			visit((NodeSetEdit.Add) nodeSetEdit);
		} else if (nodeSetEdit instanceof NodeSetEdit.Remove) {
			visit((NodeSetEdit.Remove) nodeSetEdit);
		} else if (nodeSetEdit instanceof NodeSetEdit.Change) {
			visit((Change<?>) nodeSetEdit);
		}
	}
}