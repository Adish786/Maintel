package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.global.MntlFooterComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.RTBParams;
import com.about.mantle.venus.utils.RTBTax;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.about.mantle.components.ads.MntlTaxRtbTest.TestData.SlotInfo;

/**
 * This test provides verticals ability to check that the amazon pmp rtb calls are happening as expected.
 * To manually check this functionality a user would do the following:
 * 1. Go to endpoint with chrome networking tab open and record traffic
 * 2. in page, slowly scroll to bottom. Giving ads time to load
 * 3. filter network requests using bid?
 * 4. Under the entries - take a look at the slots and pj params
 *
 * The automation will be doing checks to make sure those entries match.
 * - That all expected slot entries appear as expected
 * - That there are no unhandled slot entries
 * - That all of the Taxonomy values (setSi, setTax0, setTax1, setTax2, setTax3) match expected values
 *
 * Example:
 * SlotInfo leaderboard = new TestData.SlotInfo("leaderboard", new ArrayList<>(Arrays.asList("728x90", "970x250", "970x90")), "{}");
 * SlotInfo billboard = new TestData.SlotInfo("billboard", new ArrayList<>(Arrays.asList("300x250", "300x600", "160x600", "300x250", "300x1050")), "{}");
 * SlotInfo billboard2 = new TestData.SlotInfo("billboard2", new ArrayList<>(Arrays.asList("300x250", "300x600", "300x601", "160x600", "300x251")), "{}");
 * SlotInfo billboardfooter2 = new TestData.SlotInfo("billboardfooter2",new ArrayList<>(Arrays.asList("300x250")), "{}");
 * SlotInfo adhesive = new TestData.SlotInfo("adhesive",new ArrayList<>(Arrays.asList("320x50")), "{}");
 * SlotInfo leaderboardac = new TestData.SlotInfo("leaderboardac",new ArrayList<>(Arrays.asList("728x90", "970x250")), "{}");
 * SlotInfo leaderboardfooter = new TestData.SlotInfo("leaderboardfooter",new ArrayList<>(Arrays.asList("728x90","728x98","970x250","970x258")), "{}");
 * SlotInfo leaderboardfooter2 = new TestData.SlotInfo("leaderboardfooter2",new ArrayList<>(Arrays.asList("728x90","728x99","970x250","970x259")), "{}");
 *
 * public void ScTaxRtbTest() {
 *    test(devices(Device.Any), (driver, proxy) -> {
 *    String url = SC_ARTICLE_LONG.url() + "?kw=" + (mobile(driver) ? "dd17" : "dd5");
 *    startTest(url, driver)
 *       .withAds()
 *       .withProxy(proxy)
 *       .loadUrl()
 *       .runTest(runner -> {
 *           TestData data = new TestData()
 *           .setSi("bal_money-over-55")
 *           .setTax0("bal_root")
 *           .setTax1("bal_personal-finance")
 *           .setTax2("bal_money-over-55")
 *           .setTax3("bal_before-retirement");
 *           if(desktop(driver)) {
 *               data.addSlotInfo(leaderboard)
 *                 .addSlotInfo(billboard)
 *                 .addSlotInfo(billboard2)
 *                 .addSlotInfo(leaderboardac)
 *                 .addSlotInfo(leaderboardfooter)
 *                 .addSlotInfo(leaderboardfooter2);
 *           }
 *           if(mobile(driver)) {
 *               data.addSlotInfo(adhesive)
 *                 .addSlotInfo(billboard)
 *                 .addSlotInfo(billboardfooter)
 *                 .addSlotInfo(billboardfooter2);
 *           }
 *           if(tablet(driver)) {
 *               data.addSlotInfo(leaderboard)
 *                 .addSlotInfo(billboard)
 *                 .addSlotInfo(billboard2)
 *                 .addSlotInfo(leaderboardac)
 *                 .addSlotInfo(leaderboardfooter)
 *                 .addSlotInfo(leaderboardfooter2);
 *           }
 *          rtbTest.accept(runner, data);
 *       });
 *   });
 * }
 * @param <T>
 */
