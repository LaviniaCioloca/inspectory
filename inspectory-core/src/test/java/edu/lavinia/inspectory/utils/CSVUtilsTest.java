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
package edu.lavinia.inspectory.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

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
		final Writer w = new FileWriter(fileName);
		final List<String> values = new ArrayList<>();
		CSVUtils.writeLine(w, values);
	}

	@Test
	public void testWriteLineFourArgsSpace() throws IOException {
		final Writer w = new FileWriter(fileName);
		final List<String> values = new ArrayList<>();
		values.add("test1");
		values.add("test2\"");
		CSVUtils.writeLine(w, values, ',', ' ');
	}

	@Test
	public void testWriteLineFourArgsNotSpace() throws IOException {
		final Writer w = new FileWriter(fileName);
		final List<String> values = new ArrayList<>();
		values.add("test1");
		values.add("test2\"");
		values.add("\'test3\'");
		CSVUtils.writeLine(w, values, ' ', '\'');
	}

	@Test
	public void testInstantiation() throws IOException {
		final CSVUtils csvUtils = new CSVUtils();
		assertFalse(csvUtils == null);
	}
}
