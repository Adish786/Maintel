package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.useragent.UserAgent;
import com.about.venus.core.utils.HTMLUtils;
import com.about.venus.core.utils.Url;
import com.gargoylesoftware.htmlunit.TextPage;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({ "deprecation", "rawtypes" })
public abstract class MntlAdsTxtTest extends MntlVenusTest implements MntlCommonTestMethods {
    protected abstract String url();

    // Verticals need to return their account pattern string that they want to test against
    // e.g. ".*google.com, pub-5995202563537249, RESELLER, f08c47fec0942fa0.*" is an approprate regex for a full
    // line match
    protected abstract String getGoogleAccountPattern();

    public void testAdsTxt() {
        TextPage page = null;
        try {
            page = (TextPage) HTMLUtils.page((new Url(this.url(), true)).url(), UserAgent.DESKTOP.userAgent(), this.webClient());
        } catch (Exception e) {
            Assert.fail("TextPage could not have been initialized");
        }
        String adsTxt = StringEscapeUtils.escapeJava(page.getContent());
        String commentPatternDDM = ".*#Ads.txt Dotdash Meredith.*";
        String googlePattern = ".*google.com.*";
        String googleAccountPattern = this.getGoogleAccountPattern();

        Pattern regexCommentPatternDDM = Pattern.compile(commentPatternDDM);
        Matcher matcherCommentPatternDDM = regexCommentPatternDDM.matcher(adsTxt);
        Pattern regexGooglePattern = Pattern.compile(googlePattern);
        Matcher matcherGooglePattern = regexGooglePattern.matcher(adsTxt);
        Pattern regexGoogleAccountPattern = Pattern.compile(googleAccountPattern);
        Matcher matcherGoogleAccountPattern = regexGoogleAccountPattern.matcher(adsTxt);

        assertThat("Ads.txt comment exists for Dotdash Meredith", matcherCommentPatternDDM.matches(), is(true));
        assertThat("Ads.txt contains google entries", matcherGooglePattern.matches(), is(true));
        assertThat("Ads.txt contains an exact google pub ID", matcherGoogleAccountPattern.matches(), is(true));
    }
}