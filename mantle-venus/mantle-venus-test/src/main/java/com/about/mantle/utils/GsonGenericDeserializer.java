package com.about.mantle.utils;

import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.google.gson.Gson;
import io.restassured.response.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GsonGenericDeserializer {
	private final Response response;
	private final Class<?> genericType;


	public GsonGenericDeserializer(Response response, Class<?> genericType){
		this.response = response;
		this.genericType = genericType;
	}

	/*
	 * convenience method to deserialize generic data field in the DataWithStatus class, to parameter type
	 * Since Gson by default does not know the data type of the parameterized data in ResponseWithStatus class,
	 * we need to tell it the type we are looking for in the response
	 */
	public DataWithStatus getResponse() {
		return new Gson().fromJson(response.asString(), new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {genericType};
			}

			@Override
			public Type getRawType() {
				return DataWithStatus.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		});
	}
}
