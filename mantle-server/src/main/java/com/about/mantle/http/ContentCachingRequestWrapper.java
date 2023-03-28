package com.about.mantle.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows you to read the inputStream of an HttpServletRequest more than once
 */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {
	
	public static final Logger logger = LoggerFactory.getLogger(ContentCachingRequestWrapper.class);

	private final String body;

	public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		body = readBufferedInput(request);
	}

	private String readBufferedInput(HttpServletRequest request) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(request.getInputStream(), writer);
		} catch (IOException e) {
			logger.error("Error reading request's input stream.", e);
		}
		return writer.toString();
	}

	// changes done for servlet 3.1 as per https://stackoverflow.com/a/30748533/1478852
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream() {
			
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return byteArrayInputStream.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new RuntimeException("Not implemented");

			}
		};
		return servletInputStream;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}
