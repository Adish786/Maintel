package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentKeyTermsEx extends AbstractStructuredContentContentEx<StructuredContentKeyTermsEx.StructuredContentKeyTermsDataEx> {
    public static class StructuredContentKeyTermsDataEx extends AbstractStructuredContentDataEx {
        private SliceableListEx<KeyTerm> keyTerms;

        public SliceableListEx<KeyTerm> getKeyTerms() {
            return keyTerms;
        }

        public void setKeyTerms(SliceableListEx<KeyTerm> keyTerms) {
            this.keyTerms = keyTerms;
        }

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("StructuredContentKeyTermsDataEx [keyTerms=");
			builder.append(keyTerms);
			builder.append("]");
			return builder.toString();
		}
    }

    public static class KeyTerm {
        private String definition;
        private String term;
        private String featureTitle;
        private String featureLink;

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
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
            builder.append("KeyTerm [definition=");
            builder.append(definition);
            builder.append(", term=");
            builder.append(term);
            builder.append(", featureTitle=");
            builder.append(featureTitle);
            builder.append(", featureLink=");
            builder.append(featureLink);
            builder.append("]");
            return builder.toString();
        }
    }
}
