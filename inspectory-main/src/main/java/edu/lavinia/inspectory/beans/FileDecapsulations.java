package edu.lavinia.inspectory.beans;

import java.util.List;

public class FileDecapsulations {

	private String file;
	private List<Field> fields;
	private String category;
	private String name;
	private Integer value;

	class Field {
		private String id;
		private List<Decapsulation> decapsulations;
		private Integer value;

		public String getId() {
			return id;
		}

		public void setId(final String id) {
			this.id = id;
		}

		public List<Decapsulation> getDecapsulations() {
			return decapsulations;
		}

		public void setDecapsulations(
				final List<Decapsulation> decapsulations) {
			this.decapsulations = decapsulations;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(final Integer value) {
			this.value = value;
		}

	}

	class Decapsulation {
		private String fieldId;
		private String sourceNodeId;
		private String revisionId;
		private String message;

		public String getFieldId() {
			return fieldId;
		}

		public void setFieldId(final String fieldId) {
			this.fieldId = fieldId;
		}

		public String getSourceNodeId() {
			return sourceNodeId;
		}

		public void setSourceNodeId(final String sourceNodeId) {
			this.sourceNodeId = sourceNodeId;
		}

		public String getRevisionId() {
			return revisionId;
		}

		public void setRevisionId(final String revisionId) {
			this.revisionId = revisionId;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(final String message) {
			this.message = message;
		}

	}

	public String getFile() {
		return file;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(final List<Field> fields) {
		this.fields = fields;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(final Integer value) {
		this.value = value;
	}

}
