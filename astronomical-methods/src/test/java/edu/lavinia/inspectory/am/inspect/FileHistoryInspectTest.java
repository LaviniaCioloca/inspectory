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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.am.beans.AstronomicalMethodChangesInformation;
import edu.lavinia.inspectory.am.beans.FileMethodDynamics;
import edu.lavinia.inspectory.am.beans.PulsarCriteria;
import edu.lavinia.inspectory.am.beans.SupernovaCriteria;
import edu.lavinia.inspectory.am.inspection.AstronomicalMethodsInspection;
import edu.lavinia.inspectory.am.utils.MethodDynamicsUtils;
import edu.lavinia.inspectory.am.visitor.NodeVisitor;
import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.visitor.GenericVisitor;

public class FileHistoryInspectTest {

	private static final File FILE = new File(
			"./src/test/resources/testFile.csv");
	private static Optional<PersistentProject> project;
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	private static final String CLASS_NAME = "SimpleClass";

	@Test(expected = NullPointerException.class)
	public void testGetHistoryFunctionsAnalyze() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		astronomicalMethodsInspection.analyzeAstronomicalMethods();
	}

	@Test
	public void testCheckEntryInResultSetIdentifierNull() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final String fileName = "testFileName";
		final GenericVisitor visitor = new NodeVisitor(fileName);

		assertFalse(astronomicalMethodsInspection.checkEntryInResultSet(visitor,
				CLASS_NAME, new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodNotExists() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final String fileName = "testFileName";
		final GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");

		assertTrue(astronomicalMethodsInspection.checkEntryInResultSet(visitor,
				CLASS_NAME, new Commit()));
	}

	@Test
	public void testCheckEntryInResultSetMethodExists() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final String fileName = "testFileName";
		final GenericVisitor visitor = new NodeVisitor(fileName);
		visitor.setIdentifier("abc");
		final Map<String, AstronomicalMethodChangesInformation> result = new HashMap<>();
		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		methodChangesInformation.setChangesList(new ArrayList<Integer>());
		methodChangesInformation.setCommits(new ArrayList<Commit>());
		result.put(fileName + ":" + CLASS_NAME + ": " + visitor.getIdentifier(),
				methodChangesInformation);
		astronomicalMethodsInspection.setResult(result);

		assertFalse(astronomicalMethodsInspection.checkEntryInResultSet(visitor,
				CLASS_NAME, new Commit()));
	}

	@Test
	public void testAddToAllCommits() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final Commit commit = new Commit();
		final ArrayList<Commit> commits = new ArrayList<>();
		commits.add(commit);
		commits.add(commit);
		astronomicalMethodsInspection.addToAllCommits(commits);

		assertEquals(commits, astronomicalMethodsInspection.getAllCommits());
	}

	@Test
	public void testSortAllCommits() throws IOException, ParseException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		ArrayList<Commit> commits = new ArrayList<>();
		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/30"));
		commits.add(commit1);

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
		commits.add(commit2);

		final Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/10/01"));
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
	public void testCreateAndSortAllCommits()
			throws ParseException, IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final ArrayList<Commit> commits = new ArrayList<>();
		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/10"));
		commits.add(commit1);

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
		commits.add(commit2);

		final Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/10/01"));
		commits.add(commit3);

		final Map<String, AstronomicalMethodChangesInformation> result = new HashMap<>();
		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		methodChangesInformation.setFileName("test");
		methodChangesInformation.setClassName("test");
		methodChangesInformation.setMethodName("test");
		result.put("test:test: test", methodChangesInformation);
		methodChangesInformation.setCommits(commits);
		astronomicalMethodsInspection.setResult(result);
		final ArrayList<AstronomicalMethodChangesInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodChangesInformation);

		astronomicalMethodsInspection
				.setMethodInformationList(methodInformationList);
		astronomicalMethodsInspection.createAndSortAllCommits();
		assertEquals(commits, astronomicalMethodsInspection.getAllCommits());
	}

	@Test
	public void testWriteCSVFileData() throws IOException, ParseException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final ArrayList<Commit> commits = new ArrayList<>();
		final Commit commit1 = new Commit();
		commit1.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/10"));
		commits.add(commit1);

		final Commit commit2 = new Commit();
		commit2.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2016/08/20"));
		commits.add(commit2);

		final Commit commit3 = new Commit();
		commit3.setDate(new SimpleDateFormat(DATE_FORMAT).parse("2017/10/01"));
		commits.add(commit3);

		final Map<String, AstronomicalMethodChangesInformation> result = new HashMap<>();
		final ArrayList<Integer> changesList = new ArrayList<>(
				Arrays.asList(210, -10, 50));
		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		methodChangesInformation.setChangesList(changesList);
		methodChangesInformation.setFileName("test");
		methodChangesInformation.setClassName("test");
		methodChangesInformation.setMethodName("test");
		result.put("test:test: test", methodChangesInformation);
		methodChangesInformation.setCommits(commits);
		final SupernovaCriteria supernovaCriteria = new SupernovaCriteria();
		final PulsarCriteria pulsarCriteria = new PulsarCriteria();
		methodChangesInformation.setSupernovaCriteria(supernovaCriteria);
		methodChangesInformation.setPulsarCriteria(pulsarCriteria);
		astronomicalMethodsInspection.setResult(result);
		final ArrayList<AstronomicalMethodChangesInformation> methodInformationList = new ArrayList<>();
		methodInformationList.add(methodChangesInformation);

		astronomicalMethodsInspection
				.setMethodInformationList(methodInformationList);
		astronomicalMethodsInspection.writeCSVFileData();
	}

	@Test
	public void testAddDataInCSVList() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		final AstronomicalMethodChangesInformation methodChangesInformation = new AstronomicalMethodChangesInformation();
		methodChangesInformation.setFileName("\"test\"");
		methodChangesInformation.setClassName("\"test\"");
		methodChangesInformation.setMethodName("\"test\"");
		final ArrayList<AstronomicalMethodChangesInformation> methodInformationList = astronomicalMethodsInspection
				.getMethodInformationList();
		methodInformationList.add(methodChangesInformation);
		astronomicalMethodsInspection.addDataInMethodInformationList("test",
				"test", "test");

		assertEquals(methodInformationList,
				astronomicalMethodsInspection.getMethodInformationList());
	}

	@Test
	public void testGettersAndSetters() throws IOException {
		FILE.getParentFile().mkdirs();
		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);
		astronomicalMethodsInspection.setResult(
				new HashMap<String, AstronomicalMethodChangesInformation>());
		astronomicalMethodsInspection.getResult();
		astronomicalMethodsInspection.getAllCommits();
		astronomicalMethodsInspection.getMethodInformationList();
	}

	@Test
	public void testSortFilesAffectedByNumberOfSupernovaMethods()
			throws IOException {
		final Map<String, FileMethodDynamics> filesAffectedAndTheirSeverity = new HashMap<>();

		final FileMethodDynamics methodsForTestFile1 = new FileMethodDynamics();
		methodsForTestFile1.setNumberOfSupernovaMethods(3);

		final FileMethodDynamics methodsForTestFile2 = new FileMethodDynamics();
		methodsForTestFile2.setNumberOfSupernovaMethods(5);

		final FileMethodDynamics methodsForTestFile3 = new FileMethodDynamics();
		methodsForTestFile3.setNumberOfSupernovaMethods(1);

		filesAffectedAndTheirSeverity.put("testFile1", methodsForTestFile1);
		filesAffectedAndTheirSeverity.put("testFile2", methodsForTestFile2);
		filesAffectedAndTheirSeverity.put("testFile3", methodsForTestFile3);

		final FileWriter writer = new FileWriter(FILE);
		final AstronomicalMethodsInspection astronomicalMethodsInspection = new AstronomicalMethodsInspection(
				project, writer, writer, writer);

		final MethodDynamicsUtils methodDynamics = new MethodDynamicsUtils();
		methodDynamics.setProjectMethodDynamics(filesAffectedAndTheirSeverity);

		astronomicalMethodsInspection.setMethodDynamics(methodDynamics);

		final Map<String, FileMethodDynamics> expectedFilesAffectedAndTheirSeverity = new LinkedHashMap<>();

		expectedFilesAffectedAndTheirSeverity.put("testFile2",
				methodsForTestFile2);
		expectedFilesAffectedAndTheirSeverity.put("testFile1",
				methodsForTestFile1);
		expectedFilesAffectedAndTheirSeverity.put("testFile3",
				methodsForTestFile3);

		assertEquals(expectedFilesAffectedAndTheirSeverity,
				astronomicalMethodsInspection
						.sortFilesAffectedByNumberOfSupernovaMethods());

	}
}
