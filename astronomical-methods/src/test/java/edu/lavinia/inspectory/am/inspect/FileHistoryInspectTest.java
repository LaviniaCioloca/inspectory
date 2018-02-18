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
package edu.lavinia.inspectory.am.inspect;

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
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.am.beans.MethodChangesInformation;
import edu.lavinia.inspectory.am.beans.PulsarCriteria;
import edu.lavinia.inspectory.am.beans.SupernovaCriteria;
import edu.lavinia.inspectory.am.inspection.AstronomicalMethodsInspection;
import edu.lavinia.inspectory.am.metrics.MethodMetrics;
import edu.lavinia.inspectory.am.visitor.GenericVisitor;
import edu.lavinia.inspectory.am.visitor.NodeVisitor;
import edu.lavinia.inspectory.beans.Commit;

public class FileHistoryInspectTest {

	private static final File file = new File("./src/test/resources/testFile.csv");
	private static final PersistentProject project = null;

	@Test(expected = NullPointerException.class)
	public void testGetHistoryFunctionsAnalyze() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		astronomicalMethodsInspection.getHistoryFunctionsAnalyze();
	}

	@Test
	public void testCheckEntryInResultSetIdentifierNull() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		assertFalse(astronomicalMethodsInspection.checkEntryInResultSet(visitor, new ArrayList<Integer>(),
				"SimpleClass", new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodNotExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		assertTrue(astronomicalMethodsInspection.checkEntryInResultSet(visitor, new ArrayList<Integer>(), "SimpleClass",
				new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodExists() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		String fileName = "testFileName";
		GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		Map<String, MethodChangesInformation> result = new HashMap<String, MethodChangesInformation>();
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setChangesList(new ArrayList<Integer>());
		methodChangesInformation.setCommits(new ArrayList<Commit>());
		result.put("SimpleClass" + ": " + visitor.getIdentifier(), methodChangesInformation);
		astronomicalMethodsInspection.setResult(result);
		assertFalse(astronomicalMethodsInspection.checkEntryInResultSet(visitor, new ArrayList<Integer>(),
				"SimpleClass", new Commit()));
	}

	@Test
	public void testAddToAllCommits() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		Commit commit = new Commit();
		ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);
		commits.add(commit);
		astronomicalMethodsInspection.addToAllCommits(commits);
		assertEquals(commits, astronomicalMethodsInspection.getAllCommits());
	}

	@Test
	public void testSortAllCommits() throws IOException, ParseException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
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

		astronomicalMethodsInspection.addToAllCommits(commits);
		astronomicalMethodsInspection.sortAllCommits();
		commits = new ArrayList<>();
		commits.add(commit2);
		commits.add(commit1);
		commits.add(commit3);
		assertEquals(commits, astronomicalMethodsInspection.getAllCommits());
	}

	@Test
	public void testCreateAndSortAllCommits() throws ParseException, IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
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

		Map<String, MethodChangesInformation> result = new HashMap<String, MethodChangesInformation>();
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setClassName("test");
		methodChangesInformation.setMethodName("test");
		result.put("test: test", methodChangesInformation);
		methodChangesInformation.setCommits(commits);
		astronomicalMethodsInspection.setResult(result);
		ArrayList<MethodChangesInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodChangesInformation);
		astronomicalMethodsInspection.createAndSortAllCommits(methodInformationList);
		assertEquals(commits, astronomicalMethodsInspection.getAllCommits());
	}

	@Test
	public void testWriteCSVFileData() throws IOException, ParseException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
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

		Map<String, MethodChangesInformation> result = new HashMap<String, MethodChangesInformation>();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, -10, 50));
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setFileName("test");
		methodChangesInformation.setClassName("test");
		methodChangesInformation.setMethodName("test");
		result.put("test: test", methodChangesInformation);
		methodChangesInformation.setCommits(commits);
		SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		PulsarCriteria pulsarCriteria = new PulsarCriteria();
		methodChangesInformation.setSupernovaCriteria(supernovaCriteria);
		methodChangesInformation.setPulsarCriteria(pulsarCriteria);
		astronomicalMethodsInspection.setResult(result);
		ArrayList<MethodChangesInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodChangesInformation);
		MethodMetrics.setAllCommits(commits);
		MethodMetrics.setAllCommitsIntoTimeFrames();
		MethodMetrics.setNow(commit3.getDate());
		astronomicalMethodsInspection.writeCSVFileData(methodInformationList);
	}

	@Test
	public void testAddDataInCSVList() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		MethodChangesInformation methodChangesInformation = new MethodChangesInformation();
		methodChangesInformation.setFileName("\"test\"");
		methodChangesInformation.setClassName("\"test\"");
		methodChangesInformation.setMethodName("\"test\"");
		ArrayList<MethodChangesInformation> methodInformationList = astronomicalMethodsInspection.getMethodInformationList();
		methodInformationList.add(methodChangesInformation);
		astronomicalMethodsInspection.addDataInMethodInformationList("test", "test", "test");
		assertEquals(methodInformationList, astronomicalMethodsInspection.getMethodInformationList());
	}

	@Test
	public void testGettersAndSetters() throws IOException {
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(project, writer,
				writer, writer);
		astronomicalMethodsInspection.setResult(new HashMap<String, MethodChangesInformation>());
		astronomicalMethodsInspection.getResult();
		astronomicalMethodsInspection.getAllCommits();
		astronomicalMethodsInspection.getMethodInformationList();
	}
}
