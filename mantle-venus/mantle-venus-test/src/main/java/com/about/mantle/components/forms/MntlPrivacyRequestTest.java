package com.about.mantle.components.forms;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.forms.MntlEmailFormManualInstructionsComponent;
import com.about.mantle.venus.model.components.forms.MntlPrivacyRequestComponent;
import com.about.mantle.venus.model.pages.MntlPrivacyRequestPage;
import com.about.venus.core.driver.factory.feature.Features;
import com.about.venus.core.driver.selection.Device;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.Select;

import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

public abstract class MntlPrivacyRequestTest extends MntlVenusTest implements MntlCommonTestMethods {

	abstract public String setUrl();
	
	public void testPrivacyRequest() {
		test(devices(Device.Any), (driver) -> {
			driver.get(setUrl());
			MntlPrivacyRequestPage initialPage = new MntlPrivacyRequestPage(driver);
			MntlPrivacyRequestComponent initialPrivacyRequest = (MntlPrivacyRequestComponent) initialPage
					.privacyRequest();
			initialPage.waitFor().aMoment();
			int selectOptions = initialPrivacyRequest.requestSelectoptions().size();
			for (int i = 0; i < selectOptions; i++) {
				MntlPrivacyRequestPage page = new MntlPrivacyRequestPage(driver);
				MntlPrivacyRequestComponent privacyRequest = (MntlPrivacyRequestComponent) page.privacyRequest();
				MntlEmailFormManualInstructionsComponent emailInstructions = (MntlEmailFormManualInstructionsComponent) page
						.emailFormManualInstruction();
				try {
					new Select(privacyRequest.requestSelect().getWrappedElement()).selectByIndex(i);
					page.waitFor().aMoment();
					emailInstructions.optionText().scrollIntoView();
					collector.checkThat("Text is Empty. ", emailInstructions.optionText().text(),
							not(isEmptyOrNullString()));
					collector.checkThat("Text is Empty. ", emailInstructions.tMogValue().text(),
							not(isEmptyOrNullString()));
					privacyRequest.submitRequest("test", "qaprivacyrequests@dotdash.com");
					page.waitFor().aMoment();
					collector.checkThat("Text is Empty. ", privacyRequest.sucessMessage().text(),
							not(isEmptyOrNullString()));
					driver.navigate().refresh();

				} catch (StaleElementReferenceException e) {
					e.printStackTrace();
				}
			}
		},Features.condition(!isProd()));
	}
	
}
