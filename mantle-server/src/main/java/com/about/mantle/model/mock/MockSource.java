package com.about.mantle.model.mock;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockSource<T> {
	private final ObjectMapper objectMapper;
	private final Map<String, T> mocks;
	private final String mockDirectory;
	private final JavaType type;

	public MockSource(String mockDirectory, JavaType type) {
		this.objectMapper = new StandardObjectMapperProvider().getContext(null);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		this.mocks = new ConcurrentHashMap<>();
		this.mockDirectory = mockDirectory;
		this.type = type;
	}
	
	/* One strategy for generating the contents of this file is to update a real task method to save the object
	 * to a json file just prior to its return. For example, consider the following task method:
	 * @Task(name = "myTask")
	 * public MyObject getMyObject() {
	 *     MyObject myObject = jumpThruHoopsToGetMyObject();
	 *     // everything following this comment should only be added locally for the purpose of generating the file
	 *     ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
	 *     objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	 *     objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("mock-my-task.json"), myObject);
	 *     // everything above this comment should only be added locally for the purpose of generating the file
	 *     return myObject;
	 * }
	 */
	public T get(String mockFilename) throws GlobeException {
		T mockObject = mocks.get(mockFilename);
		if (mockObject == null) {
			final String mockPath = "/mocks/"+mockDirectory+"/"+mockFilename+".json";
			try {
				mockObject = objectMapper.readValue(
						IOUtils.toString(getClass().getResourceAsStream(mockPath)), type);
				mocks.put(mockFilename, mockObject);
			} catch (IOException e) {
				throw new GlobeException("failed to read mock object from " + mockPath, e);
			}
		}
		return mockObject;
	}
}