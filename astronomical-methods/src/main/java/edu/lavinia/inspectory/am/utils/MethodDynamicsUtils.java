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

import java.util.HashMap;
import java.util.Map;

import edu.lavinia.inspectory.am.beans.FileMethodDynamics;

/**
 * Utility class for Method Dynamics that has the entire
 * {@code projectMethodDynamics} map and utility functions for Astronomical
 * Method Metrics.
 * 
 * @author Lavinia Cioloca
 *
 */
public class MethodDynamicsUtils {
	private Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<String, FileMethodDynamics>();

	/**
	 * @param fileName
	 *            The .java file to add its Supernova severity
	 * @param supernovaSeverity
	 *            The Integer value of Supernova severity that can added to 0 or
	 *            to the already existent points
	 */
	public void addSupernovaMethodDynamics(String fileName,
			Integer supernovaSeverity) {

		final FileMethodDynamics fileMethodDynamics = projectMethodDynamics
				.get(fileName);
		Integer currentNumberOfSupernovaMethods = fileMethodDynamics
				.getSupernovaMethods();
		final Integer currentSupernovaSeverityPoints = fileMethodDynamics
				.getSupernovaSeverity();

		fileMethodDynamics
				.setSupernovaMethods(++currentNumberOfSupernovaMethods);
		fileMethodDynamics.setSupernovaSeverity(
				currentSupernovaSeverityPoints + supernovaSeverity);
	}

	/**
	 * @param fileName
	 *            The .java file to add its Pulsar severity
	 * @param pulsarSeverity
	 *            The Integer value of Pulsar severity that can added to 0 or to
	 *            the already existent points
	 */
	public void addPulsarMethodDynamics(String fileName,
			Integer pulsarSeverity) {

		final FileMethodDynamics fileMethodDynamics = projectMethodDynamics
				.get(fileName);
		Integer currentNumberOfPulsarMethods = fileMethodDynamics
				.getPulsarMethods();
		final Integer currentPulsarSeverityPoints = fileMethodDynamics
				.getPulsarSeverity();

		fileMethodDynamics.setPulsarMethods(++currentNumberOfPulsarMethods);
		fileMethodDynamics.setPulsarSeverity(
				currentPulsarSeverityPoints + pulsarSeverity);
	}

	/**
	 * Initially, every file in repository has Method Dynamics values 0.
	 * 
	 * @param fileName
	 */
	public void addDefaultMethodDynamics(String fileName) {
		final FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();
		fileMethodDynamics.setPulsarMethods(0);
		fileMethodDynamics.setPulsarSeverity(0);
		fileMethodDynamics.setSupernovaMethods(0);
		fileMethodDynamics.setSupernovaSeverity(0);
		projectMethodDynamics.put(fileName, fileMethodDynamics);
	}

	public void setProjectMethodDynamics(
			Map<String, FileMethodDynamics> projectMethodDynamics) {
		this.projectMethodDynamics = projectMethodDynamics;
	}

	public Map<String, FileMethodDynamics> getProjectMethodDynamics() {
		return projectMethodDynamics;
	}

}
