package com.lavinia.inspect;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;
import org.metanalysis.core.project.PersistentProject;

import com.lavinia.visitor.NodeVisitor;

public class RepoInspect {

	public final static Logger logger = Logger.getLogger(RepoInspect.class.getName());

	public static PersistentProject getProject() {
		try {
			return PersistentProject.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getFinalMap() {
		PersistentProject project = getProject();

		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String file : filesList) {
				// logger.info("file: " + file);
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}
				SourceFile fileModel = project.getFileModel(file);

				String logFilePath = "./" + logFolderName + "/" + file + ".log";
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

	public static void main(String[] args) {
		getFinalMap();
	}

}
