package com.about.mantle.components.media;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MntlJWVideoPlayerComponent;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.media.MntlFigureComponent;
import com.about.mantle.venus.model.components.media.MntlVideosTitlesComponent;
import com.about.mantle.venus.model.components.media.MntlRightRailVideoComponent;
import com.about.mantle.venus.model.components.media.MntlScBlockInlineVideoComponent;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.utils.DataLayerUtils;
import com.about.venus.core.utils.Scroll.Speed;
import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

public abstract class MntlVideoTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	protected static List<String> eventActions = Arrays.asList("Player Load", "Video Load", "Media external",
			"Percent played", "First 10 Seconds Played", "Percent played");
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<MntlScBlockComponent> getBlocksWithInlineVideo(MntlScPage mntlScPage) {
		mntlScPage.clickContinueReading();
		List<MntlScBlockComponent> mntlListScItemComponents = mntlScPage.getComponents();
		List<MntlScBlockComponent> mntlListScItemComponentsWithVideo = mntlListScItemComponents.stream().
				filter(ScB->ScB.className().contains("mntl-sc-block-inlinevideo")).collect(Collectors.toList());
		return mntlListScItemComponentsWithVideo;
	}
	
	public void testVideoPlaying(WebDriverExtended driver, MntlJWVideoPlayerComponent jwPlayer, String position) {
		if (driver.getCurrentUrl().contains("videoPlayOnScroll")) {
			collector.checkThat("video is playing after scrolling player out of view from " + position,
					jwPlayer.started(), is(false));
		} else {
			collector.checkThat("video is not playing after scrolling player out of view from " + position,
					jwPlayer.started(), is(true));
		}
	}
	
	protected void videoPlayerEventTest(MntlJWVideoPlayerComponent jwPlayer, Runner runner, int numberOfEventsToSkip) {
		MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlScBlockComponent.class);
		WebDriverExtended driver = runner.driver();
		String eventCategory = "JWPlayer Inline";
		runner.page().scroll().scrollInViewPort(jwPlayer, 35, Speed.SLOW);
		driver.waitFor(2, TimeUnit.SECONDS);
		driver.waitFor((Predicate<WebDriver>) wd -> jwPlayer.started(), 10);
		driver.waitFor(40, TimeUnit.SECONDS);
		final List<Map<String, Object>> videoEventObjects = DataLayerUtils.dataLayerEvents(driver, "videoEvent");

		for (Map<String, Object> videoEventObject : videoEventObjects) {
			collector.checkThat("Video Event is not correct ", videoEventObject.get("event"), is("videoEvent"));
			collector.checkThat("Video Event Category is not correct ", videoEventObject.get("eventCategory"),
					is(eventCategory));
			collector.checkThat("Video Event label is not correct ", (String) videoEventObject.get("eventLabel"),
					not(isEmptyOrNullString()));
		}
		boolean pointerUpdated = false;
		for (int i = 0; i < eventActions.size() + numberOfEventsToSkip; i++) {
			if(i==2 && numberOfEventsToSkip > 0) { 
				i += numberOfEventsToSkip;
				pointerUpdated = true;
			}
			collector.checkThat("event action is not correct for " + videoEventObjects.get(i).get("eventAction"),
					videoEventObjects.get(i).get("eventAction"), is(eventActions.get(pointerUpdated ? i - numberOfEventsToSkip : i)));
		}
		if (desktop(driver)) {
			if (getBlocksWithInlineVideo(mntlScPage).size() > 0) {
				jwPlayer.controlBar().dragAndDropSlider(driver, true);
			} else {
				jwPlayer.controlBar().dragAndDropSlider(driver, false);
			}
			driver.waitFor(2, TimeUnit.SECONDS);
			final List<Map<String, Object>> videoEventObjectsMediaComplete = DataLayerUtils.dataLayerEvents(driver,
					"videoEvent");
			if (videoEventObjectsMediaComplete.iterator().next().containsValue("Media Complete")) {
				collector.checkThat("media complete is not displayed",
						videoEventObjectsMediaComplete.iterator().next().get("eventAction"), is("Media Complete"));
			}
		}
	}

	protected BiConsumer<Runner, MntlJWVideoPlayerComponent> videoCommonTest = (runner,videoPlayer) -> {
		collector.checkThat("Video Player not displayed",videoPlayer.displayed(),is(true));
		collector.checkThat("Video sound is ON",videoPlayer.volumeLevel(),is(0));
		videoPlayer.toggleVideoPlay();
		runner.driver().waitFor((Predicate<WebDriver>) webdriver -> videoPlayer.paused(), 30);
		collector.checkThat("Video is playing",videoPlayer.paused(),is(true));
		videoPlayer.bigPlayButton().jsClick();
		videoPlayer.toggleSound();
		collector.checkThat("Video sound is OFF",videoPlayer.soundOn(),is(true));
	};
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Consumer<Runner> inlineBlockVideoTest = runner -> {
		MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlScBlockComponent.class);
		MntlScBlockComponent scBlockWithVideo = getBlocksWithInlineVideo(mntlScPage).get(0);
		MntlScBlockInlineVideoComponent inlineVideo = scBlockWithVideo.inlineVideoComponent();
		inlineVideo.scrollIntoViewCentered();
		collector.checkThat("inline video title is not displayed", scBlockWithVideo.inlineVideoTitle().isDisplayed(), is(true));
		collector.checkThat("inline video is not displayed", inlineVideo.displayed(), is(true));
		collector.checkThat("inline video is playing by default", inlineVideo.videoPlaying(), is(false));
		inlineVideo.bigPlayButton().sendKeys(Keys.ENTER);
		collector.checkThat("inline video is not playing after clicking on play", inlineVideo.videoPlaying(), is(true));
		inlineVideo.toggleVideoPlay();
		inlineVideo.waitFor().aMoment();
		collector.checkThat("inline video is playing after clicking on pause", inlineVideo.paused(), is(true));
		inlineVideo.toggleVideoPlay();
		inlineVideo.waitFor().aMoment();
		collector.checkThat("Video sound is off", inlineVideo.soundOn(), is(true));
		if (desktop(runner.driver())) { //sound button is not exist on mobile and tablet chromeEmulator
		inlineVideo.toggleSound();
		collector.checkThat("Video sound is on when it should be off", inlineVideo.soundOff(),is(true));
		inlineVideo.toggleSound();
		collector.checkThat("Video sound is off after turning it on", inlineVideo.soundOn(),is(true));
		}

		inlineVideo.scrollIntoViewCentered();
		inlineVideo.scrollIntoView("true");
		mntlScPage.scroll().scrollBy(0, inlineVideo.height());
		mntlScPage.waitFor().exactMoment(3, TimeUnit.SECONDS);
		testVideoPlaying(runner.driver(), inlineVideo, "bottom");
		inlineVideo.scrollIntoView("false");
		mntlScPage.scroll().scrollBy(0, -inlineVideo.height());
		mntlScPage.waitFor().exactMoment(3, TimeUnit.SECONDS);
		testVideoPlaying(runner.driver(), inlineVideo, "top");
		int initialSize = inlineVideo.videoPlayerSize().height * inlineVideo.videoPlayerSize().width;
		inlineVideo.toggleVideoSize();
		int fullSize = inlineVideo.videoPlayerSize().height * inlineVideo.videoPlayerSize().width;
		collector.checkThat("Video not maximized",initialSize, lessThan(fullSize));
	};
	
	@SuppressWarnings("rawtypes")
	protected Consumer<Runner> inlineVideoClickTrackingTest = (runner) -> {
		MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlScBlockComponent.class);
		List<MntlScBlockComponent> scBlocksWithVideo = getBlocksWithInlineVideo(mntlScPage);
		int numberOfEventsToSkip = scBlocksWithVideo.size()*2 - 2;
		MntlScBlockInlineVideoComponent inlineVideo = scBlocksWithVideo.get(0).inlineVideoComponent();
		inlineVideo.bigPlayButton().sendKeys(Keys.ENTER);
		videoPlayerEventTest(inlineVideo, runner, numberOfEventsToSkip);
	};
	
	protected Consumer<Runner> videoTest = runner -> {
		MntlFigureComponent figure = (MntlFigureComponent) runner.component();
		MntlJWVideoPlayerComponent videoPlayer = figure.videoPlayer();
		videoPlayer.click();
		// the ad that comes with kw=prerolltest runs for 15 seconds
		videoPlayer.waitFor().aMoment(18, TimeUnit.SECONDS);
		videoCommonTest.accept(runner, videoPlayer);
	};
	

	protected Consumer<Runner> videoTestPC = runner -> {
		MntlFigureComponent figure = (MntlFigureComponent) runner.component();
		MntlJWVideoPlayerComponent videoPlayer = figure.videoPlayer();
		// the ad that comes with kw=prerolltest runs for 15 seconds
		videoPlayer.waitFor().aMoment(18, TimeUnit.SECONDS);
		collector.checkThat("Video is not playing on article", videoPlayer.started(), is(true));
		videoCommonTest.accept(runner, videoPlayer);
		int initialSize = videoPlayer.videoPlayerSize().height *videoPlayer.videoPlayerSize().width;
		videoPlayer.toggleVideoSize();
		int fullSize = videoPlayer.videoPlayerSize().height * videoPlayer.videoPlayerSize().width;
		collector.checkThat("Video not maximized",initialSize, lessThan(fullSize));
	};

	protected Consumer<Runner> rrVideoPCTest = runner -> {
		MntlRightRailVideoComponent comp = (MntlRightRailVideoComponent) runner.component();
		Actions actions = new Actions(runner.driver());
		comp.loadRRVideo(comp.rrVideoTitle());
		collector.checkThat("video text not showing",comp.videoText().getText().isEmpty(),is(false));
		collector.checkThat("video title not showing",comp.rrVideoTitle().getText().isEmpty(),is(false));
		actions.moveToElement(comp.rrVideoControlBar()).perform();
		if(runner.page().componentExists(By.cssSelector(".cc-icon"))){
			comp.ccButton().mouseClick();
			comp.waitFor().aMoment(3,TimeUnit.SECONDS);
			collector.checkThat("cc doesn't get activated",comp.ccButton().getAttribute("class"),containsString("active"));
		}
		collector.checkThat("video doesn't autoplay",comp.playerTracker().getAttribute("class").contains("playing"),is(true));
		comp.controlBar().playControl().jsClick();
		collector.checkThat("video doesn't pause",comp.playerTracker().getAttribute("class").contains("paused"),is(true));
		comp.controlBar().playControl().jsClick();
		collector.checkThat("video doesn't play",comp.playerTracker().getAttribute("class").contains("playing"),is(true));
		comp.controlBar().volumeIcon().jsClick();
		collector.checkThat("video stayed muted",comp.controlBar().volumeIcon().getAttribute("aria-label").contains("Mute button"),is(true));
		comp.controlBar().volumeIcon().jsClick();
		collector.checkThat("video couldn't get set to mute",comp.controlBar().volumeIcon().getAttribute("aria-label").contains("Mute button"),is(false));
		comp.fullScreen().jsClick();
		collector.checkThat("video didn't enter fullscreen mode",comp.fullScreen().getAttribute("aria-label").equals("Exit Fullscreen"),is(true));
		comp.fullScreen().jsClick();
		collector.checkThat("video didn't exit fullscreen mode",comp.fullScreen().getAttribute("aria-label").equals("Fullscreen"),is(true));
		String firstTitle = comp.rrVideoTitle().text();
		comp.nextButton().jsClick();
		collector.checkThat("title didn't change with new video",comp.rrVideoTitle().getText().equals(firstTitle),is(false));
		comp.waitFor().aMoment(10,TimeUnit.SECONDS);
	};

	protected Consumer<Runner> rrVideoPlayListTestPC = runner -> {
		MntlRightRailVideoComponent component = (MntlRightRailVideoComponent) runner.component();
		component.scrollIntoViewCentered();
		collector.checkThat(component.shelfComponent().moreVideos().getText().equals(MntlRightRailVideoComponent.ShelfComponent.MORE_VIDEOS),is(TRUE));
		collector.checkThat("two toggles are not showed",component.shelfComponent().toggles().size() == 2, is(TRUE));
		String videoTitle = component.shelfComponent().videoTitle().getText();
		component.shelfComponent().rightArrow().jsClick();
		component.waitFor().aMoment();
		collector.checkThat(component.shelfComponent().videoTitle().getText().equals(videoTitle), is(FALSE));
		component.shelfComponent().lefArrow().jsClick();
		component.waitFor().aMoment();
		collector.checkThat(component.shelfComponent().videoTitle().getText().equals(videoTitle), is(TRUE));
		component.shelfComponent().rightArrow().jsClick();
		component.shelfComponent().toggles().get(1).jsClick();
		collector.checkThat(component.rrVideoTitle().getText().contains(component.shelfComponent().videoTitle().getText()), is(TRUE));
	};

	protected static void knobDrag(MntlRightRailVideoComponent component, Runner runner){
		component.controlBar().playControl().jsClick();
		Actions actions = new Actions(runner.driver().returnDriver());
		Dimension horizontalSliderSize = component.horizontalSlider().getSize();
		int horizontalSizeWidth = horizontalSliderSize.getWidth();
		actions.dragAndDropBy(component.knob(), horizontalSizeWidth, 0).build().perform();
	}

	protected Consumer<Runner> videoContentsTest = runner -> {
		MntlVideosTitlesComponent component = (MntlVideosTitlesComponent) runner.component();
		component.loadRRVideo(component.rrVideoTitle());
		component.inlineVideoTitles().forEach(inlineTitles -> {collector.checkThat("same content is played by more than one player",inlineTitles.text().contains(component.rrVideoTitle().text()),is(false));
		});
	};
}