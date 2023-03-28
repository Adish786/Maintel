package com.about.mantle.commerce;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlBylinesComponent;
import com.about.mantle.venus.model.pages.BylinesAndTaglinesPage;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;

public abstract class MntlBylineCNDTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String getDocId();
    private final String revenuePath = "$.data.revenueGroup";
    private final String updatedPath = "$.data.dates.updated";
    private final String lastPublishedPath = "$.data.dates.lastPublished";
    private final List<String> timeZone = new ArrayList<>(Arrays.asList("EDT","EST"));

    public Consumer<MntlRunner> commerceNewsDealsTimeStampTest = runner -> {

        BylinesAndTaglinesPage page = new BylinesAndTaglinesPage(runner.driver(), MntlBylinesComponent.class);
        DocumentContext seleneData = SeleneUtils.getDocuments(getDocId());
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        var revenueGroup = JsonPath.using(conf).parse(seleneData.jsonString()).read(revenuePath);
        if(revenueGroup != null && revenueGroup.toString().equalsIgnoreCase("commercenewsdeals")) {
            var updatedDate = JsonPath.using(conf).parse(seleneData.jsonString()).read(updatedPath);
            var firstPublishedDate = JsonPath.using(conf).parse(seleneData.jsonString()).read(lastPublishedPath);
            if(updatedDate != null || firstPublishedDate != null) {
                String seleneDate = updatedDate != null
                        ? seleneData.read(updatedPath).toString()
                        : seleneData.read(lastPublishedPath).toString();
                String bylineDate = formattedText(page.updatedStamps().get(0).text());
                String bylineFormattedDate = bylineDate.toUpperCase()
                        .replaceAll("EDT", "")
                        .replaceAll("EST","")
                        .trim();
                String formattedDate = epochToDate(seleneDate).trim();
                boolean hasTime = bylineFormattedDate.contains("PM") || bylineFormattedDate.contains("AM");
                boolean hasTimeZone = timeZone.stream().anyMatch(bylineDate.toUpperCase()::endsWith);
                collector.checkThat("The date displayed " + bylineFormattedDate + " is not equal to selene date " + formattedDate ,
                        bylineFormattedDate, is(formattedDate));
                collector.checkThat("Time is not displayed on the date" , hasTime, is(true));
                collector.checkThat("Time zone is not displayed on the date" , hasTimeZone, is(true));
            } else {
                Assert.fail("Document has no date on byline");
            }
        } else {
            Assert.fail("Revenue group is not Commerce News and Deals");
        }
    };

    private String epochToDate(String epoch){
        long date = Long.parseLong(epoch);
        DateFormat format = new SimpleDateFormat("MM/dd/yy hh:mma");
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return format.format(date);
    }

    private String formattedText(String text){
        return text.replace("Updated on","")
                   .replace("UPDATED ON", "")
                   .replace("Published on","")
                   .replace("PUBLISHED ON", "").trim();
    }
}
