package com.about.mantle.commons;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.driver.selection.DriverSelection.Matcher;
import com.about.venus.core.driver.selection.Platform;
import com.about.venus.core.utils.CaptureScreen;
import com.about.venus.core.utils.ConfigurationProperties;
import junit.framework.Assert;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.about.venus.core.driver.selection.Device.PC;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

/**
 * Will provide methods for running a image test against a verticals page
 * 
 * Example usage from vertical:
 *
 * @Test
 * @TestDescription(device = { Device.PC, Device.Mobile_ChromeEmulator }, page = VideoPage.class, desc = "visual test for finance video page")
 * @Category({ VisualTests.class })
 * public void VideoPageTest() {
 * test(browsers(Chrome), driver -> financeVisualTest(VIDEO.data(), driver, dummyComps, deleteComps, hideComps), condition(!isProd()));
 * }
 * 
 * private void financeVisualTest(Document data, WebDriverExtended driver, List<String> dummyComps, List<String> delComps, List<String> hideBlocks) {
 * String url = data.url();
 * String template = data.template + "_" + device(driver);
 * List<NameValuePair> adDisabled = Arrays.asList(new BasicNameValuePair("globeNoTest", ""), new BasicNameValuePair("google_nofetch", "true"));
 * startTest(url, driver)
 * .withCaptureScreen(template)
 * .customCode(() -> driver.manage().window().fullscreen())
 * .withQueryParams(adDisabled)
 * .loadUrl()
 * .hideScrollbarAndMouse()
 * .fullPageScroll()
 * .deleteComponents(delComps)
 * .dummyComponents(dummyComps)
 * .hideBlocks(hideBlocks)
 * .takeFullPageScreenshot(2000)
 * .scrollTop()
 * .withQueryParams(adDisabled)
 * .loadAbsoluteUrl()
 * .hideScrollbarAndMouse()
 * .fullPageScroll()
 * .deleteComponents(delComps)
 * .dummyComponents(dummyComps)
 * .hideBlocks(hideBlocks)
 * .takeFullPageScreenshot(2000)
 * .compareImages();
 * }
 */
@SuppressWarnings("unchecked")
public interface MntlVisualCommonTestMethods extends MntlCommonTestMethods<MntlVisualCommonTestMethods.Runner> {

    final Device actualDevice = ConfigurationProperties.getDevice(Device.PC);

    /**
     * Chapters test starts here
     *
     * @param path   Url that needs to be tested
     * @param driver Driver instance
     * @return runner
     */
    default Runner startBaseUrl(String path, WebDriverExtended driver) {
        return new Runner(path, driver);
    }

    public default Matcher getVisualDeviceType() {

        if (actualDevice.is(Device.iPhone6_ChromeEmulator)) {
            return Matcher.devices(Device.iPhone6_ChromeEmulator);
        } else if (actualDevice.is(Device.PC)) {
            return Matcher.devices(Device.PC);
        } else if (actualDevice.is(Device.iPad_ChromeEmulator)) {
            return Matcher.devices(Device.iPad_ChromeEmulator);
        } else if (actualDevice.is(Device.iPhoneX_ChromeEmulator)) {
            return Matcher.devices(Device.iPhoneX_ChromeEmulator);
        }
        return null;
    }

    default Runner startTestUrl(String path, WebDriverExtended driver) {
        String baseUrlUnderTest = ConfigurationProperties.getTargetProjectBaseUrl(null);
        if (baseUrlUnderTest == null) Assert.fail("Target  url unser test is not provided in parameters");
        return new Runner(baseUrlUnderTest + path, driver);
    }

    class Runner extends MntlCommonTestMethods.Runner<Runner> {
        private CaptureScreen captureScreen;
        private String visualTestId;
        private WebElementEx elementCapture;
        private WebElementEx clickableElement;

        public Runner(String url, WebDriverExtended driver) {
            super(url, driver);
        }

        private static boolean hasRetinaDisplay() {

            return (getScaleFactor() == 2.0); // 1 indicates a regular mac display.

        }

