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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.lavinia.beans.Commit;

public abstract class MethodMetrics {
	protected final static Integer MIN_REFINE_LINES = -3; // lines
	protected final static Integer MAX_REFINE_LINES = 3; // lines
	protected final static Integer SIGNIFICANT_METHOD_SIZE = 50; // lines
	protected final static Integer VERY_LARGE_METHOD = 2 * SIGNIFICANT_METHOD_SIZE;
	protected final static Integer EXTREMELY_LARGE_METHOD = 3 * SIGNIFICANT_METHOD_SIZE;
	protected final static Integer TIME_FRAME = 14; // days
	protected final static Integer SHORT_TIMESPAN = 1 * TIME_FRAME;
	protected final static Integer MEDIUM_TIMESPAN = 3 * TIME_FRAME;
	protected final static Integer LONG_TIMESPAN = 18 * TIME_FRAME;
	protected final static Integer MANY_PULSAR_CYCLES = 3; // commits
	protected final static Integer SMALL_SIZE_CHANGE = 10; // lines
	protected final static Integer MAJOR_SIZE_CHANGE = 1 * SIGNIFICANT_METHOD_SIZE;
	protected final static Integer ACTIVELY_CHANGED = 3; // times changed
	protected static Date now = null;

	public MethodMetrics() {
		try {
			now = new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/06");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param start
	 *            Date
	 * @param end
	 *            Date
	 * @return A Long representing the difference in days between start and end
	 *         dates
	 */
	protected Long getDifferenceInDays(Date start, Date end) {
		Long startTime = start.getTime();
		Long endTime = end.getTime();
		Long diffTime = endTime - startTime;
		if (diffTime < 0) {
			return -diffTime / (1000 * 60 * 60 * 24);
		}
		return diffTime / (1000 * 60 * 60 * 24);
	}

	/**
	 * Categorize commits into 3 types and return that list.
	 * 
	 * @param changesList
	 *            ArrayList with number of lines the method suffered during the
	 *            commits
	 * @return ArrayList of Strings divided by categories in:
	 *         refactor/refine/develop commits.
	 */
	protected ArrayList<String> getCommitsTypes(ArrayList<Integer> changesList) {
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
	 * Returns the points for the method if its size is bigger than
	 * EXTREMELY_LARGE_METHOD.
	 * 
	 * @param methodSize
	 *            The actual method size at the current time
	 * @return An Integer: 0 or 1 representing the points of method's size in
	 *         metrics.
	 */
	public Integer getMethodSizePoints(Integer methodSize) {
		if (methodSize >= EXTREMELY_LARGE_METHOD) {
			return 1;
		}
		return 0;
	}

	/**
	 * If the method is active: the latest activity of the method has occurred
	 * in of the most recent MEDIUM_TIMESPAN time-frames return 1 point.
	 * 
	 * @param commit
	 *            Latest commit in list
	 * @return An Integer: 0 or 1 representing the points of method's activity
	 *         in metrics.
	 */
	public Integer getActiveMethodPoints(Commit commit) {
		if (getDifferenceInDays(commit.getDate(), now) <= MEDIUM_TIMESPAN) {
			return 1;
		}
		return 0;
	}
}
