package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlRollawayBlockComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

@SuppressWarnings({ "rawtypes", "unused" })
public class MntlRollawayBlockTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	protected static Map<String,String> expectedAttributeValues = new HashMap<>();
	@SuppressWarnings("unchecked")
	public Consumer<MntlRunner> blockTest = runner -> {
		MntlBasePage page = new MntlBasePage(runner.driver(),MntlRollawayBlockComponent.class);
		MntlRollawayBlockComponent block = (MntlRollawayBlockComponent) page.getComponent();
		collector.checkThat("Rollway block is missing", block.displayed(), is(true));
		collector.checkThat("Rollway block text is empty", block.text(),
				not(emptyOrNullString()));
		runner.page().scroll();
		runner.page().waitFor().aMoment();
		runner.page().scroll().bottom();
		runner.page().waitFor().aMoment();
		collector.checkThat("Rollway block is not collapsing",block.className(),containsString("is-collapsing"));
		collector.checkThat("Rollway block collapsepoint is incorrect",
				block.getWebElement().getAttribute("data-collapsepoint"),is(expectedAttributeValues.get("data-collapsepoint")));
		collector.checkThat("Rollway block scrollheight is incorrect",
				block.getWebElement().getAttribute("data-scrollheight"),
				is(expectedAttributeValues.get("data-scrollheight")));
   };

}
