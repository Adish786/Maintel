package com.about.mantle.model.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.PageEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.tasks.TableOfContentsTask.TableOfContents;

public class TableOfContentsTaskTest {
	
	private TableOfContentsTask task;
	private RequestContext requestContext;
	private static final Long TEST_ID = 42L;

	@Before
	public void before() {
		task = new TableOfContentsTask();
		requestContext = mock(RequestContext.class);
	}

	@Test
	public void testTableOfContents_inactive() {
		assertNull(task.getTableOfContents(requestContext, getTestDoc("foo <h3>One Two</h3> baz", false)));
	}

	@Test
	public void testTableOfContents_empty() {
		TableOfContents toc = new TableOfContents();
		runTest_TableOfContents(toc, "");
		runTest_TableOfContents(toc, "foo bar");
		runTest_TableOfContents(toc, "foo <h3>bar</h3> baz");
	}

	@Test
	public void testTableOfContents_single() {
		TableOfContents toc = new TableOfContents();
		toc.addItem("One Two", "one-two");
		runTest_TableOfContents(toc, "foo <a class=\"heading-toc\" id=\"one-two\"></a><h3>One Two</h3> baz");
		runTest_TableOfContents(toc, "foo <a class=\"heading-toc\" id=\"one-two\"></a><h3 attr1=1 attr2=2>One Two</h3> baz");
		runTest_TableOfContents(toc, "foo <a class=\"heading-toc\" id=\"one-two\"></a><h3><b>One</b> <em>Two</em></h3> baz");
	}

	@Test
	public void testTableOfContents_multiple() {
		TableOfContents toc = new TableOfContents();
		toc.addItem("One", "one");
		toc.addItem("Two", "two");
		toc.addItem("Three", "three");
		runTest_TableOfContents(toc, "foo <a class=\"heading-toc\" id=\"one\"></a><h3>One</h3> bar <a class=\"heading-toc\" id=\"two\"></a><h3>Two</h3> baz <a class=\"heading-toc\" id=\"three\"></a><h3>Three</h3> bin");
	}
	
	private void runTest_TableOfContents(TableOfContents expectedTOC, String content) {
		TableOfContents actualTOC = task.getTableOfContents(requestContext, getTestDoc(content));
		assertEquals(expectedTOC, actualTOC);
	}

	@Test
	public void testInjectAnchorIds_inactive() {
		String content = "foo <h3>bar</h3> baz";
		BaseDocumentEx testDoc = getTestDoc(content, false);
		TableOfContentsTask.injectTableOfContentsAnchorIds(testDoc);
		assertEquals(content, getContent(testDoc));
	}

	@Test
	public void testInjectAnchorIds() {
		runTest_InjectAnchorIds("", "");
		runTest_InjectAnchorIds("foo bar", "foo bar");
		runTest_InjectAnchorIds("foo <b>bar</b> baz", "foo <b>bar</b> baz");
		runTest_InjectAnchorIds("foo <h4>bar</h4> baz", "foo <h4>bar</h4> baz");
		runTest_InjectAnchorIds("foo <h3n>bar</h3n> baz", "foo <h3n>bar</h3n> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"bar\"></a><h3>bar</h3> baz", "foo <h3>bar</h3> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"bar\"></a><h3>BAR</h3> baz", "foo <h3>BAR</h3> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"bar\"></a><h3 attr1=1 attr2=2>bar</h3> baz", "foo <h3 attr1=1 attr2=2>bar</h3> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"one-two-punch\"></a><h3>one-two punch</h3> baz", "foo <h3>one-two punch</h3> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"onetwo-punch\"></a><h3><small><em>one</em>/<b>two</b></small> punch</h3> baz", "foo <h3><small><em>one</em>/<b>two</b></small> punch</h3> baz");
		runTest_InjectAnchorIds("foo <a class=\"heading-toc\" id=\"one\"></a><h3>one</h3> bar <a class=\"heading-toc\" id=\"two\"></a><h3>two</h3> baz <a class=\"heading-toc\" id=\"three\"></a><h3>three</h3> bin", "foo <h3>one</h3> bar <h3>two</h3> baz <h3>three</h3> bin");
	}
	
	private void runTest_InjectAnchorIds(String expectedContent, String actualContent) {
		BaseDocumentEx testDoc = getTestDoc(actualContent);
		TableOfContentsTask.injectTableOfContentsAnchorIds(testDoc);
		assertEquals(expectedContent, getContent(testDoc));
	}
	
	private String getContent(BaseDocumentEx doc) {
		switch (doc.getTemplateType()) {
		case FLEXIBLEARTICLE:
			return ((FlexibleArticleDocumentEx) doc).getPages().getList().get(0).getContent().getList().get(0).getContent();
		default:
			return null;
		}
	}
	
	private FlexibleArticleDocumentEx getTestDoc(String content) {
		return this.getTestDoc(content, true);
	}

	private FlexibleArticleDocumentEx getTestDoc(String content, Boolean hasTableOfContents) {
		HtmlSlice htmlSlice = new HtmlSlice();
		htmlSlice.setContent(content);
		PageEx page = new PageEx();
		page.setContent(SliceableListEx.of(htmlSlice));
		FlexibleArticleDocumentEx doc = new FlexibleArticleDocumentEx();
		doc.setDocumentId(TEST_ID);
		doc.setTemplateType(TemplateTypeEx.FLEXIBLEARTICLE);
		doc.setPages(SliceableListEx.of(page));
		doc.setMetaData(new MetaDataEx());
		doc.getMetaData().setHasTableOfContents(hasTableOfContents);
		return doc;
	}
}