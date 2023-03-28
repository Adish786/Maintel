package com.about.mantle.schema;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.images.MntlPrimaryImageComponent;
import com.about.mantle.venus.model.components.images.MntlScBlockImageComponent;
import com.about.mantle.venus.model.components.media.MntlScBlockInlineVideoComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.venus.core.driver.WebDriverExtended;
import io.restassured.path.json.JsonPath;
import org.json.JSONArray;
import org.junit.Assert;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;


public class ImageSchemaTest extends ArticleSchemaTest {

    protected void compositeImagesTest(WebDriverExtended driver, String url, String template) {
        JsonPath schema = new JsonPath(getSchemas(driver, url).get(0));
        ArrayList compositeImage = new ArrayList();
        if (template.contains("HOW_TO")) {
            ArrayList list = schema.get("about.step.image[0]");
            for (Object imageList : list) {
                for (Object i : (ArrayList) imageList) {
                    if (!(i == null)) {
                        var image = (ArrayList) i;
                        if (image.size() > 1) compositeImage.add(image);
                    }
                }
            }
        }
        if (template.contains("RECIPE")) {
            ArrayList list = schema.get("recipeInstructions.image");
            for (Object imageList : list) {
                if (!(imageList == null)) {
                    var image = (ArrayList) imageList;
                    if (image.size() > 1) compositeImage.add(image);
                }
            }
        }
        collector.checkThat("Composite image not found in the Schema", compositeImage.size(), greaterThan(0));
    }

