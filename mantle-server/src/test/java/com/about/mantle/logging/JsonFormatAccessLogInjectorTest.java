package com.about.mantle.logging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.about.mantle.util.MantlTestUtils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import com.about.globe.core.definition.common.TaskModel;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextImpl;
import com.about.globe.core.http.RequestHeaders;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgentImpl;
import com.about.globe.core.task.processor.ModelResult;
import com.about.hippodrome.url.ExternalUrlData;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.TaxeneNodeTypeEx;
import com.about.mantle.model.extended.TaxeneRelationshipEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.FlexibleArticleDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TaxonomyDocumentEx;

public class JsonFormatAccessLogInjectorTest {
	private RequestContext requestContext;
	private HttpServletResponse response = mock(HttpServletResponse.class);
	private final JsonFormatAccessLogInjector accessLogInjector = new JsonFormatAccessLogInjector();

	@Before
	public void before() {
		MDC.clear();
	}
	
	private RequestContextImpl.Builder getRequestContextBuilder() throws Exception {
		RequestContextImpl.Builder builder = new RequestContextImpl.Builder();

		builder.setRequestId("idstampValue");
		builder.setSessionId("mintValue");
		builder.setUserId("tmogValue");
		builder.setRequestTimestamp(1234567890L);
		builder.setPageview(true);

		AuthorEx author = new AuthorEx();
		author.setId("12345");

		BaseDocumentEx document = new FlexibleArticleDocumentEx();
		document.setAuthor(author);
		document.setTemplateType(TemplateTypeEx.FLEXIBLEARTICLE);
		document.setDocumentId(56789L);
		document.setAuthor(author);
		builder.addModel("DOCUMENT",
				new ModelResult(document, 0, 0, null, new TaskModel.Builder().name("DOCUMENT").build()));

		TaxeneNodeEx taxeneNode1 = new TaxeneNodeEx();
		taxeneNode1.setDocId(56789L);
		taxeneNode1.setNodeType(TaxeneNodeTypeEx.DOCUMENT);
		taxeneNode1.setDocument(document);

		TaxeneNodeEx taxeneNode2 = new TaxeneNodeEx();
		taxeneNode2.setDocId(103L);
		taxeneNode2.setNodeType(TaxeneNodeTypeEx.TAXONOMY);
		TaxonomyDocumentEx taxonomyDocument = new TaxonomyDocumentEx();
		taxonomyDocument.setTemplateType(TemplateTypeEx.TAXONOMY);
		taxonomyDocument.setDocumentId(103L);
		taxonomyDocument.setShortHeading("taxonomy 103");
		taxeneNode2.setDocument(taxonomyDocument);
		TaxeneRelationshipEx relationship1 = new TaxeneRelationshipEx();
		relationship1.setName("primaryParent");
		relationship1.setTargetNode(taxeneNode2);
		relationship1.setWeight(1f);
		taxeneNode1.setRelationships(SliceableListEx.of(relationship1));

		TaxeneNodeEx taxeneNode3 = new TaxeneNodeEx();
		taxeneNode3.setDocId(102L);
		taxeneNode3.setNodeType(TaxeneNodeTypeEx.TAXONOMY);
		taxonomyDocument = new TaxonomyDocumentEx();
		taxonomyDocument.setTemplateType(TemplateTypeEx.TAXONOMY);
		taxonomyDocument.setDocumentId(102L);
		taxonomyDocument.setShortHeading("taxonomy 102");
		taxeneNode3.setDocument(taxonomyDocument);
		TaxeneRelationshipEx relationship2 = new TaxeneRelationshipEx();
		relationship2.setName("primaryParent");
		relationship2.setTargetNode(taxeneNode3);
		relationship2.setWeight(1f);
		taxeneNode2.setRelationships(SliceableListEx.of(relationship2));

		TaxeneNodeEx taxeneNode4 = new TaxeneNodeEx();
		taxeneNode4.setDocId(101L);
		taxeneNode4.setNodeType(TaxeneNodeTypeEx.TAXONOMY);
		taxonomyDocument = new TaxonomyDocumentEx();
		taxonomyDocument.setTemplateType(TemplateTypeEx.TAXONOMY);
		taxonomyDocument.setDocumentId(101L);
		taxonomyDocument.setShortHeading("taxonomy 101");
		taxeneNode4.setDocument(taxonomyDocument);
		TaxeneRelationshipEx relationship3 = new TaxeneRelationshipEx();
		relationship3.setName("primaryParent");
		relationship3.setTargetNode(taxeneNode4);
		relationship3.setWeight(1f);
		taxeneNode3.setRelationships(SliceableListEx.of(relationship3));

		builder.addModel("breadcrumb",
				new ModelResult(taxeneNode1, 0, 0, null, new TaskModel.Builder().name("breadcrumb").build()));

		builder.setTemplateComponent(new TemplateComponent.Builder().id("myRenderTemplate").build());

		builder.setUrlData(ExternalUrlData.builder(new ValidDomainUrlDataFactory(APPLICATION_NAME))
				.from("http://site.about.com/od/category/fl/flexiblearticle.htm?foo=bar#baz").build());
		builder.setHeaders(new RequestHeaders.Builder().setReferer("http://site.about.com/od/category")
				.setUserAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36")
				.setRemoteIp("123.234.345.456")
				.build());
		builder.setUserAgent(new UserAgentImpl(DeviceCategory.PERSONAL_COMPUTER));
		
		return builder;
	}

