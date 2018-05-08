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
	private Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<>();

	/**
	 * @param fileName
	 *            The .java file to add its Supernova severity
	 * @param supernovaSeverity
	 *            The Integer value of Supernova severity that can added to 0 or
	 *            to the already existent points
	 */
	public void addSupernovaMethodDynamics(final String fileName,
			final Integer supernovaSeverity) {

		final FileMethodDynamics fileMethodDynamics = projectMethodDynamics
				.get(fileName);
		Integer currentNumberOfSupernovaMethods = fileMethodDynamics
				.getNumberOfSupernovaMethods();
		final Integer currentSupernovaSeverityPoints = fileMethodDynamics
				.getSupernovaMethodsSeverityPoints();

		fileMethodDynamics
				.setNumberOfSupernovaMethods(++currentNumberOfSupernovaMethods);
		fileMethodDynamics.setSupernovaMethodsSeverityPoints(
				currentSupernovaSeverityPoints + supernovaSeverity);
	}

	/**
	 * @param fileName
	 *            The .java file to add its Pulsar severity
	 * @param pulsarSeverity
	 *            The Integer value of Pulsar severity that can added to 0 or to
	 *            the already existent points
	 */
	public void addPulsarMethodDynamics(final String fileName,
			final Integer pulsarSeverity) {

		final FileMethodDynamics fileMethodDynamics = projectMethodDynamics
				.get(fileName);
		Integer currentNumberOfPulsarMethods = fileMethodDynamics
				.getNumberOfPulsarMethods();
		final Integer currentPulsarSeverityPoints = fileMethodDynamics
				.getPulsarMethodsSeverityPoints();

		fileMethodDynamics
				.setNumberOfPulsarMethods(++currentNumberOfPulsarMethods);
		fileMethodDynamics.setPulsarMethodsSeverityPoints(
				currentPulsarSeverityPoints + pulsarSeverity);
	}

	/**
	 * Initially, every file in repository has Method Dynamics values 0.
	 *
	 * @param fileName
	 */
	public void addDefaultMethodDynamics(final String fileName) {
		final FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();
		fileMethodDynamics.setNumberOfPulsarMethods(0);
		fileMethodDynamics.setPulsarMethodsSeverityPoints(0);
		fileMethodDynamics.setNumberOfSupernovaMethods(0);
		fileMethodDynamics.setSupernovaMethodsSeverityPoints(0);
		projectMethodDynamics.put(fileName, fileMethodDynamics);
	}

	public void setProjectMethodDynamics(
			final Map<String, FileMethodDynamics> projectMethodDynamics) {
		this.projectMethodDynamics = projectMethodDynamics;
	}

	public Map<String, FileMethodDynamics> getProjectMethodDynamics() {
		return projectMethodDynamics;
	}

}
