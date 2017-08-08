package org.lavinia.versioning;

import java.util.Date;

public class Commit {
	private String revision;
	private Date date;
	private String author;

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Commit [revision=" + revision + ", date=" + date + ", author=" + author + "]";
	}

}
