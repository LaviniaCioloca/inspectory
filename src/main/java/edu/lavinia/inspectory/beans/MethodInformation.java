/*******************************************************************************
 * Copyright (c) 2017 Lavinia Cioloca
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
package edu.lavinia.inspectory.beans;

import java.util.ArrayList;

public class MethodInformation {
	private ArrayList<Commit> commits;
	private String fileName;
	private String className;
	private String methodName;
	private Integer initialSize;
	private Integer actualSize;
	private Integer numberOfChanges;
	private ArrayList<Integer> changesList;
	private Boolean isPulsar;
	private Boolean isSupernova;
	private Integer pulsarSeverity;
	private Integer supernovaSeverity;
	private PulsarCriteria pulsarCriteria;
	private SupernovaCriteria supernovaCriteria;

	public ArrayList<Commit> getCommits() {
		return commits;
	}

	public void setCommits(ArrayList<Commit> commits) {
		this.commits = commits;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Integer getActualSize() {
		return actualSize;
	}

	public void setActualSize(Integer actualSize) {
		this.actualSize = actualSize;
	}

	public Integer getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	public ArrayList<Integer> getChangesList() {
		return changesList;
	}

	public void setChangesList(ArrayList<Integer> changesList) {
		this.changesList = changesList;
	}

	public Boolean isPulsar() {
		return isPulsar;
	}

	public void setPulsar(Boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Boolean isSupernova() {
		return isSupernova;
	}

	public void setSupernova(Boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	public Boolean getIsPulsar() {
		return isPulsar;
	}

	public void setIsPulsar(Boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Boolean getIsSupernova() {
		return isSupernova;
	}

	public void setIsSupernova(Boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	public Integer getPulsarSeverity() {
		return pulsarSeverity;
	}

	public void setPulsarSeverity(Integer pulsarSeverity) {
		this.pulsarSeverity = pulsarSeverity;
	}

	public Integer getSupernovaSeverity() {
		return supernovaSeverity;
	}

	public void setSupernovaSeverity(Integer supernovaSeverity) {
		this.supernovaSeverity = supernovaSeverity;
	}

	public PulsarCriteria getPulsarCriteria() {
		return pulsarCriteria;
	}

	public void setPulsarCriteria(PulsarCriteria pulsarCriteria) {
		this.pulsarCriteria = pulsarCriteria;
	}

	public SupernovaCriteria getSupernovaCriteria() {
		return supernovaCriteria;
	}

	public void setSupernovaCriteria(SupernovaCriteria supernovaCriteria) {
		this.supernovaCriteria = supernovaCriteria;
	}

	/**
	 * Retrieve the method's line to be written in CSV file.
	 * 
	 * @return An ArrayList of Strings data type.
	 */
	public ArrayList<String> getMethodInformationLine() {
		ArrayList<String> methodInformationLine = new ArrayList<>();
		methodInformationLine.add(this.getFileName());
		methodInformationLine.add(this.getClassName());
		methodInformationLine.add(this.getMethodName());
		methodInformationLine.add(this.getInitialSize().toString());
		methodInformationLine.add(this.getActualSize().toString());
		methodInformationLine.add(this.getNumberOfChanges().toString());
		methodInformationLine.add(this.getChangesList().toString());

		methodInformationLine.add(this.isSupernova.toString());
		methodInformationLine.add(this.getSupernovaSeverity().toString());
		methodInformationLine.add(supernovaCriteria.getLeapsSizePoints().toString());
		methodInformationLine.add(supernovaCriteria.getRecentLeapsSizePoints().toString());
		methodInformationLine.add(supernovaCriteria.getSubsequentRefactoringPoints().toString());
		methodInformationLine.add(supernovaCriteria.getMethodSizePoints().toString());
		methodInformationLine.add(supernovaCriteria.getActivityStatePoints().toString());

		methodInformationLine.add(this.isPulsar.toString());
		methodInformationLine.add(this.getPulsarSeverity().toString());
		methodInformationLine.add(pulsarCriteria.getRecentCyclesPoints().toString());
		methodInformationLine.add(pulsarCriteria.getAverageSizeIncreasePoints().toString());
		methodInformationLine.add(pulsarCriteria.getMethodSizePoints().toString());
		methodInformationLine.add(pulsarCriteria.getActivityStatePoints().toString());
		return methodInformationLine;
	}

}
