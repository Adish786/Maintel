package com.about.mantle.components.links;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.ReadJsonData;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.ConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.openqa.selenium.By;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/*
This will test if `nofollow` and `sponsored` are present in `rel` attributes of the links with
domains that are in `REVGROUP_SPONSOREDLIST` in certain templates:
 - a vertical test suppose to use a custom document that has sponsored links, passing a json into the test
 - we will check if we are using a correct template
 - we will collect the links from a page that have hrefs and don't have `data-type` (which is an attribute for internal links)
 - we will check if a domain name is in the `REVGROUP_SPONSOREDLIST` and if it is, will validate presence of `nofollow` and `sponsored`
 - there should be at least one link that satisfies all conditions added to the page - please add this to the custom doc before saving it.
 */
public abstract class MntlSponsoredLinkTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String setDomain();

    private HashSet<String> sponsoredList = getSponsoredList();

    public Consumer<Runner> sponsoredLinkTest = (runner) -> {

        assertThat("Please utilize one of the templates that has revenue group: ", isRevenueGroupTemplate(runner), is(true));

        List<WebElementEx> links = runner.page().findElements(By.tagName("a"));

        List<WebElementEx> filteredLinks = links.stream()
                .filter( link -> link.getAttribute("href") != null)
                .filter( link -> !link.getAttribute("href").contains(setDomain()) && !link.getAttribute("href").contains(ConfigurationProperties.getTargetProjectBaseUrl(null)))
                .filter( link -> !link.getAttributes().containsKey("data-type"))
                .collect(Collectors.toList());

        Boolean isLinksFromSponsoredListPresent = false;
        for (WebElementEx link : filteredLinks) {
            String domainName = null;
            try {
                domainName = getDomainName(link.href());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if ((domainName != null) && isLinkSponsored(domainName)) {
                collector.checkThat("The link" + link.href() + " has a domain name in a SPONSOREDLIST link doesn't contain `nofollow` in its `rel` attribute", link.rel(), containsStringIgnoringCase("nofollow"));
                collector.checkThat("The link" + link.href() + " has a domain name in a SPONSOREDLIST link doesn't contain `sponsored` in its `rel` attribute", link.rel(), containsStringIgnoringCase("sponsored"));
                isLinksFromSponsoredListPresent = true;
            }
        }

        collector.checkThat("Out of " + filteredLinks.size() + " links none of the domain names can be found in REVGROUP_SPONSOREDLIST", isLinksFromSponsoredListPresent, is(true));

    };

    public boolean isLinkSponsored(String link) {
        boolean isLinkSponsored = false;
        if (sponsoredList.contains(link))
            isLinkSponsored = true;
        return isLinkSponsored;
    }

    private Boolean isRevenueGroupTemplate(Runner runner) {
        Boolean isRevenueGroupTemplate = false;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data;
        String revenueGroupItem;
        try {
            InputStream inputStream = ReadJsonData.class.getResourceAsStream(runner.testData().toString());
            data = objectMapper.readValue(inputStream, HashMap.class);
            revenueGroupItem = JsonPath.read(data, "$.revenueGroup").toString();
            if (!revenueGroupItem.isEmpty()) isRevenueGroupTemplate = true;
        } catch (Exception e) {
            throw new RuntimeException("Error reading json data from: " + runner.testData().toString(), e);
        }
        return isRevenueGroupTemplate;
    }

}
