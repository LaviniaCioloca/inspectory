package edu.lavinia.inspectory.am.visitor;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.metanalysis.core.model.Node;

import edu.lavinia.inspectory.am.visitor.CodeVisitor;
import edu.lavinia.inspectory.am.visitor.NodeVisitor;

public class CodeVisitorTest {
	@Test
	public void testVisitNode() {
		CodeVisitor codeVisitor = new NodeVisitor(null);

		Node node = new Node.Type("test", new HashSet<>(), new HashSet<>(), new HashSet<>());
		codeVisitor.visit(node);

		node = new Node.Variable("test", new HashSet<>(), new ArrayList<>());
		codeVisitor.visit(node);

		node = new Node.Function("test", new ArrayList<>(), new HashSet<>(), new ArrayList<>());
		codeVisitor.visit(node);
	}
}