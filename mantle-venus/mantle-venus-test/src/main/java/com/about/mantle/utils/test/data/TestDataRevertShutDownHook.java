package com.about.mantle.utils.test.data;

import java.util.concurrent.atomic.AtomicBoolean;

/*
Ensure test data revert shutdown hook added only once
 */
public class TestDataRevertShutDownHook {
	private static AtomicBoolean testDataShutDownHookAdded = new AtomicBoolean();

	private TestDataRevertShutDownHook(){};

	public static synchronized void addTestDataShutDownHook() {
		synchronized (TestDataRevertShutDownHook.class) {
			if(!testDataShutDownHookAdded.get()) {
				new TestDataRevertShutDownHook().setTestDataShutDownHook();
				testDataShutDownHookAdded.compareAndSet(false, true);
			}
		}
	}

	private void setTestDataShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// Revert documents to original state
			for(String testDataObjectName: TestDataFactory.dataStore().keySet()) {
				TestDataFactory.dataStore().get(testDataObjectName).revertData();
			}
		}));
	}

}
