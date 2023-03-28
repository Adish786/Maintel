package com.about.mantle.model.services.rtb;

import java.util.HashSet;
import java.util.Set;

public class RtbRequestContext {

	private final Set<String> slots;
	private final Set<String> partners;
	private final Long timeout;

	private RtbRequestContext(RtbRequestContext.Builder builder) {
		this.slots = builder.slots;
		this.partners = builder.partners;
		this.timeout = builder.timeout;
	}

	public Set<String> getSlots() {
		return slots;
	}

	public Set<String> getPartners() {
		return partners;
	}

	public Long getTimeout() {
		return timeout;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RtbRequestContext [slots=").append(slots).append(", partners=").append(partners)
				.append(", timeout=").append(timeout).append("]");
		return sb.toString();
	}

	public static class Builder {
		private Set<String> slots = new HashSet<>();
		private Set<String> partners = new HashSet<>();
		private Long timeout = null;

		public RtbRequestContext.Builder slots(String...slots) {
			for (String slot : slots) {
				this.slots.add(slot);
			}
			return this;
		}

		public RtbRequestContext.Builder partners(String...partners) {
			for (String partner : partners) {
				this.partners.add(partner);
			}
			return this;
		}

		public RtbRequestContext.Builder timeout(Long timeout) {
			this.timeout = timeout;
			return this;
		}

		public RtbRequestContext build() {
			return new RtbRequestContext(this);
		}
	}

}
