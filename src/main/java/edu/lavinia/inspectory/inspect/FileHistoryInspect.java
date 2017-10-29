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
package edu.lavinia.inspectory.inspect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metanalysis.core.delta.NodeSetEdit;
import org.metanalysis.core.delta.SourceFileTransaction;
import org.metanalysis.core.delta.Transaction;
import org.metanalysis.core.delta.TypeTransaction;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.project.PersistentProject;
import org.metanalysis.core.project.Project.HistoryEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.beans.FileMethodDynamics;
import edu.lavinia.inspectory.beans.MethodInformation;
import edu.lavinia.inspectory.beans.PulsarCriteria;
import edu.lavinia.inspectory.beans.SupernovaCriteria;
import edu.lavinia.inspectory.metrics.MethodMetrics;
import edu.lavinia.inspectory.metrics.PulsarMetric;
import edu.lavinia.inspectory.metrics.SupernovaMetric;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.utils.JSONUtils;
import edu.lavinia.inspectory.utils.MethodDynamicsUtils;
import edu.lavinia.inspectory.visitor.EditVisitor;
import edu.lavinia.inspectory.visitor.GenericVisitor;
import edu.lavinia.inspectory.visitor.NodeVisitor;

public class FileHistoryInspect {
	private static PersistentProject project = null;
	private Map<String, MethodInformation> result = null;
	private ArrayList<String> deletedNodes = null;
	private FileWriter csvWriter = null;
	private FileWriter csvMethodDynamicsWriter = null;
	private FileWriter jsonWriter = null;
	private ArrayList<MethodInformation> methodInformationList = null;
	private ArrayList<Commit> allCommits = null;
	private MethodDynamicsUtils methodDynamics = null;

	/**
	 * FileHistoryInspect Constructor that initializes the result map and CSV
	 * writer to file.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public FileHistoryInspect(PersistentProject project, FileWriter csvWriter,
			FileWriter csvMethodDynamicsWriter, FileWriter jsonWriter) {
		FileHistoryInspect.project = project;
		result = new HashMap<String, MethodInformation>();
		deletedNodes = new ArrayList<String>();
		this.csvWriter = csvWriter;
		this.csvMethodDynamicsWriter = csvMethodDynamicsWriter;
		this.jsonWriter = jsonWriter;
		methodInformationList = new ArrayList<>();
		allCommits = new ArrayList<>();
		methodDynamics = new MethodDynamicsUtils();
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
	 * @param commit
	 *            The current commit where the method had changes
	 * @return A boolean: false if the method's identifier is null or if it
	 *         already exists in result set and true otherwise.
	 */
	public boolean checkEntryInResultSet(GenericVisitor visitor, ArrayList<Integer> lineChanges,
			String className, Commit commit) {
		if (visitor.getIdentifier() == null) {
			return false;
		}
		if (result.get(className + ": " + visitor.getIdentifier()) != null) {
			MethodInformation methodInformation = result
					.get(className + ": " + visitor.getIdentifier());
			methodInformation.getChangesList().add(visitor.getTotal());
			methodInformation.getCommits().add(commit);
			return false;
		} else {
			lineChanges = new ArrayList<Integer>();
			lineChanges.add(visitor.getTotal());
			ArrayList<Commit> commits = new ArrayList<>();
			commits.add(commit);
			MethodInformation methodInformation = new MethodInformation();
			methodInformation.setChangesList(lineChanges);
			methodInformation.setCommits(commits);
			methodInformation.setClassName(className);
			methodInformation.setMethodName(visitor.getIdentifier());
			result.put(className + ": " + visitor.getIdentifier(), methodInformation);
			return true;
		}
	}

	/**
	 * Adds to the list with all commits the ArrayList of commits for every
	 * method.
	 * 
	 * @param commits
	 */
	public void addToAllCommits(ArrayList<Commit> commits) {
		for (Commit commit : commits) {
			allCommits.add(commit);
		}
	}

	/**
	 * Sorts the ArrayList containing all the commits in the repository in
	 * chronological order.
	 */
	public void sortAllCommits() {
		Collections.sort(allCommits, new Comparator<Commit>() {
			@Override
			public int compare(Commit commit1, Commit commit2) {
				return commit1.getDate().compareTo(commit2.getDate());
			}
		});
	}

	/**
	 * Takes all the commits from the methodInformationList that will be written
	 * in the CSV file and add them to the allCommits ArrayList. After that,
	 * sorts the ArrayList chronological.
	 * 
	 * @param methodInformationList
	 */
	public void createAndSortAllCommits(ArrayList<MethodInformation> methodInformationList) {
		for (MethodInformation methodInformation : methodInformationList) {
			ArrayList<Commit> commits = result
					.get(methodInformation.getClassName().replaceAll("\"", "") + ": "
							+ methodInformation.getMethodName().replaceAll("\"", ""))
					.getCommits();
			addToAllCommits(commits);
		}
		sortAllCommits();
	}

