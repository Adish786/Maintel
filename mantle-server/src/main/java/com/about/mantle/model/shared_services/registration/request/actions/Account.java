package com.about.mantle.model.shared_services.registration.request.actions;

import com.about.mantle.model.shared_services.registration.request.Brand;

public class Account {
    private Brand brandData;

    public Account(Brand brandData) {
        this.brandData = brandData;
    }

    public Brand getBrandData() {
        return brandData;
    }

    public void setBrandData(Brand brandData) {
        this.brandData = brandData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Account{");
        sb.append("brandData='").append(brandData);
        sb.append('}');
        return sb.toString();
    }
}
