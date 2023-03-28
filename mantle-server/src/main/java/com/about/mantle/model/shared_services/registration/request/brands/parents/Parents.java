package com.about.mantle.model.shared_services.registration.request.brands.parents;

import com.about.mantle.model.shared_services.registration.request.Brand;

import java.util.List;

public class Parents extends Brand {
    private List<Child> children;

    public Parents(List<Child> children) {
        this.children = children;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Parents{");
        sb.append("children='").append(children);
        sb.append('}');
        return sb.toString();
    }
}
