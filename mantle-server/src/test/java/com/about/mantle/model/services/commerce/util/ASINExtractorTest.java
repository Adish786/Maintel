package com.about.mantle.model.services.commerce.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ASINExtractorTest {
	
	@Test
	public void test() {
		assertEquals("B004UU9ZB6", ASINExtractor.get("https://www.amazon.com/Fisher-Price-W1778-Harley-Davidson-Tough-Trike/dp/B004UU9ZB6/ref=sr_1_1"));
		assertEquals("B007VX11O8", ASINExtractor.get("https://www.amazon.com/Joe-Chipper-CJ601E-Electric-Shredder/dp/B007VX11O8?SubscriptionId=AKIAIH6BKLR7M6KSMDGQ&tag=aboutcom02landscaping-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=B007VX11O8/"));
		assertEquals("B007VX11O8", ASINExtractor.get("https://www.amazon.com/Joe-Chipper-CJ601E-Electric-Shredder/dp/B007VX11O8%3FSubscriptionId%3DAKIAIH6BKLR7M6KSMDGQ%26tag%3Daboutcom02landscaping-20%26linkCode%3Dxm2%26camp%3D2025%26creative%3D165953%26creativeASIN%3DB007VX11O8/"));
		//TODO: to be addressed in https://iacpublishing.atlassian.net/browse/GLBE-6107
		//assertEquals("B01ARGBKP0", ASINExtractor.get("https://www.amazon.com/Mega-Bloks-American-Saigeâ€s-Studio/dp/B01ARGBKP0/"));
		assertNull(ASINExtractor.get("https://www.amazon.com/dp/foo/bar/baz"));
		assertNull(ASINExtractor.get("https://www.amazon.com/Mega-Bloks-American-Isabelles-Construction/dp/B01ARGBBB/"));
	}

}
