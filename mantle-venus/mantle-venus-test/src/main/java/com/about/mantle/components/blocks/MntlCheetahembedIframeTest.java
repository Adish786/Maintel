package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.components.frames.MntlCheetahembedIframe;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.pages.MntlScPage;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.utils.ConfigurationProperties;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;
@Deprecated
public class MntlCheetahembedIframeTest extends MntlVenusTest implements MntlCommonTestMethods {

    private static String seleneAws = ConfigurationProperties.getTargetSelene(null)+ "/document?docId=" ;

    protected BiConsumer<MntlCommonTestMethods.MntlRunner, String> mntlCheetahembedIframeTest = (runner, docId) -> {

        MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlCheetahembedIframe.class);
        /*
        Validate CheetahEmbed iFrame is loaded properly on the page
         */
        List<MntlCheetahembedIframe> mntlCheetahembedIframes = mntlScPage.mntlCheetahembedIframe();
        collector.checkThat("CheetahEmbed iFrames were not found on the page", mntlCheetahembedIframes.size(), greaterThan(0));

        for (MntlCheetahembedIframe frame : mntlCheetahembedIframes) {
            frame.iframe().scrollIntoViewCentered();
            collector.checkThat("CheetahEmbed iFrame data source is not correct", frame.iframe().getAttribute("data-src"), not(emptyOrNullString()));
            collector.checkThat("CheetahEmbed iFrame source is not correct", frame.iframe().getAttribute("src"), not(emptyOrNullString()));
            collector.checkThat("CheetahEmbed iFrame style height is zero, iFrame is empty or null", frame.iframe().getSize().getHeight(), greaterThan(0));
        }
         /*
        Validate the cheetahembed for Selene
         */
        collector.checkThat("Number of CheetahEmbed iFrames on the page does not match for Selene data", mntlCheetahembedIframes.size(), is(cheetahIdFromSelene(runner, docId).size()));
        /*
        Validate LazyLoad property of the CheetahEmbed iFrame
         */
        collector.checkThat("CheetahEmbed call not found on network calls", filterNetworkCalls(runner, "embed").size(), greaterThan(0));
        for (String cheetahId : cheetahIdFromSelene(runner, docId)) {
            List<VenusHarEntry> cheetahEmbedContainerCalls = filterNetworkCalls(runner, cheetahId);
            collector.checkThat("Cheetah content not populated", cheetahEmbedContainerCalls.size(), greaterThan(0));
        }
        runner.page().scroll().top();
        runner.driver().navigate().refresh();
        runner.page().slowScrollToBottom();
        collector.checkThat("CheetahEmbed didn't load after refresh ", filterNetworkCalls(runner, "embed").size(), greaterThan(1));
        for (String cheetahId : cheetahIdFromSelene(runner, docId)) {
            List<VenusHarEntry> cheetahEmbedContainerCalls = filterNetworkCalls(runner, cheetahId);
            collector.checkThat("Cheetah content not populated after refresh", cheetahEmbedContainerCalls.size(), is(greaterThan(1)));
        }
    };

    protected static List<VenusHarEntry> filterNetworkCalls(Runner runner, String filter) {
        return runner.proxy().capture().page(runner.proxyPage()).entries("wayin").stream().filter((entry) -> entry.request().url().url().contains(filter))
                .collect(Collectors.toList());
    }

    protected static List<String> cheetahIdFromSelene(MntlRunner runner, String docId) {
        String endPoint = seleneAws + docId;
        HttpResponse<String> docResponse = null;
        try {
            docResponse = Unirest.get(endPoint).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        assert docResponse != null;
        if (docResponse.getStatus() != Response.Status.OK.getStatusCode() && docResponse.getStatus() != Response.Status.BAD_REQUEST.getStatusCode())
            fail("Error in reading selene data.");

        List<String> cheetahIds = new ArrayList<>();
        List<String> cheetahIdEntry = Arrays.stream(docResponse.getBody().split(",")).filter(array -> array.contains("cheetahId")).collect(Collectors.toList());
        for (String id : cheetahIdEntry) {
            String cheetahId = id.replaceAll("[\":{}\\[\\]]*", "").replace("data", "").replace("cheetahId", "");
            cheetahIds.add(cheetahId);
        }
        return cheetahIds;
    }
}
