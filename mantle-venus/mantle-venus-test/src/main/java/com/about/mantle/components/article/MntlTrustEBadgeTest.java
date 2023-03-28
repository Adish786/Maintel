package com.about.mantle.components.article;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MntlTrustEBadgeComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.about.mantle.venus.model.components.article.MntlTrustEBadgeComponent.TrustEBadge;
import static com.about.mantle.venus.model.components.article.MntlTrustEBadgeComponent.TrustEBadgePage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MntlTrustEBadgeTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected static String TRUSTE_URL;

    protected Consumer<Runner> trustEBadgeTest = (runner) -> {
        MntlBasePage page = new MntlBasePage<>(runner.driver(), MntlTrustEBadgeComponent.class);
        boolean componentExists = page.componentExists(MntlTrustEBadgeComponent.class);
        assertThat("TrustE badge component doesn't exist", componentExists, is(true));
        MntlTrustEBadgeComponent trustEBadgeComponent = (MntlTrustEBadgeComponent) page.getComponent();
        trustEBadgeComponent.scrollIntoViewCentered();
        trustEBadgeComponent.waitFor().aMoment(3, TimeUnit.SECONDS);
        collector.checkThat("TRUSTe image is not displayed", trustEBadgeComponent.trustEBadgeImage().displayed(), is(true));
        TrustEBadge trust = trustEBadgeComponent.trustEBadgeImage();
        String parent = runner.driver().getWindowHandle();
        trust.scrollIntoViewCentered();
        TrustEBadgePage trustEBadgePage = trust.openInNewWindow();
        collector.checkThat("TRUSTe title is not correct", trustEBadgePage.currentUrl(), is(TRUSTE_URL));
        trust.closeWindow(parent);
    };
}
