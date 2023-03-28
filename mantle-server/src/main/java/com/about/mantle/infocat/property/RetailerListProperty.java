package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.infocat.model.product.Product;

public class RetailerListProperty extends Property<List<Retailer>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private RetailerListPropertyValue value;
	
	public RetailerListProperty() {
		super();
	}
    
	public RetailerListProperty(RetailerListProperty property) {
    	super(property.getPropertyDefinitionMetadata());
		this.value = property.getValue();
	}
	
    public RetailerListProperty(RetailerListPropertyValue value) {
    	super(new PropertyDefinitionMetadata(null, new PropertyDefinition(Product.PropertyType.RETAILERS.getKey(), PropertyDefinitionType.RETAILER_LIST)));
		this.value = value;
	}
    
    public RetailerListProperty(String asin, List<Retailer> retailers) {
    	this(new RetailerListPropertyValue(asin, retailers));
	}
	
	@Override
	public RetailerListPropertyValue getValue() {
		return value;
	}
	
	public void setValue(RetailerListPropertyValue value) {
		this.value = value;
	}
	
	public static class RetailerListPropertyValue extends PropertyValue<List<Retailer>> {

		private String asin;
	    private List<Retailer> retailers  = Collections.emptyList();
	    
	    public RetailerListPropertyValue() {
			super();
		}
	    
	    public RetailerListPropertyValue(String asin, List<Retailer> retailers) {
			super();
			this.asin = asin;
			this.retailers = retailers;
		}
	    
		public String getAsin() {
			return asin;
		}
		
		public void setAsin(String asin) {
			this.asin = asin;
		}
		
		public List<Retailer> getRetailers() {
			return retailers;
		}
		
		public void setRetailers(List<Retailer> retailers) {
			this.retailers = retailers;
		}
		
		@Override
		public List<Retailer> getPrimaryValue() {
	    	return retailers;
	    }
	}
	
}