package com.about.mantle.model.disqus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DisqusAuthor {
	
	private String name;
	
	// threadRating is per author and not per thread. So an author can have multiple
	// comments but only one rating
	private Integer threadRating;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getThreadRating() {
		return threadRating;
	}

	public void setThreadRating(Integer threadRating) {
		this.threadRating = threadRating;
	}
}
