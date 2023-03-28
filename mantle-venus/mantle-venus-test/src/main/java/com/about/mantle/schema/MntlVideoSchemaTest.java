package com.about.mantle.schema;

import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataLayerUtils;
import com.jayway.jsonpath.DocumentContext;
import io.restassured.path.json.JsonPath;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;

public class MntlVideoSchemaTest extends ArticleSchemaTest {

    /**
     * The test validates video schema is enabled if there's inline video content block;
     * video schema is disabled (video is null) if there's primary video or no video present on the page at all.
     * @param driver
     * @param url
     * */
    protected void testVideoSchema(WebDriverExtended driver, String url) {
        JsonPath schema = new JsonPath(getSchemas(driver, url).get(0));

        // Retrieving unifiedPageview event and getting docId
        Map<String, Object> unifiedPageViewMap = DataLayerUtils.dataLayerEvents(driver, "unifiedPageview").get(0);
        String docId = String.valueOf(unifiedPageViewMap.get("documentId"));
        DocumentContext documentData = SeleneUtils.getDocuments(docId);

        // Checking if document data has primary video
        boolean hasPrimaryVideo = false;
        try {
            hasPrimaryVideo = !ObjectUtils.isEmpty(documentData.read("$.data.primaryVideo"));
        } catch (Exception ignored){}

        List<WebElementEx> inlineVideos = driver.findElementsEx(By.cssSelector(".article-content .mntl-sc-block-inlinevideo"));
        if (inlineVideos.size() > 0) {
            collector.checkThat("Video schema is disabled for inline video", schema.get("video.'@type'[0]"), not(nullValue()));
        } else if (hasPrimaryVideo) {
            collector.checkThat("Video schema is not disabled for primary video", schema.get("video.'@type'[0]"), is(nullValue()));
        } else {
            collector.checkThat("Video schema is not disabled for page with no primary or inline video", schema.get("video.'@type'[0]"), is(nullValue()));
        }
    }

    /**
     * Method validated that the UpdateDate on Schema.org is in EST time and is same as the firstPublished date from Selene.
     * @param driver
     * @param url
     */
    protected void testVideoSchemaDateValidation(WebDriverExtended driver, String url) {
        JsonPath schema = new JsonPath(getSchemas(driver, url).get(0));

        // Retrieving unifiedPageview event and getting docId
        Map<String, Object> unifiedPageViewMap = DataLayerUtils.dataLayerEvents(driver, "unifiedPageview").get(0);
        String docId = String.valueOf(unifiedPageViewMap.get("documentId"));
        DocumentContext documentData = SeleneUtils.getDocuments(docId);
        List<WebElementEx> inlineVideos = driver.findElementsEx(By.cssSelector(".article-content .mntl-sc-block-inlinevideo"));
        if (!inlineVideos.isEmpty()) {
            ArrayList components = new ArrayList(documentData.read("$.data.pages.list[0].contents.list"));
            String videoUpdatedDateEpochTime = (Arrays.stream(components.stream().filter((component) -> {
                return component.toString().contains("type=INLINEVIDEO");
            }).findFirst().get().toString().split(",")).filter((key) -> {
                return key.contains("firstPublished");
            }).findFirst().get()).split("=")[1];
            Date date = new Date(Long.parseLong(videoUpdatedDateEpochTime));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            format.setTimeZone(TimeZone.getTimeZone("EST"));
            String formattedDate = format.format(date);
            this.collector.checkThat("Video schema has incorrect update date", schema.get("video.uploadDate[0]").toString().contains(formattedDate), is(true));
        }
    }
}