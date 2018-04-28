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
import java.util.Set;

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
	public FileOwnershipInspection(PersistentProject project,
			FileWriter csvWriter) {
		super(project, csvWriter);
	}

	@Override
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
				String fileCreator = null;
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
						if (fileCreator == null) {
							fileCreator = historyEntry.getAuthor();
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
							((EditVisitor) visitor).visit(edit);

							final ArrayList<Integer> changedLines = authorsLineChanges
									.get(historyEntry.getAuthor());

							/*
							 * System.out.println("\n\tFile: " + fileName +
							 * "; author: " + historyEntry.getAuthor() +
							 * "; visitorAddedLines: " + ((EditVisitor)
							 * visitor).getAddedLines() +
							 * "; visitorDeletedLines: " + ((EditVisitor)
							 * visitor) .getDeletedLines());
							 */

							authorsLineChanges = checkChangedLinesInMap(
									changedLines, authorsLineChanges,
									historyEntry.getAuthor(),
									((EditVisitor) visitor).getAddedLines(),
									((EditVisitor) visitor).getDeletedLines());
						}
					} catch (Exception e) {
						continue;
					}
				}

				LinkedHashMap<String, Double> ownershipPercentages = calculateFileOwnership(
						authorsLineChanges);
				ownershipPercentages = sortPercentagesMap(ownershipPercentages);

				addFileInformation(fileName, numberOfChanges, fileCreator,
						authorsChanges, authorsLineChanges,
						ownershipPercentages);
			}
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
			for (final HashMap.Entry<String, EntityOwnershipInformation> entry : entityOwnershipResult
					.entrySet()) {
				final ArrayList<String> fileOwnershipInformationLine = new ArrayList<>();
				final String fileName = entry.getKey();
				final EntityOwnershipInformation fileOwnershipInformation = entry
						.getValue();
				fileOwnershipInformationLine.add(fileName);
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getNumberOfChanges().toString());
				fileOwnershipInformationLine.add(String.valueOf(
						fileOwnershipInformation.getAuthorsChanges().size()));
				fileOwnershipInformationLine
						.add(fileOwnershipInformation.getEntityCreator());
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getAuthorsChanges().toString());
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getOwnershipPercentages().toString());
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getAuthorsLineChanges().toString());

				CSVUtils.writeLine(csvWriter, fileOwnershipInformationLine, ',',
						'"');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
