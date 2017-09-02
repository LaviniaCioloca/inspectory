/*******************************************************************************
 * Copyright (c) 2017 Lavinia Cioloca
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.lavinia.inspect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;
import org.lavinia.utils.CSVUtils;
import org.lavinia.visitor.EditVisitor;
import org.lavinia.visitor.GenericVisitor;
import org.lavinia.visitor.NodeVisitor;
import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

public class FileHistoryInspect {
	private static PersistentProject project = null;
	private Map<String, ArrayList<Integer>> result = null;
	private ArrayList<String> deletedNodes = null;
	private FileWriter csvWriter = null;

	/**
	 * FileHistoryInspect Constructor that initializes the result map and csv
	 * writer to file.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result csv file.
	 */
	public FileHistoryInspect(PersistentProject project, FileWriter csvWriter) {
		FileHistoryInspect.project = project;
		result = new HashMap<String, ArrayList<Integer>>();
		deletedNodes = new ArrayList<String>();
		this.csvWriter = csvWriter;
	}

	/**
	 * Checking if the method exists in resultSet in order to add values to
	 * changes list or to create a new one.
	 * 
	 * @param visitor
	 *            The visitor
	 * @param lineChanges
	 *            ArrayList of Integers with the line changes.
	 * @param className
	 *            The method's class name in order to identify uniquely the
	 *            method.
	 * @return A boolean: false if the method's identifier is null or if it's a
	 *         new entry in result and true otherwise
	 */
	public boolean checkEntryInResultSet(GenericVisitor visitor, ArrayList<Integer> lineChanges, String className) {
		if (visitor.getIdentifier() == null) {
			return false;
		}
		if (result.get(className + ": " + visitor.getIdentifier()) != null) {
			result.get(className + ": " + visitor.getIdentifier()).add(visitor.getTotal());
			// logger.info(
			// "---> Total: " + (visitor.getTotal() > 0 ? "+" +
			// visitor.getTotal() : visitor.getTotal()) + "\n");
			return false;
		} else {
			lineChanges = new ArrayList<Integer>();
			lineChanges.add(visitor.getTotal());
			result.put(className + ": " + visitor.getIdentifier(), lineChanges);
			return true;
		}
		// logger.info("---> Total: " + (visitor.getTotal() > 0 ? "+" +
		// visitor.getTotal() : visitor.getTotal()) + "\n");
	}
	
	private void writeCsvFileData(ArrayList<CSVData> csvDataList) {
		for (CSVData csvLine : csvDataList) {
			try {
				ArrayList<Integer> changesList = result.get(csvLine.getClassName().replaceAll("\"", "") + ": "
						+ csvLine.getMethodName().replaceAll("\"", ""));
				Integer actualSize = 0;
				for (Integer change : changesList) {
					actualSize += change;
				}
				if (changesList != null) {
					csvLine.setInitialSize(changesList.get(0));
					csvLine.setNumberOfChanges(changesList.size());
					csvLine.setActualSize(actualSize);
					csvLine.setChangesList(changesList);
				}
				CSVUtils.writeLine(csvWriter, csvLine.getCSVLine(), ',', '"');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the CSV information for every method by parsing every
	 * history.json file of every .java file from .metanalysis folder.
	 */
	private void createResults() {
		ArrayList<CSVData> csvDataList = null;
		try {
			// String logFolderName = ".inspectory_results";
			Set<String> filesList = project.listFiles();
			csvDataList = new ArrayList<CSVData>();
			for (String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}
				// System.out.println("\n\nfile: " + file);
				List<HistoryEntry> fileHistory = project.getFileHistory(fileName);

				/*
				 * String logFilePath = "./" + logFolderName + "/" + fileName +
				 * ".history"; Logger logger = Logger.getRootLogger();
				 * 
				 * FileAppender appender = (FileAppender)
				 * logger.getAppender("file"); appender.setFile(logFilePath);
				 * appender.activateOptions();
				 */
				Logger logger = Logger.getRootLogger();

				for (HistoryEntry he : fileHistory) {
					try {
						Commit commit = new Commit();
						commit.setRevision(he.getRevision());
						commit.setAuthor(he.getAuthor());
						commit.setDate(he.getDate());
						// logger.info("----------------------------------------------------------\n");
						// logger.info(commit.toString());
						ArrayList<Integer> lineChanges = null;
						SourceFileTransaction sourceFileTransaction = he.getTransaction();
						List<NodeSetEdit> nodeEditList = sourceFileTransaction.getNodeEdits();
						GenericVisitor visitor = null;

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
								Transaction<?> t = ((NodeSetEdit.Change<?>) edit).getTransaction();
								List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
								for (NodeSetEdit memberEdit : memberEdits) {
									try {
										visitor = new EditVisitor(logger, fileName);
										((EditVisitor) visitor).visit(memberEdit);
										if (checkEntryInResultSet(visitor, lineChanges, className)) {
											CSVData csvData = new CSVData();
											csvData.setFileName("\"" + fileName + "\"");
											csvData.setClassName("\"" + className + "\"");
											csvData.setMethodName("\"" + visitor.getIdentifier() + "\"");
											csvDataList.add(csvData);
										}
									} catch (Exception e) {
										continue;
									}
								}
							} else if (edit instanceof NodeSetEdit.Add) {
								Node node = ((NodeSetEdit.Add) edit).getNode();
								if (node instanceof Node.Type) {
									String className = ((Node.Type) node).getName();
									visitor = new NodeVisitor(logger, fileName);
									Set<Node> members = ((Node.Type) node).getMembers();
									for (Node member : members) {
										try {
											if (member instanceof Node.Function) {
												((NodeVisitor) visitor).visit(member);
												if (checkEntryInResultSet(visitor, lineChanges, className)) {
													CSVData csvData = new CSVData();
													csvData.setFileName("\"" + fileName + "\"");
													csvData.setClassName("\"" + className + "\"");
													csvData.setMethodName("\"" + visitor.getIdentifier() + "\"");
													csvDataList.add(csvData);
												}
											}
										} catch (Exception e) {
											continue;
										}
									}
								}
							} else {
								deletedNodes.add(fileName);
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
		}
		writeCsvFileData(csvDataList);
	}

	/**
	 * Sorts the result map descending by the number of changes a method has in
	 * the commits history.
	 * 
	 * @return A List of ArrayLists of Integers: the result map of methods' line
	 *         changes transformed into a list
	 */
	public List<ArrayList<Integer>> sortResults() {
		long startTime = System.nanoTime();

		List<ArrayList<Integer>> changesValues = new ArrayList<>(result.values());
		Collections.sort(changesValues, new Comparator<ArrayList<Integer>>() {
			public int compare(ArrayList<Integer> s1, ArrayList<Integer> s2) {
				return Integer.compare(s2.size(), s1.size());
			}
		});

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		System.out.println("Duration of sort is: " + duration + "ms\n");
		return changesValues;
	}

	public void getHistoryFunctionsAnalyze() {
		createResults();
		// List<ArrayList<Integer>> changesValues = sortResults();

		/*
		 * for (ArrayList<Integer> changeValues : changesValues) {
		 * Iterator<Entry<String, ArrayList<Integer>>> iterator =
		 * result.entrySet().iterator(); while (iterator.hasNext()) {
		 * Entry<String, ArrayList<Integer>> entry = iterator.next(); if
		 * (entry.getValue().equals(changeValues)) {
		 * //System.out.println(entry.getKey() + "-" + changeValues + "; size: "
		 * + changeValues.size() + "\n"); iterator.remove(); } } }
		 */
		/*
		 * System.out.println("\n\nDeleted nodes are:"); for (String deletedNode
		 * : deletedNodes) { System.out.println(deletedNode); }
		 */
	}

	public Map<String, ArrayList<Integer>> getResult() {
		return result;
	}

	public void setResult(Map<String, ArrayList<Integer>> result) {
		this.result = result;
	}
}
