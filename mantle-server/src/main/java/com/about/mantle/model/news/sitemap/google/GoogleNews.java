package com.about.mantle.model.news.sitemap.google;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GoogleNews {

	private static final String NEW_YORK_TIMEZONE_ID = "America/New_York";
	private DateTime publicationDate;
	private DateTime lastModDate;
	private String title;
	private Publication publication;

	public GoogleNews(DateTime publicationDate, String title, Publication publication, DateTime lastModDate) {
		this.publicationDate = publicationDate;
		this.title = title;
		this.publication = publication;
		this.lastModDate = lastModDate;
	}

	public DateTime getLastModDate() {
		return lastModDate;
	}
	
	@JsonIgnore
	public DateTime getLastModDateInNewYorkTimeZone() {
		return lastModDate.withZone(DateTimeZone.forID(NEW_YORK_TIMEZONE_ID));
	}

	public DateTime getPublicationDate() {
		return publicationDate;
	}

	@JsonIgnore
	public DateTime getPublicationDateInNewYorkTimeZone() {
		return publicationDate.withZone(DateTimeZone.forID(NEW_YORK_TIMEZONE_ID));
	}

	public String getTitle() {
		return title;
	}

	public Publication getPublication() {
		return publication;
	}

	@Override
	public String toString() {
		return "GoogleNews [publicationDate=" + publicationDate + ", title=" + title + ", publication=" + publication
				+ "]";
	}

	public static class Publication {

		private final String language;
		private final String name;

		public Publication(String name, String language) {
			this.language = language;
			this.name = name;
		}

		public String getLanguage() {
			return language;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Publication [language=" + language + ", name=" + name + "]";
		}
	}
}
