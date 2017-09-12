package org.lavinia.metrics;

import java.util.ArrayList;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class SupernovaMetric extends MethodMetrics {
	public Integer getLeapsSizePoints(Integer sumOfAllLeaps) {
		if (sumOfAllLeaps >= 4 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (sumOfAllLeaps >= 2 * MAJOR_SIZE_CHANGE && sumOfAllLeaps < 4 * MAJOR_SIZE_CHANGE) {
			return 1;
		}
		return 0;
	}

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

	public Integer getSubsequentRefactoringPoints(Integer averageSubsequentCommits) {
		if (averageSubsequentCommits >= 0 * MAJOR_SIZE_CHANGE && averageSubsequentCommits < 0.5 * MAJOR_SIZE_CHANGE) {
			return 2;
		} else if (averageSubsequentCommits >= 0.5 * MAJOR_SIZE_CHANGE
				&& averageSubsequentCommits < 1 * MAJOR_SIZE_CHANGE) {
			return 1;
		}
		return 0;
	}

	public Integer getFileSizePoints(Integer fileSize) {
		if (fileSize >= EXTREMELY_LARGE_FILE) {
			return 1;
		}
		return 0;
	}

	public Integer getActiveFilePoints(Commit commit) {
		if (getDifferenceInDays(commit.getDate(), now) <= MEDIUM_TIMESPAN) {
			return 1;
		}
		return 0;
	}

	public Integer countSupernovaSeverityPoints(Integer sumOfAllLeaps, Integer sumRecentLeaps,
			Integer averageSubsequentCommits, Integer fileSize, Commit commit) {
		return 1 + getLeapsSizePoints(sumOfAllLeaps) + getRecentLeapsSizePoints(sumRecentLeaps)
				+ getSubsequentRefactoringPoints(averageSubsequentCommits) + getFileSizePoints(fileSize)
				+ getActiveFilePoints(commit);
	}

	public Integer getSupernovaSeverity(ArrayList<Commit> commits, ArrayList<Integer> changesList,
			Integer actualFileSize) {
		Integer sumOfAllLeaps = 0;
		Integer sumRecentLeaps = 0;
		Integer deletedRefactoringLines = 0;
		Integer countLeaps = 0;
		Integer averageSubsequentCommits = 0;
		for (int i = 0; i < commits.size(); ++i) {
			if (getDifferenceInDays(commits.get(i).getDate(), now) <= MEDIUM_TIMESPAN) {
				sumRecentLeaps += changesList.get(i);
			}
			for (int j = i + 1; j < commits.size() - 1; ++j) {
				Long diffDays = getDifferenceInDays(commits.get(i).getDate(), commits.get(j).getDate());
				if (diffDays <= SHORT_TIMESPAN) {
					sumOfAllLeaps += changesList.get(j);
					if (changesList.get(i).equals("refactor")) {
						deletedRefactoringLines += changesList.get(i);
					}
				} else if (diffDays <= MEDIUM_TIMESPAN) {
					if (changesList.get(i).equals("refactor")) {
						deletedRefactoringLines += changesList.get(i);
					}
				} else {
					++countLeaps;
					break;
				}
			}
		}
		if (countLeaps > 0) {
			averageSubsequentCommits = deletedRefactoringLines / countLeaps;
		}
		return countSupernovaSeverityPoints(sumOfAllLeaps, sumRecentLeaps, averageSubsequentCommits, actualFileSize,
				commits.get(commits.size() - 1));
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
		ArrayList<Commit> commits = csvData.getCommits();
		ArrayList<Integer> changesList = csvData.getChangesList();
		for (int i = 1; i < commits.size(); ++i) {
			if (getDifferenceInDays(commits.get(0).getDate(), commits.get(i).getDate()) >= MEDIUM_TIMESPAN) {
				Integer sum = changesList.get(i);
				for (int j = i + 1; j < commits.size() - 1; ++j) {
					Long diffDays = getDifferenceInDays(commits.get(i).getDate(), commits.get(j).getDate());
					if (diffDays <= SHORT_TIMESPAN) {
						sum += changesList.get(j);
						if (sum >= MAJOR_SIZE_CHANGE) {
							return true;
						}
					} else {
						break;
					}
				}
			}
		}
		return false;
	}
}
