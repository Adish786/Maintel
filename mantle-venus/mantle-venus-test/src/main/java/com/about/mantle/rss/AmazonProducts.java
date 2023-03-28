package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class AmazonProducts {


    @XmlElement(name = "product", namespace = "https://amazon.com/ospublishing/1.0/")
    private List<AmazonProduct> amazonProduct = new ArrayList<>();
    
    
    public List<AmazonProduct> getAmazonProduct() {
        return this.amazonProduct;
    }

}
