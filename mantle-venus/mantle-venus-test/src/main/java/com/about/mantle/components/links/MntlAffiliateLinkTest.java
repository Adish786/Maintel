package com.about.mantle.components.links;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.pages.MntlAffiliateLinkPage;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.about.mantle.venus.model.pages.MntlAffiliateLinkPage.LINK_DATA_ATTRIBUTE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class MntlAffiliateLinkTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	public static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(.*)\\}"); //matches ${any character sequence}

    /*
    This method will test an affiliate link:
        - validate if at least affiliate link is present
        If not in commerce or PRM it also will:
         - verify that no tokens (for e.g. ${DOC_ID}, ${REQUEST_ID} etc.) are present in a link's href.
         - identify what query params are being used and then verify their presence in a link's href
     */
    public Consumer<Runner> affiliateLinkTest = (runner) -> {
        MntlAffiliateLinkPage affiliateLinkTestPage = new MntlAffiliateLinkPage(runner.driver());
        affiliateLinkTestPage.waitFor().exactMoment(1, TimeUnit.SECONDS);
        List<WebElementEx> links = affiliateLinkTestPage.links();
        collector.checkThat("Links are not present on the page ", links.size(), is(not(0)));
        for (WebElementEx link : links) {
            collector.checkThat("Affiliate link " + link.href() + " has its " + LINK_DATA_ATTRIBUTE + " attribute empty", link.getAttribute(LINK_DATA_ATTRIBUTE), is(not(emptyOrNullString())));
            if (!ConfigurationProperties.getTargetProject("").equals("commerce") && !ConfigurationProperties.getTargetProject("").equals("performance-marketing")) {
                String dataAffiliateLinkRewriterValue = link.getAttribute(LINK_DATA_ATTRIBUTE);
                collector.checkThat("Affiliate link " + link.href() + " has token(s) in its href attribute", false, equalTo(TOKEN_PATTERN.matcher(link.href()).find()));
                
                String[] queryParams = dataAffiliateLinkRewriterValue.split("&");
                for (int i = 0; i < queryParams.length; i++) {
                	String queryParamKey = queryParams[i].split("=")[0];
                    collector.checkThat("Query param key " + queryParamKey + " is not present in the href " + link.href(), link.href(), containsString(queryParamKey));
                }
            }
        }
    };

}
