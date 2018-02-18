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

import java.util.HashMap;
import java.util.Map;

import edu.lavinia.inspectory.am.beans.FileMethodDynamics;

public class MethodDynamicsUtils {
	private Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<String, FileMethodDynamics>();

	public void addSupernovaMethodDynamics(String fileName, Integer supernovaSeverity) {
		fileName = fileName.substring(1, fileName.length() - 1);
		FileMethodDynamics fileMethodDynamics = projectMethodDynamics.get(fileName);
		Integer currentNumberOfSupernovaMethods = fileMethodDynamics.getSupernovaMethods();
		Integer currentSupernovaSeverityPoints = fileMethodDynamics.getSupernovaSeverity();
		fileMethodDynamics.setSupernovaMethods(++currentNumberOfSupernovaMethods);
		fileMethodDynamics.setSupernovaSeverity(currentSupernovaSeverityPoints + supernovaSeverity);
	}

	public void addPulsarMethodDynamics(String fileName, Integer pulsarSeverity) {
		fileName = fileName.substring(1, fileName.length() - 1);
		FileMethodDynamics fileMethodDynamics = projectMethodDynamics.get(fileName);
		Integer currentNumberOfPulsarMethods = fileMethodDynamics.getPulsarMethods();
		Integer currentPulsarSeverityPoints = fileMethodDynamics.getPulsarSeverity();
		fileMethodDynamics.setPulsarMethods(++currentNumberOfPulsarMethods);
		fileMethodDynamics.setPulsarSeverity(currentPulsarSeverityPoints + pulsarSeverity);
	}

	public void addDefaultMethodDynamics(String fileName) {
		FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();
		fileMethodDynamics.setPulsarMethods(0);
		fileMethodDynamics.setPulsarSeverity(0);
		fileMethodDynamics.setSupernovaMethods(0);
		fileMethodDynamics.setSupernovaSeverity(0);
		projectMethodDynamics.put(fileName, fileMethodDynamics);
	}

	public void setProjectMethodDynamics(Map<String, FileMethodDynamics> projectMethodDynamics) {
		this.projectMethodDynamics = projectMethodDynamics;
	}

	public Map<String, FileMethodDynamics> getProjectMethodDynamics() {
		return projectMethodDynamics;
	}

}
