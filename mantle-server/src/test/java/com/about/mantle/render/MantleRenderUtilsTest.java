package com.about.mantle.render;

import static com.about.mantle.util.MantlTestUtils.APPLICATION_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.about.mantle.model.services.CuratedDomainService;
import com.about.mantle.model.services.document.ElementRewriter;
import com.about.mantle.model.services.impl.CuratedDomainServiceImpl;
import com.about.globe.core.task.processor.ModelResult;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestHeaders;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.about.mantle.htmlslicing.ConfigurableHtmlSlicer;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.render.image.MantleImageRenderUtils;
import com.about.mantle.render.image.MantleImageRenderUtilsImpl;
import com.about.mantle.util.MantlTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.netflix.archaius.api.PropertyFactory;

public class MantleRenderUtilsTest {

	private static final String REVGROUP_SPONSOREDLIST_URI = "www.revgroup.com";
	private static final String CAES_SPONSOREDLIST_URI = "www.caes.com";
	private static final String NON_SPONSORED_URI = "www.nonSponsored.com";

	private MantleRenderUtils renderUtils;
	private MantleImageRenderUtils imageRenderUtils;
	private UrlDataFactory urlDataFactory;
	private CuratedDomainService curatedDomainService;
	private RequestContext requestContext;
	private ProjectInfo projectInfo;
	private List<ElementRewriter> elementRewriters;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void before() throws IOException {
		requestContext = mock(RequestContext.class);
		urlDataFactory = new ValidDomainUrlDataFactory(APPLICATION_NAME);
		projectInfo = new ProjectInfo("title", "vendor", "version", DateTime.now());

		curatedDomainService = mock(CuratedDomainServiceImpl.class);
		Set<String> safelist = Sets.newHashSet("www.amazon.com", "www.barnesandnoble.com", "www1.macys.com",
				"testdomain.com", "testdomain2.com", "testdomain3.com", "testdomain4.com", "testdomain5.com",
				"about.com", "www.microsoft.com", "verywellhealth.com");

		when(curatedDomainService.getDomainsBySource(CuratedDomainService.SEO_SAFELIST, null)).thenReturn(safelist);

		Set<String> revgroupSponsoredlist = Sets.newHashSet(REVGROUP_SPONSOREDLIST_URI);
		Set<String> caesSponsoredlist = Sets.newHashSet(CAES_SPONSOREDLIST_URI);
		when(curatedDomainService.getDomainsBySource(CuratedDomainService.REVGROUP_SPONSOREDLIST, null)).thenReturn(revgroupSponsoredlist);
		when(curatedDomainService.getDomainsBySource(CuratedDomainService.CAES_SPONSOREDLIST, null)).thenReturn(caesSponsoredlist);

		imageRenderUtils = new MantleImageRenderUtilsImpl("0.tqn.com", 0.7);

		renderUtils = new MantleRenderUtils("verywellhealth.com", imageRenderUtils, urlDataFactory,  projectInfo, null, new ConfigurableHtmlSlicer(new ObjectMapper()), null, MantlTestUtils.generateMockPropertyFactory(true, true, false, false), null, curatedDomainService, false, false, elementRewriters);
	}

	@Test
	public void testRewriteUrl() throws MalformedURLException, UnsupportedEncodingException {
		renderUtils = new MantleRenderUtils("verywellhealth.com", imageRenderUtils, urlDataFactory,  projectInfo, null, new ConfigurableHtmlSlicer(new ObjectMapper()), null, MantlTestUtils.generateMockPropertyFactory(true, false, false, false), null, curatedDomainService, false, false, elementRewriters);
		String url;
		String rewrittenUrl;

		when(requestContext.getUrlData()).thenReturn(urlDataFactory
				.create("https://www.health-local.qa.aws.verywellhealth.com:8443/foobar-1337"));
		when(requestContext.getServerName()).thenReturn("www.health-local.qa.aws.verywellhealth.com");
		when(requestContext.getServerPort()).thenReturn(8443);
		when(requestContext.getHeaders()).thenReturn(new RequestHeaders.Builder().setRemoteSecure(true).build());

		url = "https://www.verywellhealth.com/foobar-1337";
		rewrittenUrl = renderUtils.rewriteContentUrl(url, requestContext);
		assertThat(rewrittenUrl, is("https://www.health-local.qa.aws.verywellhealth.com:8443/foobar-1337"));

		when(requestContext.getUrlData()).thenReturn(urlDataFactory
				.create("https://www.health-foobar.qa.aws.verywellhealth.com:1337/doc-123"));
		when(requestContext.getServerName()).thenReturn("www.health-foobar.qa.aws.verywellhealth.com");
		when(requestContext.getServerPort()).thenReturn(1337);

		url = "https://www.verywellhealth.com/doc-123";
		rewrittenUrl = renderUtils.rewriteContentUrl(url, requestContext);
		assertThat(rewrittenUrl, is("https://www.health-foobar.qa.aws.verywellhealth.com:1337/doc-123"));
	}