	/**
	 * @param methodInformation
	 * @param changesList
	 * @param commits
	 * @param actualSize
	 * @return
	 */
	public MethodInformation setMethodInformation(MethodInformation methodInformation,
			ArrayList<Integer> changesList, ArrayList<Commit> commits, Integer actualSize) {
		methodInformation.setInitialSize(changesList.get(0));
		methodInformation.setNumberOfChanges(changesList.size());
		methodInformation.setActualSize(actualSize);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);

		SupernovaMetric supernovaMetric = new SupernovaMetric();
		methodInformation.setSupernova(supernovaMetric.isSupernova(methodInformation));
		methodInformation
				.setSupernovaSeverity(supernovaMetric.getSupernovaSeverity(methodInformation));
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		supernovaCriteria.setLeapsSizePoints(supernovaMetric.getLeapsSizePoints());
		supernovaCriteria.setRecentLeapsSizePoints(supernovaMetric.getRecentLeapsSizePoints());
		supernovaCriteria
				.setSubsequentRefactoringPoints(supernovaMetric.getSubsequentRefactoringPoints());
		supernovaCriteria.setMethodSizePoints(supernovaMetric.getMethodSizePoints());
		supernovaCriteria.setActivityStatePoints(supernovaMetric.getActivityStatePoints());
		methodInformation.setSupernovaCriteria(supernovaCriteria);

