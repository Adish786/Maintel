package com.about.mantle.components.article;

import com.about.mantle.components.image.MntlImageTest;
import com.about.mantle.venus.model.components.article.MntlBylineTaglineItemComponent;
import com.about.mantle.venus.model.components.article.MntlBylinesComponent;
import com.about.mantle.venus.model.components.article.MntlDynamicTooltipComponent;
import com.about.mantle.venus.model.components.article.MntlUpdatedStampBylinesComponent;
import com.about.mantle.venus.model.components.navigation.MntlSocialFollowNavComponent;
import com.about.mantle.venus.model.pages.BylinesAndTaglinesPage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.test.ErrorCollectorEx;
import com.about.venus.core.utils.ConfigurationProperties;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.hamcrest.Matchers;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import static org.hamcrest.MatcherAssert.assertThat;

/*
This will test all the elements of a new `bylines` component:
    - `mntl-bylines` component is present,
    - `mntl-taglines` component is present when `hasTagline` flag is set to true.
    For any item in bylines or taglines it will:
        - validate whether the Author's names, dates and descriptors are displayed,
        - triggers dynamic tooltip and validates it's elements,
        - validate whether any links contain author's name, `rel` tag and start with `https://`,
        - if there is a Social Follow component present, it will validate it's elements;
        - `rel` tag contains `noopener nofollow` if it's not in the safelist and the domain name doesn't contain the project url;
          `nocaes` if doesn't exist in the CAES_SPONSOREDLIST; and doesn't contain `sponsored` even if exists in sponsored list
        - fact checked badge and description validation.
 */
public class MntlBylinesTest extends MntlImageTest {

    private static String SCHEMA_SELECTOR = "script[type=\"application/ld+json\"]";
    private HashSet<String> sponsoredList = getSponsoredList();
    private HashSet<String> caesSponsoredList = getCAESSponsoredList();
    private ArrayList<String> safeList = getSafeList();
    protected boolean checkNoCaesRel = true;

