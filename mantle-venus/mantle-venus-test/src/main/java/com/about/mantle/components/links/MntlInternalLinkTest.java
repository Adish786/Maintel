package com.about.mantle.components.links;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.utils.ConfigurationProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public abstract class MntlInternalLinkTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String setDomain();

    public Consumer<Runner> internalLinkNoFollowTest = runner -> {

        List<WebElement> links = runner.driver().findElements(By.tagName("a"));

        List<WebElement> internalLinks = links.stream()
                .filter( link -> link.getAttribute("href") != null)
                .filter( link -> link.getAttribute("href").startsWith(setDomain()) || link.getAttribute("href").startsWith(ConfigurationProperties.getTargetProjectBaseUrl(null)))
                .collect(Collectors.toList());

        for (WebElement internalLink : internalLinks) {
            collector.checkThat("Internal link " + internalLink.getAttribute("href") +" has nofollow ", internalLink.getAttribute("rel"), not(containsString("nofollow")));
        }
    };
}
