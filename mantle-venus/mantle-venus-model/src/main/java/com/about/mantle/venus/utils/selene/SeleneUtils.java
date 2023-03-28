package com.about.mantle.venus.utils.selene;

import com.about.venus.core.utils.ConfigurationProperties;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.joda.time.Instant;
import org.junit.Assert;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class SeleneUtils {
	private static final Logger logger = LoggerFactory.getLogger(SeleneUtils.class);

	private static final String project = ConfigurationProperties.getTargetProject("tech");

	public static Map<String, List<ImmutableMap<String, Serializable>>> outputMap = new HashMap<>();
	private static final String inputFolder = "/input/" + project;
	private static final String outputFolder = "/";
	private static Map<String, DocumentContext> jsons2PostMap = Collections
			.synchronizedMap(new HashMap<String, DocumentContext>());
	private static String seleneAws = ConfigurationProperties.getTargetSelene(null) ;
	public static boolean flagCustomData = false;
	public static List<DocumentContext> successfulPostsJson = new ArrayList<DocumentContext>();
	public static List<DocumentContext> errorPostsJson = new ArrayList<DocumentContext>();
	private static final String AUTHKEY = getAuthToken();
	private static final Configuration JSONPATH_CONFIG = Configuration.defaultConfiguration()
			.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

	private static void init()  {
		Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new ResourcesScanner())
	            .setUrls(ClasspathHelper.forPackage(inputFolder)));
		Set<String> fileNames = reflections.getResources(Pattern.compile(".*(CUSTOME).*\\.json"));
	    for (String nextFile : fileNames) {
	        logger.info(nextFile);
	        DocumentContext aJson = null;
			try {InputStream is = SeleneUtils.class.getClassLoader().getResourceAsStream(nextFile);
			aJson = JsonPath.parse(is);
			jsons2PostMap.put(nextFile.toString().split("/")[nextFile.toString().split("/").length - 1], aJson);
			flagCustomData = true;
			} catch (Exception e) {
				flagCustomData = false;
				logger.info(e.getMessage());
			}
	    }
	}

	/**
	 * this methods posts all the documents from specified folder. it removes the
	 * docID from the payload and post the documents as new documents. It creates a
	 * array list of all successful posts "successfulPostsList" which is used in the
	 * entire scripts in order to read the newly created document id and url
	 */
	public static void post() {
		logger.info("in post");
		init();
		logger.info("after init");
		if (flagCustomData && seleneAws != null && seleneAws.contains(".qa.aws.dotdash.com")) { // making it prod safe
			HttpResponse<String> response = null;
			for (Entry<String, DocumentContext> anEntry : jsons2PostMap.entrySet()) {
				logger.info("in for in post ");
				response = actualPost(prePostPrep(anEntry));
				if (response.getStatus() != Response.Status.OK.getStatusCode()) {
					unSuccessfulPostLog(response, anEntry);
				} else {
					processSuccessfulPost(response, anEntry);
					logger.info("successful post");
				}
			}
			createOutputJson();
		}
	}

	public static void taxonomyCreation(String testName, int docId) {
		String qaUrl = seleneAws + "/taxene";
		try {
			DocumentContext taxonomyJson = JsonPath.parse(SeleneUtils.class.getClassLoader().getResourceAsStream("input/" + project + "/CUSTOM_DATA.json"));

			int taxId = taxonomyJson.read(testName + ".taxId");

			HttpResponse<String> response = null;
			InputStream is = SeleneUtils.class.getResourceAsStream("/taxeneRequest.json");
			DocumentContext aJson = JsonPath.parse(is);
			aJson.set("docId", docId);
			aJson.set("relationship.targetNode.docId", taxId);
			logger.info(">>> posting taxene for doc: " + docId);
			try {
				response = Unirest.post(qaUrl)
						.headers(ImmutableMap.of(
								HttpHeaders.ACCEPT,
								MediaType.create("application", "vnd.abt.v2+json").toString(),
								HttpHeaders.CONTENT_TYPE,
								MediaType.create("application", "vnd.abt.v2+json").toString()))
						.body(aJson.jsonString()).asString();
				Thread.sleep(2000);
			} catch (Exception e) {
				logger.info(e.getMessage());
			}
			// anything that is not 200 is a candidate for re-POSTing
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				logger.info("taxonomy creation failed ");
				String errorCode = JsonPath.using(JSONPATH_CONFIG).parse(response.getBody())
						.read("$.status.errors[0].message");
				String errorMessage = JsonPath.using(JSONPATH_CONFIG).parse(response.getBody())
						.read("$.status.errors[0].message");
				logger.info(errorCode);
				logger.info(errorMessage);
			} else {
				logger.info("successful add taxonomy for " + docId);
			}
		} catch (Exception e){
			logger.info("No taxonomy id assign to this custom doc");
		}
	}

	private static void processSuccessfulPost(HttpResponse<String> response, Entry<String, DocumentContext> anEntry) {
		HttpResponse<String> response2 = null;
		String testName = anEntry.getKey().replace(".json", "");
		DocumentContext responseJson = JsonPath.parse(response.getBody());
		String url = responseJson.read("$.data.url").toString();
		String slug = responseJson.read("$.data.slug").toString();
		String templateType = responseJson.read("$.data.templateType").toString();
		int docId = Integer.parseInt(responseJson.read("$.data.docId").toString());

		String autoUrl = slug + "-" + docId;
		DocumentContext successfulJson = JsonPath.parse("{}");
		successfulJson.put("$", "automation-url", autoUrl);
		successfulJson.put("$", "docId", docId);
		successfulJson.put("$", "templateType", templateType);
		successfulJson.put("$", "testName", testName);
		successfulJson.put("$", "fileName", anEntry.getKey());
		successfulPostsJson.add(successfulJson);
		logger.info("successful post for id: " + docId + " slug: " + autoUrl);
		taxonomyCreation(testName, docId);

		DocumentContext stampJson = JsonPath.parse("{}");
		stampJson.put("$", "url", url);
		stampJson.put("$", "docId", docId);
		stampJson.put("$", "stampValue", slug);
		String qaUrl2 = seleneAws + "/stamp";
		try {
			response2 = Unirest.post(qaUrl2)
					.headers(ImmutableMap.of(HttpHeaders.ACCEPT, MediaType.create("application", "json").toString(),
							HttpHeaders.CONTENT_TYPE, MediaType.create("application", "json").toString()))
					.body(stampJson.jsonString()).asString();
		} catch (UnirestException e) {
			logger.info(e.getMessage());
		}
		if (response2.getStatus() != Response.Status.OK.getStatusCode()) {
			unSuccessfulPostLog(response2, anEntry);
		}
	}

	private static void unSuccessfulPostLog(HttpResponse<String> response, Entry<String, DocumentContext> anEntry) {
		String testName = anEntry.getKey().split("-")[0];
		DocumentContext responseJson = JsonPath.parse(response.getBody());
		String errorCode = responseJson.read("$.status.errors[0].code").toString();
		String throwErrorMessage = responseJson.read("$.status.errors[0].message").toString();
		logger.info("POST failed, will re-attempt [" + anEntry.getKey() + "][" + response.getStatus() + "][" + errorCode
				+ "][" + throwErrorMessage + "]");
		DocumentContext errorJson = JsonPath.parse("{}");
		errorJson.put("$", "testName", testName);
		errorJson.put("$", "errorCode", errorCode);
		errorJson.put("$", "throwErrorMessage", throwErrorMessage);
		errorPostsJson.add(errorJson);
	}

	private static HttpResponse<String> actualPost(DocumentContext jsonData) {
		String qaUrl = seleneAws + "/document";
		HttpResponse<String> response = null;
		try {
			// if docId present use PUT
			HttpRequestWithBody preResponse = null;
			logger.info(jsonData.jsonString());
			if (isPathValid(jsonData, "$.docId")) {
				preResponse = Unirest.put(qaUrl);
			}
			// else use POST
			else {
				preResponse = Unirest.post(qaUrl);
			}
			response = preResponse
					.headers(ImmutableMap.of("Selene-Client-Id", "venus-automation", HttpHeaders.ACCEPT,
							MediaType.create("application", "vnd.abt.v2+json").toString(), HttpHeaders.CONTENT_TYPE,
							MediaType.create("application", "vnd.abt.v2+json").toString()))
					.body(jsonData.jsonString()).asString();

		} catch (UnirestException e) {
			logger.info(e.getMessage());
		}
		return response;
	}

	public static boolean isPathValid(DocumentContext json, String path) {
		try {
			JsonPath.read(json, path);
		} catch (PathNotFoundException e) {
			return false;
		}
		return true;
	}

	private static DocumentContext prePostPrep(Entry<String, DocumentContext> anEntry) {
		DocumentContext jsonDocContxt = anEntry.getValue();
		Long newScheduledStart = Instant.now().getMillis();
		jsonDocContxt.delete("$.dates.scheduledStart");
		jsonDocContxt.set("$.documentState.activeDate", newScheduledStart);
		if (!jsonDocContxt.read("$.templateType").equals("BIO")) {
			String tempSlug = project + "-" + jsonDocContxt.read("$.slug");
			jsonDocContxt.set("$.slug", tempSlug);
		}
		jsonDocContxt.delete("$.docId");
		jsonDocContxt.delete("$.url");
		logger.info("done prepost ");
		return jsonDocContxt;
	}

	private static void createOutputJson() {
		DocumentContext outputson = JsonPath.parse("{ERRORS:[],PROJECT:[]}");
		for (DocumentContext error : errorPostsJson) {
			outputson.add("$.ERRORS", error.json());
		}
		for (DocumentContext success : successfulPostsJson) {
			outputson.add("$.PROJECT", success.json());
		}
		createOutputFiles(outputson, project+"-output");
	}

	private static void createOutputFiles(DocumentContext outputson, String fileName) {
		logger.info("in createOutputFiles ");
		String filepath = System.getProperties().getProperty("user.dir") + "/";

		// save to a json file and if exist in /output dir then wipe it off first
		File file = new File(filepath + fileName + ".json");
		try {

			if (file.exists())
				com.google.common.io.Files.write("", file, Charsets.UTF_8);
			else
				file.getParentFile().mkdirs();
			file.createNewFile();

			com.google.common.io.Files.write(outputson.jsonString(), file, Charsets.UTF_8);
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		logger.info("Output file is created at: " + filepath + "output.json");
	}

	public static void deleteDocuments() {

		String endPoint = seleneAws + "/document/full?docId=000000&forceMode=true";
		HttpResponse<String> delResponse = null;
		DocumentContext deleteErrorJson = JsonPath.parse("{}");
		if (flagCustomData)
			for (DocumentContext entry : successfulPostsJson) {
				try {
					delResponse = Unirest.delete(endPoint.replace("000000", entry.read("$.docId").toString()))
							.headers(ImmutableMap.of(HttpHeaders.AUTHORIZATION, "Token " + AUTHKEY, HttpHeaders.ACCEPT,
									MediaType.create("application", "vnd.abt.v2+json").toString(),
									HttpHeaders.CONTENT_TYPE,
									MediaType.create("application", "vnd.abt.v2+json").toString()))
							.asString();
				} catch (UnirestException e) {
					logger.info(e.getMessage());
				}

				DocumentContext response2Json = JsonPath.parse(delResponse.getBody());
				if (delResponse.getStatus() != Response.Status.OK.getStatusCode()) {
					String errorCode = response2Json.read("$.status.errors[0].code").toString();
					String throwErrorMessage = response2Json.read("$.status.errors[0].message").toString();
					logger.info("Failed to delete the document " + entry.read("$.docId").toString() + "[" + errorCode
							+ "][" + throwErrorMessage + "]");
					DocumentContext errorJson = JsonPath.parse("{}");
					errorJson.put("$", "taskFailed", "delete of DocId " + entry.read("$.docId").toString());
					errorJson.put("$", "testName", entry.read("$.testName"));
					errorJson.put("$", "errorCode", errorCode);
					errorJson.put("$", "throwErrorMessage", throwErrorMessage);
					deleteErrorJson.put("$", entry.read("$.docId"), errorJson.json());
				}
			}
		createOutputFiles(deleteErrorJson, project + "-deleteErros");

	}

	public static DocumentContext getDocuments(String docId) {
		
		String endPoint = seleneAws + "/document?docId=" + docId + "&forceMode=true";
		HttpResponse<String> docResponse = null;
		try {
			docResponse = Unirest.get(endPoint)
					.headers(ImmutableMap.of(HttpHeaders.ACCEPT,
							MediaType.create("application", "vnd.abt.v2+json").toString(), HttpHeaders.CONTENT_TYPE,
							MediaType.create("application", "vnd.abt.v2+json").toString()))
					.asString();
		} catch (UnirestException e) {
			Assert.fail("Error in reading doc selene data: " + docId);
			logger.info(e.getMessage());
		}

		DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
		if (docResponse.getStatus() != Response.Status.OK.getStatusCode() && docResponse.getStatus() != Response.Status.BAD_REQUEST.getStatusCode()) {
			throwErrorMessage(responseJson);
		}
		return responseJson;
	}
	
	private static void throwErrorMessage(DocumentContext responseJson){
		String errorCode = responseJson.read("$.status.errors[0].code").toString();
		String throwErrorMessage = responseJson.read("$.status.errors[0].message").toString();
		logger.info("Failed to read the selene data " + "[" + errorCode + "][" + throwErrorMessage + "]");
		Assert.fail("Error in reading selene data.");
	}
	
	public static DocumentContext getSeleneData(String slug) {

		String endPoint = seleneAws + slug;
		HttpResponse<String> docResponse = null;
		try {
			docResponse = Unirest.get(endPoint).asString();
		} catch (UnirestException e) {
			Assert.fail("Error in reading data: " + slug);
			logger.info(e.getMessage());
		}

		DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
		if (docResponse.getStatus() != Response.Status.OK.getStatusCode() && docResponse.getStatus() != Response.Status.BAD_REQUEST.getStatusCode()) {
			throwErrorMessage(responseJson);
		}
		return responseJson;
	}

	/**
	 * This function is using selene /deion/search endpoint to get the bio recric cards data for specific author.
	 * @param verticals
	 * @param authorAttributeId (Pls use selene /attribution/search/{authorId} to get authorAttributeId.)
	 * @param limit (Limit num of recric cards)
	 * @return
	 */
	public static DocumentContext getBioRecricData(String verticals, String authorAttributeId, int limit) {
		String endPoint = seleneAws + "/deion/search?query=*&filterQuery=vertical:"+ verticals + "&filterQuery=-templateType:" +
				"(REDIRECT+OR+LEGACY+OR+CATEGORY+OR+LANDINGPAGE+OR+USERPATH+OR+IMAGEGALLERY+OR+VIDEO+OR+JWPLAYERVIDEO+OR+PROGRAMMEDSUMMARY+OR+AMAZONOSP)" +
				"&filterQuery=-templateType:BIO&filterQuery=-templateType:TAXONOMY" +
				"&filterQuery=byline_id:"+ authorAttributeId +
				"&field=url&field=docId&field=title&field=description&field=state" +
				"&field=activeDate&offset=0&limit=" + limit + "&sort=displayed+DESC&noCache=false&includeDocumentSummaries=true";
		HttpResponse<String> docResponse = null;
		try {
			docResponse = Unirest.get(endPoint).asString();
		} catch (UnirestException e) {
			Assert.fail("Error in reading data from selene");
			logger.info(e.getMessage());
		}

		DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
		if (docResponse.getStatus() != Response.Status.OK.getStatusCode() && docResponse.getStatus() != Response.Status.BAD_REQUEST.getStatusCode()) {
			throwErrorMessage(responseJson);
		}
		return responseJson;
	}

	public static DocumentContext getSeleneDataWithHeader(String slug, Map<String, String> headers) {

		String endPoint = seleneAws + slug;
		HttpResponse<String> docResponse = null;
		try {
			docResponse = Unirest.get(endPoint).headers(headers).asString();
		} catch (UnirestException e) {
			Assert.fail("Error in reading data: " + slug);
			logger.info(e.getMessage());
		}

		DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
		if (docResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			throwErrorMessage(responseJson);
		}
		return responseJson;
	}

	private static String getAuthToken() {
		String endPoint = seleneAws + "/auth/token";
		HttpResponse<String> authResponse = null;
		try {
			authResponse = Unirest.post(endPoint)
					.headers(ImmutableMap.of(HttpHeaders.ACCEPT,
							MediaType.create("application", "vnd.abt.v2+json").toString(), HttpHeaders.CONTENT_TYPE,
							MediaType.create("application", "vnd.abt.v2+json").toString()))
					.body("{\n" + "		  \"username\":\"customdata.qa@dotdash.com\",\n" + "		  \"secret\":\""
							+ new String(Base64.getUrlDecoder().decode("QGJvdXRNYWMhMQ")) + "\"\n" + "		}")
					.asString();
		} catch (UnirestException e) {
			Assert.fail("Error in getting AuthToken from selene");
			logger.info(e.getMessage());
		}
		DocumentContext response2Json = JsonPath.parse(authResponse.getBody());
		if (authResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			String errorCode = response2Json.read("$.status.errors[0].code").toString();
			String throwErrorMessage = response2Json.read("$.status.errors[0].message").toString();
			logger.info("Getting Auth key  failed [" + errorCode + "][" + throwErrorMessage + "]");
			DocumentContext errorJson = JsonPath.parse("{}");
			errorJson.put("$", "taskFailed", "generating Auth key");
			errorJson.put("$", "testName", "none");
			errorJson.put("$", "errorCode", errorCode);
			errorJson.put("$", "throwErrorMessage", throwErrorMessage);
			errorPostsJson.add(errorJson);
		}
		return response2Json.read("$.data.signedToken").toString();
	}
}
