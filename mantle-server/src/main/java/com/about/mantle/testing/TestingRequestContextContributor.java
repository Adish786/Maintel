package com.about.mantle.testing;

import static org.apache.commons.lang3.StringUtils.replaceOnce;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.exception.RequestContextContributorException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContext.Builder;
import com.about.globe.core.http.RequestContextContributor;
import com.about.globe.core.testing.GlobeBucket;
import com.about.globe.core.testing.GlobeTestFramework;
import com.google.common.collect.ImmutableMap;

public class TestingRequestContextContributor implements RequestContextContributor {

	private static final Logger logger = LoggerFactory.getLogger(TestingRequestContextContributor.class);

	private static final String FORCE_TEST_BUCKET_PREFIX = "globeTest_";
	private static final String FORCE_NO_TEST = "globeNoTest";

	private final GlobeTestFramework testFramework;

	public TestingRequestContextContributor(GlobeTestFramework testFramework) {
		this.testFramework = testFramework;
	}

	@Override
	public void contribute(HttpServletRequest request, Builder builder) throws RequestContextContributorException {

		// Skip all tests if "No Test" parameter exists
		if (request.getParameterMap().containsKey(FORCE_NO_TEST)) return;
		
		builder.setForcedTestBuckets(getForcedTestBuckets(request));

		// Need copy of request context to use in test evaluations like EL
		RequestContext requestContext = builder.build(request);

		builder.setTests(getTestAssignments(request, requestContext));

	}

	private Map<String, GlobeBucket> getTestAssignments(HttpServletRequest request, RequestContext requestContext)
			throws RequestContextContributorException {
		try {
			return testFramework.getAssignments(requestContext);
		} catch (GlobeNotFoundException e) {
			// no need to log error getting test assignments for page not found
			throw e; // allow this exception to bubble up to {@link PageNotFoundFilter}
		} catch (Exception e) {
			logger.error("Error getting test assignments for request " + requestContext, e);
		}
		return ImmutableMap.of();
	}

	protected Map<String, Integer> getForcedTestBuckets(HttpServletRequest request) {

		ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();

		for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (entry.getKey().startsWith(FORCE_TEST_BUCKET_PREFIX)) {
				String test = replaceOnce(entry.getKey(), FORCE_TEST_BUCKET_PREFIX, "");
				Integer bucket = NumberUtils.toInt(entry.getValue()[0], -1);
				builder.put(test, bucket);
			}
		}

		return builder.build();
	}

}
