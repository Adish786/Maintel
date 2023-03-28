package com.about.mantle.venus.model.components.quizzes;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.components.MntlSocialShareComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

public class ResultComponent extends Component {

    private final Lazy<WebElementEx> result;
    private final Lazy<WebElementEx> image;
    private final Lazy<WebElementEx> shareText;
    private final Lazy<MntlSocialShareComponent> socialShare;

    public ResultComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.result = lazy(() -> findElement(By.className("result__heading")));
        this.image = lazy(() -> findElement(By.cssSelector("img")));
        this.shareText = lazy(() -> findElement(By.className("share-text")));
        this.socialShare = lazy(() -> findElement(By.className("social-share"),MntlSocialShareComponent::new));
    }

    @Override
    public String text(){
        return result.get().text();
    }

    public WebElementEx image() {
        return image.get();
    }

    public WebElementEx shareText(){
        return shareText.get();
    }

    public MntlSocialShareComponent socialShare(){
        return socialShare.get();
    }

}

