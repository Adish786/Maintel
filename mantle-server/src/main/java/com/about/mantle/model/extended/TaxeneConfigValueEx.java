package com.about.mantle.model.extended;

import java.io.Serializable;

public class TaxeneConfigValueEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof TaxeneConfigValueEx)) return false;
		TaxeneConfigValueEx other = (TaxeneConfigValueEx) obj;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}
}
