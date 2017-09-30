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

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class PulsarMetricTest {
	private static PulsarMetric pulsarMetric = new PulsarMetric("2017/09/06");

	@Test
	public void testIsPulsarTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, -10, 50, -40, 250, -40, 100, -10, 15, -11, 100, -20));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/01/01"));
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
		assertTrue(pulsarMetric.isPulsar(csvData));
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
		assertTrue(pulsarMetric.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSizeFalse() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(20);
		assertFalse(pulsarMetric.isPulsar(csvData));
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
		assertFalse(pulsarMetric.isPulsar(csvData));
	}

	@Test
	public void testGetRecentCyclesPointsThree() {
		assertTrue(pulsarMetric.getRecentCyclesPoints(6) == 3);
	}

	@Test
	public void testGetRecentCyclesPointsTwo() {
		assertTrue(pulsarMetric.getRecentCyclesPoints(4) == 2);
	}

	@Test
	public void testGetRecentCyclesPointsOne() {
		assertTrue(pulsarMetric.getRecentCyclesPoints(2) == 1);
	}

	@Test
	public void testGetRecentCyclesPointsZero() {
		assertTrue(pulsarMetric.getRecentCyclesPoints(0) == 0);
	}

	@Test
	public void testGetAverageSizeIncreaseThree() {
		assertTrue(pulsarMetric.getAverageSizeIncrease(0.0) == 3);
	}

	@Test
	public void testGetAverageSizeIncreaseTwo() {
		assertTrue(pulsarMetric.getAverageSizeIncrease(16.7) == 2);
	}

	@Test
	public void testGetAverageSizeIncreaseOne() {
		assertTrue(pulsarMetric.getAverageSizeIncrease(25.0) == 1);
	}

	@Test
	public void testGetAverageSizeIncreaseZero() {
		assertTrue(pulsarMetric.getAverageSizeIncrease(50.0) == 0);
	}

	@Test
	public void testGetFileSizePointsTwo() {
		assertTrue(pulsarMetric.getMethodSizePoints(150) == 2);
	}

	@Test
	public void testGetFileSizePointsOne() {
		assertTrue(pulsarMetric.getMethodSizePoints(70) == 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		assertTrue(pulsarMetric.getMethodSizePoints(50) == 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(pulsarMetric.getActiveMethodPoints(commit) == 1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(pulsarMetric.getActiveMethodPoints(commit) == 0);
	}

	@Test
	public void testCountPulsarSeverityPointsMax() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(pulsarMetric.countPulsarSeverityPoints(6, 0.0, 150, commit) == 10);
	}

	@Test
	public void testCountPulsarSeverityPointsMin() throws ParseException {
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(pulsarMetric.countPulsarSeverityPoints(0, 50.0, 10, commit) == 1);
	}

	@Test
	public void testGetPulsarSeverityEight() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(200);
		ArrayList<Commit> commits = new ArrayList<>();
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-5);
		changesList.add(5);
		changesList.add(1);
		changesList.add(1);
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/03"));
		commits.add(commit);
		csvData.setCommits(commits);
		csvData.setChangesList(changesList);
		assertTrue(pulsarMetric.getPulsarSeverity(csvData) == 8);
	}

	@Test
	public void testGetPulsarSeverityFour() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(10);
		ArrayList<Commit> commits = new ArrayList<>();
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-5);
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);
		csvData.setCommits(commits);
		csvData.setChangesList(changesList);
		assertTrue(pulsarMetric.getPulsarSeverity(csvData) == 4);
	}

	@Test
	public void testIsMethodActivelyChangedFalse() {
		CSVData csvData = new CSVData();
		ArrayList<Commit> commits = new ArrayList<>();
		csvData.setCommits(commits);
		assertTrue(pulsarMetric.isMethodActivelyChanged(csvData) == false);
	}

	@Test
	public void testIsMethodActivelyChangedTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/21"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));
		commits.add(commit);
		csvData.setCommits(commits);
		assertTrue(pulsarMetric.isMethodActivelyChanged(csvData) == true);
	}

	@Test
	public void testCalculateAverageSizeIncreaseZero() {
		assertTrue(pulsarMetric.calculateAverageSizeIncrease(0, 0) == 0.0);
	}

	@Test
	public void testCalculateAverageSizeIncreaseTen() {
		assertTrue(pulsarMetric.calculateAverageSizeIncrease(1, 10) == 10.0);
	}

	@Test
	public void testCheckIfRecentPulsarCycleZero() throws ParseException {
		Date date = new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01");
		assertTrue(pulsarMetric.checkIfRecentPulsarCycle(date) == 0);
	}

	@Test
	public void testCheckIfRecentPulsarCycleOne() throws ParseException {
		Date date = new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/10");
		assertTrue(pulsarMetric.checkIfRecentPulsarCycle(date) == 1);
	}

	@Test
	public void testGetPulsarCriterionValues() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(250);
		ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(-10);
		changesList.add(50);
		changesList.add(2);
		changesList.add(2);
		changesList.add(2);
		changesList.add(20);
		changesList.add(-10);
		changesList.add(60);
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/20"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/21"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/02"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/03"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/04"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		Map<String, Object> pulsarCriterionValues = new HashMap<>();
		pulsarCriterionValues.put("isPulsar", true);
		pulsarCriterionValues.put("averageSizeIncrease", 18.0);
		pulsarCriterionValues.put("countPulsarCycles", 3);
		pulsarCriterionValues.put("countRecentPulsarCycles", 3);
		assertEquals(pulsarMetric.getPulsarCriterionValues(csvData), pulsarCriterionValues);
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedTrue() throws ParseException {
		ArrayList<Commit> allCommits = new ArrayList<>();
		Date dateNow = new SimpleDateFormat("yyyy/MM/dd").parse("2017/01/01");
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2015/08/01"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/05/21"));
		allCommits.add(commit);

		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));
		allCommits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		allCommits.add(commit2);

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/02"));
		allCommits.add(commit3);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/03"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/04"));
		allCommits.add(commit);
		PulsarMetric pulsarMetric = new PulsarMetric(dateNow, allCommits);
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		commits.add(commit3);
		CSVData csvData = new CSVData();
		csvData.setCommits(commits);
		assertEquals(pulsarMetric.isMethodTimeFrameActivelyChanged(csvData), true);
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedSmallSizeFalse() throws ParseException {
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/02"));

		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		CSVData csvData = new CSVData();
		csvData.setCommits(commits);
		assertEquals(pulsarMetric.isMethodTimeFrameActivelyChanged(csvData), false);
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedFalse() throws ParseException {
		ArrayList<Commit> allCommits = new ArrayList<>();
		Date dateNow = new SimpleDateFormat("yyyy/MM/dd").parse("2018/01/01");
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/05/30"));

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/07/01"));

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/09/02"));

		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/03"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/04"));
		allCommits.add(commit);
		
		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/11/04"));
		allCommits.add(commit);
		
		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/12/30"));
		allCommits.add(commit);
		PulsarMetric pulsarMetric = new PulsarMetric(dateNow, allCommits);
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		commits.add(commit3);
		CSVData csvData = new CSVData();
		csvData.setCommits(commits);
		assertEquals(pulsarMetric.isMethodTimeFrameActivelyChanged(csvData), false);
	}
}
