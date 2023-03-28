package com.about.mantle.render.image;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.ImageEx;

public class MantleImageRenderUtilsTest {
	private MantleImageRenderUtilsImpl renderUtils;

	private RequestContext requestContext;

	@Before
	public void before() throws IOException {
		requestContext = mock(RequestContext.class);
		renderUtils = new MantleImageRenderUtilsImpl("0.tqn.com", 0.7);
	}

	@Test
	public void testResizeImage() {

		ImageEx image = new ImageEx();
		image.setUrl("http://0.tqn.com/d/neurology/1/G/2/-/-/-/Brain.jpg");
		image.setCaption("existing image caption");
		image.setAlt("existing image alt text");
		image.setOwner("existing image owner");
		image.setWidth(1280);
		image.setHeight(1024);

		ImageEx result = renderUtils.resizeImage(image, ImageType.FULL_SIZE);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/0/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(1280));
		Assert.assertEquals(result.getHeight(), new Integer(1024));

		result = renderUtils.resizeImage(image, ImageType.DOCUMENTBODY_MED);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/W/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(385));
		Assert.assertEquals(result.getHeight(), new Integer(308));

		result = renderUtils.resizeImage(image, ImageType.CIRC_3COL);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/O/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(85));
		Assert.assertEquals(result.getHeight(), new Integer(56));
	}

	@Test
	public void existingImage() {

		ImageEx image = new ImageEx();
		image.setUrl("http://0.tqn.com/d/neurology/1/G/2/-/-/-/Brain.jpg");
		image.setCaption("existing image caption");
		image.setAlt("existing image alt text");
		image.setOwner("existing image owner");
		image.setWidth(1280);
		image.setHeight(1024);

		ImageEx result = renderUtils.getImage(image, ImageType.CIRC_3COL, null, false, requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/O/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(85));
		Assert.assertEquals(result.getHeight(), new Integer(56));

		result = renderUtils.getImage(image, ImageType.CIRC_3COL, null, false,
				requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/O/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(85));
		Assert.assertEquals(result.getHeight(), new Integer(56));

		result = renderUtils.getImage(image, ImageType.CIRC_3COL, null, false,
				requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/O/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(85));
		Assert.assertEquals(result.getHeight(), new Integer(56));

		image.setUrl("http://0.tqn.com/d/neurology/1/G/2/-/-/-/Brain.jpg");
		image.setCaption("existing image caption");
		image.setAlt("existing image alt text");
		image.setOwner("existing image owner");
		image.setWidth(1280);
		image.setHeight(1024);

		result = renderUtils.getImage(image, ImageType.CIRC_4COL, null, false, requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/P/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(130));
		Assert.assertEquals(result.getHeight(), new Integer(86));

		image.setUrl("http://0.tqn.com/d/neurology/1/G/2/-/-/-/Brain.jpg");
		image.setCaption("existing image caption");
		image.setAlt("existing image alt text");
		image.setOwner("existing image owner");
		image.setWidth(1280);
		image.setHeight(1024);

		result = renderUtils.getImage(image, ImageType.CIRC_4COL, null, false, requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/neurology/1/P/2/-/-/-/Brain.jpg");
		Assert.assertEquals(result.getOwner(), "existing image owner");
		Assert.assertEquals(result.getWidth(), new Integer(130));
		Assert.assertEquals(result.getHeight(), new Integer(86));

		image.setUrl("http://0.tqn.com/d/default/homegarden/0/185938855_215x130.jpg");
		image.setOwner("Dejan Patic/The Image Bank/Getty Images");
		image.setWidth(215);
		image.setHeight(130);

		result = renderUtils.getImage(image, ImageType.CIRC_GRID, null, false, requestContext, null, null);
		Assert.assertEquals("http://0.tqn.com/d/default/homegarden/Q/185938855_215x130.jpg", result.getUrl());
		Assert.assertEquals(image.getWidth(), result.getWidth());
		Assert.assertEquals(image.getHeight(), result.getHeight());

		result = renderUtils.getImage(image, ImageType.CIRC_4COL, null, true, requestContext, null, null);
		Assert.assertEquals(result.getUrl(), "http://0.tqn.com/d/default/homegarden/P/185938855_215x130.jpg");
		Assert.assertEquals(result.getOwner(), "Dejan Patic/The Image Bank/Getty Images");
		Assert.assertEquals(result.getWidth(), new Integer(130));
		Assert.assertEquals(result.getHeight(), new Integer(86));
	}
}
