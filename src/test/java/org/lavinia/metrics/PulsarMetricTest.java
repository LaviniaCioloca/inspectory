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

public class PulsarMetricTest {
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
		PulsarMetric pulsarMetric = new PulsarMetric();
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
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSizeFalse() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(20);
		PulsarMetric pulsarMetric = new PulsarMetric();
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
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertFalse(pulsarMetric.isPulsar(csvData));
	}

	@Test
	public void testGetRecentCyclesPointsThree() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getRecentCyclesPoints(6) == 3);
	}

	@Test
	public void testGetRecentCyclesPointsTwo() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getRecentCyclesPoints(4) == 2);
	}

	@Test
	public void testGetRecentCyclesPointsOne() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getRecentCyclesPoints(2) == 1);
	}

	@Test
	public void testGetRecentCyclesPointsZero() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getRecentCyclesPoints(0) == 0);
	}

	@Test
	public void testGetAverageSizeIncreaseThree() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getAverageSizeIncrease(0.0) == 3);
	}

	@Test
	public void testGetAverageSizeIncreaseTwo() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getAverageSizeIncrease(16.7) == 2);
	}

	@Test
	public void testGetAverageSizeIncreaseOne() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getAverageSizeIncrease(25.0) == 1);
	}

	@Test
	public void testGetAverageSizeIncreaseZero() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getAverageSizeIncrease(50.0) == 0);
	}

	@Test
	public void testGetFileSizePointsTwo() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getMethodSizePoints(150) == 2);
	}

	@Test
	public void testGetFileSizePointsOne() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getMethodSizePoints(70) == 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		PulsarMetric pulsarMetric = new PulsarMetric();
		assertTrue(pulsarMetric.getMethodSizePoints(50) == 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(pulsarMetric.getActiveMethodPoints(commit) == 1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(pulsarMetric.getActiveMethodPoints(commit) == 0);
	}

	@Test
	public void testCountPulsarSeverityPointsMax() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		assertTrue(pulsarMetric.countPulsarSeverityPoints(6, 0.0, 150, commit) == 10);
	}

	@Test
	public void testCountPulsarSeverityPointsMin() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));
		assertTrue(pulsarMetric.countPulsarSeverityPoints(0, 50.0, 10, commit) == 1);
	}

	@Test
	public void testGetPulsarSeverityEight() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
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
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/20"));
		commits.add(commit);
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		commits.add(commit);
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/03"));
		commits.add(commit);
		csvData.setCommits(commits);
		csvData.setChangesList(changesList);
		assertTrue(pulsarMetric.getPulsarSeverity(csvData) == 8);
	}

	@Test
	public void testGetPulsarSeverityFour() throws ParseException {
		PulsarMetric pulsarMetric = new PulsarMetric();
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
}
