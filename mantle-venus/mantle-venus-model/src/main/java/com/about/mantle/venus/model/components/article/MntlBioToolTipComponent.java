package com.about.mantle.venus.model.components.article;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-bio-tooltip")
public class MntlBioToolTipComponent extends MntlComponent{

	 private final Lazy<MntlToolTipTopComponent> tooltipTop;
	 private final Lazy<MntlToolTipBottomComponent> tooltipBottom;

	public MntlBioToolTipComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.tooltipTop = lazy(() -> findElement(By.className("mntl-bio-tooltip__top"), MntlToolTipTopComponent::new));
		this.tooltipBottom = lazy(() -> findElement(By.className("mntl-bio-tooltip__bottom"), MntlToolTipBottomComponent::new));
	}
	
	
	public MntlToolTipTopComponent tooltipTop() {
		return tooltipTop.get();
	}
	
	public MntlToolTipBottomComponent tooltipBottom() {
		return tooltipBottom.get();
	}
	
	
	public class MntlToolTipTopComponent extends Component {
		
		
		 private final Lazy<WebElementEx> toolTipBio;
		 private final Lazy<WebElementEx> toolTipImg;
		 private final Lazy<WebElementEx> toolTipJobTitle;
		 private final Lazy<WebElementEx> toolTipLink;
		 private final Lazy<WebElementEx> toolTipImageWrapper;
		 private final Lazy<List<BioToolTipSocialFollowListComponent>> socialFollowList;
		 
		public MntlToolTipTopComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			
			this.toolTipBio = lazy(() -> findElement(By.className("mntl-bio-tooltip__bio")));
			this.toolTipImageWrapper = lazy(() -> findElement(By.className("mntl-bio-tooltip__image-wrapper")));
			this.toolTipImg = lazy(() -> findElement(By.tagName("img")));
			this.toolTipJobTitle = lazy(() -> findElement(By.className("mntl-bio-tooltip__job-title-text")));
			this.toolTipLink = lazy(() -> findElement(By.className("mntl-bio-tooltip__link")));
			this.socialFollowList = lazy(() -> findElements(By.cssSelector(".social-nav__list a"), BioToolTipSocialFollowListComponent::new));
		}
		
		public WebElementEx toolTipBio() {
			return toolTipBio.get();
		}
		
		public WebElementEx toolTipImageWrapper() {
			return toolTipImageWrapper.get();
		}
		
		public WebElementEx toolTipImg() {
			return toolTipImg.get();
		}
		
		public WebElementEx toolTipJobTitle() {
			return toolTipJobTitle.get();
		}
		
		public WebElementEx toolTipLink() {
			return toolTipLink.get();
		}

		public List<BioToolTipSocialFollowListComponent> socialFollowList() {
			return socialFollowList.get();
		}

	}

	public class MntlToolTipBottomComponent extends Component {

		private final Lazy<WebElementEx> toolTipLearnMore;
		private final Lazy<WebElementEx> toolTipReviewedDate;
		private final Lazy<WebElementEx> factCheckBadge;
		private final Lazy<List<BioToolTipSocialFollowListComponent>> socialFollowList;
		
		public MntlToolTipBottomComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.toolTipLearnMore = lazy(() -> findElement(By.cssSelector(".mntl-bio-tooltip__learn-more a")));
			this.toolTipReviewedDate = lazy(() -> findElement(By.cssSelector(".mntl-bio-tooltip__reviewed-date")));
			this.factCheckBadge = lazy(() -> findElement(By.cssSelector(".fact-check-badge")));
			this.socialFollowList = lazy(() -> findElements(By.cssSelector(".bio-tooltip__social-follow-list a"), BioToolTipSocialFollowListComponent::new));
		}
		
		public WebElementEx toolTipLearnMore() {
			return toolTipLearnMore.get();
		}
		
		public WebElementEx toolTipReviewedDate() {
			return toolTipReviewedDate.get();
		}
		
		public WebElementEx factCheckBadge() {
			return factCheckBadge.get();
		}
		
		
		public List<BioToolTipSocialFollowListComponent> socialFollowList() {
			return socialFollowList.get();
		}

	}
	
	
	public class BioToolTipSocialFollowListComponent extends Component {
		
		private final Lazy<WebElementEx> icon;

		public BioToolTipSocialFollowListComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.icon = lazy(() -> findElement(By.cssSelector("svg")));
		}
		
		public WebElementEx icon() {
			return icon.get();
		}
		
	}

}
