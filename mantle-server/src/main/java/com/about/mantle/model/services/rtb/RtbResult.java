package com.about.mantle.model.services.rtb;

import java.util.Map;

public class RtbResult {

	private final String error;
	private final Long timing;
	private final Map<String, Map<String, String>> slots;

	private RtbResult(RtbResult.Builder builder) {
		this.error = builder.error;
		this.timing = builder.timing;
		this.slots = builder.slots;
	}

	public String getError() {
		return error;
	}

	public Long getTiming() {
		return timing;
	}

	/**
	 * Get the key-vals for each slot as a map
	 * @return map: slot -> key -> value
	 */
	public Map<String, Map<String, String>> getSlots() {
		return slots;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RtbResult [error=").append(error).append(", timing=").append(timing).append(", slots=")
				.append(slots).append("]");
		return sb.toString();
	}

	public static RtbResult fromError(String partnerName, String errorMessage) {
		return new Builder().error("RTB partner [" + partnerName + "] " + errorMessage).build();
	}

	public static class Builder {
		private String error;
		private Long timing;
		private Map<String, Map<String, String>> slots;

		public RtbResult.Builder error(String error) {
			this.error = error;
			return this;
		}

		public RtbResult.Builder timing(Long timing) {
			this.timing = timing;
			return this;
		}

		public RtbResult.Builder slots(Map<String, Map<String, String>> slots) {
			this.slots = slots;
			return this;
		}

		public RtbResult build() {
			return new RtbResult(this);
		}
	}

}
