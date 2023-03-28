package com.about.mantle.model.descriptive_taxonomy_terms;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class DescriptiveTaxonomyTermList implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Term> terms = Collections.emptyList();

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public static class Term implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id;
		private List<String> parents;
		private List<String> groups;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<String> getParents() {
			return parents;
		}

		public void setParents(List<String> parents) {
			this.parents = parents;
		}

		public List<String> getGroups() {
			return groups;
		}

		public void setGroups(List<String> groups) {
			this.groups = groups;
		}
	}
}
