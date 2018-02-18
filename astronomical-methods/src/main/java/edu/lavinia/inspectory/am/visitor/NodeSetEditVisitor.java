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

import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.NodeSetEdit.Change;

abstract class NodeSetEditVisitor extends GenericVisitor {
	public abstract void visit(NodeSetEdit.Add add);

	public abstract void visit(NodeSetEdit.Remove remove);

	public abstract void visit(NodeSetEdit.Change<?> change);

	public final void visit(NodeSetEdit nodeSetEdit) {
		// safe to use 'instanceof' because the class hierarchy is sealed
		if (nodeSetEdit instanceof NodeSetEdit.Add) {
			visit((NodeSetEdit.Add) nodeSetEdit);
		} else if (nodeSetEdit instanceof NodeSetEdit.Remove) {
			visit((NodeSetEdit.Remove) nodeSetEdit);
		} else if (nodeSetEdit instanceof NodeSetEdit.Change) {
			visit((Change<?>) nodeSetEdit);
		}
	}
}