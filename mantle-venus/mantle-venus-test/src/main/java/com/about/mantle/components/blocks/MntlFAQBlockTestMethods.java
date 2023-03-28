package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.taxonomySC.MntlTaxonomySCFAQBlockComponent;
import com.about.mantle.venus.model.components.taxonomySC.MntlTaxonomySCFAQBlockComponent.AccordionItem;

import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public abstract class MntlFAQBlockTestMethods extends MntlVenusTest implements MntlCommonTestMethods {

    protected abstract String getFAQText();

    /**
     * Test verify FAQ block with expanded theme or non expanded
     * Test verify block and its elements
     * @Boolean = nonExpanded
     */
    protected BiConsumer<Runner, Boolean> faqBlockTest = (runner, nonExpanded) -> {
        boolean faqComponentExists = runner.page().componentExists(MntlTaxonomySCFAQBlockComponent.class);
        assertThat("Faq Block is not present ", faqComponentExists, is(true));

        MntlTaxonomySCFAQBlockComponent faqBlock = (MntlTaxonomySCFAQBlockComponent) runner.component();

        collector.checkThat("faq block title text is empty or null", faqBlock.title().text(),
                is(getFAQText()));
        collector.checkThat("faq block accordion items size is 0", faqBlock.accordionItems().size(), greaterThan(0));

        for (AccordionItem item : faqBlock.accordionItems()) {
            item.scrollIntoViewCentered();
            collector.checkThat("accordion item text is empty or null", item.title().text(), not(emptyOrNullString()));
            if(nonExpanded) {
                collector.checkThat("accordion item icon is not displayed", item.icon().isDisplayed(), is(true));
                item.click();
            }
            collector.checkThat("accordion class does not contain is-active", item.className(),
                    containsString("is-active"));
            collector.checkThat("accordion item answer text is empty or null", item.accordionText().text(),
                    not(emptyOrNullString()));
            List<AccordionItem.Answer> answers = item.answers();
            collector.checkThat("faq block accordion item " + item.text() + " doesn't have any answers displayed", answers.size(), greaterThan(0));
            if (answers.size() > 0) {
                for (AccordionItem.Answer answer : answers) {
                    collector.checkThat("answer text is empty or null", answer.text(), not(emptyOrNullString()));
                    if (answer.hasLink()) {
                        collector.checkThat("inline link text is empty or null", answer.inlineLink().text(),
                                not(emptyOrNullString()));
                        collector.checkThat("inline link href is empty or null", answer.inlineLink().href(),
                                not(emptyOrNullString()));
                    }
                }
            }
            if (item.hasFeaturedLink()) {
                collector.checkThat("featured link text is empty or null", item.featuredLink().text(),
                        not(emptyOrNullString()));
                collector.checkThat("featured link href is empty or null", item.featuredLinkHref(),
                        not(emptyOrNullString()));
            }
        }

    };
}
