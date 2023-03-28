package com.about.mantle.model.extended.docv2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;

public class DocumentExTest {
	
	@Test
	public void testBestTitle() throws Exception {
		BaseDocumentEx docExtend = new FlexibleArticleDocumentEx();
		docExtend.setUrl("http://www.about.com");
		assertNull(docExtend.getBestTitle());
		
		docExtend = new FlexibleArticleDocumentEx();
		docExtend.setSocialTitle("Social Title");
		docExtend.setUrl("http://www.about.com");
		assertNull(docExtend.getBestTitle());

		docExtend = new JwPlayerVideoDocumentEx();
		docExtend.setTitle("Document Title");
		docExtend.setUrl("http://www.dotdashmeredith.com");
		assertEquals("Document Title", docExtend.getBestTitle());
		
		docExtend = new CuratedDocumentEx();
		docExtend.setSocialTitle("Social Title");
		docExtend.setUrl("http://www.about.com");
		((CuratedDocumentEx)docExtend).setProgrammingTitle("Programming Title");
		assertEquals("Programming Title", docExtend.getBestTitle());

		docExtend = new StructuredContentDocumentEx();
		docExtend.setHeading("Document Title");
		docExtend.setUrl("http://www.dotdashmeredith.com");
		assertEquals("Document Title", docExtend.getBestTitle());

		docExtend = new RedirectDocumentEx();
		docExtend.setUrl("http://www.dotdashmeredith.com/abc-xyz-12345");
		docExtend.setSlug("abc-xyz");
		assertEquals("abc xyz", docExtend.getBestTitle());
	}
	
}
