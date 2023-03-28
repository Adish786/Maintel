(function() {
    Mntl.utilities.onLoad(function() {
        <#-- provide our geo data to the OT library to prevent it from calling its own location service --><#t>
        <#-- https://my.onetrust.com/s/article/UUID-fe9e37a5-1560-433e-abcb-642ce2073ad4 --><#t>
        var geolocationResponse = {};
        const externalJS = {
            src: '${model.oneTrustEntryScript?js_string}',
            'data-domain-script': '${model.domainId.value?js_string}',
            <#if ((model.ignoreGA.value)!"true") != "false"><#t>
            'data-ignore-ga': true,
            </#if><#t>
            'data-language': '${model.lang.value?js_string}',
            async: true,
            charset: 'UTF-8',
            onerror: 'Mntl.CMP.onError()',
            id: 'onetrust-script'
        };
        <#if (requestContext.geoData.isoCode)?has_content><#t>
            <#-- OT does not support state-level granularity outside the US --><#t>
            <#if requestContext.geoData.isoCode == "US" && (requestContext.geoData.subdivisionCode)?has_content><#t>
        geolocationResponse.stateCode = '${requestContext.geoData.subdivisionCode?js_string}';
            </#if><#t>
        geolocationResponse.countryCode = '${requestContext.geoData.isoCode?js_string}';
        </#if><#t>
        <#if (requestContext.geoData.inEuropeanUnion)><#t>
        geolocationResponse.regionCode = 'EU';
        <#elseif (requestContext.geoData.isoCode == "US")><#t>
        geolocationResponse.regionCode = 'US';
        </#if><#t>
        if (Object.keys(geolocationResponse).length) {
            window.OneTrust = window.OneTrust || {};
            window.OneTrust.geolocationResponse = geolocationResponse;
        }
        <#if model.oneTrustTemplateName == 'ccpa'><#t>
        const preferenceCenterTrigger = document.querySelector('.ot-pref-trigger');
        preferenceCenterTrigger.addEventListener('click', (e) => {
            e.preventDefault();
            Mntl.utilities.loadExternalJS(
                externalJS
            , () => {
                Mntl.CMP.onSdkLoaded(() => {
                    OneTrust.ToggleInfoDisplay();
                })
            });
        },
        { once: true });
        <#else><#t>
        Mntl.utilities.loadExternalJS(
            externalJS
        );
        </#if><#t> 
    });
    Mntl.CMP.init(${model.isConsentRequired?c}, '${model.oneTrustTemplateName?js_string}', ${model.showConsentBanner?c}, ${model.scriptTimeout?c});
})();
