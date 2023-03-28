package com.about.mantle.model.services.commerce;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.util.StringUtils;

import com.about.mantle.exception.CommerceTypeException;

/**
 * Provides a map to commerceInfoEx.type to equivalent commerceApi class
 */
public class CommerceMappingServiceImpl implements CommerceMappingService {

	private Map<String, CommerceApi> mapOfTypeToCommerceApiWrapper;

	public CommerceMappingServiceImpl(Map<String, CommerceApi> mapOfTypeToCommerceApiWrapper) {
		this.mapOfTypeToCommerceApiWrapper = mapOfTypeToCommerceApiWrapper;
	}

	@Override
	public CommerceApi getService(String type) {
		String lowercaseType = StringUtils.toLowerCase(type);
		if (!mapOfTypeToCommerceApiWrapper.containsKey(lowercaseType))
			throw new CommerceTypeException("CommerceInfo Type Not Found: " + lowercaseType);
		return mapOfTypeToCommerceApiWrapper.get(lowercaseType);
	}

	@Override
	public Map<String, CommerceApi> getServices(Set<String> types) {
		return types.stream().collect(Collectors.toMap(type -> type, type -> getService(type)));
	}

}
