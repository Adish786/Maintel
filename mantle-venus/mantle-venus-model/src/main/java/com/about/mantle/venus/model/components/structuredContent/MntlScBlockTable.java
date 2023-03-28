package com.about.mantle.venus.model.components.structuredContent;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-table")
public class MntlScBlockTable extends MntlComponent {
	private final Lazy<Section> thead;
	private final Lazy<Section> tbody;
	private final Lazy<WebElementEx> title;
	private final Lazy<WebElementEx> caption;

	public MntlScBlockTable(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.thead = lazy(() -> findElement(By.cssSelector("thead"), Section::new));
		this.tbody = lazy(() -> findElement(By.cssSelector("tbody"), Section::new));
		this.title = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-table__title")));
		this.caption = lazy(() -> findElement(By.cssSelector("figcaption")));
	}

	public WebElementEx title() {
		return title.get();
	}

	public WebElementEx caption() {
		return caption.get();
	}

	public Section thead() {
		return this.thead.get();
	}

	public Section tbody() {
		return this.tbody.get();
	}

	public static class Section extends MntlComponent {
		private final Lazy<List<Row>> rows;

		public Section(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.rows = lazy(() -> findElements(By.cssSelector("tr"), Row::new));
		}

		public List<Row> rows() {
			return this.rows.get();
		}
	}

	public static class Row extends MntlComponent {
		private final Lazy<List<WebElementEx>> columns;
		private final Lazy<List<WebElementEx>> headings;

		public Row(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.columns = lazy(() -> findElements(By.cssSelector("td")));
			this.headings = lazy(() -> findElements(By.cssSelector("th")));
		}

		public List<WebElementEx> columns() {
			return this.columns.get();
		}

		public List<WebElementEx> headings() {
			return this.headings.get();
		}
	}
}