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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JSONUtilsTest {

	private static JSONUtils jsonUtils = new JSONUtils();

	@Test
	public void testGetSupernovaMethodsJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Supernova Methods");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getSupernovaMethodsJSON("testFileName", 0));
	}

	@Test
	public void testGetPulsarMethodsJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Methods");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getPulsarMethodsJSON("testFileName", 0));
	}

	@Test
	public void testGetSupernovaSeverityJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Supernova Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getSupernovaSeverityJSON("testFileName", 0));
	}

	@Test
	public void testGetPulsarSeverityJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getPulsarSeverityJSON("testFileName", 0));
	}

}
