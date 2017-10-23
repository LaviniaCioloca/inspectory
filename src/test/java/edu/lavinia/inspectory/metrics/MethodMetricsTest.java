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
package edu.lavinia.inspectory.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import edu.lavinia.inspectory.beans.CSVData;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodMetrics;
import edu.lavinia.inspectory.metrics.PulsarMetric;

public class MethodMetricsTest {
	private static MethodMetrics methodMetric = new PulsarMetric();

	@Test
	public void testGetDifferenceInDays() throws ParseException {
		Date start = new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01");
		Date end = new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/05");
		assertTrue(MethodMetrics.getDifferenceInDays(start, end) == 4.0);
	}

	@Test
	public void testGetCommitsTypes() {
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(1);
		changesList.add(10);
		ArrayList<String> expectedResultList = new ArrayList<>();
		expectedResultList.add("refactor");
		expectedResultList.add("refine");
		expectedResultList.add("develop");
		assertEquals(methodMetric.getCommitsTypes(changesList), expectedResultList);
	}

	@Test
	public void testSplitCommitsIntoTimeFrames() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/10/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/10/03"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/05"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/06"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/08"));
		commits.add(commit);
		csvData.setCommits(commits);
		HashMap<Commit, Integer> expected = new HashMap<>();
		expected.put(commits.get(0), 0);
		expected.put(commits.get(1), 0);
		expected.put(commits.get(2), 1);
		expected.put(commits.get(3), 1);
		expected.put(commits.get(4), 2);
		expected.put(commits.get(5), 2);
		expected.put(commits.get(6), 2);
		expected.put(commits.get(7), 2);
		assertEquals(expected, MethodMetrics.splitCommitsIntoTimeFrames(commits));
	}
	/*
	 * @Test public void getActiveTimeFrameMethodPointsOne() throws
	 * ParseException { CSVData csvData = new CSVData(); ArrayList<Commit>
	 * commits = new ArrayList<>(); Commit commit = new Commit();
	 * commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/01"));
	 * commits.add(commit);
	 * 
	 * Commit lastCommit = new Commit(); lastCommit.setDate(new
	 * SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
	 * commits.add(lastCommit);
	 * 
	 * csvData.setCommits(commits); HashMap<Commit, Integer>
	 * commitsIntoTimeFrames = methodMetric.splitCommitsIntoTimeFrames(commits);
	 * assertTrue(methodMetric.getActiveTimeFrameMethodPoints(commit,
	 * lastCommit, commitsIntoTimeFrames) == 1); }
	 * 
	 * @Test public void getActiveTimeFrameMethodPointsZero() throws
	 * ParseException { CSVData csvData = new CSVData(); ArrayList<Commit>
	 * commits = new ArrayList<>(); Commit commit = new Commit();
	 * commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/01"));
	 * commits.add(commit);
	 * 
	 * Commit lastCommit = new Commit(); lastCommit.setDate(new
	 * SimpleDateFormat("yyyy/MM/dd").parse("2010/08/20"));
	 * commits.add(lastCommit);
	 * 
	 * csvData.setCommits(commits); HashMap<Commit, Integer>
	 * commitsIntoTimeFrames = methodMetric.splitCommitsIntoTimeFrames(commits);
	 * assertTrue(methodMetric.getActiveTimeFrameMethodPoints(commit,
	 * lastCommit, commitsIntoTimeFrames) == 1); }
	 */
}
