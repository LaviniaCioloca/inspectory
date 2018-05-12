package edu.lavinia.inspectory.op.beans;

public class MethodChangesData extends FileChangesData {
	private String className;
	private String methodName;

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

}
