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

import com.google.gson.JsonObject;

public class JSONUtils {
	public JsonObject getSupernovaMethodsJSON(String fileName, Integer supernovaMethods) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Supernova Methods");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", supernovaMethods);
		return jsonArrayElement;
	}

	public JsonObject getPulsarMethodsJSON(String fileName, Integer pulsarMethods) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Pulsar Methods");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", pulsarMethods);
		return jsonArrayElement;
	}

	public JsonObject getSupernovaSeverityJSON(String fileName, Integer supernovaSeverity) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Supernova Severity");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", supernovaSeverity);
		return jsonArrayElement;
	}

	public JsonObject getPulsarSeverityJSON(String fileName, Integer pulsarSeverity) {
		final JsonObject jsonArrayElement = new JsonObject();
		jsonArrayElement.addProperty("file", fileName);
		jsonArrayElement.addProperty("name", "Pulsar Severity");
		jsonArrayElement.addProperty("category", "Method Dynamics");
		jsonArrayElement.addProperty("value", pulsarSeverity);
		return jsonArrayElement;
	}
}
