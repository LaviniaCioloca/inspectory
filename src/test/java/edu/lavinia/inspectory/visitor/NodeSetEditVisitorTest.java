package edu.lavinia.inspectory.visitor;

import java.util.HashSet;

import org.junit.Test;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Type;

import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.KClassImpl;

public class NodeSetEditVisitorTest {
	@Test
	public void testVisitNode() {
		NodeSetEditVisitor visitor = new EditVisitor(null);
		Node node = new Node.Type("test", new HashSet<>(), new HashSet<>(), new HashSet<>());
		NodeSetEdit nodeSetEdit = new NodeSetEdit.Add(node);
		visitor.visit(nodeSetEdit);

		KClass<? extends Node> kclassNode = new KClassImpl<Node.Type>(Node.Type.class);
		nodeSetEdit = new NodeSetEdit.Remove(kclassNode, "");
		visitor.visit(nodeSetEdit);

		KClass<Type> kclassType = new KClassImpl<Node.Type>(Type.class);
		nodeSetEdit = new NodeSetEdit.Change<Node.Type>(kclassType, "", new TypeTransaction());
		visitor.visit(nodeSetEdit);
	}
}
