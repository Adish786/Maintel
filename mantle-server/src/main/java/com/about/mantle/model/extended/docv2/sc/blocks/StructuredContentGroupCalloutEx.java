package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import org.joda.time.DateTime;

public class StructuredContentGroupCalloutEx extends AbstractStructuredContentContentEx<StructuredContentGroupCalloutEx.StructuredContentGroupCalloutDataEx>{

	public static class StructuredContentGroupCalloutDataEx extends AbstractStructuredContentDataEx {

		private SliceableListEx<Callout> callOuts;
		private String heading;

		private String subheading;

		public SliceableListEx<Callout> getCallOuts() {
			return callOuts;
		}

		public void setCallOuts(SliceableListEx<Callout> callOuts) {
			this.callOuts = callOuts;
		}

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public String getSubheading() {
			return subheading;
		}

		public void setSubheading(String subheading) {
			this.subheading = subheading;
		}

		@Override
		public String toString() {
			return "StructuredContentGroupCalloutDataEx{" +
					"callOuts=" + callOuts +
					"heading=" + heading +
					"subheading=" + subheading +
					"} " + super.toString();
		}
	}

	public static class Callout {

		private String caption;
		private ImageEx image;
		private String title;
		private String url;
		private DateTime date;
		private String buttonText;

		public String getCaption() {
			return caption;
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		public ImageEx getImage() {
			return image;
		}
		public void setImage(ImageEx image) {
			this.image = image;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public DateTime getDate() {
			return date;
		}
		public void setDate(DateTime date) {
			this.date = date;
		}
		public String getButtonText() {
			return buttonText;
		}

		public void setButtonText(String buttonText) {
			this.buttonText = buttonText;
		}

		@Override
		public String toString() {
			return "Callout [caption=" + caption
					+ ", image=" + image
					+ ", title=" + title
					+ ", url=" + url
					+ ", date=" + date
					+ ", buttonText=" + buttonText + "]";
		}
	}

}
