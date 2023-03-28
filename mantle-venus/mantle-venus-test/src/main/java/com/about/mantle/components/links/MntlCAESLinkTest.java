package com.about.mantle.components.links;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.ConfigurationProperties;
import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.openqa.selenium.By;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/* This test will validate CAES links
It gathers all the links containing 'mntl-commerce-button' and 'mntl-commerce-btn'.
It will first check if CAES link has `noskim nofollow` rel attributes and doesn't contain `nocaes`.
If the link is not caes then it will check the link has `nocaes` rel attribute.
If the link is not caes and sponsored it should have `nofollow sponsored` rel attributes.
Then depending on the type of element ('mntl-commerce-button' or 'mntl-commerce-btn') and retailer type it will validate
`noskim` rel attribute.*/
public abstract class MntlCAESLinkTest extends MntlVenusTest implements MntlCommonTestMethods {

    private HashSet<String> caesSponsoredList = getCAESSponsoredList();
    private HashSet<String> sponsoredList = getSponsoredList();

    public Consumer<Runner> caesSponsoredLinkTest = (runner) -> {
        List<WebElementEx> links = runner.page()
                .findElements(By.xpath("//a[contains(@class, 'mntl-commerce-button')] | //a[contains(@class, 'mntl-commerce-btn')]"));

        boolean isLinksFromCAESSponsoredListPresent = false;
        DocumentContext documentData = documentData(runner);
        for (WebElementEx link : links) {
            String domainName = null;
            try {
                domainName = getDomainName(link.href());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if ((domainName != null) && isLinkCAES(domainName)) {
                collector.checkThat("The link " + link.href() + " has a domain name in CAES_SPONSOREDLIST, but doesn't contain `noskim` in its `rel` attribute", link.rel(), containsStringIgnoringCase("noskim"));
                collector.checkThat("The link " + link.href() + " has a domain name in CAES_SPONSOREDLIST, but doesn't contain `nofollow` in its `rel` attribute", link.rel(), containsStringIgnoringCase("nofollow"));
                collector.checkThat("The link " + link.href() + " has a domain name in CAES_SPONSOREDLIST, but contains `nocaes` in its `rel` attribute", link.rel(), not(containsStringIgnoringCase("nocaes")));
                isLinksFromCAESSponsoredListPresent = true;
            } else {
                if (isLinkSponsored(domainName)) {
                    collector.checkThat("The link " + link.href() + " doesn't have a domain name in CAES_SPONSOREDLIST, but has in REVGROUP_SPONSOREDLIST and doesn't contain `nofollow` in its `rel` attribute", link.rel(), containsStringIgnoringCase("nofollow"));
                    collector.checkThat("The link " + link.href() + " doesn't have a domain name in CAES_SPONSOREDLIST, but has in REVGROUP_SPONSOREDLIST and doesn't contain `sponsored` in its `rel` attribute", link.rel(), containsStringIgnoringCase("sponsored"));
                }
                if (link.getAttribute("class").contains("mntl-commerce-button")) {
                    if (link.getAttribute("data-retailer-type") != null && !link.getAttribute("data-retailer-type").equalsIgnoreCase("SKIMLINKS")) {
                        collector.checkThat("The link " + link.href() + " with `mntl-commerce-button` doesn't have a domain name in CAES_SPONSOREDLIST and isn't a skimlinks link, but doesn't contain `noskim` in its `rel` attribute", link.rel(), containsStringIgnoringCase("noskim"));
                    } else collector.checkThat("The link " + link.href() + " with `mntl-commerce-button` doesn't have a domain name in CAES_SPONSOREDLIST and is a skimlinks link, but contains `noskim` in its `rel` attribute", link.rel(), not(containsStringIgnoringCase("noskim")));
                } else if (link.getAttribute("class").contains("mntl-commerce-btn")) {
                    String retailerType = getRetailerTypeFromDocument(documentData, link.getAttribute("href"));
                    if (!retailerType.equalsIgnoreCase("walmart") && !retailerType.equalsIgnoreCase("skimlinks")) {
                        collector.checkThat("The link " + link.href() + " with `mntl-commerce-btn` doesn't have a domain name in CAES_SPONSOREDLIST and isn't a skimlinks or walmart retailer type, but contains `noskim` in its `rel` attribute", link.rel(), containsStringIgnoringCase("noskim"));
                    } else collector.checkThat("The link " + link.href() + " with `mntl-commerce-btn` doesn't have a domain name in CAES_SPONSOREDLIST and is a skimlinks or walmart retailer type, but doesn't contain `noskim` in its `rel` attribute", link.rel(), not(containsStringIgnoringCase("noskim")));
                }

                collector.checkThat("The link " + link.href() + " doesn't have a domain name in CAES_SPONSOREDLIST, but doesn't contain `nocaes` in its `rel` attribute", link.rel(), containsStringIgnoringCase("nocaes"));
            }
        }
        collector.checkThat("Out of " + links.size() + " links none of the domain names can be found in CAES_SPONSOREDLIST", isLinksFromCAESSponsoredListPresent, is(true));

    };

    private boolean isLinkCAES(String link) {
        return caesSponsoredList.contains(link);
    }

    public boolean isLinkSponsored(String link) {
        return sponsoredList.contains(link);
    }

    private DocumentContext documentData(Runner runner) throws IndexOutOfBoundsException {
        DocumentContext documentData = null;
        try {
            if (!ConfigurationProperties.getTargetProject("").contains("mantle-ref")) {
                String url = runner.url().replace("?globeNoTest=true", "");
                documentData = SeleneUtils.getDocuments(url.split("-")[url.split("-").length - 1]);
            }
        } catch (com.jayway.jsonpath.PathNotFoundException e) {
            e.printStackTrace();
        }
        return documentData;
    }

    /* This method will return the retailer type of specific testing link */
    private String getRetailerTypeFromDocument(DocumentContext documentData, String href) {
        String retailerType = "";
        JSONArray commerceInfoString;
        try {
            commerceInfoString = documentData.read("$..commerceInfo.list[0]");
            for (Object data : commerceInfoString) {
                HashMap<?, ?> dataMap = (HashMap<?, ?>) data;
                if (dataMap.get("id").equals(href)) {
                    retailerType = dataMap.get("type").toString();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retailerType;
    }
}