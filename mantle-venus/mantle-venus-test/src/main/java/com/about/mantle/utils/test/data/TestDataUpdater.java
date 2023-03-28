package com.about.mantle.utils.test.data;

import com.about.mantle.utils.GsonGenericDeserializer;
import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.meta.client.MetaClient;
import com.about.mantle.utils.selene.meta.request.ReadRequest;
import com.about.mantle.utils.selene.taxeneconfig.client.TaxeneConfigClient;
import com.about.mantle.utils.selene.taxeneconfig.request.WriteRequest;
import com.about.mantle.utils.selene.version.Version;
import com.about.mantle.utils.test.model.DocumentFileInfo;
import com.amazonaws.util.StringUtils;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.about.mantle.utils.jsonfileutils.FileUtils.readFile;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;

public class TestDataUpdater {

	public static final String TEST_DATA_PATH = "data-files/testData/";

	protected final Long docId;
	protected final String documentJsonFileName;
	protected final String meta;
	protected final String taxeneConfig;
	protected final String relatedArticleJsonFileName;
	protected final String taxonomy;
	private Meta storeMeta;
	private String storeDocument;
	private Document doc;

	public TestDataUpdater(Long docId, DocumentFileInfo fileInfo) {
		this.docId = docId;
		this.documentJsonFileName = fileInfo.getDocument();
		this.meta = fileInfo.getMeta();
		this.taxeneConfig = fileInfo.getTaxeneConfig();
		this.relatedArticleJsonFileName = fileInfo.getRelatedArticle();
		this.taxonomy = fileInfo.getTaxonomy();
	}

	@SneakyThrows
	public void storeDocumentAndMeta() {

		storeDocument = readDocument();
		if(!StringUtils.isNullOrEmpty(meta)) {
			MetaClient metaClient = new MetaClient();
			ReadRequest metaRequest = ReadRequest.builder().docId(docId).build();
			DataWithStatus<Meta> metaDataWithStatus = metaClient.read(OK, metaRequest);
			storeMeta = metaDataWithStatus.getData();
		}
	}

	public String readDocument() {
		Prettifier prettifier = new Prettifier();
		RestAssuredClient client = new RestAssuredClient();
		Response response = given()
				.spec(client.requestSpecification())
				.param("docId", docId)
				.params("includesummaries", false)
				.headers(client.headers())
				.with().contentType(Version.V2.getContentType()).get("/document");
		response.then().statusCode(OK.getStatusCode());
		String responseWithStatus = prettifier.getPrettifiedBodyIfPossible(response, response.body());
		// remove status part of the response to get data
		String dataWithBrace = responseWithStatus.replaceAll("\\n","")
												 .replaceFirst("\\{\\s+\"status\".*?\"data\":","");
		// finally remove last curly brace
		return dataWithBrace.substring(0, dataWithBrace.length() - 1);
	}

	@SneakyThrows
	public void updateDocumentAndMeta(String path) {
		if(!StringUtils.isNullOrEmpty(meta)) {
			String metaPath = path + meta;
			MetaClient metaClient;
			if(path.equals(TEST_DATA_PATH)) {
				metaClient = new MetaClient(metaPath);
			} else {
				metaClient = new MetaClient(metaPath, false);
			}
			metaClient.setDocumentMeta(docId);
		}
		String docPath = path + documentJsonFileName;
		updateDocument(docPath);
	}

	@SneakyThrows
	public void createDocumentWithInfo(String testDataFolderPath) {
		createDocumentWithTaxonomyAndRelatedArticles(testDataFolderPath);

		if(!StringUtils.isNullOrEmpty(taxeneConfig)) {
			String taxeneConfigPath = testDataFolderPath + taxeneConfig;
			String taxeneConfig = readFile(taxeneConfigPath.startsWith(TEST_DATA_PATH), taxeneConfigPath);
			WriteRequest request = WriteRequest.builder().docId(doc.getDocId()).config(taxeneConfig).build();
			TaxeneConfigClient taxeneConfigClient = new TaxeneConfigClient(request);
			taxeneConfigClient.create(javax.ws.rs.core.Response.Status.OK);
			taxeneConfigClient.pollTaxeneConfig();
		}
	}

