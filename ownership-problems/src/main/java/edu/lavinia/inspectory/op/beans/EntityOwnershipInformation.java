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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class EntityOwnershipInformation {
	private Integer numberOfChanges;
	private String entityCreator;
	private LinkedHashMap<String, Integer> authorsChanges;
	private LinkedHashMap<String, ArrayList<Integer>> authorsLineChanges;
	private LinkedHashMap<String, Double> ownershipPercentages;

	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}

	public void setNumberOfChanges(Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}

	public String getEntityCreator() {
		return entityCreator;
	}

	public void setEntityCreator(String entityCreator) {
		this.entityCreator = entityCreator;
	}

	public LinkedHashMap<String, Integer> getAuthorsChanges() {
		return authorsChanges;
	}

	public void setAuthorsChanges(
			LinkedHashMap<String, Integer> authorsChanges) {
		this.authorsChanges = authorsChanges;
	}

	public LinkedHashMap<String, ArrayList<Integer>> getAuthorsLineChanges() {
		return authorsLineChanges;
	}

	public void setAuthorsLineChanges(
			LinkedHashMap<String, ArrayList<Integer>> authorsLineChanges) {
		this.authorsLineChanges = authorsLineChanges;
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
		return "EntityOwnershipInformation [numberOfChanges=" + numberOfChanges
				+ ", fileCreator=" + entityCreator + ", authorsChanges="
				+ authorsChanges + ", authorsLineChanges=" + authorsLineChanges
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
		return numberOfChanges == fileOwnershipInformation.numberOfChanges
				&& Objects.equals(entityCreator,
						fileOwnershipInformation.entityCreator)
				&& Objects.equals(authorsChanges,
						fileOwnershipInformation.authorsChanges)
				&& Objects.equals(authorsLineChanges,
						fileOwnershipInformation.authorsLineChanges)
				&& Objects.equals(ownershipPercentages,
						fileOwnershipInformation.ownershipPercentages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(numberOfChanges, entityCreator, authorsChanges,
				authorsLineChanges, ownershipPercentages);
	}
}
