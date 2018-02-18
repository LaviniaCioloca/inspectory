package edu.lavinia.inspectory.op.beans;

import java.util.HashMap;

public class FileOwnershipInformation {
	private Integer numberOfChanges;
	private String fileOwner;
	private HashMap<String, Integer> authorsChanges;

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public HashMap<String, Integer> getAuthorsChanges() {
		return authorsChanges;
	}

	public void setAuthorsChanges(HashMap<String, Integer> authorsChanges) {
		this.authorsChanges = authorsChanges;
	}

	@Override
	public String toString() {
		return "FileOwnershipInformation [numberOfChanges=" + numberOfChanges + ", fileOwner=" + fileOwner
				+ ", authorsChanges=" + authorsChanges + "]";
	}

}
