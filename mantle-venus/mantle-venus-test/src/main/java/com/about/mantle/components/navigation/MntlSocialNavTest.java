package com.about.mantle.components.navigation;

import com.about.mantle.clickTracking.MntlClickTrackingTest;
import com.about.mantle.venus.model.components.navigation.MntlSocialFollowNavComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.ConfigurationProperties;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

/*
This will test the Social Nav components. It will consume a map containing the data in an order we expect social nav components to appear on the page,
then will iterate over that map, applying the following validations to every social navigation item:
    - transmitInteractiveEvent dataLayer event is present after clicking on a link;
    - an item appears on the page and not in test data;
    - item's href contains a corresponding url;
    - item's label is not empty;
    - item's icon has a corresponding social network name in it's class;
    - rel attribute is noopener;
I will also check if the number of itmes in a social nav component equals to the number of the items in a test data.
 */
public abstract class MntlSocialNavTest extends MntlClickTrackingTest {

    public abstract ImmutableMap<Integer, ImmutableMap<String, ArrayList<String>>> socialNavTestData();

    public Consumer<Runner> socialNavTest = (runner) -> {
        List<MntlSocialFollowNavComponent> socialNavComponents = new MntlBasePage<>(runner.driver(), MntlSocialFollowNavComponent.class).getComponents();
        collector.checkThat("Social nav component is not present on the page", socialNavComponents.size(), is(not(0)));
        int i = 0;
        Iterator it = socialNavTestData().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ImmutableMap<String, ArrayList<String>> socialNavData = (ImmutableMap<String, ArrayList<String>>) pair.getValue();
            MntlSocialFollowNavComponent socialNavComponent = socialNavComponents.get(i);
            for (MntlSocialFollowNavComponent.MntlSocialNavItemComponent socialNavItem : socialNavComponent.socialNavItems()) {
                String[] linkClassItems = socialNavItem.attributeValue("class").split("-");
                String linkType = linkClassItems[linkClassItems.length - 1];
                TrackingData data = new TrackingData()
                        .event("transmitInteractiveEvent")
                        .action(socialNavData.get(linkType).get(1))
                        .label(socialNavData.get(linkType).get(0))
                        .category("social-nav Click");
                if (!ConfigurationProperties.getTargetProject("").contains("mantle-ref"))
                    testLinkClick(runner.driver(), data, getLastDataLayerIndex(runner.driver(), data.category), socialNavItem.getElement());
                if (socialNavData.containsKey(linkType)) {
                    collector.checkThat(linkType + " link is not correct", socialNavItem.href(), containsString(socialNavData.get(linkType).get(0)));
                    collector.checkThat(linkType + " icon is not correct", socialNavItem.icon().className(), containsString(linkType));
                    collector.checkThat(linkType + " label attribute is empty", socialNavItem.label().text(), not(emptyOrNullString()));
                    if (!linkType.equals("newsletter"))
                        collector.checkThat(linkType + " rel attribute is not noopener", socialNavItem.attributeValue("rel"), is("noopener nocaes"));
                } else {
                    collector.checkThat(linkType + " social nav link is present on the page but absent in test data", true, is(false));
                }
            }
            collector.checkThat("number of items in test data and number of items in the component" + i + " mismatch", socialNavData.size(), is(socialNavComponent.socialNavItems().size()));
            i++;
        }
    };

}
