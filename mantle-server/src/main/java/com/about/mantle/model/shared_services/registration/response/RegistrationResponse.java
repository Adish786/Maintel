package com.about.mantle.model.shared_services.registration.response;

import com.about.mantle.model.shared_services.responses.SharedServicesBaseResponse;

public class RegistrationResponse extends SharedServicesBaseResponse<RegistrationResponse.RegistrationResponseData> {

    public static class RegistrationResponseData {
        private String hashId;
        private String emailReputation;

        public String getHashId() {
            return hashId;
        }

        public void setHashId(String hashId) {
            this.hashId = hashId;
        }

        public String getEmailReputation() {
            return emailReputation;
        }

        public void setEmailReputation(String emailReputation) {
            this.emailReputation = emailReputation;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RegistrationResponseData{");
            sb.append("hashId='").append(hashId).append('\'');
            sb.append(", emailReputation='").append(emailReputation).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
