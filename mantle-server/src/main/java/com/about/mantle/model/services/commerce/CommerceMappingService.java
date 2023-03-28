package com.about.mantle.model.services.commerce;

import java.util.Map;
import java.util.Set;

/**
 * Provides a map to commerceInfoEx.type to equivalent commerceApi class
 */
public interface CommerceMappingService {

	public CommerceApi getService(String type);

	public Map<String, CommerceApi> getServices(Set<String> type);

}
