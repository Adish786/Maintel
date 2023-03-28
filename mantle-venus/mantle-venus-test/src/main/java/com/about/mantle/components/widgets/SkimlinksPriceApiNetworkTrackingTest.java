package com.about.mantle.components.widgets;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.buttons.MntlProductBlockCommerceButton;
import com.about.mantle.venus.model.components.buttons.MntlScBLockCommerceButton;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.components.lists.MntlProductBlockCommerceItemComponent;
import com.about.mantle.venus.model.pages.MntlListScPage;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.test.VenusTest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

public abstract class SkimlinksPriceApiNetworkTrackingTest extends MntlVenusTest
	implements MntlCommonTestMethods<MntlRunner> {

	abstract public String getTestUrl();

	abstract public String getSkimlinkId();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MntlScBLockCommerceButton getCommerceButtonWithRedirect(MntlRunner runner) {
		MntlListScPage mntlListScPage = new MntlListScPage<>(runner.driver(), MntlListScItemComponent.class);
		// Get all commerce button on the page
		List<MntlListScItemComponent> mntlListScBlocks = mntlListScPage.listScBlocks();
		List<MntlScBLockCommerceButton> commerceButtons = new ArrayList<>();
		for (MntlListScItemComponent block : mntlListScBlocks) {
			commerceButtons.addAll(block.commerceButtons());
		}
		// Filter commerce button which has redirect
		List<MntlScBLockCommerceButton> commerceButtonsWithRedirect = new ArrayList<>();
		for (MntlScBLockCommerceButton button : commerceButtons) {
			if (button.hasRedirect())
				commerceButtonsWithRedirect.add(button);
		}
		collector.checkThat("commerceButtons with redirect sz is null ", commerceButtonsWithRedirect.size(),
			greaterThan(0));
		// Random pick one commerce button with price tag
		final int index = new SecureRandom().nextInt(commerceButtonsWithRedirect.size());
		MntlScBLockCommerceButton randomButtonWithPrice = commerceButtonsWithRedirect.get(index);
		return randomButtonWithPrice;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MntlProductBlockCommerceButton getCommerceProductBlockButtonWithRedirect(MntlRunner runner) {
		MntlListScPage mntlListScProduct = new MntlListScPage<>(runner.driver(),
			MntlProductBlockCommerceItemComponent.class);
		// Get all commerce button on the page
		List<MntlProductBlockCommerceItemComponent> mntlListScBlocks = mntlListScProduct.ProductRecordBlocks();
		List<MntlProductBlockCommerceButton> commerceButtons = new ArrayList<>();
		for (MntlProductBlockCommerceItemComponent block : mntlListScBlocks) {
			commerceButtons.addAll(block.commerceProductButtons());
		}
		// Filter commerce button which has redirect
		List<MntlProductBlockCommerceButton> commerceButtonsWithRedirect = new ArrayList<>();
		for (MntlProductBlockCommerceButton button : commerceButtons) {
			if (button.hasRedirect())
				commerceButtonsWithRedirect.add(button);
		}
		collector.checkThat("commerceButtons with redirect sz is null ", commerceButtonsWithRedirect.size(),
			greaterThan(0));
		// Random pick one commerce button with price tag
		final int index = new SecureRandom().nextInt(commerceButtonsWithRedirect.size());
		MntlProductBlockCommerceButton randomButtonWithPrice = commerceButtonsWithRedirect.get(index);
		return randomButtonWithPrice;
	}

	private void verifySkimlinksPriceApiUrlPresent(MntlRunner runner) {
		runner.page().waitFor().aMoment();
		List<VenusHarEntry> entries = runner.proxy().capture().page(runner.proxyPage()).entries("go.redirectingat")
			.stream().filter(entry -> entry.request().url().url().contains(getSkimlinkId()))
			.collect(Collectors.toList());
		collector.checkThat("Skimlink price size is more then one or is zero", entries.size(), is(1));
		collector.checkThat("Skimlink link id is incorrect", entries.get(0).request().url().url(),
			containsString(getSkimlinkId()));
		collector.checkThat("Skimlink does not contain xcust", entries.get(0).request().url().url(),
			containsString("xcust"));
	};

	protected Consumer<MntlRunner> skimlinksButtonPriceApicallTest = (runner) -> {
		runner.page().waitFor().documentComplete(60);
		MntlListScPage<MntlComponent> mntlListScPage = new MntlListScPage<>(runner.driver(),
			MntlListScItemComponent.class);
		List<MntlListScItemComponent> listScBlocks = mntlListScPage.listScBlocks();
		if (!listScBlocks.get(0).hasProductBlock()) {
			MntlScBLockCommerceButton button = getCommerceButtonWithRedirect(runner);
			button.scrollIntoViewCentered();
			button.click();
			runner.page().waitFor().aMoment();
			verifySkimlinksPriceApiUrlPresent(runner);
		} else {
			MntlProductBlockCommerceButton button = getCommerceProductBlockButtonWithRedirect(runner);
			button.scrollIntoViewCentered();
			button.click();
			runner.page().waitFor().aMoment();
			verifySkimlinksPriceApiUrlPresent(runner);
		}
	};
}

