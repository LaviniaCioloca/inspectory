package org.lavinia.visitor;

import org.apache.log4j.Logger;

public abstract class GenericVisitor {
	public Logger logger = null;
	protected String fileName = null;
	protected String identifier = null;
	protected Integer total = 0;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

}
