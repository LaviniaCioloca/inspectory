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
package edu.lavinia.inspectory.op.inspection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.visitor.EditVisitor;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class MethodOwnershipInspection extends GenericOwnershipInspection {
	private ArrayList<String> deletedNodes;
	// private Map<String, Map<String, List<Integer>>> methodsAuthorsChanges;
	private Table<String, String, List<Integer>> methodsAuthorsChanges = HashBasedTable
			.create();

	/**
	 * MethodOwnershipInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public MethodOwnershipInspection(PersistentProject project,
			FileWriter csvWriter) {
		super(project, csvWriter);
	}

	/**
	 * Checks for every Change MemberEdit of the current NodeSetEdit if there is
	 * in the result set in order to add it to the CSV line.
	 * 
	 * @param edit
	 * @param visitor
	 * @param fileName
	 * @param commit
	 * @param lineChanges
	 */
	public void handleNodeSetEditChange(NodeSetEdit edit, String fileName,
			Commit commit, ArrayList<Integer> lineChanges) {
		final String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		final Transaction<?> t = ((NodeSetEdit.Change<?>) edit)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) t)
				.getMemberEdits();

		/*
		 * for (final NodeSetEdit memberEdit : memberEdits) { try { if
		 * (memberEdit instanceof NodeSetEdit.Remove) { Integer lastMethodSize =
		 * 0; final ArrayList<Integer> changesList = result.get(fileName + ":" +
		 * className + ": " + ((NodeSetEdit.Remove) memberEdit).getIdentifier())
		 * .getChangesList();
		 * 
		 * for (final Integer change : changesList) { lastMethodSize += change;
		 * }
		 * 
		 * visitor.setLastMethodSize(lastMethodSize); }
		 * 
		 * ((EditVisitor) visitor).visit(memberEdit);
		 * 
		 * if (checkEntryInResultSet(visitor, className, commit)) {
		 * addDataInMethodInformationList(fileName, className,
		 * visitor.getIdentifier()); } } catch (Exception e) { continue; } }
		 */
	}

	private void addNewMethodsAuthorsChanges(Node typeMember, String fileName,
			String className, Commit commit) {
		final String methodSignature;
		final String methodFullPath;
		final Integer methodSize;
		final List<String> body = ((Node.Function) typeMember).getBody();
		methodSignature = ((Node.Function) typeMember).getSignature();
		methodFullPath = fileName + " -> " + className + " -> "
				+ methodSignature;
		methodSize = body.size();

		List<Integer> authorsLineAdded = new ArrayList<>(
				Arrays.asList(methodSize, 0));
		methodsAuthorsChanges.put(methodFullPath, commit.getAuthor(),
				authorsLineAdded);
	}

	/**
	 * Checks for every Add NodeSetEdit if there is in the result set in order
	 * to add it to the CSV line.
	 * 
	 * @param edit
	 * @param visitor
	 * @param fileName
	 * @param commit
	 * @param lineChanges
	 */
	public void handleNodeSetEditAdd(NodeSetEdit edit, String fileName,
			Commit commit, ArrayList<Integer> lineChanges) {
		final Node node = ((NodeSetEdit.Add) edit).getNode();

		if (node instanceof Node.Type) {
			String className;
			final Set<Node> members = ((Node.Type) node).getMembers();

			for (final Node member : members) {
				try {
					if (member instanceof Node.Type) {
						className = ((Node.Type) member).getName();

						final Set<Node> typeMembers = ((Node.Type) member)
								.getMembers();
						for (final Node typeMember : typeMembers) {
							if (typeMember instanceof Node.Function) {
								addNewMethodsAuthorsChanges(typeMember,
										fileName, className, commit);
							}
						}
					} else if (member instanceof Node.Function) {
						className = ((Node.Type) node).getName();
						addNewMethodsAuthorsChanges(member, fileName, className,
								commit);
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

	public void createResults() {
		try {
			final Set<String> filesList = project.listFiles();

			for (final String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}

				final List<HistoryEntry> fileHistory = project
						.getFileHistory(fileName);

				final GenericVisitor visitor = new EditVisitor(fileName);

				int numberOfChanges = 0;
				String methodCreator = null;
				final LinkedHashMap<String, Integer> authorsChanges = new LinkedHashMap<>();
				LinkedHashMap<String, ArrayList<Integer>> authorsLineChanges = new LinkedHashMap<>();

				for (final HistoryEntry historyEntry : fileHistory) {
					try {
						final Commit commit = new Commit();
						commit.setRevision(historyEntry.getRevision());
						commit.setAuthor(historyEntry.getAuthor());
						commit.setDate(historyEntry.getDate());

						((EditVisitor) visitor).setAddedLines(0);
						((EditVisitor) visitor).setDeletedLines(0);

						++numberOfChanges;
						if (methodCreator == null) {
							methodCreator = historyEntry.getAuthor();
						}

						Integer numberOfChangesAuthorHas = authorsChanges
								.get(historyEntry.getAuthor());
						if (numberOfChangesAuthorHas == null) {
							authorsChanges.put(historyEntry.getAuthor(), 1);
						} else {
							authorsChanges.put(historyEntry.getAuthor(),
									++numberOfChangesAuthorHas);
						}

						final SourceFileTransaction sourceFileTransaction = historyEntry
								.getTransaction();
						final List<NodeSetEdit> nodeEditList = sourceFileTransaction
								.getNodeEdits();
						final ArrayList<Integer> lineChanges = null;

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								handleNodeSetEditChange(edit, fileName, commit,
										lineChanges);
							} else if (edit instanceof NodeSetEdit.Add) {
								handleNodeSetEditAdd(edit, fileName, commit,
										lineChanges);
							} else {
								deletedNodes.add(fileName);
							}

							final ArrayList<Integer> changedLines = authorsLineChanges
									.get(historyEntry.getAuthor());
						}
					} catch (Exception e) {
						continue;
					}
				}

				LinkedHashMap<String, Double> ownershipPercentages = calculateFileOwnership(
						authorsLineChanges);
				ownershipPercentages = sortPercentagesMap(ownershipPercentages);

				addFileInformation(fileName, numberOfChanges, methodCreator,
						authorsChanges, authorsLineChanges,
						ownershipPercentages);
			}

			for (Cell<String, String, List<Integer>> cell : methodsAuthorsChanges
					.cellSet()) {
				System.out.println("\nMethod: " + cell.getRowKey()
						+ "; author: " + cell.getColumnKey() + "; changes: "
						+ cell.getValue());
			}
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}
}
