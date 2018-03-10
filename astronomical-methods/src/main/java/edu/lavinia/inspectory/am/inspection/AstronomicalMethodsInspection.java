/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
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
package edu.lavinia.inspectory.am.inspection;

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

import edu.lavinia.inspectory.am.beans.FileMethodDynamics;
import edu.lavinia.inspectory.am.beans.MethodChangesInformation;
import edu.lavinia.inspectory.am.beans.PulsarCriteria;
import edu.lavinia.inspectory.am.beans.SupernovaCriteria;
import edu.lavinia.inspectory.am.metrics.MethodMetrics;
import edu.lavinia.inspectory.am.metrics.PulsarMetric;
import edu.lavinia.inspectory.am.metrics.SupernovaMetric;
import edu.lavinia.inspectory.am.utils.MethodDynamicsUtils;
import edu.lavinia.inspectory.am.visitor.EditVisitor;
import edu.lavinia.inspectory.am.visitor.NodeVisitor;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.utils.JSONUtils;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class AstronomicalMethodsInspection {
	private PersistentProject project;
	private Map<String, MethodChangesInformation> result;
	private ArrayList<String> deletedNodes = null;
	private final FileWriter csvWriter;
	private final FileWriter csvMethodDynamicsWriter;
	private final FileWriter jsonWriter;
	private ArrayList<MethodChangesInformation> methodInformationList;
	private final ArrayList<Commit> allCommits;
	private final MethodDynamicsUtils methodDynamics;

	/**
	 * AstronomicalMethodsInspection Constructor that initializes the result map
	 * and CSV writer to file.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public AstronomicalMethodsInspection(PersistentProject project,
			FileWriter csvWriter, FileWriter csvMethodDynamicsWriter,
			FileWriter jsonWriter) {
		this.project = project;
		result = new HashMap<String, MethodChangesInformation>();
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
	public boolean checkEntryInResultSet(GenericVisitor visitor,
			ArrayList<Integer> lineChanges, String className, Commit commit) {
		if (visitor.getIdentifier() == null) {
			return false;
		}

		MethodChangesInformation methodChangesInformation = null;
		if (result.get(visitor.getFileName() + ":" + className + ": "
				+ visitor.getIdentifier()) != null) {
			methodChangesInformation = result.get(visitor.getFileName() + ":"
					+ className + ": " + visitor.getIdentifier());
			methodChangesInformation.getChangesList().add(visitor.getTotal());
			methodChangesInformation.getCommits().add(commit);

			if (visitor.getMethodDeleted()) {
				methodChangesInformation.setMethodDeleted(true);
			}

			return false;
		} else {
			ArrayList<Integer> changesList = new ArrayList<Integer>();
			changesList.add(visitor.getTotal());
			ArrayList<Commit> commits = new ArrayList<>();
			commits.add(commit);
			methodChangesInformation = new MethodChangesInformation();
			methodChangesInformation.setCommits(commits);
			methodChangesInformation.setChangesList(changesList);
			methodChangesInformation.setClassName(className);
			methodChangesInformation.setMethodName(visitor.getIdentifier());

			if (visitor.getMethodDeleted()) {
				methodChangesInformation.setMethodDeleted(true);
			}

			result.put(
					visitor.getFileName() + ":" + className + ": "
							+ visitor.getIdentifier(),
					methodChangesInformation);
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
	public void createAndSortAllCommits() {
		for (final MethodChangesInformation methodChangesInformation : methodInformationList) {
			ArrayList<Commit> commits = result
					.get(methodChangesInformation.getFileName() + ":"
							+ methodChangesInformation.getClassName()
									.replaceAll("\"", "")
							+ ": " + methodChangesInformation.getMethodName()
									.replaceAll("\"", ""))
					.getCommits();
			addToAllCommits(commits);
		}
		sortAllCommits();
	}

	/**
	 * @param methodChangesInformation
	 * @param changesList
	 * @param commits
	 * @param actualSize
	 * @return
	 */
	public MethodChangesInformation setMethodInformation(
			MethodChangesInformation methodChangesInformation,
			Boolean wasDeleted, ArrayList<Integer> changesList,
			ArrayList<Commit> commits, Integer actualSize) {
		methodChangesInformation.setInitialSize(changesList.get(0));
		methodChangesInformation.setNumberOfChanges(changesList.size());
		methodChangesInformation.setMethodDeleted(wasDeleted);
		methodChangesInformation.setActualSize(actualSize);
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setCommits(commits);

		SupernovaMetric supernovaMetric = new SupernovaMetric();
		methodChangesInformation.setSupernova(
				supernovaMetric.isSupernova(methodChangesInformation));
		methodChangesInformation.setSupernovaSeverity(
				supernovaMetric.getSupernovaSeverity(methodChangesInformation));
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		supernovaCriteria
				.setLeapsSizePoints(supernovaMetric.getLeapsSizePoints());
		supernovaCriteria.setRecentLeapsSizePoints(
				supernovaMetric.getRecentLeapsSizePoints());
		supernovaCriteria.setSubsequentRefactoringPoints(
				supernovaMetric.getSubsequentRefactoringPoints());
		supernovaCriteria
				.setMethodSizePoints(supernovaMetric.getMethodSizePoints());
		supernovaCriteria.setActivityStatePoints(
				supernovaMetric.getActivityStatePoints());
		methodChangesInformation.setSupernovaCriteria(supernovaCriteria);

		PulsarMetric pulsarMetric = new PulsarMetric();
		methodChangesInformation
				.setPulsar(pulsarMetric.isPulsar(methodChangesInformation));
		methodChangesInformation.setPulsarSeverity(
				pulsarMetric.getPulsarSeverity(methodChangesInformation));
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		pulsarCriteria
				.setRecentCyclesPoints(pulsarMetric.getRecentCyclesPoints());
		pulsarCriteria.setAverageSizeIncreasePoints(
				pulsarMetric.getAverageSizeIncreasePoints());
		pulsarCriteria.setMethodSizePoints(pulsarMetric.getMethodSizePoints());
		pulsarCriteria
				.setActivityStatePoints(pulsarMetric.getActivityStatePoints());
		methodChangesInformation.setPulsarCriteria(pulsarCriteria);
		if (methodChangesInformation.isSupernova()) {
			methodDynamics.addSupernovaMethodDynamics(
					methodChangesInformation.getFileName(),
					methodChangesInformation.getSupernovaSeverity());
		}

		if (methodChangesInformation.isPulsar()) {
			methodDynamics.addPulsarMethodDynamics(
					methodChangesInformation.getFileName(),
					methodChangesInformation.getPulsarSeverity());
		}

		return methodChangesInformation;
	}

	/**
	 * Writes the CSV lines in the inspectory result CSV file.
	 * 
	 * @param methodInformationList
	 *            List with every {@code methodInformation} line, of every
	 *            method, to be written in the inspectory result CSV file.
	 */
	public void writeCSVFileData() {
		createAndSortAllCommits();
		Commit latestCommit = allCommits.get(allCommits.size() - 1);
		MethodMetrics.setAllCommits(allCommits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		MethodMetrics.setNow(latestCommit.getDate());
		for (MethodChangesInformation methodChangesInformation : methodInformationList) {
			try {
				ArrayList<Integer> changesList = result
						.get(methodChangesInformation.getFileName() + ":"
								+ methodChangesInformation.getClassName()
										.replaceAll("\"", "")
								+ ": " + methodChangesInformation
										.getMethodName().replaceAll("\"", ""))
						.getChangesList();
				ArrayList<Commit> commits = result
						.get(methodChangesInformation.getFileName() + ":"
								+ methodChangesInformation.getClassName()
										.replaceAll("\"", "")
								+ ": " + methodChangesInformation
										.getMethodName().replaceAll("\"", ""))
						.getCommits();
				Integer actualSize = 0;
				for (Integer change : changesList) {
					actualSize += change;
				}

				final Boolean wasDeleted = result
						.get(methodChangesInformation.getFileName() + ":"
								+ methodChangesInformation.getClassName()
										.replaceAll("\"", "")
								+ ": "
								+ methodChangesInformation.getMethodName()
										.replaceAll("\"", ""))
						.getMethodDeleted();

				methodChangesInformation = setMethodInformation(
						methodChangesInformation, wasDeleted, changesList,
						commits, actualSize);
				CSVUtils.writeLine(csvWriter,
						methodChangesInformation.getMethodInformationLine(),
						',', '"');
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
	public void handleNodeSetEditChange(NodeSetEdit edit,
			GenericVisitor visitor, String fileName, Commit commit,
			ArrayList<Integer> lineChanges) {
		String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		Transaction<?> t = ((NodeSetEdit.Change<?>) edit).getTransaction();
		List<NodeSetEdit> memberEdits = ((TypeTransaction) t).getMemberEdits();
		for (NodeSetEdit memberEdit : memberEdits) {
			try {
				visitor = new EditVisitor(fileName);

				if (memberEdit instanceof NodeSetEdit.Remove) {
					Integer lastMethodSize = 0;
					ArrayList<Integer> changesList = result.get(fileName + ":"
							+ className.replaceAll("\"", "")
							+ ": " + ((NodeSetEdit.Remove) memberEdit)
									.getIdentifier().replaceAll("\"", ""))
							.getChangesList();

					for (Integer change : changesList) {
						lastMethodSize += change;
					}
					visitor.setLastMethodSize(lastMethodSize);
				}

				((EditVisitor) visitor).visit(memberEdit);
				if (checkEntryInResultSet(visitor, lineChanges, className,
						commit)) {
					addDataInMethodInformationList(fileName, className,
							visitor.getIdentifier());
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
	public void handleNodeSetEditAdd(NodeSetEdit edit, GenericVisitor visitor,
			String fileName, Commit commit, ArrayList<Integer> lineChanges) {
		Node node = ((NodeSetEdit.Add) edit).getNode();
		if (node instanceof Node.Type) {
			visitor = new NodeVisitor(fileName);
			String className = "";
			final Set<Node> members = ((Node.Type) node).getMembers();

			for (final Node member : members) {
				try {
					if (member instanceof Node.Type) {
						className = ((Node.Type) member).getName();

						final Set<Node> typeMembers = ((Node.Type) member)
								.getMembers();
						for (final Node typeMember : typeMembers) {
							if (typeMember instanceof Node.Function) {
								((NodeVisitor) visitor).visit(typeMember);
								if (checkEntryInResultSet(visitor, lineChanges,
										className, commit)) {
									addDataInMethodInformationList(fileName,
											className, visitor.getIdentifier());
								}
							}
						}
					} else if (member instanceof Node.Function) {
						className = ((Node.Type) node).getName();
						((NodeVisitor) visitor).visit(member);
						if (checkEntryInResultSet(visitor, lineChanges,
								className, commit)) {
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
	public void addDataInMethodInformationList(String fileName,
			String className, String methodName) {
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		/*
		 * methodChangesInformation.setFileName("\"" + fileName + "\"");
		 * methodChangesInformation.setClassName("\"" + className + "\"");
		 * methodChangesInformation.setMethodName("\"" + methodName + "\"");
		 */

		methodChangesInformation.setFileName(fileName);
		methodChangesInformation.setClassName(className);
		methodChangesInformation.setMethodName(methodName);
		methodInformationList.add(methodChangesInformation);
	}

	/**
	 * Creates the information for every method by parsing every history.json
	 * file of every .java file from .metanalysis folder.
	 */
	public void createResults() {
		try {
			Set<String> filesList = project.listFiles();
			methodInformationList = new ArrayList<MethodChangesInformation>();
			for (String fileName : filesList) {
				if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
					continue;
				}

				List<HistoryEntry> fileHistory = project
						.getFileHistory(fileName);

				for (HistoryEntry historyEntry : fileHistory) {
					try {
						final Commit commit = new Commit();
						commit.setRevision(historyEntry.getRevision());
						commit.setAuthor(historyEntry.getAuthor());
						commit.setDate(historyEntry.getDate());
						final ArrayList<Integer> lineChanges = null;
						final SourceFileTransaction sourceFileTransaction = historyEntry
								.getTransaction();
						final List<NodeSetEdit> nodeEditList = sourceFileTransaction
								.getNodeEdits();
						GenericVisitor visitor = null;

						for (final NodeSetEdit edit : nodeEditList) {
							if (edit instanceof NodeSetEdit.Change<?>) {
								handleNodeSetEditChange(edit, visitor, fileName,
										commit, lineChanges);
							} else if (edit instanceof NodeSetEdit.Add) {
								handleNodeSetEditAdd(edit, visitor, fileName,
										commit, lineChanges);
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
			// JsonObject entireJson = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			for (HashMap.Entry<String, FileMethodDynamics> entry : methodDynamics
					.getProjectMethodDynamics().entrySet()) {
				if (entry.getValue().getSupernovaMethods().intValue() > 0) {
					jsonArray.add(
							jsonUtils.getSupernovaMethodsJSON(entry.getKey(),
									entry.getValue().getSupernovaMethods()));
					jsonArray.add(
							jsonUtils.getSupernovaSeverityJSON(entry.getKey(),
									entry.getValue().getSupernovaSeverity()));
				}
				if (entry.getValue().getPulsarMethods().intValue() > 0) {
					jsonArray.add(jsonUtils.getPulsarMethodsJSON(entry.getKey(),
							entry.getValue().getPulsarMethods()));
					jsonArray
							.add(jsonUtils.getPulsarSeverityJSON(entry.getKey(),
									entry.getValue().getPulsarMethods()));
				}
				CSVUtils.writeLine(csvMethodDynamicsWriter, Arrays.asList(
						entry.getKey(),
						entry.getValue().getSupernovaMethods().toString(),
						entry.getValue().getPulsarMethods().toString(),
						entry.getValue().getSupernovaSeverity().toString(),
						entry.getValue().getPulsarSeverity().toString()));
			}
			// entireJson.add("repository result", jsonArray);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			jsonWriter.write(gson.toJson(jsonArray));
			// jsonWriter.write(gson.toJson(entireJson));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Getters and setters
	public void getHistoryFunctionsAnalyze() {
		createResults();
		createDefaultMethodInformation();
		writeCSVFileData();
		writeMethodDynamicsData();
	}

	public Map<String, MethodChangesInformation> getResult() {
		return result;
	}

	public void setResult(Map<String, MethodChangesInformation> result) {
		this.result = result;
	}

	public ArrayList<Commit> getAllCommits() {
		return allCommits;
	}

	public ArrayList<MethodChangesInformation> getMethodInformationList() {
		return methodInformationList;
	}

	public void setMethodInformationList(
			ArrayList<MethodChangesInformation> methodInformationList) {
		this.methodInformationList = methodInformationList;
	}

	public PersistentProject getProject() {
		return project;
	}

	public void setProject(PersistentProject project) {
		this.project = project;
	}

	public ArrayList<String> getDeletedNodes() {
		return deletedNodes;
	}

	public void setDeletedNodes(ArrayList<String> deletedNodes) {
		this.deletedNodes = deletedNodes;
	}

}
