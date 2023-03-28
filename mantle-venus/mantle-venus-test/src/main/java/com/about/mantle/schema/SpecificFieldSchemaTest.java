package com.about.mantle.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecificFieldSchemaTest extends AbstractMntlSchemaTest {

	private final Map<String, Object> fields;
	public SpecificFieldSchemaTest(Map<String, Object> fields) {
		this.fields = fields;
	}

	@Override
	protected List<String> getBasicStructure() {
		return fields.keySet().stream().collect(Collectors.toList());
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		return fields;
	}
}
