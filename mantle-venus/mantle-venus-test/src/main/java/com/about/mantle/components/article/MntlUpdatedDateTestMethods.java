package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.driver.WebDriverExtended;
import com.jayway.jsonpath.DocumentContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;

@Deprecated // https://dotdash.atlassian.net/browse/AXIS-2022
public abstract class MntlUpdatedDateTestMethods extends MntlVenusTest implements MntlCommonTestMethods{

	abstract public Class<? extends MntlComponent> getComponent();
	
	protected void updatedDateTest (WebDriverExtended driver, String updatedDateText, String frontEndFormattedDate) {

		MntlBasePage page = new MntlBasePage<>(driver, getComponent());
		MntlComponent updatedStamp =  page.getComponent();
		String dLUpdatedDate = returnDisplayedDate(driver, "$.data.dates.displayed");
		String updateDate = frontEndFormattedDate;
		collector.checkThat("updated Date is not displayed on load", updatedStamp.displayed(), is(true));
		collector.checkThat("updated Date does not start with the word 'Updated'", updatedStamp.text(),
				startsWith(updatedDateText));
		collector.checkThat("Header Date " + updateDate + " is different from \"displayed\" date" + dLUpdatedDate,
				updateDate, is(dLUpdatedDate));
	};

	protected String returnDisplayedDate(WebDriverExtended driver, String date) {
		String queryFreeUrl = new MntlUrl(driver.getCurrentUrl(), true).queryFreeUrl();
		String docID = queryFreeUrl.split("-")[queryFreeUrl.split("-").length - 1];
		DocumentContext seleneData = SeleneUtils.getDocuments(docID);
		Date displayedDateSelene = new Date(Long.parseLong(seleneData.read(date).toString()));
		DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		String seleneFormattedDate = format.format(displayedDateSelene);
		return seleneFormattedDate;
	}

}
