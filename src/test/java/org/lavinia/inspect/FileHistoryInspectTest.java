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

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
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
	public void testGetHistoryFunctionsAnalyze() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		fileHistoryInspect.getHistoryFunctionsAnalyze();
	}

	@Test
	public void testAddToResult() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		Logger logger = Logger.getRootLogger();
		GenericVisitor visitor = new NodeVisitor(logger);
		visitor.setIdentifier("abc");
		ArrayList<Integer> lineChanges = new ArrayList<Integer>();
		lineChanges.add(15);
		lineChanges.add(5);

		visitor.setTotal(15);
		fileHistoryInspect.checkEntryInResultSet(visitor, lineChanges, "SimpleClass");
		visitor.setTotal(5);
		fileHistoryInspect.checkEntryInResultSet(visitor, lineChanges, "SimpleClass");

		Map<String, ArrayList<Integer>> actual = fileHistoryInspect.getResult();
		Map<String, ArrayList<Integer>> expected = new HashMap<String, ArrayList<Integer>>();
		expected.put("SimpleClass: abc", lineChanges);

		assertEquals(expected, actual);
	}

	@Test
	public void testSortResults() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		Logger logger = Logger.getRootLogger();
		GenericVisitor visitor = new NodeVisitor(logger);
		visitor.setIdentifier("SimpleClass: abc");
		ArrayList<Integer> lineChanges = new ArrayList<Integer>();

		visitor.setTotal(15);
		fileHistoryInspect.checkEntryInResultSet(visitor, lineChanges, "SimpleClass");

		visitor.setIdentifier("SimpleClass: cde");
		visitor.setTotal(10);
		fileHistoryInspect.checkEntryInResultSet(visitor, lineChanges, "SimpleClass");
		visitor.setTotal(5);
		fileHistoryInspect.checkEntryInResultSet(visitor, lineChanges, "SimpleClass");

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
