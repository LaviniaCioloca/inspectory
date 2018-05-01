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

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.op.beans.EntityOwnershipInformation;

public class FileOwnershipInspectionTest {

	private static final File FILE = new File(
			"./src/test/resources/testFile.csv");
	private static final PersistentProject PROJECT = null;

	private static FileWriter csvWriter;
	private static FileOwnershipInspection fileOwnershipInspection;

	public FileOwnershipInspectionTest() {
		try {
			csvWriter = new FileWriter(FILE);
			fileOwnershipInspection = new FileOwnershipInspection(PROJECT,
					csvWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateResults() {
		fileOwnershipInspection.createResults();
	}

	@Test
	public void testWriteFileResults() {
		final HashMap<String, EntityOwnershipInformation> entityOwnershipResult = new HashMap<>();
		final EntityOwnershipInformation fileOwnershipInformation = new EntityOwnershipInformation();
		fileOwnershipInformation.setNumberOfChanges(10);
		fileOwnershipInformation.setEntityCreator("test");

		final LinkedHashMap<String, Integer> authorsChanges = new LinkedHashMap<>();
		authorsChanges.put("test", 5);
		fileOwnershipInformation.setAuthorsChanges(authorsChanges);

		final LinkedHashMap<String, ArrayList<Integer>> authorsLineChanges = new LinkedHashMap<>();
		final ArrayList<Integer> lineChanges = new ArrayList<>(
				Arrays.asList(10, 5));
		authorsLineChanges.put("test", lineChanges);
		fileOwnershipInformation.setAuthorsLineChanges(authorsLineChanges);

		final LinkedHashMap<String, Double> ownershipPercentages = new LinkedHashMap<>();
		ownershipPercentages.put("test", 50.0);
		fileOwnershipInformation.setOwnershipPercentages(ownershipPercentages);

		fileOwnershipInspection.setEntityOwnershipResult(entityOwnershipResult);

		fileOwnershipInspection.writeFileResults();
	}
}
