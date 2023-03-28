package com.about.mantle.model.extended;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeionRangeFacet implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private final List<Count> counts = new ArrayList<>();


	public DeionRangeFacet (){

	}

	public void addCount(String value, int count) {
		this.counts.add(new DeionRangeFacet.Count(value, count));
	}


	public List<DeionRangeFacet.Count> getCounts() {
		return this.counts;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static class Count {
		private  String value;
		private  int count;

		public Count(){
			
		}
		public Count(String value, int count) {
			this.setValue(value);
			this.setCount(count);

		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
	}
}