package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.about.venus.core.utils.NetworkCallFilterUtils.request;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class MntlRTBBiddersTest extends MntlVenusTest implements MntlCommonTestMethods{
	
	protected abstract String filter();
	
	protected abstract org.hamcrest.Matcher matcher();
	

	protected Consumer<MntlRunner> testRTBBiddersUS = (runner) -> {
			VenusHarRequest requestUrl = request(runner.driver(), runner.proxyPage(), 1,
					Collections.singletonList(filter()));
			assertThat("RTB Bidders request is not being made for US location", requestUrl.url().url(), matcher());
		
	};
	
	
	protected Consumer<MntlRunner> testRTBBiddersNonUS = (runner) -> {
		List<VenusHarEntry> entries = runner.proxy().capture().page(runner.proxyPage()).entries(filter()).stream()
				.collect(Collectors.toList());
		assertThat("RTB Bidders request is being made for non US location", entries.size(), is(0));
	};
	
	

}
