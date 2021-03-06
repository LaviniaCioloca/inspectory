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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.AstronomicalMethodChangesInformation;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;

public class MethodThresholdMeasureTest {

	private static final String DATE_FORMAT = "yyyy/MM/dd";

	@Test
	public void testGetDifferenceInDays() throws ParseException {
		final Date start = new SimpleDateFormat(DATE_FORMAT)
				.parse("2010/01/01");
		final Date end = new SimpleDateFormat(DATE_FORMAT).parse("2010/01/05");

		assertTrue(MethodThresholdsMeasure.getTimeDifferenceInDays(start,
				end) == 4.0);
	}

	@Test
	public void testGetCommitsTypes() {
		final ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(1);
		changesList.add(10);
		final ArrayList<String> expectedResultList = new ArrayList<>();
		expectedResultList.add("refactor");
		expectedResultList.add("refine");
		expectedResultList.add("develop");
		assertEquals(MethodThresholdsMeasure.getCommitsTypes(changesList),
				expectedResultList);
	}

	@Test
	public void testSplitCommitsIntoTimeFrames() throws ParseException {
		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/10/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/10/03"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/11/05"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/11/06"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/11/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/11/08"));
		commits.add(commit);
		methodChangesInformation.setCommits(commits);
		final LinkedHashMap<Commit, Integer> expected = new LinkedHashMap<>();
		expected.put(commits.get(0), 0);
		expected.put(commits.get(1), 0);
		expected.put(commits.get(2), 1);
		expected.put(commits.get(3), 1);
		expected.put(commits.get(4), 2);
		expected.put(commits.get(5), 2);
		expected.put(commits.get(6), 2);
		expected.put(commits.get(7), 2);

		final Pair<Integer, LinkedHashMap<Commit, Integer>> expectedPair = Pair
				.of(2, expected);

		assertEquals(expectedPair,
				MethodThresholdsMeasure.splitCommitsIntoTimeFrames(commits));
	}
	/*
	 * @Test public void getActiveTimeFrameMethodPointsOne() throws
	 * ParseException { CSVData methodInformation = new CSVData();
	 * ArrayList<Commit> commits = new ArrayList<>(); Commit commit = new
	 * Commit(); commit.setDate(new
	 * SimpleDateFormat(DATE_FORMAT).parse("2016/08/01")); commits.add(commit);
	 *
	 * Commit lastCommit = new Commit(); lastCommit.setDate(new
	 * SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
	 * commits.add(lastCommit);
	 *
	 * methodInformation.setCommits(commits); HashMap<Commit, Integer>
	 * commitsIntoTimeFrames = methodMetric.splitCommitsIntoTimeFrames(commits);
	 * assertTrue(methodMetric.getActiveTimeFrameMethodPoints(commit,
	 * lastCommit, commitsIntoTimeFrames) == 1); }
	 *
	 * @Test public void getActiveTimeFrameMethodPointsZero() throws
	 * ParseException { CSVData methodInformation = new CSVData();
	 * ArrayList<Commit> commits = new ArrayList<>(); Commit commit = new
	 * Commit(); commit.setDate(new
	 * SimpleDateFormat(DATE_FORMAT).parse("2016/08/01")); commits.add(commit);
	 *
	 * Commit lastCommit = new Commit(); lastCommit.setDate(new
	 * SimpleDateFormat(DATE_FORMAT).parse("2010/08/20"));
	 * commits.add(lastCommit);
	 *
	 * methodInformation.setCommits(commits); HashMap<Commit, Integer>
	 * commitsIntoTimeFrames = methodMetric.splitCommitsIntoTimeFrames(commits);
	 * assertTrue(methodMetric.getActiveTimeFrameMethodPoints(commit,
	 * lastCommit, commitsIntoTimeFrames) == 1); }
	 */
}
