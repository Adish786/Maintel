package com.about.mantle.infocat.property;

import java.io.Serializable;
import org.apache.commons.lang3.tuple.Pair;

public class MeasurementProperty extends Property<Pair<Long,String>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private MeasurementPropertyValue value;
	
	@Override
	public MeasurementPropertyValue getValue() {
		return value;
	}
	
	public void setValue(MeasurementPropertyValue value) {
		this.value = value;
	}
	
	public class MeasurementPropertyValue extends PropertyValue<Pair<Long,String>> {
		
        private Long measurementValue;
	    private String uom;

		public Long getMeasurementValue() {
			return measurementValue;
		}

		public void setMeasurementValue(Long measurementValue) {
			this.measurementValue = measurementValue;
		}

		public String getUOM() {
			return uom;
		}

		public void setUOM(String uom) {
			this.uom = uom;
		}

		@Override
	    public Pair<Long,String> getPrimaryValue() {
	    	return Pair.of(measurementValue, uom);
	    }

		@Override
		public boolean isEmpty() {
			return getMeasurementValue() == null;
		}
	}
}