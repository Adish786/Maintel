package com.about.mantle.model.extended.docv2;

public class LegacyDocumentEx extends BaseDocumentEx {

	private static final long serialVersionUID = 1L;
	private String binary;
	private SliceableListEx<String> labels = SliceableListEx.emptyList();

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public SliceableListEx<String> getLabels() {
		return labels;
	}

	public void setLabels(SliceableListEx<String> labels) {
		this.labels = SliceableListEx.emptyIfNull(labels);
	}

}
