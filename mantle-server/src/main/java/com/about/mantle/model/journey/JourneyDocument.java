package com.about.mantle.model.journey;

import java.io.Serializable;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A leaf journey document.
 */
public class JourneyDocument extends AbstractJourneyNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonCreator
	public JourneyDocument(@JsonProperty("node") TaxeneNodeEx node) {
		super(node);
	}

}
