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
package edu.lavinia.inspectory.op.inspection;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.op.beans.FileChangesData;

public abstract class GenericOwnershipInspection {
	protected final Optional<PersistentProject> project;
	protected final FileWriter csvWriter;
	protected final FileWriter jsonWriter;
	protected Map<String, FileChangesData> entityChangesData = new HashMap<>();

	protected static Integer numberOfJavaSourcesCount = 0;

	/**
	 * GenericOwnershipInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 *
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public GenericOwnershipInspection(final Optional<PersistentProject> project,
			final FileWriter csvWriter, final FileWriter jsonWriter) {
		this.project = project;
		this.csvWriter = csvWriter;
		this.jsonWriter = jsonWriter;
	}

	public LinkedHashMap<String, List<Integer>> checkChangedLinesInMap(
			final ArrayList<Integer> changedLines,
			final LinkedHashMap<String, List<Integer>> authorsLineChanges,
			final String author, final Integer visitorAddedLines,
			final Integer visitorDeletedLines) {

		final ArrayList<Integer> newChangedLines;

		if (changedLines == null) {
			newChangedLines = new ArrayList<>(
					Arrays.asList(visitorAddedLines, visitorDeletedLines));

			authorsLineChanges.put(author, newChangedLines);
		} else {
			newChangedLines = new ArrayList<>();

			newChangedLines.add(changedLines.get(0) + visitorAddedLines);
			newChangedLines.add(changedLines.get(1) + visitorDeletedLines);

			authorsLineChanges.put(author, newChangedLines);
		}

		return authorsLineChanges;
	}

	public final void setEntityOwnerAfterCommit(final String entityName) {
		LinkedHashMap<String, Double> ownershipPercentages = calculateEntityOwnership(
				entityName);
		ownershipPercentages = sortPercentagesMap(ownershipPercentages);

		final String entityOwnerAfterThisCommit = ownershipPercentages
				.entrySet().iterator().next().getKey();
		ArrayList<String> listOfPreviousOwners = entityChangesData
				.get(entityName).getAllOwners();

		if (listOfPreviousOwners == null) {
			listOfPreviousOwners = new ArrayList<>();
			entityChangesData.get(entityName)
					.setAllOwners(listOfPreviousOwners);
		}

		listOfPreviousOwners.add(entityOwnerAfterThisCommit);
	}

	public final LinkedHashMap<String, Double> calculateEntityOwnership(
			final String entityName) {

		final LinkedHashMap<String, Double> ownershipPercentages = new LinkedHashMap<>();
		final Map<String, List<Integer>> authorsLineChanges = entityChangesData
				.get(entityName).getAuthorsAddedAndDeletedLines();

		Integer authorLineChanges;
		Double authorPercentage;

		for (final Entry<String, List<Integer>> entry : authorsLineChanges
				.entrySet()) {
			final ArrayList<Integer> addedAndDeletedLines = (ArrayList<Integer>) entry
					.getValue();

			authorLineChanges = (addedAndDeletedLines.get(0)
					+ addedAndDeletedLines.get(1));

			authorPercentage = getAuthorOwnershipPercentage(authorLineChanges,
					entityName);

			ownershipPercentages.put(entry.getKey(), authorPercentage);
		}

		return ownershipPercentages;
	}

	private final Double getAuthorOwnershipPercentage(
			final Integer authorLineChanges, final String entityName) {

		final DecimalFormat twoDecimalsFormat = new DecimalFormat(".##");
		Double authorPercentage;
		/*
		 * (x / 100) * entityTotalLineChanges = authorLineChanges => x = (100 *
		 * authorLineChanges) / entityTotalLineChanges
		 */
		if (entityChangesData.get(entityName)
				.getAddedAndDeletedLinesSum() == 0) {
			authorPercentage = 0.0;
		} else {
			authorPercentage = (100 * (double) authorLineChanges)
					/ (double) entityChangesData.get(entityName)
							.getAddedAndDeletedLinesSum();
			authorPercentage = Double
					.parseDouble(twoDecimalsFormat.format(authorPercentage));
		}

		return authorPercentage;
	}

	public final LinkedHashMap<String, Double> sortPercentagesMap(
			final Map<String, Double> ownershipPercentages) {

		return ownershipPercentages.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue, (x, y) -> {
							throw new AssertionError();
						}, LinkedHashMap::new));
	}

	public abstract void writeFileResults();

	public abstract void createResults();

	public Map<String, FileChangesData> getEntityChangesData() {
		return entityChangesData;
	}

	public void setEntityChangesData(
			final Map<String, FileChangesData> entityChangesData) {
		this.entityChangesData = entityChangesData;
	}

	public static Integer getNumberOfJavaSourcesCount() {
		return numberOfJavaSourcesCount;
	}

}
