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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class SupernovaMetricTest {
	@Test
	public void testIsSupernovaTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, 110, 50, -250, -40));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/05/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/05/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/05/30"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/06/01"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.isSupernova(csvData));
	}

	@Test
	public void testIsSupernovaFalse() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(200, -10, 50, 250, -40));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/04/01"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertFalse(methodMetrics.isSupernova(csvData));
	}

	@Test
	public void testGetLeapsSizePointsTwo() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getLeapsSizePoints(200) == 2);
	}

	@Test
	public void testGetLeapsSizePointsOne() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getLeapsSizePoints(100) == 1);
	}

	@Test
	public void testGetLeapsSizePointsZero() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetRecentLeapsSizePointsThree() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(200) == 3);
	}

	@Test
	public void testGetRecentLeapsSizePointsTwo() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(90) == 2);
	}

	@Test
	public void testGetRecentLeapsSizePointsOne() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(60) == 1);
	}

	@Test
	public void testGetRecentLeapsSizePointsZero() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetSubsequentRefactoringPointsTwo() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(10) == 2);
	}

	@Test
	public void testGetSubsequentRefactoringPointsOne() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(25) == 1);
	}

	@Test
	public void testGetSubsequentRefactoringPointsZero() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(50) == 0);
	}

	@Test
	public void testGetFileSizePointsOne() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getMethodSizePoints(150) == 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		assertTrue(methodMetrics.getMethodSizePoints(10) == 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(methodMetrics.getActiveMethodPoints(commit) == 1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(methodMetrics.getActiveMethodPoints(commit) == 0);
	}

	@Test
	public void testCountSupernovaSeverityPointsMax() throws ParseException {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(methodMetrics.countSupernovaSeverityPoints(200, 200, 10, 150, commit) == 10);
	}

	@Test
	public void testCountSupernovaSeverityPointsMin() throws ParseException {
		SupernovaMetric methodMetrics = new SupernovaMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(methodMetrics.countSupernovaSeverityPoints(20, 20, 200, 10, commit) == 1);
	}
}
