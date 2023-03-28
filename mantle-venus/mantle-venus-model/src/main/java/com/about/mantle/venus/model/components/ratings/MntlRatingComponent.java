package com.about.mantle.venus.model.components.ratings;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentId("mntl-rating")
public class MntlRatingComponent extends MntlComponent {


	public MntlRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	
	public static class Form extends MntlComponent.AbstractForm {

		public Form(WebDriverExtended driver, WebElementEx element, String prefix) {
			super(driver, element, prefix);
		}

		public void docUrl(String docUrl) {
			field("pl-edit_made-it_rating_userRating_documentId_document_url", docUrl);
		}

	}

}