/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package edu.lavinia.inspectory.am.visitor;

import java.util.HashSet;

import org.junit.Test;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Type;

import edu.lavinia.inspectory.visitor.NodeSetEditVisitor;
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
