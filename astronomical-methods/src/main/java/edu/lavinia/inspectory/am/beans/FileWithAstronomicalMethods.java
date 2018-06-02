package edu.lavinia.inspectory.am.beans;

public class FileWithAstronomicalMethods {

	private String fileName;
	private Integer numberOfAstronomicalMethods;
	private Integer sumOfSeverity;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public Integer getNumberOfAstronomicalMethods() {
		return numberOfAstronomicalMethods;
	}

	public void setNumberOfAstronomicalMethods(
			final Integer numberOfAstronomicalMethods) {
		this.numberOfAstronomicalMethods = numberOfAstronomicalMethods;
	}

	public Integer getSumOfSeverity() {
		return sumOfSeverity;
	}

	public void setSumOfSeverity(final Integer sumOfSeverity) {
		this.sumOfSeverity = sumOfSeverity;
	}

}
