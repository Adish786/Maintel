package com.about.mantle.utils.test.data;

import com.amazonaws.util.StringUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestDataRule implements BeforeEachCallback{

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		//test data pre processing step where we check for the TestData annotation
		//if TestData annotation is set, this will read the <project>-test-data.json file
		//and store the original state and update the document for the test
		TestData testData = context.getRequiredTestMethod().getAnnotation(TestData.class);
		if(testData != null) {
			String testDataObjectName = testData.testData();
			Long docId;
			if(!StringUtils.isNullOrEmpty(testDataObjectName)) {
				String method = testData.method();
				synchronized(this) {
					TestDataFactory.DocumentUpdater updater = TestDataFactory.dataStore().get(testDataObjectName);
					if (updater == null) {
						updater = TestDataFactory.get(testDataObjectName);
						if (method.equals("PUT")) {
							updater.storeAndUpdateTestData();
						} else if (method.equals("POST")) {
							updater.createData();
						} else {
							throw new RuntimeException(method + " is not supported!");
						}
					}
				}

			} else {
				throw new RuntimeException("test data must not be null or empty!");
			}
		}
		
	}
}
