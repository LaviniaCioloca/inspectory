package edu.lavinia.inspectory.op.beans;

import java.util.ArrayList;

public class MethodsInFileAffectedByOwnershipProblems {
	private ArrayList<String> methodsName;
	private Integer numberOfMethodsAffected;
	private Integer sumOfMethodsSeverity;

	public ArrayList<String> getMethodsName() {
		return methodsName;
	}

	public void setMethodsName(final ArrayList<String> methodsName) {
		this.methodsName = methodsName;
	}

	public Integer getNumberOfMethodsAffected() {
		return numberOfMethodsAffected;
	}

	public void setNumberOfMethodsAffected(
			final Integer numberOfMethodsAffected) {
		this.numberOfMethodsAffected = numberOfMethodsAffected;
	}

	public Integer getSumOfMethodsSeverity() {
		return sumOfMethodsSeverity;
	}

	public void setSumOfMethodsSeverity(final Integer sumOfMethodsSeverity) {
		this.sumOfMethodsSeverity = sumOfMethodsSeverity;
	}

}
