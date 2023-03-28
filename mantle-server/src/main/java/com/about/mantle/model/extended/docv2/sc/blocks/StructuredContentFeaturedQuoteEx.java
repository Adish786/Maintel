package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentFeaturedQuoteEx extends AbstractStructuredContentContentEx<StructuredContentFeaturedQuoteEx.StructuredContentFeaturedQuoteDataEx> {

    public static class StructuredContentFeaturedQuoteDataEx extends AbstractStructuredContentDataEx {

        private String heading;
        private String quote;
        private String creditText;
        private String creditUrl;
        private ImageEx image = ImageEx.EMPTY;

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote(String quote) {
            this.quote = quote;
        }

        public String getCreditText() {
            return creditText;
        }

        public void setCreditText(String creditText) {
            this.creditText = creditText;
        }

        public String getCreditUrl() {
            return creditUrl;
        }

        public void setCreditUrl(String creditUrl) {
            this.creditUrl = creditUrl;
        }

        public ImageEx getImage() {
            return image;
        }

        public void setImage(ImageEx image) {
            this.image = image;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StructuredContentFeaturedQuoteEx{");
            sb.append("heading=").append(heading);
            sb.append(", quote=").append(quote);
            sb.append(", creditText=").append(creditText);
            sb.append(", creditUrl=").append(creditUrl);
            sb.append(", image=").append(image);
            sb.append('}');
            return sb.toString();
        }
    }
}
