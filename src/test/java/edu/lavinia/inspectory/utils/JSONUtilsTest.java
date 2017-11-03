package edu.lavinia.inspectory.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.JsonObject;

public class JSONUtilsTest {

	private static JSONUtils jsonUtils = new JSONUtils();

	@Test
	public void testGetSupernovaMethodsJSON() {
		JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Supernova Methods");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getSupernovaMethodsJSON("testFileName", 0));
	}

	@Test
	public void testGetPulsarMethodsJSON() {
		JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Methods");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getPulsarMethodsJSON("testFileName", 0));
	}

	@Test
	public void testGetSupernovaSeverityJSON() {
		JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Supernova Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getSupernovaSeverityJSON("testFileName", 0));
	}

	@Test
	public void testGetPulsarSeverityJSON() {
		JsonObject expectedJSONObject = new JsonObject();
		expectedJSONObject.addProperty("file", "testFileName");
		expectedJSONObject.addProperty("name", "Pulsar Severity");
		expectedJSONObject.addProperty("category", "Method Dynamics");
		expectedJSONObject.addProperty("value", 0);
		assertEquals(expectedJSONObject, jsonUtils.getPulsarSeverityJSON("testFileName", 0));
	}

}
