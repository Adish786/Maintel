package com.about.mantle.model.extended.docv2.brightcovevideo;

import java.io.Serializable;

public class ScheduleEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long start;
	private Long end;

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}
}
