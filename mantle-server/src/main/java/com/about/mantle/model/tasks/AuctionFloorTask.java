package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.services.AuctionFloorService.AuctionFloorListRequestContext;
import com.about.mantle.model.services.AuctionFloorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Tasks
public class AuctionFloorTask {
	
	private AuctionFloorMappingConfigService auctionFloorMappingConfigService;
	private AuctionFloorService auctionFloorService;

	public AuctionFloorTask(AuctionFloorMappingConfigService auctionFloorMappingConfigService, AuctionFloorService auctionFloorService) {
		this.auctionFloorMappingConfigService = auctionFloorMappingConfigService;
		this.auctionFloorService = auctionFloorService;
	}

	@Task(name = "auctionFloors")
	public Map<String, Map<String, String>> auctionFloors(@RequestContextTaskParameter RequestContext requestContext) {

		AuctionFloorListRequestContext.Builder auctionCtxBuilder = new AuctionFloorListRequestContext.Builder()
			.deviceCategory(requestContext.getUserAgent().getDeviceCategory().toString())
			.geoLocation(requestContext.getGeoData().getIsoCode())
			.operatingSystem(requestContext.getUserAgent().getOsName())
			.tier(requestContext.getConfigs().getValue("tier", String.class));

		AuctionFloorConfig auctionFloorConfig = auctionFloorMappingConfigService.getAuctionFloorConfig();

		if(auctionFloorConfig != null) {
			SliceableListEx<AuctionFloorInfoListItem> auctionFloorInfo = auctionFloorService.getAuctionFloorInfoList(auctionCtxBuilder.build(), auctionFloorConfig);

			ArrayList<AuctionFloorInfoListItem> listOfAuctionItems = new ObjectMapper().convertValue(auctionFloorInfo.getList(), new TypeReference<>() {});

			return listOfAuctionItems.stream().collect(Collectors.toMap(AuctionFloorInfoListItem::getSlot, AuctionFloorInfoListItem::getFloorConfig));

		}

		return Collections.emptyMap();
	}
}
