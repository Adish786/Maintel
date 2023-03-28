package com.about.mantle.model.attributedef.value;

public class AttributePeriodValueEx extends AbstractAttributeValueEx {
	
	private static final long serialVersionUID = 1L;

	private PeriodValue value;

	public AttributePeriodValueEx() {
		setType(AttributeValueTypeEx.PERIOD.toString());
	}

	@Override
	public PeriodValue getValue() {
		return value;
	}

	public void setValue(PeriodValue value) {
		this.value = value;
	}

	public static class PeriodValue {
		private PeriodUnit unit;
		private Integer value;

		public PeriodUnit getUnit() {
			return unit;
		}

		public void setUnit(PeriodUnit unit) {
			this.unit = unit;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
		}
	}

	public enum PeriodUnit {
		YEAR,
		MONTH,
		DAY
	}

}
