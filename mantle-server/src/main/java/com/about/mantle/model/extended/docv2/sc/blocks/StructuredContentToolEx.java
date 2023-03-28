package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;

public class StructuredContentToolEx extends AbstractStructuredContentContentEx<StructuredContentToolEx.StructuredContentToolDataEx> {
	public static class StructuredContentToolDataEx extends AbstractStructuredContentDataEx {
		private String toolId;
	
		public String getToolId() {
			return toolId;
		}
	
		public void setToolId(String toolId) {
			this.toolId = toolId;
		}
	}
}