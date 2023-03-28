package com.about.mantle.model.extended.docv2.sc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class MaterialGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	private String heading;
	private String subHeading;
	private SliceableListEx<MaterialEx> materials;

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	public SliceableListEx<MaterialEx> getMaterials() {
		return materials;
	}

	public void setMaterials(SliceableListEx<MaterialEx> materials) {
		this.materials = materials;
	}
}
