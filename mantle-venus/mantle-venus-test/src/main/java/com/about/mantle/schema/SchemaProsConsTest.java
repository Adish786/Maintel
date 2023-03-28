package com.about.mantle.schema;
import java.util.*;
import java.util.function.Consumer;
import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.google.gson.*;
import com.jayway.jsonpath.DocumentContext;
import io.restassured.path.json.JsonPath;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;


public abstract class SchemaProsConsTest extends MntlVenusTest
        implements MntlCommonTestMethods<MntlCommonTestMethods.MntlRunner> {

    protected abstract String getDocId();

    static List<String> addFieldsToVerify = new ArrayList<>();
    static Map<String, Object> additionalFields = new HashMap<>();

    protected Consumer<MntlRunner> productRecordProsConsSchemaTest = runner -> {

        JsonPath schema = mntlSchema().getSchema(runner.driver(), runner.url());
        String docId = getDocId();

        DocumentContext seleneData = SeleneUtils.getDocuments(docId);
        String pageType = parseJsonObject(seleneData).get("data").getAsJsonObject().get("templateType").getAsString();
        if (pageType.equalsIgnoreCase("REVIEWSC")) {
            JsonObject intro = parseJsonObject(seleneData).get("data").getAsJsonObject().get("intro").getAsJsonObject();
            JsonArray contentsList = intro.getAsJsonObject().get("list").getAsJsonArray();
            //Get actual data from schema
            ArrayList<String> positiveNotesSchema = schema.get("positiveNotes.itemListElement.name");
            ArrayList<String> negativeNotesSchema = schema.get("negativeNotes.itemListElement.name");
            //method to compare selene pros & cons against schema
            testProsCons(contentsList,positiveNotesSchema, negativeNotesSchema);

        } else {
            JsonArray seleneArrayWithProductCardsData = parseJsonObject(seleneData).get("data").getAsJsonObject().get("items").getAsJsonObject()
                    .get("list").getAsJsonArray();
            if (seleneArrayWithProductCardsData.size() < 0) {
                Assertions.fail("Selene product cards array is empty");
            }

            for (int product = 0; product < seleneArrayWithProductCardsData.size(); product++) {
                JsonElement item = seleneArrayWithProductCardsData.get(product);
                JsonArray contentsList = item.getAsJsonObject().get("contents").getAsJsonObject().get("list").getAsJsonArray();
                if (contentsList.size() < 0) {
                    Assertions.fail("Content list is empty!");
                }
                JsonObject schemaObject = new Gson().toJsonTree(((ArrayList) schema.get("about[0].itemListElement[0]")).get(product)).getAsJsonObject().get("review").getAsJsonObject();
                //Get actual data from schema
                ArrayList<String> positiveNotesSchema = new ArrayList<>();
                ArrayList<String> negativeNotesSchema = new ArrayList<>();
                JsonArray positiveNotes = schemaObject.get("positiveNotes").getAsJsonObject().get("itemListElement").getAsJsonArray();
                JsonArray negativeNotes = schemaObject.get("negativeNotes").getAsJsonObject().get("itemListElement").getAsJsonArray();
                positiveNotes.forEach(positiveNote -> positiveNotesSchema.add(positiveNote.getAsJsonObject().get("name").getAsString()));
                negativeNotes.forEach(negativeNote -> negativeNotesSchema.add(negativeNote.getAsJsonObject().get("name").getAsString()));
                //method to compare selene pros & cons against schema
                testProsCons(contentsList,   positiveNotesSchema, negativeNotesSchema);
            }
            mntlSchema().testSchemaRules(schema, collector);
        }
    };

    protected void testProsCons(JsonArray contentsList, ArrayList<String> positiveNotesSchema,ArrayList<String> negativeNotesSchema){
        //Get expected data from selene
        JsonArray positiveNotesSelene = null;
        JsonArray negativeNotesSelene = null;

        for (JsonElement contentsListItem : contentsList) {
            JsonObject data = contentsListItem.getAsJsonObject().get("data").getAsJsonObject();
            String type = contentsListItem.getAsJsonObject().get("type").getAsString();
            if (type.equalsIgnoreCase("COMPARISONLIST")) {
                JsonArray positiveNotes = data.get("listA").getAsJsonObject().get("items").getAsJsonObject().get("list").getAsJsonArray();
                JsonArray negativeNotes = data.get("listB").getAsJsonObject().get("items").getAsJsonObject().get("list").getAsJsonArray();
                positiveNotesSelene = data.get("listA").getAsJsonObject().get("items").getAsJsonObject().get("list").getAsJsonArray();
                negativeNotesSelene = data.get("listB").getAsJsonObject().get("items").getAsJsonObject().get("list").getAsJsonArray();
            }
        }
        //compare schema and selene data
        String unwantedCharsRegexPatternSchema = "(^\\[)|(\\]$)|,\\s*";
        String unwantedCharsRegexPatternSelene = "[\\[\\\"\\\\,]*<[^>]*>[\\]\\\"\\\\,]*|,\\s*";

        collector.checkThat("Positive notes Values on schema do not match selene",
                String.valueOf(positiveNotesSchema).replaceAll(unwantedCharsRegexPatternSchema, "").replaceAll(unwantedCharsRegexPatternSchema,""),
                CoreMatchers.is(String.valueOf(positiveNotesSelene).replaceAll(unwantedCharsRegexPatternSelene, "")));
        collector.checkThat("Negative notes Values on schema do not match selene",
                String.valueOf(negativeNotesSchema).replaceAll(unwantedCharsRegexPatternSchema, "").replaceAll(unwantedCharsRegexPatternSchema,""),
                CoreMatchers.is(String.valueOf(negativeNotesSelene).replaceAll(unwantedCharsRegexPatternSelene, "")));
    }

    private JsonObject parseJsonObject(DocumentContext context) {
        JsonParser parser = new JsonParser();
        JsonElement jsonTree = parser.parse(context.jsonString());
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        return jsonObject;
    }

    public AbstractMntlSchemaTest mntlSchema() {
        AbstractMntlSchemaTest mntlSchemaTest = new AbstractMntlSchemaTest() {
            @Override
            protected List<String> getBasicStructure() { return addFieldsToVerify;}
            @Override
            protected Map<String, Object> getAdditionalFieldsToVerify() {
                return additionalFields;
            }
        };
        return mntlSchemaTest;
    }
}