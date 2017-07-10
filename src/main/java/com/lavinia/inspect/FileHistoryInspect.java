package com.lavinia.inspect;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

public class FileHistoryInspect {
	private static PersistentProject project = null;

	public FileHistoryInspect(PersistentProject project) {
		FileHistoryInspect.project = project;
	}

	public void getHistoryFunctionsAnalyze() {
		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String file : filesList) {
				// logger.info("file: " + file);
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}
				List<HistoryEntry> fileHistory = project.getFileHistory(file);

				String logFilePath = "./" + logFolderName + "/" + file + ".history";
				Logger logger = Logger.getRootLogger();
				FileAppender appender = (FileAppender) logger.getAppender("file");
				appender.setFile(logFilePath);
				appender.activateOptions();

			}

		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
			// e.printStackTrace();
		}
	}
}
