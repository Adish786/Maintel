package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScDefinitionBlockComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.openqa.selenium.By;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/*
This test takes the schema from the page and validates the name and description fields against the definition block component.

You will need to:
1. Provide custom data into the test;
2. Remove all the styling and citations: i.e. `﻿</span>﻿`
 */
public class MntlScDefinitionBlockComponentTest extends MntlVenusTest implements MntlCommonTestMethods {

    String SCHEMA_SELECTOR = "script[type=\"application/ld+json\"]";

    protected Consumer<Runner> scDefinitionBlockTest = (runner) -> {
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        SchemaComponent schemaComp = runner.page().head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
        JSONArray schemaArray = null;
        if (getJSONArrayfromSchema(schemaComp) != null)
            schemaArray = getJSONArrayfromSchema(schemaComp);
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        List<MntlScDefinitionBlockComponent> scDefinitionBlockComponents = new MntlBasePage<>(runner.driver(), MntlScDefinitionBlockComponent.class).getComponents();
        List<HashMap<String, String>> schemaValuesMap = getValuesFromSchema(schemaArray);
        assertThat("Definition block component size is 0", scDefinitionBlockComponents.size(), greaterThan(0));
        int counter = 0;
        for (MntlScDefinitionBlockComponent item : scDefinitionBlockComponents) {
            HashMap<String, String> schemaItem = schemaValuesMap.get(counter++);
                collector.checkThat("Schema item name " + schemaItem.get("name") + " doesn't match component block heading " + item.heading().getText(),
                        schemaItem.get("name"), is(item.heading().getText()));
                collector.checkThat("Schema item description " + schemaItem.get("description") + " doesn't match component block description " +
                        item.bodyText(), item.bodyText().contains(schemaItem.get("description")), is(true));
        }
    };

    private JSONArray getJSONArrayfromSchema(SchemaComponent schemaComp) {
        try {
            return JsonPath.read(schemaComp.schemaContent(), "$[0].about");
        } catch (Exception e) {
            assertThat("Couldn't get schema from the document", true, is(false));
        }
        return null;
    }

    private List<HashMap<String, String>> getValuesFromSchema(JSONArray schemaMap) {
        List<HashMap<String, String>> schemaValuesList = new ArrayList<>();
        for (Object schemaItem : schemaMap) {
            LinkedHashMap<?, ?> valueLinkedHashMap = (LinkedHashMap<?, ?>) schemaItem;
            if (valueLinkedHashMap.get("@type").equals("DefinedTerm")) {
                HashMap<String, String> schemaValuesMap = new HashMap<>();
                schemaValuesMap.put("name", valueLinkedHashMap.get("name").toString());
                schemaValuesMap.put("description", valueLinkedHashMap.get("description").toString());
                schemaValuesList.add(schemaValuesMap);
            }
        }
        return schemaValuesList;
    }

}