    /**
     * This test is not applicable to :
     *                 HOW_TO template
     *                 PRM OR Commerce OR Financial Services and template does NOT equal Structured Content
     * @param driver
     * @param url
     */
    protected void imageSchemaTest(WebDriverExtended driver, String url){

        JsonPath schema = new JsonPath(getSchemas(driver, url).get(0));
        MntlPage page = new MntlPage(driver);

            try {
                collector.checkThat("One ImageObject is expected on the page schema image field", new ArrayList<>(schema.get("image")).size(), is(1));
                String imageURL = schema.get("image[0].url");
                Assert.assertNotNull(imageURL);
                String imageNameFromSchema = imageName(imageURL);

            if (page.componentExists(MntlPrimaryImageComponent.class)) {
                MntlBasePage primaryImagepage = new MntlBasePage(driver, MntlPrimaryImageComponent.class);
                MntlPrimaryImageComponent primaryImage = (MntlPrimaryImageComponent) primaryImagepage.getComponent();
                primaryImage.scrollIntoViewCentered();
                primaryImage.waitFor().aMoment();
                String primaryImageNameFromPage = imageName(primaryImage.attributeValue("src"));
                collector.checkThat("Primary Image is expected on the page schema image field", primaryImageNameFromPage, equalTo(imageNameFromSchema));
                collector.checkThat("Image height is expected on the page schema image field", schema.get("image[0].height"), not(0));
                collector.checkThat("Image width is expected on the page schema image field", schema.get("image[0].width"), not(0));

            } else if (page.componentExists(MntlScBlockImageComponent.class)) {
                MntlBasePage blockImagepage = new MntlBasePage(driver, MntlScBlockImageComponent.class);
                List<MntlScBlockImageComponent> scBlockImagesComponent = (List<MntlScBlockImageComponent>) blockImagepage.getComponents();
                scBlockImagesComponent.get(0).scrollIntoViewCentered();
                scBlockImagesComponent.get(0).waitFor().exactMoment(10, TimeUnit.SECONDS);
                String firstBlockImageNameFromPage = imageName(scBlockImagesComponent.get(0).image().attributeValue("src"));
                collector.checkThat("First Block Image is expected on the page schema image field", firstBlockImageNameFromPage, equalTo(imageNameFromSchema));
                collector.checkThat("Image height is expected on the page schema image field", schema.get("image[0].height"), not(0));
                collector.checkThat("Image width is expected on the page schema image field", schema.get("image[0].width"), not(0));

            } else if (page.componentExists(MntlScBlockInlineVideoComponent.class)) {
                MntlScPage mntlScPage = new MntlScPage<>(driver, MntlScBlockComponent.class);
                List<MntlScBlockComponent> mntlListScItemComponents = mntlScPage.getComponents();
                List<MntlScBlockComponent> mntlListScItemComponentsWithVideo = mntlListScItemComponents.stream().
                        filter(ScB->ScB.className().contains("mntl-sc-block-inlinevideo")).collect(Collectors.toList());
                mntlListScItemComponentsWithVideo.get(0).scrollIntoViewCentered();
                collector.checkThat("Primary Video Thumbnail Url is expected on the page schema image field", schema.get("video[0].thumbnailUrl"), equalTo(imageURL));
            } else {
                Assert.fail("There is no Primary/Inline image nor Inline video on the page.");
            }
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
    }

    /**
     * ImageSchemaTest for page with no image on it.
     * @param driver
     * @param url
     */
    protected void noImageSchemaTest(WebDriverExtended driver, String url){
        MntlPage page = new MntlPage(driver);
        SchemaComponent schemaComp = (SchemaComponent)page.head().findElement(By.cssSelector(this.SCHEMA_SELECTOR), SchemaComponent::new);
        final var jsonArray = new JSONArray(schemaComp.schemaContent());
        collector.checkThat("Image field is present on the page schema", jsonArray.getJSONObject(0).keySet().contains("image"), is(false));
    }

    /**
     * This test is valid only for PRM OR Commerce OR Financial Services and template does NOT equal Structured Content
     * @param driver
     * @param url
     */
    protected void imageSchemaTestForCommercePRM(WebDriverExtended driver, String url){
        JsonPath schema = new JsonPath(getSchemas(driver, url).get(0));
        MntlPage page = new MntlPage(driver);

        try {
            collector.checkThat("One ImageObject is expected on the page schema image field", new ArrayList<>(schema.get("image")).size(), is(1));
            String imageURL = schema.get("image[0].url");
            Assert.assertNotNull(imageURL);
            String imageNameFromSchema = imageName(imageURL);

            if (page.componentExists(MntlPrimaryImageComponent.class)) {
                MntlBasePage primaryImagepage = new MntlBasePage(driver, MntlPrimaryImageComponent.class);
                MntlPrimaryImageComponent primaryImage = (MntlPrimaryImageComponent) primaryImagepage.getComponent();
                primaryImage.scrollIntoViewCentered();
                primaryImage.waitFor().aMoment();
                String primaryImageNameFromPage = imageName(primaryImage.attributeValue("src"));
                collector.checkThat("Primary Image is expected on the page schema image field", primaryImageNameFromPage, equalTo(imageNameFromSchema));
                collector.checkThat("Image height is expected on the page schema image field", schema.get("image[0].height"), not(0));
                collector.checkThat("Image width is expected on the page schema image field", schema.get("image[0].width"), not(0));

            } else if (page.componentExists(MntlScBlockInlineVideoComponent.class)) {
                MntlScPage mntlScPage = new MntlScPage<>(driver, MntlScBlockComponent.class);
                List<MntlScBlockComponent> mntlListScItemComponents = mntlScPage.getComponents();
                List<MntlScBlockComponent> mntlListScItemComponentsWithVideo = mntlListScItemComponents.stream().
                        filter(ScB->ScB.className().contains("mntl-sc-block-inlinevideo")).collect(Collectors.toList());
                mntlListScItemComponentsWithVideo.get(0).scrollIntoViewCentered();
                collector.checkThat("Primary Video Thumbnail Url is expected on the page schema image field", schema.get("video[0].thumbnailUrl"), equalTo(imageURL));
            } else {
                Assert.fail("There is no Primary image nor Inline video on the page.");
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    protected String imageName(String imageSource){
        String imageName = "No image found.";
        if(imageSource.contains(".jpeg"))
            imageName = Arrays.stream(imageSource.split("/")).filter(s -> s.contains(".jpeg")).findFirst().get();
        if(imageSource.contains(".jpg"))
            imageName = Arrays.stream(imageSource.split("/")).filter(s -> s.contains(".jpg")).findFirst().get();
        if(imageSource.contains(".png"))
            imageName = Arrays.stream(imageSource.split("/")).filter(s -> s.contains(".png")).findFirst().get();

        return  imageName ;
    }

}
