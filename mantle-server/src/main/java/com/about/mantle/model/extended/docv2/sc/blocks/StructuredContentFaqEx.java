package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentFaqEx extends AbstractStructuredContentContentEx<StructuredContentFaqEx.StructuredContentFaqDataEx> {
    public static class StructuredContentFaqDataEx extends AbstractStructuredContentDataEx {
        private SliceableListEx<Faq> faqs;
        private String heading;
        private Boolean hideOnTOC;

        public SliceableListEx<Faq> getFaqs() {
            return faqs;
        }

        public void setFaqs(SliceableListEx<Faq> faqs) {
            this.faqs = faqs;
        }

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public Boolean getHideOnTOC() {
            return hideOnTOC;
        }

        public void setHideOnTOC(Boolean hideOnTOC) {
            this.hideOnTOC = hideOnTOC;
        }

        @Override
        public String toString() {
            return "StructuredContentFaqDataEx{" +
                    "faqs=" + faqs +
                    ", heading='" + heading + '\'' +
                    ", hideOnTOC=" + hideOnTOC +
                    "} " + super.toString();
        }
    }

    public static class Faq {
        private String answer;
        private String question;
        private String featureTitle;
        private String featureLink;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getFeatureTitle() {
            return featureTitle;
        }

        public void setFeatureTitle(String featureTitle) {
            this.featureTitle = featureTitle;
        }

        public String getFeatureLink() {
            return featureLink;
        }

        public void setFeatureLink(String featureLink) {
            this.featureLink = featureLink;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Faq [answer=");
            builder.append(answer);
            builder.append(", question=");
            builder.append(question);
            builder.append(", featureTitle=");
            builder.append(featureTitle);
            builder.append(", featureLink=");
            builder.append(featureLink);
            builder.append("]");
            return builder.toString();
        }
    }
}
