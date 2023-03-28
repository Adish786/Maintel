package com.about.mantle.model.shared_services.registration.request.brands.parents;

public class Child {
    private String birthDate;

    public Child(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Child{");
        sb.append("birthDate='").append(birthDate);
        sb.append('}');
        return sb.toString();
    }
}