	@Test
	public void testIsNoFollow() {
		String url = "http://www.about.com/testUrl/moreTest";
		assertThat(renderUtils.isNoFollow(requestContext, url), is(false));

		String url2 = "//www.about.com/testUrl";
		assertThat(renderUtils.isNoFollow(requestContext, url2), is(false));

		String url3 = "/testUrl/";
		assertThat(renderUtils.isNoFollow(requestContext, url3), is(false));

		String url4 = "http://www.about.com";
		assertThat(renderUtils.isNoFollow(requestContext, url4), is(false));

		String url5 = "www.about.com/test";
		assertThat(renderUtils.isNoFollow(requestContext, url5), is(false));

		String url6 = "www.affiliate.com/test";
		assertThat(renderUtils.isNoFollow(requestContext, url6), is(true));

		String url7 = "http://www.affiliate.com/test";
		assertThat(renderUtils.isNoFollow(requestContext, url7), is(true));

		String url8 = "//www.affiliate.com/test";
		assertThat(renderUtils.isNoFollow(requestContext, url8), is(true));

		String url9 = "www.about.com/test//doubleSlashPath";
		assertThat(renderUtils.isNoFollow(requestContext, url9), is(false));

		String url10 = "/test//doubleSlashPath";
		assertThat(renderUtils.isNoFollow(requestContext, url10), is(false));

		String url11 = "www.about.com";
		assertThat(renderUtils.isNoFollow(requestContext, url11), is(false));

		String url12 = "http://www.microsoft.com";
		assertThat(renderUtils.isNoFollow(requestContext, url12), is(false));

		String url13 = "http://kindle.amazon.com";
		assertThat(renderUtils.isNoFollow(requestContext, url13), is(true));

		String url14 = "http://amazon.com";
		assertThat(renderUtils.isNoFollow(requestContext, url14), is(true));
	}

	@Test
	public void testRewriteContentUrl_HTTPS() {
		renderUtils = new MantleRenderUtils("verywellhealth.com", imageRenderUtils, urlDataFactory, projectInfo, null, new ConfigurableHtmlSlicer(new ObjectMapper()), null, MantlTestUtils.generateMockPropertyFactory(true, false, false, false), null, curatedDomainService, false, false, elementRewriters);

		
		String href = "http://www.verywellhealth.com/";

		when(requestContext.getHeaders()).thenReturn(new RequestHeaders.Builder().setRemoteSecure(true).build());
		when(requestContext.getUrlData()).thenReturn(urlDataFactory.create("https://www.health-qa5.qa.aws.verywellhealth.com/doc-123"));

		assertThat(renderUtils.rewriteContentUrl(href, requestContext), is("http://www.verywellhealth.com/"));

		href = "https://www.verywellhealth.com/";
		assertThat(renderUtils.rewriteContentUrl(href, requestContext), is("https://www.health-qa5.qa.aws.verywellhealth.com/"));

		href = "https://random.domain.com/";
		assertThat(renderUtils.rewriteContentUrl(href, requestContext), is("https://random.domain.com/"));
	}

	@Test
	public void testGetStaticPath() {
		String result = renderUtils.getStaticPath();
		assertThat(result, is("/static/version"));
	}

