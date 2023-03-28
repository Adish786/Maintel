package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.ReadJsonData;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlTableOfContentsComponent;
import com.about.mantle.venus.model.components.blocks.MntlScBlockQandAComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.venus.core.driver.WebElementEx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.openqa.selenium.By;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public abstract class MntlQandAblockTest <T extends MntlTableOfContentsComponent> extends MntlVenusTest implements MntlCommonTestMethods {

    protected abstract Class<T> getTOCcomponent();

    String SCHEMA_SELECTOR = "script[type=\"application/ld+json\"]";

    /*
    This test will test question and answer component in regards to presence of its items in the schema and
    whether those are placed in a schema in an order of how they displayed on the page. It will also test any links
    present inside the answer component.
     */
    protected Consumer<Runner> qAndAblockTest = (runner) -> {
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        SchemaComponent schemaComp = runner.page().head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        List<MntlScBlockQandAComponent> qAndAblockComponents = (List<MntlScBlockQandAComponent>) new MntlBasePage<>(runner.driver(), MntlScBlockQandAComponent.class).getComponents();
        collector.checkThat("Question and answer block items size is 0 ", qAndAblockComponents.size(), greaterThan(0));
        //toc validations
        JSONArray listOfJsonQandAitems = getListOfJSONitems(runner);
        if (runner.page().componentExists(getTOCcomponent())) {
            MntlTableOfContentsComponent toc = new MntlBasePage<>(runner.driver(), getTOCcomponent()).getComponent();
            if (mobile(runner.driver()) && (toc.isViewMoreButtonPresent() && toc.viewAllButton().isDisplayed())) toc.viewAllButton().jsClick();
            List<String> tocItems = toc.items().stream().map(item -> item.getText()).collect(Collectors.toList());
            //iterating over JSON QUESTIONANDANSWER items
            for (Object JSONarrayElement : listOfJsonQandAitems) {
                LinkedHashMap<?, ?> dataElement = (LinkedHashMap<?, ?>) JSONarrayElement;
                if (dataElement.containsKey("shortText") && dataElement.containsKey("hideOnTOC") && dataElement.get("hideOnTOC").toString().equals("false")) {
                    // checking if shortText value is displayed in TOC if hideOnTOC set to false and shortText has a value
                    collector.checkThat("Short text " + dataElement.get("shortText").toString() + " is not present in TOC ",
                            tocItems.toString(), containsString(dataElement.get("shortText").toString()));
                } else if ((!dataElement.containsKey("shortText") && !dataElement.containsKey("hideOnTOC")) ||
                        ((dataElement.containsKey("shortText") && dataElement.get("shortText").toString().isEmpty()) || !dataElement.containsKey("shortText"))) {
                    // checking if Question value is displayed in TOC if hideOnTOC set to false and shortText is empty or missing
                    collector.checkThat("Question heading " + dataElement.get("question").toString() + " is not present in TOC ",
                            tocItems.toString(), containsString(dataElement.get("question").toString()));
                } else if (dataElement.containsKey("question") && dataElement.containsKey("hideOnTOC") && dataElement.get("hideOnTOC").toString().equals("true")) {
                    // checking if Question value is NOT displayed in TOC if hideOnTOC set to true
                    collector.checkThat("Question heading " + dataElement.get("question").toString() + " is present in TOC despite hideOnTOC set to true",
                            tocItems.toString(), not(containsString(dataElement.get("question").toString())));
                }
            }
        }
        boolean areItemsInOrderInSchema = false;
        JSONArray questionsInSchema = JsonPath.read(schemaComp.schemaContent(), "$[0].about[?(@.@type == 'FAQPage')].mainEntity[*].name");
        JSONArray answersInSchema = JsonPath.read(schemaComp.schemaContent(), "$[0].about[?(@.@type == 'FAQPage')].mainEntity[*].acceptedAnswer.text");
        for (int i = 0; i < questionsInSchema.size(); i++) {
            if (qAndAblockComponents.get(i).question().getText().equals(questionsInSchema.get(i).toString())) {
                areItemsInOrderInSchema = true;
                collector.checkThat("Question and answer block heading id is empty ", qAndAblockComponents.get(i).tocId().getAttribute("id"), not(emptyOrNullString()));
                collector.checkThat("Question and answer block answer text is empty or doesn't match the value in schema ", listOfJsonQandAitems.get(i).toString(), containsString(answersInSchema.get(i).toString().replace("[\"", "").replace("\"]", "")));
                if (qAndAblockComponents.get(i).getElement().elementExists("a")) {
                    List<WebElementEx> answerLinks = qAndAblockComponents.get(i).answerLinks();
                    for (WebElementEx answerLink : answerLinks)
                        collector.checkThat("Answer link's href is empty ", answerLink.getAttribute("href"), not(emptyOrNullString()));
                }
            } else {
                areItemsInOrderInSchema = false;
            }
        }
        collector.checkThat("Question and answer block items are either missing in schema or are not in order ", areItemsInOrderInSchema, is(true));
    };

    private JSONArray getListOfJSONitems(Runner runner) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data;
        JSONArray listOfJSONQandAitems;
        try {
            InputStream inputStream = ReadJsonData.class.getResourceAsStream(runner.testData().toString());
            data = objectMapper.readValue(inputStream, HashMap.class);
            listOfJSONQandAitems = JsonPath.read(data, "$." + (data.get("templateType").toString().equals("LISTSC") ? "items.list[*]" : "pages.list[0]") + ".contents.list[?(@.type == 'QUESTIONANDANSWER')].data");
        } catch (Exception e) {
            throw new RuntimeException("Error reading json data from: " + runner.testData().toString(), e);
        }
        return listOfJSONQandAitems;
    }

}
