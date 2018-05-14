package edu.lavinia.inspectory.op.metrics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MethodOwnershipProblemsMetricTest {

	@Test
	public void testLongestSequenceOfDistinctOwnersOne() {
		final MethodOwnershipProblemsMetric methodOwnershipProblemsMetric = new MethodOwnershipProblemsMetric();
		final String[] owners = { "one" };

		final int expectedResult = 1;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersTwo() {
		final MethodOwnershipProblemsMetric methodOwnershipProblemsMetric = new MethodOwnershipProblemsMetric();
		final String[] owners = { "one", "two" };

		final int expectedResult = 2;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersOneDuplicate() {
		final MethodOwnershipProblemsMetric methodOwnershipProblemsMetric = new MethodOwnershipProblemsMetric();
		final String[] owners = { "one", "one" };

		final int expectedResult = 1;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}

	@Test
	public void testLongestSequenceOfDistinctOwnersThree() {
		final MethodOwnershipProblemsMetric methodOwnershipProblemsMetric = new MethodOwnershipProblemsMetric();
		final String[] owners = { "one", "two", "three", "one", "four" };

		final int expectedResult = 3;
		assertEquals(expectedResult, methodOwnershipProblemsMetric
				.longestSequenceOfDistinctOwners(owners));
	}
}
