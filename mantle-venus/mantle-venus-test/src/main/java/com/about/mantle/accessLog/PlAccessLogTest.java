package com.about.mantle.accessLog;

import com.about.hippodrome.url.ExternalUrlData;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlUrl;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Random;

public abstract class PlAccessLogTest extends MntlVenusTest {
	
	private Random random = new Random();
	protected abstract String getUrl();

	private ExternalUrlData urlParts(String url) {
		try {
			return new MntlUrl(url).urlBuilder(url).environment(null).query(null).build();
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			e.printStackTrace();

		}
		return null;
	}

}