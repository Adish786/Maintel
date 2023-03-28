package com.about.mantle.components.ads;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;


public class MntlPrebidCsvTest extends MntlRTBTests {
    private static final String SCRIPT = "script";
    private static final String TYPE_VALUE = "text/javascript";
    private static final String SET_CONFIG = "Mntl.RTB.Plugins.prebid.setConfig";
    private static final String INIT_DISPLAY_AND_OUT_OF_STREAM_BIDDERS = "Mntl.RTB.initDisplayAndOutstreamBidders";
    private static final String TRACKING_INIT = "Mntl.RTBTracking.init";
    private static final String INIT_VIDEO_BIDDERS = "Mntl.RTB.initVideoBidders";
    private static final String TIMEOUT_LENGTH = "Mntl.RTB.setTimeoutLength";
    private static final String MEDIA_GRID = "grid";
    private static final String INDEX_EXCHANGE = "ix";
    private static final String CRITEO_PG = "criteopg";
    private static final String CRITEO = "criteo";
    private static final String RUBICON = "rubicon";
    private static final String RUBICON_PG = "pgRubicon";
    private final String initBidderValue = "{ type: \"amazon\", id: '3222'},{ type: \"prebid\", id: 'true'}";
    private final String rtbTrackingValue = "'amazon','lotameLightning','ixid','prebid'";


    protected List<List<String>> csvReader(String vertical) throws IOException {
        String path = "src/main/resources/prebid.csv";
        String line = "";
        List<List<String>> listOfCsvVoices = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values[0].equals(vertical)) {
                listOfCsvVoices.add(Arrays.asList(values));
            }
        }
        return listOfCsvVoices;
    }

    protected BiConsumer<Runner, String> prebidCsvValidation = (runner, vertical) -> {
        List<List<String>> verticalsBidderValues = new ArrayList<>();
        try {
            verticalsBidderValues = csvReader(vertical);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> valuesForDevice = new ArrayList<>();
        for (int i = 0; i < verticalsBidderValues.size(); i++) {
            if (desktop(runner.driver())) {
                if (verticalsBidderValues.get(i).get(1).contains("pc")) {
                    valuesForDevice.add(verticalsBidderValues.get(i).get(5).equals("Criteo") ? CRITEO :
                            verticalsBidderValues.get(i).get(5).equals("CriteoPG") ? CRITEO_PG :
                                    verticalsBidderValues.get(i).get(5).equals("MediaGrid") ? MEDIA_GRID :
                                            verticalsBidderValues.get(i).get(5).equals("Index Exchange") ? INDEX_EXCHANGE :
                                                    verticalsBidderValues.get(i).get(5).equals("RubiconPG") ? RUBICON_PG : RUBICON);
                }
            } else {
                if (verticalsBidderValues.get(i).get(1).contains("mobile")) {
                    valuesForDevice.add(verticalsBidderValues.get(i).get(4).equals("Criteo") ? CRITEO :
                            verticalsBidderValues.get(i).get(4).equals("CriteoPG") ? CRITEO_PG :
                                    verticalsBidderValues.get(i).get(4).equals("MediaGrid") ? MEDIA_GRID :
                                            verticalsBidderValues.get(i).get(4).equals("Index Exchange") ? INDEX_EXCHANGE :
                                                    verticalsBidderValues.get(i).get(4).equals("RubiconPG") ? RUBICON_PG : RUBICON);
                }
            }
        }
        collector.checkThat("Values for device list is empty, please check the data...", valuesForDevice.size(), greaterThan(0));
        for (String i : domJsContent(runner, SCRIPT, TYPE_VALUE, SET_CONFIG)) {
            if (i.contains(SET_CONFIG)) {
                for (int k = 0; k < valuesForDevice.size(); k++) {
                    collector.checkThat("bidders are not set up correctly", i.contains(valuesForDevice.get(k)), is(true));
                }
            }
        }
    };

    protected static String[] domJsContent(Runner runner, String firstLocator, String typeAttributeValue, String secondLocator) {
        Document doc = null;
        try {
            doc = Jsoup.connect(runner.url()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements jsHtmlFunctions = doc.select(firstLocator).attr("type", typeAttributeValue);
        String[] object = null;
        for (Element i : jsHtmlFunctions) {
            if (i.toString().contains(secondLocator)) {
                object = i.toString().split(";");
            }
        }
        return object;
    }

    protected Consumer<Runner> rtbLoadingValidation = runner -> {
        String bidders_init = "";
        for (String i : domJsContent(runner, SCRIPT, TYPE_VALUE, INIT_DISPLAY_AND_OUT_OF_STREAM_BIDDERS)) {
            if (i.contains(INIT_DISPLAY_AND_OUT_OF_STREAM_BIDDERS)) {
                bidders_init = i;
                    collector.checkThat("expected value to contain " + initBidderValue + " but contained " + i,bidders_init.contains(initBidderValue), is(TRUE));
            }
        }
    };

    protected Consumer<Runner> libraryLoadTimeOut = runner -> {
        for (String i : domJsContent(runner, SCRIPT, TYPE_VALUE, TIMEOUT_LENGTH)) {
            if (i.contains(TIMEOUT_LENGTH)) {
                if (mobile()) {
                    collector.checkThat("mobile timeout bidding time is not set up correctly",i.contains("900,1000"), is(TRUE));
                } else {
                    collector.checkThat("desktop timeout bidding time is not set up correctly",i.contains("500,800"), is(TRUE));
                }
            }
        }
    };

    protected Consumer<Runner> videoBidding = runner -> {
        for (String i : domJsContent(runner, SCRIPT, TYPE_VALUE, INIT_VIDEO_BIDDERS)) {
            if (i.contains(INIT_VIDEO_BIDDERS)) {
                collector.checkThat("video bidders are not showing in the dom",i.contains("type: 'amazon', id: '3222'"), is(TRUE));
            }
        }
    };

    protected Consumer<Runner> libraryLoad = runner -> {
        for (String i : domJsContent(runner, SCRIPT, TYPE_VALUE, TRACKING_INIT)) {
            if (i.contains(TRACKING_INIT)) {
                collector.checkThat("expected value to contain " + rtbTrackingValue + " but contained " + i, i.contains(rtbTrackingValue), is(TRUE));
            }
        }
    };

}
