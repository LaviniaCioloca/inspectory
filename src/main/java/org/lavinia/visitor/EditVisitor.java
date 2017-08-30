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
			// identifier = fileName + ":\t" + n.getIdentifier();
			identifier = ((Node.Function) n).getIdentifier();
			logger.info("\n" + identifier);
			List<String> body = ((Node.Function) n).getBody();
			logger.info("Add: +" + body.size() + ": " + body);
			total += body.size();
		}
	}

	@Override
	public void visit(Remove remove) {
		// identifier = fileName + ":\t" + ((NodeSetEdit.Remove)
		// remove).getIdentifier();
		if (remove.getNodeType().getQualifiedName().equals(Node.Function.class.getCanonicalName())) {
			identifier = ((NodeSetEdit.Remove) remove).getIdentifier();
			logger.info("Remove: " + identifier);
			total -= 1;
		}
	}

	@Override
	public void visit(Change<?> change) {
		// identifier = fileName + ":\t" + ((NodeSetEdit.Change<?>)
		// change).getIdentifier();
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
