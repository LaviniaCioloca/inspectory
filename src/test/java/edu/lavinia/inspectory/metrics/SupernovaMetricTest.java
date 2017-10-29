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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.beans.MethodInformation;

public class SupernovaMetricTest {
	SupernovaMetric supernovaMetric = new SupernovaMetric();

	@After
	public void tearDown() {
		MethodMetrics.setAllCommits(new ArrayList<Commit>());
		MethodMetrics.setNow(new Date());
	}

	@Test
	public void testIsSupernovaTrue() throws ParseException {
		MethodInformation methodInformation = new MethodInformation();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, 110, 50, -250, -40, 25, -10, 15, -10, 5, -15, 50, 15));
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

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/10/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/11/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/12/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/21"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/03/15"));
		commits.add(commit);
		methodInformation.setActualSize(100);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		assertTrue(supernovaMetric.isSupernova(methodInformation));
	}

	@Test
	public void testIsSupernovaFalse() throws ParseException {
		MethodInformation methodInformation = new MethodInformation();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(200, -10, 50, 250, -40));
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
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		methodInformation.setActualSize(100);
		assertFalse(supernovaMetric.isSupernova(methodInformation));
	}

	@Test
	public void testGetLeapsSizePointsTwo() {
		assertTrue(supernovaMetric.getLeapsSizePoints(200) == 2);
	}

	@Test
	public void testGetLeapsSizePointsOne() {
		assertTrue(supernovaMetric.getLeapsSizePoints(100) == 1);
	}

	@Test
	public void testGetLeapsSizePointsZero() {
		assertTrue(supernovaMetric.getLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetRecentLeapsSizePointsThree() {
		assertTrue(supernovaMetric.getRecentLeapsSizePoints(200) == 3);
	}

	@Test
	public void testGetRecentLeapsSizePointsTwo() {
		assertTrue(supernovaMetric.getRecentLeapsSizePoints(90) == 2);
	}

	@Test
	public void testGetRecentLeapsSizePointsOne() {
		assertTrue(supernovaMetric.getRecentLeapsSizePoints(60) == 1);
	}

	@Test
	public void testGetRecentLeapsSizePointsZero() {
		assertTrue(supernovaMetric.getRecentLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetSubsequentRefactoringPointsTwo() {
		assertTrue(supernovaMetric.getSubsequentRefactoringPoints(10.0) == 2);
	}

	@Test
	public void testGetSubsequentRefactoringPointsOne() {
		assertTrue(supernovaMetric.getSubsequentRefactoringPoints(25.0) == 1);
	}

	@Test
	public void testGetSubsequentRefactoringPointsZero() {
		assertTrue(supernovaMetric.getSubsequentRefactoringPoints(50.0) == 0);
	}

	@Test
	public void testGetFileSizePointsOne() {
		assertTrue(supernovaMetric.getMethodSizePoints(150) == 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		assertTrue(supernovaMetric.getMethodSizePoints(10) == 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		MethodMetrics.setAllCommits(new ArrayList<>(Arrays.asList(commit)));
		MethodMetrics.setAllCommitsIntoTimeFrames();
		assertTrue(supernovaMetric.getActiveMethodPoints(commit) == 1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		commits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/01/01"));
		commits.add(commit2);

		commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/03/01"));
		commits.add(commit2);

		commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/04/15"));
		commits.add(commit2);

		commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));
		commits.add(commit2);
		MethodMetrics.setAllCommits(commits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		assertTrue(supernovaMetric.getActiveMethodPoints(commit1) == 0);
	}

	@Test
	public void testCountSupernovaSeverityPointsMax() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		MethodMetrics.setAllCommits(new ArrayList<>(Arrays.asList(commit)));
		MethodMetrics.setAllCommitsIntoTimeFrames();
		assertTrue(supernovaMetric.countSupernovaSeverityPoints(200, 200, 10.0, 150, commit) == 10);
	}

	@Test
	public void testCountSupernovaSeverityPointsMin() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(supernovaMetric.countSupernovaSeverityPoints(20, 20, 200.0, 10, commit) == 1);
	}

	@Test
	public void testGetSupernovaSeverity() throws ParseException {
		MethodInformation methodInformation = new MethodInformation();
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/03/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/03/10"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/03/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/04/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/06/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/10"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		commits.add(commit);
		ArrayList<Integer> changesList = new ArrayList<>(
				Arrays.asList(100, -5, -5, 20, 70, -5, 150, 5));
		methodInformation.setActualSize(120);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		MethodMetrics.setAllCommits(commits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		assertTrue(supernovaMetric.getSupernovaSeverity(methodInformation) == 10);
	}

	@Test
	public void testGetSupernovaCriterionValues() throws ParseException {
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setActualSize(250);
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(50);
		changesList.add(-20);
		changesList.add(2);
		changesList.add(2);
		changesList.add(20);
		changesList.add(-10);
		changesList.add(60);
		changesList.add(-20);
		changesList.add(-10);
		changesList.add(-6);
		changesList.add(10);
		changesList.add(-6);
		changesList.add(1);
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
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/02"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/03"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/04"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/12/04"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2018/02/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2018/04/30"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2018/05/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2018/06/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2018/07/30"));
		commits.add(commit);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		Map<String, Object> supernovaCriterionValues = new HashMap<>();
		supernovaCriterionValues.put("isSupernova", false);
		supernovaCriterionValues.put("sumOfAllLeaps", -31);
		supernovaCriterionValues.put("sumRecentLeaps", 5);
		supernovaCriterionValues.put("averageSubsequentCommits", (double) 22 / (double) 3);
		assertEquals(supernovaMetric.getSupernovaCriterionValues(methodInformation),
				supernovaCriterionValues);
	}

	@Test
	public void testDivideLifetimeInIntervals() throws ParseException {
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setActualSize(250);
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(50);
		changesList.add(-20);
		changesList.add(2);
		changesList.add(2);
		changesList.add(20);
		changesList.add(-10);
		changesList.add(60);
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/09/02"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/01/01"));
		commits.add(commit);

		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/04/01"));
		commits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/02"));
		commits.add(commit2);

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/03"));
		commits.add(commit3);

		Commit commit4 = new Commit();
		commit4.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/04"));
		commits.add(commit4);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);

		HashMap<Commit, Integer> lifetimeIntoIntervals = supernovaMetric
				.divideLifetimeInIntervals(methodInformation);
		HashMap<Commit, Integer> expectedLifetimeIntoIntervals = new HashMap<>();
		expectedLifetimeIntoIntervals.put(commit1, 0);
		expectedLifetimeIntoIntervals.put(commit2, 1);
		expectedLifetimeIntoIntervals.put(commit3, 2);
		expectedLifetimeIntoIntervals.put(commit4, 3);
		assertEquals(expectedLifetimeIntoIntervals, lifetimeIntoIntervals);
	}
}