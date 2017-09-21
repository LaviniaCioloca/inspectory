package org.lavinia.visitor;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Function;
import org.metanalysis.core.model.Node.Type;
import org.metanalysis.core.model.Node.Variable;

public class CodeVisitorTest {
	@Test
	public void testVisitNode() {
		CodeVisitor visitor = new NodeVisitor(null);
		Node.Type type = new Type("test", new HashSet<>(), new HashSet<>(), new HashSet<>());
		visitor.visit(type);
		Node.Variable variable = new Variable("test", new HashSet<>(), new ArrayList<>());
		visitor.visit(variable);
		Node.Function function = new Function("test", new ArrayList<>(), new HashSet<>(), new ArrayList<>());
		visitor.visit(function);
	}
}
