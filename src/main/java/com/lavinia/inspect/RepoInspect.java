package com.lavinia.inspect;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.lavinia.visitor.NodeVisitor;

public class RepoInspect {

	public final static Logger logger = Logger.getLogger(RepoInspect.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			/*
			 * Process process2 = Runtime.getRuntime().exec(
			 * "/home/lavinia/Licenta/bootique/metanalysis", null, new
			 * File("/home/lavinia/Licenta/bootique"));
			 */
			PersistentProject project = PersistentProject.load();
			logger.info("The resulted instance of Project after load is: " + project);
			/*
			 * Set<String> filesList = project.listFiles(); for (String file :
			 * filesList) { logger.info("file: " + file); }
			 */
			List<HistoryEntry> fileHistory = project.getFileHistory("bootique/src/main/java/io/bootique/Bootique.java");
			/*for (HistoryEntry he : fileHistory) {
				logger.info("history entry: " + he.toString());
			}*/
			SourceFile fileModel = project.getFileModel("bootique/src/main/java/io/bootique/Bootique.java");
			// logger.info("File model:" + fileModel.toString());
			/*
			 * logger.info("\n\n\n\n\n"); Set<Node> fileModelNodes =
			 * fileModel.component1(); for (Node n : fileModelNodes) {
			 * logger.info("Components: " + n); }
			 */
			Set<Node> fileModelNodes = fileModel.getNodes();
			NodeVisitor visitor = new NodeVisitor();
			for (Node n : fileModelNodes) {
				visitor.visit(n);
			}
			logger.info("Final result map: ");

			Map<String, Integer> map = visitor.getFunctionSize();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				logger.info(entry.getKey() + " - " + entry.getValue());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
