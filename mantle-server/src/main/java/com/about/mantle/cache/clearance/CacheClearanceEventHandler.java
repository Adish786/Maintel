package com.about.mantle.cache.clearance;

/**
 * Used to get notified of cache clearance messages.
 */
public interface CacheClearanceEventHandler {

	void handle(String path);
}