	@Test
	public void writeTestWithRequestContext() throws Exception {
		requestContext = getRequestContextBuilder().build();

		when(response.getStatus()).thenReturn(200);
		
		accessLogInjector.write(requestContext, response);
		
		Assert.assertEquals("\"56789\"", MDC.get("documentId"));
		Assert.assertEquals("\"FLEXIBLEARTICLE\"", MDC.get("documentType"));
		Assert.assertEquals("65", MDC.get("documentTypeId"));
		Assert.assertEquals(
				"{\"path1\":[{\"docId\":\"101\",\"text\":\"taxonomy 101\"},{\"docId\":\"102\",\"text\":\"taxonomy 102\"},{\"docId\":\"103\",\"text\":\"taxonomy 103\"}]}",
				MDC.get("taxonomy"));
		Assert.assertEquals("\"myRenderTemplate\"", MDC.get("renderTemplate"));
		Assert.assertEquals("true", MDC.get("isPageview"));
		Assert.assertEquals("\"desktop\"", MDC.get("deviceCategory"));
		Assert.assertEquals(
				"\"Mozilla\\/5.0 (X11; Linux x86_64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/41.0.2227.0 Safari\\/537.36\"",
				MDC.get("userAgent"));
		Assert.assertEquals("\"idstampValue\"", MDC.get("requestId"));
		Assert.assertEquals("\"mintValue\"", MDC.get("sessionId"));
		Assert.assertEquals("\"tmogValue\"", MDC.get("userId"));
		Assert.assertEquals("1234567890", MDC.get("requestTimestamp"));
		Assert.assertEquals(
				"{\"domain\":\"site.about.com\",\"frag\":\"baz\",\"path\":\"\\/od\\/category\\/fl\\/flexiblearticle.htm\",\"protocol\":\"http\", \"qString\":\"foo=bar\"}",
				MDC.get("requestUrlComponents"));
		Assert.assertEquals("\"http:\\/\\/site.about.com\\/od\\/category\"", MDC.get("refererUrl"));
		Assert.assertEquals(
				"{\"domain\":\"site.about.com\",\"frag\":null,\"path\":\"\\/od\\/category\",\"protocol\":\"http\", \"qString\":null}",
				MDC.get("refererUrlComponents"));
		Assert.assertEquals("\"123.234.345.456\"", MDC.get("remoteIp"));
	}
	
	private void runReferrerTest(String referrerUrl, String expectedReferrerUrl, String expectedReferrerUrlComponents) throws Exception {
		RequestContextImpl.Builder builder = getRequestContextBuilder();
		RequestHeaders headers = builder.getHeaders();
		builder.setHeaders(new RequestHeaders.Builder().setReferer(referrerUrl)
				.setUserAgent(headers.getUserAgent())
				.setRemoteIp(headers.getRemoteIp())
				.build());
		requestContext = builder.build();

		when(response.getStatus()).thenReturn(200);
		
		accessLogInjector.write(requestContext, response);

		Assert.assertEquals(expectedReferrerUrl, MDC.get("refererUrl"));
		Assert.assertEquals(expectedReferrerUrlComponents, MDC.get("refererUrlComponents"));
	}

	@Test
	public void writeTestWithRequestContext_referrerUrlParsing_noScheme() throws Exception {
		runReferrerTest("google.com",
				"\"google.com\"",
				"{\"domain\":null,\"frag\":null,\"path\":\"google.com\",\"protocol\":null, \"qString\":null}");
	}

	@Test
	public void writeTestWithRequestContext_referrerUrlParsing_androidApp() throws Exception {
		runReferrerTest("android-app://com.google.android.googlequicksearchbox",
				"\"android-app:\\/\\/com.google.android.googlequicksearchbox\"",
				"{\"domain\":\"com.google.android.googlequicksearchbox\",\"frag\":null,\"path\":\"\",\"protocol\":\"android-app\", \"qString\":null}");
	}

	@Test
	public void writeTestWithRequestContext_referrerUrlParsing_curlyBraceFailure() throws Exception {
		runReferrerTest("http://alothome.com/landing?slk=clinical+research+studies&nid=2&cid=14720161162&kwid=21357097814&akwd=clinical%20research%20studies&dmt=b&bmt=bb&dist=s&uq=discovery%20trials&device=c&ismobile=false&msclkid={msclkid}&accid=35000704&campid=62100753&agid=5668136636&vx=0#foobar",
				"\"http:\\/\\/alothome.com\\/landing?slk=clinical+research+studies&nid=2&cid=14720161162&kwid=21357097814&akwd=clinical%20research%20studies&dmt=b&bmt=bb&dist=s&uq=discovery%20trials&device=c&ismobile=false&msclkid={msclkid}&accid=35000704&campid=62100753&agid=5668136636&vx=0#foobar\"",
				"{\"domain\":null,\"frag\":null,\"path\":null,\"protocol\":null, \"qString\":null}");
	}

	@Test
	public void writeTestWithHttpServletRequest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer().append("http://site.about.com/od/category/fl/flexiblearticle.htm"));
		Mockito.when(request.getQueryString()).thenReturn("foo=bar");
		Mockito.when(request.getRemoteAddr()).thenReturn("123.234.345.456");

		accessLogInjector.write(request);

		Assert.assertEquals("false", MDC.get("isPageview"));
		Assert.assertNotNull(MDC.get("requestTimestamp"));
		Assert.assertEquals(
				"{\"domain\":\"site.about.com\",\"frag\":null,\"path\":\"\\/od\\/category\\/fl\\/flexiblearticle.htm\",\"protocol\":\"http\", \"qString\":\"foo=bar\"}",
				MDC.get("requestUrlComponents"));
		Assert.assertEquals("\"123.234.345.456\"", MDC.get("remoteIp"));
	}
}
