package org.lavinia.metrics;

import java.util.ArrayList;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class PulsarMetric extends MethodMetrics {
	private Boolean isPulsar = false;
	private Integer pulsarSeverity = 1;

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
	public Integer getFileSizePoints(Integer fileSize) {
		if (fileSize >= EXTREMELY_LARGE_FILE) {
			return 2;
		} else if (fileSize >= VERY_LARGE_FILE) {
			return 1;
		}
		return 0;
	}

	public Integer countPulsarSeverityPoints(Integer countRecentPulsarCycles, Double averageSizeIncrease,
			Integer fileSize, Commit commit) {
		return 1 + getRecentCyclesPoints(countRecentPulsarCycles) + getAverageSizeIncrease(averageSizeIncrease)
				+ getFileSizePoints(fileSize) + getActiveFilePoints(commit);
	}

	private void checkIfPulsar(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		ArrayList<Integer> changesList = csvData.getChangesList();
		ArrayList<String> commitsTypes = getCommitsTypes(changesList);
		Integer countPulsarCycles = 0;
		Integer methodGrowth = 0;
		Integer countActiveChanges = 0;
		if (csvData.getActualSize() >= SIGNIFICANT_FILESIZE) {
			for (int i = commits.size() - 1; i >= 0; --i) {
				if (getDifferenceInDays(commits.get(i).getDate(), now) <= LONG_TIMESPAN) {
					++countActiveChanges;
				} else {
					break;
				}
			}
			if (countActiveChanges >= ACTIVELY_CHANGED) {
				for (int i = 1; i < commitsTypes.size() - 1; ++i) {
					if (commitsTypes.get(i).equals("refactor") && commitsTypes.get(i + 1).equals("develop")) {
						++countPulsarCycles;
					}
					if (commitsTypes.get(i).equals("refine")) {
						methodGrowth += changesList.get(i);
						if (methodGrowth >= SMALL_SIZE_CHANGE) {
							++countPulsarCycles;
						}
					} else {
						methodGrowth = 0;
					}
					if (countPulsarCycles >= MANY_PULSAR_CYCLES) {
						setIsPulsar(true);
					}
				}
			} else {
				setIsPulsar(false);
			}
		}
	}

	/**
	 * To be a Pulsar, a method must: have at least SIGNIFICANT_FILESIZE lines,
	 * and have been actively changed over the last LONG_TIMESPAN. We count the
	 * number of refactor commits that are preceded by at least one develop
	 * commit, or an uninterrupted sequence of refine commits that cumulated
	 * produce a file growth that is larger than SMALL_SIZE_CHANGE lines. A
	 * Pulsar needs to have at least MANY_PULSAR_CYCLES.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return True if the method is Pulsar, false otherwise
	 */
	public void measurePulsarCriterions(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		ArrayList<Integer> changesList = csvData.getChangesList();
		ArrayList<String> commitsTypes = getCommitsTypes(changesList);
		Double averageSizeIncrease = 0.0;
		Integer countRecentPulsarCycles = 0;
		Integer methodGrowth = 0;
		int countActiveChanges = 0;
		if (csvData.getActualSize() >= SIGNIFICANT_FILESIZE) {
			for (int i = commits.size() - 1; i >= 0; --i) {
				if (getDifferenceInDays(commits.get(i).getDate(), now) <= LONG_TIMESPAN) {
					++countActiveChanges;
				} else {
					break;
				}
			}
			if (countActiveChanges >= ACTIVELY_CHANGED) {
				for (int i = 1; i < commitsTypes.size() - 1; ++i) {
					if (getDifferenceInDays(commits.get(i).getDate(), now) <= MEDIUM_TIMESPAN) {
						if (commitsTypes.get(i).equals("refactor") && commitsTypes.get(i + 1).equals("develop")) {
							++countRecentPulsarCycles;
						}
						if (commitsTypes.get(i).equals("refine")) {
							methodGrowth += changesList.get(i);
							if (methodGrowth >= SMALL_SIZE_CHANGE) {
								++countRecentPulsarCycles;
							}
						} else {
							methodGrowth = 0;
						}
					}
				}
			}
		}
		countPulsarSeverityPoints(countRecentPulsarCycles, averageSizeIncrease, csvData.getActualSize(),
				commits.get(commits.size() - 1));
	}

	/**
	 * To be a Pulsar, a method must: have at least SIGNIFICANT_FILESIZE lines,
	 * and have been actively changed over the last LONG_TIMESPAN. We count the
	 * number of refactor commits that are preceded by at least one develop
	 * commit, or an uninterrupted sequence of refine commits that cumulated
	 * produce a file growth that is larger than SMALL_SIZE_CHANGE lines. A
	 * Pulsar needs to have at least MANY_PULSAR_CYCLES.
	 * 
	 * @param csvData
	 *            The information of the current method
	 * @return True if the method is Pulsar, false otherwise
	 */
	public Boolean isPulsar(CSVData csvData) {
		if (csvData.getActualSize() >= SIGNIFICANT_FILESIZE) {
			ArrayList<Commit> commits = csvData.getCommits();
			Integer count = 0;
			for (int i = commits.size() - 1; i >= 0; --i) {
				if (getDifferenceInDays(commits.get(i).getDate(), now) <= LONG_TIMESPAN) {
					++count;
				} else {
					break;
				}
			}
			if (count >= ACTIVELY_CHANGED) {
				ArrayList<Integer> changesList = csvData.getChangesList();
				ArrayList<String> commitsTypes = getCommitsTypes(changesList);
				Integer countPulsarCycles = 0;
				Integer methodGrowth = 0;
				for (int i = 1; i < commitsTypes.size() - 1; ++i) {
					if (commitsTypes.get(i).equals("refactor") && commitsTypes.get(i + 1).equals("develop")) {
						++countPulsarCycles;
					}
					if (commitsTypes.get(i).equals("refine")) {
						methodGrowth += changesList.get(i);
						if (methodGrowth >= SMALL_SIZE_CHANGE) {
							++countPulsarCycles;
						}
					} else {
						methodGrowth = 0;
					}
					if (countPulsarCycles >= MANY_PULSAR_CYCLES) {
						return true;
					}
				}
			} else {
				return false;
			}
		}
		return false;
	}

	public Boolean getIsPulsar() {
		return isPulsar;
	}

	public void setIsPulsar(Boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Integer getPulsarSeverity() {
		return pulsarSeverity;
	}

	public void setPulsarSeverity(Integer pulsarSeverity) {
		this.pulsarSeverity = pulsarSeverity;
	}

}
