package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.components.frames.MntlCheetahembedIframe;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlDigiohEmbedComponent;
import com.about.mantle.venus.model.pages.MntlScPage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

public class MntlDigiohEmbedTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected Consumer<MntlRunner> mntlDigiohEmbedTest = runner -> {

        MntlScPage mntlScPage = new MntlScPage<>(runner.driver(), MntlDigiohEmbedComponent.class);

         /*
        Validate Digioh iFrame is loaded properly on the page
         */
        List<MntlDigiohEmbedComponent> mntlDigiohembeds = mntlScPage.mntlDigiohEmbedComponent();
        collector.checkThat("Digioh iFrames were not found on the page", mntlDigiohembeds.size(), greaterThan(0));

        for (MntlDigiohEmbedComponent embed : mntlDigiohembeds) {
            embed.scrollIntoViewCentered();
            collector.checkThat("DigiohEmbed data guid is null or empty", embed.container().getAttribute("data-guid"), not(emptyOrNullString()));
            collector.checkThat("DigiohEmbed data title is null or empty", embed.container().getAttribute("data-title"), not(emptyOrNullString()));
            embed.iframe().scrollIntoView();
            collector.checkThat("DigiohEmbed iFrame style height is zero, iFrame is empty or null", embed.iframe().getSize().getHeight(), greaterThan(0));
            collector.checkThat("DigiohEmbed iFrame data title does not match DigiohEmbed container data title", embed.container().getAttribute("data-title"), is(embed.iframe().getAttribute("title")));
        }
    };
}
