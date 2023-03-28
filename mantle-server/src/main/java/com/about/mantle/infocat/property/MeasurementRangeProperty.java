package com.about.mantle.infocat.property;

import org.apache.commons.lang3.tuple.Triple;
import java.io.Serializable;

public class MeasurementRangeProperty extends Property<Triple<Long, Long, String>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private MeasurementRangePropertyValue value;
	
	@Override
	public MeasurementRangePropertyValue getValue() {
		return value;
	}
	
	public void setValue(MeasurementRangePropertyValue value) {
		this.value = value;
	}
	
	public class MeasurementRangePropertyValue extends PropertyValue<Triple<Long, Long, String>> {
		
        private Long measurementMinValue;
		private Long measurementMaxValue;

	    private String uom;  //Unit of Measure

		public Long getMeasurementMinValue() {
			return measurementMinValue;
		}

		public void setMeasurementMinValue(Long measurementMinValue) {
			this.measurementMinValue = measurementMinValue;
		}

		public Long getMeasurementMaxValue() {
			return measurementMaxValue;
		}

		public void setMeasurementMaxValue(Long measurementMaxValue) {
			this.measurementMaxValue = measurementMaxValue;
		}

		public String getUOM() {
			return uom;
		}

		public void setUOM(String uom) {
			this.uom = uom;
		}

		@Override
	    public Triple<Long, Long, String> getPrimaryValue() {
	    	return Triple.of(measurementMinValue, measurementMaxValue, uom);
	    }

		@Override
		public boolean isEmpty() {
			return getMeasurementMinValue() == null && getMeasurementMaxValue() == null;
		}
	}
}