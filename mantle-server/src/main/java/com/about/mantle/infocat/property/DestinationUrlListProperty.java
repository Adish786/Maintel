package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.about.mantle.infocat.model.DestinationUrl;
import com.about.mantle.infocat.model.product.Product;

public class DestinationUrlListProperty extends Property<List<DestinationUrl>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private DestinationUrlListPropertyValue value;
	
	public DestinationUrlListProperty() {
		super();
	}
    
    public DestinationUrlListProperty(DestinationUrlListPropertyValue value) {
    	super(new PropertyDefinitionMetadata(null, new PropertyDefinition(Product.PropertyType.DESTINATIONURLS.getKey(), PropertyDefinitionType.DESTINATION_URL_LIST)));
		this.value = value;
	}
    
    public DestinationUrlListProperty(List<DestinationUrl> destinationUrls) {
    	this(new DestinationUrlListPropertyValue(destinationUrls));
	}
	
	@Override
	public DestinationUrlListPropertyValue getValue() {
		return value;
	}
	
	public void setValue(DestinationUrlListPropertyValue value) {
		this.value = value;
	}
	
	public static class DestinationUrlListPropertyValue extends PropertyValue<List<DestinationUrl>> {

	    private List<DestinationUrl> destinationUrls  = Collections.emptyList();
	    
	    public DestinationUrlListPropertyValue() {
			super();
		}
	    
	    public DestinationUrlListPropertyValue(List<DestinationUrl> destinationUrls) {
			super();
			this.destinationUrls = destinationUrls;
		}
	    
		
		public List<DestinationUrl> getDestinationUrls() {
			return destinationUrls;
		}
		
		public void setDestinationUrls(List<DestinationUrl> destinationUrls) {
			this.destinationUrls = destinationUrls;
		}
		
		@Override
		public List<DestinationUrl> getPrimaryValue() {
	    	return destinationUrls;
	    }
	}
	
}