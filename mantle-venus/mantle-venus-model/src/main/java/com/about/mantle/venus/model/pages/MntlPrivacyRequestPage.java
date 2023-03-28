package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.forms.MntlEmailFormManualInstructionsComponent;
import com.about.mantle.venus.model.components.forms.MntlPrivacyRequestComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.utils.Lazy;

public class MntlPrivacyRequestPage extends MntlPage {

	private final Lazy<MntlEmailFormManualInstructionsComponent> emailFormManualInstruction;
	private final Lazy<MntlPrivacyRequestComponent> privacyRequest;

	public MntlPrivacyRequestPage(WebDriverExtended driver) {
		super(driver);
		this.emailFormManualInstruction = lazy(() -> findComponent(MntlEmailFormManualInstructionsComponent.class));
		this.privacyRequest = lazy(() -> findComponent(MntlPrivacyRequestComponent.class));

	}

	public MntlEmailFormManualInstructionsComponent emailFormManualInstruction() {
		return emailFormManualInstruction.get();
	}

	public MntlPrivacyRequestComponent privacyRequest() {
		return privacyRequest.get();
	}

}
