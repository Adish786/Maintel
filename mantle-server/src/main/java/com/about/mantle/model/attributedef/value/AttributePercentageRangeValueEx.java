package com.about.mantle.model.attributedef.value;

public class AttributePercentageRangeValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private PercentageRangeValue value;

	public AttributePercentageRangeValueEx() {
		setType(AttributeValueTypeEx.PERCENTAGE_RANGE.toString());
	}

	@Override
	public PercentageRangeValue getValue() {
		return value;
	}

	public void setValue(PercentageRangeValue value) {
		this.value = value;
	}

	public static class PercentageRangeValue {
		private Float min;
		private Float max;

		public Float getMin() {
			return min;
		}

		public void setMin(Float min) {
			this.min = min;
		}

		public Float getMax() {
			return max;
		}

		public void setMax(Float max) {
			this.max = max;
		}
	}
}
