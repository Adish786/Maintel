package com.about.mantle.venus.model.components;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.interactions.Mouse;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-jwplayer")
public class MntlJWVideoPlayerComponent extends MntlComponent {

	private final Supplier<WebElementEx> bigPlayButton;
	private final Lazy<WebElementEx> volumeLevel;
	private final Supplier<WebElementEx> mobilePauseButton;
	private final Supplier<WebElementEx> mobilePlayButton;
	private final Supplier<VideoControlBarComponent> controlbar;

	public MntlJWVideoPlayerComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.bigPlayButton = () -> findElement(By.cssSelector(".jw-icon-display.jw-icon"));
		this.mobilePauseButton = () -> findElement(By.cssSelector("div[aria-label='Pause']"));
		this.mobilePlayButton = () -> findElement(By.cssSelector("div[aria-label='Play']"));
		this.volumeLevel = lazy(() -> findElement(By.cssSelector(".jw-slider-volume .jw-progress")));
		this.controlbar = () -> findElement(By.cssSelector(".jw-controlbar"), VideoControlBarComponent::new);
	}

	public WebElementEx bigPlayButton() {
		return findElement(By.cssSelector(".jw-icon-display.jw-icon"));
	}
	
	public WebElementEx mobilePauseButton() {
		return mobilePauseButton.get();
	}
	
	public WebElementEx mobilePlayButton() {
		return mobilePlayButton.get();
	}

	public int volumeLevel() {
		String style = volumeLevel.get().getAttribute("style");
		Pattern pattern = Pattern.compile("height: (.*)%;");
		Matcher matcher = pattern.matcher(style);
		if (matcher.find()) {
			style = matcher.group(1);
		}
		return Integer.valueOf(style);
	}

	/**
	 * Video started, note: either ads or the video is playing
	 * @return
	 */
	public boolean started() {
		this.waitFor().aMoment(2, TimeUnit.SECONDS);
		return this.getElement().getAttribute("class").contains("jw-state-playing");
	}

	public boolean paused() {
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
		return this.getElement().getAttribute("class").contains("jw-state-paused");
	}

	public VideoControlBarComponent controlBar() {
		return controlbar.get();
	}

	public boolean videoBuffering() {
		return this.getElement().getAttribute("class").contains("jw-state-buffering");
	}

	public void bringIntoViewByPercentFromBottom(int percent, MntlBasePage<? extends MntlComponent> page) {
		bringIntoViewByPercent(percent, page, false);
	}

	private void bringIntoViewByPercent(int percent, MntlBasePage<? extends MntlComponent> page, boolean fromTop) {
		String alignToTop = "true";
		int heightToScroll = videoPlayerSize().height * (100 - percent) / 100;
		if (fromTop) {
			alignToTop = "false";
			heightToScroll = -heightToScroll;
		}
		this.scrollIntoView(alignToTop);
		page.scroll().scrollBy(0, heightToScroll);
		page.waitFor().aMoment();
	}

	public Dimension videoPlayerSize() {
		return this.getElement().getSize();
	}
	
	public boolean soundOn() {
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
		return this.findElements(By.cssSelector(".jw-icon-volume.jw-off")).size() == 0;
	}
	
	public boolean soundOff() {
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
		return this.findElements(By.cssSelector(".jw-icon-volume.jw-off")).size() > 0;
	}

	/**
	 * The video is playing (not ads)
	 * @return
	 */
	public boolean videoPlaying() {
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
		String classes = this.getElement().getAttribute("class");
		return classes.contains("jw-state-playing") & !classes.contains("jw-flag-ads");
	}
	
	/**
	 * The video ad is playing (not actual video)
	 * @return
	 */
	public boolean adPlaying() {
		return this.getElement().getAttribute("class").contains("jw-flag-ads");
	}

	/**
	 * Toggles between play and pause
	 */
	public void toggleVideoPlay() {
		Locatable hoverItem = (Locatable) getElement().getWrappedElement();
		Mouse mouse = ((HasInputDevices) getDriver().returnDriver()).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());
		controlbar.get().playControl().sendKeys(Keys.ENTER);
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
	}
	
	/**
	 * Toggles between CC on/off
	 */
	
	public void toggleVideoCaption() {
		controlbar.get().videoCaption().click();
		this.waitFor().aMoment(1, TimeUnit.SECONDS);
	}
	
	public void toggleSound() {
		Locatable hoverItem = (Locatable) getElement().getWrappedElement();
		Mouse mouse = ((HasInputDevices) getDriver().returnDriver()).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());
		controlbar.get().volumeIcon().sendKeys(Keys.ENTER);
	}

	public void toggleVideoSize() {
		Locatable hoverItem = (Locatable) getElement().getWrappedElement();
		Mouse mouse = ((HasInputDevices) getDriver().returnDriver()).getMouse();
		mouse.mouseMove(hoverItem.getCoordinates());
		waitFor().exactMoment(1, TimeUnit.SECONDS);
		mouse.click(hoverItem.getCoordinates()); // need this click for mobile control bar to show up
		controlbar.get().fullscreenControl().sendKeys(Keys.ENTER);
		waitFor().exactMoment(1, TimeUnit.SECONDS);
	}
	
	public double getAspectRatio() {
		Dimension size = findElement(By.cssSelector(".jw-wrapper")).getSize();
		int height = size.height;
		int width = size.width;
		double ratio = (double)width/height;
		return ratio;
	}


	public static class VideoControlBarComponent extends Component {

		private final Supplier<WebElementEx> playControl;
		private final Lazy<WebElementEx> timeControl;
		private final Lazy<WebElementEx> duration;
		private final Lazy<WebElementEx> fullscreenControl;
		private final Lazy<WebElement> slider;
		private final Lazy<WebElementEx> knob;
		private final Lazy<WebElementEx> volumeIcon;

		public VideoControlBarComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.playControl = () -> findElement(By.cssSelector(".jw-icon-playback"));
			this.timeControl = lazy(() -> findElement(By.cssSelector(".jw-text-elapsed")));
			this.duration = lazy(() -> findElement(By.cssSelector(".jw-text-duration")));
			this.fullscreenControl = lazy(() -> findElement(By.xpath("//div[@class='jw-icon jw-icon-inline jw-button-color jw-reset jw-icon-fullscreen']")));
			this.slider = lazy(() -> findElement(By.cssSelector(".jw-slider-time .jw-slider-container")));
			this.knob = lazy(() -> findElement(By.cssSelector(".jw-slider-time .jw-knob")));
			this.volumeIcon = lazy(() -> findElement(By.cssSelector(".jw-icon-volume")));
		}

		public void expose() {
			getDriver().exposeElement(getElement().webElement());
		}

		public WebElementEx playControl() {
			return playControl.get();
		}
		

		public WebElementEx timeControl() {
			return timeControl.get();
		}

		public WebElementEx duration() {
			return duration.get();
		}

		public WebElementEx fullscreenControl() {
			return fullscreenControl.get();
		}

		public WebElementEx knob() {
			return knob.get();
		}
		
		public WebElementEx videoCaption() {
			return findElement(By.cssSelector(".jw-icon.cc-icon"));
		}

		public void dragAndDropSlider(WebDriverExtended driver, boolean inlineVideo) {
			WebElement slider;
			//if particular page has inline video, set inlineVideo flag to true else false
			if (inlineVideo) {
				slider = driver.findElement(By.cssSelector(".mntl-sc-block-inlinevideo .jw-slider-time .jw-slider-container"));
			} else {
				slider = driver.findElement(By.cssSelector(".video-footer .jw-slider-time .jw-slider-container"));
			}

			Dimension sliderSize = slider.getSize();
			int sliderWidth = sliderSize.getWidth();
			int sliderHeight = slider.getLocation().getX();

			Actions builder = new Actions(driver.returnDriver());
			builder.dragAndDropBy(slider, sliderHeight + sliderWidth, 0).build().perform();
			Pattern pattern = Pattern.compile("-?\\d+");
			Matcher matcher = pattern.matcher(knob().getAttribute("style"));
			int sliderValue = 0;
			if (matcher.find()) {
				sliderValue = Integer.parseInt(matcher.group(0));
			}

			if (sliderValue < 99) {

				Assert.fail("Video is still playing");
			}

		}
		
		public WebElementEx volumeIcon() {
			return volumeIcon.get();
		}
		
	}

}