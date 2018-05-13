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
import java.util.stream.Collectors;

import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.beans.FileChangesData;
import edu.lavinia.inspectory.op.beans.MethodChangesData;
import edu.lavinia.inspectory.utils.CSVUtils;

public class MethodOwnershipInspection extends GenericOwnershipInspection {

	/**
	 * MethodOwnershipInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 *
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public MethodOwnershipInspection(final Optional<PersistentProject> project,
			final FileWriter csvWriter) {
		super(project, csvWriter);
	}

	private ArrayList<String> addMethodOwnershipInformation(
			final String methodFullPath) {

		final ArrayList<String> methodOwnershipInformationLine = new ArrayList<>();
		final MethodChangesData methodChangesData = (MethodChangesData) entityChangesData
				.get(methodFullPath);
		final Map<String, List<Integer>> authorChanges = methodChangesData
				.getAuthorsAddedAndDeletedLines();

		addGeneralMethodInformation(methodOwnershipInformationLine,
				methodChangesData, authorChanges);

		addOwnershipInformation(methodFullPath, methodOwnershipInformationLine,
				methodChangesData, authorChanges);

		return methodOwnershipInformationLine;
	}

	private void addGeneralMethodInformation(
			final ArrayList<String> methodOwnershipInformationLine,
			final MethodChangesData methodChangesData,
			final Map<String, List<Integer>> authorChanges) {

		methodOwnershipInformationLine.add(methodChangesData.getFileName());
		methodOwnershipInformationLine.add(methodChangesData.getClassName());
		methodOwnershipInformationLine.add(methodChangesData.getMethodName());
		methodOwnershipInformationLine
				.add(methodChangesData.getNumberOfChanges().toString());
		methodOwnershipInformationLine
				.add(String.valueOf(authorChanges.size()));
		methodOwnershipInformationLine
				.add(methodChangesData.getActualSize().toString());
	}

	private void addOwnershipInformation(final String methodFullPath,
			final ArrayList<String> methodOwnershipInformationLine,
			final MethodChangesData methodChangesData,
			final Map<String, List<Integer>> authorChanges) {

		final List<String> listOfAllOwners = methodChangesData.getAllOwners();
		final List<String> distinctListOfOwners = listOfAllOwners.stream()
				.distinct().collect(Collectors.toList());
		final ArrayList<String> distinctOwners = new ArrayList<>(
				distinctListOfOwners);

		entityChangesData.get(methodFullPath).setDistinctOwners(distinctOwners);

		methodOwnershipInformationLine
				.add(String.valueOf(distinctListOfOwners.size()));
		methodOwnershipInformationLine.add(distinctOwners.toString());

		LinkedHashMap<String, Double> ownershipPercentages = calculateEntityOwnership(
				methodFullPath);
		ownershipPercentages = sortPercentagesMap(ownershipPercentages);
		methodOwnershipInformationLine.add(ownershipPercentages.toString());

		final LinkedHashMap<String, List<Integer>> methodLineChangesByAuthor = getMethodLineChangesByAuthor(
				authorChanges);
		methodOwnershipInformationLine
				.add(methodLineChangesByAuthor.toString());
	}

	public void addMethodsAuthorsChanges(final Integer methodBodySize,
			final String methodSignature, final String fileName,
			final String className, final Commit commit) {

		final String methodFullPath = fileName + " -> " + className + " -> "
				+ methodSignature;

		// Add +1 to method's size for signature and modifiers
		final Integer methodTotalSize = methodBodySize + 1;
		final List<Integer> authorsLineAdded = new ArrayList<>(
				Arrays.asList(methodTotalSize, 0));
		final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines = new LinkedHashMap<>();
		authorsAddedAndDeletedLines.put(commit.getAuthor(), authorsLineAdded);

		final MethodChangesData methosChangesData = addMethodChangesData(
				methodSignature, fileName, className, methodTotalSize,
				authorsAddedAndDeletedLines);

		entityChangesData.put(methodFullPath, methosChangesData);

		addCommitToList(methodFullPath, commit);

		setEntityOwnerAfterCommit(methodFullPath);
	}

	private MethodChangesData addMethodChangesData(final String methodSignature,
			final String fileName, final String className,
			final Integer methodTotalSize,
			final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines) {

		final MethodChangesData fileChangesData = new MethodChangesData();
		fileChangesData.setActualSize(methodTotalSize);
		fileChangesData.setAddedAndDeletedLinesSum(methodTotalSize);
		fileChangesData.setNumberOfChanges(1);
		fileChangesData
				.setAuthorsAddedAndDeletedLines(authorsAddedAndDeletedLines);
		fileChangesData.setFileName(fileName);
		fileChangesData.setClassName(className);
		fileChangesData.setMethodName(methodSignature);

		return fileChangesData;
	}

	public List<Integer> countAddedAndDeletedLines(
			final List<ListEdit<String>> bodyEdits) {

		Integer addedLines = 0;
		Integer deletedLines = 0;

		for (final ListEdit<String> listEdit : bodyEdits) {
			if (listEdit instanceof ListEdit.Add<?>) {
				++addedLines;
			} else if (listEdit instanceof ListEdit.Remove<?>) {
				++deletedLines;
			}
		}

		return new ArrayList<>(Arrays.asList(addedLines, deletedLines));
	}

	private void createResultForEachHistoryEntry(final String fileName,
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
		} catch (final Exception e) {
			return;
		}
	}

	public void createResultForEachMethod(final String fileName) {
		try {
			final List<HistoryEntry> fileHistory = project.get()
					.getFileHistory(fileName);

			final LinkedHashMap<String, Integer> authorsChanges = new LinkedHashMap<>();

			for (final HistoryEntry historyEntry : fileHistory) {
				createResultForEachHistoryEntry(fileName, authorsChanges,
						historyEntry);
			}
		} catch (final IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
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

	private LinkedHashMap<String, List<Integer>> getMethodLineChangesByAuthor(
			final Map<String, List<Integer>> methodData) {

		final LinkedHashMap<String, List<Integer>> methodLineChangesByAuthor = new LinkedHashMap<>();

		for (final Map.Entry<String, List<Integer>> authorChanges : methodData
				.entrySet()) {
			methodLineChangesByAuthor.put(authorChanges.getKey(),
					authorChanges.getValue());
		}

		return methodLineChangesByAuthor;
	}

	public void handleNodeSetEditAdd(final NodeSetEdit edit,
			final String fileName, final Commit commit) {

		final Node node = ((NodeSetEdit.Add) edit).getNode();

		if (node instanceof Node.Type) {
			treatGeneralNodeType(fileName, commit, node);
		}
	}

	public void handleNodeSetEditChange(final NodeSetEdit edit,
			final String fileName, final Commit commit) {

		final String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		final Transaction<?> transaction = ((NodeSetEdit.Change<?>) edit)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) transaction)
				.getMemberEdits();

		for (final NodeSetEdit memberEdit : memberEdits) {
			try {
				treatGeneralNodeSetEdit(fileName, commit, className,
						memberEdit);
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	private void setCommitInformation(final HistoryEntry historyEntry,
			final Commit commit) {

		commit.setRevision(historyEntry.getRevision());
		commit.setAuthor(historyEntry.getAuthor());
		commit.setDate(historyEntry.getDate());
	}

	public Integer setCurrentAddedLines(final List<Integer> authorsNewChanges,
			final List<Integer> currentAuthorsChanges) {

		final Integer currentAddedLines;

		if (currentAuthorsChanges == null) {
			currentAddedLines = authorsNewChanges.get(0);
		} else {
			currentAddedLines = currentAuthorsChanges.get(0)
					+ authorsNewChanges.get(0);
		}

		return currentAddedLines;
	}

	public Integer setCurrentDeletedLines(final List<Integer> authorsNewChanges,
			final List<Integer> currentAuthorsChanges) {

		final Integer currentDeletedLines;

		if (currentAuthorsChanges == null) {
			currentDeletedLines = authorsNewChanges.get(1);
		} else {
			currentDeletedLines = currentAuthorsChanges.get(1)
					+ authorsNewChanges.get(1);
		}

		return currentDeletedLines;
	}

	private void treatEachNodeSetEdit(final String fileName,
			final Commit commit, final List<NodeSetEdit> nodeEditList) {

		for (final NodeSetEdit edit : nodeEditList) {
			if (edit instanceof NodeSetEdit.Change<?>) {
				handleNodeSetEditChange(edit, fileName, commit);
			} else if (edit instanceof NodeSetEdit.Add) {
				handleNodeSetEditAdd(edit, fileName, commit);
			} else {
				entityChangesData.get(fileName).setEntityWasDeleted(true);
			}
		}
	}

	private void treatGeneralNodeSetEdit(final String fileName,
			final Commit commit, final String className,
			final NodeSetEdit memberEdit) {

		if (memberEdit instanceof NodeSetEdit.Change) {
			treatNodeSetEditChange(fileName, commit, className, memberEdit);
		} else if (memberEdit instanceof NodeSetEdit.Add) {
			treatNodeSetEditAdd(fileName, commit, className, memberEdit);
		} else if (memberEdit instanceof NodeSetEdit.Remove) {
			treatNodeSetEditRemove(fileName, commit, className, memberEdit);
		}
	}

	private void treatGeneralNodeType(final String fileName,
			final Commit commit, final Node node) {

		final Set<Node> members = ((Node.Type) node).getMembers();

		for (final Node member : members) {
			try {
				if (member instanceof Node.Type) {
					treatNodeType(fileName, commit, member);
				} else if (member instanceof Node.Function) {
					treatNodeFunction(fileName, commit, node, member);
				}
			} catch (final Exception e) {
				continue;
			}
		}
	}

	private void treatNodeFunction(final String fileName, final Commit commit,
			final Node node, final Node member) {

		final String className = ((Node.Type) node).getName();
		final List<String> methodBody = ((Node.Function) member).getBody();
		final String methodSignature = ((Node.Function) member).getSignature();

		addMethodsAuthorsChanges(methodBody.size(), methodSignature, fileName,
				className, commit);
	}

	private void treatNodeSetEditAdd(final String fileName, final Commit commit,
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

	private void treatNodeSetEditChange(final String fileName,
			final Commit commit, final String className,
			final NodeSetEdit memberEdit) {

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

			addCommitToList(methodFullPath, commit);
		}
	}

	private void addCommitToList(final String methodFullPath,
			final Commit commit) {
		ArrayList<Commit> fileCommits = entityChangesData.get(methodFullPath)
				.getCommits();

		if (fileCommits == null) {
			fileCommits = new ArrayList<>();
			entityChangesData.get(methodFullPath).setCommits(fileCommits);
		}

		fileCommits.add(commit);
	}

	private void treatNodeSetEditRemove(final String fileName,
			final Commit commit, final String className,
			final NodeSetEdit memberEdit) {

		if (((NodeSetEdit.Remove) memberEdit).getNodeType().getQualifiedName()
				.equals(Node.Function.class.getCanonicalName())) {

			final String methodSignature = ((NodeSetEdit.Remove) memberEdit)
					.getIdentifier();
			final String methodFullPath = fileName + " -> " + className + " -> "
					+ methodSignature;
			final Integer deletedLines = entityChangesData.get(methodFullPath)
					.getActualSize();
			final List<Integer> authorsNewChanges = new ArrayList<>(
					Arrays.asList(0, deletedLines));

			updateMethodsAuthorsChanges(methodFullPath, commit.getAuthor(),
					authorsNewChanges);

			addCommitToList(methodFullPath, commit);
		}
	}

	private void treatNodeType(final String fileName, final Commit commit,
			final Node member) {

		final String className = ((Node.Type) member).getName();

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

	private void updateEntitySizeValues(final String methodFullPath,
			final List<Integer> authorsNewChanges) {

		final Integer currentMethodSize = entityChangesData.get(methodFullPath)
				.getActualSize();
		final Integer newMethodSize = currentMethodSize
				+ (authorsNewChanges.get(0) - authorsNewChanges.get(1));

		entityChangesData.get(methodFullPath).setActualSize(newMethodSize);

		final Integer currentAddedAndDeletedLines = entityChangesData
				.get(methodFullPath).getAddedAndDeletedLinesSum();
		final Integer newAddedAndDeletedLines = currentAddedAndDeletedLines
				+ (authorsNewChanges.get(0) + authorsNewChanges.get(1));

		entityChangesData.get(methodFullPath)
				.setAddedAndDeletedLinesSum(newAddedAndDeletedLines);
	}

	private void updateMethodsAuthorsChanges(final String methodFullPath,
			final String author, final List<Integer> authorsNewChanges) {

		final List<Integer> currentAuthorsChanges = entityChangesData
				.get(methodFullPath).getAuthorsAddedAndDeletedLines()
				.get(author);
		final Integer increasedMethodChanges = entityChangesData
				.get(methodFullPath).getNumberOfChanges() + 1;
		final Integer currentAddedLines = setCurrentAddedLines(
				authorsNewChanges, currentAuthorsChanges);
		final Integer currentDeletedLines = setCurrentDeletedLines(
				authorsNewChanges, currentAuthorsChanges);
		final List<Integer> newMethodsChanges = new ArrayList<>(
				Arrays.asList(currentAddedLines, currentDeletedLines));

		entityChangesData.get(methodFullPath).getAuthorsAddedAndDeletedLines()
				.put(author, newMethodsChanges);
		entityChangesData.get(methodFullPath)
				.setNumberOfChanges(increasedMethodChanges);

		updateEntitySizeValues(methodFullPath, authorsNewChanges);

		setEntityOwnerAfterCommit(methodFullPath);
	}

	@Override
	public void writeFileResults() {
		try {

			for (final HashMap.Entry<String, FileChangesData> entry : entityChangesData
					.entrySet()) {
				final ArrayList<String> methodOwnershipInformationLine = addMethodOwnershipInformation(
						entry.getKey());

				CSVUtils.writeLine(csvWriter, methodOwnershipInformationLine,
						',', '"');
			}
		} catch (final IOException e) {
			e.printStackTrace();

		}
	}
}