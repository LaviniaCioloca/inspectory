/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
		final CodeVisitor codeVisitor = new NodeVisitor(null);

		Node node = new Node.Type("test", new HashSet<>(), new HashSet<>(),
				new HashSet<>());
		codeVisitor.visit(node);

		node = new Node.Variable("test", new HashSet<>(), new ArrayList<>());
		codeVisitor.visit(node);

		node = new Node.Function("test", new ArrayList<>(), new HashSet<>(),
				new ArrayList<>());
		codeVisitor.visit(node);
	}
}
