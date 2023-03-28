package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.UUID;

public class MultiselectValueDefinition implements Serializable {
	private String value;
	private Boolean isSelected = false;
	private UUID id;

	public MultiselectValueDefinition() {
	}
	
	public MultiselectValueDefinition(UUID id, String value) {
		this.id = id;
		this.value = value;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}
}
