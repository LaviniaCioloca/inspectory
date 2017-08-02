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
import org.metanalysis.core.delta.FunctionTransaction;
import org.metanalysis.core.delta.ListEdit;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.lavinia.versioning.Commit;

public class FileHistoryInspect {
	private static PersistentProject project = null;
	private Map<String, ArrayList<Integer>> result = null;

	public FileHistoryInspect(PersistentProject project) {
		FileHistoryInspect.project = project;
		result = new HashMap<String, ArrayList<Integer>>();
	}

	@SuppressWarnings("rawtypes")
	public void getHistoryFunctionsAnalyze() {
		try {
			String logFolderName = "results";
			Set<String> filesList = project.listFiles();
			for (String file : filesList) {
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}
				// System.out.println("\n\nfile: " + file);
				List<HistoryEntry> fileHistory = project.getFileHistory(file);

				String logFilePath = "./" + logFolderName + "/" + file + ".history";
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
						logger.info("----------------------------------------------------------\n");
						logger.info(commit.toString());
						ArrayList<Integer> lineChanges = null;
						SourceFileTransaction sourceFileTransaction = he.getTransaction();
						List<NodeSetEdit> nodeEditList = sourceFileTransaction.getNodeEdits();

						for (final NodeSetEdit edit : nodeEditList) {

							if (edit instanceof NodeSetEdit.Change<?>) {
								Transaction t = ((NodeSetEdit.Change) edit).getTransaction();
								List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
								for (NodeSetEdit me : memberEdits) {
									Integer total = 0;
									String identifier = null;
									if (me instanceof NodeSetEdit.Add) {
										Node n = ((NodeSetEdit.Add) me).getNode();
										identifier = file + ":\t" + ((NodeSetEdit.Add) me).getNode().getIdentifier();
										logger.info("\n" + identifier);
										List<String> body = ((Node.Function) n).getBody();
										logger.info("Add: +" + body.size() + ": " + body);
										total += body.size();
									} else if (me instanceof NodeSetEdit.Change) {
										identifier = file + ":\t" + ((NodeSetEdit.Change) me).getIdentifier();
										logger.info(identifier);
										Transaction t1 = ((NodeSetEdit.Change) me).getTransaction();
										List<ListEdit<String>> bodyEdits = ((FunctionTransaction) t1).getBodyEdits();
										for (ListEdit<String> le : bodyEdits) {
											if (le instanceof ListEdit.Add<?>) {
												logger.info("Change: +1: " + le);
												total += 1;
											} else if (le instanceof ListEdit.Remove<?>) {
												logger.info("Change: -1: " + le);
												total -= 1;
											}
										}
									} else {
										identifier = file + ":\t" + ((NodeSetEdit.Remove) me).getIdentifier();
										logger.info("Remove: " + identifier);
										total -= 1;
									}
									if (result.get(identifier) != null) {
										result.get(identifier).add(total);
									} else {
										lineChanges = new ArrayList<Integer>();
										lineChanges.add(total);
										result.put(identifier, lineChanges);
									}
									logger.info("---> Total: " + (total > 0 ? "+" + total : total) + "\n");
								}
							} else if (edit instanceof NodeSetEdit.Add) {
								Node node = ((NodeSetEdit.Add) edit).getNode();
								String identifier = null;
								if (node instanceof Node.Type) {
									Set<Node> members = ((Node.Type) node).getMembers();
									for (Node n : members) {
										int total = 0;
										if (n instanceof Node.Function) {
											identifier = file + ":\t" + ((Node.Function) n).getSignature();
											logger.info(identifier);
											logger.info("Add: +" + ((Node.Function) n).getBody().size() + " "
													+ ((Node.Function) n).getBody());
											total += ((Node.Function) n).getBody().size();
											if (result.get(identifier) != null) {
												result.get(identifier).add(total);
											} else {
												lineChanges = new ArrayList<Integer>();
												lineChanges.add(total);
												result.put(identifier, lineChanges);
											}
											logger.info("---> Total: +" + total + "\n");
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
