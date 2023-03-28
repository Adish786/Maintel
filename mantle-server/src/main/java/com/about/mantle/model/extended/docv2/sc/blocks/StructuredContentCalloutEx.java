package com.about.mantle.model.extended.docv2.sc.blocks;


import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentCalloutEx extends AbstractStructuredContentContentEx<StructuredContentCalloutEx.StructuredContentCalloutDataEx> {
	public static class StructuredContentCalloutDataEx extends AbstractStructuredContentDataEx {
		private String html;
		private String heading;

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public String getHtml() {
			return html;
		}

		public void setHtml(String html) {
			this.html = html;
		}
	}
}
