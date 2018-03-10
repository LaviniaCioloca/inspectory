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
package edu.lavinia.inspectory.op.visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.NodeSetEdit.Add;
import org.metanalysis.core.delta.NodeSetEdit.Change;
import org.metanalysis.core.delta.NodeSetEdit.Remove;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.model.Node;

import edu.lavinia.inspectory.visitor.NodeSetEditVisitor;

public class EditVisitor extends NodeSetEditVisitor {

	private Set<Node> members = new HashSet<>();
	private Integer numberOfLines = 0;

	public EditVisitor(final String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void visit(Add add) {
		final Node node = ((NodeSetEdit.Add) add).getNode();
		if (node instanceof Node.Type) {
			++numberOfLines; // for the identifier, supertype and modifiers
			identifier = ((Node.Type) node).getIdentifier();
			members = ((Node.Type) node).getMembers();

			for (final Node memberNode : members) {
				if (memberNode instanceof Node.Type) {
					NodeVisitor nodeVisitor = new NodeVisitor(fileName);
					nodeVisitor.visit(memberNode);
				} else if (memberNode instanceof Node.Function) {
					++numberOfLines; // for signature, modifiers and parameters
					final List<String> body = ((Node.Function) node).getBody();
					numberOfLines += body.size();
				} else if (memberNode instanceof Node.Variable) {
					++numberOfLines;
				}
			}
		} else if (node instanceof Node.Function) {
			identifier = ((Node.Function) node).getIdentifier();
			final List<String> body = ((Node.Function) node).getBody();
			total += body.size();
		} else if (node instanceof Node.Variable) {

		}
	}

	@Override
	public void visit(Remove remove) {
		if (remove.getNodeType().getQualifiedName()
				.equals(Node.Function.class.getCanonicalName())) {
			identifier = ((NodeSetEdit.Remove) remove).getIdentifier();
			total -= 1;
		}
	}

	@Override
	public void visit(Change<?> change) {
		if (change.getNodeType().getQualifiedName()
				.equals(Node.Function.class.getCanonicalName())) {
			identifier = ((NodeSetEdit.Change<?>) change).getIdentifier();
			final Transaction<?> transaction = ((NodeSetEdit.Change<?>) change)
					.getTransaction();
			final List<ListEdit<String>> bodyEdits = ((FunctionTransaction) transaction)
					.getBodyEdits();
			for (final ListEdit<String> listEdit : bodyEdits) {
				if (listEdit instanceof ListEdit.Add<?>) {
					total += 1;
				} else if (listEdit instanceof ListEdit.Remove<?>) {
					total -= 1;
				}
			}
		}
	}
}
