package com.about.mantle.cache.type;

import java.lang.reflect.Method;
import java.util.List;

import com.about.globe.core.cache.type.JavaTypeReference;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ListOfDocumentTaxeneCompositeTypeReference implements JavaTypeReference {
	public ListOfDocumentTaxeneCompositeTypeReference(){}

	@Override
	public JavaType getTypeReference(ObjectMapper mapper, Method method) {
		JavaType inside = mapper.getTypeFactory().constructParametrizedType(DocumentTaxeneComposite.class, DocumentTaxeneComposite.class, BaseDocumentEx.class);
		return mapper.getTypeFactory().constructCollectionType(List.class, inside);
	}
}
