package com.about.mantle.commerce;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import org.hamcrest.Matchers;

public class MntlSkimlinkScriptTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected void skimlinkScriptTest(MntlRunner runner, String skimlinkId) {

        String pageSource = runner.driver().getPageSource();
        collector.checkThat("Page has not skimlink script", pageSource,
                Matchers.containsString("//s.skimresources.com/js/" + skimlinkId + ".skimlinks.js"));
    }
}
