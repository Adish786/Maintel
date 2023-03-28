package com.about.mantle.render.image;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

import static com.about.mantle.util.MantlTestUtils.*;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.about.mantle.model.services.document.ElementRewriter;
import org.junit.Before;
import org.junit.Test;

import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.mantle.htmlslicing.ConfigurableHtmlSlicer;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.render.image.filter.ImageFilterBuilder;
import com.about.mantle.util.MantlTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;

public class ThumborImageRenderUtilsTest {

	private static final String THUMBOR_URL_MACRO = "thumborUrl.ftl";
	private static final String THUMBOR_IMG_MACRO = "thumborImg.ftl";

	private static final String TEMPLATE_DIR = "src/test/resources/";
	private static final String MACROS_DIR = "/macros";

	private MantleRenderUtils renderUtils;
	private PlatformUrlDataFactory urlDataFactory;
	private PollexorImageResizer resizer;
	private List<ElementRewriter> elementRewriters;

	private Configuration cfg;
	private Template template;

	private Map<String, Object> rootMap;

	@Before
	public void setUp() throws Exception {
		String host = "http://nyprthumbor1.ops.about.com:8891/";
		String key = "vaQuachecvognec7";

		resizer = new PollexorImageResizer(host, key);
		urlDataFactory = new ValidDomainUrlDataFactory(APPLICATION_NAME);
		renderUtils = spy(new MantleRenderUtils("about.com", new ThumborImageRenderUtils("0.tqn.com", 0.7, resizer),
				urlDataFactory, null, null,  new ConfigurableHtmlSlicer(new ObjectMapper()), null, MantlTestUtils.generateMockPropertyFactory(false, false, false, false), null, null, false, false, elementRewriters));


		cfg = new Configuration(Configuration.VERSION_2_3_24);
		cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_DIR + getClass().getSimpleName() + MACROS_DIR));

		rootMap = new HashMap<>();
		rootMap.put("utils", renderUtils);

		addStaticModel(rootMap, "imageFilter", ImageFilterBuilder.class);
	}

	@Test
	public void testThumborUrlMacro() throws Exception {
		final String thumborUrlMacro = "thumborUrl.ftl";
		Pattern pattern = Pattern.compile(
				"http:\\/\\/nyprthumbor1\\.ops\\.about\\.com:8891\\/(.*?)=\\/400x500\\/filters:no_upscale\\(\\):grayscale\\(\\):brightness\\(30\\)\\/56aa06f73df78cf772ac127e");
		// @formatter:off
		ImageMacro model = new ImageMacro.Builder()
				.url("http://0.tqn.com/d/weightloss/1/0/2/W/foodiesfeed.com_making-healthy-smoothie.jpg")
				.img("56aa06f73df78cf772ac127e").height(500).width(400)
				.filters(new String[] { ImageFilterBuilder.noUpscale(), ImageFilterBuilder.grayscale(),
						ImageFilterBuilder.brightness(30) })
				.build();
		// @formatter:on
		testMacroFile(pattern, thumborUrlMacro, model);
	}

	@Test
	public void testThumborUrlMacroDefaultFilter() throws Exception {
		final String thumborUrlMacro = "thumborUrl.ftl";
		Pattern pattern = Pattern.compile(
				"http:\\/\\/nyprthumbor1\\.ops\\.about\\.com:8891\\/(.*?)=\\/500x800\\/filters:no_upscale\\(\\)\\/GettyImages_135385237-56a689d33df78cf7728edf36\\.jpg");
		// @formatter:off
		ImageMacro model = new ImageMacro.Builder()
				.url("http://0.tqn.com/d/multiples/1/0/z/l/GettyImages_135385237.jpg")
				.img("GettyImages_135385237-56a689d33df78cf7728edf36.jpg").height(800).width(500).build();
		// @formatter:on
		testMacroFile(pattern, thumborUrlMacro, model);
	}

	@Test
	public void testThumborImgMacro() throws Exception {
		Pattern pattern = Pattern.compile(
				"<img\nsrc=\"http:\\/\\/nyprthumbor1\\.ops\\.about\\.com:8891\\/(.*?)=\\/700x500\\/filters:no_upscale\\(\\):rgb\\(50,40,30\\)\\/463246871-56a190415f9b58b7d0c0b0b5\\.jpg\"\n\\/>");
		// @formatter:off
		ImageMacro model = new ImageMacro.Builder().url("http://0.tqn.com/d/collegesavings/1/0/a/-/-/-/463246871.jpg")
				.img("463246871-56a190415f9b58b7d0c0b0b5.jpg").height(500).width(700)
				.filters(new String[] { ImageFilterBuilder.noUpscale(), ImageFilterBuilder.rgb(50, 40, 30) }).build();
		// @formatter:on
		testMacroFile(pattern, THUMBOR_IMG_MACRO, model);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testThumborUrlMacroInvalidBrightnessFilter() throws Exception {
		// @formatter:off
		ImageMacro model = new ImageMacro.Builder()
				.url("http://0.tqn.com/d/weightloss/1/0/2/W/foodiesfeed.com_making-healthy-smoothie.jpg")
				.img("56aa06f73df78cf772ac127e").height(500).width(400)
				.filters(new String[] { ImageFilterBuilder.noUpscale(), ImageFilterBuilder.brightness(120) }).build();
		// @formatter:on
		testMacroFile(null, THUMBOR_URL_MACRO, model);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThumborUrlMacroInvalidColorizeFilter() throws Exception {
		// @formatter:off
		ImageMacro model = new ImageMacro.Builder().url("http://0.tqn.com/f/ga/1/mrb-claudia-chaves-large.jpg")
				.img("1234").height(500).width(400).filters(new String[] { ImageFilterBuilder.noUpscale(),
						ImageFilterBuilder.colorize(40, 30, 70, "$2B4B5C") })
				.build();
		// @formatter:on
		testMacroFile(null, THUMBOR_URL_MACRO, model);
	}

	private void testMacroFile(Pattern expectedPattern, String macro, ImageMacro model) throws Exception {
		template = cfg.getTemplate(macro);
		rootMap.put("model", model);

		StringWriter out = new StringWriter();
		template.process(rootMap, out);
		if (expectedPattern != null) {
			assertThat(expectedPattern.matcher(out.toString().trim()).matches(), is(true));
		} else {
			assertThat(out.toString().trim(), is(expectedPattern));
		}

	}

	@SuppressWarnings("deprecation")
	private void addStaticModel(Map<String, Object> rootMap, String name, Class<?> clazz) throws Exception {
		TemplateHashModel staticModel = (TemplateHashModel) BeansWrapper.getDefaultInstance().getStaticModels()
				.get(clazz.getName());
		rootMap.put(name, staticModel);
	}
}
