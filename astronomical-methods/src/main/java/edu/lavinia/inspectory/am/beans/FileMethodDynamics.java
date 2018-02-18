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
package edu.lavinia.inspectory.am.beans;

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
		return "FileMethodDynamics [supernovaMethods=" + supernovaMethods + ", pulsarMethods=" + pulsarMethods
				+ ", supernovaSeverity=" + supernovaSeverity + ", pulsarSeverity=" + pulsarSeverity + "]";
	}

}
