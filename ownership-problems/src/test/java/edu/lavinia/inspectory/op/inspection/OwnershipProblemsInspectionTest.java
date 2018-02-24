package edu.lavinia.inspectory.op.inspection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.op.beans.FileOwnershipInformation;

public class OwnershipProblemsInspectionTest {

	private static final File FILE = new File("./src/test/resources/testFile.csv");
	private static final PersistentProject PROJECT = null;

	private FileWriter csvWriter;
	private OwnershipProblemsInspection ownershipProblemsInspection;

	public OwnershipProblemsInspectionTest() {
		try {
			csvWriter = new FileWriter(FILE);
			ownershipProblemsInspection = new OwnershipProblemsInspection(PROJECT, csvWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testCreateResults() {
		ownershipProblemsInspection.createResults();
	}

	@Test
	public void testAddFileInformation() {
		final HashMap<String, FileOwnershipInformation> expectedFileOwnershipResult = new HashMap<>();
		final FileOwnershipInformation fileOwnershipInformation = new FileOwnershipInformation();
		final HashMap<String, Integer> authorsChanges = new HashMap<>();
		authorsChanges.put("test", 1);

		fileOwnershipInformation.setNumberOfChanges(1);
		fileOwnershipInformation.setFileOwner("test");
		fileOwnershipInformation.setAuthorsChanges(authorsChanges);
		expectedFileOwnershipResult.put("testFileName", fileOwnershipInformation);

		ownershipProblemsInspection.addFileInformation("testFileName", 1, "test", authorsChanges);

		assertEquals(expectedFileOwnershipResult, ownershipProblemsInspection.getFileOwnershipResult());
	}
}
