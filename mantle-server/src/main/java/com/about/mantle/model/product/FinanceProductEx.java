package com.about.mantle.model.product;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class FinanceProductEx extends BaseProductEx {
	
	private static final long serialVersionUID = 1L;
	
	private SliceableListEx<String> quickFacts = SliceableListEx.emptyList();

	public SliceableListEx<String> getQuickFacts() {
		return quickFacts;
	}

	public void setQuickFacts(SliceableListEx<String> quickFacts) {
		this.quickFacts = SliceableListEx.emptyIfNull(quickFacts);
	}
}