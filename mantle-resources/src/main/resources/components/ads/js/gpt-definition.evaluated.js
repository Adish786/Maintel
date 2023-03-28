<#compress>
(function(){
    const pageTargeting = <@convertHashToJs hash=model.pageTargeting />;
    const baseSlotTargeting = <@convertHashToJs hash=model.baseSlotTargeting />;

    <#if model.mtax?has_content>
    pageTargeting.mtax = [<#list model.mtax as val>'${val}'<#sep>,</#list>];
    </#if>

    <#if (model.useLmdFormat)!false>
        <#if model.sentiment?has_content>
        pageTargeting.sentiment = '${model.sentiment?js_string}';
        </#if>

        <#if model.concepts?has_content>
        pageTargeting.concepts = [<#list model.concepts as val>'${(val.name)?js_string}'<#sep>,</#list>];
        </#if>

        <#if model.taxons?has_content>
        pageTargeting.taxons = '${(model.taxons[0].name)?js_string}'.split(/\/|\|/).filter((n) => n),
        </#if>
    </#if>
    
    pageTargeting.w = Mntl.utilities.getW();

    const initialSlots = [];
    <#list model.gptAdSet as ad>
        <#assign updatedId=ad.id />
        <#assign updatedSizes=ad.sizes />
        <#if model.useOxygen>
            <#if requestContext.userAgent.deviceCategory == 'mobile'>
                <#if ad.id == 'billboard'>
                    <#assign updatedId = 'mob-square-flex-1' />
                    <#assign updatedSizes="[[300, 250],[299, 251],[340,450],[1,3],'fluid']" />
                <#elseif ad.id == 'billboardfooter'>
                    <#assign updatedId='mob-squarefooter-fixed-1' />
                    <#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
                <#elseif ad.id == 'billboardfooter2'>
                    <#assign updatedId='mob-squarefooter-fixed-2' />
                    <#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
                <#elseif ad.id?contains('billboard') || ad.id?contains('dynamic-inline')>
                    <#assign updatedId='mob-square-fixed' />
                    <#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
                <#elseif ad.id == 'adhesive'>
                    <#-- adhesives are only included on mobile -->
                    <#assign updatedId='mob-adhesive-banner-fixed' />
                    <#assign updatedSizes="[[320, 50], [319, 51]]" />
                </#if>
            <#else>
                <#if ad.id == 'billboard'>
                    <#assign updatedId = 'square-flex-1' />
                    <#assign updatedSizes='[[300, 250],[299, 251],[300, 600],[300, 1050],[160, 600]]' />
                <#elseif ad.id == 'billboard2'>
                    <#assign updatedId = 'square-flex-2' />
                    <#assign updatedSizes="[[300, 250], [300, 251],[300, 600],[300, 601],[2, 1],[160, 600],'fluid']" />
                <#elseif ad.id?contains('billboard')>
                    <#assign updatedId='square-fixed' />
                    <#assign updatedSizes="[[300, 250],[299, 251],'fluid']" />
                <#elseif ad.id?contains('dynamic-inline')>
                    <#assign updatedId='leaderboard-fixed' />
                    <#assign updatedSizes="[[728, 90]]" />
                <#elseif ad.id == 'leaderboard'>
                    <#assign updatedId = 'leaderboard-flex-1' />
                    <#assign updatedSizes='[[728, 90], [970,90], [970, 250]]' />
                <#elseif ad.id == 'leaderboard2'>
                    <#assign updatedId = 'leaderboard-flex-2' />
                    <#assign updatedSizes='[[728, 90], [970,90], [970, 251]]' />
                <#elseif ad.id == 'leaderboardfooter'>
                    <#assign updatedId = 'leaderboardfooter-flex-1' />
                    <#assign updatedSizes="[[728, 90], [728, 98], [970, 250], [970, 258], [1, 9], 'fluid']" />
                <#elseif ad.id == 'leaderboardfooter2'>
                    <#assign updatedId = 'leaderboardfooter-flex-2' />
                    <#assign updatedSizes="[[728, 90], [728, 98], [970, 250], [970, 258], [1, 9], 'fluid']" />
                <#elseif ad.id?contains('leaderboard')
                    && !ad.id?contains('leaderboard-flex')
                    && !ad.id?contains('leaderboardfooter-flex')>
                    <#if ad.id == 'leaderboardac'>
                        <#--  keeping the id "leaderboardac", but updating its sizes  -->
                        <#assign updatedSizes="[[728, 90], 'fluid', [970, 250], [1, 11], [1200, 450]]" />
                    <#elseif ad.id != 'leaderboard-fixed-0'>
                        <#--  any remaining leaderboards will be leaderboard-fixed  -->
                        <#assign updatedId='leaderboard-fixed' />
                        <#assign updatedSizes='[[728, 90]]' />
                    </#if>
                </#if>
            </#if>
        </#if>

        initialSlots.push({
            config: {
                id: '${updatedId}',
                sizes: ${updatedSizes},
                type: '${ad.type}',
                rtb: ${ad.rtb?c},
                timedRefresh: ${ad.timedRefresh?c},
                waitForThirdParty: ${ad.waitForThirdParty?c}
            },
            targeting: Mntl.fnUtilities.deepExtend(${utils.toJSONString(ad.targeting)}, {
                pos: '${ad.pos}',
                priority: ${ad.priority}
                <#if (ad.auctionFloorInfo.id)?has_content>
                ,floor_id: '${ad.auctionFloorInfo.id}'
                ,floor: '${ad.auctionFloorInfo.floor?c}'
                </#if>
            })
        });

    </#list>

    <#if requestContext.deferred>
        Mntl.GPT.updateBaseSlotTargeting(baseSlotTargeting);
    <#else>
        const testIds = Mntl.GPT.getTestIds();
        pageTargeting.ab = testIds;
        pageTargeting.bts = testIds;
        Mntl.utilities.onLoad(function() {
            Mntl.utilities.loadExternalJS({
                src: '//securepubads.g.doubleclick.net/tag/js/gpt.js',
                async: false
            });
        });
        const options = {
            domain: '${model.domain?js_string}',
            templateName: '${model.templateName?js_string}',
            isMobile: ${model.isMobile?c},
            dfpId: '${model.dfpId?js_string}',
            publisherProvidedId: '${model.publisherProvidedId?js_string}',
            singleRequest: ${((model.singleRequest)!false)?c},
            useLmdFormat: ${((model.useLmdFormat)!false)?c},
            useOxygen: ${((model.useOxygen)!false)?c},
            useInfiniteRightRail: ${((model.useInfiniteRightRail)!false)?c},
            useAuctionFloorSearch: ${((model.useAuctionFloorSearch)!false)?c},
            bundlePrebid: ${((model.bundlePrebid)!false)?c},
            lmdSiteCode: '${model.lmdSiteCode?js_string}',
            pageTargeting,
            baseSlotTargeting,
            geo: {
                isInEurope: ${((requestContext.geoData.isInEuropeanUnion())!false)?c},
                isInUsa: ${((requestContext.geoData.isoCode == "US")!false)?c}
            },
            initialSlots,
            <#if (model.auctionFloors)?has_content>
            auctionFloors: ${utils.toJSONString(model.auctionFloors)},
            </#if>
            utils: {
                <#list model.gptUtils?keys as key>
                    ${key}: ${model.gptUtils[key]}<#if key_has_next>,</#if>
                </#list>
            },
            displayOnScroll: ${((model.displayOnScroll!"true")?lower_case == "true")?c},
            displayOnConsent: ${(model.displayOnConsent!false)?c}
        };

        if (Mntl.AdMetrics) {
            Mntl.AdMetrics.init("${(requestContext.urlData.docId?c)!''}", "${(requestContext.requestId)!''}", initialSlots.map(slot => slot.config.id), Date.now());
        } else {
            Mntl.AdMetrics = { pushMetrics: () => {} };
        }
        Mntl.GPT.init(options);
    </#if>
}());
</#compress>