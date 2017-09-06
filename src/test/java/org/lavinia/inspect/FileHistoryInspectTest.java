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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;
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
	public void testCheckEntryInResultSetIdentifierNull() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		assertFalse(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodNotExists() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		assertTrue(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodExists() throws IOException {
		FileWriter writer = new FileWriter("testFile.csv");
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		Map<String, CSVData> result = new HashMap<String, CSVData>();
		CSVData csvData = new CSVData();
		csvData.setChangesList(new ArrayList<Integer>());
		csvData.setCommits(new ArrayList<Commit>());
		result.put("SimpleClass" + ": " + visitor.getIdentifier(), csvData);
		fileHistoryInspect.setResult(result);
		assertFalse(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}
}
