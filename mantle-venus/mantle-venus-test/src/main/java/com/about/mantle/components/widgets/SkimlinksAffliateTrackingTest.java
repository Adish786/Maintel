package com.about.mantle.components.widgets;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.pages.MntlListScPage;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.test.VenusTest;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class SkimlinksAffliateTrackingTest extends MntlVenusTest 
implements MntlCommonTestMethods<MntlRunner> {
	
	abstract public String getTestUrl();
	abstract public String getSkimlinkId();
	
	private  MntlListScItemComponent skimlinksPageOpenTest(MntlRunner runner) {
		MntlListScPage mntlListScPage = new MntlListScPage<>(runner.driver(), MntlListScItemComponent.class);
		mntlListScPage.scrollIntoViewCentered();
		List<MntlListScItemComponent> mntlListScBlocks = mntlListScPage.listScBlocks();
		collector.checkThat("ListSc blocks are missing ", mntlListScBlocks.size(), greaterThan(0));
		MntlListScItemComponent listScItem = mntlListScBlocks.get(1);
		listScItem.scrollIntoViewCentered();
		runner.page().waitFor().aMoment();
			return listScItem ;
		}
	
	protected Consumer<MntlRunner> skimlinksHeaderClickTest = (runner) -> {
		MntlListScItemComponent listScItem = skimlinksPageOpenTest(runner);
		listScItem.listHeading().scrollIntoViewCentered();
		listScItem.listHeading().click();
		runner.page().waitFor().aMoment();
		verifySkimlinksIdUrlPresent(runner);
	};
	
	protected Consumer<MntlRunner> skimlinksImageTest = (runner) -> {
		MntlListScItemComponent listScItem = skimlinksPageOpenTest(runner);
		listScItem.commerceImage().scrollIntoViewCentered();
		listScItem.commerceImage().click();
		runner.page().waitFor().aMoment();
		verifySkimlinksIdUrlPresent(runner);
	};
	
	protected Consumer<MntlRunner> skimlinksButtonTest = (runner) -> {
		MntlListScItemComponent listScItem = skimlinksPageOpenTest(runner);
		collector.checkThat("No commerce button found", listScItem.commerceButtons().size(), greaterThan(0));
		listScItem.commerceButtons().get(0).scrollIntoViewCentered();
		listScItem.commerceButtons().get(0).click();
		runner.page().waitFor().aMoment();
		verifySkimlinksIdUrlPresent(runner);
	};
	
	private void verifySkimlinksIdUrlPresent(MntlRunner runner) {	
	  runner.page().waitFor().aMoment();
	  List<VenusHarEntry> entries = runner.proxy()
				.capture()
				.page(runner.proxyPage())
				.entries("go.skimresources")
				.stream()
				.filter(entry -> entry.request().url().url().contains(getSkimlinkId()))
				.collect(Collectors.toList());
		collector.checkThat("Skimlink size is more then one or is zero", 
				entries.size(),
				is(1));
		collector.checkThat("Skimlink link id is incorrect", 
				entries.get(0).request().url().url(),
				containsString(getSkimlinkId()));
	};
}


