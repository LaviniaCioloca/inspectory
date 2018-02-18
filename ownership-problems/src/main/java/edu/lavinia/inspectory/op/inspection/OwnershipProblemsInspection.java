package edu.lavinia.inspectory.op.inspection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.beans.FileOwnershipInformation;
import edu.lavinia.inspectory.utils.CSVUtils;

public class OwnershipProblemsInspection {
	private PersistentProject project = null;
	private FileWriter csvWriter = null;
	private HashMap<String, FileOwnershipInformation> fileOwnershipResult = null;

	/**
	 * OwnershipProblemsInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public OwnershipProblemsInspection(PersistentProject project, FileWriter csvWriter) {
		this.project = project;
		this.csvWriter = csvWriter;
	}

	public void writeFileResults() {
		try {
			Set<String> filesList = project.listFiles();
			fileOwnershipResult = new HashMap<>();

			for (String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}
				List<HistoryEntry> fileHistory = project.getFileHistory(fileName);

				int numberOfChanges = 0;
				String fileOwner = null;
				HashMap<String, Integer> authorsChanges = new HashMap<>();

				for (HistoryEntry historyEntry : fileHistory) {
					try {
						Commit commit = new Commit();
						commit.setRevision(historyEntry.getRevision());
						commit.setAuthor(historyEntry.getAuthor());
						commit.setDate(historyEntry.getDate());

						++numberOfChanges;
						if (fileOwner == null) {
							fileOwner = historyEntry.getAuthor();
						}

						Integer numberOfChangesAuthorHas = authorsChanges.get(historyEntry.getAuthor());
						if (numberOfChangesAuthorHas != null) {
							authorsChanges.put(historyEntry.getAuthor(), ++numberOfChangesAuthorHas);
						} else {
							authorsChanges.put(historyEntry.getAuthor(), 1);
						}
					} catch (Exception e) {
						continue;
					}
				}

				FileOwnershipInformation fileOwnershipInformation = new FileOwnershipInformation();
				fileOwnershipInformation.setNumberOfChanges(numberOfChanges);
				fileOwnershipInformation.setFileOwner(fileOwner);
				fileOwnershipInformation.setAuthorsChanges(authorsChanges);

				fileOwnershipResult.put(fileName, fileOwnershipInformation);
			}

			for (HashMap.Entry<String, FileOwnershipInformation> entry : fileOwnershipResult.entrySet()) {
				ArrayList<String> fileOwnershipInformationLine = new ArrayList<>();
				String fileName = entry.getKey();
				FileOwnershipInformation fileOwnershipInformation = entry.getValue();
				fileOwnershipInformationLine.add(fileName);
				fileOwnershipInformationLine.add(fileOwnershipInformation.getFileOwner());
				fileOwnershipInformationLine.add(fileOwnershipInformation.getNumberOfChanges().toString());
				fileOwnershipInformationLine.add(fileOwnershipInformation.getAuthorsChanges().toString());

				CSVUtils.writeLine(csvWriter, fileOwnershipInformationLine, ',', '"');
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
