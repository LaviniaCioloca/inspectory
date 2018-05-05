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

import edu.lavinia.inspectory.op.beans.EntityOwnershipInformation;

public abstract class GenericOwnershipInspection {
	protected final Optional<PersistentProject> project;
	protected final FileWriter csvWriter;
	protected Map<String, EntityOwnershipInformation> entityOwnershipResult = new HashMap<>();

	/**
	 * GenericOwnershipInspection Constructor that receives the persistent
	 * project and the CSV file to write in from {@code inspectory-main}.
	 * 
	 * @param project
	 *            The project of the repository to inspect.
	 * @param csvWriter
	 *            The writer of result CSV file.
	 */
	public GenericOwnershipInspection(Optional<PersistentProject> project,
			FileWriter csvWriter) {
		this.project = project;
		this.csvWriter = csvWriter;
	}

	public LinkedHashMap<String, List<Integer>> checkChangedLinesInMap(
			ArrayList<Integer> changedLines,
			LinkedHashMap<String, List<Integer>> authorsLineChanges,
			String author, Integer visitorAddedLines,
			Integer visitorDeletedLines) {
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

	public LinkedHashMap<String, Double> calculateEntityOwnership(
			LinkedHashMap<String, List<Integer>> authorsLineChanges) {
		final LinkedHashMap<String, Double> ownershipPercentages = new LinkedHashMap<>();

		Integer authorLineChanges;
		Double authorPercentage;

		final Integer entityCurrentSize = getEntityCurrentSize(
				authorsLineChanges);

		for (final Entry<String, List<Integer>> entry : authorsLineChanges
				.entrySet()) {
			final ArrayList<Integer> addedAndDeletedLines = (ArrayList<Integer>) entry
					.getValue();

			authorLineChanges = (addedAndDeletedLines.get(0)
					- addedAndDeletedLines.get(1));

			authorPercentage = getAuthorOwnership(authorLineChanges,
					entityCurrentSize);

			ownershipPercentages.put(entry.getKey(), authorPercentage);
		}

		return ownershipPercentages;
	}

	private Double getAuthorOwnership(Integer authorLineChanges,
			final Integer entityTotalLineChanges) {
		final DecimalFormat twoDecimalsFormat = new DecimalFormat(".##");
		Double authorPercentage;
		/*
		 * (x / 100) * entityTotalLineChanges = authorLineChanges => x = (100 *
		 * authorLineChanges) / entityTotalLineChanges
		 */
		if (entityTotalLineChanges == 0) {
			authorPercentage = 0.0;
		} else {
			authorPercentage = (100 * (double) authorLineChanges)
					/ (double) entityTotalLineChanges;
			authorPercentage = Double
					.parseDouble(twoDecimalsFormat.format(authorPercentage));
		}

		return authorPercentage;
	}

	private Integer getEntityCurrentSize(
			LinkedHashMap<String, List<Integer>> authorsLineChanges) {
		Integer fileCurrentSize = 0;

		for (final Map.Entry<String, List<Integer>> entry : authorsLineChanges
				.entrySet()) {
			final ArrayList<Integer> addedAndDeletedLines = (ArrayList<Integer>) entry
					.getValue();

			fileCurrentSize += addedAndDeletedLines.get(0);
			fileCurrentSize -= addedAndDeletedLines.get(1);
		}

		return fileCurrentSize;
	}

	public LinkedHashMap<String, Double> sortPercentagesMap(
			LinkedHashMap<String, Double> ownershipPercentages) {
		return ownershipPercentages.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue, (x, y) -> {
							throw new AssertionError();
						}, LinkedHashMap::new));
	}

	public abstract void writeFileResults();

	public abstract void createResults();

	public Map<String, EntityOwnershipInformation> getEntityOwnershipResult() {
		return entityOwnershipResult;
	}

	public void setEntityOwnershipResult(
			Map<String, EntityOwnershipInformation> entityOwnershipResult) {
		this.entityOwnershipResult = entityOwnershipResult;
	}

	public void setEntityOwnershipResult(
			HashMap<String, EntityOwnershipInformation> entityOwnershipResult) {
		this.entityOwnershipResult = entityOwnershipResult;
	}

}
