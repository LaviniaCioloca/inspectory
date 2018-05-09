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

import java.util.ArrayList;

import edu.lavinia.inspectory.beans.AbstractMethodChangesInformation;

/**
 * Bean class having the entire result information after applying Astronomical
 * Methods Metric onto methods.
 *
 * @author Lavinia Cioloca
 *
 */
public class AstronomicalMethodChangesInformation
		extends AbstractMethodChangesInformation {
	private ArrayList<Integer> changesList;
	private Boolean isPulsar;
	private Boolean isSupernova;
	private Integer pulsarSeverity;
	private Integer supernovaSeverity;
	private PulsarCriteria pulsarCriteria;
	private SupernovaCriteria supernovaCriteria;

	public ArrayList<Integer> getChangesList() {
		return changesList;
	}

	public void setChangesList(final ArrayList<Integer> changesList) {
		this.changesList = changesList;
	}

	public Boolean isPulsar() {
		return isPulsar;
	}

	public void setPulsar(final Boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Boolean isSupernova() {
		return isSupernova;
	}

	public void setSupernova(final Boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	public Boolean getIsPulsar() {
		return isPulsar;
	}

	public void setIsPulsar(final Boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Boolean getIsSupernova() {
		return isSupernova;
	}

	public void setIsSupernova(final Boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	public Integer getPulsarSeverity() {
		return pulsarSeverity;
	}

	public void setPulsarSeverity(final Integer pulsarSeverity) {
		this.pulsarSeverity = pulsarSeverity;
	}

	public Integer getSupernovaSeverity() {
		return supernovaSeverity;
	}

	public void setSupernovaSeverity(final Integer supernovaSeverity) {
		this.supernovaSeverity = supernovaSeverity;
	}

	public PulsarCriteria getPulsarCriteria() {
		return pulsarCriteria;
	}

	public void setPulsarCriteria(final PulsarCriteria pulsarCriteria) {
		this.pulsarCriteria = pulsarCriteria;
	}

	public SupernovaCriteria getSupernovaCriteria() {
		return supernovaCriteria;
	}

	public void setSupernovaCriteria(
			final SupernovaCriteria supernovaCriteria) {
		this.supernovaCriteria = supernovaCriteria;
	}

	/**
	 * Retrieve the method's entire information line to be written in the CSV
	 * file.
	 *
	 * @return An ArrayList of Strings data type.
	 */
	public ArrayList<String> getMethodInformationLine() {
		final ArrayList<String> methodInformationLine = new ArrayList<>();

		addGeneralInformation(methodInformationLine);

		addSupernovaInformation(methodInformationLine);

		addPulsarInformation(methodInformationLine);

		return methodInformationLine;
	}

	private void addPulsarInformation(
			final ArrayList<String> methodInformationLine) {

		methodInformationLine.add(this.isPulsar.toString());
		methodInformationLine.add(this.getPulsarSeverity().toString());
		methodInformationLine
				.add(pulsarCriteria.getRecentCyclesPoints().toString());
		methodInformationLine
				.add(pulsarCriteria.getAverageSizeIncreasePoints().toString());
		methodInformationLine
				.add(pulsarCriteria.getMethodSizePoints().toString());
		methodInformationLine
				.add(pulsarCriteria.getActivityStatePoints().toString());
	}

	private void addSupernovaInformation(
			final ArrayList<String> methodInformationLine) {

		methodInformationLine.add(this.isSupernova.toString());
		methodInformationLine.add(this.getSupernovaSeverity().toString());
		methodInformationLine
				.add(supernovaCriteria.getLeapsSizePoints().toString());
		methodInformationLine
				.add(supernovaCriteria.getRecentLeapsSizePoints().toString());
		methodInformationLine.add(
				supernovaCriteria.getSubsequentRefactoringPoints().toString());
		methodInformationLine
				.add(supernovaCriteria.getMethodSizePoints().toString());
		methodInformationLine
				.add(supernovaCriteria.getActivityStatePoints().toString());
	}

	private void addGeneralInformation(
			final ArrayList<String> methodInformationLine) {

		methodInformationLine.add(this.getFileName());
		methodInformationLine.add(this.getClassName());
		methodInformationLine.add(this.getMethodName());
		methodInformationLine.add(this.getInitialSize().toString());
		methodInformationLine.add(this.getActualSize().toString());
		methodInformationLine.add(this.getNumberOfChanges().toString());
		methodInformationLine.add(this.getMethodDeleted().toString());
		methodInformationLine.add(this.getChangesList().toString());
	}

	@Override
	public String toString() {
		return "MethodChangesInformation [commits=" + commits + ", fileName="
				+ fileName + ", className=" + className + ", methodName="
				+ methodName + ", initialSize=" + initialSize + ", actualSize="
				+ actualSize + ", methodDeleted=" + methodDeleted
				+ ", numberOfChanges=" + numberOfChanges + ", changesList="
				+ changesList + ", isPulsar=" + isPulsar + ", isSupernova="
				+ isSupernova + ", pulsarSeverity=" + pulsarSeverity
				+ ", supernovaSeverity=" + supernovaSeverity
				+ ", pulsarCriteria=" + pulsarCriteria + ", supernovaCriteria="
				+ supernovaCriteria + "]";
	}
}
