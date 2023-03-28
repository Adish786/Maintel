package com.about.mantle.components.forms;

import com.about.mantle.commons.Email;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.forms.MntlNewsletterSignupComponent;
import com.about.venus.core.driver.WebDriverExtended;
import org.codehaus.plexus.util.StringUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

public abstract class MntlNewsletterSignupTest extends MntlVenusTest {

	private static String TEST_EMAIL = "yudytacct@gmail.com";
	private static String url = "https://www.mantle.abt/pattern-library/mantle-components/newsletter-signup";
	private String MAIL = getMail();
	private String PASSWD = getPassword();
	private String WELCOME_SUBJECT = getWelcomeSubject();
	private String WELCOME_MSG = getWelcomeMsg();

	protected abstract String getMail();

	protected abstract String getPassword();

	protected abstract String getWelcomeSubject();

	protected abstract String getWelcomeMsg();

	protected abstract String getDisclaimer();

	protected <T extends MntlNewsletterSignupComponent> void welcomeMsg(WebDriverExtended driver, T newsletter) {
		newsletter.subscribe(MAIL);
		newsletter.waitFor().aMoment();
		try {
			new Email(MAIL, PASSWD, "imap.gmail.com").folder("Inbox")
					.unreadMessage(WELCOME_SUBJECT).body().test(WELCOME_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected <T extends MntlNewsletterSignupComponent> void PlTest(WebDriverExtended driver, T component) {
		collector.checkThat("heading is not displayed", component.heading().isDisplayed(), is(true));
		collector.checkThat("heading does not have any content", StringUtils.isNotEmpty(component.heading().getText()),
				is(true));
		collector.checkThat("form is not displayed", component.displayed(), is(true));
		collector.checkThat("email input label is not displayed", component.emailInputLabel().isDisplayed(), is(true));
		collector.checkThat("email input field is not displayed", component.emailInputField().isDisplayed(), is(true));
		String regex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
		collector.checkThat("Pattern attribute is not correct for the email Input field",component.emailInputField().getAttribute("pattern"),is(regex));
		collector.checkThat("Required attribute is not present for the email Input field",component.emailInputField().getAttributes().keySet().contains("required"),is(true));
	
		collector.checkThat("submit button is not displayed", component.submitButton().isDisplayed(), is(true));
	}

	protected <T extends MntlNewsletterSignupComponent> void newsletterTest(WebDriverExtended driver, T newsletter) {
		newsletter.subscribe(TEST_EMAIL);
		newsletter.waitFor().aMoment();
		collector.checkThat("success message is not correct", newsletter.signupSuccess().text(),
				is("Thank you " + TEST_EMAIL + " for signing up."));
	};
}