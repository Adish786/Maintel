package com.about.mantle.components.recipe;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.MnltArticleComponent;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.mantle.venus.model.components.taxonomySC.MntlTaxonomySCarticleListComponent;
import com.about.mantle.venus.model.components.taxonomySC.MntlTaxonomySCfixedContentComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.HTMLUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.about.mantle.venus.utils.selene.SeleneUtils.getDocuments;
import static org.hamcrest.Matchers.is;

public class MntlHiddenRecipeTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected BiConsumer<MntlCommonTestMethods.MntlRunner, String> mntlHiddenRecipeTest = (runner, docId) -> {

        Map<String, String> metaTags = null;
        try {
            metaTags = HTMLUtils.metaTagsFromDocument(runner.driver().getCurrentUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert metaTags != null;

            collector.checkThat("document.hidden == false for hidden document", getDocuments(docId).read("data.hidden"), is(true));
            collector.checkThat("document.noIndex == false for hidden document", getDocuments(docId).read("data.noIndex"), is(true));
            collector.checkThat("Robots metaTag is expected to contain NOINDEX for hidden document", metaTags.get("robots").contains("NOINDEX"), is(true));
            collector.checkThat("Robots metaTag is expected to contain NOFOLLOW for hidden document", metaTags.get("robots").contains("NOFOLLOW"), is(true));


        MnltArticleComponent articleComponent = (MnltArticleComponent) new MntlBasePage(runner.driver(), MnltArticleComponent.class).getComponent();
        String recipeName = articleComponent.heading();

        articleComponent.tagNavItems().get(new Random().nextInt(articleComponent.tagNavItems().size())).webElement().click();
        MntlTaxonomySCfixedContentComponent recipeContent = (MntlTaxonomySCfixedContentComponent) new MntlBasePage(runner.driver(), MntlTaxonomySCfixedContentComponent.class).getComponent();
        MntlTaxonomySCarticleListComponent recipeList = recipeContent.articleList();
        List<MntlCardComponent> testRecipe = recipeList.articles().stream().filter(a -> a.content().title().getText().contains(recipeName)).collect(Collectors.toList());
        collector.checkThat("Recipe List should not contain hidden recipes", testRecipe.size(), is(0));
    };

}
