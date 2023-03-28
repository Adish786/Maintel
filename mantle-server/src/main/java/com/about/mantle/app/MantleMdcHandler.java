package com.about.mantle.app;

import org.eclipse.jetty.server.AsyncContextState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class checks for AsyncEvents and copies the MDC context map onto the request
 * so a listener can prepare the context before logging occurs. This is to address
 * access log entries with null fields.
 */
public class MantleMdcHandler extends HandlerWrapper {
	private static Logger logger = LoggerFactory.getLogger("com.about.globe.accessLogInvestigation");
	private static final String mdcAttribName = "globe-MDC";
	private static Long lastLoggingEvent = 0L;
	private static Long loggingCooldown = 1000 * 60L; // Only log once per minute to avoid spamming logs

	private final AsyncListener _listener = new AsyncListener()
	{

		@Override
		public void onTimeout(AsyncEvent event) throws IOException
		{

		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException
		{
			event.getAsyncContext().addListener(this);
		}

		@Override
		public void onError(AsyncEvent event) throws IOException
		{

		}

		@Override
		public void onComplete(AsyncEvent event) throws IOException
		{
			// Steps to get Request reference mirror those in jetty RequestLogHandler
			AsyncContextState context = (AsyncContextState)event.getAsyncContext();
			Request request=context.getHttpChannelState().getBaseRequest();
			try {
				/* Testing for existence of requestTimestamp value as both write methods on
				 * JsonFormatAccessLogInjector cause this field to be set. This is to narrow
				 * down possible sources of null data in the logs and provide information about
				 * the context in which null logs may be occurring.
				 */
				if (MDC.get("requestTimestamp") == null) {
//					MDC.setContextMap((Map<String,String>)request.getAttribute(mdcAttribName));
					if (System.currentTimeMillis() > (lastLoggingEvent + loggingCooldown)) {
						logger.info("Possible Null Async Access Log Entry for " + request.getRequestURL(), new Throwable("Possible Null Async Access Log Entry"));
						lastLoggingEvent = System.currentTimeMillis();
					}
				}
			} catch (Exception e) {
				logger.error("Error handling MDC context on AsyncEvent.", e);
			}
		}
	};

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// Mirroring RequestLogHandler logic for adding listeners for AsyncEvents
		try
		{
			super.handle(target, baseRequest, request, response);
		}
		finally
		{
			if (baseRequest.getDispatcherType().equals(DispatcherType.REQUEST))
			{
				if (baseRequest.getHttpChannelState().isAsync())
				{
					if (baseRequest.getHttpChannelState().isInitial()) {
						baseRequest.setAttribute(mdcAttribName, MDC.getCopyOfContextMap());
						baseRequest.getAsyncContext().addListener(_listener);
					}
				}
			}
		}

		/* Testing for existence of requestTimestamp value as both write methods on JsonFormatAccessLogInjector
		 * cause this field to be set. This is to narrow down possible sources of null data in the logs
		 * and provide information about the context in which null logs may be occurring.
		 */
		if (MDC.get("requestTimestamp") == null) {
			if (System.currentTimeMillis() > (lastLoggingEvent + loggingCooldown)) {
				logger.info("Possible Null Access Log Entry for " + request.getRequestURL(), new Throwable("Possible Null Access Log Entry"));
				lastLoggingEvent = System.currentTimeMillis();
			}
		}
	}
}
