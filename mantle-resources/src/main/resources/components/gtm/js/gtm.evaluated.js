<#compress>
// moved from gtm.ftl so we can initialize GTM only onLoad. From https://support.google.com/tagmanager/answer/6103696?hl=en
Mntl.utilities.onLoad(function() {
    (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;defer=true;j.src='//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);})(window,document,'script','dataLayer','${model.accountId!"GTM-N3QTB5X"}');
});
var dataLayer = dataLayer || [];

dataLayer.push({
	event: 'ab-proctor',
    'abTests-proctor': {<#list (model.gtmPageView.abTests)![] as abTest>
    	   "${abTest.bucketTrackingId}<#if abTest.bucketTrackingId == '99'>-${abTest?index}</#if>"
    	   : "${abTest.testName?js_string} | ${abTest.bucketName?js_string} | ${abTest.bucketDescription?js_string} | ${abTest.bucketValue?js_string}"
		<#sep>, </#list>}
});
dataLayer.push({
	envData: {
		environment: {
			environment: "${model.gtmPageView.environment.environment.environment?js_string}",
			application: "${model.gtmPageView.environment.environment.application?js_string}",
			dataCenter: "${model.gtmPageView.environment.environment.dataCenter?js_string}"
		},
		server: {
			version: "${(model.gtmPageView.environment.server.version!'')?js_string}",
			title: "${(model.gtmPageView.environment.server.title!'')?js_string}"
		},
		client : {
			browserUA: navigator.userAgent,
			serverUA: "${(model.gtmPageView.environment.client.serverUA!'')?js_string}",
			deviceType: "${(model.gtmPageView.environment.client.deviceType!'')?js_string}",
			usStateCode: "${(model.gtmPageView.environment.client.usStateCode!'')?js_string}"
		},
		mantle: "${(model.gtmPageView.mantleVersion!'')?js_string}",
		commerce: "${(model.gtmPageView.commerceVersion!'')?js_string}"
	}
});
(function(win, fnUtils, CMP) {
    var pageViewDataAsJSON = ${model.gtmPageView.pageViewDataAsJSON};
    var deferLoadTime = 5000;
    var readyForThirdPartyTrackingEvent = new CustomEvent('readyForThirdPartyTracking', { bubbles: true });
	var readyForThirdPartyTracking = fnUtils.once(function() {
		dataLayer.push({event: 'readyForThirdPartyTracking'});
		window.dispatchEvent(readyForThirdPartyTrackingEvent);
	});
	var readyForDeferredScriptsEvent = new CustomEvent('readyForDeferredScripts', { bubbles: true });
	var readyForDeferredScripts = fnUtils.once(function() {
		dataLayer.push({event: 'readyForDeferredScripts'});
		window.dispatchEvent(readyForDeferredScriptsEvent);
	});

	var hasTargetingConsentHandler = function() {
        const hasConsent = CMP.hasTargetingConsent();
        if (hasConsent) {
            readyForThirdPartyTracking();
        }
		// if there is consent or the user has closed the banner(AlertBox) in EU then trigger readyForDeferredScripts
		if (hasConsent || CMP.isAlertBoxClosed()) {
			readyForDeferredScripts();
		}
		return hasConsent;
	};
	var onRequiredDomEvent = fnUtils.once(function() {
		if (!CMP) {
			readyForThirdPartyTracking();
			readyForDeferredScripts();
			return;
		}
		if (!CMP.isLoading()) {
			hasTargetingConsentHandler();
		}
		CMP.onConsentChange(hasTargetingConsentHandler);
	});

    [
		['adRendered', onRequiredDomEvent],
		['beforeunload', onRequiredDomEvent],
		['load', function() { setTimeout(onRequiredDomEvent, deferLoadTime); }]
    ].forEach(function(event) {
		win.addEventListener(event[0], event[1], { once: true });
    });

	<#if model.jsKeys?has_content>
		<#list model.jsKeys?keys as key>
			pageViewDataAsJSON.${key} = ${model.jsKeys[key]};
		</#list>
	</#if>
	<#if model.mtax?has_content>
		pageViewDataAsJSON.descriptiveTaxonomy = '${model.mtax?join(",")}';
	</#if>

    <#-- Delaying the PageView init call so the event fires slower than other commerce data (ab-commerce) per GLBE-7763 -->
    Mntl.utilities.onLoad(function() {
        Mntl.PageView.${requestContext.deferred?string('pushToDataLayer', 'init')}(pageViewDataAsJSON);
    });
})(window || {}, Mntl.fnUtilities || {}, Mntl.CMP);
</#compress>
