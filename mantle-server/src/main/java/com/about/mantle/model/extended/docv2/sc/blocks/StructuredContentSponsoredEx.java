package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentSponsoredEx extends AbstractStructuredContentContentEx<StructuredContentSponsoredEx.StructuredContentSponsoredDataEx> {

    public static class StructuredContentSponsoredDataEx extends AbstractStructuredContentDataEx {

        private String title;
        private ImageEx image;
        private String uri;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ImageEx getImage() {
            return image;
        }

        public void setImage(ImageEx image) {
            this.image = image;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StructuredContentSponsoredDataEx{");
            sb.append("title='").append(title).append('\'');
            sb.append(", image=").append(image);
            sb.append(", uri='").append(uri).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
