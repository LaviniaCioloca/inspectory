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
package org.lavinia.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class SupernovaMetric extends MethodMetrics {
	private Integer maximumTimeInterval = 0;

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
		return 1 + getLeapsSizePoints(sumOfAllLeaps) + getRecentLeapsSizePoints(sumRecentLeaps)
				+ getSubsequentRefactoringPoints(averageSubsequentCommits) + getMethodSizePoints(fileSize)
				+ getActiveMethodPoints(commit);
	}

	/**
	 * @param csvData
	 * @return
	 */
	/*
	 * public Map<String, Object> getSupernovaCriterionValues(CSVData csvData) {
	 * Map<String, Object> supernovaCriterionValues = new HashMap<>();
	 * supernovaCriterionValues.put("isSupernova", false); ArrayList<Commit>
	 * commits = csvData.getCommits(); ArrayList<Integer> changesList =
	 * csvData.getChangesList(); ArrayList<String> commitTypes =
	 * getCommitsTypes(changesList); Integer methodGrowth = 0; boolean
	 * commitOlderThanMediumTimespan = false; Integer sumOfAllLeaps = 0; Integer
	 * sumRecentLeaps = 0; Integer deletedRefactoringLines = 0; Integer
	 * countLeaps = 0; Double averageSubsequentCommits = 0.0; for (int i = 1; i
	 * < commits.size(); ++i) { if
	 * (getDifferenceInDays(commits.get(commits.size() - 1).getDate(),
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
	 * @return
	 */
	public ArrayList<Commit> getCommitsAfterMediumTimespan(HashMap<Commit, Integer> commitsIntoTimeFrames) {
		Iterator<Map.Entry<Commit, Integer>> entries = commitsIntoTimeFrames.entrySet().iterator();
		ArrayList<Commit> commitsAfterMediumTimespan = new ArrayList<>();
		while (entries.hasNext()) {
			Map.Entry<Commit, Integer> currentEntry = entries.next();
			if (currentEntry.getValue() > MEDIUM_TIMESPAN_TF) {
				commitsAfterMediumTimespan.add(currentEntry.getKey());
			}
		}
		return commitsAfterMediumTimespan;
	}

	/**
	 * @param commits
	 * @return
	 */
	public HashMap<Commit, Integer> splitCommitsIntoTimeIntervals(ArrayList<Commit> commits) {
		HashMap<Commit, Integer> commitsIntoTimeIntervals = new HashMap<>();
		Integer currentTimeInterval = 0;
		commitsIntoTimeIntervals.put(commits.get(0), currentTimeInterval);
		for (int i = 1; i < commits.size(); ++i) {
			if (getDifferenceInDays(commits.get(i - 1).getDate(), commits.get(i).getDate()) > TIME_FRAME) {
				++currentTimeInterval;
			}
			commitsIntoTimeIntervals.put(commits.get(i), currentTimeInterval);
		}
		maximumTimeInterval = currentTimeInterval;
		return commitsIntoTimeIntervals;
	}

	/**
	 * @param commitsAfterMediumTimespan
	 * @return
	 */
	public ArrayList<Commit> sortAllCommitsAfterMediumTimespan(ArrayList<Commit> commitsAfterMediumTimespan) {
		Collections.sort(commitsAfterMediumTimespan, new Comparator<Commit>() {
			@Override
			public int compare(Commit commit1, Commit commit2) {
				return commit1.getDate().compareTo(commit2.getDate());
			}
		});
		// Collections.sort(commitsAfterMediumTimespan, (commit1, commit2) ->
		// commit1.getDate().compareTo(commit2.getDate()));
		return commitsAfterMediumTimespan;
	}

	/**
	 * @param csvData
	 * @return
	 */
	public HashMap<Commit, Integer> divideLifetimeInIntervals(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		HashMap<Commit, Integer> commitsIntoTimeFrames = splitCommitsIntoTimeFrames(commits);
		ArrayList<Commit> commitsAfterMediumTimespan = getCommitsAfterMediumTimespan(commitsIntoTimeFrames);
		commitsAfterMediumTimespan = sortAllCommitsAfterMediumTimespan(commitsAfterMediumTimespan);
		HashMap<Commit, Integer> lifetimeIntoIntervals = new HashMap<>();
		lifetimeIntoIntervals = splitCommitsIntoTimeIntervals(commitsAfterMediumTimespan);
		return lifetimeIntoIntervals;
	}

	/**
	 * @param csvData
	 * @return
	 */
	public Map<String, Object> getSupernovaCriterionValues(CSVData csvData) {
		Map<String, Object> supernovaCriterionValues = new HashMap<>();
		supernovaCriterionValues.put("isSupernova", false);
		ArrayList<Commit> commits = csvData.getCommits();
		ArrayList<Integer> changesList = csvData.getChangesList();
		ArrayList<String> commitTypes = getCommitsTypes(changesList);
		Integer methodGrowth = 0;
		boolean commitOlderThanMediumTimespan = false;
		Integer sumOfAllLeaps = 0;
		Integer sumRecentLeaps = 0;
		Integer deletedRefactoringLines = 0;
		Integer countLeaps = 0;
		Double averageSubsequentCommits = 0.0;
		if (csvData.getActualSize() >= 0) {
			HashMap<Commit, Integer> commitsIntoTimeFrames = splitCommitsIntoTimeFrames(commits);
			for (HashMap.Entry<Commit, Integer> entry : commitsIntoTimeFrames.entrySet()) {
				if (entry.getValue() > MEDIUM_TIMESPAN_TF) {

				}
				System.out.println(entry.getKey() + "/" + entry.getValue());
			}
			for (int i = 1; i < commits.size(); ++i) {
				if (getDifferenceInDays(commits.get(commits.size() - 1).getDate(),
						commits.get(i).getDate()) <= MEDIUM_TIMESPAN) {
					sumRecentLeaps += changesList.get(i);
				}
				if (getDifferenceInDays(commits.get(0).getDate(), commits.get(i).getDate()) >= MEDIUM_TIMESPAN) {
					methodGrowth = changesList.get(i);
					commitOlderThanMediumTimespan = true;
				}
				for (int j = i + 1; j < commits.size() - 1; ++j) {
					Long diffDays = getDifferenceInDays(commits.get(i).getDate(), commits.get(j).getDate());
					if (diffDays <= SHORT_TIMESPAN) {
						sumOfAllLeaps += changesList.get(j);
						if (commitTypes.get(i).equals("refactor")) {
							deletedRefactoringLines += changesList.get(i);
						}
						if (commitOlderThanMediumTimespan) {
							methodGrowth += changesList.get(j);
							if (methodGrowth >= MAJOR_SIZE_CHANGE
									&& !supernovaCriterionValues.get("isSupernova").equals(true)) {
								supernovaCriterionValues.put("isSupernova", true);
							}
						}
					} else if (diffDays <= MEDIUM_TIMESPAN) {
						if (commitTypes.get(i).equals("refactor")) {
							deletedRefactoringLines += changesList.get(i);
						}
					} else {
						++countLeaps;
						break;
					}
				}
				commitOlderThanMediumTimespan = false;
			}
		}
		if (countLeaps > 0) {
			averageSubsequentCommits = (double) -deletedRefactoringLines / (double) countLeaps;
		}
		supernovaCriterionValues.put("sumOfAllLeaps", sumOfAllLeaps);
		supernovaCriterionValues.put("sumRecentLeaps", sumRecentLeaps);
		supernovaCriterionValues.put("averageSubsequentCommits", averageSubsequentCommits);
		/*
		 * for (Map.Entry<String, Object> entry :
		 * supernovaCriterionValues.entrySet()) {
		 * System.out.println(entry.getKey() + " = " + entry.getValue()); }
		 */
		return supernovaCriterionValues;
	}

	/**
	 * Calculates the overall Supernova severity points of a method.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return An Integer that represents the Supernova severity of the given
	 *         method.
	 */
	public Integer getSupernovaSeverity(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		Map<String, Object> supernovaCriterionValues = getSupernovaCriterionValues(csvData);
		Integer sumOfAllLeaps = (Integer) supernovaCriterionValues.get("sumOfAllLeaps");
		Integer sumRecentLeaps = (Integer) supernovaCriterionValues.get("sumRecentLeaps");
		Double averageSubsequentCommits = (Double) supernovaCriterionValues.get("averageSubsequentCommits");
		/*
		 * System.out.println("\nSupernovaMetric - Method: " +
		 * csvData.getFileName() + csvData.getMethodName());
		 * System.out.println("Parameters called: " + "sumOfAllLeaps: " +
		 * sumOfAllLeaps + "; sumRecentLeaps: " + sumRecentLeaps +
		 * "; averageSubComm: " + averageSubsequentCommits + "; countLeaps" +
		 * countLeaps); System.out.println("Count: " +
		 * countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps,
		 * averageSubsequentCommits, csvData.getActualSize(),
		 * commits.get(commits.size() - 1)));
		 */
		return countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps, averageSubsequentCommits,
				csvData.getActualSize(), commits.get(commits.size() - 1));
	}

	/**
	 * To detect Supernova methods, we divide its lifetime in time intervals of
	 * SHORT_TIMESPAN; then, for each time interval we compute the growth of
	 * method's size during that period. If there is at least one time interval
	 * during which the file size has grown significantly, i.e. with at least
	 * MAJOR_SIZE_CHANGE lines the files is classified as Supernova.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return True if the method is Supernova, false otherwise
	 */
	public Boolean isSupernova(CSVData csvData) {
		return (Boolean) getSupernovaCriterionValues(csvData).get("isSupernova");
	}

	public Integer getMaximumTimeInterval() {
		return maximumTimeInterval;
	}

}
