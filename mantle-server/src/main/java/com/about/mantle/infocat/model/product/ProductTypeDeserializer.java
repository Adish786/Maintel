package com.about.mantle.infocat.model.product;

import java.io.IOException;
import java.util.Iterator;

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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

public class ProductTypeDeserializer extends AsPropertyTypeDeserializer {
	
	private static final long serialVersionUID = 1L;
	
	public ProductTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
			JavaType defaultImpl) {
		super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
	}
	
	public ProductTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
			JavaType defaultImpl, As inclusion) {
		super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, inclusion);
	}
	
	public ProductTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
		super(src, property);
	}
	
	@Override
	public TypeDeserializer forProperty(BeanProperty prop) {
	    return (prop == _property) ? this : new ProductTypeDeserializer(this, prop);
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
		Class<? extends Product> subType = DefaultProduct.class;
		
	    JsonNode category = node.get("category");
	    
	    if(category != null) {
		    ArrayNode ancestors = (ArrayNode)category.get("ancestors");
		    
		    //In this case the category tree is at least 3 levels deep, use the category of the 2nd level. Otherwise use the top level category node
		    if(ancestors != null && ancestors.size() > 1) {
		    	Iterator<JsonNode> itr = ancestors.elements();
			    itr.next();
			    category = itr.next();
		    } 
		    
		    String categoryName = category.get("name").asText();
		    
		    if (categoryName.equalsIgnoreCase("Books")) {
		        subType = BookProduct.class;
		    } else if (categoryName.equalsIgnoreCase("Media") || categoryName.equalsIgnoreCase("Software")) {
		        subType = MediaSoftwareProduct.class;
		    } else if (categoryName.equalsIgnoreCase("Brands & Retailers")) {
		        subType = BrandProduct.class;
		    } else if (categoryName.equalsIgnoreCase("Animals")) {
				subType = AnimalProfile.class;
			} else if (categoryName.equalsIgnoreCase("Dog Breeds")) {
				subType = DogProfile.class;
			} else if (categoryName.equalsIgnoreCase("Cat Breeds")) {
				subType = CatProfile.class;
			} else if (categoryName.equalsIgnoreCase("Plants")) {
				subType = PlantProfile.class;
			}
	    }
	    
	    return subType;
	}
}
