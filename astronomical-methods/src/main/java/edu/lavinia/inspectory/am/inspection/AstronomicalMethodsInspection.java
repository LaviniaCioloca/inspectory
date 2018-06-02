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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
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

import edu.lavinia.inspectory.am.beans.AstronomicalMethodChangesInformation;
import edu.lavinia.inspectory.am.beans.FileMethodDynamics;
import edu.lavinia.inspectory.am.beans.FileWithAstronomicalMethods;
import edu.lavinia.inspectory.am.beans.PulsarCriteria;
import edu.lavinia.inspectory.am.beans.SupernovaCriteria;
import edu.lavinia.inspectory.am.metrics.PulsarMetric;
import edu.lavinia.inspectory.am.metrics.SupernovaMetric;
import edu.lavinia.inspectory.am.utils.MethodDynamicsUtils;
import edu.lavinia.inspectory.am.visitor.EditVisitor;
import edu.lavinia.inspectory.am.visitor.NodeVisitor;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;
import edu.lavinia.inspectory.utils.CSVUtils;
import edu.lavinia.inspectory.utils.JSONUtils;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class AstronomicalMethodsInspection {
	private Optional<PersistentProject> project;
	private Map<String, AstronomicalMethodChangesInformation> allMethodsResult = new HashMap<>();
	private ArrayList<String> deletedNodes = new ArrayList<>();
	private final FileWriter csvWriter;
	private final FileWriter csvMethodDynamicsWriter;
	private final FileWriter jsonWriter;
	private ArrayList<AstronomicalMethodChangesInformation> methodInformationList = new ArrayList<>();

	private final ArrayList<Commit> allCommits = new ArrayList<>();
	private LinkedHashMap<Commit, Integer> allCommitsIntoTimeFrames;
	private Integer maximumTimeFrameNumber;

	private static Integer numberOfJavaSourcesCount = 0;
	private MethodDynamicsUtils methodDynamics = new MethodDynamicsUtils();

	private final Map<String, FileWithAstronomicalMethods> filesWithSupernovaMethods = new HashMap<>();
	private final Map<String, FileWithAstronomicalMethods> filesWithPulsarMethods = new HashMap<>();

	/**
	 * AstronomicalMethodsInspection Constructor that initializes the result map
	 * and CSV writer to file.
	 *
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public AstronomicalMethodsInspection(
			final Optional<PersistentProject> project,
			final FileWriter csvWriter,
			final FileWriter csvMethodDynamicsWriter,
			final FileWriter jsonWriter) {

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
	public boolean checkEntryInResultSet(final GenericVisitor visitor,
			final String className, final Commit commit) {

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

	private void treatNewMethodInResultSet(final GenericVisitor visitor,
			final String className, final Commit commit) {

		final ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(visitor.getTotal());
		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);

		final AstronomicalMethodChangesInformation methodChangesInformation = addMehodChangesInformation(
				visitor, className, changesList, commits);

		if (visitor.getMethodDeleted()) {
			methodChangesInformation.setMethodDeleted(true);
		}

		allMethodsResult.put(visitor.getFileName() + ":" + className + ": "
				+ visitor.getIdentifier(), methodChangesInformation);
	}

	private AstronomicalMethodChangesInformation addMehodChangesInformation(
			final GenericVisitor visitor, final String className,
			final ArrayList<Integer> changesList,
			final ArrayList<Commit> commits) {

		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		methodChangesInformation.setCommits(commits);
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setClassName(className);
		methodChangesInformation.setMethodName(visitor.getIdentifier());

		return methodChangesInformation;
	}

	private void treatExistentMethodInResultSet(final GenericVisitor visitor,
			final String className, final Commit commit) {

		final AstronomicalMethodChangesInformation methodChangesInformation = allMethodsResult
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
	public void addToAllCommits(final List<Commit> commits) {
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
			public int compare(final Commit commit1, final Commit commit2) {
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
		for (final AstronomicalMethodChangesInformation methodChangesInformation : methodInformationList) {
			final ArrayList<Commit> commits = allMethodsResult
					.get(methodChangesInformation.getFileName() + ":"
							+ methodChangesInformation.getClassName() + ": "
							+ methodChangesInformation.getMethodName())
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
	public AstronomicalMethodChangesInformation setMethodInformation(
			AstronomicalMethodChangesInformation methodChangesInformation,
			final Boolean wasDeleted, final ArrayList<Integer> changesList,
			final ArrayList<Commit> commits, final Integer actualSize) {

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
			final AstronomicalMethodChangesInformation methodChangesInformation) {

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

	private AstronomicalMethodChangesInformation setPulsarMetricInformation(
			AstronomicalMethodChangesInformation methodChangesInformation) {

		final PulsarMetric pulsarMetric = new PulsarMetric();
		pulsarMetric.setAllCommits(allCommits);
		pulsarMetric.setAllCommitsIntoTimeFrames(allCommitsIntoTimeFrames);
		pulsarMetric.setMaximumTimeFrameNumber(maximumTimeFrameNumber);

		methodChangesInformation
				.setPulsar(pulsarMetric.isPulsar(methodChangesInformation));
		methodChangesInformation.setPulsarSeverity(
				pulsarMetric.getPulsarSeverity(methodChangesInformation));

		methodChangesInformation = setPulsarCriteriaValues(
				methodChangesInformation, pulsarMetric);

		return methodChangesInformation;
	}

	private AstronomicalMethodChangesInformation setSupernovaMetricInformation(
			AstronomicalMethodChangesInformation methodChangesInformation) {

		final SupernovaMetric supernovaMetric = new SupernovaMetric();
		supernovaMetric.setAllCommits(allCommits);
		supernovaMetric.setAllCommitsIntoTimeFrames(allCommitsIntoTimeFrames);
		supernovaMetric.setMaximumTimeFrameNumber(maximumTimeFrameNumber);

		methodChangesInformation.setSupernova(
				supernovaMetric.isSupernova(methodChangesInformation));
		methodChangesInformation.setSupernovaSeverity(
				supernovaMetric.getSupernovaSeverity(methodChangesInformation));

		methodChangesInformation = setSupernovaCriteriaValues(
				methodChangesInformation, supernovaMetric);

		return methodChangesInformation;
	}

	private AstronomicalMethodChangesInformation setGeneralInformationValues(
			final AstronomicalMethodChangesInformation methodChangesInformation,
			final Boolean wasDeleted, final ArrayList<Integer> changesList,
			final ArrayList<Commit> commits, final Integer actualSize) {

		methodChangesInformation.setInitialSize(changesList.get(0));
		methodChangesInformation.setNumberOfChanges(changesList.size());
		methodChangesInformation.setMethodDeleted(wasDeleted);
		methodChangesInformation.setActualSize(actualSize);
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setCommits(commits);

		return methodChangesInformation;
	}

	private AstronomicalMethodChangesInformation setSupernovaCriteriaValues(
			final AstronomicalMethodChangesInformation methodChangesInformation,
			final SupernovaMetric supernovaMetric) {

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

	private AstronomicalMethodChangesInformation setPulsarCriteriaValues(
			final AstronomicalMethodChangesInformation methodChangesInformation,
			final PulsarMetric pulsarMetric) {

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

	private void getAllCommitsIntoTimeFrames() {
		final Pair<Integer, LinkedHashMap<Commit, Integer>> maximumTimeFrameCommits = MethodThresholdsMeasure
				.splitCommitsIntoTimeFrames(allCommits);

		maximumTimeFrameNumber = maximumTimeFrameCommits.getLeft();
		allCommitsIntoTimeFrames = maximumTimeFrameCommits.getRight();
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

		getAllCommitsIntoTimeFrames();

		for (AstronomicalMethodChangesInformation methodChangesInformation : methodInformationList) {
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
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Boolean checkIfMethodWasDeleted(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		final Boolean wasDeleted = allMethodsResult
				.get(methodChangesInformation.getFileName() + ":"
						+ methodChangesInformation.getClassName() + ": "
						+ methodChangesInformation.getMethodName())
				.getMethodDeleted();

		return wasDeleted;
	}

	private ArrayList<Commit> getCommitsFromResult(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = allMethodsResult
				.get(methodChangesInformation.getFileName() + ":"
						+ methodChangesInformation.getClassName() + ": "
						+ methodChangesInformation.getMethodName())
				.getCommits();

		return commits;
	}

	private ArrayList<Integer> getChangesListFromResult(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

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
	public void handleNodeSetEditChange(final NodeSetEdit edit,
			final GenericVisitor visitor, final String fileName,
			final Commit commit, final ArrayList<Integer> lineChanges) {

		final String className = ((NodeSetEdit.Change<?>) edit).getIdentifier();
		final Transaction<?> transaction = ((NodeSetEdit.Change<?>) edit)
				.getTransaction();
		final List<NodeSetEdit> memberEdits = ((TypeTransaction) transaction)
				.getMemberEdits();

		treatEachNodeSetEdit(fileName, commit, className, memberEdits);
	}

	private void treatEachNodeSetEdit(final String fileName,
			final Commit commit, final String className,
			final List<NodeSetEdit> memberEdits) {

		for (final NodeSetEdit memberEdit : memberEdits) {
			try {
				final GenericVisitor visitor = new EditVisitor(fileName);

				if (memberEdit instanceof NodeSetEdit.Remove) {
					treatNodeSetEditRemove(fileName, className, visitor,
							memberEdit);
				}

				((EditVisitor) visitor).visit(memberEdit);

				if (checkEntryInResultSet(visitor, className, commit)) {
					addDataInMethodInformationList(fileName, className,
							visitor.getIdentifier());
				}
			} catch (final Exception e) {
				continue;
			}
		}
	}

	private void treatNodeSetEditRemove(final String fileName,
			final String className, final GenericVisitor visitor,
			final NodeSetEdit memberEdit) {

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
	public void handleNodeSetEditAdd(final NodeSetEdit edit,
			final GenericVisitor visitor, final String fileName,
			final Commit commit, final ArrayList<Integer> lineChanges) {

		final Node node = ((NodeSetEdit.Add) edit).getNode();

		if (node instanceof Node.Type) {
			treatGeneralNodeType(fileName, commit, node);
		}
	}

	private void treatGeneralNodeType(final String fileName,
			final Commit commit, final Node node) {

		final GenericVisitor visitor = new NodeVisitor(fileName);
		final Set<Node> members = ((Node.Type) node).getMembers();

		for (final Node member : members) {
			treatEachMemberNode(fileName, commit, node, visitor, member);
		}
	}

	private void treatEachMemberNode(final String fileName, final Commit commit,
			final Node node, final GenericVisitor visitor, final Node member) {

		try {
			if (member instanceof Node.Type) {
				treatNodeType(fileName, commit, visitor, member);
			} else if (member instanceof Node.Function) {
				treatNodeFunction(fileName, commit, node, visitor, member);
			}
		} catch (final Exception e) {
			return;
		}
	}

	private void treatNodeFunction(final String fileName, final Commit commit,
			final Node node, final GenericVisitor visitor, final Node member) {

		final String className = ((Node.Type) node).getName();
		((NodeVisitor) visitor).visit(member);

		if (checkEntryInResultSet(visitor, className, commit)) {
			addDataInMethodInformationList(fileName, className,
					visitor.getIdentifier());
		}
	}

	private void treatNodeType(final String fileName, final Commit commit,
			final GenericVisitor visitor, final Node member) {

		final String className = ((Node.Type) member).getName();

		final Set<Node> typeMembers = ((Node.Type) member).getMembers();
		for (final Node typeMember : typeMembers) {
			treatTypeMember(fileName, commit, visitor, className, typeMember);
		}
	}

	private void treatTypeMember(final String fileName, final Commit commit,
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
	public void addDataInMethodInformationList(final String fileName,
			final String className, final String methodName) {

		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
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
			methodInformationList = new ArrayList<>();

			for (final String fileName : filesList) {
				parseEachHistoryFile(fileName);
			}
		} catch (final IOException e) {
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

		++numberOfJavaSourcesCount;

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
		} catch (final Exception e) {
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

	public Map<String, FileMethodDynamics> sortFilesAffectedByNumberOfSupernovaMethods() {
		final List<Map.Entry<String, FileMethodDynamics>> listToSort = new LinkedList<>(
				methodDynamics.getProjectMethodDynamics().entrySet());

		sortCollectionInReverseOrder(listToSort);

		final Map<String, FileMethodDynamics> sortedMap = new LinkedHashMap<>();
		for (final Map.Entry<String, FileMethodDynamics> entry : listToSort) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private void sortCollectionInReverseOrder(
			final List<Map.Entry<String, FileMethodDynamics>> listToSort) {
		Collections.sort(listToSort,
				new Comparator<Map.Entry<String, FileMethodDynamics>>() {
					@Override
					public int compare(
							final Map.Entry<String, FileMethodDynamics> file1,
							final Map.Entry<String, FileMethodDynamics> file2) {
						return file1.getValue().getNumberOfSupernovaMethods()
								.compareTo(file2.getValue()
										.getNumberOfSupernovaMethods());
					}
				});

		Collections.reverse(listToSort);
	}

	/**
	 * Writes data from method dynamics analysis into a JSON and a CSV file.
	 */
	public void writeJSONMethodDynamicsData() {
		final JSONUtils jsonUtils = new JSONUtils();
		try {
			final JsonArray jsonArray = new JsonArray();

			final Map<String, FileMethodDynamics> sortedMetricResultMap = sortFilesAffectedByNumberOfSupernovaMethods();

			for (final HashMap.Entry<String, FileMethodDynamics> entry : sortedMetricResultMap
					.entrySet()) {
				parseMethodDynamicsSet(jsonUtils, jsonArray, entry);
			}

			// entireJson.add("repository result", jsonArray);
			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			jsonWriter.write(gson.toJson(jsonArray));
			// jsonWriter.write(gson.toJson(entireJson));
		} catch (final IOException e) {
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

			setFileWithPulsarMethodsData(entry, numberOfPulsarMethods,
					severityOfPulsarMethods);
		}
	}

	private void setFileWithPulsarMethodsData(
			final HashMap.Entry<String, FileMethodDynamics> entry,
			final Integer numberOfPulsarMethods,
			final Integer severityOfPulsarMethods) {
		final FileWithAstronomicalMethods fileWithAstronomicalMethods = new FileWithAstronomicalMethods();

		fileWithAstronomicalMethods.setFileName(entry.getKey());
		fileWithAstronomicalMethods
				.setNumberOfAstronomicalMethods(numberOfPulsarMethods);
		fileWithAstronomicalMethods.setSumOfSeverity(severityOfPulsarMethods);

		filesWithPulsarMethods.put(entry.getKey(), fileWithAstronomicalMethods);
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

			setFileWithSupernovaMethodsData(entry, numberOfSupernovaMethods,
					severityOfSupernovaMethods);
		}
	}

	private void setFileWithSupernovaMethodsData(
			final HashMap.Entry<String, FileMethodDynamics> entry,
			final Integer numberOfSupernovaMethods,
			final Integer severityOfSupernovaMethods) {

		final FileWithAstronomicalMethods fileWithAstronomicalMethods = new FileWithAstronomicalMethods();

		fileWithAstronomicalMethods.setFileName(entry.getKey());
		fileWithAstronomicalMethods
				.setNumberOfAstronomicalMethods(numberOfSupernovaMethods);
		fileWithAstronomicalMethods
				.setSumOfSeverity(severityOfSupernovaMethods);

		filesWithSupernovaMethods.put(entry.getKey(),
				fileWithAstronomicalMethods);
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
	public Map<String, AstronomicalMethodChangesInformation> getResult() {
		return allMethodsResult;
	}

	public void setResult(
			final Map<String, AstronomicalMethodChangesInformation> result) {
		this.allMethodsResult = result;
	}

	public ArrayList<Commit> getAllCommits() {
		return allCommits;
	}

	public ArrayList<AstronomicalMethodChangesInformation> getMethodInformationList() {
		return methodInformationList;
	}

	public void setMethodInformationList(
			final ArrayList<AstronomicalMethodChangesInformation> methodInformationList) {
		this.methodInformationList = methodInformationList;
	}

	public Optional<PersistentProject> getProject() {
		return project;
	}

	public void setProject(final Optional<PersistentProject> project) {
		this.project = project;
	}

	public ArrayList<String> getDeletedNodes() {
		return deletedNodes;
	}

	public void setDeletedNodes(final ArrayList<String> deletedNodes) {
		this.deletedNodes = deletedNodes;
	}

	public static Integer getNumberOfJavaSourcesCount() {
		return numberOfJavaSourcesCount;
	}

	public MethodDynamicsUtils getMethodDynamics() {
		return methodDynamics;
	}

	public void setMethodDynamics(final MethodDynamicsUtils methodDynamics) {
		this.methodDynamics = methodDynamics;
	}

	public Map<String, FileWithAstronomicalMethods> getFilesWithSupernovaMethods() {
		return filesWithSupernovaMethods;
	}

	public Map<String, FileWithAstronomicalMethods> getFilesWithPulsarMethods() {
		return filesWithPulsarMethods;
	}

}
