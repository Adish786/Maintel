package com.about.mantle.commerce;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.ExtCommerceLatestComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import org.junit.Assert;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class MntlLatestComponentCNDTest extends MntlVenusTest implements MntlCommonTestMethods {

    public Consumer<MntlRunner> commerceLatestComponentTest = runner -> {
        MntlBasePage page = new MntlBasePage(runner.driver(), ExtCommerceLatestComponent.class);
        ExtCommerceLatestComponent latestComponent = (ExtCommerceLatestComponent) page.getComponent();
        Assert.assertTrue("Latest component does not exist on the document",latestComponent.isLatestComponentExist());
        collector.checkThat("Latest component header is empty", latestComponent.latestHeading(), not(emptyOrNullString()));
        ExtCommerceLatestComponent.LatestList componentList = latestComponent.latestList();
        List<WebElementEx> cardList = componentList.listItems();
        collector.checkThat("card list is greater than 3 or were not found on the page",
                cardList.size(), allOf(lessThan(4), greaterThan(0)));
        for(int card = 0; card < cardList.size(); card++){
            String href = cardList.get(card).href();
            collector.checkThat("Card is not clickable", href, not(emptyOrNullString()));
            collector.checkThat("Card image is not displayed", componentList.cardMedia().get(card).isDisplayed(), is(true));
            collector.checkThat("Card tag is empty", componentList.cardMediaTagText(card), not(emptyOrNullString()));
            collector.checkThat("Card title is empty", componentList.cardTitle(card), not(emptyOrNullString()));
        }
    };
}