	@Test
	public void testChopContent() {
		// We have following cases here.
		// 1. For valid HTML content - We get two chopped blocks from htmlSlicer
		// 2. For valid HTML content - We get only one block back from htmlSlicer (e.g. no chopping's done if content is
		// of 200 chars and we asked htmlSlicer to chop on 500 chars)
		// 3. For plain text - We get two chopped blocks from htmlSlicer
		// 4. For plain text - We get only one block from htmlSlicer (no chopping just like case #2)

		String endTags = "</li></ol></p>";
		String firstPart = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam efficitur nisi in turpis pretium hendrerit. "
				+ "Donec massa odio, luctus vitae elit in, eleifend efficitur mauris. Morbi ac neque lobortis, interdum metus sed, "
				+ "tincidunt enim. Aliquam viverra dolor sit amet dui ultricies pharetra. Nunc risus purus, tincidunt nec maximus vitae, "
				+ "fermentum et leo. Praesent sed arcu id urna ullamcorper placerat. "
				+ "Maecenas maximus condimentum ultricies. Nullam ante tortor, maximus non metus quis, molestie ultricies purus. "
				+ "Praesent vel rutrum arcu. Aliquam eget ultrices orci. item text</p><p><ol><li>Aliquam nisi mauris, molestie sed lobortis id";

		String secondPart = "Phasellus ultricies arcu libero,"
				+ " id cursus urna efficitur at. In imperdiet, urna at sagittis semper, augue lacus vulputate augue, "
				+ "sed convallis nisl leo ut odio.";

		String content = firstPart + endTags + secondPart;
		// This is only chop content (and not inline chop)
		List<HtmlSlice> slices = renderUtils.chopContent(content, "chars:600;tags:p,ul,ol;");
		assertEquals(slices.size(), 2);
		// case #2
		slices = renderUtils.chopContent(content, "chars:1200;tags:p,ul,ol;");
		assertEquals(slices.size(), 1);

		// case #1
		content = firstPart + endTags + secondPart;
		slices = renderUtils.chopContentInline(content, "chars:600;tags:p,ul,ol;");
		assertEquals(slices.size(), 3);
		assertEquals(slices.get(0).getContent(), firstPart);
		assertEquals(slices.get(1).getContent(), endTags);

		// case #3
		content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, "
				+ "when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, "
				+ "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, "
				+ "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

		slices = renderUtils.chopContentInline(content, "chars:200;tags:p,ul,ol;");
		assertEquals(slices.size(), 2);

		// case #4
		slices = renderUtils.chopContentInline(content, "chars:500;tags:p,ul,ol;");
		assertEquals(slices.size(), 1);

		// for malformed html, expect the exception from HtmlSlicer
		content = "<p" + secondPart;
		thrown.expect(Exception.class);
		renderUtils.chopContentInline(content, "chars:80;tags:p,ul,ol;");
	}

	@Test
	public void testGetTag() {
		String html = "<div><ul><li>some content</li></ul></div>";
		assertEquals("div", renderUtils.getTag(html));

		html = "Content without a tag";
		assertEquals(null, renderUtils.getTag(html));

		html = "<image id='someSelfClosingTag'/>";
		assertEquals(null, renderUtils.getTag(html));

		assertEquals(null, renderUtils.getTag(""));
	}

	@Test
	public void testStripOuterTag() {
		String html = "<div><ul><li>some content</li></ul></div>";
		assertEquals("<ul><li>some content</li></ul>", renderUtils.stripOuterTag(html));

		html = "Content without a tag";
		assertEquals(html, renderUtils.stripOuterTag(html));

		html = "<image id='someSelfClosingTag'/>";
		assertEquals(html, renderUtils.stripOuterTag(html));

		assertEquals("", renderUtils.stripOuterTag(""));
	}

	@Test
	public void testSuppressEndComment() {

		boolean shouldSuppressEndComment;
		boolean shouldAddSubdomains;
		boolean isCaesEnabled;
		PropertyFactory propertyFactory;
		
		shouldSuppressEndComment = false;
		shouldAddSubdomains = true;
		isCaesEnabled = false;


		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService, shouldAddSubdomains, shouldAddSubdomains, elementRewriters);
		assertEquals(shouldSuppressEndComment, renderUtils.suppressEndComments(requestContext));
		
