package com.about.mantle.venus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessLog {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessLog.class);
	private Thread thread;
	public static final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private final List<Entry> entries = new ArrayList<Entry>();

	public AccessLog(KafkaLogs kfk, String userId, String layout, String path) {
		thread = new Thread(() -> {
			kfk.logEntries(userId,layout,path).stream().map((record) -> {
				try {
					return objectMapper.readValue(record, Entry.class);
				} catch (Exception e) {
					logger.error("Json mapping error: " + record, e);
					return Entry.NULL;
				}
			}).collect(Collectors.toCollection(() -> entries));
		});
		thread.setPriority(10);
		thread.start();
	}
	
	private void waitforDataToBeCollected() {
		logger.trace("waiting for data to be collected");
		try {
			if(!thread.isInterrupted()) thread.join();
		} catch (InterruptedException e) {
			logger.debug("InterruptedException", e);
		}
	}
	

	public Entry getEntry(String path) {
		waitforDataToBeCollected();
		return entries.stream().filter((entry -> path.equals(entry.getUrl().getPath()))).findFirst().orElse(null);
	}

	public Entry getLayoutPvEntry(String path, String layout) {
		waitforDataToBeCollected();
		return entries.stream().filter(entry -> path.equals(entry.getUrl().getPath())).filter(entry -> entry.getIsPv())
				.filter(entry -> entry.getLayout().equals(layout)).findFirst().orElse(null);
	}
	
	public List<Entry> getLayoutPvEntries(String path, String layout) {
		waitforDataToBeCollected();
		logger.info("current log size is" + entries.size());
		return entries.stream().filter(entry -> path.equals(entry.getUrl().getPath())).filter(entry -> entry.getIsPv())
				.filter(entry -> entry.getLayout().equals(layout)).collect(Collectors.toList());
	}
}