    protected BiConsumer<Runner, Boolean> bylinesTaglinesTest = (runner, hasTagline) -> {
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        DocumentContext documentData = documentData(runner);
        BylinesAndTaglinesPage page = new BylinesAndTaglinesPage(runner.driver(), MntlBylinesComponent.class);
        List<BylinesAndTaglinesPage.BylineTaglineGroup> bylinesTaglinesGroups = page.bylineGroups();
        SchemaComponent schemaComp = runner.page().head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
        collector.checkThat("There are no bylines present on the page", bylinesTaglinesGroups.size(), not(0));
        if (hasTagline) {
            collector.checkThat("There are no taglines present on the page", page.taglineGroups().size(), not(0));
            bylinesTaglinesGroups.addAll(page.taglineGroups());
        }
        List<MntlUpdatedStampBylinesComponent> updatedStamps = page.updatedStamps();
        collector.checkThat("There were either no time stamps or more that one time stamps found in the bylines", updatedStamps.size(), is(1));
        if (updatedStamps.size() == 1)
            dateValidations(runner, schemaComp, updatedStamps.get(0), documentData);
        for (BylinesAndTaglinesPage.BylineTaglineGroup bylinesTaglinesGroup : bylinesTaglinesGroups) {
            List<MntlBylineTaglineItemComponent> bylineTaglineItems = new ArrayList<>();
            if (bylinesTaglinesGroup.getElement().className().contains("taglines"))
                bylineTaglineItems.addAll(bylinesTaglinesGroup.taglineItems());
            else bylineTaglineItems.addAll(bylinesTaglinesGroup.bylineItems());
            collector.checkThat("Byline or tagline group doesn't have items", bylineTaglineItems.size(), not(0));
            MntlBylineTaglineItemComponent firstItem = bylineTaglineItems.get(0);
            if (bylinesTaglinesGroups.indexOf(bylinesTaglinesGroup) == 0) {
                String author = getAuthorFromDocument(documentData);
                if (!author.isEmpty() && bylinesTaglinesGroups.indexOf(bylinesTaglinesGroup) == 0)
                    collector.checkThat("Byline's name doesn't match Selene data", firstItem.doesNameExist() ? firstItem.bylinesItemName().getAttribute("textContent").replace("\n", "") : firstItem.bylinesItemLink().getAttribute("textContent"), is(author));
            }
            WebElementEx firstDescriptor = bylineTaglineItems.get(0).bylinesItemDescriptor();
            if (firstDescriptor.getText().toLowerCase().contains("fact checked by")) {
                if (!ConfigurationProperties.getTargetProject("").contains("beauty") && !ConfigurationProperties.getTargetProject("").contains("finance") && !ConfigurationProperties.getTargetProject("").contains("money"))
                    collector.checkThat("fact checked badge icon is not displayed", firstItem.bylinesItemIcon().isDisplayed(), is(true));
                collector.checkThat("fact checked by text is not correct", firstItem.bylinesItemDescriptor().text().toLowerCase(), is("fact checked by"));
            }
            for (MntlBylineTaglineItemComponent bylineTaglineItem : bylineTaglineItems) {
                bylineTaglineItem.scrollIntoViewCentered();
                if (bylineTaglineItem.hasDescriptor())
                    collector.checkThat("mntl-attribution__item-descriptor doesn't contain `by` or `and`", (bylineTaglineItem.bylinesItemDescriptor().getAttribute("textContent") != "")
                                    ? bylineTaglineItem.bylinesItemDescriptor().getAttribute("textContent") : firstDescriptor.getText(),
                            anyOf(containsStringIgnoringCase("by"), containsStringIgnoringCase("and"), containsStringIgnoringCase("review")));
                if (bylineTaglineItem.doesNameExist() && (!ConfigurationProperties.getTargetProject("").contains("finance") && !ConfigurationProperties.getTargetProject("").contains("money"))) {
                    collector.checkThat("Byline's name is empty", bylineTaglineItem.bylinesItemName().getText(), not(emptyOrNullString()));
                } else if (!bylineTaglineItem.hasDataTooltip()) {
                    collector.checkThat("Byline's name is empty", bylineTaglineItem.bylinesItemLink().href(), startsWith("https://"));
                    if (bylineTaglineItem.bylinesItemLink().getAttributes().containsKey("rel")) {
                        checkRelAttribute(bylineTaglineItem.bylinesItemLink(), bylineTaglineItem.bylinesItemLink().href(), "");
                    }
                } else {
                    collector.checkThat("The schema doesn't contain an `url` item for author, reviewer or fact checker " + bylineTaglineItem.bylinesItemLink().text(), bylineTaglineItem.bylinesItemLink().href(), containsStringIgnoringCase(getUrlFromSchema(schemaComp, bylineTaglineItem.bylinesItemLink().text())));
                    collector.checkThat("Data-tooltip attribute is empty", bylineTaglineItem.dataTooltip().getElement().getText(), not(emptyOrNullString()));
                    collector.checkThat("Data-tooltip link href is empty", bylineTaglineItem.dataTooltip().bioLink().href(), startsWith("https://"));
                    if (bylineTaglineItem.dataTooltip().bioLink().getAttributes().containsKey("rel")) {
                        checkRelAttribute(bylineTaglineItem.dataTooltip().bioLink(), bylineTaglineItem.dataTooltip().bioLink().href(), "");
                    }
                    collector.checkThat("Data-tooltip attribute does not contain Author's name", bylineTaglineItem.dataTooltip().attributeValue("data-tooltip"), not(emptyOrNullString()));
                    if (desktop(runner.driver())) {
                        bylineTaglineItem.dataTooltip().getElement().mouseHover();
                    }
                    else {
                        final int MAX_TIME_TO_TRIGGER = 5;
                        for (int timeNum = 0; timeNum < MAX_TIME_TO_TRIGGER; timeNum++) {
                            boolean isToolTipDisplayed = false;
                            try {
                                tryToTriggerTheToolTip(runner, bylineTaglineItem);
                                waitElementDisplayed(runner, bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().getElement());
                                if(bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().displayed()) isToolTipDisplayed = true;
                            } catch (Exception ignored){}
                            if(isToolTipDisplayed) break;
                        }
                    }

                    collector.checkThat("The tooltip is not displayed ", bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().displayed(), is(true));
                    MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipTop dynamicTooltipTopComponent = bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().MntlDynamicTooltipContent().tooltipTop();
                    collector.checkThat("The tooltip for a link with href " + bylineTaglineItem.dataTooltip().bioLink() + " contains date stamp ", bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().MntlDynamicTooltipContent().tooltipTop().doesDateExist(), is(false));
                    if (!ConfigurationProperties.getTargetProject("").contains("finance") && !ConfigurationProperties.getTargetProject("").contains("money"))
                        collector.checkThat("Data-tooltip attribute does not contain Author's name", dynamicTooltipTopComponent.name().text(), containsStringIgnoringCase(bylineTaglineItem.dataTooltip().bioLink().text()));
                    else
                        collector.checkThat("Data-tooltip attribute does not equeal `full bio`", dynamicTooltipTopComponent.name().text(), Matchers.containsStringIgnoringCase("full bio"));
                    if (!dynamicTooltipTopComponent.getElement().className().contains("no-image")) {
                        verifyLazyLoadImage(dynamicTooltipTopComponent.image());
                        collector.checkThat("Alt attribute of img does not contain the author's name", dynamicTooltipTopComponent.image().getElement().getAttribute("alt"), dynamicTooltipTopComponent.image().getElement().getAttributes().containsKey("alt") ? not(emptyOrNullString()) : is(emptyOrNullString()));
                    }
                    collector.checkThat("Data-tooltip attribute of dynamic tooltip top's does not contain Author's name", dynamicTooltipTopComponent.nameText(), not(emptyOrNullString()));
                    if (dynamicTooltipTopComponent.name().getAttributes().containsKey("rel")) {
                        checkRelAttribute(dynamicTooltipTopComponent.name(), dynamicTooltipTopComponent.name().href(), "dynamic tooltip top's external");
                    }
                    collector.checkThat("Dynamic tooltip top's link href is empty", dynamicTooltipTopComponent.name().href(), startsWith("https://"));
                    if (dynamicTooltipTopComponent.doesDateExist())
                        collector.checkThat("Dynamic tooltip date is empty", dynamicTooltipTopComponent.date().getText(), not(emptyOrNullString()));
                    else if (bylineTaglineItem.doesDateExist())
                        collector.checkThat("Tooltip date is empty", bylineTaglineItem.bylinesItemDate().getText(), not(emptyOrNullString()));
                    if (dynamicTooltipTopComponent.hasSocialNav()) {
                        List<MntlSocialFollowNavComponent.MntlSocialNavItemComponent> socialNavItems = dynamicTooltipTopComponent.socialNav().socialNavItems();
                        for (MntlSocialFollowNavComponent.MntlSocialNavItemComponent socialNavItem : socialNavItems) {
                            collector.checkThat("social follow icon is missing", socialNavItem.icon().isDisplayed(), is(true));
                            collector.checkThat("social follow label text is missing", socialNavItem.label().text(), not(emptyOrNullString()));
                            collector.checkThat("social follow link is incorrect", socialNavItem.href(), startsWith("http"));
                        }
                    }
                    MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipBottom dynamicTooltipBottomComponent = bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().MntlDynamicTooltipContent().tooltipBottom();
                    if (!ConfigurationProperties.getTargetProject("").contains("beauty"))
                        collector.checkThat("Dynamic tooltip bottom learn more text is empty", dynamicTooltipBottomComponent.moreText(), not(emptyOrNullString()));
                    collector.checkThat("Dynamic tooltip bottom link href is empty", dynamicTooltipBottomComponent.link().href(), startsWith("https://"));
                    if (dynamicTooltipBottomComponent.link().getAttributes().containsKey("rel")) {
                        checkRelAttribute(dynamicTooltipBottomComponent.link(), dynamicTooltipBottomComponent.link().href(), "dynamic tooltip bottom's external");
                    }
                    collector.checkThat("Dynamic tooltip bottom does not contain Author's name", dynamicTooltipBottomComponent.linkText(), not(emptyOrNullString()));
                    if (desktop(runner.driver())) firstDescriptor.mouseHover();
                    else {
                        clickOnArticleTitle(runner);
                    }
                }
            }
        }
    };

