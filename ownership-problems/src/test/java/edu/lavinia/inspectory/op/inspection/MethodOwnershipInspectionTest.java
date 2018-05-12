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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.beans.Commit;
import edu.lavinia.inspectory.op.beans.FileChangesData;
import edu.lavinia.inspectory.op.beans.MethodChangesData;

public class MethodOwnershipInspectionTest {

	private static final File FILE = new File(
			"./src/test/resources/testFile.csv");
	private static Optional<PersistentProject> project;

	private static FileWriter csvWriter;
	private static MethodOwnershipInspection methodOwnershipInspection;

	public MethodOwnershipInspectionTest() {
		try {
			csvWriter = new FileWriter(FILE);
			methodOwnershipInspection = new MethodOwnershipInspection(project,
					csvWriter);
		} catch (final IOException e) {
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
		final LinkedHashMap<String, List<Integer>> methodsAuthorsChanges = new LinkedHashMap<>();
		methodsAuthorsChanges.put("testAuthor",
				new ArrayList<>(Arrays.asList(10, 5)));

		final Map<String, Integer> methodNumberOfChanges = new HashMap<>();
		methodNumberOfChanges.put("testMethod", 5);

		final Map<String, FileChangesData> entityOwnershipResult = new HashMap<>();
		final MethodChangesData entityOwnershipInformation = new MethodChangesData();
		final LinkedHashMap<String, Double> ownershipPercentage = new LinkedHashMap<>();
		ownershipPercentage.put("testAuthor", 100.0);

		entityOwnershipInformation.setOwnershipPercentages(ownershipPercentage);
		entityOwnershipResult.put("testMethod", entityOwnershipInformation);

		methodOwnershipInspection.writeFileResults();
	}

	@Test
	public void testAddMethodsAuthorsChanges() {
		final Commit commit = new Commit();
		commit.setAuthor("testAuthor");

		final FileChangesData fileChangesData = new FileChangesData();
		fileChangesData.setAuthorsAddedAndDeletedLines(new LinkedHashMap<>());
		methodOwnershipInspection.entityChangesData.put("test -> test -> test",
				fileChangesData);
		methodOwnershipInspection.addMethodsAuthorsChanges(10, "test", "test",
				"test", commit);
	}

}
