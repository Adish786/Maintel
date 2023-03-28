package com.about.mantle.model.extended.docv2.sc;

/**
 * An association to an external data source.
 * e.g. type may be "drugs" and id would refer to a specific drug in the data source.
 */
public class EntityReferenceInfo {
	private String type;
	private String id;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "EntityReferenceInfo [id=" + id + ", type=" + type + "]";
	}
}
