package com.about.mantle.model.extended.docv2.sc.blocks;

import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

public class StructuredContentCodeEx extends AbstractStructuredContentContentEx<StructuredContentCodeEx.StructuredContentCodeDataEx> {

	public static class StructuredContentCodeDataEx extends AbstractStructuredContentDataEx {

		private String code;

		public String getCode() { return code; }

		public void setCode(String code) {
			this.code = code;
		}

		@JsonIgnore
		public String[] getCodeList() {
			// replace spaces with $nbsp; character
			String spacedCode = StringUtils.replace(code, " ", "\u00A0");
			return StringUtils.splitPreserveAllTokens(spacedCode, "\n");
		}

		@Override
		public String toString() {
			return "StructuredContentCodeDataEx{" +
					"code='" + code + '\'' +
					"} " + super.toString();
		}
	}

}
