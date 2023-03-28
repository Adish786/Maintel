package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.dateutils.DateUtils;
import com.about.mantle.utils.selene.document.client.DocumentClient;
import com.about.mantle.utils.selene.documentutils.DocumentHelper;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.meta.client.UpdateAndRestoreMeta;
import com.about.mantle.utils.url.URLUtils;
import com.about.mantle.venus.model.components.article.MntlBylinesReviewDateComponent;
import com.about.mantle.venus.model.components.article.MntlUpdatedStampBylinesComponent;
import com.about.venus.core.driver.selection.DriverSelection;
import com.about.venus.core.test.VenusTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.about.mantle.utils.selene.utils.DocumentTestUtils.documentTestWithDelete;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MntlDisplayedDateTest extends VenusTest implements MntlCommonTestMethods {

	public void testPublishedDate(String template, String documentFilePath, String metaFilePath, DriverSelection.Matcher matcher) throws IOException {
		displayedDateTest(template, documentFilePath, metaFilePath, "Publish", false, matcher);
	}

	public void testMinorUpdate(String template, String documentFilePath, String metaFilePath, DriverSelection.Matcher matcher) throws IOException {
		displayedDateTest(template, documentFilePath, metaFilePath, "Update", false, matcher);
	}

	public void testMajorUpdate(String template, String documentFilePath, String metaFilePath, DriverSelection.Matcher matcher) throws IOException {
		displayedDateTest(template, documentFilePath, metaFilePath, "Update", true, matcher);
	}

	public void testReviewedDate(String url, String template, Meta.Review review, DriverSelection.Matcher matcher) throws InterruptedException {
		DateUtils dateUtils = new DateUtils(review.getLastReviewed(), "MM/dd/yy");
		Long docId = new URLUtils(url).docIdFromUrl();
		UpdateAndRestoreMeta updateAndRestoreMeta = new UpdateAndRestoreMeta(docId, new DocumentHelper(template).getDocumentClient());
		updateAndRestoreMeta.runTest(Meta.builder().docId(docId).review(review).build(), () -> {
			test(matcher, driver -> {
				Runner runner = startTest(url, driver).onComponent(MntlBylinesReviewDateComponent.class);
					runner.loadUrl().runTest(test -> {
						MntlBylinesReviewDateComponent component = (MntlBylinesReviewDateComponent) runner.component();
						assertThat("Reviewed on date is not correct",
											component.getElement().getText(),
											containsString("on " + dateUtils.getDateInGivenFormat()));
					});
			});
		});

	}

	private void displayedDateTest(String template, String documentFilePath, String metaFilePath, String operation,
								   boolean updateDisplayedDate, DriverSelection.Matcher matcher) throws IOException {
		DocumentHelper documentHelper = new DocumentHelper(template);
		DocumentClient client =
				documentHelper.documentWithMeta(documentFilePath, metaFilePath);
		String expectedDisplayedDateType = "Published";
		if(operation.equals("Update")) {
			if(updateDisplayedDate) {
				client.update(OK, false, true);
				expectedDisplayedDateType = "Updated";
			} else {
				client.update(OK, false, false);
			}
			//wait for update
			wait(3, TimeUnit.SECONDS);
		}
		String finalExpectedDisplayedDateType = expectedDisplayedDateType;
		documentTestWithDelete(client, () -> {
			test(matcher, driver -> {
				Runner runner = startTest(documentHelper.getUrl(), driver).onComponent(MntlUpdatedStampBylinesComponent.class);
				runner.loadUrl().runTest(
						test -> {
							MntlUpdatedStampBylinesComponent component = (MntlUpdatedStampBylinesComponent) runner.component();
							assertThat("Displayed date is not correct",
												component.getElement().getText(),
												is(finalExpectedDisplayedDateType
														   + " " + documentHelper.getDisplayedDate("MM/dd/yy")));
						});
			});
		});
	}
}

