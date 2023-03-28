package com.about.mantle.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExternalServiceProxyHandler {
	/**
	 * @return boolean indicating whether filter using this handler should continue with next filters in the chain.  
	 */
	public boolean handle(HttpServletRequest request, HttpServletResponse response);
}
