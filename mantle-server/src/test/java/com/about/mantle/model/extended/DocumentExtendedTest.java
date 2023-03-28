package com.about.mantle.model.extended;

import org.junit.Assert;
import org.junit.Test;

import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.PageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class DocumentExtendedTest {

	@Test
	public void testDocumentTitle() throws Exception {
		FlexibleArticleDocumentEx documentEx = new FlexibleArticleDocumentEx();
		documentEx.setSocialTitle("some social & title");
		documentEx.setTitle("document title");
		documentEx.setHeading("document heading");
		documentEx.setPages(SliceableListEx.of(new PageEx()));
		documentEx.getPages().getList().get(0).setHeading("some heading &amp; ");
		documentEx.getPages().getList().get(0).setTitle("some meta &amp; title");

		Assert.assertNotEquals("some social & title", documentEx.getBestTitle());
		Assert.assertNotEquals("document title", documentEx.getBestTitle());
		Assert.assertEquals("some heading &amp; ", documentEx.getBestTitle());
	}

}
