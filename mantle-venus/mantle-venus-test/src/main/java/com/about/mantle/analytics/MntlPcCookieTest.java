package com.about.mantle.analytics;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.utils.CookieUtils;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;

public class MntlPcCookieTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	protected Consumer<Runner> pcCookieTest = runner -> {
		collector.checkThat("pc cookie value is not correct", Integer.parseInt(CookieUtils.cookieValue(runner.driver(), "pc")), is(1));
		runner.driver().navigate().refresh();
		runner.page().waitFor().documentComplete();
		collector.checkThat("pc cookie value is not correct on the second article after refresh", Integer.parseInt(CookieUtils.cookieValue(runner.driver(), "pc")), is(2));
	};
	
	protected Consumer<Runner> pcCookieSearchPageTest = runner -> {
		collector.checkThat("pc cookie value is not correct", Integer.parseInt(CookieUtils.cookieValue(runner.driver(), "pc")), is(2));
	};

}
