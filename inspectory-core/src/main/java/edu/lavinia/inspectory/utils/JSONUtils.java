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

import com.google.gson.JsonObject;

public class JSONUtils {
	public JsonObject getSupernovaMethodsJSON(String fileName,
			Integer supernovaMethods) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Supernova Methods");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", supernovaMethods);
		return jsonArrayElement;
	}

	public JsonObject getPulsarMethodsJSON(String fileName,
			Integer pulsarMethods) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Pulsar Methods");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", pulsarMethods);
		return jsonArrayElement;
	}

	public JsonObject getSupernovaSeverityJSON(String fileName,
			Integer supernovaSeverity) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Supernova Severity");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", supernovaSeverity);
		return jsonArrayElement;
	}

	public JsonObject getPulsarSeverityJSON(String fileName,
			Integer pulsarSeverity) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Pulsar Severity");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", pulsarSeverity);
		return jsonArrayElement;
	}
}
