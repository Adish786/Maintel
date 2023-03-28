package com.about.mantle.infocat.property;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;

@JsonTypeInfo(use = Id.NONE)
@JsonTypeResolver(PropertyResolver.class)
public abstract class Property<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	
	protected PropertyDefinitionMetadata propertyDefinitionMetadata;
	
	public Property() {
	}
    
	public Property(PropertyDefinitionMetadata propertyDefinitionMetadata) {
		this.propertyDefinitionMetadata = propertyDefinitionMetadata;
	}

    public static class PropertyDefinitionMetadata {
		private String id;
		private String grouping;
        private PropertyDefinition definition;
        
        public PropertyDefinitionMetadata() {
		}
        
        public PropertyDefinitionMetadata(String id, String grouping, PropertyDefinition definition) {
			this.id = id;
			this.grouping = grouping;
			this.definition = definition;
		}
        
        public PropertyDefinitionMetadata(String id, PropertyDefinition definition) {
			this(id, null, definition);
		}
        
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getGrouping() {
			return grouping;
		}
		public void setGrouping(String grouping) {
			this.grouping = grouping;
		}

		public PropertyDefinition getDefinition() {
			return definition;
		}
		public void setDefinition(PropertyDefinition definition) {
			this.definition = definition;
		}
    }
    
    public static class PropertyDefinition {
        private String name;
        private PropertyDefinitionType type;
        
        public PropertyDefinition() {
		}
        
        public PropertyDefinition(String name, PropertyDefinitionType type) {
			super();
			this.name = name;
			this.type = type;
		}
        
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public PropertyDefinitionType getType() {
			return type;
		}
		public void setType(PropertyDefinitionType type) {
			this.type = type;
		}
    }

    public static enum PropertyDefinitionType {
    	BOOLEAN,
		CURRENCY,
		CURRENCY_RANGE,
		DATE,
		MULTI_SELECT,
		NUMERICAL,
		NUMERICAL_RANGE,
		RETAILER_LIST,
		STRING,
		STRING_LIST,
		DESTINATION_URL_LIST,
		MEASUREMENT,
		MEASUREMENT_RANGE,
		URL,
		QUESTION_LIST,
		REFERENCE_LIST
	}
	
	public PropertyDefinitionMetadata getPropertyDefinitionMetadata() {
		return propertyDefinitionMetadata;
	}

	public void setPropertyDefinitionMetadata(PropertyDefinitionMetadata propertyDefinitionMetadata) {
		this.propertyDefinitionMetadata = propertyDefinitionMetadata;
	}
	
	public abstract PropertyValue<T> getValue();
    
}