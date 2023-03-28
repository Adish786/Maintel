package com.about.mantle.model.gtm;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.about.mantle.model.extended.TaxeneNodeEx;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaxonomyNodes {

	private static final ObjectMapper mapper = new ObjectMapper();

	private final List<List<Map<String, Object>>> nodes;
	
	private List<List<TaxeneNodeEx>> taxeneNodes = Collections.emptyList();

	public TaxonomyNodes() {
		this.nodes = Collections.emptyList();
	}
	
	public List<List<TaxeneNodeEx>> getTaxeneNodes(){
		return taxeneNodes;
	}

	@JsonCreator
	public TaxonomyNodes(@JsonProperty("taxeneNodes") List<TaxeneNodeEx> taxonomyNodes) {
		this.nodes = new LinkedList<>();
		List<Map<String, Object>> listMap = taxonomyNodes.stream()
				.filter(node -> node.isTaxonomyDocument()).map(node -> {
					Map<String, Object> properties = new HashMap<String, Object>();
					properties.put("shortName", node.getDocument().getShortHeading());
					properties.put("documentId", node.getDocument().getDocumentId());
					return properties;
				}).collect(Collectors.toList());
		this.nodes.add(listMap);
	}

	@Override
	public String toString() {
		try {
			return TaxonomyNodes.mapper.writeValueAsString(this.nodes);
		} catch (JsonProcessingException e) {
			return "[]";
		}
	}

}
