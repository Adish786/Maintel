package com.about.mantle.spring;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 *  This class contains utility methods to for creating filters.
 *  Handy for verticals while they add their own filters.  
 *
 */
public final class MantleFilterUtils {
	
	private MantleFilterUtils() {
		
	}
	
	public static <T extends Filter> FilterRegistrationBean<T> getConfiguredFilter(T filter,
			EnumSet<DispatcherType> dispatcherTypes, int order, String... urlPatterns) {
		FilterRegistrationBean<T> filterOfTypeT = new FilterRegistrationBean<>();
		filterOfTypeT.setFilter(filter);
		filterOfTypeT.addUrlPatterns(urlPatterns);
		filterOfTypeT.setDispatcherTypes(dispatcherTypes);
		filterOfTypeT.setOrder(order);
		return filterOfTypeT;
	}

	public static boolean isRequestForEmbedTools(HttpServletRequest request) {
		return request.getServletPath().startsWith("/tools/");
	}

}
