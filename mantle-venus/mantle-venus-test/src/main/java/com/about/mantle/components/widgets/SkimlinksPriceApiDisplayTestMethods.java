package com.about.mantle.components.widgets;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.commons.MntlCommonTestMethods.MntlRunner;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.buttons.MntlProductBlockCommerceButton;
import com.about.mantle.venus.model.components.buttons.MntlScBlockCommerce;
import com.about.mantle.venus.model.components.lists.MntlListScItemComponent;
import com.about.mantle.venus.model.components.lists.MntlProductBlockCommerceItemComponent;
import com.about.mantle.venus.model.pages.MntlListScPage;
import com.about.venus.core.driver.WebElementEx;

import org.junit.Assert;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.security.SecureRandom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

public abstract class SkimlinksPriceApiDisplayTestMethods extends MntlVenusTest
        implements MntlCommonTestMethods<MntlRunner> {

    int priceCount = 0;

    abstract public String getDocID();

    protected List<String> retailers = new ArrayList<>();
    private List<WebElementEx> retailersWithPrice = new ArrayList<>();

    /*
     * use skimlinkPriceTest instead
     */
    @Deprecated
    protected void skimlinkPriceDisplayTest(MntlRunner runner) {
        runner.driver().get(runner.url());
        runner.page().waitFor().documentComplete(60);
        MntlListScPage<MntlComponent> mntlListScPage = new MntlListScPage<>(runner.driver(), MntlListScItemComponent.class);
        List<MntlProductBlockCommerceItemComponent> listScProductBlocks = mntlListScPage.ProductRecordBlocks();
        collector.checkThat("ListSc blocks are missing ", listScProductBlocks.size(), greaterThan(0));

        MntlScBlockCommerce commerceComponent = new MntlScBlockCommerce(runner.driver(), runner.driver().findElementEx(By.tagName("html")));
        commerceComponent.productButton()
                .stream().filter(retailerLink -> (retailerLink.getAttribute("data-retailer-type")
                .equalsIgnoreCase("SKIMLINKS")))
                .collect(Collectors.toList())
                .stream().forEach(retailer -> retailers.add(retailer.getText()));

        testProductBlocksPriceApiDisplay(listScProductBlocks);

        if (priceCount == 0) {
            Assert.fail("None of the list item block has price");
        }
    }

    private void testProductBlocksPriceApiDisplay(List<MntlProductBlockCommerceItemComponent> listScProductBlocks) {
        for (MntlProductBlockCommerceItemComponent block : listScProductBlocks) {
            block.scrollIntoView();
            List<MntlProductBlockCommerceButton> commerceButtons = block.commerceProductButtons();
            collector.checkThat("commerce buttons are missing ", commerceButtons.size(), greaterThan(0));
            for (MntlProductBlockCommerceButton commerceButton : commerceButtons) {
                String retailerName = commerceButton.span().getText();
                if (retailers.stream().anyMatch(retailerName::equalsIgnoreCase))
                    testProductBLockCommerceButton(commerceButton);
            }
        }
        if (priceCount == 0) {
            Assert.fail("None of the list item block has price");
        }
    }

    private void testProductBLockCommerceButton(MntlProductBlockCommerceButton commerceButton) {
        collector.checkThat("retailers widget price tag is not displayed",
                commerceButton.displayed(), is(true));
        if (commerceButton.hasPrice()) {
            testRetailerButton(commerceButton);
        }
    }

    private void testRetailerButton(MntlProductBlockCommerceButton commerceButton) {
        String dataClick = commerceButton.attributeValue("data-click-href");
        String price = commerceButton.attributeValue("data-commerce-price");
        collector.checkThat("Skimlink click href does not contain xcust parameter",
                dataClick, containsString("xcust="));
        String docID = dataClick.split("xcust=")[1].split("%")[0];
        collector.checkThat("doc ID is not correct ", docID, is(getDocID()));
        Assert.assertEquals("Skimlink click href does not have request id",
                false, dataClick.split("xcust=")[1].split("%")[1].isEmpty());
        collector.checkThat("Price does not start with $ sign", price, startsWith("$"));
        priceCount++;
    }
    
    protected Consumer<MntlRunner> skimlinkPriceTest = runner -> { 
        runner.page().waitFor().documentComplete(60);
        MntlScBlockCommerce commerceComponent = new MntlScBlockCommerce(runner.driver(), runner.driver().findElementEx(By.tagName("html")));	
        List<WebElementEx> allButtons = commerceComponent.allButtons();
        Assert.assertThat("ListSc blocks are missing ",allButtons.size(), greaterThan(0));
        
        	for (WebElementEx element : allButtons) {
        		Optional<String> attribute = Optional.ofNullable(element.getAttribute("data-commerce-price"));
        		boolean isAmazon = element.href().contains("amazon");
        			if (attribute.isPresent() && !isAmazon) {
        				retailersWithPrice.add(element);
        			} 
        		}
        	if (retailersWithPrice.size() > 0) {
        		  testRetailerButton(retailersWithPrice.get(new SecureRandom().nextInt(retailersWithPrice.size())));
			}
    };
    
    private void testRetailerButton(WebElementEx commerceButton) {
    	commerceButton.scrollIntoViewCentered();
    	commerceButton.waitFor();
        String dataClick = commerceButton.getAttribute("data-click-href");
        String price = commerceButton.getAttribute("data-commerce-price");
        collector.checkThat("Skimlink click href does not contain xcust parameter",
                dataClick, containsString("xcust="));
        collector.checkThat("Skimlink click href does not contain xs=12 parameter",
                dataClick, containsString("xs=12"));
        String docID = dataClick.split("xcust=")[1].split("%")[0];
        collector.checkThat("doc ID is not correct ", docID, is(getDocID()));
        Assert.assertEquals("Skimlink click href does not have request id",
                false, dataClick.split("xcust=")[1].split("%")[1].isEmpty());
        collector.checkThat("Price does not start with $ sign", price, startsWith("$"));
    }   
}