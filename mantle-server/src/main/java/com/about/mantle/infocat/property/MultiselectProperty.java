package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MultiselectProperty extends Property<List<MultiselectValueDefinition>> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private MultiselectPropertyValue value;
	private MultiselectPropertyDefinitionMetadata multiselectPropertyDefinitionMetadata;

	public MultiselectProperty () {
		super();
	}
	
	public MultiselectProperty (MultiselectPropertyDefinitionMetadata multiselectPropertyDefinitionMetadata) {
		super();
		this.multiselectPropertyDefinitionMetadata = multiselectPropertyDefinitionMetadata;
	}

	@Override
	public MultiselectPropertyValue getValue() {
		return value;
	}
	
	public void setValue(MultiselectPropertyValue value) {
		this.value = value;
	}

	@Override
	public PropertyDefinitionMetadata getPropertyDefinitionMetadata() {
		return multiselectPropertyDefinitionMetadata;
	}

	public void setPropertyDefinitionMetadata(MultiselectPropertyDefinitionMetadata propertyDefinitionMetadata) {
		this.multiselectPropertyDefinitionMetadata = propertyDefinitionMetadata;
	}
	
	public class MultiselectPropertyValue extends PropertyValue<List<MultiselectValueDefinition>> {
		private List<UUID> selectValue = Collections.emptyList();
		private Set<UUID> selectValueSet = Collections.emptySet();

		public MultiselectPropertyValue() {
			super();
		}
		
		public List<UUID> getSelectValue() {
			return selectValue;
		}

		public void setSelectValue(List<UUID> selectValue) {
			this.selectValue = selectValue;
			selectValueSet = new HashSet<>(selectValue);
		}
		
		@Override
		public List<MultiselectValueDefinition> getPrimaryValue() {
			List<MultiselectValueDefinition> selectedSupportedValues = new ArrayList<>();

			for(MultiselectValueDefinition supportedValue : multiselectPropertyDefinitionMetadata.getSupportedValues()) {
				if(selectValueSet.contains(supportedValue.getId())) {
					supportedValue.setIsSelected(true);
				}

				selectedSupportedValues.add(supportedValue);
			}
			
			return selectedSupportedValues;
	    }
	}

	public class MultiselectPropertyDefinitionMetadata extends PropertyDefinitionMetadata {
        private List<MultiselectValueDefinition> supportedValues;

        public MultiselectPropertyDefinitionMetadata() {
			super();
		}

		public MultiselectPropertyDefinitionMetadata(List<MultiselectValueDefinition> supportedValues) {
			super();
			this.supportedValues = supportedValues;
		}
        
		public List<MultiselectValueDefinition> getSupportedValues() {
			return supportedValues;
		}
		public void setSupportedValues(List<MultiselectValueDefinition> supportedValues) {
			this.supportedValues = supportedValues;
		}
    }
}
