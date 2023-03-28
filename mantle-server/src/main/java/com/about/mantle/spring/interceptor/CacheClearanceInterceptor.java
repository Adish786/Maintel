package com.about.mantle.spring.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;
import com.about.mantle.model.tasks.DocumentTask;

/**
 * This interceptor checks if incoming url is a candidate for cache clearance.
 * Accordingly, it sets the boolean flag to indicate this and downstream threads
 * (created by this thread) to clear the specified caches in the request and re-supply values from
 * original suppliers - causing specified caches to update themselves with latest
 * values.
 */
public class CacheClearanceInterceptor extends AbstractMantleInterceptor {

	private CacheClearanceCandidateRepo cacheClearanceCandidateRepository;

	public CacheClearanceInterceptor(CacheClearanceCandidateRepo cacheClearanceCandidateRepository) {
		this.cacheClearanceCandidateRepository = cacheClearanceCandidateRepository;
	}

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        checkCacheClearanceCandidacy(request);
		return true;
	}
	
	/**
	 * See if given requestUri is a candidate for cache clearance by looking its
	 * presence in the CacheClearanceCandidateRepository. If it is present, then set
	 * thread local flag in the current thread and remove it from
	 * CacheClearanceCandidateRepository; Also, make sure cache is cleared for valid PREVIEW requests.
	 * PREVIEW requests are issued from CMS to preview changes!
	 */
	private void checkCacheClearanceCandidacy(HttpServletRequest request) {
		try {
			if (cacheClearanceCandidateRepository != null) {
				List<CacheClearanceRequest> list = null;

				if (DocumentTask.isValidPreviewRequest(request.getParameterMap())) {
					list = List.of(CacheClearanceRequest.createBasicCacheClearanceRequest());
				} else {
					list = cacheClearanceCandidateRepository.getCandidateRequests(request);
				}

				CacheClearanceThreadLocalUtils.setClearCacheRequest(list);
			}
		} catch (Exception e) {
			// this _should_ not happen but in case it happens
			CacheClearanceThreadLocalUtils.setClearCacheRequest(null);
		}
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		try {
			List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

			if (cacheClearRequest != null && cacheClearRequest.size() > 0 &&
				!DocumentTask.isValidPreviewRequest(request.getParameterMap())) {
				cacheClearanceCandidateRepository.requestCompleted(request, response, ex, cacheClearRequest);
			}
		} finally {
			// clear threadLocal from this thread before exiting.
			CacheClearanceThreadLocalUtils.setClearCacheRequest(null);
		}
	}


	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}


	@Override
	public String getExcludePathPatterns() {
		return "";
	}

}
