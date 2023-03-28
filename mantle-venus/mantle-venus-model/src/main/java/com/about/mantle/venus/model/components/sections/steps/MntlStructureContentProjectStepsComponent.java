package com.about.mantle.venus.model.components.sections.steps;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;


@MntlComponentId("mntl-sc-project-steps")
public class MntlStructureContentProjectStepsComponent extends MntlComponent{
	
	private final Lazy<List<MntlStepsListComponent>> stepsList;
	private final Lazy<List<WebElementEx>> stepsImagesList; 
	private final Lazy<List<WebElementEx>> stepsHeadingList;
 
	public MntlStructureContentProjectStepsComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.stepsList = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-html"), MntlStepsListComponent::new));
		this.stepsImagesList = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-image")));
		this.stepsHeadingList = lazy(() -> findElements(By.className("mntl-sc-block-heading__text")));
	}
	
	public List<MntlStepsListComponent> stepsList(){
		return stepsList.get();
	}
	
	public List<WebElementEx> stepsImagesList(){
		return stepsImagesList.get();
	}
	
	public List<WebElementEx> stepsHeadingList(){
		return stepsHeadingList.get();
	}

	public class MntlStepsListComponent extends Component {

		private final Lazy<List<WebElementEx>> stepsContent;
		private final Lazy<List<WebElementEx>> stepsHeader;

		public MntlStepsListComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.stepsContent = lazy(() -> findElements(By.tagName("p")));
			this.stepsHeader = lazy(() -> findElements(By.tagName("strong")));
		}

		public List<WebElementEx> stepsContent() {
			return stepsContent.get();
		}
		public List<WebElementEx> stepsHeader() {
			return stepsHeader.get();
		}

	}
}
