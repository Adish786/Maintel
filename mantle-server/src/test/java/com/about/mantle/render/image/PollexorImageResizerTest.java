package com.about.mantle.render.image;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.render.image.filter.ImageFilterBuilder;

public class PollexorImageResizerTest {

	private PollexorImageResizer resizer;
	private RequestContext requestContext;

	@Before
	public void before() {

		requestContext = mock(RequestContext.class);

		// String host = "http://nypeimgsrv1.ops.about.com:8891/";
		String host = "http://nyprthumbor1.ops.about.com:8891/";
		String key = "vaQuachecvognec7";
		// String key = null;

		resizer = new PollexorImageResizer(host, key);
	}

	@Test
	public void testResize() throws Exception {

		ImageEx image;
		ImageType type;
		String url;

		image = new ImageEx();
		image.setAlt("Alt");
		image.setCaption("Caption");
		image.setFileName("FileName");
		image.setWidth(300);
		image.setHeight(300);
		image.setId("12345");
		image.setObjectId("1-562e78f8fd81fd61f0001456.jpg");
		image.setOwner("Owner");
		image.setUrl("http://f.tqn.com/y/humor/1/T/v/g/0/-/pacman.jpg");

		type = ImageType.DOCUMENT_LARGE;

		url = null;

		ImageEx resized = resizer.resize(image, type, null, requestContext, null,
				new String[] { ImageFilterBuilder.noUpscale() });
		Pattern pattern = Pattern.compile("http:\\/\\/nyprthumbor1\\.ops\\.about\\.com:8891\\/(.*?)=\\/640x0\\/filters:no_upscale\\(\\)\\/1-562e78f8fd81fd61f0001456\\.jpg");
		assertThat(pattern.matcher(resized.getUrl()).matches(),is(true));
	}

	@Test
	public void testFilter() throws Exception {
		ImageEx image = new ImageEx();
		image.setWidth(450);
		image.setHeight(450);
		image.setId("12345");
		image.setObjectId("1-562e78f8fd81fd61f0001456.jpg");
		image.setUrl("http://f.tqn.com/y/humor/1/T/v/g/0/-/pacman.jpg");

		ImageType type = ImageType.DOCUMENT_LARGE;
		ImageEx resized = resizer.resize(image, type, null, requestContext, null,
				new String[] { ImageFilterBuilder.noUpscale(), ImageFilterBuilder.grayscale() });
		Pattern pattern = Pattern.compile("http:\\/\\/nyprthumbor1\\.ops\\.about\\.com:8891\\/(.*?)=\\/640x0\\/filters:no_upscale\\(\\):grayscale\\(\\)\\/1-562e78f8fd81fd61f0001456\\.jpg");
		assertThat(pattern.matcher(resized.getUrl()).matches(),is(true));

	}
}
