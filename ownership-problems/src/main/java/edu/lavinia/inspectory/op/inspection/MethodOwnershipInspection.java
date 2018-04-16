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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.visitor.EditVisitor;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class MethodOwnershipInspection extends GenericOwnershipInspection {
	private ArrayList<String> deletedNodes;
	private Map<String, Integer> methodNumberOfChanges = new HashMap<>();
	private Map<String, Integer> methodSize = new HashMap<>();
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

	private void updateMethodsAuthorsChanges(String methodFullPath,
			String author, List<Integer> authorsNewChanges) {
		final List<Integer> currentAuthorsChanges = methodsAuthorsChanges
				.get(methodFullPath, author);
		Integer currentAddedLines;
		Integer currentDeletedLines;
		final Integer increasedMethodChanges = methodNumberOfChanges
				.get(methodFullPath) + 1;

		if (currentAuthorsChanges == null) {
			currentAddedLines = authorsNewChanges.get(0);
			currentDeletedLines = authorsNewChanges.get(1);
		} else {
			currentAddedLines = currentAuthorsChanges.get(0);
			currentAddedLines += authorsNewChanges.get(0);
			currentDeletedLines = currentAuthorsChanges.get(1);
			currentDeletedLines += authorsNewChanges.get(1);
		}

		final List<Integer> newMethodsChanges = new ArrayList<>(
				Arrays.asList(currentAddedLines, currentDeletedLines));
		methodsAuthorsChanges.put(methodFullPath, author, newMethodsChanges);
		methodNumberOfChanges.put(methodFullPath, increasedMethodChanges);
		final Integer currentMethodSize = methodSize.get(methodFullPath);

		final Integer newMethodSize = currentMethodSize
				+ (authorsNewChanges.get(0) - authorsNewChanges.get(1));

		methodSize.put(methodFullPath, newMethodSize);
	}

	private void addMethodsAuthorsChanges(Integer methodBodySize,
			String methodSignature, String fileName, String className,
			Commit commit) {
		final String methodFullPath;
		final Integer methodTotalSize;
		methodFullPath = fileName + " -> " + className + " -> "
				+ methodSignature;
		methodTotalSize = methodBodySize + 1; // for signature, modifiers and
												// parameters

		final List<Integer> authorsLineAdded = new ArrayList<>(
				Arrays.asList(methodTotalSize, 0));
		methodsAuthorsChanges.put(methodFullPath, commit.getAuthor(),
				authorsLineAdded);
		methodNumberOfChanges.put(methodFullPath, 1);
		methodSize.put(methodFullPath, methodTotalSize);
	}

	public void handleNodeSetEditChange(NodeSetEdit edit, String fileName,
			Commit commit) {
		final String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		final Transaction<?> transaction = ((NodeSetEdit.Change<?>) edit)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) transaction)
				.getMemberEdits();

		for (final NodeSetEdit memberEdit : memberEdits) {
			try {
				if (memberEdit instanceof NodeSetEdit.Change) {
					final Transaction<?> functionTransaction = ((NodeSetEdit.Change<?>) memberEdit)
							.getTransaction();

					if (functionTransaction instanceof FunctionTransaction) {
						final List<ListEdit<String>> bodyEdits = ((FunctionTransaction) functionTransaction)
								.getBodyEdits();
						final String methodSignature = ((NodeSetEdit.Change<?>) memberEdit)
								.getIdentifier();
						Integer addedLines = 0;
						Integer deletedLines = 0;

						for (final ListEdit<String> listEdit : bodyEdits) {
							if (listEdit instanceof ListEdit.Add<?>) {
								++addedLines;
							} else if (listEdit instanceof ListEdit.Remove<?>) {
								++deletedLines;
							}
						}

						final String methodFullPath = fileName + " -> "
								+ className + " -> " + methodSignature;
						final List<Integer> authorsNewChanges = new ArrayList<>(
								Arrays.asList(addedLines, deletedLines));

						updateMethodsAuthorsChanges(methodFullPath,
								commit.getAuthor(), authorsNewChanges);
					}
				} else if (memberEdit instanceof NodeSetEdit.Add) {
					final Node memberEditNode = ((NodeSetEdit.Add) memberEdit)
							.getNode();

					if (memberEditNode instanceof Node.Function) {
						final List<String> methodBody = ((Node.Function) memberEditNode)
								.getBody();
						final String methodSignature = ((Node.Function) memberEditNode)
								.getSignature();

						addMethodsAuthorsChanges(methodBody.size(),
								methodSignature, fileName, className, commit);
					}
				} else if (memberEdit instanceof NodeSetEdit.Remove) {
					if (((NodeSetEdit.Remove) memberEdit).getNodeType()
							.getQualifiedName()
							.equals(Node.Function.class.getCanonicalName())) {

						final String methodSignature = ((NodeSetEdit.Remove) memberEdit)
								.getIdentifier();
						final String methodFullPath = fileName + " -> "
								+ className + " -> " + methodSignature;
						final Integer deletedLines = methodSize
								.get(methodFullPath);
						final List<Integer> authorsNewChanges = new ArrayList<>(
								Arrays.asList(0, deletedLines));

						updateMethodsAuthorsChanges(methodFullPath,
								commit.getAuthor(), authorsNewChanges);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	public void handleNodeSetEditAdd(NodeSetEdit edit, String fileName,
			Commit commit) {
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
								final List<String> methodBody = ((Node.Function) typeMember)
										.getBody();
								final String methodSignature = ((Node.Function) typeMember)
										.getSignature();

								addMethodsAuthorsChanges(methodBody.size(),
										methodSignature, fileName, className,
										commit);
							}
						}
					} else if (member instanceof Node.Function) {
						className = ((Node.Type) node).getName();
						final List<String> methodBody = ((Node.Function) member)
								.getBody();
						final String methodSignature = ((Node.Function) member)
								.getSignature();

						addMethodsAuthorsChanges(methodBody.size(),
								methodSignature, fileName, className, commit);
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

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								handleNodeSetEditChange(edit, fileName, commit);
							} else if (edit instanceof NodeSetEdit.Add) {
								handleNodeSetEditAdd(edit, fileName, commit);
							} else {
								deletedNodes.add(fileName);
							}
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
			/*
			 * for (Cell<String, String, List<Integer>> cell :
			 * methodsAuthorsChanges .cellSet()) {
			 * System.out.println("\nMethod: " + cell.getRowKey() + "; author: "
			 * + cell.getColumnKey() + "; changes: " + cell.getValue()); }
			 * Map<String, Map<String, List<Integer>>> map =
			 * methodsAuthorsChanges .rowMap();
			 * 
			 * for (String row : map.keySet()) { Map<String, List<Integer>> tmp
			 * = map.get(row); System.out.print(row + " = { "); for
			 * (Map.Entry<String, List<Integer>> pair : tmp.entrySet()) {
			 * System.out.print( pair.getKey() + " = " + pair.getValue() +
			 * ", "); } System.out.print("};\n"); }
			 */
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	@Override
	public void writeFileResults() {
		try {
			final Map<String, Map<String, List<Integer>>> methodsAuthorsChangesMap = methodsAuthorsChanges
					.rowMap();

			for (String methodFullPath : methodsAuthorsChangesMap.keySet()) {
				final ArrayList<String> methodOwnershipInformationLine = new ArrayList<>();
				final Map<String, List<Integer>> methodData = methodsAuthorsChangesMap
						.get(methodFullPath);
				methodOwnershipInformationLine.add(methodFullPath);
				methodOwnershipInformationLine.add(
						methodNumberOfChanges.get(methodFullPath).toString());
				methodOwnershipInformationLine
						.add(String.valueOf(methodData.size()));
				methodOwnershipInformationLine
						.add(methodSize.get(methodFullPath).toString());

				for (Map.Entry<String, List<Integer>> authorChanges : methodData
						.entrySet()) {
					methodOwnershipInformationLine.add(authorChanges.getKey()
							+ " = " + authorChanges.getValue());
				}

				CSVUtils.writeLine(csvWriter, methodOwnershipInformationLine,
						',', '"');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}