package com.about.mantle.model.seo;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryParamSafelist {
	private final Set<String> equalsSet;
	private final Set<String> startsWithSet;

	@JsonCreator
	public QueryParamSafelist(@JsonProperty("equalsSet") Set<String> equalsSet, @JsonProperty("startsWithSet") Set<String> startsWithSet) {
		this.equalsSet = equalsSet;
		this.startsWithSet = startsWithSet;
	}

	public Set<String> getEqualsSet() {
		return equalsSet;
	}

	public Set<String> getStartsWithSet() {
		return startsWithSet;
	}

}
