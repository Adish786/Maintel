package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.driver.selection.Device;
import com.about.venus.core.utils.DataLayerUtils;
import junit.framework.Assert;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.is;

public class MntlRTBTest extends MntlVenusTest implements MntlCommonTestMethods {

     static List<String> rtbLibs = null;

    // Use RTBDataLayerTest from MntlDataLayerTests.Java

    @Deprecated
    @SuppressWarnings("deprecation")
    protected Consumer<MntlRunner> RTBDataLayerTest = (runner) -> {
        final Map<String, Object> analyticsEvent = DataLayerUtils.dataLayerEvents(runner.driver(), "analyticsEvent")
                .get(0);
        collector.checkThat(" For Url :" + runner.driver().getCurrentUrl() + "eventCategory value is not correct",
                analyticsEvent.get("eventCategory"), is("Ad Timing Metrics"));
        JSONParser parser = new JSONParser();
        JSONObject eventLabel = null;
        try {
            eventLabel = (JSONObject) parser.parse(analyticsEvent.get("eventLabel").toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail("could not parse string to json. String : " + analyticsEvent.get("eventLabel").toString());
        }
        if (MntlRTBTest.rtbLibs == null)
            Assert.fail("rtbLibs needs be set via each vertical  using setExpectedRrbLibs method");
        for (String lib : MntlRTBTest.rtbLibs) {
            collector.checkThat(
                    " For Url :" + runner.driver().getCurrentUrl() + " event lable does not contain lib :" + lib,
                    eventLabel.keySet().contains(lib), is(true));
        }

    };

    protected void setExpectedRrbLibs(List<String> libs) {
        MntlRTBTest.rtbLibs = libs;
    }


//     * convenience method to call venus test and initialize other variables
//     *
//     * @param device
//     *
//     * @param url
//     *
//     * @param test that we want to run
//     *
//     * @param geolocation name value pair
//     *

    public void runTest(Device device, String url, Consumer<MntlRunner> test, List<NameValuePair> geoLoaction) {
        test(devices(device), (driver, proxy) -> {
            startTest(url, driver).withAds().withProxyAndHeaders(proxy, geoLoaction).loadUrl().runTest(test);
        });
    }

}