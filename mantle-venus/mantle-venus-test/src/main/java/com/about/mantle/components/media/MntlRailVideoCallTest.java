package com.about.mantle.components.media;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.components.ads.MntlRTBTests;
import com.about.mantle.venus.model.components.ads.AdCallObject;
import com.about.mantle.venus.model.components.media.MntlRightRailVideoComponent;
import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.proxy.VenusHarRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.containsString;

public class MntlRailVideoCallTest extends MntlRTBTests implements MntlCommonTestMethods {

    protected static final String LIBRARY = "jwplatform.com/libraries";
    protected static final String RAIL_VIDEO_ENTRY = "https://content.jwplatform.com/libraries/";
    protected static final String MEDIAGRID = "hbnm?size";
    protected static final String RUBICON = "prebid-server.rubiconproject.com/openrtb2/auction";

    static final String SZ = "sz";
    static final String IU = "iu";
    static final String CP = "1";
    static final String SLOT = "slot";
    static final String MTP = "mpt";
    static final String TAX0 = "tax0";
    static final String AU = "au";
    static final String SBJ = "sbj";
    static final String LEADID = "leaid";
    static final String DLOAD = "dload";
    static final String LEUID = "leuid";
    static final String AB = "ab";
    static final String BTS = "bts";
    static final String VID = "vid";
    static final String JNY = "jny";
    static final String JNYROOT = "jnyroot";

    protected static Map<String, List<String>> commonVideoCallValues (Map<String, List<String>> adCallValues){
        adCallValues.put(SZ, Collections.singletonList("300x170"));
        adCallValues.put(SLOT, Collections.singletonList("video-rail"));
        adCallValues.put(MTP, Collections.singletonList("jwplayer"));
        adCallValues.put(AU, Collections.singletonList(""));
        adCallValues.put(SBJ, Collections.singletonList(""));
        adCallValues.put(LEADID, Collections.singletonList(""));
        adCallValues.put(DLOAD, Collections.singletonList("0"));
        adCallValues.put(LEUID, Collections.singletonList(""));
        adCallValues.put(AB, Collections.singletonList(""));
        adCallValues.put(BTS, Collections.singletonList(""));
        adCallValues.put(VID, Collections.singletonList(""));
        adCallValues.put(JNY, Collections.singletonList(""));
        adCallValues.put(JNYROOT, Collections.singletonList(""));
        return adCallValues;
    }

    protected Consumer<Runner> PCRightRailVideoAdCallTest = runner -> {
        MntlRightRailVideoComponent videoDoc = (MntlRightRailVideoComponent) runner.component();
        videoDoc.rrVideoTitle().scrollIntoViewCentered();
        HashMap<String, List<String>> adCallValues = new HashMap<>();
        commonVideoCallValues(adCallValues);
        UrlParams adCallParams = new AdCallObject(runner.proxy(), runner.proxyPage(), runner.driver(), adCallValues, "jwplayer").urlParams(collector);
        testAdCallParams(runner,adCallParams, adCallValues, collector);
    };

    protected Consumer<Runner> rightRailVideoLibrary = (runner) -> {
        MntlRightRailVideoComponent component = (MntlRightRailVideoComponent) runner.component();
        component.loadRRVideo(component.rrVideoTitle());
        VenusHarRequest videoReq = callsList(runner.page(), runner.proxy(), runner.proxyPage(), 1, LIBRARY);
        collector.checkThat("jw is not made", videoReq.url().url(), containsString(RAIL_VIDEO_ENTRY));
    };

    protected Consumer<MntlRunner> mediagridForVideoCall = (runner) -> {
        MntlRightRailVideoComponent component = (MntlRightRailVideoComponent) runner.component();
        component.loadRRVideo(component.rrVideoTitle());
        VenusHarRequest videoReq = callsList(runner.page(), runner.proxy(), runner.proxyPage(), 1, MEDIAGRID);
        collector.checkThat("international bidding ad call wasn't made", videoReq.url().url(), containsString(MEDIAGRID));
    };

    protected Consumer<MntlRunner> rubiconForVideoCall = (runner) -> {
        MntlRightRailVideoComponent component = (MntlRightRailVideoComponent) runner.component();
        component.loadRRVideo(component.rrVideoTitle());
        VenusHarRequest videoReq = callsList(runner.page(), runner.proxy(), runner.proxyPage(), 1, RUBICON);
        collector.checkThat("international bidding ad call wasn't made", videoReq.url().url(), containsString(RUBICON));
    };


}
