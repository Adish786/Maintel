package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class FocalPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer x;
	private Integer y;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
}
