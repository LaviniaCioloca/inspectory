package edu.lavinia.inspectory.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.lavinia.inspectory.utils.CSVUtils;

public class CSVUtilsTest {

	private static String fileName = "./src/test/resources/testFile.csv";

	@Test
	public void testFollowCVSformatTrue() {
		assertTrue(CSVUtils.followCVSformat("testQuote\"").equals("testQuote\"\""));
	}

	@Test
	public void testFollowCVSformatFalse() {
		assertFalse(CSVUtils.followCVSformat("testQuote\"").equals("testQuote"));
	}

	@Test
	public void testWriteLineTwoArgs() throws IOException {
		Writer w = new FileWriter(fileName);
		List<String> values = new ArrayList<>();
		CSVUtils.writeLine(w, values);
	}

	@Test
	public void testWriteLineFourArgsSpace() throws IOException {
		Writer w = new FileWriter(fileName);
		List<String> values = new ArrayList<>();
		values.add("test1");
		values.add("test2\"");
		CSVUtils.writeLine(w, values, ',', ' ');
	}

	@Test
	public void testWriteLineFourArgsNotSpace() throws IOException {
		Writer w = new FileWriter(fileName);
		List<String> values = new ArrayList<>();
		values.add("test1");
		values.add("test2\"");
		values.add("\'test3\'");
		CSVUtils.writeLine(w, values, ' ', '\'');
	}

	@Test
	public void testInstantiation() throws IOException {
		CSVUtils csvUtils = new CSVUtils();
		assertFalse(csvUtils == null);
	}
}
