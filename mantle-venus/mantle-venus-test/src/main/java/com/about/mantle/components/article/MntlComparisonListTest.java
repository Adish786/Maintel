package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.lists.MntlComparisionListComponent;
import com.about.venus.core.driver.WebElementEx;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

/*
This test will test all comparison list components present on the page, will go through each column and validate its headings as well as the items and colors.
If the test data is passed into a test (optional), it will then validate against that data.
It will also test optional items: `h3 heading` or/and `summary_text` if they are passes into the test.
Value for colors could also be passed into a test but it is left as optional, if not passed, checking if the color css attribute is not empty.
Test data comparisonListTestData() can be passed as null, basic validations will be executed on all components.
*/
public abstract class MntlComparisonListTest extends MntlVenusTest implements MntlCommonTestMethods {

    public abstract ImmutableMap<Integer, ImmutableMap<String, ArrayList<String>>> comparisonListTestData();

    // THIS IS SERVICE METHOD TO PROVIDE
    protected Consumer<Runner> comparisonListTest = (runner) -> {
        // Data is optional, will use simple validation if data is empty
        boolean testingData = comparisonListTestData() != null;

        // Stop testing if comparisonListComponents is empty
        runner.page().waitFor().exactMoment(1, TimeUnit.SECONDS);
        List<MntlComparisionListComponent> comparisonListComponents = runner.page().getComponents();
        if (comparisonListComponents.isEmpty())
            Assert.fail("There are no comparisonList components present on the page");

        // Verification
        for (MntlComparisionListComponent comparisonListComponent : comparisonListComponents) {
            comparisonListComponent.scrollIntoView();
            int componentNumber = comparisonListComponents.indexOf(comparisonListComponent) + 1;
            testComparisonListComponent(componentNumber, comparisonListComponent, testingData);
        }
    };

    // SUPPORT METHOD TO TEST ON A COMPONENT
    private void testComparisonListComponent(int componentNumber, MntlComparisionListComponent comparisonListComponent, boolean testingData) {
        boolean hasColors = false;
        boolean isDesiredTestComponent = testingData ? comparisonListTestData().containsKey(componentNumber) : false;
        List<MntlComparisionListComponent.ComparisonListColumn> componentColumns = comparisonListComponent.comparisonListColumns();
        if (componentColumns.isEmpty())
            Assert.fail("There are no columns in a comparisonList component number " + componentNumber + " present");

        int numberOfOptionalItems = 0;
        if (isDesiredTestComponent && testingData) {
            ImmutableMap<String, ArrayList<String>> dataForComponent = comparisonListTestData().get(componentNumber);
            if (dataForComponent.containsKey(ComparisonComponentKey.H3_HEADING.keyValue())) {
                collector.checkThat("The comparison list optional header does not match the test data or is empty", comparisonListComponent.listOptionalHeading().getText(),
                        equalToIgnoringCase(comparisonListTestData().get(componentNumber).get("h3 heading").get(0)));
                numberOfOptionalItems++;
            }
            if (dataForComponent.containsKey(ComparisonComponentKey.SUMMARY_TEXT.keyValue())) {
                collector.checkThat("The comparison list column header does not match the test data or is empty", comparisonListComponent.listOptionalSummaryText().getText(),
                        equalToIgnoringCase(comparisonListTestData().get(componentNumber).get("summary_text").get(0)));
                numberOfOptionalItems++;
            }
            if (dataForComponent.containsKey(ComparisonComponentKey.COLORS.keyValue())) {
                hasColors = true;
                numberOfOptionalItems++;
            }
        }

        int componentDataColumns = isDesiredTestComponent ? comparisonListTestData().get(componentNumber).size() - numberOfOptionalItems : componentColumns.size();
        for (int columnIndex = 0; columnIndex < componentDataColumns; columnIndex++) {
            List<WebElementEx> comparisonColumnItems = componentColumns.get(columnIndex).comparisonListItems();
            List<String> itemTextFromTestData = null;
            if (isDesiredTestComponent) {
                itemTextFromTestData = comparisonListTestData().get(componentNumber).values().asList().get(columnIndex);
            }
            collector.checkThat("The comparison list header is empty", componentColumns.get(columnIndex).comparisonListHeading().getText(), not(isEmptyOrNullString()));
            collector.checkThat("The comparison list section " + (columnIndex + 1) + " header color is not correct",
                    componentColumns.get(columnIndex).comparisonListHeading().getCssValue("color"), (isDesiredTestComponent && hasColors) ? is(comparisonListTestData().get(componentNumber).get("colors").get(0)) : not(isEmptyOrNullString()));
            for (WebElementEx comparisonColumnItem : comparisonColumnItems) {
                collector.checkThat("The item description does not match the test data or is empty", comparisonColumnItem.getText(),
                        isDesiredTestComponent ? equalToIgnoringCase(itemTextFromTestData.get(comparisonColumnItems.indexOf(comparisonColumnItem))) : not(isEmptyOrNullString()));
                collector.checkThat("The comparison list column item " + comparisonColumnItem.getText() + " icon color is not correct",
                        comparisonColumnItem.pseudoElementProperty("before", "color"), (isDesiredTestComponent && hasColors) ? is(comparisonListTestData().get(componentNumber).get("colors").get(1)) : not(isEmptyOrNullString()));
            }
        }
    }

    public enum ComparisonComponentKey {

        H3_HEADING("h3 heading"),
        SUMMARY_TEXT("summary_text"),
        COLORS("colors");

        private String keyName;

        ComparisonComponentKey(String keyName) {
            this.keyName = keyName;
        }

        public String keyValue() {
            return keyName;
        }

    }

}