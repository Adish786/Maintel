package com.about.mantle.model.pl;

import java.util.List;

public class PatternLibraryComponentCounts {
	int infoComponents;
	int totalComponents;
	List<String> unsupportedComponents;

	public PatternLibraryComponentCounts(int infoComponents, int totalComponents) {
		this.infoComponents = infoComponents;
		this.totalComponents = totalComponents;
	}

	public int getInfoComponents() {
		return infoComponents;
	}

	public void setInfoComponents(int infoComponents) {
		this.infoComponents = infoComponents;
	}

	public int getTotalComponents() {
		return totalComponents;
	}

	public void setTotalComponents(int totalComponents) {
		this.totalComponents = totalComponents;
	}

	public List<String> getUnsupportedComponents() {
		return unsupportedComponents;
	}

	public void setUnsupportedComponents(List<String> unsupportedComponents) {
		this.unsupportedComponents = unsupportedComponents;
	}
}
