package com.about.mantle.utils.test.data;

import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.meta.client.MetaClient;
import com.about.mantle.utils.selene.meta.request.ReadRequest;
import com.about.mantle.utils.selene.version.Version;
import com.about.mantle.utils.test.model.TestDataObject;
import com.about.mantle.utils.url.URLUtils;
import com.about.venus.core.utils.ConfigurationProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Map;

import static com.about.mantle.utils.jsonfileutils.FileUtils.readFile;
import static com.about.mantle.utils.test.data.TestDataUpdater.TEST_DATA_PATH;
import static io.restassured.RestAssured.given;

/*
use this class to generate test data json files to be used by tests as base line
it will read all documents present in <project>-test-data.json and store document and meta GET responses in corresponding json files
use only when all tests are succeeding because this test data will be used to create test data every time a test runs
 */
public class TestDataJsonGenerator {

	Logger logger = LoggerFactory.getLogger(TestDataJsonGenerator.class);

	private static final String DATA_FILE = ConfigurationProperties.getTargetProject(null) + "-test-data.json";
	private static final String projectBaseUrl = ConfigurationProperties.getTargetProjectBaseUrl(null);

	public void generateTestDataFiles() {
		File testDataDir = new File(getFullPath(TEST_DATA_PATH));
		if(!testDataDir.exists()) {
			testDataDir.mkdirs();
		}
		Type type = new TypeToken<Map<String, TestDataObject>>(){}.getType();
		Map<String, TestDataObject> testDataObjectMap = new Gson().fromJson(readFile(true, DATA_FILE), type);
		for(String testData: testDataObjectMap.keySet()) {
			TestDataObject testDataObject = testDataObjectMap.get(testData);
			Long docId = new URLUtils(testDataObject.getUrl()).docIdFromUrl();
			String response = null;

			if(docId != null)
				response = getDocument(docId);
			else {
				try {
					String prodDocUrl = getProjectProdDomain(projectBaseUrl) + testDataObject.getUrl();
					response = getDocument(prodDocUrl);
				}catch (Exception e) {
					logger.error("Error when encode production Doc Url");
				}
			}
			if(response != null) {
				String documentFileName = testData.toLowerCase() + "-doc.json";
				writeToFile(testDataDir.getPath() + "/" + documentFileName, getData(response));
			}
			MetaClient metaClient = new MetaClient();
			ReadRequest metaRequest = ReadRequest.builder().docId(docId).build();
			String meta = metaClient.read(metaRequest);
			if(meta != null) {
				String metaFileInfo = testData.toLowerCase() + "-meta.json";
				writeToFile(testDataDir.getPath() + "/" + metaFileInfo, getData(meta));
			}
		}
	}

	public String getData(String jsonString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(jsonString);
		JsonElement data = je.getAsJsonObject().get("data");
		return gson.toJson(data);
	}

	@FunctionalInterface
	interface GetDocument<ParamType, ParamValue, Result> {
		Result from(ParamType paramType, ParamValue paramValue);
	}

	public <T> String getDocument(T queryParam) {
		GetDocument<String, T, String> getDocData = (paramType, paramValue) -> {
			RestAssuredClient client = new RestAssuredClient();
			io.restassured.response.Response response = given()
				.spec(client.requestSpecification())
				.param(paramType, paramValue)
				.headers(client.headers())
				.with().contentType(Version.V2.getContentType()).get("/document");
			if(response.statusCode() == Response.Status.OK.getStatusCode()) {
				return response.prettyPrint();
			}
			logger.warn("Document could not be retrieved for given url/docID");
			return null;
		};
		if(queryParam instanceof Long)
			return getDocData.from("docId", queryParam);
		else
			return getDocData.from("url", queryParam);
	}

	public String getFullPath(String fileName) {
		String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
		return cwd + "/src/main/resources/" + fileName;
	}

	@SneakyThrows
	public void writeToFile(String filePath, String data) {
		File file = new File(filePath);
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(data);
		writer.close();
	}

	private String getProjectProdDomain(String baseUrl) {
		String [] segmentUrl = baseUrl.split("\\.");
		return "https://www." + segmentUrl[segmentUrl.length-2] + "." + segmentUrl[segmentUrl.length-1];
	}

}
