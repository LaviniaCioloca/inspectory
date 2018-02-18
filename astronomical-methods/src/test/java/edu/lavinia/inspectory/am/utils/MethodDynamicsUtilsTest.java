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
package edu.lavinia.inspectory.am.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.FileMethodDynamics;
import edu.lavinia.inspectory.am.utils.MethodDynamicsUtils;

public class MethodDynamicsUtilsTest {

	private static MethodDynamicsUtils methodDynamicUtils = new MethodDynamicsUtils();
	private static Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<>();
	private static FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		projectMethodDynamics.put("testFileName", fileMethodDynamics);
		methodDynamicUtils.setProjectMethodDynamics(projectMethodDynamics);
	}

	@Test
	public void testAddSupernovaMethodDynamics() {
		fileMethodDynamics.setSupernovaMethods(1);
		fileMethodDynamics.setSupernovaSeverity(10);
		methodDynamicUtils.addSupernovaMethodDynamics("\"testFileName\"", 10);
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

	@Test
	public void testAddPulsarMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(1);
		fileMethodDynamics.setPulsarSeverity(10);
		methodDynamicUtils.addPulsarMethodDynamics("\"testFileName\"", 10);
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

	@Test
	public void testAddDefaultMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(0);
		fileMethodDynamics.setPulsarSeverity(0);
		fileMethodDynamics.setSupernovaMethods(0);
		fileMethodDynamics.setSupernovaSeverity(0);
		methodDynamicUtils.addDefaultMethodDynamics("\"testFileName\"");
		assertEquals(fileMethodDynamics, methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

}
