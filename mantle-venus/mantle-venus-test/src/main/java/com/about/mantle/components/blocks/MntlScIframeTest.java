package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.pages.MntlScPage;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public abstract class MntlScIframeTest extends MntlVenusTest implements MntlCommonTestMethods{
		
	/**
	 * In case you don't want to verify exact data and want to use generic validations, return false for this method in your implementation
	 */
	protected abstract boolean verifyExactValues();
	
	protected abstract String getIframeSource();
	
	protected abstract String getIframeCaption();
	
	protected abstract String getTestUrl();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Consumer<MntlRunner> mntlScIframeTest = runner -> {
		MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlScBlockComponent.class);
		mntlScPage.clickContinueReading();
		List<MntlScBlockComponent> mntlScBlocksWithIframes = mntlScPage.mntlScBlocksWithIframes();
		collector.checkThat("iFrames were not found on the page", mntlScBlocksWithIframes.size(), greaterThan(0));
		MntlScBlockComponent mntlScBlockWithIframe = mntlScBlocksWithIframes.get(0);
		mntlScBlockWithIframe.scrollIntoViewCentered();
		collector.checkThat("iFrame caption is not correct", mntlScBlockWithIframe.iFrameCaption().text(),
				verifyExactValues() ? equalTo(getIframeCaption()) : not(isEmptyOrNullString()));
		collector.checkThat("iFrame source is not correct", mntlScBlockWithIframe.iFrame().getWebElement().getAttribute("src"),
				verifyExactValues() ? equalTo(getIframeSource()) : not(isEmptyOrNullString()));
	};
}
