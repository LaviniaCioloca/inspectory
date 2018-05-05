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
import java.util.Optional;
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
import edu.lavinia.inspectory.utils.CSVUtils;

public class MethodOwnershipInspection extends GenericOwnershipInspection {
	private final ArrayList<String> deletedNodes = new ArrayList<>();
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
	public MethodOwnershipInspection(Optional<PersistentProject> project,
			FileWriter csvWriter) {
		super(project, csvWriter);
	}

	private void updateMethodsAuthorsChanges(String methodFullPath,
			String author, List<Integer> authorsNewChanges) {
		final List<Integer> currentAuthorsChanges = methodsAuthorsChanges
				.get(methodFullPath, author);
		final Integer increasedMethodChanges = methodNumberOfChanges
				.get(methodFullPath) + 1;
		final Integer currentAddedLines = setCurrentAddedLines(
				authorsNewChanges, currentAuthorsChanges);
		final Integer currentDeletedLines = setCurrentDeletedLines(
				authorsNewChanges, currentAuthorsChanges);
		final List<Integer> newMethodsChanges = new ArrayList<>(
				Arrays.asList(currentAddedLines, currentDeletedLines));

		methodsAuthorsChanges.put(methodFullPath, author, newMethodsChanges);
		methodNumberOfChanges.put(methodFullPath, increasedMethodChanges);

		final Integer currentMethodSize = methodSize.get(methodFullPath);

		final Integer newMethodSize = currentMethodSize
				+ (authorsNewChanges.get(0) - authorsNewChanges.get(1));

		methodSize.put(methodFullPath, newMethodSize);
	}

	public Integer setCurrentAddedLines(List<Integer> authorsNewChanges,
			List<Integer> currentAuthorsChanges) {
		final Integer currentAddedLines;

		if (currentAuthorsChanges == null) {
			currentAddedLines = authorsNewChanges.get(0);
		} else {
			currentAddedLines = currentAuthorsChanges.get(0)
					+ authorsNewChanges.get(0);
		}

		return currentAddedLines;
	}

	public Integer setCurrentDeletedLines(List<Integer> authorsNewChanges,
			List<Integer> currentAuthorsChanges) {
		final Integer currentDeletedLines;

		if (currentAuthorsChanges == null) {
			currentDeletedLines = authorsNewChanges.get(1);
		} else {
			currentDeletedLines = currentAuthorsChanges.get(1)
					+ authorsNewChanges.get(1);
		}

		return currentDeletedLines;
	}

