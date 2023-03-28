package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MntlTaxonomyTest extends AbstractMntlSchemaTest {

	private boolean hasSpecialty;

	public MntlTaxonomyTest() {
		this(false);
	}

	public MntlTaxonomyTest(boolean hasSpecialty) {
		this.hasSpecialty = hasSpecialty;
	}

	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = new ArrayList<>();
		fieldsToVerify.add("@context");
		fieldsToVerify.add("@type");
		fieldsToVerify.add("itemListElement");
		fieldsToVerify.add("numberOfItems"); // Dynamic Value
		fieldsToVerify.add("mainEntityOfPage.headline");
		fieldsToVerify.add("mainEntityOfPage.datePublished");
		fieldsToVerify.add("mainEntityOfPage.dateModified");
		fieldsToVerify.add("mainEntityOfPage.description");
		fieldsToVerify.add("mainEntityOfPage.publisher");
		fieldsToVerify.add("mainEntityOfPage.description");
		fieldsToVerify.add("mainEntityOfPage.Specialty");
		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		HashMap<String, Object> fieldsToVerify = new HashMap<>();
		fieldsToVerify.put("@type", "ItemList");
		fieldsToVerify.put("mainEntityOfPage.'@type'[0]", "CollectionPage");
		fieldsToVerify.put("itemListElement[0].@type", "ListItem");
		return fieldsToVerify;
	}
}