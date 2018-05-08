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
package edu.lavinia.inspectory.am.beans;

import java.util.Objects;

/**
 * Bean class having method dynamics values of each file in repository.
 *
 * <p>
 * {@code supernovaMethods}: Number of Supernova Methods existent in file.
 * <br />
 * {@code pulsarMethod}: Number of Pulsar Methods existent in file. <br />
 * {@code supernovaSeverity}: Total severity value of Supernova methods in file.
 * <br />
 * {@code pulsarSeverity}: Total severity value of Pulsar methods in file.
 * </p>
 *
 * @author Lavinia Cioloca
 *
 */
public class FileMethodDynamics {

	private Integer numberOfSupernovaMethods;
	private Integer numberOfPulsarMethods;
	private Integer supernovaMethodsSeverityPoints;
	private Integer pulsarMethodsSeverityPoints;

	public Integer getNumberOfSupernovaMethods() {
		return numberOfSupernovaMethods;
	}

	public void setNumberOfSupernovaMethods(
			final Integer numberOfSupernovaMethods) {
		this.numberOfSupernovaMethods = numberOfSupernovaMethods;
	}

	public Integer getNumberOfPulsarMethods() {
		return numberOfPulsarMethods;
	}

	public void setNumberOfPulsarMethods(final Integer numberOfPulsarMethods) {
		this.numberOfPulsarMethods = numberOfPulsarMethods;
	}

	public Integer getSupernovaMethodsSeverityPoints() {
		return supernovaMethodsSeverityPoints;
	}

	public void setSupernovaMethodsSeverityPoints(
			final Integer supernovaMethodsSeverityPoints) {
		this.supernovaMethodsSeverityPoints = supernovaMethodsSeverityPoints;
	}

	public Integer getPulsarMethodsSeverityPoints() {
		return pulsarMethodsSeverityPoints;
	}

	public void setPulsarMethodsSeverityPoints(
			final Integer pulsarMethodsSeverityPoints) {
		this.pulsarMethodsSeverityPoints = pulsarMethodsSeverityPoints;
	}

	@Override
	public String toString() {
		return "FileMethodDynamics [numberOfSupernovaMethods="
				+ numberOfSupernovaMethods + ", numberOfPulsarMethods="
				+ numberOfPulsarMethods + ", supernovaMethodsSeverityPoints="
				+ supernovaMethodsSeverityPoints
				+ ", pulsarMethodsSeverityPoints=" + pulsarMethodsSeverityPoints
				+ "]";
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof FileMethodDynamics)) {
			return false;
		}

		final FileMethodDynamics fileMethodDynamics = (FileMethodDynamics) obj;
		return numberOfSupernovaMethods == fileMethodDynamics.numberOfSupernovaMethods
				&& numberOfPulsarMethods == fileMethodDynamics.numberOfPulsarMethods
				&& supernovaMethodsSeverityPoints == fileMethodDynamics.supernovaMethodsSeverityPoints
				&& pulsarMethodsSeverityPoints == fileMethodDynamics.pulsarMethodsSeverityPoints;
	}

	@Override
	public int hashCode() {
		return Objects.hash(numberOfSupernovaMethods, numberOfPulsarMethods,
				supernovaMethodsSeverityPoints, pulsarMethodsSeverityPoints);
	}

}
