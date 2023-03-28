package com.about.mantle.venus.model.components;

import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.components.blocks.MntlScBlockIframe;
import com.about.mantle.venus.model.components.media.MntlScBlockInlineVideoComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block")
public class MntlScBlockComponent extends MntlComponent{

	private final Lazy<WebElementEx> inlineVideoTitle;
	private final Supplier<WebElementEx> playButtonIcon;
	private final Supplier<MntlScBlockInlineVideoComponent> inlineVideoComponent;
	private final Lazy<WebElementEx> iFrameCaption;
	private final Lazy<MntlScBlockIframe> iFrame;

	public MntlScBlockComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.inlineVideoTitle = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-inlinevideo__title")));
		this.inlineVideoComponent = () -> findComponent(MntlScBlockInlineVideoComponent.class);
		this.iFrameCaption = lazy(() -> findElement(By.className("mntl-sc-block-iframe__caption")));
		this.iFrame = lazy(() -> findComponent(MntlScBlockIframe.class));
		this.playButtonIcon = () -> findElement(By.cssSelector(".icon-play-btn"));
	}

	public WebElementEx inlineVideoTitle() {
		return inlineVideoTitle.get();
	}

	public MntlScBlockInlineVideoComponent inlineVideoComponent() {
		return inlineVideoComponent.get();
	}

	public WebElementEx iFrameCaption() {
		return iFrameCaption.get();
	}

	public MntlScBlockIframe iFrame() {
		return iFrame.get();
	}

	public WebElementEx playButtonIcon() {
		return playButtonIcon.get();
	}

}