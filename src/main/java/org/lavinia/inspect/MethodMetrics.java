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
package org.lavinia.inspect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class MethodMetrics {
	private final static Integer MIN_REFINE_LINES = -5; // lines
	private final static Integer MAX_REFINE_LINES = 5; // lines
	private final static Integer SIGNIFICANT_FILESIZE = 100; // lines
	private final static Integer TIME_FRAME = 14; // days
	private final static Integer SHORT_TIMESPAN = 1 * TIME_FRAME;
	private final static Integer MEDIUM_TIMESPAN = 3 * TIME_FRAME;
	private final static Integer LONG_TIMESPAN = 6 * TIME_FRAME;
	private final static Integer MANY_PULSAR_CYCLES = 5; // commits
	private final static Integer SMALL_SIZE_CHANGE = 15; // lines
	private final static Integer MAJOR_SIZE_CHANGE = 1 * SIGNIFICANT_FILESIZE;
	private final static Integer ACTIVELY_CHANGED = 5; // times changed
	private static Date now = null;

	public MethodMetrics() {
		try {
			now = new SimpleDateFormat("yyyy/MM/dd").parse("2017/06/01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param start
	 * @param end
	 * @return A Long representing the difference in days between start and end
	 *         dates
	 */
	private Long getDifferenceInDays(Date start, Date end) {
		Long startTime = start.getTime();
		Long endTime = end.getTime();
		Long diffTime = endTime - startTime;
		return diffTime / (1000 * 60 * 60 * 24);
	}

	/**
	 * @param changesList
	 *            ArrayList with number of lines the method suffered during the
	 *            commits
	 * @return ArrayList of Strings divided by categories in:
	 *         refactor/refine/develop commits
	 */
	private ArrayList<String> getCommitsTypes(ArrayList<Integer> changesList) {
		ArrayList<String> commitsTypes = new ArrayList<String>();
		for (int i = 0; i < changesList.size(); ++i) {
			if (changesList.get(i) < MIN_REFINE_LINES) { // refactor
				commitsTypes.add("refactor");
			} else if (changesList.get(i) >= MIN_REFINE_LINES && changesList.get(i) <= MAX_REFINE_LINES) { // refine
				commitsTypes.add("refine");
			} else { // develop
				commitsTypes.add("develop");
			}
		}
		return commitsTypes;
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
				for (int i = 1; i <= commitsTypes.size() - 1; ++i) {
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
