package com.about.mantle.model.services.client;

import org.glassfish.jersey.uri.UriComponent;

import com.about.hippodrome.models.media.VersionedMediaTypes;

public class ServiceClientUtils {
	/**
	 * Used while calling Selene end points in our Services, {@See MantleSpringConfiguration}
	 * This is partly used to let Selene end point know which service version to call. 
	 */
	public static enum ClientMediaType {
		JSON_V1(VersionedMediaTypes.APPLICATION_ABT_V1_JSON), JSON_V2(VersionedMediaTypes.APPLICATION_ABT_V2_JSON),
		JSON_V3(VersionedMediaTypes.APPLICATION_ABT_V3_JSON), SMILE_V1(VersionedMediaTypes.APPLICATION_ABT_V1_SMILE),
		SMILE_V2(VersionedMediaTypes.APPLICATION_ABT_V2_SMILE), SMILE_V3(VersionedMediaTypes.APPLICATION_ABT_V3_SMILE);

		private final String mediaType;

		private ClientMediaType(String mediaType) {
			this.mediaType = mediaType;
		}

		public String getMediaType() {
			return mediaType;
		}
	}

	
	/**
	 * QueryParamEncoder is used to encode query parameter values so the jersey framework ignores curly brackets and does not template value. 
	 */
	public static class QueryParamEncoder {
		
		public static String encode(Object input) {
			if (input == null)
				return null;
			return UriComponent.encode(input.toString(), UriComponent.Type.QUERY_PARAM_SPACE_ENCODED);
		}
	}

}