        private static float getScaleFactor() {
            float scaleFactor = 1;

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice device = env.getDefaultScreenDevice();

            try {
                Field field = device.getClass().getDeclaredField("scale");
                if (field != null) {
                    field.setAccessible(true);
                    Object scale = field.get(device);

                    if (scale instanceof Integer) {
                        scaleFactor = (Integer) scale;
                    }
                }
            } catch (Throwable e) {
                // Ignore
            }

            return scaleFactor;
        }

        static Logger logger() {
            return LoggerFactory.getLogger(MntlVisualCommonTestMethods.class);
        }

        /**
         * load url and get pageObject for this test
         *
         * @return runner
         */

        public Runner loadUrl(boolean isProctorTest) {
            this.driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            List<NameValuePair> adDisabled = Arrays.asList(new BasicNameValuePair("globeNoTest", "true"), new BasicNameValuePair("google_nofetch", "true"));
            String AD_QUERY_PARAM = "?kw=mdpAdDebug,true&globeNoTest=true";
            String urlToLoad = "";
            if (isProctorTest) {
                urlToLoad = new MntlUrl(this.url, true).url() + AD_QUERY_PARAM;
            } else {
                urlToLoad = new MntlUrl(this.url, adDisabled).url();
            }
            if (this.dimension == null) {
                if (this.driver.getDriverConfig().getDevice() == PC){
                    this.driver.manage().window().fullscreen();
                }
                this.driver.getAbsoluteUrl(urlToLoad);
            }
            try {
                this.driver.getAbsoluteUrl(urlToLoad);
                logger().info(String.format("Navigating to url: %s", urlToLoad));
            } catch (TimeoutException e) {
                e.printStackTrace();
                for (int i = 0; i < 10; i++) {
                    if (stopLoading())
                        break;
                }
            }
            this.driver.navigate().refresh();
            this.page = new MntlBasePage<>(driver, null);
            this.stopPageRefreshPeriodically.start();
            return this;
        }

        /**
         * run this test with given browser dimension
         *
         * @return runner
         */
        public Runner fullPageScroll() {
            Long scrollPos = (long) 0;
            while (true) {
                page.scroll().scrollBy(0, (this.driver.returnDriver().manage().window().getSize().height) / 20);
                page.waitFor().exactMoment(100, TimeUnit.MILLISECONDS);
                if (scrollPos.equals(page.scroll().currentPosition())) {
                    break;
                }
                scrollPos = page.scroll().currentPosition();
            }
            return this;
        }

        public Runner fullPageScroll(boolean flag) {
            if (flag) return fullPageScroll();
            return this;
        }

        public Runner elementScrollTo(WebElementEx ele) {
            ele.scrollIntoViewCentered();
            return this;
        }

        public Runner scrollTop() {
            page.scroll().top();
            page.waitFor().exactMoment(100, TimeUnit.MILLISECONDS);

            return this;
        }

        /**
         * Provide custom code which can be used alongside any of the premade functions
         *
         * example:
         *
         * Runnable code = () -> {
         * WebElement ele = driver.findElementEx(By.className("user-menu__icon")).getWrappedElement();
         * ele.click();
         * driver.waitFor(1, TimeUnit.SECONDS);
         * };
         *
         * startTest(url, driver).customCode(code)
         *
         * @param code Runnable
         * @return
         */
        public Runner customCode(Runnable code) {
            code.run();
            return this;
        }

        /**
         * run this test with CaptureScreen
         *
         * @return runner
         */
        public Runner withCaptureScreen(CaptureScreen captureScreen) {
            this.captureScreen = captureScreen;
            return this;
        }

        public Runner locateElement(Object loc) {
            if (loc instanceof WebElementEx) {
                this.elementCapture = (WebElementEx) loc;
            } else if (loc instanceof By) {
                this.elementCapture = this.driver.findElementEx((By) loc);
            }
            return this;
        }

        public Runner clickElement(Object loc, boolean click) {
            if (click) {
                if (loc instanceof WebElementEx) {
                    this.clickableElement = (WebElementEx) loc;
                } else if (loc instanceof By) {
                    this.clickableElement = this.driver.findElementEx((By) loc);
                }
                clickableElement.scrollIntoViewCentered();
                clickableElement.waitFor().aMoment();
                clickableElement.click();
            }
            return this;
        }

        public Screenshot captureElement() {
            this.elementCapture.waitFor().exactMoment(2, TimeUnit.SECONDS);
            this.elementCapture.scrollIntoView("true");
            return captureElement(this.driver, this.elementCapture);
        }

