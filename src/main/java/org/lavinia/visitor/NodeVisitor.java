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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.logging.Logger;

import org.apache.log4j.Logger;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.Node.Function;
import org.metanalysis.core.model.Node.Type;
import org.metanalysis.core.model.Node.Variable;
import org.metanalysis.core.model.SourceFile;

public class NodeVisitor extends CodeVisitor {
	// public final static Logger logger = Logger.getLogger(NodeVisitor.class);
	private Map<String, Integer> functionSize = new HashMap<String, Integer>();

	public NodeVisitor(Logger logger) {
		this.logger = logger;
	}

	public NodeVisitor(Logger logger, String fileName) {
		this.logger = logger;
		this.fileName = fileName;
	}

	@Override
	public void visit(SourceFile sourceFile) {
		// logger.info("Visit SourceFile");

	}

	@Override
	public void visit(Type type) {
		// logger.info("Visit Type");
		// logger.info("\n\n");
		Set<Node> members = type.getMembers();
		for (Node n : members) {
			// logger.info("Node identifier: " + n.getIdentifier());
			this.visit(n);
		}
	}

	@Override
	public void visit(Variable variable) {
		// logger.info("Visit Variable");

	}

	@Override
	public void visit(Function function) {
		// logger.info("Visit Function");
		// String signature = function.getSignature();
		// logger.info("Function signature: " + signature);
		total = 0;
		List<String> body = function.getBody();
		// logger.info("Body size: " + body.size());
		functionSize.put(function.getIdentifier(), body.size());
		/*
		 * for (String b : body) { logger.info(b); } logger.info("\n");
		 */
		// identifier = fileName + ":\t" + function.getSignature();
		identifier = function.getSignature();
		logger.info(identifier);
		logger.info("Add: +" + function.getBody().size() + " " + function.getBody());
		total += function.getBody().size();
	}

	public Map<String, Integer> getFunctionSize() {
		return functionSize;
	}

	public void setFunctionSize(Map<String, Integer> functionSize) {
		this.functionSize = functionSize;
	}

}