		PulsarMetric pulsarMetric = new PulsarMetric();
		methodInformation.setPulsar(pulsarMetric.isPulsar(methodInformation));
		methodInformation.setPulsarSeverity(pulsarMetric.getPulsarSeverity(methodInformation));
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		pulsarCriteria.setRecentCyclesPoints(pulsarMetric.getRecentCyclesPoints());
		pulsarCriteria.setAverageSizeIncreasePoints(pulsarMetric.getAverageSizeIncreasePoints());
		pulsarCriteria.setMethodSizePoints(pulsarMetric.getMethodSizePoints());
		pulsarCriteria.setActivityStatePoints(pulsarMetric.getActivityStatePoints());
		methodInformation.setPulsarCriteria(pulsarCriteria);
		if (methodInformation.isSupernova()) {
			methodDynamics.addSupernovaMethodDynamics(methodInformation.getFileName(),
					methodInformation.getSupernovaSeverity());
		}
		if (methodInformation.isPulsar()) {
			methodDynamics.addPulsarMethodDynamics(methodInformation.getFileName(),
					methodInformation.getPulsarSeverity());
		}
		return methodInformation;
	}

	/**
	 * Writes the CSV lines in the inspectory result CSV file.
	 * 
	 * @param methodInformationList
	 *            List with every {@code methodInformation} line, of every
	 *            method, to be written in the inspectory result CSV file.
	 */
	public void writeCSVFileData(ArrayList<MethodInformation> methodInformationList) {
		createAndSortAllCommits(methodInformationList);
		Commit latestCommit = allCommits.get(allCommits.size() - 1);
		MethodMetrics.setAllCommits(allCommits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		MethodMetrics.setNow(latestCommit.getDate());
		for (MethodInformation methodInformation : methodInformationList) {
			try {
				ArrayList<Integer> changesList = result
						.get(methodInformation.getClassName().replaceAll("\"", "") + ": "
								+ methodInformation.getMethodName().replaceAll("\"", ""))
						.getChangesList();
				ArrayList<Commit> commits = result
						.get(methodInformation.getClassName().replaceAll("\"", "") + ": "
								+ methodInformation.getMethodName().replaceAll("\"", ""))
						.getCommits();
				Integer actualSize = 0;
				for (Integer change : changesList) {
					actualSize += change;
				}
				methodInformation = setMethodInformation(methodInformation, changesList, commits,
						actualSize);
				CSVUtils.writeLine(csvWriter, methodInformation.getMethodInformationLine(), ',',
						'"');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks for every Change MemberEdit of the current NodeSetEdit if there is
	 * in the result set in order to add it to the CSV line.
	 * 
	 * @param edit
	 * @param visitor
	 * @param fileName
	 * @param commit
	 * @param lineChanges
	 */
	public void handleNodeSetEditChange(NodeSetEdit edit, GenericVisitor visitor, String fileName,
			Commit commit, ArrayList<Integer> lineChanges) {
		String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		Transaction<?> t = ((NodeSetEdit.Change<?>) edit).getTransaction();
		List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
		for (NodeSetEdit memberEdit : memberEdits) {
			try {
				visitor = new EditVisitor(fileName);
				((EditVisitor) visitor).visit(memberEdit);
				if (checkEntryInResultSet(visitor, lineChanges, className, commit)) {
					addDataInMethodInformationList(fileName, className, visitor.getIdentifier());
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * Checks for every Add NodeSetEdit if there is in the result set in order
	 * to add it to the CSV line.
	 * 
	 * @param edit
	 * @param visitor
	 * @param fileName
	 * @param commit
	 * @param lineChanges
	 */
	public void handleNodeSetEditAdd(NodeSetEdit edit, GenericVisitor visitor, String fileName,
			Commit commit, ArrayList<Integer> lineChanges) {
		Node node = ((NodeSetEdit.Add) edit).getNode();
		if (node instanceof Node.Type) {
			String className = ((Node.Type) node).getName();
			visitor = new NodeVisitor(fileName);
			Set<Node> members = ((Node.Type) node).getMembers();
			for (Node member : members) {
				try {
					if (member instanceof Node.Function) {
						((NodeVisitor) visitor).visit(member);
						if (checkEntryInResultSet(visitor, lineChanges, className, commit)) {
							addDataInMethodInformationList(fileName, className,
									visitor.getIdentifier());
						}
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

	/**
	 * Adds in methodInformationList values for a method: file name, class name
	 * and method's name.
	 * 
	 * @param fileName
	 * @param className
	 * @param methodName
	 */
	public void addDataInMethodInformationList(String fileName, String className,
			String methodName) {
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setFileName("\"" + fileName + "\"");
		methodInformation.setClassName("\"" + className + "\"");
		methodInformation.setMethodName("\"" + methodName + "\"");
		methodInformationList.add(methodInformation);
	}

	/**
	 * Creates the information for every method by parsing every history.json
	 * file of every .java file from .metanalysis folder.
	 */
	public void createResults() {
		try {
			Set<String> filesList = project.listFiles();
			methodInformationList = new ArrayList<MethodInformation>();
			for (String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}
				List<HistoryEntry> fileHistory = project.getFileHistory(fileName);

				for (HistoryEntry he : fileHistory) {
					try {
						Commit commit = new Commit();
						commit.setRevision(he.getRevision());
						commit.setAuthor(he.getAuthor());
						commit.setDate(he.getDate());
						ArrayList<Integer> lineChanges = null;
						SourceFileTransaction sourceFileTransaction = he.getTransaction();
						List<NodeSetEdit> nodeEditList = sourceFileTransaction.getNodeEdits();
						GenericVisitor visitor = null;

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								handleNodeSetEditChange(edit, visitor, fileName, commit,
										lineChanges);
							} else if (edit instanceof NodeSetEdit.Add) {
								handleNodeSetEditAdd(edit, visitor, fileName, commit, lineChanges);
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
	}

	/**
	 * Creates for every method an empty entry for the fileName in the map with
	 * method properties values.
	 */
	public void createDefaultMethodInformation() {
		Set<String> filesList = project.listFiles();
		for (String fileName : filesList) {
			if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
				continue;
			}
			methodDynamics.addDefaultMethodDynamics(fileName);
		}
	}

	/**
	 * Writes data from method dynamics analysis into a JSON and a CSV file.
	 */
	public void writeMethodDynamicsData() {
		JSONUtils jsonUtils = new JSONUtils();
		try {
			JsonObject entireJson = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			for (HashMap.Entry<String, FileMethodDynamics> entry : methodDynamics
					.getProjectMethodDynamics().entrySet()) {
				jsonArray.add(jsonUtils.getSupernovaMethodsJSON(entry.getKey(),
						entry.getValue().getSupernovaMethods()));
				jsonArray.add(jsonUtils.getPulsarMethodsJSON(entry.getKey(),
						entry.getValue().getPulsarMethods()));
				jsonArray.add(jsonUtils.getSupernovaSeverityJSON(entry.getKey(),
						entry.getValue().getSupernovaSeverity()));
				jsonArray.add(jsonUtils.getPulsarSeverityJSON(entry.getKey(),
						entry.getValue().getPulsarMethods()));
				CSVUtils.writeLine(csvMethodDynamicsWriter,
						Arrays.asList(entry.getKey(),
								entry.getValue().getSupernovaMethods().toString(),
								entry.getValue().getPulsarMethods().toString(),
								entry.getValue().getSupernovaSeverity().toString(),
								entry.getValue().getPulsarSeverity().toString()));
			}
			entireJson.add("repository results", jsonArray);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			jsonWriter.write(gson.toJson(entireJson));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Getters and setters
	public void getHistoryFunctionsAnalyze() {
		createResults();
		createDefaultMethodInformation();
		writeCSVFileData(methodInformationList);
		writeMethodDynamicsData();
	}

	public Map<String, MethodInformation> getResult() {
		return result;
	}

	public void setResult(Map<String, MethodInformation> result) {
		this.result = result;
	}

	public ArrayList<Commit> getAllCommits() {
		return allCommits;
	}

	public ArrayList<MethodInformation> getMethodInformationList() {
		return methodInformationList;
	}

}
