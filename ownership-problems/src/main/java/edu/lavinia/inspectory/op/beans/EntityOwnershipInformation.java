/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
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
package edu.lavinia.inspectory.op.beans;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class EntityOwnershipInformation {
	private Integer totalNumberOfChanges;
	private String entityCreator;
	private LinkedHashMap<String, Integer> authorsNumberOfChanges;
	private LinkedHashMap<String, List<Integer>> authorsNumberOfAddedAndDeletedLines;
	private LinkedHashMap<String, Double> ownershipPercentages;

	public Integer getNumberOfChanges() {
		return totalNumberOfChanges;
	}

	public Integer getTotalNumberOfChanges() {
		return totalNumberOfChanges;
	}

	public void setTotalNumberOfChanges(Integer totalNumberOfChanges) {
		this.totalNumberOfChanges = totalNumberOfChanges;
	}

	public LinkedHashMap<String, List<Integer>> getAuthorsNumberOfAddedAndDeletedLines() {
		return authorsNumberOfAddedAndDeletedLines;
	}

	public void setAuthorsNumberOfAddedAndDeletedLines(
			LinkedHashMap<String, List<Integer>> authorsNumberOfAddedAndDeletedLines) {
		this.authorsNumberOfAddedAndDeletedLines = authorsNumberOfAddedAndDeletedLines;
	}

	public void setNumberOfChanges(Integer numberOfChanges) {
		this.totalNumberOfChanges = numberOfChanges;
	}

	public String getEntityCreator() {
		return entityCreator;
	}

	public void setEntityCreator(String entityCreator) {
		this.entityCreator = entityCreator;
	}

	public LinkedHashMap<String, Integer> getAuthorsNumberOfChanges() {
		return authorsNumberOfChanges;
	}

	public void setAuthorsNumberOfChanges(
			LinkedHashMap<String, Integer> authorsNumberOfChanges) {
		this.authorsNumberOfChanges = authorsNumberOfChanges;
	}

	public LinkedHashMap<String, Double> getOwnershipPercentages() {
		return ownershipPercentages;
	}

	public void setOwnershipPercentages(
			LinkedHashMap<String, Double> ownershipPercentages) {
		this.ownershipPercentages = ownershipPercentages;
	}

	@Override
	public String toString() {
		return "EntityOwnershipInformation [numberOfChanges="
				+ totalNumberOfChanges + ", fileCreator=" + entityCreator
				+ ", authorsChanges=" + authorsNumberOfChanges
				+ ", authorsLineChanges=" + authorsNumberOfAddedAndDeletedLines
				+ ", ownershipPercentages=" + ownershipPercentages + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof EntityOwnershipInformation)) {
			return false;
		}

		final EntityOwnershipInformation fileOwnershipInformation = (EntityOwnershipInformation) obj;
		return totalNumberOfChanges == fileOwnershipInformation.totalNumberOfChanges
				&& Objects.equals(entityCreator,
						fileOwnershipInformation.entityCreator)
				&& Objects.equals(authorsNumberOfChanges,
						fileOwnershipInformation.authorsNumberOfChanges)
				&& Objects.equals(authorsNumberOfAddedAndDeletedLines,
						fileOwnershipInformation.authorsNumberOfAddedAndDeletedLines)
				&& Objects.equals(ownershipPercentages,
						fileOwnershipInformation.ownershipPercentages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(totalNumberOfChanges, entityCreator,
				authorsNumberOfChanges, authorsNumberOfAddedAndDeletedLines,
				ownershipPercentages);
	}
}
