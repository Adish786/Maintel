package com.about.mantle.utils.jsonfileutils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import static com.about.mantle.utils.jsonfileutils.FileUtils.readFile;

public class ObjectFromFile<T> {
	private final String filepath;
	private final Class<T> type;

	public ObjectFromFile(String filepath, Class<T> type) {
		this.filepath = filepath;
		this.type = type;
	}

	public T getObject(boolean readFromJar) {
		Gson gson = new Gson();
		return gson.fromJson(readFile(readFromJar, filepath), (Type) type);
	}

	public T getObject() {
		return getObject(true);
	}
}
