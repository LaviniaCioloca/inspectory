package org.lavinia.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class CSVUtilsTest {

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
		Writer w = new FileWriter("./src/test/resources/testFile.csv");
		List<String> values = new ArrayList<>();
		CSVUtils.writeLine(w, values);
	}
	/*
	 * @Test(expected = NullPointerException.class) public void
	 * testWriteLineException() throws IOException { CSVUtils.writeLine(null,
	 * null); }
	 */
	
	@Test
	public void testWriteLineFourArgs() throws IOException {
		Writer w = new FileWriter("./src/test/resources/testFile.csv");
		List<String> values = new ArrayList<>();
		CSVUtils.writeLine(w, values, ',', ' ');
	}
}
