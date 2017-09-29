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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;
import org.lavinia.visitor.GenericVisitor;
import org.lavinia.visitor.NodeVisitor;

public class FileHistoryInspectTest {

	private static File file = new File("./src/test/resources/testFile.csv");

	@Test(expected = NullPointerException.class)
	public void testGetHistoryFunctionsAnalyze() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		fileHistoryInspect.getHistoryFunctionsAnalyze();
	}

	@Test
	public void testCheckEntryInResultSetIdentifierNull() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		assertFalse(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodNotExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		assertTrue(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
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

	@Test
	public void testAddToAllCommits() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		Commit commit = new Commit();
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);
		commits.add(commit);
		fileHistoryInspect.addToAllCommits(commits);
		assertEquals(commits, fileHistoryInspect.getAllCommits());
	}

	@Test
	public void testSortAllCommits() throws IOException, ParseException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/30"));
		commits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		commits.add(commit2);

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/10/01"));
		commits.add(commit3);

		fileHistoryInspect.addToAllCommits(commits);
		fileHistoryInspect.sortAllCommits();
		commits = new ArrayList<>();
		commits.add(commit2);
		commits.add(commit1);
		commits.add(commit3);
		assertEquals(commits, fileHistoryInspect.getAllCommits());
	}

	@Test
	public void testCreateAndSortAllCommits() throws ParseException, IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/10"));
		commits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		commits.add(commit2);

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/10/01"));
		commits.add(commit3);

		Map<String, CSVData> result = new HashMap<String, CSVData>();
		CSVData csvData = new CSVData();
		csvData.setClassName("test");
		csvData.setMethodName("test");
		result.put("test: test", csvData);
		csvData.setCommits(commits);
		fileHistoryInspect.setResult(result);
		ArrayList<CSVData> csvDataList = new ArrayList<>();
		csvDataList.add(csvData);
		fileHistoryInspect.createAndSortAllCommits(csvDataList);
		assertEquals(commits, fileHistoryInspect.getAllCommits());
	}

	@Test
	public void testWriteCSVFileData() throws IOException, ParseException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/10"));
		commits.add(commit1);

		Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2016/08/20"));
		commits.add(commit2);

		Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/10/01"));
		commits.add(commit3);

		Map<String, CSVData> result = new HashMap<String, CSVData>();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, -10, 50));
		CSVData csvData = new CSVData();
		csvData.setChangesList(changesList);
		csvData.setFileName("test");
		csvData.setClassName("test");
		csvData.setMethodName("test");
		result.put("test: test", csvData);
		csvData.setCommits(commits);
		fileHistoryInspect.setResult(result);
		ArrayList<CSVData> csvDataList = new ArrayList<>();
		csvDataList.add(csvData);
		fileHistoryInspect.writeCSVFileData(csvDataList);
	}

	@Test
	public void testAddDataInCSVList() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		CSVData csvData = new CSVData();
		csvData.setFileName("\"test\"");
		csvData.setClassName("\"test\"");
		csvData.setMethodName("\"test\"");
		ArrayList<CSVData> csvDataList = fileHistoryInspect.getCsvDataList();
		csvDataList.add(csvData);
		fileHistoryInspect.addDataInCSVList("test", "test", "test");
		assertEquals(csvDataList, fileHistoryInspect.getCsvDataList());
	}

	@Test
	public void testGettersAndSetters() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(), writer);
		fileHistoryInspect.setResult(new HashMap<String, CSVData>());
		fileHistoryInspect.getResult();
		fileHistoryInspect.getAllCommits();
		fileHistoryInspect.getCsvDataList();
	}
}
