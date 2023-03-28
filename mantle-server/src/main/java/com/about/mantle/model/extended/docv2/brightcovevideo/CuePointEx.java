package com.about.mantle.model.extended.docv2.brightcovevideo;

import java.io.Serializable;

public class CuePointEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String id;
	private Double time;
	private String type;
	private Boolean forceStop;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getForceStop() {
		return forceStop;
	}

	public void setForceStop(Boolean forceStop) {
		this.forceStop = forceStop;
	}
}
