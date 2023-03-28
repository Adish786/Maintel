package com.about.mantle.utils.neko;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class NekoDocumentContext {
	private static final Logger logger = LoggerFactory.getLogger(NekoDocument.class);
	
public static DocumentContext getNekoDocument(String nekoUrl, String productId) {

		String endPoint = nekoUrl + "/product/"+productId+"?promotionalEventFilter=ONGOING&includeMetadata=true";
		HttpResponse<String> docResponse = null;
		
		try {
			docResponse = Unirest.get(endPoint)
					.headers(ImmutableMap.of(HttpHeaders.ACCEPT,
							MediaType.create("application", "json").toString(), HttpHeaders.CONTENT_TYPE,
							MediaType.create("application", "json").toString()))
					.asString();
		} catch (UnirestException e) {
			logger.info(e.getMessage());
		}

		DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
	
		if (docResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			String errorCode = responseJson.read("$.status.errors[0].code").toString();
			String errorMessage = responseJson.read("$.status.errors[0].message").toString();
			logger.info("Failed to read the data from Neko" + "[" + errorCode + "][" + errorMessage + "]");
			Assertions.fail("Error in reading the data from Neko : ");
		}
		return responseJson;
	}

}
