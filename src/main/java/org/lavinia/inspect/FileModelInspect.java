package org.lavinia.inspect;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.lavinia.visitor.NodeVisitor;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;
import org.metanalysis.core.project.PersistentProject;

public class FileModelInspect {
	private static PersistentProject project = null;

	public FileModelInspect(PersistentProject project) {
		FileModelInspect.project = project;
	}

	public void getModelFunctionsAnalyze() {

		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String file : filesList) {
				// logger.info("file: " + file);
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}
				SourceFile fileModel = project.getFileModel(file);

				String logFilePath = "./" + logFolderName + "/" + file + ".model";
				Logger logger = Logger.getRootLogger();
				FileAppender appender = (FileAppender) logger.getAppender("file");
				appender.setFile(logFilePath);
				appender.activateOptions();

				Set<Node> fileModelNodes = fileModel.getNodes();
				NodeVisitor visitor = new NodeVisitor(logger);
				for (Node n : fileModelNodes) {
					visitor.visit(n);
				}
				// logger.info("Final result map: ");

				Map<String, Integer> map = visitor.getFunctionSize();
				for (Map.Entry<String, Integer> entry : map.entrySet()) {
					logger.info(entry.getKey() + " - " + entry.getValue());
				}
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
