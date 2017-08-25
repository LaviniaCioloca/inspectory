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
		// identifier = fileName + ":\t" + n.getIdentifier();
		identifier = n.getIdentifier();
		// logger.info("\n" + identifier);
		List<String> body = ((Node.Function) n).getBody();
		// logger.info("Add: +" + body.size() + ": " + body);
		total += body.size();

	}

	@Override
	public void visit(Remove remove) {
		// identifier = fileName + ":\t" + ((NodeSetEdit.Remove) remove).getIdentifier();
		identifier = ((NodeSetEdit.Remove) remove).getIdentifier();
		// logger.info("Remove: " + identifier);
		total -= 1;

	}

	@Override
	public void visit(Change<?> change) {
		// identifier = fileName + ":\t" + ((NodeSetEdit.Change<?>) change).getIdentifier();
		identifier = ((NodeSetEdit.Change<?>) change).getIdentifier();
		// logger.info(identifier);
		Transaction<?> t1 = ((NodeSetEdit.Change<?>) change).getTransaction();
		List<ListEdit<String>> bodyEdits = ((FunctionTransaction) t1).getBodyEdits();
		for (ListEdit<String> le : bodyEdits) {
			if (le instanceof ListEdit.Add<?>) {
				// logger.info("Change: +1: " + le);
				total += 1;
			} else if (le instanceof ListEdit.Remove<?>) {
				// logger.info("Change: -1: " + le);
				total -= 1;
			}
		}

	}

}
