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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class MethodMetricsTest {

	@Test
	public void testIsPulsarTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, -10, 50, -40, 250, -40, 100, -10, 15, -11, 100, -20));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/06/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/16"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/17"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/18"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/20"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(520);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSmallSizeTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(
				Arrays.asList(210, 5, 5, 5, -10, 10, 5, 5, 5, -10, 30, 5, 5, 5, 5));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2017/07/01"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(500);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarSizeFalse() throws ParseException {
		CSVData csvData = new CSVData();
		csvData.setActualSize(20);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsPulsarFalse() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, -10, 50, -40, 250, -40, 100));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		csvData.setActualSize(520);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isPulsar(csvData));
	}

	@Test
	public void testIsSupernovaTrue() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(210, 110, 50, -250, -40));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/30"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/04/01"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertTrue(methodMetrics.isSupernova(csvData));
	}

	@Test
	public void testIsSupernovaFalse() throws ParseException {
		CSVData csvData = new CSVData();
		ArrayList<Integer> changesList = new ArrayList<Integer>(Arrays.asList(200, -10, 50, 250, -40));
		ArrayList<Commit> commits = new ArrayList<>();
		Commit commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/07"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/01/15"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/03/01"));
		commits.add(commit);

		commit = new Commit();
		commit.setDate(new SimpleDateFormat("yyyy/MM/dd").parse("2010/04/01"));
		commits.add(commit);
		csvData.setChangesList(changesList);
		csvData.setCommits(commits);
		MethodMetrics methodMetrics = new MethodMetrics();
		assertFalse(methodMetrics.isSupernova(csvData));
	}
}
