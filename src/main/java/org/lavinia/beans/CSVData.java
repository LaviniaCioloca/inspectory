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
package org.lavinia.beans;

import java.util.ArrayList;

public class CSVData {
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

	public boolean isPulsar() {
		return isPulsar;
	}

	public void setPulsar(boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public Boolean isSupernova() {
		return isSupernova;
	}

	public void setSupernova(Boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	/**
	 * Retrieve the method's line to be written in csv file.
	 * 
	 * @return An ArrayList of Strings data type.
	 */
	public ArrayList<String> getCSVLine() {
		ArrayList<String> csvLine = new ArrayList<>();
		csvLine.add(this.getFileName());
		csvLine.add(this.getClassName());
		csvLine.add(this.getMethodName());
		csvLine.add(this.getInitialSize().toString());
		csvLine.add(this.getActualSize().toString());
		csvLine.add(this.getNumberOfChanges().toString());
		csvLine.add(this.getChangesList().toString());
		csvLine.add(this.isSupernova.toString());
		return csvLine;
	}

}
