package com.about.mantle.model.gtm;

import static org.apache.commons.lang3.StringUtils.defaultString;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder=ABTest.Builder.class)
public class ABTest {

	private final String testName;
	private final String bucketDescription;
	private final String bucketName;
	private final String bucketTrackingId;
	private final Integer bucketValue;

	private ABTest(Builder builder) {
		this.testName = defaultString(builder.testName, "");
		this.bucketDescription = defaultString(builder.bucketDescription, "");
		this.bucketName = defaultString(builder.bucketName, "");
		this.bucketTrackingId = defaultString(builder.bucketTrackingId, "");
		this.bucketValue = builder.bucketValue;
	}

	public String getTestName() {
		return testName;
	}

	public String getBucketDescription() {
		return bucketDescription;
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getBucketTrackingId() {
		return bucketTrackingId;
	}

	public Integer getBucketValue() {
		return bucketValue;
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		private String testName;
		private String bucketDescription;
		private String bucketName;
		private String bucketTrackingId;
		private Integer bucketValue;

		public Builder testName(String testName) {
			this.testName = testName;
			return this;
		}

		public Builder bucketDescription(String bucketDescription) {
			this.bucketDescription = bucketDescription;
			return this;
		}

		public Builder bucketName(String bucketName) {
			this.bucketName = bucketName;
			return this;
		}

		public Builder bucketTrackingId(String bucketTrackingId) {
			this.bucketTrackingId = bucketTrackingId;
			return this;
		}

		public Builder bucketValue(Integer bucketValue) {
			this.bucketValue = bucketValue;
			return this;
		}

		public ABTest build() {
			return new ABTest(this);
		}
	}
}
