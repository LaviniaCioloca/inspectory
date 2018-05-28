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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.op.beans.FileChangesData;

public class FileOwnershipInspectionTest {

	private static final File FILE = new File(
			"./src/test/resources/testFile.csv");
	private static Optional<PersistentProject> project;

	private static FileWriter csvWriter;
	private static FileWriter jsonWriter;
	private static FileOwnershipInspection fileOwnershipInspection;

	public FileOwnershipInspectionTest() {
		try {
			csvWriter = new FileWriter(FILE);
			fileOwnershipInspection = new FileOwnershipInspection(project,
					csvWriter, jsonWriter);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateResults() {
		fileOwnershipInspection.createResults();
	}

	@Test
	public void testWriteFileResults() {
		final HashMap<String, FileChangesData> entityOwnershipResult = new HashMap<>();
		final FileChangesData fileOwnershipInformation = new FileChangesData();
		fileOwnershipInformation.setNumberOfChanges(10);
		fileOwnershipInformation.setEntityCreator("test");

		final LinkedHashMap<String, Integer> authorsNumberOfChanges = new LinkedHashMap<>();
		authorsNumberOfChanges.put("test", 5);
		fileOwnershipInformation
				.setAuthorsNumberOfChanges(authorsNumberOfChanges);

		final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines = new LinkedHashMap<>();
		final ArrayList<Integer> lineChanges = new ArrayList<>(
				Arrays.asList(10, 5));
		authorsAddedAndDeletedLines.put("test", lineChanges);
		fileOwnershipInformation
				.setAuthorsAddedAndDeletedLines(authorsAddedAndDeletedLines);

		final LinkedHashMap<String, Double> ownershipPercentages = new LinkedHashMap<>();
		ownershipPercentages.put("test", 50.0);
		fileOwnershipInformation.setOwnershipPercentages(ownershipPercentages);
		fileOwnershipInformation.setDistinctOwners(new ArrayList<>());

		entityOwnershipResult.put("test", fileOwnershipInformation);

		fileOwnershipInspection.entityChangesData = entityOwnershipResult;

		fileOwnershipInspection.writeFileResults();
	}

	@Test
	public void testAddFileInformation() {
		final HashMap<String, FileChangesData> expectedFileOwnershipResult = new HashMap<>();
		final FileChangesData fileOwnershipInformation = new FileChangesData();
		final LinkedHashMap<String, Integer> authorsNumberOfChanges = new LinkedHashMap<>();
		final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines = new LinkedHashMap<>();
		final LinkedHashMap<String, Double> ownershipPercentages = new LinkedHashMap<>();
		final ArrayList<String> distinctOwners = new ArrayList<>();
		authorsNumberOfChanges.put("test", 1);

		fileOwnershipInformation.setNumberOfChanges(1);
		fileOwnershipInformation.setEntityCreator("test");
		fileOwnershipInformation
				.setAuthorsNumberOfChanges(authorsNumberOfChanges);
		fileOwnershipInformation
				.setAuthorsAddedAndDeletedLines(authorsAddedAndDeletedLines);
		fileOwnershipInformation.setOwnershipPercentages(ownershipPercentages);
		fileOwnershipInformation.setDistinctOwners(distinctOwners);
		expectedFileOwnershipResult.put("testFileName",
				fileOwnershipInformation);

		fileOwnershipInspection.entityChangesData = expectedFileOwnershipResult;
		fileOwnershipInspection.addFileInformation("testFileName", 1, "test",
				authorsNumberOfChanges, authorsAddedAndDeletedLines,
				ownershipPercentages, distinctOwners);

		assertEquals(expectedFileOwnershipResult,
				fileOwnershipInspection.getEntityChangesData());
	}

}
