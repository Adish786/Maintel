package com.about.mantle.model.journey;

import java.io.Serializable;
import java.util.List;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Wrapper around a journey "section", which is a tree of Journey Documents.
 * The 'document' field returns the header for this section.
 */
public class JourneySection extends AbstractJourneyNode implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private final List<JourneyDocument> journeyDocuments;

	private JourneyRoot journeyRoot;

	@JsonCreator
	public JourneySection(@JsonProperty("node") TaxeneNodeEx node) {
		this(node, createJourneyDocumentList(node));
	}
	
	public JourneySection(TaxeneNodeEx node, List<JourneyDocument> journeyDocuments) {
		super(node);
		this.journeyDocuments = journeyDocuments;
	}

	@JsonIgnore
	public List<JourneyDocument> getJourneyDocuments() {
		return journeyDocuments;
	}

	private static List<JourneyDocument> createJourneyDocumentList(TaxeneNodeEx node) {
		if (node == null) return ImmutableList.of();

		Builder<JourneyDocument> builder = ImmutableList.builder();
		if (node.getRelationships() != null && !node.getRelationships().isEmpty()) {
			// Add documents to each programmed summary section
			node.getRelationships().getList().forEach(psRelationship -> {
				builder.add(new JourneyDocument(psRelationship.getTargetNode()));
			});
		}
		return builder.build();
	}
}
