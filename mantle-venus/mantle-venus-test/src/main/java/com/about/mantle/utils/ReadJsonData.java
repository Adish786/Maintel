package com.about.mantle.utils;

import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.utils.ConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReadJsonData {
    private static final String JSON_FILE = "/" + ConfigurationProperties.getTargetProject(null) + "-test-data.json";
    private static final Logger logger = LoggerFactory.getLogger(ReadJsonData.class);
    private static Map<String, Object> data = initlizeData();

    private static Map<String, Object> initlizeData() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data;
        try {
            InputStream inputStream = ReadJsonData.class.getResourceAsStream(JSON_FILE);
            data = objectMapper.readValue(inputStream, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading json data from: " + JSON_FILE, e);
        }
        return data;
    }

    public static Map<String, Object> data() {
        return data;
    }

    public static void storeUrlsToMap(Map<String, Object> jsonData) {
    	Iterator iter = jsonData.entrySet().iterator();
    	while (iter.hasNext()) {
	    	Map.Entry entry = (Map.Entry) iter.next();
	    	Map<String, String> urlMap = (Map<String, String>) entry.getValue();
	    	   try {
		        	cacheWarmUp(urlMap.get("url").toString());  
		        } catch (IOException e) {
					logger.warn("cache warmup issues for url  " + urlMap.get("url").toString());
					e.printStackTrace();
				}
    	}
    }
    
    private static void cacheWarmUp(String url) throws IOException {
		// code to ignore ssl issues starts here
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(X509Certificate[] certs, String authType) { }
			public void checkServerTrusted(X509Certificate[] certs, String authType) { }
		} };
		// Install the all-trusting trust manager
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// code to ignore ssl issues ends here
		String urlUnderTest = new MntlUrl(url, null, null, true).url();
		logger.info(urlUnderTest);
		URL obj = new URL(urlUnderTest);
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			logger.info("cache warmed for url : " + urlUnderTest);
		} else {
			logger.warn("url  = " + urlUnderTest + "does not return a 200 response code, actual GET response code is " + responseCode);
		}
	}

}
