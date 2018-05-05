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
package edu.lavinia.inspectory.am.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import edu.lavinia.inspectory.am.beans.MethodChangesInformation;
import edu.lavinia.inspectory.beans.Commit;

/**
 * Implementation of {@link edu.lavinia.inspectory.am.metrics.MethodMetrics
 * MethodMetrics} class for Supernova methods identification.
 * 
 * @author Lavinia Cioloca
 * @see {@link edu.lavinia.inspectory.am.metrics.PulsarMetric PulsarMetric}
 *
 */
public class SupernovaMetric extends MethodMetrics {
	private Integer maximumTimeInterval = 0;
	private Integer leapsSizePoints = 0;
	private Integer recentLeapsSizePoints = 0;
	private Integer subsequentRefactoringPoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	/**
	 * @param sumOfAllLeaps
	 * @return An Integer between 0 and 2 representing the points of leaps size.
	 */
	public Integer getLeapsSizePoints(Integer sumOfAllLeaps) {
		if (sumOfAllLeaps >= 4 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (sumOfAllLeaps >= 2 * MAJOR_SIZE_CHANGE
				&& sumOfAllLeaps < 4 * MAJOR_SIZE_CHANGE) {
			return 1;
		}

		return 0;
	}

	/**
	 * @param sumRecentLeaps
	 * @return An Integer between 0 and 3 representing the points of recent
	 *         leaps size.
	 */
	public Integer getRecentLeapsSizePoints(Integer sumRecentLeaps) {
		if (sumRecentLeaps >= 4 * MAJOR_SIZE_CHANGE) {
			return 3;
		} else if (sumRecentLeaps >= 2.5 * MAJOR_SIZE_CHANGE
				&& sumRecentLeaps < 4 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (sumRecentLeaps >= 1.5 * MAJOR_SIZE_CHANGE
				&& sumRecentLeaps < 2.5 * MAJOR_SIZE_CHANGE) {
			return 1;
		}

		return 0;
	}

	/**
	 * @param averageSubsequentCommits
	 * @return An Integer between 0 and 2 representing the points of Subsequent
	 *         Refactoring.
	 */
	public Integer getSubsequentRefactoringPoints(
			Double averageSubsequentCommits) {

		if (averageSubsequentCommits >= 0 * MAJOR_SIZE_CHANGE
				&& averageSubsequentCommits < 0.5 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (averageSubsequentCommits >= 0.5 * MAJOR_SIZE_CHANGE
				&& averageSubsequentCommits < 1 * MAJOR_SIZE_CHANGE) {
			return 1;
		}

		return 0;
	}

	/**
	 * @param sumOfAllLeaps
	 * @param sumRecentLeaps
	 * @param averageSubsequentCommits
	 * @param fileSize
	 * @param commit
	 * @return An Integer representing the total points of Supernova severity.
	 */
	public Integer countSupernovaSeverityPoints(Integer sumOfAllLeaps,
			Integer sumRecentLeaps, Double averageSubsequentCommits,
			Integer fileSize, final Commit commit) {

		leapsSizePoints = getLeapsSizePoints(sumOfAllLeaps);
		recentLeapsSizePoints = getRecentLeapsSizePoints(sumRecentLeaps);
		subsequentRefactoringPoints = getSubsequentRefactoringPoints(
				averageSubsequentCommits);
		methodSizePoints = getMethodSizePoints(fileSize);
		activityStatePoints = getActiveMethodPoints(commit);

		return 1 + leapsSizePoints + recentLeapsSizePoints
				+ subsequentRefactoringPoints + methodSizePoints
				+ activityStatePoints;
	}

	/**
	 * @param commitsIntoTimeFrames
	 * @return ArrayList with the commits encountered after the
	 *         {@code MEDIUM_TIMESPAN}.
	 */
	public ArrayList<Commit> getCommitsAfterMediumTimespan(
			HashMap<Commit, Integer> commitsIntoTimeFrames) {

		final Iterator<Map.Entry<Commit, Integer>> entries = commitsIntoTimeFrames
				.entrySet().iterator();
		final ArrayList<Commit> commitsAfterMediumTimespan = new ArrayList<>();

		while (entries.hasNext()) {
			final Map.Entry<Commit, Integer> currentEntry = entries.next();

			if (currentEntry.getValue() >= MEDIUM_TIMESPAN) {
				commitsAfterMediumTimespan.add(currentEntry.getKey());
			}
		}

		return commitsAfterMediumTimespan;
	}

	/**
	 * @param commits
	 * @return A map with every commit and the associated time interval number.
	 */
	public HashMap<Commit, Integer> splitCommitsIntoTimeIntervals(
			ArrayList<Commit> commits) {

		final HashMap<Commit, Integer> commitsIntoTimeIntervals = new HashMap<>();
		Integer currentTimeInterval = 0;
		commitsIntoTimeIntervals.put(commits.get(0), currentTimeInterval);

		for (int i = 1; i < commits.size(); ++i) {
			if (getTimeDifferenceInDays(commits.get(i - 1).getDate(),
					commits.get(i).getDate()) > SHORT_TIMESPAN * TIME_FRAME) {
				++currentTimeInterval;
			}

			commitsIntoTimeIntervals.put(commits.get(i), currentTimeInterval);
		}

		maximumTimeInterval = currentTimeInterval;

		return commitsIntoTimeIntervals;
	}

	/**
	 * @param commitsAfterMediumTimespan
	 * @return Sorted ArrayList of commits after the
	 *         {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.MEDIUM_TIMESPAN
	 *         MEDIUM_TIMESPAN}.
	 */
	public ArrayList<Commit> sortAllCommitsAfterMediumTimespan(
			final ArrayList<Commit> commitsAfterMediumTimespan) {

		Collections.sort(commitsAfterMediumTimespan, new Comparator<Commit>() {
			@Override
			public int compare(final Commit commit1, final Commit commit2) {
				return commit1.getDate().compareTo(commit2.getDate());
			}
		});

		return commitsAfterMediumTimespan;
	}

	/**
	 * @param methodChangesInformation
	 * @return The lifetime of the method divided into a HashMap with commits
	 *         and their associated interval number.
	 */
	public HashMap<Commit, Integer> divideLifetimeInIntervals(
			MethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final HashMap<Commit, Integer> commitsIntoTimeFrames = splitCommitsIntoTimeFrames(
				commits);
		ArrayList<Commit> commitsAfterMediumTimespan = getCommitsAfterMediumTimespan(
				commitsIntoTimeFrames);
		commitsAfterMediumTimespan = sortAllCommitsAfterMediumTimespan(
				commitsAfterMediumTimespan);
		HashMap<Commit, Integer> lifetimeIntoIntervals = new HashMap<>();

		if (commitsAfterMediumTimespan.size() > 0) {
			lifetimeIntoIntervals = splitCommitsIntoTimeIntervals(
					commitsAfterMediumTimespan);
		}

		return lifetimeIntoIntervals;
	}

	/**
	 * For every commit put the number of lines added or deleted into a map.
	 * 
	 * @param commits
	 * @param changesList
	 * @return A map for every commit and the number of lines added/deleted in
	 *         that commit.
	 */
	public HashMap<Commit, Integer> getCommitsAndChangesMap(
			ArrayList<Commit> commits, ArrayList<Integer> changesList) {

		final HashMap<Commit, Integer> commitsAndTheirChanges = new HashMap<>();

		for (int i = 0; i < commits.size(); ++i) {
			commitsAndTheirChanges.put(commits.get(i), changesList.get(i));
		}

		return commitsAndTheirChanges;
	}

	/**
	 * Associates for every time interval its commits list.
	 * 
	 * @param commitsIntoTimeIntervals
	 * @return A sorted map with every time interval with its commits list.
	 */
	public Map<Integer, ArrayList<Commit>> getIntervalsCommitsMap(
			HashMap<Commit, Integer> commitsIntoTimeIntervals) {

		final TreeMap<Integer, ArrayList<Commit>> intervalsCommitsList = new TreeMap<>();

		for (HashMap.Entry<Commit, Integer> entry : commitsIntoTimeIntervals
				.entrySet()) {
			if (intervalsCommitsList.get(entry.getValue()) == null) {
				final ArrayList<Commit> commits = new ArrayList<>();
				commits.add(entry.getKey());
				intervalsCommitsList.put(entry.getValue(), commits);
			} else {
				intervalsCommitsList.get(entry.getValue()).add(entry.getKey());
			}
		}

		return intervalsCommitsList;
	}

	/**
	 * @param methodChangesInformation
	 * @return A Map with the values for the following Supernova criteria:
	 *         sumOfAllLeaps; sumRecentLeaps; averageSubsequentCommits;
	 *         isSupernova.
	 */
	public Map<String, Object> getSupernovaCriterionValues(
			MethodChangesInformation methodChangesInformation) {

		final Map<String, Object> supernovaCriterionValues = new HashMap<>();
		supernovaCriterionValues.put("isSupernova", false);
		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final ArrayList<Integer> changesList = methodChangesInformation
				.getChangesList();

		Integer sumOfAllLeaps = 0;
		Integer sumRecentLeaps = 0;
		Integer deletedRefactoringLines = 0;
		Integer numberOfSubsequentRefactoring = 0;
		Double averageSubsequentCommits = 0.0;
		final HashMap<Commit, Integer> commitsIntoTimeIntervals = divideLifetimeInIntervals(
				methodChangesInformation);

		if (methodChangesInformation.getActualSize() >= 0
				&& maximumTimeInterval > 0) {
			final HashMap<Commit, Integer> commitsAndTheirChanges = getCommitsAndChangesMap(
					commits, changesList);

			for (final HashMap.Entry<Commit, Integer> entry : commitsIntoTimeIntervals
					.entrySet()) {
				sumOfAllLeaps += commitsAndTheirChanges.get(entry.getKey());
				sumRecentLeaps = checkIfCommitIsInMediumTimespan(commits,
						sumRecentLeaps, commitsAndTheirChanges, entry);
			}

			final Map<Integer, ArrayList<Commit>> intervalsCommitsMap = getIntervalsCommitsMap(
					commitsIntoTimeIntervals);

			final Integer[] refactoringValues = calculateRefactoringValues(
					intervalsCommitsMap, commitsAndTheirChanges);
			deletedRefactoringLines = refactoringValues[0];
			numberOfSubsequentRefactoring = refactoringValues[1];

			checkMethodGrowthInEachTimeInterval(supernovaCriterionValues,
					commitsAndTheirChanges, intervalsCommitsMap);
		}

		if (numberOfSubsequentRefactoring > 0) {
			averageSubsequentCommits = (double) -deletedRefactoringLines
					/ (double) numberOfSubsequentRefactoring;
		}

		putSupernovaCriterionValues(supernovaCriterionValues, sumOfAllLeaps,
				sumRecentLeaps, averageSubsequentCommits);

		return supernovaCriterionValues;
	}

	private void putSupernovaCriterionValues(
			final Map<String, Object> supernovaCriterionValues,
			Integer sumOfAllLeaps, Integer sumRecentLeaps,
			Double averageSubsequentCommits) {

		supernovaCriterionValues.put("sumOfAllLeaps", sumOfAllLeaps);
		supernovaCriterionValues.put("sumRecentLeaps", sumRecentLeaps);
		supernovaCriterionValues.put("averageSubsequentCommits",
				averageSubsequentCommits);
	}

	private void checkMethodGrowthInEachTimeInterval(
			final Map<String, Object> supernovaCriterionValues,
			final HashMap<Commit, Integer> commitsAndTheirChanges,
			final Map<Integer, ArrayList<Commit>> intervalsCommitsMap) {

		for (int timeInterval = 0; timeInterval <= maximumTimeInterval; ++timeInterval) {
			final Integer methodGrowthInInterval = getMethodGrowthInInterval(
					intervalsCommitsMap, timeInterval, commitsAndTheirChanges);

			if (methodGrowthInInterval >= MAJOR_SIZE_CHANGE
					&& !supernovaCriterionValues.get("isSupernova")
							.equals(true)) {
				supernovaCriterionValues.put("isSupernova", true);
				break;
			}
		}
	}

	private Integer checkIfCommitIsInMediumTimespan(
			final ArrayList<Commit> commits, Integer sumRecentLeaps,
			final HashMap<Commit, Integer> commitsAndTheirChanges,
			final HashMap.Entry<Commit, Integer> entry) {

		if (getTimeDifferenceInDays(entry.getKey().getDate(),
				commits.get(commits.size() - 1).getDate()) <= MEDIUM_TIMESPAN
						* TIME_FRAME) {
			sumRecentLeaps += commitsAndTheirChanges.get(entry.getKey());
		}

		return sumRecentLeaps;
	}

	private Integer[] calculateRefactoringValues(
			Map<Integer, ArrayList<Commit>> intervalsCommitsMap,
			HashMap<Commit, Integer> commitsAndTheirChanges) {

		Integer[] refactoringValues = new Integer[2];
		Integer deletedRefactoringLines = 0;
		Integer numberOfSubsequentRefactoring = 0;

		for (int timeInterval = 0; timeInterval < maximumTimeInterval
				- MEDIUM_TIMESPAN; ++timeInterval) {
			for (int nextTimeInterval = timeInterval
					+ 1; nextTimeInterval <= timeInterval
							+ MEDIUM_TIMESPAN; ++nextTimeInterval) {
				for (final Commit commit : intervalsCommitsMap
						.get(nextTimeInterval)) {
					if (commitsAndTheirChanges.get(commit) < MIN_REFINE_LINES) {
						deletedRefactoringLines += commitsAndTheirChanges
								.get(commit);
						++numberOfSubsequentRefactoring;
					}
				}
			}
		}

		refactoringValues[0] = deletedRefactoringLines;
		refactoringValues[1] = numberOfSubsequentRefactoring;

		return refactoringValues;
	}

	private Integer getMethodGrowthInInterval(
			Map<Integer, ArrayList<Commit>> intervalsCommitsMap,
			int timeInterval, HashMap<Commit, Integer> commitsAndTheirChanges) {

		Integer methodGrowthInInterval = 0;

		for (final Commit commit : intervalsCommitsMap.get(timeInterval)) {
			methodGrowthInInterval += commitsAndTheirChanges.get(commit);
		}

		return methodGrowthInInterval;
	}

	/**
	 * Calculates the overall Supernova severity points of a method.
	 * 
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return An Integer that represents the Supernova severity of the given
	 *         method.
	 */
	public Integer getSupernovaSeverity(
			MethodChangesInformation methodChangesInformation) {

		final ArrayList<Commit> commits = methodChangesInformation.getCommits();
		final Map<String, Object> supernovaCriterionValues = getSupernovaCriterionValues(
				methodChangesInformation);
		final Integer sumOfAllLeaps = (Integer) supernovaCriterionValues
				.get("sumOfAllLeaps");
		final Integer sumRecentLeaps = (Integer) supernovaCriterionValues
				.get("sumRecentLeaps");
		final Double averageSubsequentCommits = (Double) supernovaCriterionValues
				.get("averageSubsequentCommits");

		return countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps,
				averageSubsequentCommits,
				methodChangesInformation.getActualSize(),
				commits.get(commits.size() - 1));
	}

	/**
	 * To detect Supernova methods, we divide its lifetime in time intervals of
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.SHORT_TIMESPAN
	 * SHORT_TIMESPAN}; then, for each time interval we compute the growth of
	 * method's size during that period. If there is at least one time interval
	 * during which the file size has grown significantly, i.e. with at least
	 * {@link edu.lavinia.inspectory.am.metrics.MethodMetrics.MAJOR_SIZE_CHANGE
	 * MAJOR_SIZE_CHANGE} lines the files is classified as Supernova.
	 * 
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return {@code True} if the method is Supernova, {@code false} otherwise.
	 */
	public Boolean isSupernova(
			final MethodChangesInformation methodChangesInformation) {
		return (Boolean) getSupernovaCriterionValues(methodChangesInformation)
				.get("isSupernova");
	}

	public Integer getMaximumTimeInterval() {
		return maximumTimeInterval;
	}

	public Integer getLeapsSizePoints() {
		return leapsSizePoints;
	}

	public Integer getRecentLeapsSizePoints() {
		return recentLeapsSizePoints;
	}

	public Integer getSubsequentRefactoringPoints() {
		return subsequentRefactoringPoints;
	}

	public Integer getMethodSizePoints() {
		return methodSizePoints;
	}

	public Integer getActivityStatePoints() {
		return activityStatePoints;
	}

}
