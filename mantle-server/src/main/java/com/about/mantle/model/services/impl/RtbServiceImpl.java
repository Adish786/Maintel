package com.about.mantle.model.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.services.RtbService;
import com.about.mantle.model.services.rtb.RtbPartner;
import com.about.mantle.model.services.rtb.RtbRequestContext;
import com.about.mantle.model.services.rtb.RtbResult;

public class RtbServiceImpl implements RtbService {

	private static final Logger logger = LoggerFactory.getLogger(RtbServiceImpl.class);
	private static final long DEFAULT_TIMEOUT = 2000; // milliseconds

	private final ExecutorService executorService;
	private final Map<String, RtbPartner> partners;

	public RtbServiceImpl(ExecutorService executorService, List<RtbPartner> partners) {
		this.executorService = executorService;
		this.partners = partners.stream().collect(Collectors.toMap(RtbPartner::getName, Function.identity(), (partner1, partner2) -> {
			throw new GlobeException("Found more than one RTB partner called " + partner1);
		}));
	}

	@Override
	public Map<String, RtbResult> getBids(RtbRequestContext ctx) {
		final long timeout = Math.max(0, ctx.getTimeout() != null ? ctx.getTimeout() : DEFAULT_TIMEOUT);
		final long deadline = System.currentTimeMillis() + timeout;

		Map<String, Future<RtbResult>> futures = new HashMap<>();
		for (String partnerName : ctx.getPartners()) {
			futures.put(partnerName, getBidsAsync(partnerName, ctx));
		}

		Map<String, RtbResult> result = new HashMap<>();
		for (String partnerName : ctx.getPartners()) {
			Future<RtbResult> future = futures.get(partnerName);
			RtbResult rtbResult = null;
			try {
				rtbResult = future.get(Math.max(0, deadline - System.currentTimeMillis()), TimeUnit.MILLISECONDS);
			} catch (InterruptedException | TimeoutException e) {
				future.cancel(true);
				rtbResult = RtbResult.fromError(partnerName, "was interrupted or timed out");
			} catch (ExecutionException e) {
				Throwable t = e.getCause();
				rtbResult = RtbResult.fromError(partnerName, "failed due to error: " + t.getMessage());
			}
			if (StringUtils.isNotBlank(rtbResult.getError())) {
				logger.error(rtbResult.getError());
			}
			result.put(partnerName, rtbResult);
		}
		return result;
	}

	private Future<RtbResult> getBidsAsync(String partnerName, RtbRequestContext ctx) {
		final RtbPartner rtbPartner = partners.get(partnerName);
		if (rtbPartner == null) {
			return CompletableFuture.completedFuture(RtbResult.fromError(partnerName, "does not exist"));
		} else {
			return executorService.submit(() -> rtbPartner.getBids(ctx));
		}
	}

}
