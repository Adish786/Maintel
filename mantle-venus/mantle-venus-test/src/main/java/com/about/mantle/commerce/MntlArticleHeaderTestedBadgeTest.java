package com.about.mantle.commerce;

import static org.hamcrest.CoreMatchers.is;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MantleArticleHeaderComponent;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Assert;

import java.util.function.Consumer;

public abstract class MntlArticleHeaderTestedBadgeTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String getDocId();
    private final String taxeneEndPoint = "/taxeneconfig/";



    public Consumer<MntlRunner> articleHeaderTestedBadge = runner -> {
        MantleArticleHeaderComponent article =  (MantleArticleHeaderComponent)runner.component();
        DocumentContext context = SeleneUtils.getSeleneData(taxeneEndPoint + getDocId());
        String status = context.read("status.code").toString();
        boolean commerceTested = false;
        boolean hasConfig = false;

        if (status.equalsIgnoreCase("SUCCESS")) {
            Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
            var testedPath = JsonPath.using(conf).parse(context.jsonString()).read("data.commerceTested");
            if (testedPath != null) {
                commerceTested = context.read("data.commerceTested.value");
                hasConfig = true;
            }
        }
        if (commerceTested) {
            String testedHref = article.testedBadge().getAttribute("xlink:href");
            collector.checkThat("Article header does not have tested badge", testedHref,
                    is( "#commerce-tested__badge"));
        } else if(hasConfig){
            Assert.assertFalse("Tested badge is displayed on the page when there is no config", article.hasTestedBadge());
        }
        else {
            Assert.fail("Either taxene is not set or tested badge is not displayed on the page");
        }
    };
}
