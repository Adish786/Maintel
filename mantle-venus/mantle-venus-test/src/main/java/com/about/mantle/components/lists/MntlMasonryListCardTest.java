package com.about.mantle.components.lists;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.mantle.venus.model.components.lists.MntlMasonryListComponent;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

public class MntlMasonryListCardTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	private String url;
	private Class<? extends MntlMasonryListComponent> masonryList1;
	private Class<? extends MntlMasonryListComponent> masonryList2;
	
	public MntlMasonryListCardTest(String url,Class<? extends MntlMasonryListComponent> masonryList1, Class<? extends MntlMasonryListComponent> masonryList2) {
		this.url = url;
		this.masonryList1 = masonryList1;
		this.masonryList2 = masonryList2;
	}

	protected Consumer<MntlRunner> masoryListCardTest = runner -> {
		MntlMasonryListComponent masonryList = (MntlMasonryListComponent) runner.component();
		masonryList.waitFor().visibility(30);
		masonryList.scrollIntoViewCentered();
		testCard(masonryList.cards().get(0));
	};

	private void testCard(MntlCardComponent card) {
		collector.checkThat("image is not correct", card.media().img().src().toLowerCase(), anyOf(endsWith(".jpg"), endsWith(".png"), endsWith(".gif")));
		collector.checkThat("card title is empty", card.content().title().text(), not(isEmptyOrNullString()));
	}

}
