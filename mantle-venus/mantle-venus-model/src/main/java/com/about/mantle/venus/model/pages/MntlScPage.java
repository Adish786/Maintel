package com.about.mantle.venus.model.pages;

import com.about.mantle.components.frames.MntlCheetahembedIframe;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.blocks.MntlDigiohEmbedComponent;
import com.about.mantle.venus.model.components.blocks.MntlMultipleSponsorshipComponent;
import com.about.mantle.venus.model.components.blocks.MntlSponsorshipComponent;
import com.about.mantle.venus.model.components.media.MntlLightBoxComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class MntlScPage<T extends MntlComponent> extends MntlBasePage{

	private final Lazy<WebElementEx> continueReading;
	private final Lazy<MntlLightBoxComponent> mntlLightBoxcomponent;
	private final Lazy<List<MntlScBlockComponent>> mntlScBlockComponents;
	private final Lazy<List<MntlCheetahembedIframe>> mntlCheetahembedIframe;
	private final Lazy<List<MntlDigiohEmbedComponent>> mntlDigiohEmbedComponent;
	private final Lazy<MntlSponsorshipComponent> mntlSponsorshipComponent;
	private final Lazy<MntlMultipleSponsorshipComponent> mntlMultipleSponsorshipComponent;
	private final Lazy<List<WebElementEx>> mntlScBlockHeading;
	private final Lazy<List<WebElementEx>> mntlScBlockHeadingText;

	@SuppressWarnings("unchecked")
	public MntlScPage(WebDriverExtended driver, Class<T> componentClass) {
		super(driver,componentClass);
		this.continueReading = lazy(() -> findElement(By.cssSelector("button.btn-chop")));
		this.mntlLightBoxcomponent = lazy(() -> findElement(By.className("mntl-lightbox"), MntlLightBoxComponent::new));
		this.mntlScBlockComponents = lazy(() -> findComponents(MntlScBlockComponent.class));
		this.mntlCheetahembedIframe = lazy(() -> findComponents(MntlCheetahembedIframe.class));
		this.mntlDigiohEmbedComponent = lazy(() -> findComponents(MntlDigiohEmbedComponent.class));
		this.mntlSponsorshipComponent = lazy(() -> findComponent(MntlSponsorshipComponent.class));
		this.mntlMultipleSponsorshipComponent = lazy(() -> findComponent(MntlMultipleSponsorshipComponent.class));
		this.mntlScBlockHeading = lazy(() -> findElements(By.className("mntl-sc-block-heading")));
		this.mntlScBlockHeadingText = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-heading__text")));
	}

	public boolean hasContinueReading() {
		return this.getElement().elementExists(".btn-chop");
	}

	public WebElementEx continueReading() {
		return continueReading.get();
	}

	public List<WebElementEx> mntlScBlockHeadingText() {
		return mntlScBlockHeadingText.get();
	}

	public List<WebElementEx> mntlScBlockHeading() {
		return mntlScBlockHeading.get();
	}

	public void clickContinueReading() {
		if(hasContinueReading()) {
			continueReading().scrollIntoViewCentered();
			continueReading().jsClick();
			waitFor().aMoment();
		}
	}

	public MntlLightBoxComponent mntlLightBoxcomponent() {
		return mntlLightBoxcomponent.get();
	}

	public List<MntlScBlockComponent> mntlScBlockComponents() {
		return mntlScBlockComponents.get();
	}

	public List<MntlScBlockComponent> mntlScBlocksWithIframes() {
		return mntlScBlockComponents().stream().filter(sc->sc.className().contains("mntl-sc-block-iframe")).collect(Collectors.toList());
	}

	public List<MntlCheetahembedIframe> mntlCheetahembedIframe() {
		return mntlCheetahembedIframe.get();
	}

	public MntlSponsorshipComponent mntlSponsorshipComponent() {
		return mntlSponsorshipComponent.get();
	}

	public MntlMultipleSponsorshipComponent mntlMultipleSponsorshipComponent() {
		return mntlMultipleSponsorshipComponent.get();
	}

	public List<MntlDigiohEmbedComponent> mntlDigiohEmbedComponent() {
		return mntlDigiohEmbedComponent.get();
	}
}
