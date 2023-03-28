package com.about.mantle.model.extended.docv2;

import com.about.mantle.model.extended.docv2.sc.quantity.QuantityRange;

import java.io.Serializable;

public class QuantityWithUnit implements Serializable{

    private String unit;
    private QuantityRange quantity;

    public QuantityWithUnit(){
    }

    public QuantityWithUnit(QuantityRange quantity, String unit){
        this.quantity = quantity;
        this.unit = unit;
    }

    public QuantityRange getQuantity() {
        return quantity;
    }

    public void setQuantity(QuantityRange quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