    /*
    Consolidated method for byline logic for date
     */
    private void dateValidations(Runner runner, SchemaComponent schemaComp, MntlUpdatedStampBylinesComponent updatedStamp, DocumentContext documentData) {
        // Byline date validation
        SimpleDateFormat commonFormat = new SimpleDateFormat("yyyy-MM-dd");
        commonFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String todayDate = commonFormat.format(new Date());
        String displayedDate = runner.testData() == null ? changeDateFormat(updatedStamp.text()
                .replace(updatedStamp.text().contains("on") ? "Updated on " : "Updated ", "")
                .replace(updatedStamp.text().contains("ON") ? "UPDATED ON " : "UPDATED ", "")
                .replace(updatedStamp.text().contains("on") ? "Published on " : "Published ", "")
                .replace(updatedStamp.text().contains("ON") ? "PUBLISHED ON " : "PUBLISHED ", "")) : todayDate;
        HashMap<String, Long> dates = getDatesFromDocument(documentData);
        String latestDateFromDocument = runner.testData() == null ? commonFormat.format(new Date(Collections.max(dates.entrySet(), Comparator.comparingLong(Map.Entry::getValue)).getValue())) : todayDate;
        collector.checkThat("The date displayed " + displayedDate + " does not equal to the latest date in between `firstPublished`, `displayed` from the document and `lastModified` from the byline",
                displayedDate, is(latestDateFromDocument));
        // Schema dates validation
        dates.remove("firstPublished");
        LinkedHashMap<?, ?> schemaMap = JsonPath.read(schemaComp.schemaContent(), "$[0]");
        schemaMap.putAll(JsonPath.read(schemaComp.schemaContent(), "$[0].mainEntityOfPage"));
        latestDateFromDocument = runner.testData() == null ? commonFormat.format(new Date(Collections.max(dates.entrySet(), Comparator.comparingLong(Map.Entry::getValue)).getValue())) : todayDate;
        String lastModifiedSchemaField = schemaMap.get("dateModified").toString();
        // Date from schema comes in a different format (i.e `2021-02-17T22:33:24.000-05:00`), have to bring to the same format here:
        collector.checkThat("`dateModified` field in the schema does not equal to the latest date in between `displayed` from the document and `lastModified` from the byline",
                lastModifiedSchemaField.substring(0, lastModifiedSchemaField.lastIndexOf("T")), is(latestDateFromDocument));
        if (isReviewedByPresentInSchema(schemaMap))
            collector.checkThat("`reviewedBy` data is present in the schema but `lastReviewed` does not equal to the latest date in between `displayed` from the document and `lastModified` from the byline",
                    getDateFromSchema(schemaMap), is(latestDateFromDocument));
    }

