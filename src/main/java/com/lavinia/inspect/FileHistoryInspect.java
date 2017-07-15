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
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

public class FileHistoryInspect {
	private static PersistentProject project = null;

	public FileHistoryInspect(PersistentProject project) {
		FileHistoryInspect.project = project;
	}

	@SuppressWarnings("rawtypes")
	public void getHistoryFunctionsAnalyze() {
		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String file : filesList) {
				// logger.info("file: " + file);
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}
				System.out.println("\n\nfile: " + file);
				List<HistoryEntry> fileHistory = project.getFileHistory(file);

				String logFilePath = "./" + logFolderName + "/" + file + ".history";
				Logger logger = Logger.getRootLogger();
				FileAppender appender = (FileAppender) logger.getAppender("file");
				appender.setFile(logFilePath);
				appender.activateOptions();

				for (HistoryEntry he : fileHistory) {
					try {
						logger.info("\n----------------------------------------------\nRevision: " + he.getRevision());
						logger.info("Author: " + he.getAuthor());
						logger.info("Date: " + he.getDate());
						System.out.println("FileHistory size: " + fileHistory.size());
						SourceFileTransaction sourceFileTransaction = he.getTransaction();
						List<NodeSetEdit> nodeEditList = sourceFileTransaction.getNodeEdits();

						for (final NodeSetEdit edit : nodeEditList) {

							if (edit instanceof NodeSetEdit.Change<?>) {
								System.out.println("\nNode type: " + ((NodeSetEdit.Change) edit).getNodeType());
								Transaction t = ((NodeSetEdit.Change) edit).getTransaction();
								List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
								for (NodeSetEdit me : memberEdits) {
									if (me instanceof NodeSetEdit.Add) {
										Node n = ((NodeSetEdit.Add) me).getNode();
										logger.info(((NodeSetEdit.Add) me).getNode().getIdentifier());
										List<String> body = ((Node.Function) n).getBody();
										// System.out.println("Add -> body
										// edits: " + ((NodeSetEdit.Add)
										// me).getNode0());
										/*
										 * for (String str : body) {
										 * System.out.print(str); }
										 */
										System.out.println("Add body: " + body);
										logger.info("Add: +" + body.size() + ": " + body);
									} else if (me instanceof NodeSetEdit.Change) {
										logger.info(((NodeSetEdit.Change) me).getIdentifier());
										Transaction t1 = ((NodeSetEdit.Change) me).getTransaction();
										List<ListEdit<String>> bodyEdits = ((FunctionTransaction) t1).getBodyEdits();
										for (ListEdit<String> le : bodyEdits) {
											if (le instanceof ListEdit.Add<?>) {
												logger.info("Change: +1: " + le);
											} else if (le instanceof ListEdit.Remove<?>) {
												logger.info("Change: -1: " + le);
											}

										}
									} else {
										logger.info("Remove: " + ((NodeSetEdit.Remove) me).getIdentifier());
									}

								}
							} else if (edit instanceof NodeSetEdit.Add) {
								Node node = ((NodeSetEdit.Add) edit).getNode();
								if (node instanceof Node.Type) {
									Set<Node> members = ((Node.Type) node).getMembers();
									for (Node n : members) {
										if (n instanceof Node.Function) {
											logger.info("\nFunction: " + ((Node.Function) n).getSignature());
											logger.info("Add: +" + ((Node.Function) n).getBody().size() + " "
													+ ((Node.Function) n).getBody());
										}
									}
								}
							}
						}
					} catch (Exception e) {
						continue;
					}

				}

				/*
				 * for (HistoryEntry he : fileHistory) { try {
				 * sourceFileTransaction = he.getTransaction(); nodeEditList =
				 * sourceFileTransaction.getNodeEdits(); for (NodeSetEdit nse :
				 * nodeEditList) { System.out.println("NodeSetEdit class: " +
				 * nse.getClass().getName()); } } catch (Exception e) {
				 * 
				 * } }
				 */
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