		shouldSuppressEndComment = true;
		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService, shouldAddSubdomains, shouldAddSubdomains, elementRewriters);

		assertEquals(shouldSuppressEndComment, renderUtils.suppressEndComments(requestContext));
		
		when(requestContext.getParameterSingle("forceEndComments")).thenReturn("true");
		assertEquals(false, renderUtils.suppressEndComments(requestContext));
		
		when(requestContext.getParameterSingle("forceEndComments")).thenReturn("false");
		assertEquals(true, renderUtils.suppressEndComments(requestContext));
	}

	@Test
	public void testIsSponsored() {

		boolean shouldSuppressEndComment = false;
		boolean shouldAddSubdomains = false;
		boolean isCaesEnabled;
		PropertyFactory propertyFactory;

		BaseDocumentEx document = new StructuredContentDocumentEx();
		document.setRevenueGroup("COMMERCE");
		ModelResult modelResult = new ModelResult(document, 0, 0, null, null);
		Map<String, ModelResult> requestContextModels = Map.of(
				"DOCUMENT", modelResult
		);
		when(requestContext.getModels()).thenReturn(requestContextModels);
		when(requestContext.getModel("DOCUMENT")).thenReturn(modelResult);

		isCaesEnabled = false;
		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService, false, false, elementRewriters);
		assertTrue(renderUtils.isSponsored(requestContext, REVGROUP_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isSponsored(requestContext, CAES_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isSponsored(requestContext, NON_SPONSORED_URI));

		isCaesEnabled = true;
		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService,false, false, elementRewriters);
		assertTrue(renderUtils.isSponsored(requestContext, REVGROUP_SPONSOREDLIST_URI));
		assertTrue(renderUtils.isSponsored(requestContext, CAES_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isSponsored(requestContext, NON_SPONSORED_URI));
	}

	@Test
	public void testIsCaes() {

		boolean shouldSuppressEndComment = false;
		boolean shouldAddSubdomains = false;
		boolean isCaesEnabled;
		PropertyFactory propertyFactory;

		BaseDocumentEx document = new StructuredContentDocumentEx();
		document.setRevenueGroup("COMMERCE");
		ModelResult modelResult = new ModelResult(document, 0, 0, null, null);
		Map<String, ModelResult> requestContextModels = Map.of(
				"DOCUMENT", modelResult
		);
		when(requestContext.getModels()).thenReturn(requestContextModels);
		when(requestContext.getModel("DOCUMENT")).thenReturn(modelResult);

		isCaesEnabled = false;
		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService,false, false, elementRewriters);
		assertFalse(renderUtils.isCaes(requestContext, REVGROUP_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isCaes(requestContext, CAES_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isCaes(requestContext, NON_SPONSORED_URI));

		isCaesEnabled = true;
		propertyFactory = MantlTestUtils.generateMockPropertyFactory(shouldSuppressEndComment, shouldAddSubdomains, isCaesEnabled, false);
		renderUtils = new MantleRenderUtils("about.com", imageRenderUtils, urlDataFactory, projectInfo, null, null, null, propertyFactory, null, curatedDomainService, false, false, elementRewriters);
		assertFalse(renderUtils.isCaes(requestContext, REVGROUP_SPONSOREDLIST_URI));
		assertTrue(renderUtils.isCaes(requestContext, CAES_SPONSOREDLIST_URI));
		assertFalse(renderUtils.isCaes(requestContext, NON_SPONSORED_URI));
	}
	
    @Test
    public void testConvertToReadableTime() {
        // 10 hours two minutes
        String answer = renderUtils.convertToReadableTime(36120000l);
        assertEquals(answer, "10 hours 2 minutes");

        // 10 hours two minutes 2 seconds
        answer = renderUtils.convertToReadableTime(36122000l);
        assertEquals(answer, "10 hours 2 minutes");

        // Check for pluralization for 10 hours 1 minute
        answer = renderUtils.convertToReadableTime(36100000l);
        assertEquals(answer, "10 hours 1 minute");

        // Check for pluralization for 1 hour 1 minute
        answer = renderUtils.convertToReadableTime(3660000l);
        assertEquals(answer, "1 hour 1 minute");

        // Check for pluralization for 1 hour 2 minute
        answer = renderUtils.convertToReadableTime(3720000l);
        assertEquals(answer, "1 hour 2 minutes");

        // Should skip hour for 0 hour 1 minute
        answer = renderUtils.convertToReadableTime(60000l);
        assertEquals(answer, "1 minute");

        // Should skip hour for 0 hour 1 minute
        answer = renderUtils.convertToReadableTime(61000l);
        assertEquals(answer, "1 minute");

        // Should skip hour for 0 hour 2 minutes
        answer = renderUtils.convertToReadableTime(120000l);
        assertEquals(answer, "2 minutes");

        // Should skip minute for 1 hour 0 minutes
        answer = renderUtils.convertToReadableTime(3600000l);
        assertEquals(answer, "1 hour");

        // Should skip minute for 2 hours 0 minutes
        answer = renderUtils.convertToReadableTime(7200000l);
        assertEquals(answer, "2 hours");

        // Should be blank for 0 seconds
        answer = renderUtils.convertToReadableTime(0l);
        assertEquals(answer, "");

        // Should be blank for 50 seconds
        answer = renderUtils.convertToReadableTime(50000l);
        assertEquals(answer, "");

        // Should be blank for -ve values
        answer = renderUtils.convertToReadableTime(-1000l);
        assertEquals(answer, "");

        // Should be blank for -ve values
        answer = renderUtils.convertToReadableTime(-3600000l);
        assertEquals(answer, "");
    }
	
    

}
