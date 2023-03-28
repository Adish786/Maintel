package com.about.mantle.model.json;

import java.io.IOException;
import java.util.HashMap;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONAware;

@Tasks
public class JsonTask {
	private final ObjectMapper objectMapper;

	public JsonTask() {
		this.objectMapper = new StandardObjectMapperProvider().getContext(null);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}

	@Task(name = "json")
	public HashMap<String,Object> json(@TaskParameter(name = "string") String jsonString) throws GlobeException {
		try {
			return objectMapper.readValue(jsonString, new TypeReference<HashMap<String,Object>>() {});
		} catch (IOException e) {
			throw new GlobeException("failed to convert string to map ", e);
		}
	}

	@Task(name = "json")
	public Object json(@TaskParameter(name = "string") String jsonString,
			@TaskParameter(name = "class") String targetClass) throws GlobeException {
		try {
			return objectMapper.readValue(jsonString, JsonTask.class.getClassLoader().loadClass(targetClass));
		} catch (IllegalArgumentException | ClassNotFoundException | IOException e) {
			throw new GlobeException("failed to convert string to class " + targetClass, e);
		}
	}

	/**
	 * Intended to be used in conjunction with {@link com.about.mantle.render.MantleRenderUtils#toJSONString(java.util.Map)}.
	 *
	 * ${utils.toJSONString(model.foobar)} is commonly found in freemarker templates.
	 * When foobar is an object a user-defined class that's NOT List, Map, String or an auto-boxed primitive then
	 * toJSONString uses Object.toString() in an effort to convert the object to JSON. However, no user-defined
	 * class is going to have a toString() method that returns valid JSON.
	 * 
	 * This task is provided as a wrapper to any object for JSON conversion.
	 */
	@Task(name = "json")
	public JSONAware json(@TaskParameter(name = "object") Object object) throws GlobeException {
		return new JSONAware(){
			@Override
			public String toJSONString() {
				try {
					return objectMapper.writeValueAsString(object);
				} catch (JsonProcessingException e) {
					throw new GlobeException("failed to serialize object to json string", e);
				}
			}
		};

	}
}
