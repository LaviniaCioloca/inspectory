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
import java.util.Optional;
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
	private Optional<PersistentProject> project;
	private Map<String, MethodChangesInformation> allMethodsResult = new HashMap<String, MethodChangesInformation>();;
	private ArrayList<String> deletedNodes = new ArrayList<String>();
	private final FileWriter csvWriter;
	private final FileWriter csvMethodDynamicsWriter;
	private final FileWriter jsonWriter;
	private ArrayList<MethodChangesInformation> methodInformationList = new ArrayList<>();
	private final ArrayList<Commit> allCommits = new ArrayList<>();
	private final MethodDynamicsUtils methodDynamics = new MethodDynamicsUtils();

	/**
	 * AstronomicalMethodsInspection Constructor that initializes the result map
	 * and CSV writer to file.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public AstronomicalMethodsInspection(Optional<PersistentProject> project,
			FileWriter csvWriter, FileWriter csvMethodDynamicsWriter,
			FileWriter jsonWriter) {

		this.project = project;
		this.csvWriter = csvWriter;
		this.csvMethodDynamicsWriter = csvMethodDynamicsWriter;
		this.jsonWriter = jsonWriter;
	}

	/**
	 * Checking if the method exists in resultSet in order to add values to
	 * changes list or to create a new one.
	 * 
	 * @param visitor
	 *            The visitor
	 * @param className
	 *            The method's class name in order to identify uniquely the
	 *            method.
	 * @param commit
	 *            The current commit where the method had changes
	 * @return A boolean: false if the method's identifier is null or if it
	 *         already exists in result set and true otherwise.
	 */
	public boolean checkEntryInResultSet(GenericVisitor visitor,
			String className, Commit commit) {

		if (visitor.getIdentifier() == null) {
			return false;
		}

		if (allMethodsResult.get(visitor.getFileName() + ":" + className + ": "
				+ visitor.getIdentifier()) == null) {
			treatNewMethodInResultSet(visitor, className, commit);

			return true;
		} else {
			treatExistentMethodInResultSet(visitor, className, commit);

			return false;
		}
	}

	private void treatNewMethodInResultSet(GenericVisitor visitor,
			String className, Commit commit) {

		final ArrayList<Integer> changesList = new ArrayList<Integer>();
		changesList.add(visitor.getTotal());
		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);

		final MethodChangesInformation methodChangesInformation = addMehodChangesInformation(
				visitor, className, changesList, commits);

		if (visitor.getMethodDeleted()) {
			methodChangesInformation.setMethodDeleted(true);
		}

		allMethodsResult.put(visitor.getFileName() + ":" + className + ": "
				+ visitor.getIdentifier(), methodChangesInformation);
	}

	private MethodChangesInformation addMehodChangesInformation(
			GenericVisitor visitor, String className,
			final ArrayList<Integer> changesList,
			final ArrayList<Commit> commits) {

		final MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setCommits(commits);
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setClassName(className);
		methodChangesInformation.setMethodName(visitor.getIdentifier());

		return methodChangesInformation;
	}

	private void treatExistentMethodInResultSet(GenericVisitor visitor,
			String className, Commit commit) {

		final MethodChangesInformation methodChangesInformation = allMethodsResult
				.get(visitor.getFileName() + ":" + className + ": "
						+ visitor.getIdentifier());
		methodChangesInformation.getChangesList().add(visitor.getTotal());
		methodChangesInformation.getCommits().add(commit);

		if (visitor.getMethodDeleted()) {
			methodChangesInformation.setMethodDeleted(true);
		}
	}

	/**
	 * Adds to the list with all commits the ArrayList of commits for every
	 * method.
	 * 
	 * @param commits
	 */
	public void addToAllCommits(List<Commit> commits) {
		for (final Commit commit : commits) {
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
			final ArrayList<Commit> commits = allMethodsResult
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

		methodChangesInformation = setGeneralInformationValues(
				methodChangesInformation, wasDeleted, changesList, commits,
				actualSize);

		methodChangesInformation = setSupernovaMetricInformation(
				methodChangesInformation);

		methodChangesInformation = setPulsarMetricInformation(
				methodChangesInformation);

		checkIfSupernovaOrPulsar(methodChangesInformation);

		return methodChangesInformation;
	}

	private void checkIfSupernovaOrPulsar(
			MethodChangesInformation methodChangesInformation) {

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
	}

	private MethodChangesInformation setPulsarMetricInformation(
			MethodChangesInformation methodChangesInformation) {

		final PulsarMetric pulsarMetric = new PulsarMetric();
		methodChangesInformation
				.setPulsar(pulsarMetric.isPulsar(methodChangesInformation));
		methodChangesInformation.setPulsarSeverity(
				pulsarMetric.getPulsarSeverity(methodChangesInformation));

		methodChangesInformation = setPulsarCriteriaValues(
				methodChangesInformation, pulsarMetric);

		return methodChangesInformation;
	}

	private MethodChangesInformation setSupernovaMetricInformation(
			MethodChangesInformation methodChangesInformation) {

		final SupernovaMetric supernovaMetric = new SupernovaMetric();
		methodChangesInformation.setSupernova(
				supernovaMetric.isSupernova(methodChangesInformation));
		methodChangesInformation.setSupernovaSeverity(
				supernovaMetric.getSupernovaSeverity(methodChangesInformation));

		methodChangesInformation = setSupernovaCriteriaValues(
				methodChangesInformation, supernovaMetric);

		return methodChangesInformation;
	}

	private MethodChangesInformation setGeneralInformationValues(
			MethodChangesInformation methodChangesInformation,
			Boolean wasDeleted, ArrayList<Integer> changesList,
			ArrayList<Commit> commits, Integer actualSize) {

		methodChangesInformation.setInitialSize(changesList.get(0));
		methodChangesInformation.setNumberOfChanges(changesList.size());
		methodChangesInformation.setMethodDeleted(wasDeleted);
		methodChangesInformation.setActualSize(actualSize);
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setCommits(commits);

		return methodChangesInformation;
	}

	private MethodChangesInformation setSupernovaCriteriaValues(
			MethodChangesInformation methodChangesInformation,
			SupernovaMetric supernovaMetric) {

		final SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
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

		return methodChangesInformation;
	}

	private MethodChangesInformation setPulsarCriteriaValues(
			MethodChangesInformation methodChangesInformation,
			PulsarMetric pulsarMetric) {

		final PulsarCriteria pulsarCriteria = new PulsarCriteria();
		pulsarCriteria
				.setRecentCyclesPoints(pulsarMetric.getRecentCyclesPoints());
		pulsarCriteria.setAverageSizeIncreasePoints(
				pulsarMetric.getAverageSizeIncreasePoints());
		pulsarCriteria.setMethodSizePoints(pulsarMetric.getMethodSizePoints());
		pulsarCriteria
				.setActivityStatePoints(pulsarMetric.getActivityStatePoints());
		methodChangesInformation.setPulsarCriteria(pulsarCriteria);

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

		setMethodMetricsCommitsInformation();

		for (MethodChangesInformation methodChangesInformation : methodInformationList) {
			try {
				final ArrayList<Integer> changesList = getChangesListFromResult(
						methodChangesInformation);
				final ArrayList<Commit> commits = getCommitsFromResult(
						methodChangesInformation);
				final Integer actualSize = calculateMethodActualSize(
						changesList);
				final Boolean wasDeleted = checkIfMethodWasDeleted(
						methodChangesInformation);

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

	private Boolean checkIfMethodWasDeleted(
			MethodChangesInformation methodChangesInformation) {

		final Boolean wasDeleted = allMethodsResult
				.get(methodChangesInformation.getFileName() + ":"
						+ methodChangesInformation.getClassName() + ": "
						+ methodChangesInformation.getMethodName())
				.getMethodDeleted();

		return wasDeleted;
	}

	private ArrayList<Commit> getCommitsFromResult(
			MethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = allMethodsResult
				.get(methodChangesInformation.getFileName() + ":"
						+ methodChangesInformation.getClassName() + ": "
						+ methodChangesInformation.getMethodName())
				.getCommits();

		return commits;
	}

	private ArrayList<Integer> getChangesListFromResult(
			MethodChangesInformation methodChangesInformation) {

		final ArrayList<Integer> changesList = allMethodsResult
				.get(methodChangesInformation.getFileName() + ":"
						+ methodChangesInformation.getClassName() + ": "
						+ methodChangesInformation.getMethodName())
				.getChangesList();

		return changesList;
	}

	private Integer calculateMethodActualSize(
			final ArrayList<Integer> changesList) {

		Integer actualSize = 0;

		for (final Integer change : changesList) {
			actualSize += change;
		}

		return actualSize;
	}

	private void setMethodMetricsCommitsInformation() {
		final Commit latestCommit = allCommits.get(allCommits.size() - 1);
		MethodMetrics.setAllCommits(allCommits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		MethodMetrics.setNow(latestCommit.getDate());
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

		final String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		final Transaction<?> transaction = ((NodeSetEdit.Change<?>) edit)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) transaction)
				.getMemberEdits();

		treatEachNodeSetEdit(fileName, commit, className, memberEdits);
	}

	private void treatEachNodeSetEdit(String fileName, Commit commit,
			final String className, final List<NodeSetEdit> memberEdits) {

		GenericVisitor visitor;
		for (final NodeSetEdit memberEdit : memberEdits) {
			try {
				visitor = new EditVisitor(fileName);

				if (memberEdit instanceof NodeSetEdit.Remove) {
					treatNodeSetEditRemove(fileName, className, visitor,
							memberEdit);
				}

				((EditVisitor) visitor).visit(memberEdit);

				if (checkEntryInResultSet(visitor, className, commit)) {
					addDataInMethodInformationList(fileName, className,
							visitor.getIdentifier());
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void treatNodeSetEditRemove(String fileName, final String className,
			GenericVisitor visitor, final NodeSetEdit memberEdit) {

		Integer lastMethodSize = 0;
		final ArrayList<Integer> changesList = allMethodsResult
				.get(fileName + ":" + className + ": "
						+ ((NodeSetEdit.Remove) memberEdit).getIdentifier())
				.getChangesList();

		for (final Integer change : changesList) {
			lastMethodSize += change;
		}

		visitor.setLastMethodSize(lastMethodSize);
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

		final Node node = ((NodeSetEdit.Add) edit).getNode();

		if (node instanceof Node.Type) {
			treatGeneralNodeType(fileName, commit, node);
		}
	}

	private void treatGeneralNodeType(String fileName, Commit commit,
			final Node node) {

		final GenericVisitor visitor = new NodeVisitor(fileName);
		final Set<Node> members = ((Node.Type) node).getMembers();

		for (final Node member : members) {
			treatEachMemberNode(fileName, commit, node, visitor, member);
		}
	}

	private void treatEachMemberNode(String fileName, Commit commit,
			final Node node, final GenericVisitor visitor, final Node member) {

		try {
			if (member instanceof Node.Type) {
				treatNodeType(fileName, commit, visitor, member);
			} else if (member instanceof Node.Function) {
				treatNodeFunction(fileName, commit, node, visitor, member);
			}
		} catch (Exception e) {
			return;
		}
	}

	private void treatNodeFunction(String fileName, Commit commit,
			final Node node, final GenericVisitor visitor, final Node member) {

		final String className = ((Node.Type) node).getName();
		((NodeVisitor) visitor).visit(member);

		if (checkEntryInResultSet(visitor, className, commit)) {
			addDataInMethodInformationList(fileName, className,
					visitor.getIdentifier());
		}
	}

	private void treatNodeType(String fileName, Commit commit,
			final GenericVisitor visitor, final Node member) {

		final String className = ((Node.Type) member).getName();

		final Set<Node> typeMembers = ((Node.Type) member).getMembers();
		for (final Node typeMember : typeMembers) {
			treatTypeMember(fileName, commit, visitor, className, typeMember);
		}
	}

	private void treatTypeMember(String fileName, Commit commit,
			final GenericVisitor visitor, final String className,
			final Node typeMember) {

		if (typeMember instanceof Node.Function) {
			((NodeVisitor) visitor).visit(typeMember);

			if (checkEntryInResultSet(visitor, className, commit)) {
				addDataInMethodInformationList(fileName, className,
						visitor.getIdentifier());
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

		final MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setFileName(fileName);
		methodChangesInformation.setClassName(className);
		methodChangesInformation.setMethodName(methodName);

		methodInformationList.add(methodChangesInformation);
	}

	/**
	 * Creates the information for every method by parsing every history.json
	 * file of every .java file from .metanalysis folder.
	 */
	public void createMethodsResults() {
		try {
			final Set<String> filesList = project.get().listFiles();
			methodInformationList = new ArrayList<MethodChangesInformation>();
			for (final String fileName : filesList) {
				parseEachHistoryFile(fileName);
			}
		} catch (IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}

	private void parseEachHistoryFile(final String fileName)
			throws IOException {

		if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
			return;
		}

		final List<HistoryEntry> fileHistory = project.get()
				.getFileHistory(fileName);

		for (final HistoryEntry historyEntry : fileHistory) {
			treatEachHistoryEntry(fileName, historyEntry);
		}
	}

	private void treatEachHistoryEntry(final String fileName,
			final HistoryEntry historyEntry) {

		try {
			final Commit commit = setCommitInformation(historyEntry);
			final ArrayList<Integer> lineChanges = null;
			final SourceFileTransaction sourceFileTransaction = historyEntry
					.getTransaction();
			final List<NodeSetEdit> nodeEditList = sourceFileTransaction
					.getNodeEdits();
			final GenericVisitor visitor = null;

			for (final NodeSetEdit edit : nodeEditList) {
				treatEachNodeSetEdit(fileName, commit, lineChanges, visitor,
						edit);
			}
		} catch (Exception e) {
			return;
		}
	}

	private void treatEachNodeSetEdit(final String fileName,
			final Commit commit, final ArrayList<Integer> lineChanges,
			final GenericVisitor visitor, final NodeSetEdit edit) {

		if (edit instanceof NodeSetEdit.Change<?>) {
			handleNodeSetEditChange(edit, visitor, fileName, commit,
					lineChanges);
		} else if (edit instanceof NodeSetEdit.Add) {
			handleNodeSetEditAdd(edit, visitor, fileName, commit, lineChanges);
		} else {
			deletedNodes.add(fileName);
		}
	}

	private Commit setCommitInformation(final HistoryEntry historyEntry) {
		final Commit commit = new Commit();
		commit.setRevision(historyEntry.getRevision());
		commit.setAuthor(historyEntry.getAuthor());
		commit.setDate(historyEntry.getDate());

		return commit;
	}

	/**
	 * Creates for every method an empty entry for the fileName in the map with
	 * method properties values.
	 */
	public void createDefaultMethodInformation() {
		final Set<String> filesList = project.get().listFiles();

		for (final String fileName : filesList) {
			if (fileName.startsWith(".") || !fileName.endsWith(".java")) {
				continue;
			}

			methodDynamics.addDefaultMethodDynamics(fileName);
		}
	}

	/**
	 * Writes data from method dynamics analysis into a JSON and a CSV file.
	 */
	public void writeJSONMethodDynamicsData() {
		final JSONUtils jsonUtils = new JSONUtils();
		try {
			final JsonArray jsonArray = new JsonArray();
			for (final HashMap.Entry<String, FileMethodDynamics> entry : methodDynamics
					.getProjectMethodDynamics().entrySet()) {
				parseMethodDynamicsSet(jsonUtils, jsonArray, entry);
			}

			// entireJson.add("repository result", jsonArray);
			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			jsonWriter.write(gson.toJson(jsonArray));
			// jsonWriter.write(gson.toJson(entireJson));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseMethodDynamicsSet(final JSONUtils jsonUtils,
			final JsonArray jsonArray,
			final HashMap.Entry<String, FileMethodDynamics> entry)
			throws IOException {

		final Integer numberOfSupernovaMethods = entry.getValue()
				.getNumberOfSupernovaMethods();
		final Integer numberOfPulsarMethods = entry.getValue()
				.getNumberOfPulsarMethods();

		setSupernovaDynamicsValues(jsonUtils, jsonArray, entry,
				numberOfSupernovaMethods);

		setPulsarDynamicsValues(jsonUtils, jsonArray, entry,
				numberOfPulsarMethods);

		CSVUtils.writeLine(csvMethodDynamicsWriter, Arrays.asList(
				entry.getKey(),
				entry.getValue().getNumberOfSupernovaMethods().toString(),
				entry.getValue().getNumberOfPulsarMethods().toString(),
				entry.getValue().getSupernovaMethodsSeverityPoints().toString(),
				entry.getValue().getPulsarMethodsSeverityPoints().toString()));
	}

	private void setPulsarDynamicsValues(final JSONUtils jsonUtils,
			final JsonArray jsonArray,
			final HashMap.Entry<String, FileMethodDynamics> entry,
			final Integer numberOfPulsarMethods) {

		if (numberOfPulsarMethods > 0) {
			final Integer severityOfPulsarMethods = entry.getValue()
					.getPulsarMethodsSeverityPoints();

			jsonArray.add(jsonUtils.getAstronomicalPropertyJSON(entry.getKey(),
					numberOfPulsarMethods, "Pulsar Methods"));
			jsonArray.add(jsonUtils.getAstronomicalPropertyJSON(entry.getKey(),
					severityOfPulsarMethods, "Pulsar Severity"));
		}
	}

	private void setSupernovaDynamicsValues(final JSONUtils jsonUtils,
			final JsonArray jsonArray,
			final HashMap.Entry<String, FileMethodDynamics> entry,
			final Integer numberOfSupernovaMethods) {

		if (numberOfSupernovaMethods > 0) {
			final Integer severityOfSupernovaMethods = entry.getValue()
					.getSupernovaMethodsSeverityPoints();

			jsonArray.add(jsonUtils.getAstronomicalPropertyJSON(entry.getKey(),
					numberOfSupernovaMethods, "Supernova Methods"));
			jsonArray.add(jsonUtils.getAstronomicalPropertyJSON(entry.getKey(),
					severityOfSupernovaMethods, "Supernova Severity"));
		}
	}

	/**
	 * Method used to create the results and write the CSV and JSON files.
	 */
	public void analyzeAstronomicalMethods() {
		createMethodsResults();
		createDefaultMethodInformation();
		writeCSVFileData();
		writeJSONMethodDynamicsData();
	}

	// Getters and setters
	public Map<String, MethodChangesInformation> getResult() {
		return allMethodsResult;
	}

	public void setResult(Map<String, MethodChangesInformation> result) {
		this.allMethodsResult = result;
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

	public Optional<PersistentProject> getProject() {
		return project;
	}

	public void setProject(Optional<PersistentProject> project) {
		this.project = project;
	}

	public ArrayList<String> getDeletedNodes() {
		return deletedNodes;
	}

	public void setDeletedNodes(ArrayList<String> deletedNodes) {
		this.deletedNodes = deletedNodes;
	}

}
