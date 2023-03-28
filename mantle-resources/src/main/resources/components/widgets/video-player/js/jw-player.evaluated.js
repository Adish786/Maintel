(function() {
    const setup = ${utils.toJSONString(model.setup)};

    // JW player requires `playlist` to be populated for contextual playlists
    setup.playlist = setup.playlistUrl;

    <#if !(model.disablePreroll!false)>
        const encodedPageURL = encodeURIComponent(window.location);

        <#if model.useOxygen>
            const domain = Mntl.utilities.getDomain();
            const iuPath = '/' + Mntl.GPT.getDfpId() + '/ddm.' + domain.replace('.com', '.video') + (Mntl.GPT.isMobile() ? '.mob' : '');
        <#else>
            <#if model.iu??>
                const iuPath = '${model.iu}';
            <#else>
                const iuPath = '/' + Mntl.GPT.getDfpId() + '${model.iuPath!""}';
            </#if>
        </#if>

        <#-- Format of the Ad Sizes by VAST TAG in this documentation: https://support.google.com/admanager/answer/1068325?hl=en -->
        function formatAdSizeArray(sizeArr) {
            return sizeArr.map(function(sz) {
                return sz.join('x');
            }).join('|');
        }

        // Assign a randomly-generated, eight character, alpha-numeric value to this property.
        setup.advertising.adscheduleid = Math.random().toString(36).substr(2,8);

        let custParams = '';
        <#if (model.custParams?has_content)>
            <#list model.custParams as paramKey, paramValue>
            custParams += '&' + encodeURIComponent('${paramKey}') + '=' + encodeURIComponent('${paramValue}');
            </#list>
        </#if>

        setup.advertising.schedule = [{
            "offset": "pre",
            "tag": [
                'https://securepubads.g.doubleclick.net/gampad/ads?',
                'sz=',
                formatAdSizeArray(${model.adSizes}),
                '&iu=',
                iuPath,
                '&env=vp&hl=en&gdfp_req=1&impl=s&output=xml_vast2&unviewed_position_start=1&lid=',
                '&url=',
                encodedPageURL,
                '&description_url=',
                encodedPageURL,
                '&correlator=',
                Math.floor(Date.now() / 1000),
                '&cust_params=',
                Mntl.GPT.serializeAllTargeting(),
                encodeURIComponent(custParams)
            ].join('')
        }];

        let tax1 = "";
        <#if (model.taxonomyStampValues)?has_content>
            <#if model.taxonomyStampValues.tax1??>
                tax1 = ${utils.toJSONString(model.taxonomyStampValues)}.tax1;
            </#if>
        </#if>

        setup.advertising.bids = {
            "bidders": [{
                "name": "Rubicon",
                "pubid": "7499",
                "siteId": "382340",
                "zoneId": "2120008"
            }],
            /* Set the mediation layer.
                Define the floor price.
            */
            "settings": {
                "mediationLayerAdServer": "dfp",
                "floorPriceCents": ${model.vpbFloorPricing?c},
                "buckets": [
                    {
                        "increment": .10,
                        "min": .10,
                        "max": 50.00
                    }
                ]
            }
        }
    </#if>

    Mntl.JWPlayer.init({
        accountKey: "${model.accountKey}",
        adSizes: ${model.adSizes},
        defer: ${model.defer!'false'},
        displayMode: "${model.displayMode!'none'}",
        divId: "${manifest.instanceId}",
        eventCategoryType: "${model.eventCategoryType!''}",
        intersectionMargin: "${model.intersectionMargin!'250px'}",
        isPlaylist: <#if model.setup.playlistUrl?has_content>true<#else>false</#if>,
        mediaSize: ${model.mediaSize},
        playerId: "${model.playerId!'oeXeniEf'}",
        playInView: ${model.playOnPercentInView!50},
        showCaptions: ${model.showCaptions!'true'},
        skipExistingMediaContent: ${model.skipExistingMediaContent},
        <#if (model.trackingCodes)?has_content>
        trackingCodes: [
            <#list model.trackingCodes as trackingCodes>
            "${(trackingCodes!'')?js_string}" <#sep>,
            </#list>
        ],
        </#if>
        useMotionThumbnails: ${model.useMotionThumbnails!'false'},
        useTAMforVideoAds: ${model.useTAMforVideoAds}
    }, setup );
}());
