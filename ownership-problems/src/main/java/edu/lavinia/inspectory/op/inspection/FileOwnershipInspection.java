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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.beans.FileChangesData;
import edu.lavinia.inspectory.op.visitor.EditVisitor;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class FileOwnershipInspection extends GenericOwnershipInspection {

	private final ArrayList<Commit> allCommits = new ArrayList<>();

	private Commit firstRepositoryCommit;
	private Commit lastRepositoryCommit;

	/**
	 * FileOwnershipInspection Constructor that receives the persistent project
	 * and the CSV file to write in from {@code inspectory-main}.
	 *
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public FileOwnershipInspection(final Optional<PersistentProject> project,
			final FileWriter csvWriter, final FileWriter jsonWriter) {
		super(project, csvWriter, jsonWriter);
	}

	private void addFileGeneralInformation(
			final ArrayList<String> fileOwnershipInformationLine,
			final String fileName) {

		fileOwnershipInformationLine.add(fileName);
		fileOwnershipInformationLine.add(entityChangesData.get(fileName)
				.getNumberOfChanges().toString());
		fileOwnershipInformationLine.add(String.valueOf(entityChangesData
				.get(fileName).getAuthorsNumberOfChanges().size()));
		fileOwnershipInformationLine
				.add(entityChangesData.get(fileName).getEntityCreator());
		fileOwnershipInformationLine.add(entityChangesData.get(fileName)
				.getAuthorsNumberOfChanges().toString());
	}

	public void addFileInformation(final String fileName,
			final Integer numberOfChanges, final String fileCreator,
			final LinkedHashMap<String, Integer> authorsNumberOfChanges,
			final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines,
			final LinkedHashMap<String, Double> ownershipPercentages,
			final ArrayList<String> distinctListOfOwners) {

		final FileChangesData fileOwnershipInformation = entityChangesData
				.get(fileName);
		fileOwnershipInformation.setNumberOfChanges(numberOfChanges);
		fileOwnershipInformation.setEntityCreator(fileCreator);
		fileOwnershipInformation
				.setAuthorsNumberOfChanges(authorsNumberOfChanges);
		fileOwnershipInformation
				.setAuthorsAddedAndDeletedLines(authorsAddedAndDeletedLines);
		fileOwnershipInformation.setOwnershipPercentages(ownershipPercentages);
		fileOwnershipInformation.setDistinctOwners(distinctListOfOwners);
	}

	private void addFileOwnershipInformation(
			final ArrayList<String> fileOwnershipInformationLine,
			final FileChangesData fileOwnershipInformation) {

		fileOwnershipInformationLine.add(String
				.valueOf(fileOwnershipInformation.getDistinctOwners().size()));
		fileOwnershipInformationLine
				.add(fileOwnershipInformation.getDistinctOwners().toString());
		fileOwnershipInformationLine.add(
				fileOwnershipInformation.getOwnershipPercentages().toString());
	}

	private ArrayList<String> addOwnershipResultLine(
			final HashMap.Entry<String, FileChangesData> entry) {

		final ArrayList<String> fileOwnershipInformationLine = new ArrayList<>();
		final String fileName = entry.getKey();
		final FileChangesData fileOwnershipInformation = entry.getValue();

		addFileGeneralInformation(fileOwnershipInformationLine, fileName);

		addFileOwnershipInformation(fileOwnershipInformationLine,
				fileOwnershipInformation);

		fileOwnershipInformationLine.add(fileOwnershipInformation
				.getAuthorsAddedAndDeletedLines().toString());

		return fileOwnershipInformationLine;
	}

	private LinkedHashMap<String, List<Integer>> checkIfHistoryEditHasTransaction(
			final String fileName, final GenericVisitor visitor,
			LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines,
			final HistoryEntry historyEntry) {

		final SourceFileTransaction sourceFileTransaction = historyEntry
				.getTransaction();

		if (sourceFileTransaction == null) {
			final ArrayList<Integer> changedLines = (ArrayList<Integer>) authorsAddedAndDeletedLines
					.get(historyEntry.getAuthor());

			authorsAddedAndDeletedLines = checkChangedLinesInMap(changedLines,
					authorsAddedAndDeletedLines, historyEntry.getAuthor(), 0,
					0);
		} else {
			final List<NodeSetEdit> nodeEditList = sourceFileTransaction
					.getNodeEdits();

			authorsAddedAndDeletedLines = treatEachNodeSetEdit(visitor,
					authorsAddedAndDeletedLines, historyEntry, nodeEditList,
					fileName);
		}

		return authorsAddedAndDeletedLines;
	}

	public void createResultForEachFile(final String fileName) {
		try {
			final List<HistoryEntry> fileHistory = project.get()
					.getFileHistory(fileName);

			final GenericVisitor visitor = new EditVisitor(fileName);

			initEntityLinesMap(fileName);

			treatEachHistoryEntry(fileName, fileHistory, visitor);

			sortAllCommits();

			firstRepositoryCommit = allCommits.get(0);
			lastRepositoryCommit = allCommits.get(allCommits.size() - 1);
		} catch (final IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	public void sortAllCommits() {
		Collections.sort(allCommits, new Comparator<Commit>() {
			@Override
			public int compare(final Commit commit1, final Commit commit2) {
				return commit1.getDate().compareTo(commit2.getDate());
			}
		});
	}

	@Override
	public void createResults() {
		final Set<String> filesList = project.get().listFiles();

		for (final String fileName : filesList) {
			if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
				continue;
			}

			createResultForEachFile(fileName);
		}
	}

	private void initEntityLinesMap(final String fileName) {
		final FileChangesData fileChangesData = new FileChangesData();
		fileChangesData.setAuthorsNumberOfChanges(new LinkedHashMap<>());
		fileChangesData.setAuthorsAddedAndDeletedLines(new LinkedHashMap<>());
		fileChangesData.setOwnershipPercentages(new LinkedHashMap<>());
		fileChangesData.setAllOwners(new ArrayList<>());
		fileChangesData.setDistinctOwners(new ArrayList<>());

		entityChangesData.put(fileName, fileChangesData);
	}

	private void initVisitorAddedAndDeletedLines(final GenericVisitor visitor) {
		((EditVisitor) visitor).setAddedLines(0);
		((EditVisitor) visitor).setDeletedLines(0);
	}

	private void setCommitInformation(final HistoryEntry historyEntry,
			final Commit commit) {

		commit.setRevision(historyEntry.getRevision());
		commit.setAuthor(historyEntry.getAuthor());
		commit.setDate(historyEntry.getDate());
	}

	private String setFileCreator(String fileCreator,
			final HistoryEntry historyEntry) {

		if (fileCreator == null) {
			fileCreator = historyEntry.getAuthor();
		}

		return fileCreator;
	}

	private void setFileOwnershipValues(final String fileName,
			final int numberOfChanges, final String fileCreator,
			final LinkedHashMap<String, Integer> authorsNumberOfChanges,
			final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines) {

		LinkedHashMap<String, Double> ownershipPercentages = calculateEntityOwnership(
				fileName);
		ownershipPercentages = sortPercentagesMap(ownershipPercentages);

		final List<String> listOfAllOwners = entityChangesData.get(fileName)
				.getAllOwners();
		final List<String> distinctListOfOwners = listOfAllOwners.stream()
				.distinct().collect(Collectors.toList());
		final ArrayList<String> distinctOwners = new ArrayList<>(
				distinctListOfOwners);

		addFileInformation(fileName, numberOfChanges, fileCreator,
				authorsNumberOfChanges, authorsAddedAndDeletedLines,
				ownershipPercentages, distinctOwners);
	}

	private void treatEachHistoryEntry(final String fileName,
			final List<HistoryEntry> fileHistory,
			final GenericVisitor visitor) {

		int numberOfChanges = 0;
		String fileCreator = null;
		final LinkedHashMap<String, Integer> authorsNumberOfChanges = new LinkedHashMap<>();
		LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines = new LinkedHashMap<>();

		for (final HistoryEntry historyEntry : fileHistory) {
			try {
				final Commit commit = new Commit();
				setCommitInformation(historyEntry, commit);

				++numberOfChanges;
				initVisitorAddedAndDeletedLines(visitor);
				fileCreator = setFileCreator(fileCreator, historyEntry);
				updateAuthorNumberOfChanges(authorsNumberOfChanges,
						historyEntry);

				authorsAddedAndDeletedLines = checkIfHistoryEditHasTransaction(
						fileName, visitor, authorsAddedAndDeletedLines,
						historyEntry);

				entityChangesData.get(fileName).setAuthorsAddedAndDeletedLines(
						authorsAddedAndDeletedLines);
				setEntityOwnerAfterCommit(fileName);

				addCommitToList(fileName, commit);

				allCommits.add(commit);
			} catch (final Exception e) {
				continue;
			}
		}

		setFileOwnershipValues(fileName, numberOfChanges, fileCreator,
				authorsNumberOfChanges, authorsAddedAndDeletedLines);
	}

	private void addCommitToList(final String fileName, final Commit commit) {
		ArrayList<Commit> fileCommits = entityChangesData.get(fileName)
				.getCommits();

		if (fileCommits == null) {
			fileCommits = new ArrayList<>();
			entityChangesData.get(fileName).setCommits(fileCommits);
		}

		fileCommits.add(commit);
	}

	private LinkedHashMap<String, List<Integer>> treatEachNodeSetEdit(
			final GenericVisitor visitor,
			LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines,
			final HistoryEntry historyEntry,
			final List<NodeSetEdit> nodeEditList, final String fileName) {

		for (final NodeSetEdit edit : nodeEditList) {
			((EditVisitor) visitor).visit(edit);

			final ArrayList<Integer> changedLines = (ArrayList<Integer>) authorsAddedAndDeletedLines
					.get(historyEntry.getAuthor());

			updateEntitySizeValues(visitor, fileName);

			authorsAddedAndDeletedLines = checkChangedLinesInMap(changedLines,
					authorsAddedAndDeletedLines, historyEntry.getAuthor(),
					((EditVisitor) visitor).getAddedLines(),
					((EditVisitor) visitor).getDeletedLines());
		}

		return authorsAddedAndDeletedLines;
	}

	private void updateAuthorNumberOfChanges(
			final LinkedHashMap<String, Integer> authorsNumberOfChanges,
			final HistoryEntry historyEntry) {

		Integer numberOfChangesAuthorHas = authorsNumberOfChanges
				.get(historyEntry.getAuthor());

		if (numberOfChangesAuthorHas == null) {
			authorsNumberOfChanges.put(historyEntry.getAuthor(), 1);
		} else {
			authorsNumberOfChanges.put(historyEntry.getAuthor(),
					++numberOfChangesAuthorHas);
		}
	}

	private void updateEntitySizeValues(final GenericVisitor visitor,
			final String fileName) {

		final FileChangesData fileChangesData = entityChangesData.get(fileName);
		Integer currentAddedAndDeletedLines = fileChangesData
				.getAddedAndDeletedLinesSum();
		currentAddedAndDeletedLines += ((EditVisitor) visitor).getAddedLines()
				+ ((EditVisitor) visitor).getDeletedLines();

		fileChangesData.setAddedAndDeletedLinesSum(currentAddedAndDeletedLines);

		Integer currentEntitySize = fileChangesData.getActualSize();
		currentEntitySize += ((EditVisitor) visitor).getAddedLines()
				- ((EditVisitor) visitor).getDeletedLines();

		fileChangesData.setActualSize(currentEntitySize);
	}

	@Override
	public void writeFileResults() {
		try {
			for (final HashMap.Entry<String, FileChangesData> entry : entityChangesData
					.entrySet()) {
				final ArrayList<String> fileOwnershipInformationLine = addOwnershipResultLine(
						entry);

				CSVUtils.writeLine(csvWriter, fileOwnershipInformationLine, ',',
						'"');
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public Commit getFirstRepositoryCommit() {
		return firstRepositoryCommit;
	}

	public Commit getLastRepositoryCommit() {
		return lastRepositoryCommit;
	}

}
