package com.lavinia.inspect;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.lavinia.visitor.NodeVisitor;

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

				NodeVisitor visitor = new NodeVisitor(logger);
				SourceFile sf = project.getFileModel(file);
				SourceFileTransaction sourceFileTransaction = null;
				List<NodeSetEdit> nodeEditList = null;

				try {
					for (final NodeSetEdit edit : nodeEditList) {
						if (edit instanceof NodeSetEdit.Change<?>) {
							if (((NodeSetEdit.Change<?>) edit).getNodeType().equals(Node.Function.class)) {
								Transaction t = ((NodeSetEdit.Change<?>) edit).getTransaction();
								FunctionTransaction ft = (FunctionTransaction) t;
								List<ListEdit<String>> bodyEdits = ft.getBodyEdits();
								for(ListEdit<String> be : bodyEdits) {
									System.out.println("Body edits: " + be);
								}
							}
						}
					}
				} catch (Exception e) {

				}
				/*
				for (HistoryEntry he : fileHistory) {
					try {
						sourceFileTransaction = he.getTransaction();
						nodeEditList = sourceFileTransaction.getNodeEdits();
						for (NodeSetEdit nse : nodeEditList) {
							System.out.println("NodeSetEdit class: " + nse.getClass().getName());
						}
					} catch (Exception e) {

					}
				}*/
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
