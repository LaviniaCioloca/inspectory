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
package edu.lavinia.inspectory.op.inspection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MethodOwnershipInspectionTest {

	private static final File FILE = new File(
			"./src/test/resources/testFile.csv");
	private static final PersistentProject PROJECT = null;

	private static FileWriter csvWriter;
	private static MethodOwnershipInspection methodOwnershipInspection;

	public MethodOwnershipInspectionTest() {
		try {
			csvWriter = new FileWriter(FILE);
			methodOwnershipInspection = new MethodOwnershipInspection(PROJECT,
					csvWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateResults() {
		methodOwnershipInspection.createResults();
	}

	@Test
	public void testWriteFileResults() {
		final Table<String, String, List<Integer>> methodsAuthorsChanges = HashBasedTable
				.create();
		methodsAuthorsChanges.put("testMethod", "testAuthor",
				new ArrayList<Integer>(Arrays.asList(10, 5)));

		final Map<String, Integer> methodNumberOfChanges = new HashMap<>();
		methodNumberOfChanges.put("testMethod", 5);

		final Map<String, Integer> methodSize = new HashMap<>();
		methodSize.put("testMethod", 10);

		methodOwnershipInspection
				.setMethodsAuthorsChanges(methodsAuthorsChanges);
		methodOwnershipInspection
				.setMethodNumberOfChanges(methodNumberOfChanges);
		methodOwnershipInspection.setMethodSize(methodSize);

		methodOwnershipInspection.writeFileResults();
	}
}
