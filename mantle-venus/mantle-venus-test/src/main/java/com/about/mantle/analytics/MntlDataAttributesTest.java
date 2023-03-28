package com.about.mantle.analytics;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlDataAttributes;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataAttributes;

import static org.hamcrest.CoreMatchers.is;

public class MntlDataAttributesTest extends MntlVenusTest {

	public void testLinkDataAttributes(WebElementEx link, MntlDataAttributes attributes) {
		DataAttributes linkData = new DataAttributes(link);
		collector.checkThat("link data type is not correct", linkData.type(), is(attributes.getType()));
		collector.checkThat("link data component is not correct", linkData.component(), is(attributes.getComponent()));
		collector.checkThat("link data source is not correct", linkData.source(), is(attributes.getSource()));
		collector.checkThat("link data ordinal is not being set", linkData.ordinal(), is(attributes.getOrdinal()));
		collector.checkThat("link rel attribute is not correct", linkData.name("rel"), is(attributes.getRel()));
		collector.checkThat("link target attribute is not correct", linkData.name("target"), is(attributes.getTarget()));
	}
	
}
