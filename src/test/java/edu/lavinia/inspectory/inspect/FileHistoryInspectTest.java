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
package edu.lavinia.inspectory.inspect;

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

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.beans.MethodInformation;
import edu.lavinia.inspectory.beans.PulsarCriteria;
import edu.lavinia.inspectory.beans.SupernovaCriteria;
import edu.lavinia.inspectory.metrics.MethodMetrics;
import edu.lavinia.inspectory.visitor.GenericVisitor;
import edu.lavinia.inspectory.visitor.NodeVisitor;

public class FileHistoryInspectTest {

	private static File file = new File("./src/test/resources/testFile.csv");

	@Test(expected = NullPointerException.class)
	public void testGetHistoryFunctionsAnalyze() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		fileHistoryInspect.getHistoryFunctionsAnalyze();
	}

	@Test
	public void testCheckEntryInResultSetIdentifierNull() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		assertFalse(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(),
				"SimpleClass", new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodNotExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		assertTrue(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(),
				"SimpleClass", new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		Map<String, MethodInformation> result = new HashMap<String, MethodInformation>();
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setChangesList(new ArrayList<Integer>());
		methodInformation.setCommits(new ArrayList<Commit>());
		result.put("SimpleClass" + ": " + visitor.getIdentifier(), methodInformation);
		fileHistoryInspect.setResult(result);
		assertFalse(fileHistoryInspect.checkEntryInResultSet(visitor, new ArrayList<Integer>(),
				"SimpleClass", new Commit()));
	}

	@Test
	public void testAddToAllCommits() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
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
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
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
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
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

		Map<String, MethodInformation> result = new HashMap<String, MethodInformation>();
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setClassName("test");
		methodInformation.setMethodName("test");
		result.put("test: test", methodInformation);
		methodInformation.setCommits(commits);
		fileHistoryInspect.setResult(result);
		ArrayList<MethodInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodInformation);
		fileHistoryInspect.createAndSortAllCommits(methodInformationList);
		assertEquals(commits, fileHistoryInspect.getAllCommits());
	}

	@Test
	public void testWriteCSVFileData() throws IOException, ParseException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
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

		Map<String, MethodInformation> result = new HashMap<String, MethodInformation>();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, -10, 50));
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setChangesList(changesList);
		methodInformation.setFileName("test");
		methodInformation.setClassName("test");
		methodInformation.setMethodName("test");
		result.put("test: test", methodInformation);
		methodInformation.setCommits(commits);
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		methodInformation.setSupernovaCriteria(supernovaCriteria);
		methodInformation.setPulsarCriteria(pulsarCriteria);
		fileHistoryInspect.setResult(result);
		ArrayList<MethodInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodInformation);
		MethodMetrics.setAllCommits(commits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		MethodMetrics.setNow(commit3.getDate());
		fileHistoryInspect.writeCSVFileData(methodInformationList);
	}

	@Test
	public void testAddDataInCSVList() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		MethodInformation methodInformation = new MethodInformation();
		methodInformation.setFileName("\"test\"");
		methodInformation.setClassName("\"test\"");
		methodInformation.setMethodName("\"test\"");
		ArrayList<MethodInformation> methodInformationList = fileHistoryInspect
				.getMethodInformationList();
		methodInformationList.add(methodInformation);
		fileHistoryInspect.addDataInMethodInformationList("test", "test", "test");
		assertEquals(methodInformationList, fileHistoryInspect.getMethodInformationList());
	}

	@Test
	public void testGettersAndSetters() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(RepoInspect.getProject(),
				writer);
		fileHistoryInspect.setResult(new HashMap<String, MethodInformation>());
		fileHistoryInspect.getResult();
		fileHistoryInspect.getAllCommits();
		fileHistoryInspect.getMethodInformationList();
	}
}
