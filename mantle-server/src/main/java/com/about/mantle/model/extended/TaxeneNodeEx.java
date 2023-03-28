package com.about.mantle.model.extended;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TaxonomyDocumentEx;
import com.about.mantle.model.extended.docv2.sc.TaxonomyStructuredContentDocumentEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;

public class TaxeneNodeEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long docId;
	private Map<String, String> nodeAttributes = ImmutableMap.of();
	private TaxeneNodeTypeEx nodeType;
	private BaseDocumentEx document;
	private Map<String, TaxeneConfigValueEx> configuration = ImmutableMap.of();
	private List<TaxeneNodeEx> descendantRelationDocuments = ImmutableList.of();
	private SliceableListEx<TaxeneRelationshipEx> relationships = SliceableListEx.emptyList();

	public TaxeneNodeEx() {
	}

	private TaxeneNodeEx(TaxeneNodeEx node) {
		this.docId = node.getDocId();
		this.nodeAttributes = node.getNodeAttributes();
		this.nodeType = node.getNodeType();
		this.document = node.getDocument();
		this.descendantRelationDocuments = node.getDescendantRelationDocuments();
		this.relationships = node.getRelationships();
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Map<String, String> getNodeAttributes() {
		return nodeAttributes;
	}

	public void setNodeAttributes(Map<String, String> nodeAttributes) {
		this.nodeAttributes = nodeAttributes;
	}

	public TaxeneNodeTypeEx getNodeType() {
		return nodeType;
	}

	public void setNodeType(TaxeneNodeTypeEx nodeType) {
		this.nodeType = nodeType;
	}

	@JsonIgnore
	public TaxeneNodeEx getPrimaryParent() {
		return TaxeneNodeEx.findFirstRelationOf("primaryParent", relationships).orElse(null);
	}

	public BaseDocumentEx getDocument() {
		return document;
	}

	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}

	@JsonIgnore
	public SliceableListEx<TaxeneNodeEx> getChildren() {
		return SliceableListEx.of(TaxeneNodeEx.findAllRelationsOf("primaryParent", relationships));
	}

	public Map<String, TaxeneConfigValueEx> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, TaxeneConfigValueEx> configuration) {
		this.configuration = configuration;
	}

	public SliceableListEx<TaxeneRelationshipEx> getRelationships() {
		return relationships;
	}
	
	/**
	 * This method returns the first instance of a given relationship in a list of relationships
	 * 
	 * @param relationship
	 *            a String
	 * @param relationships
	 *            a {@link SliceableListEx} of {@link TaxeneRelationshipEx} object
	 * @return the first instance of a given relationship name
	 */
	public static Optional<TaxeneRelationshipEx> findFirstRelationshipOf(String relationship, SliceableListEx<TaxeneRelationshipEx> relationships) {
		return SliceableListEx.isEmpty(relationships) ? Optional.empty() : relationships.stream().filter(r -> r.getName().equals(relationship)).findFirst();
	}
	
	/**
	 * This method returns target node of the first instance of a given relationship in a list of relationships
	 * 
	 * @param relationship
	 *            a String
	 * @param relationships
	 *            a {@link SliceableListEx} of {@link TaxeneRelationshipEx}
	 * @return the first instance of a node with a given relationship name
	 */
	public static Optional<TaxeneNodeEx> findFirstRelationOf(String relationship, SliceableListEx<TaxeneRelationshipEx> relationships) {
		 Optional<TaxeneRelationshipEx> r = findFirstRelationshipOf(relationship, relationships);
		 return r.isPresent() ? Optional.of(r.get().getTargetNode()) : Optional.empty();
	}
	
	/**
	 * This method returns all target nodes of a relationship in a list of relationships
	 *
	 * @param relationship
	 *            a String
	 * @param relationships
	 *            a {@link SliceableListEx} of {@link TaxeneRelationshipEx}
	 * @return a list of the target nodes from a relationship list
	 */
	public static List<TaxeneNodeEx> findAllRelationsOf(String relationship, SliceableListEx<TaxeneRelationshipEx> relationships) {
		return SliceableListEx.isEmpty(relationships) ? ImmutableList.of() : relationships.stream().filter(r -> r.getName().equals(relationship)).map(r -> r.getTargetNode()).collect(Collectors.toList());
	}
	
	/**
	 * This method returns all target nodes in a list of relationships
	 * 
	 * @param relationships
	 *            a {@link SliceableListEx} of {@link TaxeneRelationshipEx}
	 * @return a list of the target nodes from a relationship list
	 */
	public static List<TaxeneNodeEx> fetchAllRelations(SliceableListEx<TaxeneRelationshipEx> relationships) {
		return SliceableListEx.isEmpty(relationships) ? ImmutableList.of() : relationships.stream().map(r -> r.getTargetNode()).collect(Collectors.toList());
	}
	
	

	public void setRelationships(SliceableListEx<TaxeneRelationshipEx> relationships) {
		this.relationships = relationships;
		if (this.relationships == null) return;
		Builder<TaxeneNodeEx> builder = ImmutableList.builder();
		for (TaxeneRelationshipEx relation : this.relationships.getList()) {
			if (relation.getTargetNode().getDocument() != null && !relation.getTargetNode().isTaxonomyDocument()) {
				builder.add(relation.getTargetNode());
			}
		}
		for (TaxeneRelationshipEx relation : this.relationships.getList()) {
			if (!SliceableListEx.isEmpty(relation.getTargetNode().getRelationships())) {
				builder.addAll(relation.getTargetNode().getDescendantRelationDocuments());
			}
		}
		this.descendantRelationDocuments = builder.build();
	}
	@JsonIgnore
	public List<TaxeneNodeEx> getDescendantRelationDocuments() {
		return descendantRelationDocuments;
	}

	/**
	 * This method returns the value of the given {@link NodeAttribute} key
	 * 
	 * @param nodeAttribute
	 *            a {@link NodeAttribute} object
	 * @return the value of the given node attribute
	 */
	@JsonIgnore
	public String getNodeAttributeValue(NodeAttribute nodeAttribute) {
		if (nodeAttribute == null) return null;

		return nodeAttributes.get(nodeAttribute.key());
	}

	@JsonIgnore
	public boolean isTaxonomyDocument() {
		return document instanceof TaxonomyDocumentEx || document instanceof TaxonomyStructuredContentDocumentEx;
	}

	public static TaxeneNodeEx copy(TaxeneNodeEx node) {
		return node == null ? null : new TaxeneNodeEx(node);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("TaxeneNodeEx{");
		sb.append("docId=").append(docId);
		sb.append('}');
		return sb.toString();
	}
}
