package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentJourneyNavEx extends AbstractStructuredContentContentEx<StructuredContentJourneyNavEx.StructuredContentJourneyNavDataEx> {

    public static class StructuredContentJourneyNavDataEx extends AbstractStructuredContentDataEx {
        private String heading;
        private String title;
        private String text;
        private ImageEx image;
        private Long rootDocId;
        private BaseDocumentEx document;

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ImageEx getImage() {
            return image;
        }

        public void setImage(ImageEx image) {
            this.image = image;
        }

        public Long getRootDocId() {
            return rootDocId;
        }

        public void setRootDocId(Long rootDocId) {
            this.rootDocId = rootDocId;
        }

        public BaseDocumentEx getDocument() {
            return document;
        }

        public void setDocument(BaseDocumentEx document) {
            this.document = document;
        }

        @Override
        public String toString() {
            return "StructuredContentJourneyNavDataEx{" +
                    "heading='" + heading + '\'' +
                    ", title='" + title + '\'' +
                    ", text='" + text + '\'' +
                    ", image=" + image +
                    ", rootDocId=" + rootDocId +
                    ", document=" + document +
                    "} " + super.toString();
        }
    }
}