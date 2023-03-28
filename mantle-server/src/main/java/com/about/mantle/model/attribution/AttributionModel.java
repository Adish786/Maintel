package com.about.mantle.model.attribution;

import com.about.mantle.model.extended.BaseAuthor;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.docv2.BioDocumentEx;

import java.util.Objects;

/**
 * View Model for Attribution / Author pair. This is used for tasks to avoid modifying either the
 * author or attribution objects before they may be asynchronously pushed to redis
 */
public class AttributionModel {

	private final Attribution attribution;
	private final BaseAuthor author;
	private final BioDocumentEx bioDocument;

	private AttributionModel(Builder builder) {
		this.attribution = builder.attribution;
		this.author = builder.author;
		this.bioDocument = builder.bioDocument;
	}
	
	public Attribution getAttribution() {
		return attribution;
	}
	
	public BaseAuthor getAuthor() {
		return author;
	}

	public BioDocumentEx getBioDocument() {
		return bioDocument;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		Attribution attribution;
		BaseAuthor author;
		BioDocumentEx bioDocument;

		public Builder attribution(Attribution attribution) {
			this.attribution = attribution;
			return this;
		}

		public Builder author(BaseAuthor author) {
			this.author = author;
			return this;
		}

		public Builder bioDocument(BioDocumentEx bioDocument) {
			this.bioDocument = bioDocument;
			return this;
		}
		
		public AttributionModel build() {
			return new AttributionModel(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AttributionModel that = (AttributionModel) o;
		return Objects.equals(attribution, that.attribution);
	}

	@Override
	public int hashCode() {
		return Objects.hash(attribution);
	}

}
