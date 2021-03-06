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
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MethodThresholdsMeasure.MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package edu.lavinia.inspectory.am.metrics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.lavinia.inspectory.am.beans.AstronomicalMethodChangesInformation;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;

/**
 * Implementation of {@link edu.lavinia.inspectory.am.metrics.MethodMetrics
 * MethodThresholdsMeasure.MethodMetrics} class for Pulsar methods
 * identification.
 *
 * @author Lavinia Cioloca
 * @see {@link edu.lavinia.inspectory.am.metrics.SupernovaMetric
 *      SupernovaMetric}
 *
 */
public class PulsarMetric {

	/**
	 * It is considered for a method to have <b>many pulsar cycles</b> if it has
	 * more than 3 commits.
	 */
	private final static Integer MANY_PULSAR_CYCLES = 3;

	private Integer recentCyclesPoints = 0;
	private Integer averageSizeIncreasePoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	private Integer maximumTimeFrameNumber;
	private ArrayList<Commit> allCommits;
	private Map<Commit, Integer> allCommitsIntoTimeFrames;

	/**
	 * @param countRecentPulsarCycles
	 * @return An Integer between 0 and 3 representing the points from the
	 *         {@code recentPulsarCycles}.
	 */
	public Integer getRecentCyclesPoints(
			final Integer countRecentPulsarCycles) {

		if (countRecentPulsarCycles >= 6) {
			return 3;
		} else if (countRecentPulsarCycles >= 3
				&& countRecentPulsarCycles <= 5) {
			return 2;
		} else if (countRecentPulsarCycles >= 1
				&& countRecentPulsarCycles <= 2) {
			return 1;
		}

		return 0;
	}

