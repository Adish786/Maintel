package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentDefinitionEx extends AbstractStructuredContentContentEx<StructuredContentDefinitionEx.StructuredContentDefinitionDataEx> {
	
	public static class StructuredContentDefinitionDataEx extends AbstractStructuredContentDataEx {
		private String content;
		private String heading;

		public String getHeading() {
			return heading;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}
}
