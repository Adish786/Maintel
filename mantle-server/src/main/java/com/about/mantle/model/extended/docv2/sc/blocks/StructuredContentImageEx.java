package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;

/**
 * Implements the `ImageContent` definition of the Structured Content schema
 */
public class StructuredContentImageEx extends AbstractStructuredContentContentEx<StructuredContentImageEx.StructuredContentImageDataEx> {

    /**
     * Implements the `ImageContentData` definition of the Structured Content schema
     */
    public static class StructuredContentImageDataEx extends AbstractStructuredContentDataEx {

        private ImageEx image = ImageEx.EMPTY;

        public ImageEx getImage() {
            return image;
        }

        public void setImage(ImageEx image) {
            this.image = image;
        }

        @Override
        public String toString() {
            return "StructuredContentImageDataEx{" +
                    "image=" + image +
                    "} " + super.toString();
        }
    }
}
