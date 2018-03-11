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
package edu.lavinia.inspectory.am.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.FileMethodDynamics;

public class MethodDynamicsUtilsTest {

	private static MethodDynamicsUtils methodDynamicUtils = new MethodDynamicsUtils();
	private static Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<>();
	private static FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();

	// private final static String FILE_NAME_QUOTES = "\"testFileName\"";
	private final static String FILE_NAME_QUOTES = "testFileName";
	private final static String FILE_NAME = "testFileName";

	@BeforeClass
	public static void setUpBeforeClass() {
		projectMethodDynamics.put(FILE_NAME, fileMethodDynamics);
		methodDynamicUtils.setProjectMethodDynamics(projectMethodDynamics);
	}

	@Test
	public void testAddSupernovaMethodDynamics() {
		fileMethodDynamics.setSupernovaMethods(1);
		fileMethodDynamics.setSupernovaSeverity(10);
		methodDynamicUtils.addSupernovaMethodDynamics(FILE_NAME_QUOTES, 10);
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get(FILE_NAME));
	}

	@Test
	public void testAddPulsarMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(1);
		fileMethodDynamics.setPulsarSeverity(10);
		methodDynamicUtils.addPulsarMethodDynamics(FILE_NAME_QUOTES, 10);
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get(FILE_NAME));
	}

	@Test
	public void testAddDefaultMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(0);
		fileMethodDynamics.setPulsarSeverity(0);
		fileMethodDynamics.setSupernovaMethods(0);
		fileMethodDynamics.setSupernovaSeverity(0);
		methodDynamicUtils.addDefaultMethodDynamics(FILE_NAME_QUOTES);
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get(FILE_NAME));
	}

}
