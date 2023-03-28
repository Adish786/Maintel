package com.about.mantle.model.journey;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.about.mantle.model.extended.NodeAttribute;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper around a journey taxene node.  Provides easier access to what's needed from the Taxene node just for
 * journeys.
 */
public abstract class AbstractJourneyNode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String shortHeading;
	private final TaxeneNodeEx node;
	private final BaseDocumentEx document;

	@JsonCreator
	public AbstractJourneyNode(@JsonProperty("node") TaxeneNodeEx node) {
		this.node = node;
		if (this.node == null) {
			this.shortHeading = StringUtils.EMPTY;
			this.document = null;
		} else {
			String value = this.node.getNodeAttributeValue(NodeAttribute.SHORT_HEADING);
			this.shortHeading = value == null ? StringUtils.EMPTY : value;
			this.document = this.node.getDocument();
		}
	}

	/**
	 * The Taxene Node associated with this journey doc / tree
	 * @return
	 */
	public TaxeneNodeEx getNode() {
		return node;
	}

	/**
	 * The heading associated with this journey doc / tree
	 * @return
	 */
	public String getShortHeading() {
		return shortHeading;
	}

	/**
	 * The Selene document associated with this journey doc / tree.  If the instance represents a tree then this
	 * document will likely represent a Programmed Summary
	 * @return
	 */
	public BaseDocumentEx getDocument() {
		return document;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("JourneyBase{");
		sb.append("shortHeading='").append(shortHeading).append('\'');
		sb.append(", node=").append(node);
		sb.append(", document=").append(document == null ? null : document.getDocumentId());
		sb.append('}');
		return sb.toString();
	}
}
