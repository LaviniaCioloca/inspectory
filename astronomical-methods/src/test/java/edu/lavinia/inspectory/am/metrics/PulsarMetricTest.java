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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.AstronomicalMethodChangesInformation;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.metrics.MethodThresholdsMeasure;

public class PulsarMetricTest {

	private static PulsarMetric pulsarMetric = new PulsarMetric();

	@Test
	public void testIsPulsarTrue() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Integer> changesList = new ArrayList<>(Arrays.asList(
				210, -10, 50, -40, 250, -40, 100, -10, 15, -11, 100, -20));
		final ArrayList<Commit> commits = new ArrayList<>();
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
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		methodInformation.setActualSize(520);

		setCommitsForPulsarMetric(commits);

		assertTrue(pulsarMetric.isPulsar(methodInformation));
	}

	private void setCommitsForPulsarMetric(final ArrayList<Commit> commits) {
		final Pair<Integer, LinkedHashMap<Commit, Integer>> maximumTimeFrameCommits = MethodThresholdsMeasure
				.splitCommitsIntoTimeFrames(commits);

		final Integer maximumTimeFrameNumber = maximumTimeFrameCommits
				.getLeft();
		final LinkedHashMap<Commit, Integer> allCommitsIntoTimeFrames = maximumTimeFrameCommits
				.getRight();

		pulsarMetric.setAllCommits(commits);
		pulsarMetric.setAllCommitsIntoTimeFrames(allCommitsIntoTimeFrames);
		pulsarMetric.setMaximumTimeFrameNumber(maximumTimeFrameNumber);
	}

	@Test
	public void testIsPulsarSmallSizeTrue() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Integer> changesList = new ArrayList<>(Arrays.asList(
				210, 3, 3, 3, 3, -10, 10, 3, 3, 3, 3, -10, 30, 3, 3, 3, 3, 3));
		final ArrayList<Commit> commits = new ArrayList<>();
		final Commit commit = new Commit();
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
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		methodInformation.setActualSize(500);

		setCommitsForPulsarMetric(commits);

		assertTrue(pulsarMetric.isPulsar(methodInformation));
	}

	@Test
	public void testIsPulsarSizeFalse() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Commit> commits = new ArrayList<>();
		methodInformation.setActualSize(20);
		methodInformation.setCommits(commits);

		assertFalse(pulsarMetric.isPulsar(methodInformation));
	}

	@Test
	public void testIsPulsarFalse() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Integer> changesList = new ArrayList<>(
				Arrays.asList(210, -10, 50, -40, 250, -40, 100));
		final ArrayList<Commit> commits = new ArrayList<>();
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		methodInformation.setActualSize(520);

		assertFalse(pulsarMetric.isPulsar(methodInformation));
	}

	@Test
	public void testGetRecentCyclesPointsThree() {
		assertSame(pulsarMetric.getRecentCyclesPoints(6), 3);
	}

	@Test
	public void testGetRecentCyclesPointsTwo() {
		assertSame(pulsarMetric.getRecentCyclesPoints(4), 2);
	}

	@Test
	public void testGetRecentCyclesPointsOne() {
		assertSame(pulsarMetric.getRecentCyclesPoints(2), 1);
	}

	@Test
	public void testGetRecentCyclesPointsZero() {
		assertSame(pulsarMetric.getRecentCyclesPoints(0), 0);
	}

	@Test
	public void testGetAverageSizeIncreaseThree() {
		assertSame(pulsarMetric.getAverageSizeIncrease(0.0), 3);
	}

	@Test
	public void testGetAverageSizeIncreaseTwo() {
		assertSame(pulsarMetric.getAverageSizeIncrease(13.0), 2);
	}

	@Test
	public void testGetAverageSizeIncreaseOne() {
		assertSame(pulsarMetric.getAverageSizeIncrease(17.0), 1);
	}

	@Test
	public void testGetAverageSizeIncreaseZero() {
		assertTrue(pulsarMetric.getAverageSizeIncrease(50.0) == 0);
	}

	@Test
	public void testGetFileSizePointsTwo() {
		assertSame(pulsarMetric.getMethodSizePoints(150), 2);
	}

	@Test
	public void testGetFileSizePointsOne() {
		assertSame(pulsarMetric.getMethodSizePoints(50), 1);
	}

	@Test
	public void testGetFileSizePointsZero() {
		assertSame(pulsarMetric.getMethodSizePoints(30), 0);
	}

	@Test
	public void testGetActiveFilePointsOne() throws ParseException {
		final ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/05/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/15"));
		commits.add(commit);

		setCommitsForPulsarMetric(commits);

		assertSame(
				MethodThresholdsMeasure.getActiveMethodPoints(commit, commit),
				1);
	}

	@Test
	public void testGetActiveFilePointsZero() throws ParseException {
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));

		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));

		assertSame(
				MethodThresholdsMeasure.getActiveMethodPoints(commit, commit1),
				0);
	}

	@Test
	public void testCountPulsarSeverityPointsMax() throws ParseException {
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));

		setCommitsForPulsarMetric(new ArrayList<>(Arrays.asList(commit)));

		assertSame(pulsarMetric.countPulsarSeverityPoints(6, 0.0, 150, commit),
				10);
	}

	@Test
	public void testCountPulsarSeverityPointsMin() throws ParseException {
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/09/01"));

		assertSame(pulsarMetric.countPulsarSeverityPoints(0, 50.0, 10, commit),
				1);
	}

	@Test
	public void testGetPulsarSeveritySeven() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setActualSize(200);
		final ArrayList<Commit> commits = new ArrayList<>();
		final ArrayList<Integer> changesList = new ArrayList<>();
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
		methodInformation.setCommits(commits);
		methodInformation.setChangesList(changesList);

		setCommitsForPulsarMetric(commits);

		assertSame(pulsarMetric.getPulsarSeverity(methodInformation), 7);
	}

	@Test
	public void testGetPulsarSeverityFour() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setActualSize(10);
		final ArrayList<Commit> commits = new ArrayList<>();
		final ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-5);

		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);
		methodInformation.setCommits(commits);
		methodInformation.setChangesList(changesList);

		assertSame(pulsarMetric.getPulsarSeverity(methodInformation), 4);
	}

	@Test
	public void testIsMethodActivelyChangedFalse() {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Commit> commits = new ArrayList<>();
		methodInformation.setCommits(commits);

		assertFalse(pulsarMetric.isMethodActivelyChanged(methodInformation));
	}

	@Test
	public void testIsMethodActivelyChangedTrue() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		final ArrayList<Commit> commits = new ArrayList<>();
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
		methodInformation.setCommits(commits);

		setCommitsForPulsarMetric(commits);

		assertTrue(pulsarMetric.isMethodActivelyChanged(methodInformation));
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
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));

		setCommitsForPulsarMetric(new ArrayList<>(Arrays.asList(commit)));

		final Date date = new SimpleDateFormat("yyyy/MM/dd")
				.parse("2010/01/01");

		assertSame(pulsarMetric.checkIfRecentPulsarCycle(date), 0);
	}

	@Test
	public void testCheckIfRecentPulsarCycleOne() throws ParseException {
		final Date date = new SimpleDateFormat("yyyy/MM/dd")
				.parse("2017/09/10");
		final Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/01"));

		setCommitsForPulsarMetric(new ArrayList<>(Arrays.asList(commit)));

		assertSame(pulsarMetric.checkIfRecentPulsarCycle(date), 1);
	}

	@Test
	public void testGetPulsarCriterionValues() throws ParseException {
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setActualSize(250);
		final ArrayList<Integer> changesList = new ArrayList<>();
		changesList.add(-10);
		changesList.add(-10);
		changesList.add(50);
		changesList.add(2);
		changesList.add(2);
		changesList.add(2);
		changesList.add(20);
		changesList.add(-10);
		changesList.add(60);
		final ArrayList<Commit> commits = new ArrayList<>();
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
		methodInformation.setChangesList(changesList);
		methodInformation.setCommits(commits);
		final Map<String, Object> expectedPulsarCriterionValues = new HashMap<>();
		expectedPulsarCriterionValues.put("isPulsar", true);
		expectedPulsarCriterionValues.put("averageSizeIncrease", 20.0 / 3.0);
		expectedPulsarCriterionValues.put("countPulsarCycles", 3);
		expectedPulsarCriterionValues.put("countRecentPulsarCycles", 1);

		setCommitsForPulsarMetric(commits);

		assertEquals(expectedPulsarCriterionValues,
				pulsarMetric.getPulsarCriterionValues(methodInformation));
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedTrue()
			throws ParseException {
		final ArrayList<Commit> allCommits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2015/08/01"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/05/21"));
		allCommits.add(commit);

		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));
		allCommits.add(commit1);

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));
		allCommits.add(commit2);

		final Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/02"));
		allCommits.add(commit3);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/03"));
		allCommits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/04"));
		allCommits.add(commit);

		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		commits.add(commit3);
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setCommits(commits);

		setCommitsForPulsarMetric(allCommits);

		assertTrue(pulsarMetric.isMethodActivelyChanged(methodInformation));
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedSmallSizeFalse()
			throws ParseException {
		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/08/30"));

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/01"));

		final Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/09/02"));

		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setCommits(commits);

		assertFalse(pulsarMetric.isMethodActivelyChanged(methodInformation));
	}

	@Test
	public void testIsMethodTimeFrameActivelyChangedFalse()
			throws ParseException {
		final ArrayList<Commit> allCommits = new ArrayList<>();
		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/05/30"));

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/07/01"));

		final Commit commit3 = new Commit();
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

		setCommitsForPulsarMetric(allCommits);

		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit1);
		commits.add(commit2);
		commits.add(commit3);
		final AstronomicalMethodChangesInformation methodInformation = new AstronomicalMethodChangesInformation();
		methodInformation.setCommits(commits);

		assertFalse(pulsarMetric.isMethodActivelyChanged(methodInformation));
	}

}
