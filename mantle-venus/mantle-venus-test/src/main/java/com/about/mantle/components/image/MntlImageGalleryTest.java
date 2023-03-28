package com.about.mantle.components.image;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScBlockGalleryComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.mantle.venus.utils.Direction;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/* This test will test an image gallery component(s): 
 *  It will scroll back (left) to the previous slide in the gallery, then go forward (right) until the last
 *  image and then scroll forward one more time to see the first slide again. 
 * For scrolling purposes it will randomly use the arrows on both sides of the galleries or the carousels. 
 * For tablet and iPhone6 it will use swipe on the gallery, still using the arrows of the carousels.
 * For the galleries that contain less than 6 images in desktop and tablet and 4 images in iPhone6 it will
 * assume that the arrows on the carousels are absent and will use the arrows from the galleries or swipe.
 * It verifies at every gallery that the number of slides is correct, then it verifies at every slide:
 *  - actually displayed caption is different from a caption on a previous slide;
 *  - slide owner data is displayed correctly;
 *  - slide count does not equal the previous value and displayed correctly;
 *  - previous slide is not displayed;
 *  - the image displayed in gallery corresponds to the image displayed in carousel.
 * Added functionality to click on "Read More+" button and expand the article in mobile so the gallery becomes
 * interactible.
 */

public class MntlImageGalleryTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	protected BiConsumer<MntlRunner, Integer> imageGalleryTest = (runner, size) -> {
		List<MntlScBlockGalleryComponent> galleries = (List<MntlScBlockGalleryComponent>) runner.components();
		if (mobile(runner.driver())) {
			MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlScBlockGalleryComponent.class);
			if(mntlScPage.hasContinueReading()) {
				mntlScPage.continueReading().click();
			}
		}
		for (int i = 0; i < galleries.size(); i++) {
			MntlScBlockGalleryComponent galleryBlock = galleries.get(i);
			galleryBlock.scrollIntoView();
			collector.checkThat("slide count is incorrect in image gallery", galleryBlock.slides().size(),
					is(Integer.parseInt(galleryBlock.slideCountTotal().getText().substring("of ".length()))));
			for (int j = 0; j <= galleryBlock.slides().size() + 1; j++) {
				Random randomBoolean = new Random();
				// The following boolean will be used to define whether to use image (false) or carousel (true) controls
				boolean imageOrCarouselArrow = randomBoolean.nextBoolean();
				String slideCaptionPrevious = galleryBlock.slideCaption().getText();
				int actualSlideCount;
				int slideCountPrevious = Integer.parseInt(galleryBlock.slideCountCurrent().getText());
				if (galleryBlock.slides().size() <= size && !mobile(runner.driver())) {
					imageOrCarouselArrow = false;
				}
				if (j == 0) {
					actualSlideCount = galleryBlock.slides().size();
					if (imageOrCarouselArrow) {
						galleryBlock.carousel().leftArrow().click();
					} else  {
						if (!desktop(runner.driver())) {
							swipe(runner.driver(), galleryBlock.slides().get(0).getElement(), Direction.RIGHT);
						} else {
							galleryBlock.leftArrow().click();
						}
					}
				} else {
					if (j == 1 || j == galleryBlock.slides().size() + 1) {
						actualSlideCount = 1;
					} else {
						actualSlideCount = j;
					}
					if (imageOrCarouselArrow) {
						galleryBlock.carousel().rightArrow().click();
					} else {
						if (!desktop(runner.driver())) {
							swipe(runner.driver(),
									galleryBlock.slides()
											.get((j == 1 || j == galleryBlock.slides().size() + 1)
													? (galleryBlock.slides().size() - 1)
													: (j - 2))
											.getElement(),
											Direction.LEFT);
						} else {
							galleryBlock.rightArrow().click();
						}
					}
				}
				runner.page().waitFor().aMoment(300, TimeUnit.MILLISECONDS);
				collector.checkThat("slide caption is duplicated on " + actualSlideCount + " slide of image gallery",
						galleryBlock.slideCaption().getText(), not(slideCaptionPrevious));
				collector.checkThat(
						"slide owner is not correctly displayed on " + actualSlideCount + " slide of image gallery",
						galleryBlock.slides().get(actualSlideCount - 1).getImgDataOwner(),
						containsString(galleryBlock.slideOwner().getText()));
				collector.checkThat("slide count is not correct on " + actualSlideCount + " slide of image gallery",
						Integer.parseInt(galleryBlock.slideCountCurrent().getText()), not(slideCountPrevious));
				collector.checkThat("slide count is not correct on " + actualSlideCount + " slide of image gallery",
						Integer.parseInt(galleryBlock.slideCountCurrent().getText()), is(Integer.parseInt(
						galleryBlock.slides().get(actualSlideCount - 1).attributeValue("data-slide")) + 1));
				collector.checkThat("previous slide is displayed in image gallery",
						galleryBlock.slides().get(actualSlideCount - 1).className(), containsString("current"));
				collector.checkThat("previous image is displayed in carousel",
						galleryBlock.carousel().getItem().get(actualSlideCount - 1).getAttribute("class"), containsString("active"));
				collector.checkThat("previous image is displayed in carousel",
						Integer.parseInt(galleryBlock.carousel().getItem().get(actualSlideCount - 1).getAttribute("data-slide")), is(actualSlideCount - 1));
				int startIndex = galleryBlock.carousel().getItem().get(actualSlideCount - 1).findElement(By.tagName("img")).getAttribute("src").lastIndexOf("/");
				String carouselImgSrc = galleryBlock.carousel().getItem().get(actualSlideCount - 1).findElement(By.tagName("img")).getAttribute("src").substring(startIndex + 1);
				collector.checkThat("image in slider does not match itself in image gallery",
						galleryBlock.slides().get(actualSlideCount - 1).getImgSrc(),
						containsString(carouselImgSrc));
			}
		}
	};
	
}
