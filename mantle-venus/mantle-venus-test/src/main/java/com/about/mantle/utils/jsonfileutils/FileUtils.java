package com.about.mantle.utils.jsonfileutils;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class FileUtils {

	@SneakyThrows
	public static synchronized String readFile(boolean readFromJar, String filepath) {
		final Logger logger = LoggerFactory.getLogger("CustomData");

		StringBuilder sb = new StringBuilder();
		BufferedReader br;
		// use getResourceAsStream when reading files from jar
		if(readFromJar) {
			InputStream is = org.apache.commons.io.FileUtils.class.getResourceAsStream("/" + filepath);
			br = new BufferedReader(new InputStreamReader(is));
		} else {
			File file = new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/src/main/resources/" + filepath);
			br = new BufferedReader(new FileReader(file));
		}
		String line;
		while(null != (line = br.readLine())) {
			if(line.startsWith("\"slug\"")){
				logger.info("Changing slug to lowercase(if any)since only lowercase values are accepted by Selene!");
				line = line.toLowerCase();
			}
			sb.append(line);
		}
		return StringUtils.stripAccents(sb.toString());
	}
}
