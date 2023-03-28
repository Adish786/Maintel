package com.about.mantle.app;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.MDC;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class cleans up the MDC context for AsyncEvents.
 */
public class MantleMdcCleanerHandler extends HandlerWrapper {
	
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
			// MDC context should be cleared on thread
			MDC.clear();
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
						baseRequest.getAsyncContext().addListener(_listener);
					}
				}
			}
		}
	}
}
