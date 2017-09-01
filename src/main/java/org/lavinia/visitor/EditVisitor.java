/*******************************************************************************
 * Copyright (c) 2017 Lavinia Cioloca
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
package org.lavinia.visitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.NodeSetEdit.Add;
import org.metanalysis.core.delta.NodeSetEdit.Change;
import org.metanalysis.core.delta.NodeSetEdit.Remove;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.delta.Transaction;

public class EditVisitor extends NodeSetEditVisitor {

	public EditVisitor(Logger logger, String fileName) {
		this.logger = logger;
		this.fileName = fileName;
	}

	@Override
	public void visit(Add add) {
		Node n = ((NodeSetEdit.Add) add).getNode();
		if (n instanceof Node.Function) {
			identifier = ((Node.Function) n).getIdentifier();
			logger.info("\n" + identifier);
			List<String> body = ((Node.Function) n).getBody();
			logger.info("Add: +" + body.size() + ": " + body);
			total += body.size();
		}
	}

	@Override
	public void visit(Remove remove) {
		if (remove.getNodeType().getQualifiedName().equals(Node.Function.class.getCanonicalName())) {
			identifier = ((NodeSetEdit.Remove) remove).getIdentifier();
			logger.info("Remove: " + identifier);
			total -= 1;
		}
	}

	@Override
	public void visit(Change<?> change) {
		if (change.getNodeType().getQualifiedName().equals(Node.Function.class.getCanonicalName())) {
			identifier = ((NodeSetEdit.Change<?>) change).getIdentifier();
			logger.info(identifier);
			Transaction<?> transaction = ((NodeSetEdit.Change<?>) change).getTransaction();
			List<ListEdit<String>> bodyEdits = ((FunctionTransaction) transaction).getBodyEdits();
			for (ListEdit<String> listEdit : bodyEdits) {
				if (listEdit instanceof ListEdit.Add<?>) {
					logger.info("Change: +1: " + listEdit);
					total += 1;
				} else if (listEdit instanceof ListEdit.Remove<?>) {
					logger.info("Change: -1: " + listEdit);
					total -= 1;
				}
			}
		}
	}
}
