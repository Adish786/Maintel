package com.about.mantle.model.tasks;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.TreeSet;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.testing.GlobeBucket;
import com.about.mantle.model.extended.AuctionFloorInfo;
import com.about.mantle.model.extended.GptAd;
import com.about.mantle.model.services.AuctionFloorService;

@Tasks
public class GptTask {

	private AuctionFloorService auctionFloorService;

	public GptTask(AuctionFloorService auctionFloorService) {
		this.auctionFloorService = auctionFloorService;
	}

	@Task(name = "gptAd")
	public GptAd gptAd(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id, @TaskParameter(name = "sizes") String sizes,
			@TaskParameter(name = "type") String type, @TaskParameter(name = "pos") String pos,
			@TaskParameter(name = "priority") Integer priority, @TaskParameter(name = "isDynamic") Boolean isDynamic,
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "rtb") Boolean rtb, @TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty, @TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet) {

		GptAd.Builder gptAdBuilder = new GptAd.Builder().id(id).sizes(sizes).type(type).pos(pos).priority(priority)
		                                                .isDynamic(isDynamic).timedRefresh(timedRefresh)
														.refreshAfterSlotRenderedElement(refreshAfterSlotRenderedElement)
														.timeoutRefreshOnceOnly(timeoutRefreshOnceOnly)
														.rtb(rtb).waitForThirdParty(waitForThirdParty).targeting(targeting);

		GlobeBucket globeBucket = requestContext.getTests().get("useFloorSearch");
		if (auctionFloorService != null && (
			globeBucket == null ||
			globeBucket.getValue() == 0
		)) {
			AuctionFloorService.AuctionFloorRequestContext.Builder auctionCtxBuilder = new AuctionFloorService.AuctionFloorRequestContext.Builder()
					.deviceCategory(requestContext.getUserAgent().getDeviceCategory().toString())
					.geoLocation(requestContext.getGeoData().getIsoCode())
					.operatingSystem(requestContext.getUserAgent().getOsName())
					.tier(requestContext.getConfigs().getValue("tier", String.class))
					.slot(id);
			AuctionFloorInfo auctionFloorInfo = auctionFloorService.getAuctionFloorInfo(auctionCtxBuilder.build());
			gptAdBuilder = gptAdBuilder.auctionFloorInfo(auctionFloorInfo);
		}

		GptAd gptAd = gptAdBuilder.build();
		if (!isDynamic && gptAdSet != null) {
			gptAdSet.add(gptAd);
		}
		return gptAd;
	}

	// Non-dynamic ads
	@Task(name = "gptAd")
	public GptAd gptAd(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id, @TaskParameter(name = "sizes") String sizes,
			@TaskParameter(name = "type") String type, @TaskParameter(name = "pos") String pos,
			@TaskParameter(name = "priority") Integer priority, @TaskParameter(name = "rtb") Boolean rtb,
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty,
			@TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet) {

		return gptAd(requestContext, id, sizes, type, pos, priority, Boolean.FALSE, timedRefresh, refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly, rtb, waitForThirdParty, targeting, gptAdSet);
	}

	 // When Passing in a list of sizes
	 @Task(name = "gptAd")
	 public GptAd gptAdWithSizesList(@RequestContextTaskParameter RequestContext requestContext,
	 				   @TaskParameter(name = "id") String id, @TaskParameter(name = "sizesList") List<?> sizesList,
									   	@TaskParameter(name = "type") String type, @TaskParameter(name = "pos") String pos,
					   @TaskParameter(name = "isDynamic") Boolean isDynamic,
	 				   @TaskParameter(name = "priority") Integer priority, 
					   @TaskParameter(name = "timedRefresh") Integer timedRefresh,
					   @TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
					   @TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
					   @TaskParameter(name = "rtb") Boolean rtb,
	 				   @TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty, 
	 				   @TaskParameter(name = "targeting") Map<String, Object> targeting,
	 				   @TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet) {

	 	String sizesString = sizesList.stream().map(size -> size.toString()).collect(Collectors.joining(",", "[", "]"));

	 	return gptAd(requestContext, id, sizesString, type, pos, priority, isDynamic, timedRefresh, refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly, rtb, waitForThirdParty, targeting, gptAdSet);
	 }

	// Unprioritized ads
	@Task(name = "gptAd")
	public GptAd gptAdDefaultPriority(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id,
			@TaskParameter(name = "sizes") String sizes, @TaskParameter(name = "type") String type,
			@TaskParameter(name = "pos") String pos, @TaskParameter(name = "isDynamic") Boolean isDynamic,
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "rtb") Boolean rtb, @TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty,
			@TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet) {
		return this.gptAd(requestContext, id, sizes, type, pos, 99, isDynamic, timedRefresh, refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly, rtb, waitForThirdParty, targeting, gptAdSet);
	}

	// Unprioritized, non-dynamic ads
	@Task(name = "gptAd")
	public GptAd gptAdDefaultPriority(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id,
			@TaskParameter(name = "sizes") String sizes, @TaskParameter(name = "type") String type,
			@TaskParameter(name = "pos") String pos, 
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "rtb") Boolean rtb,
			@TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty,
			@TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet) {
		return this.gptAdDefaultPriority(requestContext, id, sizes, type, pos, Boolean.FALSE, timedRefresh, 
		refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly,

		
		rtb, waitForThirdParty, targeting, gptAdSet);
	}

	// TODO this is ugly and should be fixed as soon as we can exclude child
	// components' models from being run based parent's dependencies' presence
	@Task(name = "gptAd")
	public GptAd gptAdWithArbitraryDependency(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id,
			@TaskParameter(name = "sizes") String sizes, @TaskParameter(name = "type") String type,
			@TaskParameter(name = "pos") String pos, @TaskParameter(name = "priority") Integer priority,
			@TaskParameter(name = "isDynamic") Boolean isDynamic, 
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "rtb") Boolean rtb,
			@TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty,
			@TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet,
			@TaskParameter(name = "arbitraryDependency") Object arbitraryDependency) {
		return this.gptAd(requestContext, id, sizes, type, pos, priority, isDynamic, timedRefresh, refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly, rtb, waitForThirdParty, targeting, gptAdSet);
	}

	@Task(name = "gptAd")
	public GptAd gptAdWithArbitraryDependency(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id,
			@TaskParameter(name = "sizes") String sizes, @TaskParameter(name = "type") String type,
			@TaskParameter(name = "pos") String pos, @TaskParameter(name = "priority") Integer priority,
			@TaskParameter(name = "timedRefresh") Integer timedRefresh,
			@TaskParameter(name = "refreshAfterSlotRenderedElement") String refreshAfterSlotRenderedElement,
			@TaskParameter(name = "timeoutRefreshOnceOnly") Boolean timeoutRefreshOnceOnly,
			@TaskParameter(name = "rtb") Boolean rtb, @TaskParameter(name = "waitForThirdParty") Boolean waitForThirdParty,
			@TaskParameter(name = "targeting") Map<String, Object> targeting,
			@TaskParameter(name = "gptAdSet") Set<GptAd> gptAdSet,
			@TaskParameter(name = "arbitraryDependency") Object arbitraryDependency) {
		return this.gptAdWithArbitraryDependency(requestContext, id, sizes, type, pos, priority, Boolean.FALSE, timedRefresh, refreshAfterSlotRenderedElement, timeoutRefreshOnceOnly, rtb, waitForThirdParty, targeting, gptAdSet, arbitraryDependency);
	}

	@Task(name = "gptAdSet")
	public Set<GptAd> gptAdSet() {
		return Collections.synchronizedSortedSet(new TreeSet<GptAd>(new GptAdPrioritizer()));
	}

	private class GptAdPrioritizer implements Comparator<GptAd> {

		@Override
		public int compare(GptAd o1, GptAd o2) {
			int priorityCompare = o1.getPriority().compareTo(o2.getPriority());
			if (priorityCompare != 0) {
				return priorityCompare;
			} else {
				return o1.getId().compareTo(o2.getId());
			}
		}

	}

}