package com.about.mantle.model.journey;

import java.io.Serializable;
import java.util.List;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Represents a root taxene journey node as well as all of its children sections / nodes
 */
public class JourneyRoot extends AbstractJourneyNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private final List<JourneySection> sections;

	@JsonCreator
	public JourneyRoot(@JsonProperty("node") TaxeneNodeEx node) {
		this(node, createJourneySectionList(node));
	}

	public JourneyRoot(TaxeneNodeEx node, List<JourneySection> sections) {
		super(node);
		this.sections = sections;
	}

	/**
	 * Ordered list of all of the sections in this journey
	 * @return
	 */
	@JsonIgnore
	public List<JourneySection> getSections() {
		return sections;
	}

	private static List<JourneySection> createJourneySectionList(TaxeneNodeEx node) {
		if (node == null) return ImmutableList.of();

		Builder<JourneySection> builder = ImmutableList.builder();
		if (node.getRelationships() != null && !node.getRelationships().isEmpty()) {
			// Create programmed summary sections
			node.getRelationships().getList().forEach(relationship -> {
				// Relationship name will always be 'journey'
				if (relationship.getTargetNode() != null) {
					builder.add(new JourneySection(relationship.getTargetNode()));
				}
			});
		}
		return builder.build();
	}

}
