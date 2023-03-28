package com.about.mantle.model.shared_services.registration.request;

import com.about.mantle.model.shared_services.registration.request.actions.Action;

import java.util.Collections;
import java.util.List;

public class RegistrationRequestBody {
    private RegistrationRequestBodyData data;

    public RegistrationRequestBody(RegistrationRequestBodyData data) {
        this.data = data;
    }

    public static class RegistrationRequestBodyData {
        private String email;
        private String regSourceId;
        private String brand;
        private List<Action> actions = Collections.emptyList();

        public RegistrationRequestBodyData(String email, String regSourceId, String brand, List<Action> actions, String birthDate) {
            this.email = email;
            this.regSourceId = regSourceId;
            this.brand = brand;
            this.actions = actions;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRegSourceId() {
            return regSourceId;
        }

        public void setRegSourceId(String regSourceId) {
            this.regSourceId = regSourceId;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(List<Action> actions) {
            this.actions = actions;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RegistrationRequestBodyData{");
            sb.append("email='").append(email).append('\'');
            sb.append(", regSourceId='").append(regSourceId).append('\'');
            sb.append(", brand='").append(brand).append('\'');
            sb.append(", actions=").append(actions);
            sb.append('}');
            return sb.toString();
        }
    }

    public RegistrationRequestBodyData getData() {
        return data;
    }

    public void setData(RegistrationRequestBodyData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistrationRequestBody{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