        public Screenshot captureElement(WebDriverExtended driver, WebElementEx element) {
            element.waitFor().exactMoment(2, TimeUnit.SECONDS);
            element.scrollIntoView("true");
            element.waitFor().exactMoment(2, TimeUnit.SECONDS);
            return captureWebElement(driver, element.getWrappedElement());
        }

        public Screenshot captureWebElement(WebDriverExtended driver, WebElement element) {
            AShot ashot = new AShot();
            Long devicePixelRatio = driver.executeScript("return window.devicePixelRatio");
            FixedCutStrategy cutStrategy = new FixedCutStrategy(0, 0);
            ShootingStrategy strategy;
            if (ConfigurationProperties.getPlatform(Platform.OSX).is(Platform.OSX) && hasRetinaDisplay()) {
                strategy = ShootingStrategies.viewportRetina(ShootingStrategies.simple(), 500, cutStrategy, devicePixelRatio);
            } else {
                strategy = ShootingStrategies.viewportNonRetina(ShootingStrategies.simple(), 500, cutStrategy);
            }
            if (ConfigurationProperties.getDevice(PC).is(Device.PC)) {
                return ashot.shootingStrategy(strategy).coordsProvider(new WebDriverCoordsProvider())
                        .takeScreenshot(driver.returnDriver(), element);
            } else {
                return ashot.shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(devicePixelRatio), 1000))
                        .takeScreenshot(driver.returnDriver(), element);

            }
        }

        /**
         * Will grey out all of the provided locators
         *
         * @param locators
         * @return
         */
        public Runner dummyComponents(List<String> locators) {
            for (String loc : locators) {
                String javascript =
                        "document.querySelectorAll('" + loc + "').forEach(child => { " +
                                "  var overlayDiv = document.createElement('div'); " +
                                "  overlayDiv.style.background = 'grey'; " +
                                "  overlayDiv.style.position = 'absolute'; " +
                                "  var rect = child.getBoundingClientRect(); " +
                                "  overlayDiv.style.top=(rect.top + window.scrollY) + 'px'; " +
                                "  overlayDiv.style.bottom=(rect.bottom + window.scrollY) + 'px'; " +
                                "  overlayDiv.style.right=(rect.right + window.scrollX) + 'px'; " +
                                "  overlayDiv.style.left=(rect.left + window.scrollX) + 'px'; " +
                                "  overlayDiv.style.height=(rect.bottom - rect.top) + 'px'; " +
                                "  overlayDiv.style.width=(rect.right - rect.left) + 'px'; " +
                                "  overlayDiv.style.opacity='1'; " +
                                "  document.body.appendChild(overlayDiv); " +
                                "})";
                this.driver.executeScript(javascript);
                page.waitFor().aMoment(200, TimeUnit.MILLISECONDS);

            }
            return this;
        }

        /**
         * Removes the components from the dom
         *
         * @param locators
         * @return
         */
        public Runner deleteComponents(List<String> locators) {
            for (String loc : locators) {
                deleteComponent(loc);
            }
            return this;
        }

        /**
         * Will delete the component from the dom
         *
         * @param selector
         * @return
         */
        public Runner deleteComponent(String selector) {
            this.driver.executeScript("document.querySelectorAll('" + selector + "').forEach(ele => { ele.remove(); })");
            page.waitFor().aMoment(200, TimeUnit.MILLISECONDS);
            return this;
        }

        /**
         * Will hide the component by setting by setting opacity=0
         *
         * @param locators
         * @return
         */
        public Runner hideBlocks(List<String> locators) {
            for (String loc : locators) {
                String command = "document.querySelectorAll('" + loc + "').forEach(ele => { ele.style.opacity=0; })";
                this.driver.executeScript(command);
                page.waitFor().aMoment(100, TimeUnit.MILLISECONDS);

            }
            return this;
        }

        public Runner hideElement(String loc) {
            String command = "document.querySelectorAll('" + loc + "').forEach(ele => { ele.style.opacity=0; })";
            this.elementCapture.executeScript(command);
            page.waitFor().aMoment(100, TimeUnit.MILLISECONDS);
            return this;
        }

        public Runner hideAttribute(Map<String, String> hideAttributes) {
            for (Map.Entry<String, String> entry : hideAttributes.entrySet()) {
                String command = "document.querySelectorAll('" + entry.getKey() + "').forEach(ele => { ele.removeAttribute('" + entry.getValue() + "'); })";
                this.driver.executeScript(command);
                page.waitFor().aMoment(100, TimeUnit.MILLISECONDS);
            }
            return this;
        }

        public Runner setAttribute(Map<String, String> setAttributes,  List<String> values) {
        	int index = 0;
            for (Map.Entry<String, String> entry : setAttributes.entrySet()) {
                String command = "document.querySelectorAll('" + entry.getKey() + "').forEach(ele => { ele.setAttribute('" + entry.getValue() + "','"+values.get(index)+"'); })";
                this.driver.executeScript(command);
                page.waitFor().aMoment(100, TimeUnit.MILLISECONDS);
                index++;
            }
            return this;
        }
        
        /**
         * Will hide the scrollbar and mouse as well make it such that the mouse pointers do not affect the page (i.e. when scrolling the mouse going over
         * links will affect screenshots)
         *
         * @return
         */
        public Runner hideScrollbarAndMouse() {
            this.driver.executeScript("document.body.style.pointerEvents='none';");
            this.driver.executeScript("document.body.style.cursor='none';");
            this.driver.executeScript("var style = document.createElement(\"style\"); style.innerHTML = \"::-webkit-scrollbar { display: none;}\" ; document.body.appendChild(style);");
            return this;
        }

        /**
         * Take a full page screenshot for comparision
         *
         * @param scrollTimeOut
         * @return
         */
        public Runner takeFullPageScreenshot(int scrollTimeOut) {
            this.driver.executeScript("el = document.querySelector('#feedbackify'); if(el) { el.remove(); }");
            this.captureScreen.captureFullPageScreenshot(this.driver, scrollTimeOut);
            return this;
        }

        public Runner takeElementScreenshot(WebElementEx element) {
            element.scrollIntoViewCentered();
            this.driver.executeScript("el = document.querySelector('#feedbackify'); if(el) { el.remove(); }");
            this.captureScreen.captureScreenshot(this.driver, element);
            return this;
        }

        /**
         * compare images for visual test
         *
         * @return runner
         */
        public Runner compareImages() {
            assertThat("visual test failed for " + this.visualTestId, this.captureScreen.compareImages(), is(false));
            return this;
        }

        @Deprecated // use loadVisualBaseUrl instead - will be removed in 3.12
        public Runner loadAbsoluteUrl() {
            return loadVisualBaseUrl();
        }

        /**
         * Loads the base url for visual comparision
         *
         * @return
         */
        public Runner loadVisualBaseUrl() {
            String venusTargetProjectBaseUrl = ConfigurationProperties.getTargetProjectBaseUrl(null);
            assertThat("Need to provide entry for venusTargetProjectBaseUrl", venusTargetProjectBaseUrl, not(emptyOrNullString()));

            // If the test has provided venusVisualTestTargetUrl - use that - otherwise get the prod url of the site and use that
            String venusVisualTestTargetUrl = ConfigurationProperties.getVenusVisualTestTargetUrl(null);
            if (venusVisualTestTargetUrl == null || venusVisualTestTargetUrl.isEmpty()) {
                venusVisualTestTargetUrl = new MntlUrl(url).getProdUrl();
            }

            String prodUrl = url.replace(venusTargetProjectBaseUrl, venusVisualTestTargetUrl);
            this.driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            if (this.dimension == null) this.driver.manage().window().maximize();
            if (this.queryParams != null) prodUrl = this.driver.urlFromNvp(prodUrl, queryParams);
            logger().info(String.format("Navigating to url: %s", prodUrl));
            this.driver.get(prodUrl, adsEnabled);
            page = new MntlBasePage<>(driver, null);
            return this;
        }

        /**
         * run standard test
         *
         * @param test a base page test
         * @param <T>  page data type
         */
        public <T extends MntlPage> void runTest(BiConsumer<WebDriverExtended, T> test) {
            assertThat("must call loadUrl before runTest", page, is(notNullValue()));
            test.accept(driver, (T) page);
        }

    }
}