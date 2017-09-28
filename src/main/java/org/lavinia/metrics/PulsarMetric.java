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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class PulsarMetric extends MethodMetrics {

	public PulsarMetric(String dateNow) {
		super(dateNow);
	}
	
	public PulsarMetric(Date dateNow, ArrayList<Commit> allCommits) {
		super(dateNow, allCommits);
	}

	/**
	 * @param countRecentPulsarCycles
	 * @return An Integer: 0 - 3 representing the points of the recent Pulsar
	 *         cycles.
	 */
	public Integer getRecentCyclesPoints(Integer countRecentPulsarCycles) {
		if (countRecentPulsarCycles >= 6) {
			return 3;
		} else if (countRecentPulsarCycles >= 3 && countRecentPulsarCycles <= 5) {
			return 2;
		} else if (countRecentPulsarCycles >= 1 && countRecentPulsarCycles <= 2) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param averageSizeIncrease
	 * @return An Integer: 0 - 3 representing the points of the Pulsar method's
	 *         average size increase.
	 */
	public Integer getAverageSizeIncrease(Double averageSizeIncrease) {
		if (averageSizeIncrease >= 0.0 && averageSizeIncrease < (1.0 / 3.0) * MAJOR_SIZE_CHANGE) {
			return 3;
		} else if (averageSizeIncrease >= (1.0 / 3.0) * MAJOR_SIZE_CHANGE
				&& averageSizeIncrease < (2.0 / 3.0) * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (averageSizeIncrease >= (2.0 / 3.0) * MAJOR_SIZE_CHANGE && averageSizeIncrease < MAJOR_SIZE_CHANGE) {
			return 1;
		}
		return 0;
	}

	@Override
	public Integer getMethodSizePoints(Integer fileSize) {
		if (fileSize >= EXTREMELY_LARGE_METHOD) {
			return 2;
		} else if (fileSize >= VERY_LARGE_METHOD) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param countRecentPulsarCycles
	 * @param averageSizeIncrease
	 * @param fileSize
	 * @param commit
	 * @return An Integer representing the total points of Pulsar severity.
	 */
	public Integer countPulsarSeverityPoints(Integer countRecentPulsarCycles, Double averageSizeIncrease,
			Integer fileSize, Commit commit) {
		return 1 + getRecentCyclesPoints(countRecentPulsarCycles) + getAverageSizeIncrease(averageSizeIncrease)
				+ getMethodSizePoints(fileSize) + getActiveMethodPoints(commit);
	}

	/**
	 * If a method has been actively changed over the last LONG_TIMESPAN then it
	 * is an Actively Changed method.
	 * 
	 * @param csvData
	 * @return A Boolean: true if the method is actively changed.
	 */
	public Boolean isMethodActivelyChanged(CSVData csvData) {
		Integer countActiveChanges = 0;
		ArrayList<Commit> commits = csvData.getCommits();
		for (int i = commits.size() - 1; i >= 0; --i) {
			if (getDifferenceInDays(commits.get(i).getDate(), now) <= LONG_TIMESPAN) {
				++countActiveChanges;
			} else {
				break;
			}
		}
		return countActiveChanges >= ACTIVELY_CHANGED;
	}

	/**
	 * If a method has been actively changed over the last LONG_TIMESPAN
	 * time-frames then it is an Actively Changed method.
	 * 
	 * @param csvData
	 * @return A Boolean: true if the method is actively changed.
	 */
	public Boolean isMethodTimeFrameActivelyChanged(CSVData csvData) {
		Integer countActiveChanges = 0;
		ArrayList<Commit> commits = csvData.getCommits();
		for (int i = commits.size() - 1; i >= 0; --i) {
			if (getDifferenceInDays(commits.get(i).getDate(), now) <= LONG_TIMESPAN) {
				++countActiveChanges;
			} else {
				break;
			}
		}
		return countActiveChanges >= ACTIVELY_CHANGED;
	}

	/**
	 * @param countPulsarCycles
	 * @param sumOfSizeIncrease
	 * @return
	 */
	public Double calculateAverageSizeIncrease(Integer countPulsarCycles, Integer sumOfSizeIncrease) {
		Double averageSizeIncrease = 0.0;
		if (countPulsarCycles > 0) {
			averageSizeIncrease = (double) sumOfSizeIncrease / (double) countPulsarCycles;
		}
		return averageSizeIncrease;
	}

	/**
	 * Calculates the overall Pulsar severity points of a method.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return An Integer that represents the Pulsar severity of the given
	 *         method.
	 */
	public Integer getPulsarSeverity(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		Map<String, Object> pulsarCriterionValues = getPulsarCriterionValues(csvData);
		Integer countRecentPulsarCycles = (Integer) pulsarCriterionValues.get("countRecentPulsarCycles");
		Double averageSizeIncrease = (Double) pulsarCriterionValues.get("averageSizeIncrease");
		/*
		 * System.out.println("\nPulsarMetric - Method: " +
		 * csvData.getFileName() + csvData.getMethodName() + ": ");
		 * System.out.println("Method called with: " + "countPulsarCycles: " +
		 * countPulsarCycles + "; avg: " + averageSizeIncrease +
		 * "; last commit: " + commits.get(commits.size() - 1));
		 */
		return countPulsarSeverityPoints(countRecentPulsarCycles, averageSizeIncrease, csvData.getActualSize(),
				commits.get(commits.size() - 1));
	}

	/**
	 * A PulsarCycle is considered recent if it was detected very recently, i.e.
	 * in the last MEDIUM_TIMESPAN time-frames of the project.
	 * 
	 * @param commitDate
	 * @return An Integer: 1 if Pulsar cycle is recent and 0 if false.
	 */
	public Integer checkIfRecentPulsarCycle(Date commitDate) {
		if (getDifferenceInDays(commitDate, now) <= MEDIUM_TIMESPAN) {
			return 1;
		}
		return 0;
	}

	/**
	 * @param csvData
	 * @return A Map with the values for the following Pulsar criterias:
	 *         averageSizeIncrease; countPulsarCycles; countRecentPulsarCycles;
	 *         isPulsar.
	 */
	public Map<String, Object> getPulsarCriterionValues(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		Map<String, Object> pulsarCriterionValues = new HashMap<>();
		pulsarCriterionValues.put("isPulsar", false);
		Integer sumOfSizeIncrease = 0;
		Double averageSizeIncrease = 0.0;
		Integer countRecentPulsarCycles = 0;
		Integer countPulsarCycles = 0;
		Integer methodGrowth = 0;
		if (csvData.getActualSize() >= SIGNIFICANT_METHOD_SIZE) {
			if (isMethodActivelyChanged(csvData)) {
				ArrayList<Integer> changesList = csvData.getChangesList();
				ArrayList<String> commitsTypes = getCommitsTypes(changesList);
				for (int i = 0; i < commitsTypes.size() - 1; ++i) {
					if (commitsTypes.get(i).equals("refactor") && commitsTypes.get(i + 1).equals("develop")) {
						++countPulsarCycles;
						countRecentPulsarCycles += checkIfRecentPulsarCycle(commits.get(i).getDate());
					}
					if (commitsTypes.get(i).equals("refine")) {
						methodGrowth += changesList.get(i);
						if (methodGrowth >= SMALL_SIZE_CHANGE) {
							++countPulsarCycles;
							countRecentPulsarCycles += checkIfRecentPulsarCycle(commits.get(i).getDate());
						}
					} else {
						methodGrowth = 0;
					}
					if (commitsTypes.get(i + 1).equals("refine")) {
						sumOfSizeIncrease += changesList.get(i);
					}
					if (countPulsarCycles >= MANY_PULSAR_CYCLES
							&& !pulsarCriterionValues.get("isPulsar").equals(true)) {
						pulsarCriterionValues.put("isPulsar", true);
					}
				}
			}
		}
		averageSizeIncrease = calculateAverageSizeIncrease(countPulsarCycles, sumOfSizeIncrease);
		pulsarCriterionValues.put("averageSizeIncrease", averageSizeIncrease);
		pulsarCriterionValues.put("countPulsarCycles", countPulsarCycles);
		pulsarCriterionValues.put("countRecentPulsarCycles", countRecentPulsarCycles);
		/*
		 * for (Map.Entry<String, Object> entry :
		 * pulsarCriterionValues.entrySet()) { System.out.println(entry.getKey()
		 * + " = " + entry.getValue()); }
		 */
		return pulsarCriterionValues;
	}

	/**
	 * To be a Pulsar, a method must: have at least SIGNIFICANT_METHOD_SIZE
	 * lines, and have been actively changed over the last LONG_TIMESPAN. We
	 * count the number of refactor commits that are preceded by at least one
	 * develop commit, or an uninterrupted sequence of refine commits that
	 * cumulated produce a file growth that is larger than SMALL_SIZE_CHANGE
	 * lines. A Pulsar needs to have at least MANY_PULSAR_CYCLES.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return True if the method is Pulsar, false otherwise.
	 */
	public Boolean isPulsar(CSVData csvData) {
		return (Boolean) getPulsarCriterionValues(csvData).get("isPulsar");
	}
}
