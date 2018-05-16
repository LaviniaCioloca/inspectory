package edu.lavinia.inspectory.op.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import edu.lavinia.inspectory.beans.Commit;

public class MethodOwnershipProblemsMetricTest {

	private final MethodOwnershipProblemsMetric methodOwnershipProblemsMetric = new MethodOwnershipProblemsMetric();

	private static final String DATE_FORMAT = "yyyy/MM/dd";

	@Test
	public void testGetMethodSizePointsTwo() {
		assertTrue(methodOwnershipProblemsMetric.getMethodSizePoints(100) == 2);
	}

	@Test
	public void testGetMethodSizePointsOne() {
		assertTrue(methodOwnershipProblemsMetric.getMethodSizePoints(50) == 1);
	}

	@Test
	public void testGetMethodSizePointsZero() {
		assertTrue(methodOwnershipProblemsMetric.getMethodSizePoints(5) == 0);
	}

	@Test
	public void TestCheckIfMethodOlderThanMediumTimespanTrue()
			throws ParseException {
		final Commit firstMethodCommit = new Commit();
		firstMethodCommit
				.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
		final Commit lastRepositoryCommit = new Commit();
		lastRepositoryCommit
				.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2018/08/20"));

		assertTrue(methodOwnershipProblemsMetric
				.checkIfMethodOlderThanMediumTimespan(firstMethodCommit,
						lastRepositoryCommit));
	}

	@Test
	public void testCheckIfMethodOlderThanMediumTimespanFalse()
			throws ParseException {
		final Commit firstMethodCommit = new Commit();
		firstMethodCommit
				.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2018/08/20"));
		final Commit lastRepositoryCommit = new Commit();
		lastRepositoryCommit
				.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2018/08/20"));

		assertFalse(methodOwnershipProblemsMetric
				.checkIfMethodOlderThanMediumTimespan(firstMethodCommit,
						lastRepositoryCommit));
	}

	@Test
	public void testGetDisruptiveOwnersPointsThree() {
		assertTrue(methodOwnershipProblemsMetric
				.getDisruptiveOwnersPoints(5) == 3);
	}

	@Test
	public void testGetDisruptiveOwnersPointsTwo() {
		assertTrue(methodOwnershipProblemsMetric
				.getDisruptiveOwnersPoints(2) == 2);
	}

	@Test
	public void testGetDisruptiveOwnersPointsOne() {
		assertTrue(methodOwnershipProblemsMetric
				.getDisruptiveOwnersPoints(1) == 1);
	}

	@Test
	public void testGetDisruptiveOwnersPointsZero() {
		assertTrue(methodOwnershipProblemsMetric
				.getDisruptiveOwnersPoints(0) == 0);
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersOne() {
		final String[] owners = { "one" };

		final int expectedResult = 1;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersTwo() {
		final String[] owners = { "one", "two" };

		final int expectedResult = 2;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersOneDuplicate() {
		final String[] owners = { "one", "one" };

		final int expectedResult = 1;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersThree() {
		final String[] owners = { "one", "two", "three", "one", "four" };

		final int expectedResult = 3;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}
}
