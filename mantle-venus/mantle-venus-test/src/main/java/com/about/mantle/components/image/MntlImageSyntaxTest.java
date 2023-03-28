package com.about.mantle.components.image;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.mantle.venus.model.seo.MntlMetaTags;
import com.about.venus.core.driver.WebElementEx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;

public class MntlImageSyntaxTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected String FILTER = "filters:no_upscale():max_bytes(150000):strip_icc()";
    protected String SIZE = "/1500x0/";
    protected String WEBP_FORMAT = "format(webp)";
    private static final String SCHEMA_SELECTOR = "script[type=\"application/ld+json\"]";

    /**
     * The test reads TestData and verifies those fields, passing true (og:image, twitter:image, social image (if there's primary image in the document),
     * image in schema, image src and noscript tag). If true, it verifies meta tags content, schema image object and image source are not null or empty, the field
     * contains expected filter, format(webp) (expected on src and noscript) is present.
     */
    protected Consumer<MntlRunner> imageSyntaxTest = runner -> {
        TestingData data = (TestingData) runner.testData();

        // Validating og:image property and twitter:image name contents
        if (data.verifyMetaTags) metaTagsValidations(runner, data.verifyMetaTagsSize);

        // Validating imageObject url in schema
        if (data.verifySchema) {
            List<String> imageUrls = getImageUrlsFromSchema(runner);
            collector.checkThat("There were no image urls found in schema", imageUrls.size(), greaterThan(0));
            for (String url : imageUrls) {
                validateFields("Image url from schema " + url, url, false, data.verifySchemaSize);
            }
        }

        // Validating image src and noscript tag (validate primary image; if no primary image, then check first image on the page)
        if (data.verifySrcAndNoScript) {
            List<WebElementEx> primaryImages = runner.driver().findElementsEx(By.xpath("//img[contains(@class, 'mntl-universal-primary-image')] | //img[contains(@class, 'mntl-primary-image')]"));
            List<WebElementEx> images = runner.driver().findElementsEx(By.xpath("//div[contains(@class,'img-placeholder')]/img"));
            if (primaryImages.size() != 0) {
                primaryImages.get(0).scrollIntoViewCentered();
                validateSrcAndNoScript(primaryImages.get(0), runner, "Primary image", data.verifySrcAndNoScriptSize, data.verifyNoScript);
            } else {
                collector.checkThat("There were no image elements found on the page", images.size(), greaterThan(0));
                for (WebElementEx image : images) {
                    image.scrollIntoViewCentered();
                    if (!image.getAttribute("class").contains("tooltip") && !image.getAttribute("class").contains("card")) {
                        validateSrcAndNoScript(image, runner, "Image", data.verifySrcAndNoScriptSize, data.verifyNoScript);
                        break;
                    }
                }
            }
        }
    };

    /**
     * This method validates og:image and twitter:image fields in the page head.
     */
    protected void metaTagsValidations(MntlRunner runner, boolean verifyMetaTagsSize) {
        MntlMetaTags metaTags = new MntlBasePage<>(runner.driver(), MntlMetaTags.class).getComponent();

        // Validating og:image property content
        if (metaTags.doesMetaOgImageExist()) {
            validateFields("og:image property content", metaTags.ogImage().getAttribute("content"), false, verifyMetaTagsSize);
        } else {
            collector.checkThat("og:image property is not present in the page head", true, is(false));
        }

        // Validating twitter:image name content
        if (metaTags.doesMetaTwitterImageExist()) {
            validateFields("twitter:image name content", metaTags.twitterImage().getAttribute("content"), false, verifyMetaTagsSize);
        } else {
            collector.checkThat("twitter:image name is not present in the page head", true, is(false));
        }
    }

    /**
     * This method validates the field being passed is not null or empty; contains or not contains 'format(webp)' parameter
     * (depends on the boolean passed) and contains expected filter.
     */
    private void validateFields(String message, String field, boolean expectWebpFormat, boolean validateSize) {
        collector.checkThat(message + " is empty or null", field, not(emptyOrNullString()));
        if (expectWebpFormat)
            collector.checkThat(message + " does not contain 'format(webp)' parameter", field, containsString(WEBP_FORMAT));
        else
            collector.checkThat(message + " contains 'format(webp)' parameter", field, not(containsString(WEBP_FORMAT)));
        collector.checkThat(message + " does not contain expected filter " + FILTER, field, containsString(FILTER));
        if (validateSize)
            collector.checkThat(message + " does not contain expected size " + SIZE, field, containsString(SIZE));
    }

    /**
     * This method validates image src, also gets innerHTML from the element passed and validates the noscript src value.
     */
    private void validateSrcAndNoScript(WebElementEx element, MntlRunner runner, String image, boolean verifySrcAndNoScriptSize, boolean verifyNoScript) {
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        validateFields(image, getPathFromURI(element.src()), true, verifySrcAndNoScriptSize);
        if (verifyNoScript) {
            List<WebElement> noscript = element.findElements(By.xpath("following-sibling::noscript"));
            if (noscript.size() > 0) {
                String innerHTML = noscript.get(0).getAttribute("innerHTML");
                if (innerHTML.contains("img")) {
                    String noScriptSrc = getSrcFromNoScript(innerHTML);
                    collector.checkThat("Image src and noscript src paths do not match", getPathFromURI(element.src()), is(getPathFromURI(noScriptSrc)));
                    validateFields("The noscript src value for image", noScriptSrc, true, verifySrcAndNoScriptSize);
                }
            } else {
                collector.checkThat("There was no noscript tag found for the image " + element.src(), true, is(false));
            }
        }
    }

    /**
     * This method is getting the path from the image src.
     */
    private String getPathFromURI(String imageSrc) {
        String path = "";
        URI imageURI;
        try {
            imageURI = new URI(imageSrc);
            path = imageURI.getPath();
        } catch (URISyntaxException e) {
            collector.checkThat("Could not get path from image src", true, is(false));
        }
        return path;
    }

    /**
     * This method looks for the ImageObject in schema and returns all urls in the ImageObject.
     */
    private List<String> getImageUrlsFromSchema(MntlRunner runner) {
        List<String> imageUrls = new ArrayList<>();
        SchemaComponent schemaComp = runner.page().head().findElement(By.cssSelector(SCHEMA_SELECTOR), SchemaComponent::new);
        Object imageJSONObject;
        try {
            imageJSONObject = JsonPath.read(schemaComp.schemaContent(), "$[0].image");
            String imageObjStr = "";
            if (imageJSONObject != null) {
                try {
                    // Converting java object into a JSON String
                    imageObjStr = new ObjectMapper().writeValueAsString(imageJSONObject);
                    imageObjStr = imageObjStr.substring(1, imageObjStr.length() - 1)
                            .replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .replaceAll("\"", "");
                } catch (IOException e) {
                    Assert.fail("Image Object could not be converted to String.");
                }
            }
            String[] imageObjArr = imageObjStr.split("@type:ImageObject,");
            for (int i = 0; i < imageObjArr.length; i++) {
                if (!imageObjArr[i].isEmpty()) {
                    String[] imageObjProperties = imageObjArr[i].split(",");
                    for (int j = 0; j < imageObjProperties.length; j++) {
                        String pair = imageObjProperties[j];
                        if (pair.contains("url")) {
                            imageUrls.add(pair.substring(pair.indexOf("https")));
                        }
                    }
                }
            }
        } catch (Exception e) {
            collector.checkThat("Could not read value for $[0].image from schema", true, is(false));
        }

        return imageUrls;
    }

    /**
     * This method gets noscript value of the element passed and then returns its src value.
     */
    private String getSrcFromNoScript(String innerHTML) {
        String src = "";
        String[] innerHTMLArray = innerHTML.split("\"");
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < innerHTMLArray.length - 1; i += 2) {
            result.put(innerHTMLArray[i].trim(), innerHTMLArray[i + 1].trim());
        }

        for (String entry : result.keySet()) {
            if (entry.contains("src=")) src = result.get(entry).replaceAll("\"", "");
        }

        return src;
    }

    public static class TestingData {
        private Boolean verifyMetaTags = true;
        private Boolean verifyMetaTagsSize = false;
        private Boolean verifySchema = true;
        private Boolean verifySchemaSize = true;
        private Boolean verifySrcAndNoScript = true;
        private Boolean verifyNoScript = true;
        private Boolean verifySrcAndNoScriptSize = true;

        public TestingData() {
        }

        public TestingData verifyMetaTags(Boolean verifyMetaTags) {
            this.verifyMetaTags = verifyMetaTags;
            return this;
        }

        public TestingData verifyMetaTagsSize(Boolean verifyMetaTagsSize) {
            this.verifyMetaTagsSize = verifyMetaTagsSize;
            return this;
        }

        public TestingData verifySchema(Boolean verifySchema) {
            this.verifySchema = verifySchema;
            return this;
        }

        public TestingData verifySchemaSize(Boolean verifySchemaSize) {
            this.verifySchemaSize = verifySchemaSize;
            return this;
        }

        public TestingData verifySrcAndNoScript(Boolean verifySrcAndNoScript) {
            this.verifySrcAndNoScript = verifySrcAndNoScript;
            return this;
        }

        public TestingData verifyNoScript(Boolean verifyNoScript) {
            this.verifyNoScript = verifyNoScript;
            return this;
        }

        public TestingData verifySrcAndNoScriptSize(Boolean verifySrcAndNoScriptSize) {
            this.verifySrcAndNoScriptSize = verifySrcAndNoScriptSize;
            return this;
        }
    }
}