	/**
	 * @param averageSizeIncrease
	 * @return An Integer between 0 and 3 representing the points of the Pulsar
	 *         method's {@code averageSizeIncrease}.
	 */
	public Integer getAverageSizeIncrease(final Double averageSizeIncrease) {
		if (averageSizeIncrease >= 0.0 && averageSizeIncrease < (1.0 / 3.0)
				* MethodThresholdsMeasure.MAJOR_SIZE_CHANGE) {
			return 3;
		} else if (averageSizeIncrease >= (1.0 / 3.0)
				* MethodThresholdsMeasure.MAJOR_SIZE_CHANGE
				&& averageSizeIncrease < (2.0 / 3.0)
						* MethodThresholdsMeasure.MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (averageSizeIncrease >= (2.0 / 3.0)
				* MethodThresholdsMeasure.MAJOR_SIZE_CHANGE
				&& averageSizeIncrease < MethodThresholdsMeasure.MAJOR_SIZE_CHANGE) {
			return 1;
		}

		return 0;
	}

	public Integer getMethodSizePoints(final Integer methodSize) {
		if (methodSize >= MethodThresholdsMeasure.EXTREMELY_LARGE_METHOD) {
			return 2;
		} else if (methodSize >= MethodThresholdsMeasure.VERY_LARGE_METHOD) {
			return 1;
		}

		return 0;
	}

	/**
	 * If a method has been actively changed over the last LONG_TIMESPAN
	 * time-frames then it is an Actively Changed method.
	 *
	 * @param methodChangesInformation
	 * @return A Boolean: true if the method is actively changed.
	 */
	public Boolean isMethodActivelyChanged(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final ArrayList<Commit> latestCommits = new ArrayList<>();

		if (commits.size() < MethodThresholdsMeasure.ACTIVELY_CHANGED) {
			return false;
		}

		addLatestCommitsList(commits, latestCommits);

		final Integer countActiveChanges = checkIfActivityIsInLongTimespan(
				latestCommits);

		return countActiveChanges >= MethodThresholdsMeasure.ACTIVELY_CHANGED;
	}

	private Integer checkIfActivityIsInLongTimespan(
			final ArrayList<Commit> latestCommits) {

		Integer countActiveChanges = 0;

		for (int i = allCommits.size() - 1; i >= 0; --i) {
			if ((allCommitsIntoTimeFrames
					.get(allCommits.get(i)) >= maximumTimeFrameNumber
							- MethodThresholdsMeasure.LONG_TIMESPAN)) {
				for (int j = 0; j < latestCommits.size(); ++j) {
					if (allCommits.get(i).equals(latestCommits.get(j))) {
						++countActiveChanges;
					}
				}
			} else {
				break;
			}
		}

		return countActiveChanges;
	}

	private void addLatestCommitsList(final ArrayList<Commit> commits,
			final ArrayList<Commit> latestCommits) {

		for (int i = commits.size() - 1; i > commits.size() - 1
				- MethodThresholdsMeasure.ACTIVELY_CHANGED; --i) {
			latestCommits.add(commits.get(i));
		}
	}

	/**
	 * @param countRecentPulsarCycles
	 * @param averageSizeIncrease
	 * @param fileSize
	 * @param lastCommit
	 * @return An Integer representing the total points of Pulsar severity.
	 */
	public Integer countPulsarSeverityPoints(
			final Integer countRecentPulsarCycles,
			final Double averageSizeIncrease, final Integer fileSize,
			final Commit lastCommit) {

		recentCyclesPoints = getRecentCyclesPoints(countRecentPulsarCycles);
		averageSizeIncreasePoints = getAverageSizeIncrease(averageSizeIncrease);
		methodSizePoints = getMethodSizePoints(fileSize);
		activityStatePoints = MethodThresholdsMeasure.getActiveMethodPoints(
				lastCommit, allCommits.get(allCommits.size() - 1));

		return 1 + recentCyclesPoints + averageSizeIncreasePoints
				+ methodSizePoints + activityStatePoints;
	}

	/**
	 * @param countPulsarCycles
	 * @param sumOfSizeIncrease
	 * @return The {@code averageSizeIncrease} criterion for Pulsar methods.
	 */
	public Double calculateAverageSizeIncrease(final Integer countPulsarCycles,
			final Integer sumOfSizeIncrease) {

		Double averageSizeIncrease = 0.0;

		if (countPulsarCycles > 0) {
			averageSizeIncrease = (double) sumOfSizeIncrease
					/ (double) countPulsarCycles;
		}

		return averageSizeIncrease;
	}

	/**
	 * Calculates the overall Pulsar severity points of a method.
	 *
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return An Integer that represents the Pulsar severity of the given
	 *         method.
	 */
	public Integer getPulsarSeverity(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final Map<String, Object> pulsarCriterionValues = getPulsarCriterionValues(
				methodChangesInformation);
		final Integer countRecentPulsarCycles = (Integer) pulsarCriterionValues
				.get("countRecentPulsarCycles");
		final Double averageSizeIncrease = (Double) pulsarCriterionValues
				.get("averageSizeIncrease");

		return countPulsarSeverityPoints(countRecentPulsarCycles,
				averageSizeIncrease, methodChangesInformation.getActualSize(),
				commits.get(commits.size() - 1));
	}

	/**
	 * A PulsarCycle is considered recent if it was detected very recently, i.e.
	 * in the last MethodThresholdsMeasure.MEDIUM_TIMESPAN time-frames of the
	 * project.
	 *
	 * @param commitDate
	 * @return An Integer: 1 if Pulsar cycle is recent and 0 if false.
	 */
	/*
	 * public Integer checkIfRecentPulsarCycle(Date commitDate) { if
	 * (getDifferenceInDays(commitDate, now) <=
	 * MethodThresholdsMeasure.MEDIUM_TIMESPAN) { return 1; } return 0; }
	 */

	/**
	 * A PulsarCycle is considered recent if it was detected very recently, i.e.
	 * in the last MethodThresholdsMeasure.MEDIUM_TIMESPAN time-frames of the
	 * project.
	 *
	 * @param commitDate
	 * @return An Integer: 1 if Pulsar cycle is recent and 0 if false.
	 */
	public Integer checkIfRecentPulsarCycle(final Date commitDate) {
		for (int i = allCommits.size() - 1; i >= 0; --i) {
			if ((allCommitsIntoTimeFrames
					.get(allCommits.get(i)) >= maximumTimeFrameNumber
							- MethodThresholdsMeasure.MEDIUM_TIMESPAN)) {
				if (allCommits.get(i).getDate().compareTo(commitDate) <= 0) {
					return 1;
				} else {
					break;
				}
			}
		}

		return 0;
	}

	/**
	 * @param methodChangesInformation
	 * @return A MethodThresholdsMeasure.Map with the values for the following
	 *         Pulsar criteria: averageSizeIncrease; countPulsarCycles;
	 *         countRecentPulsarCycles; isPulsar.
	 */
	public Map<String, Object> getPulsarCriterionValues(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final Map<String, Object> pulsarCriterionValues = new HashMap<>();

		pulsarCriterionValues.put("isPulsar", false);
		Integer sumOfSizeIncrease = 0;
		Double averageSizeIncrease = 0.0;
		Integer countRecentPulsarCycles = 0;
		Integer countPulsarCycles = 0;
		Integer methodGrowth = 0;

		if (methodChangesInformation
				.getActualSize() >= MethodThresholdsMeasure.SIGNIFICANT_METHOD_SIZE
				&& isMethodActivelyChanged(methodChangesInformation)) {
			final ArrayList<Integer> changesList = methodChangesInformation
					.getChangesList();
			final ArrayList<String> commitsTypes = MethodThresholdsMeasure
					.getCommitsTypes(changesList);

			for (int i = 0; i < commitsTypes.size() - 1; ++i) {
				if (commitsTypes.get(i).equals("refactor")
						&& commitsTypes.get(i + 1).equals("develop")) {
					++countPulsarCycles;
					countRecentPulsarCycles += checkIfRecentPulsarCycle(
							commits.get(i).getDate());
				}

				if (commitsTypes.get(i).equals("refine")) {
					methodGrowth += changesList.get(i);
					if (methodGrowth >= MethodThresholdsMeasure.SMALL_SIZE_CHANGE) {
						++countPulsarCycles;
						countRecentPulsarCycles += checkIfRecentPulsarCycle(
								commits.get(i).getDate());
					}
				} else {
					methodGrowth = 0;
				}

				if (!commitsTypes.get(i).equals("refactor")
						&& commitsTypes.get(i + 1).equals("refactor")) {
					sumOfSizeIncrease += changesList.get(i);
				}

				treatMethodWithManyPulsarCycles(pulsarCriterionValues,
						countPulsarCycles);
			}
		}

		averageSizeIncrease = calculateAverageSizeIncrease(countPulsarCycles,
				sumOfSizeIncrease);

		putPulsarCriterionValues(pulsarCriterionValues, averageSizeIncrease,
				countRecentPulsarCycles, countPulsarCycles);

		return pulsarCriterionValues;
	}

	private void treatMethodWithManyPulsarCycles(
			final Map<String, Object> pulsarCriterionValues,
			final Integer countPulsarCycles) {

		if (countPulsarCycles >= MANY_PULSAR_CYCLES
				&& !pulsarCriterionValues.get("isPulsar").equals(true)) {
			pulsarCriterionValues.put("isPulsar", true);
		}
	}

	private void putPulsarCriterionValues(
			final Map<String, Object> pulsarCriterionValues,
			final Double averageSizeIncrease,
			final Integer countRecentPulsarCycles,
			final Integer countPulsarCycles) {

		pulsarCriterionValues.put("averageSizeIncrease", averageSizeIncrease);
		pulsarCriterionValues.put("countPulsarCycles", countPulsarCycles);
		pulsarCriterionValues.put("countRecentPulsarCycles",
				countRecentPulsarCycles);
	}

	/**
	 * To be a Pulsar, a method must: have at least
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.SIGNIFICANT_METHOD_SIZE
	 * SIGNIFICANT_METHOD_SIZE} lines, and have been actively changed over the
	 * last {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.LONG_TIMESPAN
	 * LONG_TIMESPAN}. We count the number of refactor commits that are preceded
	 * by at least one develop commit, or an uninterrupted sequence of refine
	 * commits that cumulated produce a file growth that is larger than
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.SMALL_SIZE_CHANGE
	 * SMALL_SIZE_CHANGE} lines. A Pulsar needs to have at least
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.MANY_PULSAR_CYCLES
	 * MethodThresholdsMeasure.MANY_PULSAR_CYCLES}.
	 *
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return {@code True} if the method is Pulsar, {@code false} otherwise.
	 */
	public Boolean isPulsar(
			final AstronomicalMethodChangesInformation methodChangesInformation) {

		return (Boolean) getPulsarCriterionValues(methodChangesInformation)
				.get("isPulsar");
	}

	public Integer getRecentCyclesPoints() {
		return recentCyclesPoints;
	}

	public Integer getAverageSizeIncreasePoints() {
		return averageSizeIncreasePoints;
	}

	public Integer getMethodSizePoints() {
		return methodSizePoints;
	}

	public Integer getActivityStatePoints() {
		return activityStatePoints;
	}

	public ArrayList<Commit> getAllCommits() {
		return allCommits;
	}

	public void setAllCommits(final ArrayList<Commit> allCommits) {
		this.allCommits = allCommits;
	}

	public Map<Commit, Integer> getAllCommitsIntoTimeFrames() {
		return allCommitsIntoTimeFrames;
	}

	public void setAllCommitsIntoTimeFrames(
			final Map<Commit, Integer> allCommitsIntoTimeFrames) {
		this.allCommitsIntoTimeFrames = allCommitsIntoTimeFrames;
	}

	public Integer getMaximumTimeFrameNumber() {
		return maximumTimeFrameNumber;
	}

	public void setMaximumTimeFrameNumber(
			final Integer maximumTimeFrameNumber) {
		this.maximumTimeFrameNumber = maximumTimeFrameNumber;
	}

}
