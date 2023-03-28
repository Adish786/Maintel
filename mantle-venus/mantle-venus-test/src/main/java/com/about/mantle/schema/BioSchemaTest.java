package com.about.mantle.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioSchemaTest extends AbstractMntlSchemaTest{

	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = new ArrayList<>();

		fieldsToVerify.add("@context");
		fieldsToVerify.add("@type");
		fieldsToVerify.add("@id");
		fieldsToVerify.add("about");
		fieldsToVerify.add("about.name");
		fieldsToVerify.add("about.description");
		fieldsToVerify.add("about.sameAs");
		fieldsToVerify.add("about.jobTitle");
		fieldsToVerify.add("about.knowsAbout");
		fieldsToVerify.add("about.homeLocation");
		fieldsToVerify.add("about.homeLocation.Name");
		fieldsToVerify.add("about.alumniOf");
		fieldsToVerify.add("about.alumniOf.Name");
		fieldsToVerify.add("about.alumniOf.@type");
		fieldsToVerify.add("about.url");
		fieldsToVerify.add("publisher");
		fieldsToVerify.add("publisher.brand");
		fieldsToVerify.add("publisher.name");
		fieldsToVerify.add("publisher.logo");
		fieldsToVerify.add("publisher.sameAs");

		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		HashMap<String, Object> fieldsToVerify = new HashMap<>();

		fieldsToVerify.put("@type", "ProfilePage");
		fieldsToVerify.put("about.@type", "Person");
		fieldsToVerify.put("about.homeLocation.@type", "Place");
		fieldsToVerify.put("about.alumniOf[0].@type", "Organization");

		return fieldsToVerify;
	}

}