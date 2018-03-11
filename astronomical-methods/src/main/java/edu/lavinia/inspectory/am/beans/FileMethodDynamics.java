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

	private Integer supernovaMethods;
	private Integer pulsarMethods;
	private Integer supernovaSeverity;
	private Integer pulsarSeverity;

	public Integer getSupernovaMethods() {
		return supernovaMethods;
	}

	public void setSupernovaMethods(Integer supernovaMethods) {
		this.supernovaMethods = supernovaMethods;
	}

	public Integer getPulsarMethods() {
		return pulsarMethods;
	}

	public void setPulsarMethods(Integer pulsarMethods) {
		this.pulsarMethods = pulsarMethods;
	}

	public Integer getSupernovaSeverity() {
		return supernovaSeverity;
	}

	public void setSupernovaSeverity(Integer supernovaSeverity) {
		this.supernovaSeverity = supernovaSeverity;
	}

	public Integer getPulsarSeverity() {
		return pulsarSeverity;
	}

	public void setPulsarSeverity(Integer pulsarSeverity) {
		this.pulsarSeverity = pulsarSeverity;
	}

	@Override
	public String toString() {
		return "FileMethodDynamics [supernovaMethods=" + supernovaMethods
				+ ", pulsarMethods=" + pulsarMethods + ", supernovaSeverity="
				+ supernovaSeverity + ", pulsarSeverity=" + pulsarSeverity
				+ "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof FileMethodDynamics)) {
			return false;
		}

		FileMethodDynamics fileMethodDynamics = (FileMethodDynamics) obj;
		return supernovaMethods == fileMethodDynamics.supernovaMethods
				&& pulsarMethods == fileMethodDynamics.pulsarMethods
				&& supernovaSeverity == fileMethodDynamics.supernovaSeverity
				&& pulsarSeverity == fileMethodDynamics.pulsarSeverity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(supernovaMethods, pulsarMethods, supernovaSeverity,
				pulsarSeverity);
	}

}
