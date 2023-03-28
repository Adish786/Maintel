package com.about.mantle.venus.model.components;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-external-component")
public class MntlExternalComponent extends MntlComponent {

	private final Lazy<List<SvgTag>> svgElements;
	private final Lazy<WebElementEx> parentElement;

	public MntlExternalComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.svgElements = lazy(() -> findElements(By.tagName("svg"), SvgTag::new));
		this.parentElement = lazy(() -> findElement(By.xpath("..")));
	}

	public List<SvgTag> svgElements() {
		return svgElements.get();
	}

	public WebElementEx parentElement() {
		return parentElement.get();
	}

	public static class SvgTag extends Component {

		private final Lazy<WebElementEx> useTag;

		public SvgTag(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.useTag = lazy(() -> findElement(By.tagName("use")));
		}

		public String svgId() {
			return useTag.get().getAttribute("xlink:href");
		}

	}
}
