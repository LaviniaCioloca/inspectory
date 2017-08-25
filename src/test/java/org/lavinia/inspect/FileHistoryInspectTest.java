package org.lavinia.inspect;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.lavinia.visitor.GenericVisitor;
import org.lavinia.visitor.NodeVisitor;

public class FileHistoryInspectTest {

	@Test(expected = NullPointerException.class)
	public void testGetHistoryFunctionsAnalyze() {
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject());
		fileHistoryInspect.getHistoryFunctionsAnalyze();
	}

	@Test
	public void testAddToResult() {
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject());
		Logger logger = Logger.getRootLogger();
		GenericVisitor visitor = new NodeVisitor(logger);
		visitor.setIdentifier("abc");
		ArrayList<Integer> lineChanges = new ArrayList<Integer>();
		lineChanges.add(15);
		lineChanges.add(5);

		visitor.setTotal(15);
		fileHistoryInspect.newEntryInResult(visitor, lineChanges, logger);
		visitor.setTotal(5);
		fileHistoryInspect.newEntryInResult(visitor, lineChanges, logger);

		Map<String, ArrayList<Integer>> actual = fileHistoryInspect.getResult();
		Map<String, ArrayList<Integer>> expected = new HashMap<String, ArrayList<Integer>>();
		expected.put("abc", lineChanges);

		assertEquals(expected, actual);
	}

	@Test
	public void testSortResults() {
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject());
		Logger logger = Logger.getRootLogger();
		GenericVisitor visitor = new NodeVisitor(logger);
		visitor.setIdentifier("abc");
		ArrayList<Integer> lineChanges = new ArrayList<Integer>();

		visitor.setTotal(15);
		fileHistoryInspect.newEntryInResult(visitor, lineChanges, logger);

		visitor.setIdentifier("cde");
		visitor.setTotal(10);
		fileHistoryInspect.newEntryInResult(visitor, lineChanges, logger);
		visitor.setTotal(5);
		fileHistoryInspect.newEntryInResult(visitor, lineChanges, logger);

		List<ArrayList<Integer>> actual = fileHistoryInspect.sortResults();

		List<ArrayList<Integer>> expected = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> list1 = new ArrayList<>();
		list1.add(10);
		list1.add(5);

		ArrayList<Integer> list2 = new ArrayList<>();
		list2.add(15);
		expected.add(list1);
		expected.add(list2);

		assertEquals(expected, actual);

	}
}
