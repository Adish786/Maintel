package com.about.mantle.venus.utils;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaLogs {

	private KafkaConsumer<String, String> consumer;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(KafkaLogs.class);
	private List<String> entries = new ArrayList<>();
    private List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
    private static final String kafkaNodesList = "kafka-qa.ops.k8s.use1.dotdash.com:31090,"
			+ "kafka-qa.ops.k8s.use1.dotdash.com:31091," + "kafka-qa.ops.k8s.use1.dotdash.com:31092,"
			+ "kafka-qa.ops.k8s.use1.dotdash.com:31093";
	private Properties setProperties() {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaNodesList);
	    props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "90000");
		props.put("group.id", "grpId" + (new Random()).nextInt(1000000));
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	public KafkaLogs(String topic) {
		this.consumer = new KafkaConsumer<String, String>(setProperties());
		this.consumer.subscribe(Collections.singletonList(topic));
	}

	public List<String> logEntries(String userId, String layout,String path) {
		long startTime = System.currentTimeMillis();
		int bufferSize = 0;
			while (entries.size() == 0 && (System.currentTimeMillis() - startTime) < 90000) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
				for (ConsumerRecord<String, String> record : records) {
					
					if (record.value().contains(userId) && record.value().contains(layout) && record.value().contains(path))
						buffer.add(record);
				}
				bufferSize = buffer.size();
				if (bufferSize > 0) {
					for (ConsumerRecord<String, String> record : buffer) {
						Entry recordObj = null;
						try {
							recordObj = objectMapper.readValue(record.value(), Entry.class);
						} catch (IOException e) {
							logger.error("Json mapping error: " + record, e);
							e.printStackTrace();
						}
						if (recordObj.getUserId().equals(userId) && recordObj.getLayout().equals(layout) && recordObj.getUrl().getPath().equals(path)) 
							{ 
							logger.info("Entry selected for test : "+record.value());
							entries.add(record.value());
							}
						
					}
					consumer.commitSync();
					buffer.clear();
				}
			}
			assertThat("No Entry was found in Kibana with "+layout+ " and  given path "+path+", userId and Layout eventhough the kafka buffer size was " + bufferSize, entries.size(),
					not(0));
			return entries;
	}

}
