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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.NodeSetEdit.Add;
import org.metanalysis.core.delta.NodeSetEdit.Change;
import org.metanalysis.core.delta.NodeSetEdit.Remove;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;

import edu.lavinia.inspectory.visitor.NodeSetEditVisitor;

public class EditVisitor extends NodeSetEditVisitor {

	private Set<Node> members = new HashSet<>();
	private Integer addedLines = 0;
	private Integer deletedLines = 0;

	private final Map<String, Integer> methodSize = new HashMap<>();

	public EditVisitor(final String fileName) {
		this.fileName = fileName;
	}

	private void visitTypeMemberNode(Node typeMemberNode) {
		if (typeMemberNode instanceof Node.Variable) {
			++addedLines;
		} else if (typeMemberNode instanceof Node.Function) {
			++addedLines; // for signature, modifiers and parameters
			final List<String> body = ((Node.Function) typeMemberNode)
					.getBody();
			addedLines += body.size();

			methodSize.put(((Node.Function) typeMemberNode).getSignature(),
					body.size());
		}
	}

	@Override
	public void visit(Add add) {
		final Node node = add.getNode();
		if (node instanceof Node.Type) {
			++addedLines; // for the identifier, supertype and modifiers
			identifier = ((Node.Type) node).getIdentifier();
			members = ((Node.Type) node).getMembers();

			for (final Node memberNode : members) {
				// @TODO check if Type of Type exist
				if (memberNode instanceof Node.Type) {
					final Set<Node> members = ((Node.Type) memberNode)
							.getMembers();
					for (final Node typeMemberNode : members) {
						visitTypeMemberNode(typeMemberNode);
					}
				} else if (memberNode instanceof Node.Function) {
					++addedLines; // for signature, modifiers and parameters
					final List<String> body = ((Node.Function) memberNode)
							.getBody();
					addedLines += body.size();

					methodSize.put(((Node.Function) memberNode).getSignature(),
							body.size());
				} else if (memberNode instanceof Node.Variable) {
					++addedLines;
				}
			}
		} else if (node instanceof Node.Function) {
			identifier = ((Node.Function) node).getIdentifier();
			++addedLines; // for signature, modifiers and parameters
			final List<String> body = ((Node.Function) node).getBody();
			addedLines += body.size();

			methodSize.put(identifier, body.size());
		} else if (node instanceof Node.Variable) {
			++addedLines;
		}
	}

	@Override
	public void visit(Remove remove) {
		if (remove.getNodeType().getQualifiedName()
				.equals(Node.Function.class.getCanonicalName())) {
			identifier = remove.getIdentifier();

			final Integer currentMethodSize = methodSize.get(identifier);
			++deletedLines; // remove signature, modifiers and parameters
			deletedLines += currentMethodSize;
			methodSize.put(identifier, 0);

			/*
			 * System.out.println("\t\tMethod deleted: " + identifier +
			 * "; currentMethodSize: " + currentMethodSize + "; deletedLines: "
			 * + deletedLines);
			 */
		}
	}

	@Override
	public void visit(Change<?> change) {
		identifier = ((NodeSetEdit.Change<?>) change).getIdentifier();
		final Transaction<?> transaction = ((NodeSetEdit.Change<?>) change)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) transaction)
				.getMemberEdits();

		for (final NodeSetEdit memberEdit : memberEdits) {
			if (memberEdit instanceof NodeSetEdit.Change<?>) {
				final Transaction<?> changeTransaction = ((NodeSetEdit.Change<?>) memberEdit)
						.getTransaction();
				final List<ListEdit<String>> bodyEdits = ((FunctionTransaction) changeTransaction)
						.getBodyEdits();

				Integer currentMethodSize = methodSize.get(
						((NodeSetEdit.Change<?>) memberEdit).getIdentifier());

				for (final ListEdit<String> listEdit : bodyEdits) {
					if (listEdit instanceof ListEdit.Add<?>) {
						++addedLines;

						++currentMethodSize;
						methodSize.put(((NodeSetEdit.Change<?>) memberEdit)
								.getIdentifier(), currentMethodSize);
					} else if (listEdit instanceof ListEdit.Remove<?>) {
						++deletedLines;

						--currentMethodSize;
						methodSize.put(((NodeSetEdit.Change<?>) memberEdit)
								.getIdentifier(), currentMethodSize);
					}
				}
			} else if (memberEdit instanceof NodeSetEdit.Add) {
				visit(memberEdit);
			} else if (memberEdit instanceof NodeSetEdit.Remove) {
				/*
				 * To change and remove the number of line the method has, not
				 * -1!
				 */
				visit(memberEdit);
			}
		}
	}

	public Integer getAddedLines() {
		return addedLines;
	}

	public void setAddedLines(Integer addedLines) {
		this.addedLines = addedLines;
	}

	public Integer getDeletedLines() {
		return deletedLines;
	}

	public void setDeletedLines(Integer deletedLines) {
		this.deletedLines = deletedLines;
	}

}
