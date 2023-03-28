package com.about.mantle.utils.test.data;

import com.about.mantle.utils.test.model.TestDataObject;
import com.about.mantle.utils.url.URLUtils;
import com.about.venus.core.utils.ConfigurationProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.about.mantle.utils.jsonfileutils.FileUtils.readFile;
import static com.about.mantle.utils.test.data.TestDataUpdater.TEST_DATA_PATH;

/*
TestDataFactory is responsible for test data update
But in a manner that a testData object from <vertical>-test-data.json only gets updates once during the lifetime of
entire test execution
This is achieved by keeping a HashMap of singleton objects that makes sure that a certain TestData object is instantiated
only once as a Singleton
 */
public final class TestDataFactory {
	static Map<String, DocumentUpdater> dataStore =
			new ConcurrentHashMap<String, DocumentUpdater>();
	static final String DATA_FILE = ConfigurationProperties.getTargetProject(null) + "-test-data.json";
	static Map<String, TestDataObject> testDataObjectMap = null;

	public static final class DocumentUpdater {
		
		private final TestDataUpdater dataUpdater;
		
		private DocumentUpdater(String testDataObjectName) {
			this.dataUpdater = setDataUpdater(testDataObjectName);
		}

		/*
		retrieve docId, document and meta file path from <project>-test-data.json file
		and create/set TestDataUpdater for this DocumentUpdater
		 */
		public TestDataUpdater setDataUpdater(String testDataObjectName) {
			if(testDataObjectMap == null) {
				Type type = new TypeToken<Map<String, TestDataObject>>(){}.getType();
				testDataObjectMap = new Gson().fromJson(readFile(true, DATA_FILE), type);
			}
			TestDataObject testDataObject = testDataObjectMap.get(testDataObjectName);
			try {
				Long docId = null;
				if(testDataObject.getUrl() != null) {
					URLUtils urlUtils = new URLUtils(testDataObject.getUrl());
					docId = urlUtils.docIdFromUrl();
				}
				return new TestDataUpdater(docId, testDataObject.getTestData());
			} catch (NullPointerException npe) {
				throw new RuntimeException(testDataObjectName + " must be an entry in file " +
												   ConfigurationProperties.getTargetProject(null) + "-test-data.json");
			}
		}

		public TestDataUpdater getDataUpdater() {
			return dataUpdater;
		}

		/*
		preserve the original state when a test starts
		then update with the test data
		 */
		@SneakyThrows
		public void storeAndUpdateTestData() {
			dataUpdater.storeDocumentAndMeta();
			dataUpdater.updateDocumentAndMeta(TEST_DATA_PATH);
		}

		public void createData() {
			dataUpdater.createDocumentWithInfo(TEST_DATA_PATH);
		}

		/*
		revert back to original state at the end of tests
		 */
		public void revertData() {
			dataUpdater.revertDocumentAndMeta();
		}

	}

	public static DocumentUpdater get(String testDataObjectName) {
		synchronized (dataStore) {
			// makes sure DocumentUpdater is called only the first time for particular testDataObjectName
			DocumentUpdater testData = dataStore.get(testDataObjectName);
			if(testData == null) {
				testData = new DocumentUpdater(testDataObjectName);
				dataStore.put(testDataObjectName, testData);
			}
			return testData;
		}
	}

	public synchronized static Map<String, DocumentUpdater> dataStore() {
		return dataStore;
	}
}
