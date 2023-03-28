package com.about.mantle.schema;

import com.about.mantle.utils.selene.document.client.DocumentClient;
import com.about.mantle.utils.selene.documentutils.DocumentHelper;
import com.about.venus.core.driver.selection.DriverSelection;
import com.about.venus.core.test.VenusTest;
import com.google.common.collect.ImmutableMap;
import io.restassured.path.json.JsonPath;

import java.io.IOException;

import static com.about.mantle.utils.selene.utils.DocumentTestUtils.documentTestWithDelete;

public class MntlNewsArticleTest extends VenusTest {
	public void testViewTypeInheritsNewsArticle(String documentPath, DriverSelection.Matcher matcher)
			throws IOException {
		DocumentHelper documentHelper = new DocumentHelper("STRUCTUREDCONTENT");
		DocumentClient client = documentHelper.createDocument(documentPath);
		documentTestWithDelete(client, () -> {
			test(matcher, driver -> {
				AbstractMntlSchemaTest schemaTest = new SpecificFieldSchemaTest(
						ImmutableMap.of("@type", "NewsArticle"));
				JsonPath schema = schemaTest.getSchema(driver, documentHelper.getUrl());
				schemaTest.testSchemaRules(schema, collector);
			});
		});
	}
}
