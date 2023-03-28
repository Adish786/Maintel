package com.about.mantle.model.extended;

import java.util.Collections;
import java.util.Map;

import com.about.globe.core.model.extended.Configs;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jersey.repackaged.com.google.common.collect.Maps;

public class TaxeneConfigs extends Configs {

	public static final TaxeneConfigs NULL = new TaxeneConfigs();

	private Map<String, TaxeneConfigValueEx> configs;

	public TaxeneConfigs() {
		super();
		this.configs = Collections.emptyMap();
	}

	public TaxeneConfigs(Map<String, TaxeneConfigValueEx> configs) {
		super(((Map<String, Object>) Maps.transformValues(configs, v -> v.getValue())));
		this.configs = Collections.unmodifiableMap(configs);
	}

	@JsonIgnore
	public Map<String, TaxeneConfigValueEx> getTaxeneNodeConfigsMap() {
		return configs;
	}
}
