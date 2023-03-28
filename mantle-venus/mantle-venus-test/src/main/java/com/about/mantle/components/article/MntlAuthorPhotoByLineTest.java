package com.about.mantle.components.article;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.util.function.Consumer;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlBylineTaglineItemComponent;
import com.about.mantle.venus.model.components.article.MntlBylinesComponent;
import com.about.mantle.venus.model.pages.BylinesAndTaglinesPage;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;


public abstract class MntlAuthorPhotoByLineTest extends MntlVenusTest implements MntlCommonTestMethods {

	abstract public String getDocId();

	private String authorId;
	private String authorName;
	Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

	protected Consumer<MntlRunner> authorPhotoTest = runner -> {
		BylinesAndTaglinesPage page = new BylinesAndTaglinesPage(runner.driver(), MntlBylinesComponent.class);
		MntlBylineTaglineItemComponent byLine = page.bylineGroups().get(0).bylineItems().get(0);
		boolean hasAuthorImage = byLine.hasAuthorImage();
		
		if (!isUniqueAuthor(getDocId())) {
			collector.checkThat("Author photo is displayed when there is more than one author", hasAuthorImage,
					is(false));
		} else {
			if (authorHasImage(this.authorId)) {
				collector.checkThat("Author photo is not displayed ", hasAuthorImage, is(true));
				collector.checkThat("Author photo is not correct ", byLine.bylinesItemLink().getText().toLowerCase(),
						is(this.authorName));
				collector.checkThat("Author photo src is empty", byLine.bylinesAuthorImage().getAttribute("src"),
						not(emptyOrNullString()));
			} else {
				collector.checkThat("Author photo is displayed when there is no author primary image ", hasAuthorImage,
						is(false));
			}
		}
	};

	private boolean isUniqueAuthor(String docId) {
		DocumentContext documentData = SeleneUtils.getDocuments(docId);
		JsonArray bylineArray = jsonObject(documentData).get("data").getAsJsonObject().get("bylines").getAsJsonObject()
				.get("list").getAsJsonArray();
		int authorNumber = 0;
		for (int i = 0; i < bylineArray.size(); i++) {
			String authorType = JsonPath.using(conf).parse(documentData.jsonString())
					.read("data.bylines.list[" + i + "].type");
			if (authorType.equalsIgnoreCase("AUTHOR")) {
				String authorId = documentData.read("$.data.bylines.list[" + i + "].authorId").toString();
				this.authorId = authorId;
				authorNumber++;
			}
		}
		return authorNumber == 1 ? true : false;
	}

	private boolean authorHasImage(String authorId) {
		String bioEndpoint = "/author/id/" + authorId;
		String[] bioUrl = documentData(bioEndpoint).read("$.data.bioUrl").toString().split("-");
		String authorBioId = bioUrl[bioUrl.length - 1];
		String urlEndpoint = "/document?url=" + authorBioId;
		DocumentContext authorUrl = documentData(urlEndpoint);
		this.authorName = documentData(bioEndpoint).read("data.displayName").toString().toLowerCase();
		var authorPrimaryImage = JsonPath.using(conf).parse(authorUrl.jsonString()).read("data.primaryImage");

		if (authorPrimaryImage == null) {	
			return false;
		} else {
			String authorImageUrl = JsonPath.using(conf).parse(authorUrl.jsonString()).read("data.primaryImage.url");
			String tagArray = JsonPath.using(conf).parse(authorUrl.jsonString()).read("data.taggedImages.list[0].tags.list").toString();
			return authorImageUrl == null && !tagArray.toLowerCase().contains("primary")? false : true;
		}
	}

	private DocumentContext documentData(String endpoint) {
		return SeleneUtils.getSeleneData(endpoint);
	}

	private JsonObject jsonObject(DocumentContext context) {
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(context.jsonString());
		JsonObject jsonObject = jsonTree.getAsJsonObject();

		return jsonObject;
	}
}
