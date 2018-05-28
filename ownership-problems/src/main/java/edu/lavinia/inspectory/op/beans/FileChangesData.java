package edu.lavinia.inspectory.op.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.lavinia.inspectory.beans.Commit;

public class FileChangesData {
	protected ArrayList<Commit> commits;
	protected String fileName;
	protected Integer initialSize = 0;
	protected Integer actualSize = 0;
	protected Integer addedAndDeletedLinesSum = 0;
	protected Boolean entityWasDeleted = false;
	protected Integer numberOfChanges = 0;
	protected String entityCreator;
	protected LinkedHashMap<String, Integer> authorsNumberOfChanges;
	protected LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines;
	protected LinkedHashMap<String, Double> ownershipPercentages;
	protected ArrayList<String> allOwners;
	protected ArrayList<String> distinctOwners;

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

	public Integer getAddedAndDeletedLinesSum() {
		return addedAndDeletedLinesSum;
	}

	public void setAddedAndDeletedLinesSum(
			final Integer addedAndDeletedLinesSum) {
		this.addedAndDeletedLinesSum = addedAndDeletedLinesSum;
	}

	public Boolean getEntityWasDeleted() {
		return entityWasDeleted;
	}

	public void setEntityWasDeleted(final Boolean entityWasDeleted) {
		this.entityWasDeleted = entityWasDeleted;
	}

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(final Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	public String getEntityCreator() {
		return entityCreator;
	}

	public void setEntityCreator(final String entityCreator) {
		this.entityCreator = entityCreator;
	}

	public LinkedHashMap<String, Integer> getAuthorsNumberOfChanges() {
		return authorsNumberOfChanges;
	}

	public void setAuthorsNumberOfChanges(
			final LinkedHashMap<String, Integer> authorsNumberOfChanges) {
		this.authorsNumberOfChanges = authorsNumberOfChanges;
	}

	public LinkedHashMap<String, List<Integer>> getAuthorsAddedAndDeletedLines() {
		return authorsAddedAndDeletedLines;
	}

	public void setAuthorsAddedAndDeletedLines(
			final LinkedHashMap<String, List<Integer>> authorsAddedAndDeletedLines) {
		this.authorsAddedAndDeletedLines = authorsAddedAndDeletedLines;
	}

	public LinkedHashMap<String, Double> getOwnershipPercentages() {
		return ownershipPercentages;
	}

	public void setOwnershipPercentages(
			final LinkedHashMap<String, Double> ownershipPercentages) {
		this.ownershipPercentages = ownershipPercentages;
	}

	public ArrayList<String> getAllOwners() {
		return allOwners;
	}

	public ArrayList<String> getDistinctOwners() {
		return distinctOwners;
	}

	public void setDistinctOwners(final ArrayList<String> distinctOwners) {
		this.distinctOwners = distinctOwners;
	}

	public void setAllOwners(final ArrayList<String> allOwners) {
		this.allOwners = allOwners;
	}

	@Override
	public String toString() {
		return "FileChangesData [commits=" + commits + ", fileName=" + fileName
				+ ", initialSize=" + initialSize + ", actualSize=" + actualSize
				+ ", addedAndDeletedLinesSum=" + addedAndDeletedLinesSum
				+ ", entityWasDeleted=" + entityWasDeleted
				+ ", numberOfChanges=" + numberOfChanges + ", entityCreator="
				+ entityCreator + ", authorsNumberOfChanges="
				+ authorsNumberOfChanges + ", authorsAddedAndDeletedLines="
				+ authorsAddedAndDeletedLines + ", ownershipPercentages="
				+ ownershipPercentages + ", allOwners=" + allOwners
				+ ", distinctOwners=" + distinctOwners + "]";
	}

}
