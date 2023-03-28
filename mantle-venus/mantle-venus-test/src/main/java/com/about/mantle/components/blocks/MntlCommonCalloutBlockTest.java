package com.about.mantle.components.blocks;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScBlockCalloutComponent;
import com.about.venus.core.driver.WebDriverExtended;

public class MntlCommonCalloutBlockTest extends MntlVenusTest implements MntlCommonTestMethods<MntlRunner> {

	protected void testStructuredContentCalloutBlocks(List<MntlScBlockCalloutComponent> calloutBlocks,
			WebDriverExtended driver, ArrayList<String> calloutBlock) {
		List<MntlScBlockCalloutComponent> calloutBlockScList = calloutBlocks;
		ArrayList<String> calloutThemes = new ArrayList<String>();
		calloutBlockScList.forEach(block -> {

			String className = block.className();

			String patternString = "theme-(.*?)(?!\\w)";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(className);
			String calloutTheme = null;
			while (matcher.find()) {
				calloutTheme = matcher.group(1);
			}
			block.scrollIntoViewCentered();
			block.waitFor().aMoment();
			collector.checkThat("calloutBlock is not displayed", block.displayed(), is(true));
			collector.checkThat("calloutBlock body is not displayed", block.body().displayed(), is(true));

			if (block.hasHeading())
				collector.checkThat(calloutTheme + "block heading is not correct", block.heading().text(),
						not(emptyOrNullString()));
			collector.checkThat(calloutTheme + "callout block doesn't contain theme image",
					block.getWebElement().pseudoElementProperty("after", "background"), not(emptyOrNullString()));
			calloutThemes.add(calloutTheme);
		});
		calloutBlock.removeAll(calloutThemes);
		collector.checkThat(calloutBlock + " - callout blocks were not found", calloutThemes.containsAll(calloutBlock),
				is(true));

	}

}
