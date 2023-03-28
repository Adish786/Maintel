package com.about.mantle.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlets.DoSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.logging.LoggingUtils;
import com.about.mantle.http.util.RequestSourceUtils;

public class ExtendedIPDoSFilter extends DoSFilter {
	private static final Logger logger = LoggerFactory.getLogger(ExtendedIPDoSFilter.class);
	private static final int THROTTLE_THRESHOLD_MINUTES = 1;
	private final Predicate<HttpServletRequest> criteria;
	private final ConcurrentHashMap<String, RateTracker> _rateTrackers = new ConcurrentHashMap<>();
	private final Map<String, Runnable> _safelistLoggers = new HashMap<>();

	public ExtendedIPDoSFilter(Predicate<HttpServletRequest> criteria) {
		super();
		this.criteria = Objects.requireNonNull(criteria, "filter criteria required");
	}

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		if (criteria.negate().test(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		HttpServletRequest extendedIpRequest = new ExtendedIPHttpServletRequest(request);

		// DoSFilter handles safelisting of IPs, but we want to log how many requests would otherwise be blocked
		String address = extendedIpRequest.getRemoteAddr();
		if (checkWhitelist(address)) {
			RateTracker tracker = _rateTrackers.get(address);
			if (tracker == null) {
				tracker = new RateTracker(getMaxRequestsPerSec());
				_rateTrackers.putIfAbsent(address, tracker);
			}
			boolean overRateLimit = tracker.isRateExceeded(System.currentTimeMillis());
			if (overRateLimit) {
				Runnable safelistLogger = _safelistLoggers.get(address);
				if (safelistLogger == null) {
					safelistLogger = LoggingUtils.throttle(THROTTLE_THRESHOLD_MINUTES * 60,
							count -> logger.error("Safelisted IP {} would have triggered DoSFilter: happened {} times in last {} minutes", new String(address), count, THROTTLE_THRESHOLD_MINUTES));
					_safelistLoggers.putIfAbsent(address, safelistLogger);
				}
				CompletableFuture.runAsync(safelistLogger);
			}
		}

		super.doFilter(extendedIpRequest, response, filterChain);
	}

	public class ExtendedIPHttpServletRequest extends HttpServletRequestWrapper {

		public ExtendedIPHttpServletRequest(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getRemoteAddr() {
			return RequestSourceUtils.getRemoteIp((HttpServletRequest) this.getRequest());
		}

	}

	/**
	 * Simplified copy of rate tracking logic from DoSFilter class. Used to track whether safelisted IPs would trigger
	 * the filter. This can't be done with the tracking inside DoSFilter, as those classes / methods are
	 * package-private.
	 */
	static class RateTracker {
		protected final long[] _timestamps;
		protected int _next;

		public RateTracker(int maxRequestsPerSecond) {
			_timestamps = new long[maxRequestsPerSecond];
			_next = 0;
		}

		/**
		 * @param now the time now (in milliseconds)
		 * @return the current calculated request rate over the last second
		 */
		public boolean isRateExceeded(long now) {
			final long last;
			synchronized (this) {
				last = _timestamps[_next];
				_timestamps[_next] = now;
				_next = (_next + 1) % _timestamps.length;
			}

			return last != 0 && (now - last) < 1000L;
		}
	}
}
