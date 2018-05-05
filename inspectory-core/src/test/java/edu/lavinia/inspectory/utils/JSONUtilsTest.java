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

		assertEquals(expectedJSONObject, jsonUtils.getAstronomicalPropertyJSON(
				"testFileName", 0, "Supernova Methods"));
	}

	@Test
	public void testGetPulsarMethodsJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Methods");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);

		assertEquals(expectedJSONObject, jsonUtils.getAstronomicalPropertyJSON(
				"testFileName", 0, "Pulsar Methods"));
	}

	@Test
	public void testGetSupernovaSeverityJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Supernova Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);

		assertEquals(expectedJSONObject, jsonUtils.getAstronomicalPropertyJSON(
				"testFileName", 0, "Supernova Severity"));
	}

	@Test
	public void testGetPulsarSeverityJSON() {
		final JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);

		assertEquals(expectedJSONObject, jsonUtils.getAstronomicalPropertyJSON(
				"testFileName", 0, "Pulsar Severity"));
	}

}
