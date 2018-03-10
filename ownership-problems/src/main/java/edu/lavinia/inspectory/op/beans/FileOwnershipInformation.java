package edu.lavinia.inspectory.op.beans;

import java.util.LinkedHashMap;
import java.util.Objects;

public class FileOwnershipInformation {
	private Integer numberOfChanges;
	private String fileCreator;
	private LinkedHashMap<String, Integer> authorsChanges;

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	public String getFileCreator() {
		return fileCreator;
	}

	public void setFileCreator(String fileCreator) {
		this.fileCreator = fileCreator;
	}

	public LinkedHashMap<String, Integer> getAuthorsChanges() {
		return authorsChanges;
	}

	public void setAuthorsChanges(
			LinkedHashMap<String, Integer> authorsChanges) {
		this.authorsChanges = authorsChanges;
	}

	@Override
	public String toString() {
		return "FileOwnershipInformation [numberOfChanges=" + numberOfChanges
				+ ", fileCreator=" + fileCreator + ", authorsChanges="
				+ authorsChanges + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof FileOwnershipInformation)) {
			return false;
		}

		FileOwnershipInformation fileOwnershipInformation = (FileOwnershipInformation) obj;
		return numberOfChanges == fileOwnershipInformation.numberOfChanges
				&& Objects.equals(fileCreator,
						fileOwnershipInformation.fileCreator)
				&& Objects.equals(authorsChanges,
						fileOwnershipInformation.authorsChanges);
	}

	@Override
	public int hashCode() {
		return Objects.hash(numberOfChanges, fileCreator, authorsChanges);
	}
}
