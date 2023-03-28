package com.about.mantle.seo;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

public abstract class MntlBylineLinksDoFollowAttributesTest extends MntlVenusTest implements MntlCommonTestMethods{
	
    abstract public String setDomain();
    
    
    private ArrayList<String> safeList = getSafeList();
	private HashSet<String> caesSponsoredList = getCAESSponsoredList();

	protected void bylineLinksAttributeTest(WebElementEx element, WebDriverExtended driver) {

		element.scrollIntoViewCentered();
		element.waitFor().aMoment();
		String linkHrefAttribute = element.getAttribute("href");
		String domainName = null;
		try {
			domainName = getDomainName(linkHrefAttribute);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (!safeList.contains(domainName) && !domainName.contains(setDomain())) {
			collector.checkThat("bio link is blank", element.getAttribute("rel"), containsString("noopener nofollow"));
		} else if (!isLinkCAES(domainName)) {
			collector.checkThat("bio link doesn't have `nocaes` rel attribute", element.getAttribute("rel"), containsString("nocaes"));
		} else {
			collector.checkThat("bio link has rel attributes", element.getAttribute("rel"), is(""));
		}

	}

	private boolean isLinkCAES(String link) {
		return caesSponsoredList.contains(link);
	}

}
