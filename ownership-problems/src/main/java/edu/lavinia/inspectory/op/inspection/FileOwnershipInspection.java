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
import edu.lavinia.inspectory.op.beans.EntityOwnershipInformation;
import edu.lavinia.inspectory.op.visitor.EditVisitor;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class FileOwnershipInspection extends GenericOwnershipInspection {

	/**
	 * FileOwnershipInspection Constructor that receives the persistent project
	 * and the CSV file to write in from {@code inspectory-main}.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public FileOwnershipInspection(Optional<PersistentProject> project,
			FileWriter csvWriter) {
		super(project, csvWriter);
	}

	public void addFileInformation(String fileName, Integer numberOfChanges,
			String fileCreator,
			LinkedHashMap<String, Integer> authorsNumberOfChanges,
			LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines,
			LinkedHashMap<String, Double> ownershipPercentages,
			ArrayList<String> distinctListOfOwners) {

		final EntityOwnershipInformation fileOwnershipInformation = new EntityOwnershipInformation();
		fileOwnershipInformation.setNumberOfChanges(numberOfChanges);
		fileOwnershipInformation.setEntityCreator(fileCreator);
		fileOwnershipInformation
				.setAuthorsNumberOfChanges(authorsNumberOfChanges);
		fileOwnershipInformation.setAuthorsNumberOfAddedAndDeletedLines(
				authorsAddedAndDeletedLines);
		fileOwnershipInformation.setOwnershipPercentages(ownershipPercentages);
		fileOwnershipInformation.setDistinctOwners(distinctListOfOwners);

		entityOwnershipResult.put(fileName, fileOwnershipInformation);
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

	public void createResultForEachFile(String fileName) {
		try {
			final List<HistoryEntry> fileHistory = project.get()
					.getFileHistory(fileName);

			final GenericVisitor visitor = new EditVisitor(fileName);

			entityAddedAndDeletedLines = 0;

			treatEachHistoryEntry(fileName, fileHistory, visitor);
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	private void treatEachHistoryEntry(String fileName,
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

				setFileOwnerAfterCommit(fileName, authorsAddedAndDeletedLines);
			} catch (Exception e) {
				continue;
			}
		}

		setFileOwnershipValues(fileName, numberOfChanges, fileCreator,
				authorsNumberOfChanges, authorsAddedAndDeletedLines);
	}

	private void setFileOwnershipValues(String fileName, int numberOfChanges,
			String fileCreator,
			final LinkedHashMap<String, Integer> authorsNumberOfChanges,
			LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines) {

		LinkedHashMap<String, Double> ownershipPercentages = calculateEntityOwnership(
				authorsAddedAndDeletedLines);
		ownershipPercentages = sortPercentagesMap(ownershipPercentages);

		final List<String> listOfAllOwners = entityOwners.get(fileName);
		final List<String> distinctListOfOwners = listOfAllOwners.stream()
				.distinct().collect(Collectors.toList());
		final ArrayList<String> distinctOwners = new ArrayList<>(
				distinctListOfOwners);

		addFileInformation(fileName, numberOfChanges, fileCreator,
				authorsNumberOfChanges, authorsAddedAndDeletedLines,
				ownershipPercentages, distinctOwners);
	}

	private LinkedHashMap<String, List<Integer>> checkIfHistoryEditHasTransaction(
			String fileName, final GenericVisitor visitor,
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

			System.out.println(
					"\nFile: " + fileName + "; has sourceFileTransaction null");
		} else {
			final List<NodeSetEdit> nodeEditList = sourceFileTransaction
					.getNodeEdits();

			authorsAddedAndDeletedLines = treatEachNodeSetEdit(visitor,
					authorsAddedAndDeletedLines, historyEntry, nodeEditList);
		}

		System.out
				.println("File: " + fileName + "; author's added and deleted: "
						+ authorsAddedAndDeletedLines);

		return authorsAddedAndDeletedLines;
	}

	private void setFileOwnerAfterCommit(final String fileName,
			final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines) {

		LinkedHashMap<String, Double> ownershipPercentages = calculateEntityOwnership(
				authorsAddedAndDeletedLines);
		ownershipPercentages = sortPercentagesMap(ownershipPercentages);

		final String fileOwnerAfterThisCommit = ownershipPercentages.entrySet()
				.iterator().next().getKey();
		List<String> listOfPreviousOwners = entityOwners.get(fileName);

		if (listOfPreviousOwners == null) {
			listOfPreviousOwners = new ArrayList<>();
			entityOwners.put(fileName, listOfPreviousOwners);
		}

		listOfPreviousOwners.add(fileOwnerAfterThisCommit);
	}

	private LinkedHashMap<String, List<Integer>> treatEachNodeSetEdit(
			final GenericVisitor visitor,
			LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines,
			final HistoryEntry historyEntry,
			final List<NodeSetEdit> nodeEditList) {

		for (final NodeSetEdit edit : nodeEditList) {
			((EditVisitor) visitor).visit(edit);

			final ArrayList<Integer> changedLines = (ArrayList<Integer>) authorsAddedAndDeletedLines
					.get(historyEntry.getAuthor());

			entityAddedAndDeletedLines += ((EditVisitor) visitor)
					.getAddedLines()
					+ ((EditVisitor) visitor).getDeletedLines();
			entityCurrentSize += ((EditVisitor) visitor).getAddedLines()
					- ((EditVisitor) visitor).getDeletedLines();

			authorsAddedAndDeletedLines = checkChangedLinesInMap(changedLines,
					authorsAddedAndDeletedLines, historyEntry.getAuthor(),
					((EditVisitor) visitor).getAddedLines(),
					((EditVisitor) visitor).getDeletedLines());
		}

		return authorsAddedAndDeletedLines;
	}

	private String setFileCreator(String fileCreator,
			final HistoryEntry historyEntry) {

		if (fileCreator == null) {
			fileCreator = historyEntry.getAuthor();
		}

		return fileCreator;
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

	@Override
	public void writeFileResults() {
		try {
			for (final HashMap.Entry<String, EntityOwnershipInformation> entry : entityOwnershipResult
					.entrySet()) {
				final ArrayList<String> fileOwnershipInformationLine = addOwnershipResultLine(
						entry);

				CSVUtils.writeLine(csvWriter, fileOwnershipInformationLine, ',',
						'"');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> addOwnershipResultLine(
			final HashMap.Entry<String, EntityOwnershipInformation> entry) {

		final ArrayList<String> fileOwnershipInformationLine = new ArrayList<>();
		final String fileName = entry.getKey();
		final EntityOwnershipInformation fileOwnershipInformation = entry
				.getValue();
		fileOwnershipInformationLine.add(fileName);
		fileOwnershipInformationLine
				.add(fileOwnershipInformation.getNumberOfChanges().toString());
		fileOwnershipInformationLine.add(String.valueOf(
				fileOwnershipInformation.getAuthorsNumberOfChanges().size()));
		fileOwnershipInformationLine
				.add(fileOwnershipInformation.getEntityCreator());
		fileOwnershipInformationLine.add(fileOwnershipInformation
				.getAuthorsNumberOfChanges().toString());
		fileOwnershipInformationLine.add(String
				.valueOf(fileOwnershipInformation.getDistinctOwners().size()));
		fileOwnershipInformationLine
				.add(fileOwnershipInformation.getDistinctOwners().toString());
		fileOwnershipInformationLine.add(
				fileOwnershipInformation.getOwnershipPercentages().toString());
		fileOwnershipInformationLine.add(fileOwnershipInformation
				.getAuthorsNumberOfAddedAndDeletedLines().toString());

		return fileOwnershipInformationLine;
	}
}
