package com.about.mantle.infocat.property;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

public class PropertyDeserializer extends AsPropertyTypeDeserializer {
	
	private static final long serialVersionUID = 1L;
	
	public PropertyDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
			JavaType defaultImpl) {
		super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
	}
	
	public PropertyDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
			JavaType defaultImpl, As inclusion) {
		super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, inclusion);
	}
	
	public PropertyDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
		super(src, property);
	}
	
	@Override
	public TypeDeserializer forProperty(BeanProperty prop) {
	    return (prop == _property) ? this : new PropertyDeserializer(this, prop);
	}

	@Override
	public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
	    JsonNode node = jp.readValueAsTree();
	    Class<?> subType = findSubType(node);
	    JavaType type = ctxt.constructType(subType);

	    JsonParser jsonParser = new TreeTraversingParser(node, jp.getCodec());
	    if (jsonParser.getCurrentToken() == null) {
	        jsonParser.nextToken();
	    }
	    
	    JsonDeserializer<Object> deser = ctxt.findContextualValueDeserializer(type, _property);
	    return deser.deserialize(jsonParser, ctxt);
	}

	protected Class<?> findSubType(JsonNode node) {
		
		JsonNode type = node.get("propertyDefinitionMetadata").get("definition").get("type");
	    String propertyType=type.asText();
	    @SuppressWarnings("rawtypes")
		Class<? extends Property> subType = null;
	    
	    if (propertyType.equalsIgnoreCase("BOOLEAN")) {
	        subType = BooleanProperty.class;
	    } else if (propertyType.equalsIgnoreCase("CURRENCY")) {
	        subType = CurrencyProperty.class;
	    } else if (propertyType.equalsIgnoreCase("CURRENCY_RANGE")) {
	        subType = CurrencyRangeProperty.class;
	    } else if (propertyType.equalsIgnoreCase("DATE")) {
	        subType = DateProperty.class;
	    } else if (propertyType.equalsIgnoreCase("MULTI_SELECT")) {
	        subType = MultiselectProperty.class;
	    } else if (propertyType.equalsIgnoreCase("NUMERICAL")) {
	        subType = NumericalProperty.class;
	    } else if (propertyType.equalsIgnoreCase("NUMERICAL_RANGE")) {
	        subType = NumericalRangeProperty.class;
	    } else if (propertyType.equalsIgnoreCase("RETAILER_LIST")) {
	        subType = RetailerListProperty.class;
	    } else if (propertyType.equalsIgnoreCase("STRING")) {
	        subType = StringProperty.class;
	    } else if (propertyType.equalsIgnoreCase("STRING_LIST")) {
	        subType = StringListProperty.class;
	    } else if (propertyType.equalsIgnoreCase("DESTINATION_URL_LIST")) {
	        subType = DestinationUrlListProperty.class;
	    } else if (propertyType.equalsIgnoreCase("MEASUREMENT")) {
	        subType = MeasurementProperty.class;
		} else if (propertyType.equalsIgnoreCase("MEASUREMENT_RANGE")) {
			subType = MeasurementRangeProperty.class;
		} else if (propertyType.equalsIgnoreCase("URL")) {
			subType = URLProperty.class;
		} else if (propertyType.equalsIgnoreCase("QUESTION_LIST")) {
			subType = QuestionListProperty.class;
		} else if (propertyType.equalsIgnoreCase("REFERENCE_LIST")) {
			subType = ReferenceListProperty.class;
		}
	    return subType;
	}
}
