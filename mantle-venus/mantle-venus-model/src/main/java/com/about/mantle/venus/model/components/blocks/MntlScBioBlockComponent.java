package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-bio")
public class MntlScBioBlockComponent extends MntlComponent {
	private final Lazy<WebElementEx> excerpt;
	private final Lazy<WebElementEx> image;
	private final Lazy<WebElementEx> jobTitle;
	private final Lazy<WebElementEx> link;
	private final Lazy<WebElementEx> meta;
	private final Lazy<WebElementEx> name;
	private final Lazy<WebElementEx> personalDetailContent;
	private final Lazy<WebElementEx> personalDetailHeader;

	public MntlScBioBlockComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.excerpt = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__excerpt")));
		this.image = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__image")));
		this.jobTitle = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__title")));
		this.link = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__link")));
		this.meta = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__meta")));
		this.name = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-bio__name")));
		this.personalDetailContent = lazy(() -> findElement(By.cssSelector((".mntl-sc-block-bio__detail--content"))));
		this.personalDetailHeader = lazy(() -> findElement(By.cssSelector((".mntl-sc-block-bio__detail--header"))));
	}

	public WebElementEx excerpt() {
		return excerpt.get();
	}

	public WebElementEx image() {
		return image.get();
	}

	public WebElementEx jobTitle() {
		return jobTitle.get();
	}

	public WebElementEx link() {
		return link.get();
	}

	public WebElementEx meta() {
		return meta.get();
	}

	public WebElementEx name() {
		return name.get();
	}

	public WebElementEx personalDetailContent() {
		return personalDetailContent.get();
	}

	public WebElementEx personalDetailHeader() {
		return personalDetailHeader.get();
	}
}