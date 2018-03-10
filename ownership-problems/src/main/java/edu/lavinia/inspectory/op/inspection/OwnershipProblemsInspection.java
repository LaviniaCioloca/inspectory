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