    private String changeDateFormat(String dateString) {
        String targetFormat = "yyyy-MM-dd";
        List<String> currentFormats = Arrays.asList("EEE mmm dd HH:mm:ss z yyyy", "MM-dd-yyyy", "MMMMM dd, yyyy", "MM/dd/yy", "MMMMM dd, yyyy HH:mmaaa", "MMMMM dd, yyyy", "MMM dd, yyyy");
        for (String currentFormat : currentFormats) {
            DateFormat srcDf = new SimpleDateFormat(currentFormat);
            srcDf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            DateFormat destDf = new SimpleDateFormat(targetFormat);
            destDf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            try {
                Date date = srcDf.parse(dateString);
                String targetDate = destDf.format(date);
                return targetDate;
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    /*
    This will get the document from selene, will get called once
     */
    protected DocumentContext documentData(Runner runner) throws IndexOutOfBoundsException {
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

    public Boolean isReviewedByPresentInSchema(LinkedHashMap<?, ?> schemaMap) {
        Boolean isReviewedByPresentInSchema = false;
        if (schemaMap.containsKey("reviewedBy"))
            return isReviewedByPresentInSchema = true;
        return isReviewedByPresentInSchema;
    }

    /*
    This method will return true if `lastReviewed` object is present in schema
    */
    private String getDateFromSchema(LinkedHashMap<?, ?> schemaMap) {
        String dateFromSchema;
        for (Object schemaItem : schemaMap.values()) {
            if (schemaItem.getClass().toString().contains("LinkedHashMap")) {
                LinkedHashMap<?, ?> valueLinkedHashMap = (LinkedHashMap<?, ?>) schemaItem;
                if (valueLinkedHashMap.containsKey("reviewedBy")) {
                    return valueLinkedHashMap.get("lastReviewed").toString().substring(0, valueLinkedHashMap.get("lastReviewed").toString().lastIndexOf("T"));
                }
            }
        }
        return null;
    }

    private String getAuthorFromDocument(DocumentContext documentData) {
        String author = "";
        try {
            author = documentData.read("$.data.guestAuthor.link.text").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return author;
    }

    /*
    This method will return selected dates from the document:
    - last material update date (dates.displayed),
    - first published,
    - last modified date for reviewers/fact-checkers (lastModified in bylines data)
     */
    private HashMap<String, Long> getDatesFromDocument(DocumentContext documentData) {
        HashMap<String, Long> dates = new HashMap<>();
        try {
            dates.put("firstPublished", documentData.read("$.data.dates.firstPublished"));
            dates.put("displayed", documentData.read("$.data.dates.displayed"));
            if (getLastMaterialPublishDate(documentData) != null)
                dates.put("lastModified", getLastMaterialPublishDate(documentData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThat("There weren't any date objects found in the document", dates.size(), greaterThan(0));
        return dates;
    }

    /*
   This method will return `Last Material Publish Date` from the selene data
    */
    private Long getLastMaterialPublishDate(DocumentContext documentData) {
        JSONArray bylineItemsList = documentData.read("$.data.bylines.list");
        List<String> attributionText = new ArrayList<>(Arrays.asList("checked", "reviewed", "tested", "Reviewed", "Checked", "Tested"));
        for (Object bylineItem : bylineItemsList) {
            HashMap<?, ?> bylineItemMap = (HashMap<?, ?>) bylineItem;
            if (bylineItemMap.containsKey("attribution"))
                if (attributionText.stream().anyMatch(bylineItemMap.get("attribution").toString()::contains))
                    return Long.parseLong(bylineItemMap.get("lastModified").toString());
        }
        return null;
    }

    /*
    This method will return a sub-stringed value of `url` field for a corresponding author, co-author, tester, fact checker, reviewer or contributor in schema or an empty string if it is not found.
     */
    private String getUrlFromSchema(SchemaComponent urlSchemaItems, String author) {
        LinkedHashMap<?, ?> schemaMap = JsonPath.read(urlSchemaItems.schemaContent(), "$[0]");
        schemaMap.putAll(JsonPath.read(urlSchemaItems.schemaContent(), "$[0].mainEntityOfPage"));
        for (Object schemaItem : schemaMap.values()) {
            if (schemaItem.getClass().toString().contains("JSONArray")) {
                JSONArray schemaItemArray = (JSONArray) schemaItem;
                if (schemaItemArray.size() > 0) {
                    for (Object value : schemaItemArray) {
                        if (value.getClass().toString().contains("LinkedHashMap")) {
                            LinkedHashMap<?, ?> valueLinkedHashMap = (LinkedHashMap<?, ?>) value;
                            if (valueLinkedHashMap.containsKey("@type") && valueLinkedHashMap.values().contains("Person") && valueLinkedHashMap.get("name").toString().equalsIgnoreCase(author))
                                if (valueLinkedHashMap.get("url").toString() != null) {
                                    int lastIndexOfSlash = valueLinkedHashMap.get("url").toString().lastIndexOf("/");
                                    return valueLinkedHashMap.get("url").toString().substring(lastIndexOfSlash + 1).replace("\"]", "");
                                }
                        }
                    }
                }
            }
        }
        return "";
    }

    /*
    This test will validate if an analytics event `transmitInteractiveEvent` of `mntl-dynamic-tooltip Click` is triggered
    when clicked on a link at the top or at the bottom of a tooltip of either byline or tagline.
    Should pass a component from the vertical test, either of
        - MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipTop
        - MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipBottom
     */
    protected BiConsumer<Runner, Boolean> bylinesTaglinesClicktrackingTest = (runner, hasTagline) -> {
        runner.page().waitFor().exactMoment(2, TimeUnit.SECONDS);
        BylinesAndTaglinesPage page = new BylinesAndTaglinesPage(runner.driver(), MntlBylinesComponent.class);
        List<BylinesAndTaglinesPage.BylineTaglineGroup> bylinesTaglinesGroups = page.bylineGroups();
        collector.checkThat("There are no bylines present on the page", bylinesTaglinesGroups.size(), not(0));
        if (hasTagline) {
            collector.checkThat("There are no taglines present on the page", page.taglineGroups().size(), not(0));
            bylinesTaglinesGroups.addAll(page.taglineGroups());
        }
        boolean topOrBottom = true;
        if (runner.component().className().contains("bottom")) topOrBottom = false;
        for (BylinesAndTaglinesPage.BylineTaglineGroup bylinesTaglinesGroup : bylinesTaglinesGroups) {
            List<MntlBylineTaglineItemComponent> bylineTaglineItems = new ArrayList<>();
            if (bylinesTaglinesGroup.getElement().className().contains("taglines"))
                bylineTaglineItems.addAll(bylinesTaglinesGroup.taglineItems());
            else bylineTaglineItems.addAll(bylinesTaglinesGroup.bylineItems());
            collector.checkThat("Byline or tagline group doesn't have items", bylineTaglineItems.size(), not(0));
            WebElementEx firstDescriptor = bylineTaglineItems.get(0).bylinesItemDescriptor();
            for (MntlBylineTaglineItemComponent bylineTaglineItem : bylineTaglineItems) {
                runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
                if (bylineTaglineItem.hasDataTooltip()) {
                    bylineTaglineItem.dataTooltip().getElement().scrollIntoViewCentered();
                    bylineTaglineItem.dataTooltip().getElement().mouseHover();
                    runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
                    MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipTop dynamicTooltipTopComponent = bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().MntlDynamicTooltipContent().tooltipTop();
                    MntlDynamicTooltipComponent.MntlDynamicTooltipContent.TooltipBottom dynamicTooltipBottomComponent = bylineTaglineItem.dataTooltip().bylinesItemDynamicDataTooltip().MntlDynamicTooltipContent().tooltipBottom();
                    WebElementEx link;
                    String label;
                    String action;
                    if (topOrBottom) {
                        link = dynamicTooltipTopComponent.name();
                        if (ConfigurationProperties.getTargetProject("").contains("finance") && ConfigurationProperties.getTargetProject("").contains("money"))
                            action = "Full Bio";
                        else if (ConfigurationProperties.getTargetProject("").contains("travel"))
                            action = bylineTaglineItem.dataTooltip().bioLink().getText();
                        else
                            action = bylineTaglineItem.dataTooltip().bioLink().getAttribute("textContent");
                        label = bylineTaglineItem.dataTooltip().bioLink().href();
                    } else {
                        link = dynamicTooltipBottomComponent.link();
                        action = dynamicTooltipBottomComponent.link().getText();
                        label = new MntlUrl(dynamicTooltipBottomComponent.link().href()).queryFreeUrl();
                    }
                    testLink(runner, action, label, link);
                    firstDescriptor.mouseHover();
                }
            }
        }
    };

    private void testLink(Runner runner, String action, String label, WebElementEx link) {
        runner.page().waitFor().exactMoment(2, TimeUnit.SECONDS);
        String parent = runner.driver().getWindowHandle();
        String handle = runner.driver().getWindowHandle(() ->
                testLinkEvent(runner, action, label, collector, link));
        if (handle != null) runner.driver().switchTo().window(handle);
        runner.driver().close();
        runner.driver().switchTo().window(parent);
    }

    private void testLinkEvent(Runner runner, String action, String label, ErrorCollectorEx collector, WebElementEx link) {
        ctrlClick(link);
        link.waitFor().exactMoment(2, TimeUnit.SECONDS);
        String eventCategory = (ConfigurationProperties.getTargetProject("").contains("finance") && action.equals("Full Bio")) ? "finance-author-tooltip Click" : "mntl-dynamic-tooltip Click";
        verifyClickTrackingEvent(runner.driver(), collector, eventCategory, action, label);
    }

    protected Consumer<Runner> bylinesCommerceDividerTest = runner -> {
        MntlBylinesComponent component = (MntlBylinesComponent) runner.component();
        collector.checkThat("Byline items doesn't have divider displayed", component.attributeValue("class").contains("byline-divider"), is(true));
    };

    private boolean isLinkSponsored(String link) {
        boolean isLinkSponsored = false;
        if (sponsoredList.contains(link))
            isLinkSponsored = true;
        return isLinkSponsored;
    }

    private boolean isLinkCAES(String link) {
        return caesSponsoredList.contains(link);
    }

    private String getDomainNameFromHref(String href) {
        String domainName = null;
        try {
            domainName = getDomainName(href);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return domainName;
    }

    private void checkRelAttribute(WebElementEx linkWebElement, String linkHref, String linkName) {
        String domainName = getDomainNameFromHref(linkHref);
        if (!safeList.contains(domainName) && !domainName.contains(ConfigurationProperties.getTargetProject(null))) {
            collector.checkThat("Noopener rel tag is not present in the " + linkName + " link", linkWebElement.getAttribute("rel"), containsString("noopener nofollow"));
        }

        if (checkNoCaesRel && !isLinkCAES(linkHref)) {
            collector.checkThat("Nocaes rel tag is not present in the " + linkName + " link, which is not in the CAES list", linkWebElement.getAttribute("rel"), containsString("nocaes"));
        }

        if (isLinkSponsored(domainName)) {
            collector.checkThat("Byline link is in the sponsored list and has `sponsored` rel attribute", linkWebElement.getAttribute("rel"), not(containsString("sponsored")));
        }
    }

    private void waitElementDisplayed(Runner runner, WebElementEx elementEx) throws TimeoutException {
        runner.driver().waitFor(ExpectedConditions.visibilityOf(elementEx));
    }

    private void clickOnArticleTitle(Runner runner){
        WebElementEx articleTitleElem = runner.driver().findElementEx(By.tagName("h1"));
        articleTitleElem.mouseClick();
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
    }

    private void tryToTriggerTheToolTip(Runner runner, MntlBylineTaglineItemComponent bylineTaglineItem){
        WebElementEx articleTitleElem = runner.driver().findElementEx(By.tagName("h1"));
        articleTitleElem.mouseClick();
        runner.page().waitFor().exactMoment(5, TimeUnit.SECONDS);
        bylineTaglineItem.toolTipTrigger().mouseClick();
    }
}