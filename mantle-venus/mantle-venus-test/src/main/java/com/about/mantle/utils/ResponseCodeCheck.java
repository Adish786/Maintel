package com.about.mantle.utils;

import com.about.mantle.venus.utils.MntlUrl;
import org.junit.jupiter.api.Assertions;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResponseCodeCheck {
        public static void sendRequest(String url, String requestMethod, String params, int expectedCode) {

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
            Assertions.fail("There was an error while getting SSL instance");
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            Assertions.fail("There was an error while initialising certificates");
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        String finalUrl = new MntlUrl(url + "?" + params,true).url();
        URL postUrl = null;
        try {
            postUrl = new URL(finalUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Assertions.fail("The " + url + " seems to be malformed");
        }
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) postUrl.openConnection();
            con.setInstanceFollowRedirects(false);
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("There seems to be error while requesting for url  " + url);
        }

        try {
            con.setRequestMethod(requestMethod);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Assertions.fail("protocol can only be GET, POST, HEAD for this test and it seems invalid protocol was provided");
        }
        int code = 0;
        try {
            code = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            Assertions.fail("There seems to be error while reading the code");
        }
        assertThat("The " + requestMethod + " for params " + params + "is incorrect", code, is(expectedCode));
        con.disconnect();

    }

}
