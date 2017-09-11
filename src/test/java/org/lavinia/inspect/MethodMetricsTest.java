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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class MethodMetricsTest {

	@Test
	public void testIsPulsarTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, -10, 50, -40, 250, -40, 100, -10, 15, -11, 100, -20));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/06/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/16"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/17"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/18"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/20"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(520);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSmallSizeTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, 3, 3, 3, 3, -10, 10, 3, 3, 3, 3, -10, 30, 3, 3, 3, 3, 3));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/01"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(500);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSizeFalse() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(20);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarFalse() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, -10, 50, -40, 250, -40, 100));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(520);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsSupernovaTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, 110, 50, -250, -40));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/30"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/04/01"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		MethodMetrics methodMetrics = new MethodMetrics();
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
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isSupernova(csvData));
	}

	@Test
	public void testGetLeapsSizePointsTwo() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getLeapsSizePoints(200) == 2);
	}

	@Test
	public void testGetLeapsSizePointsOne() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getLeapsSizePoints(100) == 1);
	}

	@Test
	public void testGetLeapsSizePointsZero() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetRecentLeapsSizePointsThree() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(200) == 3);
	}

	@Test
	public void testGetRecentLeapsSizePointsTwo() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(150) == 2);
	}

	@Test
	public void testGetRecentLeapsSizePointsOne() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(100) == 1);
	}

	@Test
	public void testGetRecentLeapsSizePointsZero() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getRecentLeapsSizePoints(10) == 0);
	}

	@Test
	public void testGetSubsequentRefactoringPointsTwo() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(10) == 2);
	}

	@Test
	public void testGetSubsequentRefactoringPointsOne() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(45) == 1);
	}

	@Test
	public void testGetSubsequentRefactoringPointsZero() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getSubsequentRefactoringPoints(50) == 0);
	}

	@Test
	public void testGetFileSizePointsOne() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getFileSizePoints(150) == 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.getFileSizePoints(10) == 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		MethodMetrics methodMetrics = new MethodMetrics();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(methodMetrics.getActiveFilePoints(commit) == 1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		MethodMetrics methodMetrics = new MethodMetrics();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(methodMetrics.getActiveFilePoints(commit) == 0);
	}

	@Test
	public void testCountSupernovaSeverityPointsMax() throws ParseException {
		MethodMetrics methodMetrics = new MethodMetrics();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(methodMetrics.countSupernovaSeverityPoints(200, 200, 20, 150, commit) == 10);
	}

	@Test
	public void testCountSupernovaSeverityPointsMin() throws ParseException {
		MethodMetrics methodMetrics = new MethodMetrics();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(methodMetrics.countSupernovaSeverityPoints(20, 20, 200, 10, commit) == 1);
	}
}
