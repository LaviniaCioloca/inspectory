package org.lavinia.beans;

import java.util.ArrayList;

public class CSVData {
	private String fileName;
	private String className;
	private String methodName;
	private Integer initialSize;
	private Integer actualSize;
	private Integer numberOfChanges;
	private boolean isPulsar;
	private boolean isSupernova;

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

	public boolean isPulsar() {
		return isPulsar;
	}

	public void setPulsar(boolean isPulsar) {
		this.isPulsar = isPulsar;
	}

	public boolean isSupernova() {
		return isSupernova;
	}

	public void setSupernova(boolean isSupernova) {
		this.isSupernova = isSupernova;
	}

	public ArrayList<String> getCSVLine() {
		ArrayList<String> csvLine = new ArrayList<>();
		csvLine.add(this.getFileName());
		csvLine.add(this.getClassName());
		csvLine.add(this.getMethodName());
		csvLine.add(this.getInitialSize().toString());
		csvLine.add(this.getActualSize().toString());
		csvLine.add(this.getNumberOfChanges().toString());
		return csvLine;
	}

}
