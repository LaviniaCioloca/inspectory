package org.lavinia.inspect;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.lavinia.inspect.FileHistoryInspect;
import com.lavinia.inspect.RepoInspect;
import com.lavinia.visitor.GenericVisitor;
import com.lavinia.visitor.NodeVisitor;

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
		fileHistoryInspect.addToResult(visitor, lineChanges, logger);
		visitor.setTotal(5);
		fileHistoryInspect.addToResult(visitor, lineChanges, logger);

		Map<String, ArrayList<Integer>> actualResult = fileHistoryInspect.getResult();
		Map<String, ArrayList<Integer>> expectedResult = new HashMap<String, ArrayList<Integer>>();
		expectedResult.put("abc", lineChanges);

		assertEquals(expectedResult, actualResult);
	}

}
