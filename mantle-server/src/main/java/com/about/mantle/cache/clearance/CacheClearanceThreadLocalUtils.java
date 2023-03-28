package com.about.mantle.cache.clearance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CacheClearanceThreadLocalUtils {

	private CacheClearanceThreadLocalUtils() {};

	private static ThreadLocal<List<CacheClearanceCacheKeyPair>> callsAlreadyCleaned = new ThreadLocal<>();
	private static ThreadLocal<List<CacheClearanceRequest>> targetedRequests = new ThreadLocal<>();

	public static List<CacheClearanceRequest> getClearCacheRequests (){
		if(targetedRequests.get() == null){
			targetedRequests.set(new ArrayList<>());
			callsAlreadyCleaned.set(new ArrayList<>());
		}

		return targetedRequests.get();
	}

	public static void setClearCacheRequest(List<CacheClearanceRequest> cacheClearanceRequest) {
		if(cacheClearanceRequest == null || cacheClearanceRequest.size() == 0){
			callsAlreadyCleaned.set(new ArrayList<>());
		}
		targetedRequests.set(cacheClearanceRequest);
	}

	public static Boolean alreadyCleaned(CacheClearanceCacheKeyPair cacheClearanceCacheKeyPair){

		if(callsAlreadyCleaned.get() == null){
			callsAlreadyCleaned.set(new ArrayList<>());
		}

		List<CacheClearanceRequest>  cacheClearRequest = getClearCacheRequests();
		if(cacheClearRequest != null && cacheClearRequest.size() > 0) {
			if (callsAlreadyCleaned.get().contains(cacheClearanceCacheKeyPair) ){
				return true;
			}else {
				callsAlreadyCleaned.get().add(cacheClearanceCacheKeyPair);
			}
		}
		return false;
	}

	public static Boolean isTargetedCacheClearCandidate(Integer level, String name){

		for(CacheClearanceRequest request : getClearCacheRequests()){
			if( level <= Optional.ofNullable(request.getLevel()).orElse(Integer.MIN_VALUE) || (request.getCacheNames() != null && request.getCacheNames().contains(name))){
				return true;
			}
		}
		return false;
	}

}