public class MntlTaxRtbTest <T extends MntlCommonTestMethods.Runner<T>>
        extends MntlVenusTest
        implements MntlCommonTestMethods<T> {

    /**
     * Test data used to verify rtb values
     */
    static public class TestData {
        public static class SlotInfo {
            private ArrayList<String> s;
            private String sd;
            private String kv;
            public SlotInfo(String sd, String s, String kv) {
                this(sd, jsonStringToArray(s), kv);
            }
            public SlotInfo(String sd, ArrayList<String> s, String kv) {
                this.sd = sd;
                this.s = s;
                this.kv = kv;
            }
        }
        private ArrayList<SlotInfo> slotInfo = new ArrayList<>();
        private String tax0Param;
        private String tax1Param;
        private String tax2Param;
        private String tax3Param;
        private String siParam;
        private String bbParam; //Note: currently handled as string , future implementation might need ArrayList<String>
        public TestData() { }
        public TestData addSlotInfo(String sd, String s, String kv) {
            ArrayList<String> sArray = jsonStringToArray(s);
            SlotInfo info = new SlotInfo(sd, sArray, kv);
            this.slotInfo.add(info);
            return this;
        }
        public TestData addSlotInfo(String sd, ArrayList<String> s, String kv) {
            SlotInfo info = new SlotInfo(sd, s, kv);
            this.slotInfo.add(info);
            return this;
        }
        public TestData addSlotInfo(SlotInfo info) {
            this.slotInfo.add(info);
            return this;
        }
        public TestData setTax0(String tax0Param) {
            this.tax0Param = tax0Param;
            return this;
        }
        public TestData setTax1(String tax1Param) {
            this.tax1Param = tax1Param;
            return this;
        }
        public TestData setTax2(String tax2Param) {
            this.tax2Param = tax2Param;
            return this;
        }
        public TestData setTax3(String tax3Param) {
            this.tax3Param = tax3Param;
            return this;
        }
        public TestData setSi(String siParam) {
            this.siParam = siParam;
            return this;
        }
        public TestData setBb(String bbParam) {
            this.bbParam = bbParam;
            return this;
        }
    }

    /**
     * Will check the tax rtb values against a set of TestData
     */
    protected BiConsumer<Runner, TestData> rtbTest = (runner, data) -> {
        MntlBasePage page = new MntlBasePage<>(runner.driver(), MntlFooterComponent.class);
     
        if(page.componentExists(MntlFooterComponent.class)) {
            MntlFooterComponent footer = (MntlFooterComponent)page.getComponent();
            slowScroll(footer, runner.page(), 300, 500);
        } else {
        	
            runner.driver().executeScript("return window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth' });");
        }

        // Loop thru all of the VenusHarRequests
        // See if all of the slot info is handled
        ArrayList<SlotInfo> needToFind = new ArrayList<>(data.slotInfo);
        ArrayList<SlotInfo> discoveredSlotInfo = new ArrayList<>();
        ArrayList<String> fieldErrors = new ArrayList<>();

        List<VenusHarEntry> entries = runner
            .proxy()
            .capture()
            .page(runner.proxyPage())
            .entries("bid?")
            .stream()
            .filter(entry -> entry.request().url().url().contains("dtb"))
            .collect(Collectors.toList());

        entries.forEach(s -> {
            VenusHarRequest req = s.request();
            RTBParams adreq = new RTBParams(req);
            for (int i = 0; i < adreq.slotsArray().length(); i++) {
                RTBTax tax = adreq.getSlotsArrayWithIndex(i);
                String param_s = tax.s();
                String param_sd = tax.sd();
                String param_kv = tax.kv();
                SlotInfo info = new SlotInfo(param_sd, param_s, param_kv);

                // Store any unique slot info - want to make sure we check they are all handled in test
                boolean unique = true;
                for (SlotInfo cur : discoveredSlotInfo) {
                    if (info.kv.equals(cur.kv) && info.sd.equals(cur.sd)) {
                        boolean allSizedFound = true;
                        for (String info_s : info.s) {
                            if(!cur.s.contains(info_s)) {
                                allSizedFound = false;
                            }
                        }
                        if(allSizedFound) unique = false;
                    }
                }
                if (unique) {
                    discoveredSlotInfo.add(info);
                }
            }
            // Check for the field params for all of the requests, if any errors then add error
            updateFieldErrors(fieldErrors, "tax0 parameter is not correct", data.tax0Param, adreq.pjTax0());
            updateFieldErrors(fieldErrors, "tax1 parameter is not correct", data.tax1Param, adreq.pjTax1());
            updateFieldErrors(fieldErrors, "tax2 parameter is not correct", data.tax2Param, adreq.pjTax2());
            updateFieldErrors(fieldErrors, "tax3 parameter is not correct", data.tax3Param, adreq.pjTax3());
            updateFieldErrors(fieldErrors, "si section is not correct", data.siParam, adreq.pjSisection());
            if (data.bbParam != null) {
            	updateFieldErrors(fieldErrors, "bb section is not correct", data.bbParam, adreq.bbParams());
            }


            Iterator itr = needToFind.iterator();
            while (itr.hasNext()) {
                SlotInfo cur = (SlotInfo) itr.next();
                for (SlotInfo info : discoveredSlotInfo) {
                    if (info.kv.equals(cur.kv) && info.sd.equals(cur.sd)) {
                        // Need to check the dimensions returned to see if they exist
                        boolean allSizedFound = true;
                        for (String discovered_s : info.s) {
                            if(!cur.s.contains(discovered_s)) {
                                allSizedFound = false;
                            }
                        }
                        if(allSizedFound) itr.remove();
                    }
                }
            }
        });

        // Check to make sure all of the slot info we expect is appearing
        if(needToFind.size() != 0) {
            StringBuilder slotsMessage = new StringBuilder();
            for (SlotInfo info : needToFind) {
                slotsMessage.append(String.format("did not find slot info. sd:%s, s:%s, kv: %s\n", info.sd, info.s.toString(), info.kv));
            }
            collector.addError(new Error(String.format("Slot Errors:\n%s", slotsMessage.toString())));
        }

        // Find any field issues
        if(fieldErrors.size() != 0) {
            collector.addError(new Error(String.format("Field Errors:\n%s", String.join("\n", fieldErrors))));
        }

        // And show any slot info that we didn't include in test
        Iterator itr = discoveredSlotInfo.iterator();
        while (itr.hasNext()) {
            SlotInfo cur = (SlotInfo)itr.next();
            for(SlotInfo info : data.slotInfo) {
                if(cur.kv.equals(info.kv) && cur.sd.equals(info.sd)) {
                    boolean allSizedFound = true;
                    for (String discovered_s : cur.s) {
                        if(!info.s.contains(discovered_s)) {
                            allSizedFound = false;
                        }
                    }
                    if(allSizedFound) itr.remove();
                }
            }
        }
        if(discoveredSlotInfo.size() != 0) {
            StringBuilder missedSlotsMessage = new StringBuilder();
            for (SlotInfo info : discoveredSlotInfo) {
                missedSlotsMessage.append(String.format("unhandled slot info found: sd:%s, s:%s, kv: %s\n", info.sd, info.s.toString(), info.kv));
            }
            collector.addError(new Error(String.format("Unchecked Slots discovered:\n%s", missedSlotsMessage.toString())));
        }
    };

    /**
     * Converts a json string to an ArrayList
     * @param jsonString
     * @return
     * @throws JSONException
     */
    static ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {
        ArrayList<String> stringArray = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }
        return stringArray;
    }

    /**
     * Quick check to make sure any existing errors are not duplicated. If they don't exist - add, else ignore
     * @param list
     * @param errorMessage
     * @param valToMatch
     * @param actual
     */
    private void updateFieldErrors(ArrayList<String> list, String errorMessage, String valToMatch, String actual) {
        if(!valToMatch.equals(actual)) {
            String error = String.format("%s. Expected: %s, Actual: %s", errorMessage, valToMatch, actual);
            if (!list.contains(error)) {
                list.add(error);
            }
        }
    }
}