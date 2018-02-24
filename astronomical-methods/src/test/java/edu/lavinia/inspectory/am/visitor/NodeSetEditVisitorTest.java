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

import java.util.HashSet;

import org.junit.Test;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Type;

import edu.lavinia.inspectory.am.visitor.EditVisitor;
import edu.lavinia.inspectory.am.visitor.NodeSetEditVisitor;
import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.KClassImpl;

public class NodeSetEditVisitorTest {
	@Test
	public void testVisitNodeSetEdit() {
		final NodeSetEditVisitor visitor = new EditVisitor(null);
		final Node node = new Node.Type("test", new HashSet<>(),
				new HashSet<>(), new HashSet<>());
		NodeSetEdit nodeSetEdit = new NodeSetEdit.Add(node);
		visitor.visit(nodeSetEdit);

		final KClass<? extends Node> kclassNode = new KClassImpl<Node.Type>(
				Node.Type.class);
		nodeSetEdit = new NodeSetEdit.Remove(kclassNode, "");
		visitor.visit(nodeSetEdit);

		final KClass<Type> kclassType = new KClassImpl<Node.Type>(Type.class);
		nodeSetEdit = new NodeSetEdit.Change<Node.Type>(kclassType, "",
				new TypeTransaction());
		visitor.visit(nodeSetEdit);
	}
}
