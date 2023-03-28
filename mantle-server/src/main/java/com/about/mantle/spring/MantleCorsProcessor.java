package com.about.mantle.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.DefaultCorsProcessor;

/**
 * Overrides DefaultCorsProcessor to enable setting Content-Security-Policy on headers 
 * This is not great, we should do this somewhere else, the plan is to revisit this as part of spring security 
 * 
 * Supporting info for why it's not great: https://stackoverflow.com/a/39491688
 * Bitbucket discussion thread where we agree to revisit post Spring Secuirty: https://bitbucket.prod.aws.about.com/projects/FRON/repos/mantle/pull-requests/2503/overview?commentId=94767
 */
public class MantleCorsProcessor extends DefaultCorsProcessor {
	
	public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
	public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";
	public  final String FRAME_ANCESTORS; 

	public MantleCorsProcessor(String primaryDomain) {
		FRAME_ANCESTORS = "frame-ancestors 'self' *.specless.io *.specless.tech http://*.seo.aws.about.com https://*.seo.aws.about.com http://*.dotdash.com https://*.dotdash.com"+" *." + primaryDomain;
	}

	@Override
	@SuppressWarnings("resource")
	public boolean processRequest(@Nullable CorsConfiguration config, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		/**
		 * Don't add CSP FRAME_ANCESTORS for /tools/ endpoints - our embeddable tools.
		 * This is required so that other sites can embed our tools.
		 */
		if(!MantleFilterUtils.isRequestForEmbedTools(request)) {
			response.addHeader(CONTENT_SECURITY_POLICY, FRAME_ANCESTORS);
			response.addHeader(X_CONTENT_SECURITY_POLICY, FRAME_ANCESTORS);
		}
		
		return super.processRequest(config, request, response);
	}
}