	@SneakyThrows
	private Document createDocumentWithTaxonomyAndRelatedArticles(String testDataFolderPath) {
		RestAssuredClient client = new RestAssuredClient();
		String documentJsonFilePath = testDataFolderPath + documentJsonFileName;
		String desiredDocumentData = readFile(documentJsonFilePath.startsWith(TEST_DATA_PATH), documentJsonFilePath);
		DataWithStatus<Document> addingDocumentRes = new GsonGenericDeserializer(given().spec(client.requestSpecification()).body(desiredDocumentData)
			.queryParam("synchSolr", false)
			.with().contentType(Version.V2.getContentType())
			.headers(client.headers())
			.post("/document"), Document.class).getResponse();
		doc = addingDocumentRes.getData();

		if(!StringUtils.isNullOrEmpty(relatedArticleJsonFileName)){
			String relatedArticleJsonFilePath = testDataFolderPath + relatedArticleJsonFileName;
			String rawRelatedArticleData = readFile(documentJsonFilePath.startsWith(TEST_DATA_PATH), relatedArticleJsonFilePath);
			String desiredRelatedArticleData = rawRelatedArticleData.replace("\"$docId\": 0", "\"docId\": " + doc.getDocId());
			DataWithStatus<Document> addingRecircCardRes = new GsonGenericDeserializer(given().spec(client.requestSpecification()).body(desiredRelatedArticleData)
				.with().contentType(Version.V2.getContentType())
				.headers(client.headers())
				.post("/article/related"), Document.class).getResponse();
			System.out.println("[INFO] Added Recirc Card with code: " + addingRecircCardRes.getStatus().getCode());
		}

		if(!StringUtils.isNullOrEmpty(taxonomy)) {
			String taxonomyJsonFilePath = testDataFolderPath + taxonomy;
			String rawTaxonomyData = readFile(documentJsonFilePath.startsWith(TEST_DATA_PATH), taxonomyJsonFilePath);
			String desiredTaxonomyData = rawTaxonomyData.replace("\"$docId\": 0", "\"docId\": " + doc.getDocId());
			DataWithStatus<Document> addingTaxonomy = new GsonGenericDeserializer(given().spec(client.requestSpecification()).body(desiredTaxonomyData)
					.with().contentType(Version.V2.getContentType())
					.headers(client.headers())
					.post("/taxene"), Document.class).getResponse();
			System.out.println("[INFO] Added taxonomy with code: " + addingTaxonomy.getStatus().getCode());
		}

		if(!StringUtils.isNullOrEmpty(meta)) {
			String metaJsonFilePath = testDataFolderPath + meta;
			String rawMetaData = readFile(documentJsonFilePath.startsWith(TEST_DATA_PATH), metaJsonFilePath);
			String desiredMetaData = rawMetaData.replace("\"$docId\": 0", "\"docId\": " + doc.getDocId());
			DataWithStatus<Meta> addingMetaData = new GsonGenericDeserializer(given().spec(client.requestSpecification()).body(desiredMetaData)
					.with().contentType("application/json")
					.headers(client.v1Headers())
					.post("/metadata"), Meta.class).getResponse();
			System.out.println("[INFO] Added metadata with code: " + addingMetaData.getStatus().getCode());

		}
		return doc;
	}


	public Document getDoc() {
		return doc;
	}

	public void deleteDocument() {
		RestAssuredClient client = new RestAssuredClient();
		Map<String, Object> params = new HashMap();
		params.put("docId", doc.getDocId());
		params.put("state", doc.getDocumentState().getState());
		params.put("activeDate", doc.getDocumentState().getActiveDate());
		params.put("forceMode", true);
		given().spec(client.requestSpecification())
			   .params(params)
			   .delete("/document");
	}

	@SneakyThrows
	public void revertDocumentAndMeta() {
		if(doc != null) {
			// delete the document if it was created as part of the test
			deleteDocument();
		} else {
			// update document and meta to stored document and meta
			if (!StringUtils.isNullOrEmpty(meta)) {
				new MetaClient().setDocumentMeta(docId, storeMeta);
			}
			updateDocumentWithData(updateActiveDate(storeDocument));
		}
	}

	@SneakyThrows
	public void updateDocument(String filePath) {
		String doc = readFile(filePath.startsWith(TEST_DATA_PATH), filePath);
		updateDocumentWithData(updateActiveDate(doc));
	}

	// update fails with trying to create a new version whose activeDate is before the currently active one
	// so we change activeDate to current time before update
	private String updateActiveDate(String document) {
		long now = Instant.now().toEpochMilli();
		String newActiveDate = "\"activeDate\"" + ": " + now;
		return document.replaceFirst("\"activeDate\":\\s+\\d{13}", newActiveDate);
	}

	public void updateDocumentWithData(Object data) {
		Map<String, Object> params = new HashMap<>();
		params.put("updateDisplayedDate", false);
		params.put("synchSolr", false);
		RestAssuredClient client = new RestAssuredClient();
		given().spec(client.requestSpecification()).body(data)
			   .with().contentType(Version.V2.getContentType())
			   .headers(client.headers()).params(params)
			   .put("/document").then().statusCode(OK.getStatusCode());
	}
}