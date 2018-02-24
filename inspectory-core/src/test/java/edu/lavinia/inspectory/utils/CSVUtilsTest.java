/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
