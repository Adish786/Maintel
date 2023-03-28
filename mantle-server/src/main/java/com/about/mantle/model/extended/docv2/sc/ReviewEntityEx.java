package com.about.mantle.model.extended.docv2.sc;

import java.io.Serializable;

import com.about.mantle.model.product.BaseProductEx;

public class ReviewEntityEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private BaseProductEx data;

	public BaseProductEx getData() {
		return data;
	}

	public void setData(BaseProductEx data) {
		this.data = data;
	}
}
