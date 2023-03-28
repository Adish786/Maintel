package com.about.mantle.components.links;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.selene.vertical.Vertical;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.structuredContent.MntlFeaturedLinkComponent;
import com.about.mantle.venus.model.pages.MntlFeaturedLinkPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.ConfigurationProperties;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;

/*
This test will check featured links on:
1. Whether the links that are not on the safelist contain `nofollow`;
2. Whether the links that are on both the safelist and the sponsored list has `nofollow`
(for sponsored link the template should have revenue group).
 */
public abstract class MntlFeaturedLinkTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String setDomain();

    private ArrayList<String> safeList = getSafeList();

    private HashSet<String> sponsoredList = getSponsoredList();

    public Consumer<Runner> featuredLinkNoFollowTest = (runner) -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlFeaturedLinkPage page = new MntlFeaturedLinkPage(runner.driver());
        List<MntlFeaturedLinkComponent> featuredComponents = page.featuredLinkContent();
        List<WebElementEx> featuredLinks = featuredComponents.stream().flatMap(comp -> comp.links().stream()).collect(Collectors.toList());
        collector.checkThat("There are no featured links present ", featuredLinks.size(), not(0));
        if (featuredLinks.size() > 0) {
            for (WebElementEx link : featuredLinks) {
                String linkHrefAttribute = link.getAttribute("href");
                String domainName = null;
                try {
                    domainName = getDomainName(linkHrefAttribute);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                // checking if featured link is cross-vertical
                boolean isValidEnum = false;
                for (Vertical vertical : Vertical.values()) {
                    if (vertical.domainValue().contains(domainName)) {
                        isValidEnum = true;
                        break;
                    }
                }

                if (isValidEnum && !domainName.contains(setDomain() + ".com")) {
                    collector.checkThat("Cross-vertical featured link " + linkHrefAttribute + " does not contain nofollow ", link.getAttribute("rel"), containsString("nofollow"));
                } else
                    // checking if featured link is internal
                    if (linkHrefAttribute.contains(setDomain()) || linkHrefAttribute.replace("www-", "").startsWith(ConfigurationProperties.getTargetProjectBaseUrl(null))) {
                        collector.checkThat("Internal featured link " + linkHrefAttribute + " does contain nofollow ", link.getAttribute("rel"), not(containsString("nofollow")));
                    } else
                        // checking if featured external link is present on the safelist or not
                        if (!isLinkSafe(domainName)) {
                            collector.checkThat("External featured link " + linkHrefAttribute + " that is NOT on the safelist does not contain nofollow ", link.getAttribute("rel"), containsString("nofollow"));
                        } else
                            // checking if featured external link is present on the sponsored list and the template has revenue group
                            if (isLinkSponsored(domainName) && hasRevenueGroup(runner.driver())) {
                                collector.checkThat("External featured link " + linkHrefAttribute + " that IS On the safelist AND On the sponsored list does not contain nofollow ", link.getAttribute("rel"), containsString("nofollow"));
                            } else
                                collector.checkThat("External featured link " + linkHrefAttribute + " that IS On the safelist does contain nofollow ", link.getAttribute("rel"), not(containsString("nofollow")));
            }
        }
    };

    public boolean isLinkSafe(String link) {
        boolean isLinkSafe = false;
        if (safeList.contains(link)) isLinkSafe = true;
        return isLinkSafe;
    }


    public boolean isLinkSponsored(String link) {
        boolean isLinkSponsored = false;
        if (sponsoredList.contains(link))
            isLinkSponsored = true;
        return isLinkSponsored;
    }

    private boolean hasRevenueGroup(WebDriverExtended driver) {
        boolean hasRevenueGroup = false;
        ArrayList unifiedPageView =
                driver.executeScript("return dataLayer.filter(function(dl){return dl.event=='unifiedPageview'})");
        int documentNode = 0;
        Map unifiedPageViewObj = (Map) unifiedPageView.get(documentNode);

        if (!unifiedPageViewObj.get("revenueGroup").toString().isEmpty()) {
            hasRevenueGroup = true;
        }
        return hasRevenueGroup;
    }

}