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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Function;
import org.metanalysis.core.model.Node.Type;
import org.metanalysis.core.model.Node.Variable;
import org.metanalysis.core.model.SourceFile;

//import java.util.logging.Logger;
import edu.lavinia.inspectory.visitor.CodeVisitor;

/**
 * Implementation of {@link edu.lavinia.inspectory.visitor.CodeVisitor
 * CodeVisitor} for visiting nodes of types: Type and Function.
 *
 * @author Lavinia Cioloca
 *
 */
public class NodeVisitor extends CodeVisitor {
	private Map<String, Integer> functionSize = new HashMap<>();

	public NodeVisitor(final String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void visit(final SourceFile sourceFile) {
	}

	@Override
	public void visit(final Type type) {
		final Set<Node> members = type.getMembers();

		for (final Node node : members) {
			this.visit(node);
		}
	}

	@Override
	public void visit(final Variable variable) {
	}

	@Override
	public void visit(final Function function) {
		total = 0;
		final List<String> body = function.getBody();
		functionSize.put(function.getIdentifier(), body.size());
		identifier = function.getSignature();
		total += body.size();
	}

	public Map<String, Integer> getFunctionSize() {
		return functionSize;
	}

	public void setFunctionSize(final Map<String, Integer> functionSize) {
		this.functionSize = functionSize;
	}

}
