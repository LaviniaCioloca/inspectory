package com.lavinia.inspect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.lavinia.versioning.Commit;
import com.lavinia.visitor.EditVisitor;
import com.lavinia.visitor.GenericVisitor;
import com.lavinia.visitor.NodeVisitor;

public class FileHistoryInspect {
	private static PersistentProject project = null;
	private Map<String, ArrayList<Integer>> result = null;

	public FileHistoryInspect(PersistentProject project) {
		FileHistoryInspect.project = project;
		result = new HashMap<String, ArrayList<Integer>>();
	}

	public void addToResult(GenericVisitor visitor, ArrayList<Integer> lineChanges, Logger logger) {
		if (result.get(visitor.getIdentifier()) != null) {
			result.get(visitor.getIdentifier()).add(visitor.getTotal());
		} else {
			lineChanges = new ArrayList<Integer>();
			lineChanges.add(visitor.getTotal());
			result.put(visitor.getIdentifier(), lineChanges);
		}
		//logger.info("---> Total: " + (visitor.getTotal() > 0 ? "+" + visitor.getTotal() : visitor.getTotal()) + "\n");
	}

	@SuppressWarnings("rawtypes")
	public void getHistoryFunctionsAnalyze() {
		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}
				// System.out.println("\n\nfile: " + file);
				List<HistoryEntry> fileHistory = project.getFileHistory(fileName);

				String logFilePath = "./" + logFolderName + "/" + fileName + ".history";
				Logger logger = Logger.getRootLogger();
				FileAppender appender = (FileAppender) logger.getAppender("file");
				appender.setFile(logFilePath);
				appender.activateOptions();

				for (HistoryEntry he : fileHistory) {
					try {
						Commit commit = new Commit();
						commit.setRevision(he.getRevision());
						commit.setAuthor(he.getAuthor());
						commit.setDate(he.getDate());
						//logger.info("----------------------------------------------------------\n");
						//logger.info(commit.toString());
						ArrayList<Integer> lineChanges = null;
						SourceFileTransaction sourceFileTransaction = he.getTransaction();
						List<NodeSetEdit> nodeEditList = sourceFileTransaction.getNodeEdits();
						GenericVisitor visitor = null;

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								Transaction t = ((NodeSetEdit.Change) edit).getTransaction();
								List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
								for (NodeSetEdit me : memberEdits) {
									visitor = new EditVisitor(logger, fileName);
									((EditVisitor) visitor).visit(me);
									addToResult(visitor, lineChanges, logger);
								}
							} else if (edit instanceof NodeSetEdit.Add) {
								Node node = ((NodeSetEdit.Add) edit).getNode();
								if (node instanceof Node.Type) {
									visitor = new NodeVisitor(logger, fileName);
									Set<Node> members = ((Node.Type) node).getMembers();
									for (Node n : members) {
										if (n instanceof Node.Function) {
											((NodeVisitor) visitor).visit(n);
											addToResult(visitor, lineChanges, logger);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						continue;
					}

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
		long startTime = System.nanoTime();

		List<ArrayList<Integer>> l = new ArrayList<>(result.values());
		Collections.sort(l, new Comparator<ArrayList<Integer>>() {
			public int compare(ArrayList<Integer> s1, ArrayList<Integer> s2) {
				return Integer.compare(s2.size(), s1.size());
			}
		});
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		System.out.println("Duration of sort is: " + duration + "ms");

		startTime = System.nanoTime();
		for (ArrayList<Integer> a : l) {
			Iterator<Entry<String, ArrayList<Integer>>> iter = result.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, ArrayList<Integer>> e = iter.next();
				if (e.getValue().equals(a)) {

					System.out.println(e.getKey() + "-" + a + "; size: " + a.size() + "\n");
					iter.remove();
				}
			}
		}
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000;
		System.out.println("Duration of writing to file is: " + duration + "ms");
	}
}
