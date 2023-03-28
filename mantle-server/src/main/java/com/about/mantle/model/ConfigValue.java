package com.about.mantle.model;

public class ConfigValue<T> {

	private String key;
	private T value;
	
	public ConfigValue(String key, T value){
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public T getValue() {
		return value;
	}
}
