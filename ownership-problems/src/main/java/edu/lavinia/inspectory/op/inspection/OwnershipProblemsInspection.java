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

import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.beans.FileOwnershipInformation;
import edu.lavinia.inspectory.utils.CSVUtils;

public class OwnershipProblemsInspection {
	private final PersistentProject project;
	private final FileWriter csvWriter;
	private final HashMap<String, FileOwnershipInformation> fileOwnershipResult = new HashMap<>();

	/**
	 * OwnershipProblemsInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public OwnershipProblemsInspection(PersistentProject project,
			FileWriter csvWriter) {
		this.project = project;
		this.csvWriter = csvWriter;
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

				int numberOfChanges = 0;
				String fileCreator = null;
				final LinkedHashMap<String, Integer> authorsChanges = new LinkedHashMap<>();

				for (final HistoryEntry historyEntry : fileHistory) {
					try {
						final Commit commit = new Commit();
						commit.setRevision(historyEntry.getRevision());
						commit.setAuthor(historyEntry.getAuthor());
						commit.setDate(historyEntry.getDate());

						++numberOfChanges;
						if (fileCreator == null) {
							fileCreator = historyEntry.getAuthor();
						}

						Integer numberOfChangesAuthorHas = authorsChanges
								.get(historyEntry.getAuthor());
						if (numberOfChangesAuthorHas != null) {
							authorsChanges.put(historyEntry.getAuthor(),
									++numberOfChangesAuthorHas);
						} else {
							authorsChanges.put(historyEntry.getAuthor(), 1);
						}
					} catch (Exception e) {
						continue;
					}
				}

				addFileInformation(fileName, numberOfChanges, fileCreator,
						authorsChanges);
			}
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	public void addFileInformation(String fileName, Integer numberOfChanges,
			String fileOwner, LinkedHashMap<String, Integer> authorsChanges) {
		final FileOwnershipInformation fileOwnershipInformation = new FileOwnershipInformation();
		fileOwnershipInformation.setNumberOfChanges(numberOfChanges);
		fileOwnershipInformation.setFileCreator(fileOwner);
		fileOwnershipInformation.setAuthorsChanges(authorsChanges);

		fileOwnershipResult.put(fileName, fileOwnershipInformation);
	}

	public void writeFileResults() {
		try {
			for (final HashMap.Entry<String, FileOwnershipInformation> entry : fileOwnershipResult
					.entrySet()) {
				final ArrayList<String> fileOwnershipInformationLine = new ArrayList<>();
				final String fileName = entry.getKey();
				final FileOwnershipInformation fileOwnershipInformation = entry
						.getValue();
				fileOwnershipInformationLine.add(fileName);
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getNumberOfChanges().toString());
				fileOwnershipInformationLine.add(String.valueOf(
						fileOwnershipInformation.getAuthorsChanges().size()));
				fileOwnershipInformationLine
						.add(fileOwnershipInformation.getFileCreator());
				fileOwnershipInformationLine.add(fileOwnershipInformation
						.getAuthorsChanges().toString());

				CSVUtils.writeLine(csvWriter, fileOwnershipInformationLine, ',',
						'"');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, FileOwnershipInformation> getFileOwnershipResult() {
		return fileOwnershipResult;
	}

}
