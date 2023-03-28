package com.about.mantle.model.extended.quiz;

import java.io.Serializable;

public class DimensionEx implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String key;
	private int min;
	private int max;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	
}
