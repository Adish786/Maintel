package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfo;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.AuctionFloorInfoListResponse;
import com.about.mantle.model.extended.responses.AuctionFloorInfoResponse;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.services.AuctionFloorService;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionFloorServiceImpl extends AbstractHttpServiceClient implements AuctionFloorService {

	private static final Logger logger = LoggerFactory.getLogger(AuctionFloorServiceImpl.class);

	private static final String SELENE_AUCTION_FLOOR_MAPPING_PATH = "/auction/floor/mapping";
	private static final String SELENE_AUCTION_FLOOR_SEARCH_PATH = "/auction/floor/search";

	private static final String DEFAULT_TIER = "L";
	private static final Set<String> SLOT_EXCLUSION_LIST = ImmutableSet.of(
			"bankingtable",
			"brokertable",
			"button",
			"cdtable",
			"checkingtable",
			"moneymarketstable",
			"mortgagetable",
			"native",
			"partnerlinks",
			"savingstable",
			"textlink",
			"textnote",
			"webbar"
		);

	private static final AuctionFloorInfo DEFAULT_AUCTION_FLOOR_INFO = defaultAuctionFloorInfo();
	private static AuctionFloorInfo defaultAuctionFloorInfo() {
		AuctionFloorInfo defaultAuctionFloorInfo = new AuctionFloorInfo();
		defaultAuctionFloorInfo.setFloor(50); // $0.50
		defaultAuctionFloorInfo.setId("DEFAULT");
		return defaultAuctionFloorInfo;
	}

	private static WebTarget setParam(WebTarget t, String name, String value) {
		// NOTE that all params are required or the endpoint will fail.
		// We are asked to provide a dummy value e.g. "unknown" when the value is missing.
		return t.queryParam(name, StringUtils.defaultIfBlank(value, "unknown"));
	}

	private final String vertical;

	public AuctionFloorServiceImpl(HttpServiceClientConfig httpServiceClientConfig, String vertical) {
		super(httpServiceClientConfig);
		this.vertical = vertical;
	}

	protected boolean isSlotExcluded(String slot) {
		// We employ a blocklist instead of a safelist because we want new ad types to be subject
		// to auction floor pricing automatically.
		for (String excludedSlot : SLOT_EXCLUSION_LIST) {
			// We check that the slot `startWith` the value instead of matches the value because there can be
			// more than one of each type of ad. For example, brokertable1, brokertable2, brokertable3, etc.
			if (slot.startsWith(excludedSlot)) {
				return true;
			}
		}
		return false;
	}

	private <T extends BaseResponse<?>> T getAuctionFloorByMapping(AuctionFloorRequestContext ctx, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_AUCTION_FLOOR_MAPPING_PATH);

		// No fastly in QA means no geoLocation code unless you manually set the fastly header.
		webTarget = setParam(webTarget, "geo", ctx.getGeoLocation());
		webTarget = setParam(webTarget, "vertical", vertical);
		webTarget = setParam(webTarget, "slot", ctx.getSlot());
		webTarget = setParam(webTarget, "tier", ctx.getTier() != null ? ctx.getTier() : DEFAULT_TIER);
		webTarget = setParam(webTarget, "os", ctx.getOperatingSystem());
		webTarget = setParam(webTarget, "deviceCategory", ctx.getDeviceCategory());

		T response = readResponse(webTarget, bindToTarget);

		return response;
	}

	private <T extends BaseResponse<?>> T getAuctionFloorListByMapping(AuctionFloorListRequestContext ctx, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_AUCTION_FLOOR_SEARCH_PATH);

		// No fastly in QA means no geoLocation code unless you manually set the fastly header.
		webTarget = setParam(webTarget, "geo", ctx.getGeoLocation());
		webTarget = setParam(webTarget, "vertical", ctx.getVertical());
		webTarget = setParam(webTarget, "tier", ctx.getTier() != null ? ctx.getTier() : DEFAULT_TIER);
		webTarget = setParam(webTarget, "os", ctx.getOperatingSystem());
		webTarget = setParam(webTarget, "deviceCategory", ctx.getDeviceCategory());

		T response = readResponse(webTarget, bindToTarget);

		return response;
	}

	private AuctionFloorInfo processResponse(AuctionFloorRequestContext ctx, AuctionFloorInfoResponse auctionFloorInfoResponse) {
		if (auctionFloorInfoResponse.getStatus().getErrors() != null) {
			logger.error("Failed to get auction floor info for ctx:{} due to errors [{}]; falling back to defaults",
			             ctx.toString(), getErrorLogMessage(auctionFloorInfoResponse));
			// Gracefully failing to defaults as per data science recommendation
			return DEFAULT_AUCTION_FLOOR_INFO;
		}

		return auctionFloorInfoResponse.getData();
	}

	@Override
	public AuctionFloorInfo getAuctionFloorInfo(AuctionFloorRequestContext ctx) {
		AuctionFloorInfo answer;
		if (isSlotExcluded(ctx.getSlot())) {
			logger.debug("Slot [{}] excluded from auction floor pricing", ctx.getSlot());
			answer = null;
		} else {
			try {
				AuctionFloorInfoResponse auctionFloorResponse = getAuctionFloorByMapping(ctx, AuctionFloorInfoResponse.class);
				answer = processResponse(ctx, auctionFloorResponse);
			} catch (Exception e) {
				logger.error("Failed to get auction floor info for ctx:" + ctx.toString() +
				             " due to exception; falling back to defaults", e);
				// Gracefully failing to defaults as per data science recommendation
				answer = DEFAULT_AUCTION_FLOOR_INFO;
			}
		}
		return answer;
	}


	private SliceableListEx<AuctionFloorInfoListItem> processListResponse(AuctionFloorListRequestContext ctx, AuctionFloorInfoListResponse auctionFloorInfoResponse) {
		if (auctionFloorInfoResponse.getStatus().getErrors() != null) {
			logger.error("Failed to get auction floor info for ctx:{} due to errors [{}]; falling back to defaults",
					ctx.toString(), getErrorLogMessage(auctionFloorInfoResponse));
			// Gracefully failing to defaults as per data science recommendation
			return DEFAULT_AUCTION_FLOOR_INFO_LIST;
		}

		return auctionFloorInfoResponse.getData();
	}

	@Override
	public SliceableListEx<AuctionFloorInfoListItem> getAuctionFloorInfoList(AuctionFloorListRequestContext ctx, AuctionFloorConfig config) {
		SliceableListEx<AuctionFloorInfoListItem> answer = DEFAULT_AUCTION_FLOOR_INFO_LIST;

		try {
			AuctionFloorListRequestContext.Builder newCtxBuilder = new AuctionFloorListRequestContext.Builder()
					.deviceCategory(config.parseViaMapping("DEVICE_CATEGORY", ctx.getDeviceCategory()))
					.geoLocation(config.parseViaMapping("GEO", ctx.getGeoLocation()))
					.operatingSystem(config.parseViaMapping("OS", ctx.getOperatingSystem()))
					.tier(config.parseViaMapping("TIER", ctx.getTier()))
					.vertical(config.parseViaMapping("VERTICAL", vertical));

			AuctionFloorInfoListResponse auctionFloorResponse = getAuctionFloorListByMapping(newCtxBuilder.build(), AuctionFloorInfoListResponse.class);
			answer = processListResponse(ctx, auctionFloorResponse);
		} catch (Exception e) {
			logger.error("Failed to get auction floor info for ctx:" + ctx.toString() +
					" due to exception; falling back to defaults", e);
		}

		return answer;
	}

}
