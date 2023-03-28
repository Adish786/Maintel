package com.about.mantle.model.extended.docv2.sc;

/**
 * Parent class for all structured content data.  IE all ____ContentData schema definitions.
 */
public abstract class AbstractStructuredContentDataEx {
	
	//The following are currently a temporary solution for the issue of nested list items. Will likely be replace
	//by a unique grouping block at a later date. See GLBE-5893
	private String stepType; 
	private Boolean isLastStep; 
	private Boolean isLastBlockOfLastStep;
	private String theme;
	
	public Boolean getIsLastStep() {
  		return isLastStep;
  	}

  	public void setIsLastStep(Boolean isLastStep) {
  		this.isLastStep = isLastStep;
  	}

  	public String getStepType() {
  		return stepType;
  	}

  	public void setStepType(String stepType) {
  		this.stepType = stepType;
  	}

	public Boolean getIsLastBlockOfLastStep() {
		return isLastBlockOfLastStep;
	}

	public void setIsLastBlockOfLastStep(Boolean isLastBlockOfLastStep) {
		this.isLastBlockOfLastStep = isLastBlockOfLastStep;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Override
	public String toString() {
		return "AbstractStructuredContentDataEx{" +
				"stepType='" + stepType + '\'' +
				", isLastStep=" + isLastStep +
				", isLastBlockOfLastStep=" + isLastBlockOfLastStep +
				", theme='" + theme + '\'' +
				'}';
	}
}
