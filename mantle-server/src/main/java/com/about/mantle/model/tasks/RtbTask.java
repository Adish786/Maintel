package com.about.mantle.model.tasks;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.services.RtbService;
import com.about.mantle.model.services.rtb.RtbRequestContext;
import com.about.mantle.model.services.rtb.RtbResult;

/**
 * Tasks responsible for server-side RTB.
 */
@Tasks
public class RtbTask {

	private final RtbService rtbService;

	public RtbTask(RtbService rtbService) {
		this.rtbService = rtbService;
	}

	/**
	 * Get the bids.
	 * @param requestContext
	 * @param slots           whitespace-separated slots to get bids for
	 * @param partners        whitespace-separated partners to get bids from
	 * @param timeout         how long to wait before abandoning pending beds
	 * @return                map of bid results keyed by partner
	 */
	@Task(name = "getBids")
	public Map<String, RtbResult> getBids(@RequestContextTaskParameter RequestContext requestContext,
	                                      @TaskParameter(name = "slots") String slots,
	                                      @TaskParameter(name = "partners") String partners,
	                                      @TaskParameter(name = "timeout") Long timeout) {
		RtbRequestContext.Builder builder = new RtbRequestContext.Builder();
		builder.slots(StringUtils.split(slots))
		       .partners(StringUtils.split(partners))
		       .timeout(timeout);
		RtbRequestContext ctx = builder.build();
		return rtbService.getBids(ctx);
	}

}
