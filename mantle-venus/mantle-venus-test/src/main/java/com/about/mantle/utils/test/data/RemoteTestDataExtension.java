package com.about.mantle.utils.test.data;

import com.about.mantle.utils.test.model.TestDataObject;
import com.about.venus.core.utils.ConfigurationProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.about.mantle.utils.jsonfileutils.FileUtils.readFile;
import static com.about.mantle.utils.test.data.TestDataFactory.*;
import static com.about.venus.core.driver.selection.Environment.Local;
import static com.about.venus.core.driver.selection.Environment.Remote;

public class RemoteTestDataExtension implements BeforeAllCallback {
	// Gate keeper to prevent multiple Threads within the same routine
	private final static Lock lock = new ReentrantLock();
	private static AtomicBoolean createDocuments = new AtomicBoolean();
	private static final Logger logger = LoggerFactory.getLogger(RemoteTestDataExtension.class);

	@Override
	@SneakyThrows
	public void beforeAll(ExtensionContext extensionContext) {
		lock.lock();
		String baseUrl = ConfigurationProperties.getTargetProjectBaseUrl(null);
		boolean isCommerceVertical = !baseUrl.startsWith("https://commerce") && baseUrl.contains("commerce");
		// run only in remote aft or cicd v2 environment
		if(ConfigurationProperties.getEnvironment(Local).is(Remote) && !isCommerceVertical) {
			if (!createDocuments.get()) {
				createDocuments.compareAndSet(false, true);
				logger.info("Creating Documents before running tests!");
				if(testDataObjectMap == null) {
					Type type = new TypeToken<Map<String, TestDataObject>>(){}.getType();

					try {
						testDataObjectMap = new Gson().fromJson(readFile(true, DATA_FILE), type);
					} catch (RuntimeException e) {
						logger.error("Error reading file " + DATA_FILE);
						lock.unlock();
						return;
					}

					for (Map.Entry<String, TestDataObject> entry : testDataObjectMap.entrySet()) {
						String testData = entry.getKey();
						TestDataObject testDataObject = entry.getValue();
						if (testDataObject.getTestData() != null) {
							DocumentUpdater updater = dataStore.get(testData);
							if (updater == null) {
								updater = get(testData);

								try {
									updater.createData();
								} catch (RuntimeException e) {
									logger.error("Error posting data for " + testData);
								}

							}
						}
					}
				}
				// Selene gets overwhelmed when a lot of writing and reading is done simultaneously
				// Wait for some time after Document Creation
				logger.info("Done Creating Documents!");
				TimeUnit.SECONDS.sleep(5);
			}
		}
		lock.unlock();
	}
}
