package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This block represents sc block unknown to Globe. This will let Globe continue
 * deserializing document having unknown sc blocks from Selene.
 */
public class StructuredContentUnknownEx
		extends AbstractStructuredContentContentEx<StructuredContentUnknownEx.StructuredContentUnknownDataEx> {

	private static final String UNKNOWN = "unknown";
	/**
	 *  This is used to store type coming from first deserilization attempt i.e. Selene -> Java Object.
	 *  setType gets called which internally stores value for actualType. Note that setActualType won't be called as Selene data doesn't have actualType in it.  
	 *  Next, we try to serialize this Java Object into Json and then store that in redis.During this, Jackson calls getters in this class - getActualType, getType
	 *  and thus stores "actualValue" : "original type" and "type" : "unkown" . This is done because we wanted getType to return "unknown" always. 
	 */
	private String actualType;

	public String getActualType() {
		return actualType;
	}

	public void setActualType(String actualType) {
		this.actualType = actualType;
	}

	@Override
	public String getType() {
		return UNKNOWN;
	}

	@Override
	public void setType(String type) {
		if (!UNKNOWN.equals(type)) {
			actualType = type;
		}
		super.setType(type);
	}

	//skip it from deserialization attempts 
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class StructuredContentUnknownDataEx extends AbstractStructuredContentDataEx {
		// nothing about data here
	}

}
