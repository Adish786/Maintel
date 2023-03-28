package com.about.mantle.model.services.rtb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class RtbPartner {

	private static final Logger logger = LoggerFactory.getLogger(RtbPartner.class);

	/**
	 * Partners must have a unique name.
	 */
	abstract public String getName();

	/**
	 * Partners must populate the builder provided with the bid results.
	 */
	abstract protected RtbResult.Builder getBids(RtbRequestContext ctx, RtbResult.Builder builder);

	final public RtbResult getBids(RtbRequestContext ctx) {
		logger.debug("Getting bids for [{}]", getName());
		final long start = System.currentTimeMillis();
		RtbResult.Builder builder = getBids(ctx, new RtbResult.Builder());
		builder.timing(System.currentTimeMillis() - start);
		logger.debug("Returning bids for [{}]", getName());
		return builder.build();
	}

}
