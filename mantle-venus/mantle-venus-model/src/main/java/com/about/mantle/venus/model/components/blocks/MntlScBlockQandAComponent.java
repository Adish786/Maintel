package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-questionandanswer")
public class MntlScBlockQandAComponent extends MntlComponent {

    private final Lazy<WebElementEx> tocId;
    private final Lazy<WebElementEx> question;
    private final Lazy<WebElementEx> answer;
    private final Lazy<List<WebElementEx>> answerLinks;

    public MntlScBlockQandAComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.tocId = lazy(() -> findElement(By.tagName("span")));
        this.question = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-questionandanswer__question")));
        this.answer = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-questionandanswer__answer")));
        this.answerLinks = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-questionandanswer__answer a")));
    }

    public WebElementEx tocId() {
        return tocId.get();
    }

    public WebElementEx question() {
        return question.get();
    }

    public WebElementEx answer() { return answer.get(); }

    public List<WebElementEx> answerLinks() { return answerLinks.get(); }

}