	public void addMethodsAuthorsChanges(Integer methodBodySize,
			String methodSignature, String fileName, String className,
			Commit commit) {
		final String methodFullPath = fileName + " -> " + className + " -> "
				+ methodSignature;

		// Add +1 to method's size for signature and modifiers
		final Integer methodTotalSize = methodBodySize + 1;
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
				treatGeneralNodeSetEdit(fileName, commit, className,
						memberEdit);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	private void treatGeneralNodeSetEdit(String fileName, Commit commit,
			final String className, final NodeSetEdit memberEdit) {

		if (memberEdit instanceof NodeSetEdit.Change) {
			treatNodeSetEditChange(fileName, commit, className, memberEdit);
		} else if (memberEdit instanceof NodeSetEdit.Add) {
			treatNodeSetEditAdd(fileName, commit, className, memberEdit);
		} else if (memberEdit instanceof NodeSetEdit.Remove) {
			treatNodeSetEditRemove(fileName, commit, className, memberEdit);
		}
	}

	private void treatNodeSetEditRemove(String fileName, Commit commit,
			final String className, final NodeSetEdit memberEdit) {

		if (((NodeSetEdit.Remove) memberEdit).getNodeType().getQualifiedName()
				.equals(Node.Function.class.getCanonicalName())) {

			final String methodSignature = ((NodeSetEdit.Remove) memberEdit)
					.getIdentifier();
			final String methodFullPath = fileName + " -> " + className + " -> "
					+ methodSignature;
			final Integer deletedLines = methodSize.get(methodFullPath);
			final List<Integer> authorsNewChanges = new ArrayList<>(
					Arrays.asList(0, deletedLines));

			updateMethodsAuthorsChanges(methodFullPath, commit.getAuthor(),
					authorsNewChanges);
		}
	}

	private void treatNodeSetEditAdd(String fileName, Commit commit,
			final String className, final NodeSetEdit memberEdit) {

		final Node memberEditNode = ((NodeSetEdit.Add) memberEdit).getNode();

		if (memberEditNode instanceof Node.Function) {
			final List<String> methodBody = ((Node.Function) memberEditNode)
					.getBody();
			final String methodSignature = ((Node.Function) memberEditNode)
					.getSignature();

			addMethodsAuthorsChanges(methodBody.size(), methodSignature,
					fileName, className, commit);
		}
	}

	private void treatNodeSetEditChange(String fileName, Commit commit,
			final String className, final NodeSetEdit memberEdit) {

		final Transaction<?> functionTransaction = ((NodeSetEdit.Change<?>) memberEdit)
				.getTransaction();

		if (functionTransaction instanceof FunctionTransaction) {
			final List<ListEdit<String>> bodyEdits = ((FunctionTransaction) functionTransaction)
					.getBodyEdits();
			final String methodSignature = ((NodeSetEdit.Change<?>) memberEdit)
					.getIdentifier();
			final Integer addedLines = countAddedAndDeletedLines(bodyEdits)
					.get(0);
			final Integer deletedLines = countAddedAndDeletedLines(bodyEdits)
					.get(1);
			final String methodFullPath = fileName + " -> " + className + " -> "
					+ methodSignature;
			final List<Integer> authorsNewChanges = new ArrayList<>(
					Arrays.asList(addedLines, deletedLines));

			updateMethodsAuthorsChanges(methodFullPath, commit.getAuthor(),
					authorsNewChanges);
		}
	}

	public List<Integer> countAddedAndDeletedLines(
			List<ListEdit<String>> bodyEdits) {

		Integer addedLines = 0;
		Integer deletedLines = 0;

		for (final ListEdit<String> listEdit : bodyEdits) {
			if (listEdit instanceof ListEdit.Add<?>) {
				++addedLines;
			} else if (listEdit instanceof ListEdit.Remove<?>) {
				++deletedLines;
			}
		}

		return new ArrayList<Integer>(Arrays.asList(addedLines, deletedLines));
	}

	public void handleNodeSetEditAdd(NodeSetEdit edit, String fileName,
			Commit commit) {

		final Node node = ((NodeSetEdit.Add) edit).getNode();

		if (node instanceof Node.Type) {
			treatGeneralNodeType(fileName, commit, node);
		}
	}

	private void treatGeneralNodeType(String fileName, Commit commit,
			final Node node) {

		final Set<Node> members = ((Node.Type) node).getMembers();

		for (final Node member : members) {
			try {
				if (member instanceof Node.Type) {
					treatNodeType(fileName, commit, member);
				} else if (member instanceof Node.Function) {
					treatNodeFunction(fileName, commit, node, member);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void treatNodeFunction(String fileName, Commit commit,
			final Node node, final Node member) {

		String className;
		className = ((Node.Type) node).getName();
		final List<String> methodBody = ((Node.Function) member).getBody();
		final String methodSignature = ((Node.Function) member).getSignature();

		addMethodsAuthorsChanges(methodBody.size(), methodSignature, fileName,
				className, commit);
	}

	private void treatNodeType(String fileName, Commit commit,
			final Node member) {

		String className;
		className = ((Node.Type) member).getName();

		final Set<Node> typeMembers = ((Node.Type) member).getMembers();
		for (final Node typeMember : typeMembers) {
			if (typeMember instanceof Node.Function) {
				final List<String> methodBody = ((Node.Function) typeMember)
						.getBody();
				final String methodSignature = ((Node.Function) typeMember)
						.getSignature();

				addMethodsAuthorsChanges(methodBody.size(), methodSignature,
						fileName, className, commit);
			}
		}
	}

	@Override
	public void createResults() {
		final Set<String> filesList = project.get().listFiles();

		for (final String fileName : filesList) {
			if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
				continue;
			}

			createResultForEachMethod(fileName);
		}
	}

	public void createResultForEachMethod(String fileName) {
		try {
			final List<HistoryEntry> fileHistory = project.get()
					.getFileHistory(fileName);

			final LinkedHashMap<String, Integer> authorsChanges = new LinkedHashMap<>();

			for (final HistoryEntry historyEntry : fileHistory) {
				createResultForEachHistoryEntry(fileName, authorsChanges,
						historyEntry);
			}
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	private void createResultForEachHistoryEntry(String fileName,
			final LinkedHashMap<String, Integer> authorsChanges,
			final HistoryEntry historyEntry) {

		try {
			final Commit commit = new Commit();
			setCommitInformation(historyEntry, commit);

			updateAuthorNumberOfChanges(authorsChanges, historyEntry);

			final SourceFileTransaction sourceFileTransaction = historyEntry
					.getTransaction();
			final List<NodeSetEdit> nodeEditList = sourceFileTransaction
					.getNodeEdits();

			treatEachNodeSetEdit(fileName, commit, nodeEditList);
		} catch (Exception e) {
			return;
		}
	}

	private void updateAuthorNumberOfChanges(
			final LinkedHashMap<String, Integer> authorsChanges,
			final HistoryEntry historyEntry) {

		Integer numberOfChangesAuthorHas = authorsChanges
				.get(historyEntry.getAuthor());

		if (numberOfChangesAuthorHas == null) {
			authorsChanges.put(historyEntry.getAuthor(), 1);
		} else {
			authorsChanges.put(historyEntry.getAuthor(),
					++numberOfChangesAuthorHas);
		}
	}

	private void treatEachNodeSetEdit(String fileName, final Commit commit,
			final List<NodeSetEdit> nodeEditList) {

		for (final NodeSetEdit edit : nodeEditList) {
			if (edit instanceof NodeSetEdit.Change<?>) {
				handleNodeSetEditChange(edit, fileName, commit);
			} else if (edit instanceof NodeSetEdit.Add) {
				handleNodeSetEditAdd(edit, fileName, commit);
			} else {
				deletedNodes.add(fileName);
			}
		}
	}

	private void setCommitInformation(final HistoryEntry historyEntry,
			final Commit commit) {

		commit.setRevision(historyEntry.getRevision());
		commit.setAuthor(historyEntry.getAuthor());
		commit.setDate(historyEntry.getDate());
	}

	@Override
	public void writeFileResults() {
		try {
			final Map<String, Map<String, List<Integer>>> methodsAuthorsChangesMap = methodsAuthorsChanges
					.rowMap();

			for (String methodFullPath : methodsAuthorsChangesMap.keySet()) {
				final ArrayList<String> methodOwnershipInformationLine = addMethodOwnershipInformation(
						methodsAuthorsChangesMap, methodFullPath);

				CSVUtils.writeLine(csvWriter, methodOwnershipInformationLine,
						',', '"');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> addMethodOwnershipInformation(
			final Map<String, Map<String, List<Integer>>> methodsAuthorsChangesMap,
			String methodFullPath) {

		final ArrayList<String> methodOwnershipInformationLine = new ArrayList<>();
		final Map<String, List<Integer>> authorChanges = methodsAuthorsChangesMap
				.get(methodFullPath);
		methodOwnershipInformationLine.add(methodFullPath);
		methodOwnershipInformationLine
				.add(methodNumberOfChanges.get(methodFullPath).toString());
		methodOwnershipInformationLine
				.add(String.valueOf(authorChanges.size()));
		methodOwnershipInformationLine
				.add(methodSize.get(methodFullPath).toString());

		final LinkedHashMap<String, List<Integer>> methodLineChangesByAuthor = getMethodLineChangesByAuthor(
				authorChanges);

		methodOwnershipInformationLine.add(
				calculateEntityOwnership(methodLineChangesByAuthor).toString());

		methodOwnershipInformationLine
				.add(methodLineChangesByAuthor.toString());

		return methodOwnershipInformationLine;
	}

	private LinkedHashMap<String, List<Integer>> getMethodLineChangesByAuthor(
			final Map<String, List<Integer>> methodData) {

		final LinkedHashMap<String, List<Integer>> methodLineChangesByAuthor = new LinkedHashMap<>();

		for (Map.Entry<String, List<Integer>> authorChanges : methodData
				.entrySet()) {
			methodLineChangesByAuthor.put(authorChanges.getKey(),
					authorChanges.getValue());
		}

		return methodLineChangesByAuthor;
	}

	public Table<String, String, List<Integer>> getMethodsAuthorsChanges() {
		return methodsAuthorsChanges;
	}

	public void setMethodsAuthorsChanges(
			Table<String, String, List<Integer>> methodsAuthorsChanges) {
		this.methodsAuthorsChanges = methodsAuthorsChanges;
	}

	public Map<String, Integer> getMethodNumberOfChanges() {
		return methodNumberOfChanges;
	}

	public void setMethodNumberOfChanges(
			Map<String, Integer> methodNumberOfChanges) {
		this.methodNumberOfChanges = methodNumberOfChanges;
	}

	public Map<String, Integer> getMethodSize() {
		return methodSize;
	}

	public void setMethodSize(Map<String, Integer> methodSize) {
		this.methodSize = methodSize;
	}

}