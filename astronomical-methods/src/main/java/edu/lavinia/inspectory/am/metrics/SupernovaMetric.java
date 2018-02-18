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

public class SupernovaMetric extends MethodMetrics {
	private Integer maximumTimeInterval = 0;
	private Integer leapsSizePoints = 0;
	private Integer recentLeapsSizePoints = 0;
	private Integer subsequentRefactoringPoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	/**
	 * @param sumOfAllLeaps
	 * @return An Integer: 0 - 2 representing the points of leaps size.
	 */
	public Integer getLeapsSizePoints(Integer sumOfAllLeaps) {
		if (sumOfAllLeaps >= 4 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (sumOfAllLeaps >= 2 * MAJOR_SIZE_CHANGE && sumOfAllLeaps < 4 * MAJOR_SIZE_CHANGE) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param sumRecentLeaps
	 * @return An Integer: 0 - 3 representing the points of recent leaps size.
	 */
	public Integer getRecentLeapsSizePoints(Integer sumRecentLeaps) {
		if (sumRecentLeaps >= 4 * MAJOR_SIZE_CHANGE) {
			return 3;
		} else if (sumRecentLeaps >= 2.5 * MAJOR_SIZE_CHANGE && sumRecentLeaps < 4 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (sumRecentLeaps >= 1.5 * MAJOR_SIZE_CHANGE && sumRecentLeaps < 2.5 * MAJOR_SIZE_CHANGE) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param averageSubsequentCommits
	 * @return An Integer: 0 - 2 representing the points of Subsequent
	 *         Refactoring.
	 */
	public Integer getSubsequentRefactoringPoints(Double averageSubsequentCommits) {
		if (averageSubsequentCommits >= 0 * MAJOR_SIZE_CHANGE && averageSubsequentCommits < 0.5 * MAJOR_SIZE_CHANGE) {
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
	public Integer countSupernovaSeverityPoints(Integer sumOfAllLeaps, Integer sumRecentLeaps,
			Double averageSubsequentCommits, Integer fileSize, Commit commit) {
		leapsSizePoints = getLeapsSizePoints(sumOfAllLeaps);
		recentLeapsSizePoints = getRecentLeapsSizePoints(sumRecentLeaps);
		subsequentRefactoringPoints = getSubsequentRefactoringPoints(averageSubsequentCommits);
		methodSizePoints = getMethodSizePoints(fileSize);
		activityStatePoints = getActiveMethodPoints(commit);
		return 1 + leapsSizePoints + recentLeapsSizePoints + subsequentRefactoringPoints + methodSizePoints
				+ activityStatePoints;
	}

	/**
	 * @param methodInformation
	 * @return
	 */
	/*
	 * public Map<String, Object> getSupernovaCriterionValues(CSVData
	 * methodInformation) { Map<String, Object> supernovaCriterionValues = new
	 * HashMap<>(); supernovaCriterionValues.put("isSupernova", false);
	 * ArrayList<Commit> commits = methodInformation.getCommits();
	 * ArrayList<Integer> changesList = methodInformation.getChangesList();
	 * ArrayList<String> commitTypes = getCommitsTypes(changesList); Integer
	 * methodGrowth = 0; boolean commitOlderThanMediumTimespan = false; Integer
	 * sumOfAllLeaps = 0; Integer sumRecentLeaps = 0; Integer
	 * deletedRefactoringLines = 0; Integer countLeaps = 0; Double
	 * averageSubsequentCommits = 0.0; for (int i = 1; i < commits.size(); ++i)
	 * { if (getDifferenceInDays(commits.get(commits.size() - 1).getDate(),
	 * commits.get(i).getDate()) <= MEDIUM_TIMESPAN) { sumRecentLeaps +=
	 * changesList.get(i); } if (getDifferenceInDays(commits.get(0).getDate(),
	 * commits.get(i).getDate()) >= MEDIUM_TIMESPAN) { methodGrowth =
	 * changesList.get(i); commitOlderThanMediumTimespan = true; } for (int j =
	 * i + 1; j < commits.size() - 1; ++j) { Long diffDays =
	 * getDifferenceInDays(commits.get(i).getDate(), commits.get(j).getDate());
	 * if (diffDays <= SHORT_TIMESPAN) { sumOfAllLeaps += changesList.get(j); if
	 * (commitTypes.get(i).equals("refactor")) { deletedRefactoringLines +=
	 * changesList.get(i); } if (commitOlderThanMediumTimespan) { methodGrowth
	 * += changesList.get(j); if (methodGrowth >= MAJOR_SIZE_CHANGE &&
	 * !supernovaCriterionValues.get("isSupernova").equals(true)) {
	 * supernovaCriterionValues.put("isSupernova", true); } } } else if
	 * (diffDays <= MEDIUM_TIMESPAN) { if
	 * (commitTypes.get(i).equals("refactor")) { deletedRefactoringLines +=
	 * changesList.get(i); } } else { ++countLeaps; break; } }
	 * commitOlderThanMediumTimespan = false; } if (countLeaps > 0) {
	 * averageSubsequentCommits = (double) -deletedRefactoringLines / (double)
	 * countLeaps; } supernovaCriterionValues.put("sumOfAllLeaps",
	 * sumOfAllLeaps); supernovaCriterionValues.put("sumRecentLeaps",
	 * sumRecentLeaps); supernovaCriterionValues.put("averageSubsequentCommits",
	 * averageSubsequentCommits);
	 */
	/*
	 * for (Map.Entry<String, Object> entry :
	 * supernovaCriterionValues.entrySet()) { System.out.println(entry.getKey()
	 * + " = " + entry.getValue()); }
	 */
	/*
	 * return supernovaCriterionValues; }
	 */

	/**
	 * @param commitsIntoTimeFrames
	 * @return ArrayList with the commits encountered after the
	 *         {@code MEDIUM_TIMESPAN}.
	 */
	public ArrayList<Commit> getCommitsAfterMediumTimespan(HashMap<Commit, Integer> commitsIntoTimeFrames) {
		Iterator<Map.Entry<Commit, Integer>> entries = commitsIntoTimeFrames.entrySet().iterator();
		ArrayList<Commit> commitsAfterMediumTimespan = new ArrayList<>();
		while (entries.hasNext()) {
			Map.Entry<Commit, Integer> currentEntry = entries.next();
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
	public HashMap<Commit, Integer> splitCommitsIntoTimeIntervals(ArrayList<Commit> commits) {
		HashMap<Commit, Integer> commitsIntoTimeIntervals = new HashMap<>();
		Integer currentTimeInterval = 0;
		commitsIntoTimeIntervals.put(commits.get(0), currentTimeInterval);
		for (int i = 1; i < commits.size(); ++i) {
			if (getDifferenceInDays(commits.get(i - 1).getDate(), commits.get(i).getDate()) > SHORT_TIMESPAN
					* TIME_FRAME) {
				++currentTimeInterval;
			}
			commitsIntoTimeIntervals.put(commits.get(i), currentTimeInterval);
		}
		maximumTimeInterval = currentTimeInterval;
		return commitsIntoTimeIntervals;
	}

	/**
	 * @param commitsAfterMediumTimespan
	 * @return Sorted ArrayList of commits after the {@code MEDIUM_TIMESPAN}.
	 */
	public ArrayList<Commit> sortAllCommitsAfterMediumTimespan(ArrayList<Commit> commitsAfterMediumTimespan) {
		Collections.sort(commitsAfterMediumTimespan, new Comparator<Commit>() {
			@Override
			public int compare(Commit commit1, Commit commit2) {
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
	public HashMap<Commit, Integer> divideLifetimeInIntervals(MethodChangesInformation methodChangesInformation) {
		ArrayList<Commit> commits = methodChangesInformation.getCommits();
		HashMap<Commit, Integer> commitsIntoTimeFrames = splitCommitsIntoTimeFrames(commits);
		ArrayList<Commit> commitsAfterMediumTimespan = getCommitsAfterMediumTimespan(commitsIntoTimeFrames);
		commitsAfterMediumTimespan = sortAllCommitsAfterMediumTimespan(commitsAfterMediumTimespan);
		HashMap<Commit, Integer> lifetimeIntoIntervals = new HashMap<>();
		if (commitsAfterMediumTimespan.size() > 0) {
			lifetimeIntoIntervals = splitCommitsIntoTimeIntervals(commitsAfterMediumTimespan);
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
	public HashMap<Commit, Integer> getCommitsAndChangesMap(ArrayList<Commit> commits, ArrayList<Integer> changesList) {
		HashMap<Commit, Integer> commitsAndTheirChanges = new HashMap<>();
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
	public TreeMap<Integer, ArrayList<Commit>> getIntervalsCommitsMap(
			HashMap<Commit, Integer> commitsIntoTimeIntervals) {
		TreeMap<Integer, ArrayList<Commit>> intervalsCommitsList = new TreeMap<>();
		for (HashMap.Entry<Commit, Integer> entry : commitsIntoTimeIntervals.entrySet()) {
			if (intervalsCommitsList.get(entry.getValue()) != null) {
				intervalsCommitsList.get(entry.getValue()).add(entry.getKey());
			} else {
				ArrayList<Commit> commits = new ArrayList<>();
				commits.add(entry.getKey());
				intervalsCommitsList.put(entry.getValue(), commits);
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
	public Map<String, Object> getSupernovaCriterionValues(MethodChangesInformation methodChangesInformation) {
		Map<String, Object> supernovaCriterionValues = new HashMap<>();
		supernovaCriterionValues.put("isSupernova", false);
		ArrayList<Commit> commits = methodChangesInformation.getCommits();
		ArrayList<Integer> changesList = methodChangesInformation.getChangesList();
		Integer sumOfAllLeaps = 0;
		Integer sumRecentLeaps = 0;
		Integer deletedRefactoringLines = 0;
		Integer numberOfSubsequentRefactoring = 0;
		Double averageSubsequentCommits = 0.0;
		HashMap<Commit, Integer> commitsIntoTimeIntervals = divideLifetimeInIntervals(methodChangesInformation);
		if (methodChangesInformation.getActualSize() >= 0 && maximumTimeInterval > 0) {
			HashMap<Commit, Integer> commitsAndTheirChanges = getCommitsAndChangesMap(commits, changesList);
			for (HashMap.Entry<Commit, Integer> entry : commitsIntoTimeIntervals.entrySet()) {
				sumOfAllLeaps += commitsAndTheirChanges.get(entry.getKey());
				if (getDifferenceInDays(entry.getKey().getDate(),
						commits.get(commits.size() - 1).getDate()) <= MEDIUM_TIMESPAN * TIME_FRAME) {
					sumRecentLeaps += commitsAndTheirChanges.get(entry.getKey());
				}
			}
			TreeMap<Integer, ArrayList<Commit>> intervalsCommitsMap = getIntervalsCommitsMap(commitsIntoTimeIntervals);
			for (int timeInterval = 0; timeInterval < maximumTimeInterval - MEDIUM_TIMESPAN; ++timeInterval) {
				for (int nextTimeInterval = timeInterval + 1; nextTimeInterval <= timeInterval
						+ MEDIUM_TIMESPAN; ++nextTimeInterval) {
					for (Commit commit : intervalsCommitsMap.get(nextTimeInterval)) {
						if (commitsAndTheirChanges.get(commit) < MIN_REFINE_LINES) {
							deletedRefactoringLines += commitsAndTheirChanges.get(commit);
							++numberOfSubsequentRefactoring;
						}
					}
				}
			}
			Integer methodGrowthInInterval = 0;
			for (int timeInterval = 0; timeInterval <= maximumTimeInterval; ++timeInterval) {
				methodGrowthInInterval = 0;
				for (Commit commit : intervalsCommitsMap.get(timeInterval)) {
					methodGrowthInInterval += commitsAndTheirChanges.get(commit);
				}
				if (methodGrowthInInterval >= MAJOR_SIZE_CHANGE
						&& !supernovaCriterionValues.get("isSupernova").equals(true)) {
					supernovaCriterionValues.put("isSupernova", true);
					break;
				}
			}
		}
		if (numberOfSubsequentRefactoring > 0) {
			averageSubsequentCommits = (double) -deletedRefactoringLines / (double) numberOfSubsequentRefactoring;
		}
		supernovaCriterionValues.put("sumOfAllLeaps", sumOfAllLeaps);
		supernovaCriterionValues.put("sumRecentLeaps", sumRecentLeaps);
		supernovaCriterionValues.put("averageSubsequentCommits", averageSubsequentCommits);
		return supernovaCriterionValues;
	}

	/**
	 * Calculates the overall Supernova severity points of a method.
	 * 
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return An Integer that represents the Supernova severity of the given
	 *         method.
	 */
	public Integer getSupernovaSeverity(MethodChangesInformation methodChangesInformation) {
		ArrayList<Commit> commits = methodChangesInformation.getCommits();
		Map<String, Object> supernovaCriterionValues = getSupernovaCriterionValues(methodChangesInformation);
		Integer sumOfAllLeaps = (Integer) supernovaCriterionValues.get("sumOfAllLeaps");
		Integer sumRecentLeaps = (Integer) supernovaCriterionValues.get("sumRecentLeaps");
		Double averageSubsequentCommits = (Double) supernovaCriterionValues.get("averageSubsequentCommits");
		/*
		 * System.out.println("\nSupernovaMetric - Method: " +
		 * methodInformation.getFileName() + methodInformation.getMethodName());
		 * System.out.println("Parameters called: " + "sumOfAllLeaps: " +
		 * sumOfAllLeaps + "; sumRecentLeaps: " + sumRecentLeaps +
		 * "; averageSubComm: " + averageSubsequentCommits + "; countLeaps" +
		 * countLeaps); System.out.println("Count: " +
		 * countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps,
		 * averageSubsequentCommits, methodInformation.getActualSize(),
		 * commits.get(commits.size() - 1)));
		 */
		return countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps, averageSubsequentCommits,
				methodChangesInformation.getActualSize(), commits.get(commits.size() - 1));
	}

	/**
	 * To detect Supernova methods, we divide its lifetime in time intervals of
	 * SHORT_TIMESPAN; then, for each time interval we compute the growth of
	 * method's size during that period. If there is at least one time interval
	 * during which the file size has grown significantly, i.e. with at least
	 * MAJOR_SIZE_CHANGE lines the files is classified as Supernova.
	 * 
	 * @param methodChangesInformation
	 *            The information of the current method
	 * @return True if the method is Supernova, false otherwise.
	 */
	public Boolean isSupernova(MethodChangesInformation methodChangesInformation) {
		return (Boolean) getSupernovaCriterionValues(methodChangesInformation).get("isSupernova");
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
