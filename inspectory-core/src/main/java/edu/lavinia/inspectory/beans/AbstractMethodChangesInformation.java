package edu.lavinia.inspectory.beans;

import java.util.ArrayList;

public class AbstractMethodChangesInformation {
	protected ArrayList<Commit> commits;
	protected String fileName;
	protected String className;
	protected String methodName;
	protected Integer initialSize;
	protected Integer actualSize;
	protected Boolean methodDeleted = false;
	protected Integer numberOfChanges;

	public ArrayList<Commit> getCommits() {
		return commits;
	}

	public void setCommits(final ArrayList<Commit> commits) {
		this.commits = commits;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(final String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	public Integer getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(final Integer initialSize) {
		this.initialSize = initialSize;
	}

	public Integer getActualSize() {
		return actualSize;
	}

	public void setActualSize(final Integer actualSize) {
		this.actualSize = actualSize;
	}

	public Boolean getMethodDeleted() {
		return methodDeleted;
	}

	public void setMethodDeleted(final Boolean methodDeleted) {
		this.methodDeleted = methodDeleted;
	}

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(final Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

}
