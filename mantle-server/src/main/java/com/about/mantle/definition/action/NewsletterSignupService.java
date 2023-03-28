package com.about.mantle.definition.action;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ArrayUtils.nullToEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.mantle.model.services.NewsletterValidationService;
import com.google.common.collect.ImmutableMap;
import com.sailthru.client.SailthruClient;
import com.sailthru.client.handler.response.JsonResponse;
import com.sailthru.client.params.Event;
import com.sailthru.client.params.User;

public class NewsletterSignupService {

	public static final Logger logger = LoggerFactory.getLogger(NewsletterSignupService.class);

	protected final SailthruClient sailthruClient;
	protected final NewsletterValidationService newsletterValidationService;

	public NewsletterSignupService(SailthruClient sailthruClient, NewsletterValidationService newsletterValidationService) {
		this.sailthruClient = sailthruClient;
		this.newsletterValidationService = newsletterValidationService;
	}

	public boolean signupNewsletter(HttpServletRequest request,
			HttpServletResponse response, @RequestContextTaskParameter RequestContext requestContext)
					throws IOException, URISyntaxException {

		String[] emails = requestContext.getParameters().get("email");
		String email = emails == null ? null : stripToNull(emails[0]);

		if (email == null) {
			response.setStatus(500);
			return false;
		}
		
		if (!newsletterValidationService.isValidMailbox(email)) return false;
		
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

		for (String list : nullToEmpty(requestContext.getParameters().get("list[]"))) {
			list = stripToNull(list);
			if (list != null) builder.put(list, 1);
		}

		String[] sources = requestContext.getParameters().get("source1");
		if (isNotEmpty(sources)) builder.put("source1", stripToEmpty(sources[0]));

		for (String key : requestContext.getParameters().keySet()) {		
			if (key.startsWith("customVariable_")) {
				builder.put(key, stripToEmpty(requestContext.getParameterSingle(key)));
			}
		}
		
		User user = new User(email);
		user.setKey("email");
		user.setLists(ImmutableMap.of("Aboutcom Master List", 1));
		user.setVars(builder.build());

		JsonResponse saveUserJsonResponse = sailthruClient.saveUser(user);

		boolean isUserSaved = saveUserJsonResponse.isOK();
		
		if (isUserSaved) {
			logger.info("Successfully saved user:{}", email);
			String customEventName = requestContext.getParameterSingle("customEventName");
			if (isNotEmpty(customEventName)) {
				triggerSailthruCustomEvent(email, customEventName);
			}
			response.setStatus(200);
		} else {
			logger.warn("Could not save user:{}", email);
			response.setStatus(500);
		}

		return isUserSaved;
	}

	/**
	 * Triggers Sailthru custom event (for e.g. Welcome Email Series)  
	 * @param email Email address used as key of Sailthru event
	 * @param customEventName Custom Sailthru event name
	 * @throws IOException
	 */
	private void triggerSailthruCustomEvent(String email, String customEventName) throws IOException {
		// Sailthru documentation for pushEvent:
		// https://getstarted.sailthru.com/developers/api-client/java/#public_JsonResponse_pushEventEvent_event
		JsonResponse pushEventJsonResponse = sailthruClient.pushEvent(new Event(email).setEvent(customEventName));
		boolean isEventPushed = pushEventJsonResponse.isOK();

		if (isEventPushed) {
			logger.info("Successfully pushed custom event {} for email:{}", customEventName, email);
		} else {
			logger.warn("Could not push custom event {} for email:{}", customEventName, email);
		}
	}

}
