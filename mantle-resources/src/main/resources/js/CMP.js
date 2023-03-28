window.Mntl = window.Mntl || {};

/* OneTrust calls OptanonWrapper when the SDK loads and when a user interacts with the banner/pref center by accepting/rejecting
 * So this function needs to be defined BEFORE we load the SDK
 */
// eslint-disable-next-line no-unused-vars
window.OptanonWrapper = function() {
    var overlay = document.querySelector('.onetrust-pc-dark-filter');
    var banner = document.querySelector('#onetrust-banner-sdk');

    /* This is to handle the overlay display for these scenarios:
        1. We don't show the banner but OT does.
        2. We show the banner but OT hides it.
    */
    function hideOverlay() {
        overlay.style.display = 'none';
    }

    function showOverlay() {
        overlay.style.display = 'block';
    }

    window.debug.log('OptanonWrapper called');

    if (Mntl.CMP.hasFailedToLoad()) {
        return;
    }

    if (Mntl.CMP.isLoading()) {
        Mntl.CMP.onLoad();
    }

    OneTrust.OnConsentChanged(Mntl.CMP.triggerConsentChange); // eslint-disable-line new-cap

    if (Mntl.CMP.isOptInConsent()) {
        Mntl.CMP.broadcastPreexistingConsent();
        Mntl.CMP.showBannerOneTrustFallback();

        if (!banner.classList.contains('show-banner') || window.getComputedStyle(banner).display === 'none') {
            hideOverlay();
        }

        if (banner.style.display !== 'none' && banner.style.visibility !== 'hidden') {
            showOverlay();
        }
    }

    document.addEventListener('click', function(event) {
        if (event.target.closest('#onetrust-accept-btn-handler') || event.target.closest('.onetrust-close-btn-handler')) {
            hideOverlay();
        }

        if (event.target.closest('.ot-pref-trigger')) {
            event.preventDefault();
            OneTrust.ToggleInfoDisplay(); // eslint-disable-line new-cap
        }

        return;
    });

    Mntl.CMP.trackBannerLogicScenarios();
    // need to know when the wrapper has been called because that will mean the otBannerSdk.js has loaded
    // this will let us load the otBannerSdk.js on click of the footer link in opt-out regions (US/ccpa)
    Mntl.CMP.sdkLoaded();
}

Mntl.CMP = (function(utils, fnUtils) {
    const CONSENT_COOKIE = 'OptanonConsent';
    const ALERT_BOX_COOKIE = 'OptanonAlertBoxClosed';
    const TARGETING_REJECTED_IDENTIFIER = '4:0';
    const optInOneTrustTemplates = ['gdpr'];

    var _loadState;
    var _isConsentRequired = false;
    var _oneTrustTemplateName = null;
    var _showConsentBanner = false;
    var events = new Mntl.PubSub(['onConsentChange', 'onLoad', 'onFail', 'onSdkLoaded']);

    function _shouldShowBannerAccordingToOneTrust() {
        return !OneTrust.IsAlertBoxClosed(); // eslint-disable-line new-cap
    }

    /**
     * Intended to be called from OptanonWrapper.
     *
     * 1) Preexisting consent can be either from the user
     * previously interacting with the CMP banner or from being in a geo region
     * where CMP consent is opt-out such as California (CCPA).
     */
    var _broadcastPreexistingConsent = fnUtils.once(() => {
        const DD = _showConsentBanner;
        const OT = Mntl.CMP.isOptInConsent() && _shouldShowBannerAccordingToOneTrust();
        const bannerOnDisplay = DD || OT;
        const hasPreexistingConsent = !Mntl.CMP.isOptInConsent() || !bannerOnDisplay; // 1

        if (hasPreexistingConsent) {
            // if we are not showing the banner it implies that there is pre-existing consent
            // when we have pre-existing consent we still want to trigger all the downstream
            // things that do something based on consent changes
            triggerConsentChange(); // eslint-disable-line no-use-before-define
        }
    });

    /**
     * Intended to be called from OptanonWrapper.
     */
    var _showBannerOneTrustFallback = fnUtils.once(() => {
        const DD = _showConsentBanner;
        const OT = Mntl.CMP.isOptInConsent() && _shouldShowBannerAccordingToOneTrust();

        if (!DD && OT) {
            const banner = document.querySelector('#onetrust-banner-sdk');

            if (!banner.classList.contains('show-banner')) {
                banner.classList.add('show-banner');
            }
        }
    });

    /**
     * Intended to be called from OptanonWrapper.
     */
    var _trackBannerLogicScenarios = fnUtils.once(() => {
        const DD = _showConsentBanner;
        const OT = Mntl.CMP.isOptInConsent() && _shouldShowBannerAccordingToOneTrust();
        const pushToDataLayer = utils.pushToDataLayer({
            event: 'transmitNonInteractiveEvent',
            eventCategory: 'Consent Management',
            eventAction: 'Banner Display Logic'
        });

        if (DD && OT) {
            pushToDataLayer({ eventLabel: 'Dotdash: Show | OneTrust: Show' });
        } else if (!DD && OT) {
            pushToDataLayer({ eventLabel: 'Dotdash: Hide | OneTrust: Show' });
        } else if (DD && !OT) {
            pushToDataLayer({ eventLabel: 'Dotdash: Show | OneTrust: Hide' });
        } // we are not tracking the hide/hide case
    });

    function templateName() {
        return _oneTrustTemplateName;
    }

    function hasTargetingConsent() {
        const userConsentCookie = docCookies.getItem(CONSENT_COOKIE);
        // probably need a better way to do this for the future if we have more opt out geolocations/countries
        const userCountryCode = window.otStubData ? window.otStubData.userLocation.country : _oneTrustTemplateName === 'ccpa' ? 'US' : null;
        let consentMap;

        if (!userConsentCookie) {
            // return true if there is no cookie and it is not opt-in consent (ie. ccpa, consent is provided by default)
            // this is so we do not need to wait for the OT library to determine consent
            return !Mntl.CMP.isOptInConsent();
        }

        consentMap = querystring.parse(userConsentCookie);

        // possible for groups to be empty if a user is in US and has not interacted with the banner or preference center
        if (!consentMap.groups) {
            return !Mntl.CMP.isOptInConsent();
        }

        /* Expected Cookie Format - groups=<category-id>:<consent-flag>,<category-id[_subgroup-id]>:<consent-flag>,...
            the TARGETING_REJECTED_IDENTIFIER (4:0) means that the user has rejected targeted ads and there is no consent
            if the cookie groups does not contain this identifier then they have accepted targeted ads and there is consent
            there are only two possible scenarios and they are outlined below

            Example1: consentMap.groups="1:1,2:1,3:1,5:1,BG82:1,4:1"
                     consentMap.groups.indexOf(TARGETING_REJECTED_IDENTIFIER) will equal -1
                     hasConsentFromCookie will equal -1 === -1 which is true. the user has given consent to targeted ads

            Example2: consentMap.groups="1:1,2:1,3:1,5:1,BG82:1,4:0"
                     consentMap.groups.indexOf(TARGETING_REJECTED_IDENTIFIER) will equal 23
                     hasConsentFromCookie will equal 23 === -1 which is false. the user has not given consent to targeted ads */
        const hasConsentFromCookie = consentMap.groups.indexOf(TARGETING_REJECTED_IDENTIFIER) === -1;

        // if user is in the US, has not given consent, and they have not interacted with the alertBox then this is not a valid opt-out of targeting
        if (userCountryCode === 'US' && !hasConsentFromCookie && docCookies.getItem(ALERT_BOX_COOKIE) === null) {
            return true;
        }

        // by default return the consent stored in the cookie
        return hasConsentFromCookie;
    }

    /*
    * Check if user has closed the banner. This means they have accepted or rejected consent in a opt-in geolocation
    */
    function isAlertBoxClosed() {
        return docCookies.getItem(ALERT_BOX_COOKIE) !== null;
    }

    function isConsentRequired() {
        return _isConsentRequired;
    }

    /*
    * Check if user has rejected consent and are based in a specific region 
    */
    function hasRejectedUserConsent(requestedRegion) {
        const regionCode = window.OneTrust && window.OneTrust.geolocationResponse.regionCode;

        return regionCode === requestedRegion && !hasTargetingConsent();
    }

    /*
    * fires if either no consent is required or consent has been given
    */
    function onConsentChange(callback, once = false) {
        if (!isConsentRequired()) {
            callback();

            return;
        }

        if (once) {
            events.once('onConsentChange', false, callback);
        } else {
            events.on('onConsentChange', false, callback);
        }
    }

    /*
    * executes a callback when the onSdkLoaded event has been triggered
    */
    function onSdkLoaded(callback) {
        events.once('onSdkLoaded', false, callback);
    }

    function triggerConsentChange() {
        events.publish('onConsentChange');
    }

    function isLoading() {
        return _loadState === 'loading';
    }

    function hasFailedToLoad() {
        return _loadState === 'failed';
    }

    function isOptInConsent() {
        return isConsentRequired() && optInOneTrustTemplates.includes(_oneTrustTemplateName);
    }

    function _teardown() {
        var otScript = document.getElementById('onetrust-script');
        var banner = document.getElementById('onetrust-banner-sdk');

        // visually hide banner
        banner.classList.remove('show-banner');
        // remove script tag for CMP which effectively "cancels" the request
        if (otScript) {
            otScript.parentNode.removeChild(otScript);
        }
    }

    function onLoad() {
        window.debug.log('CMP loaded');
        _loadState = 'loaded';
        events.publish('onLoad');
        // any additional logic to handle a successful CMP load
    }

    function onError() {
        window.debug.log('CMP error');
        _loadState = 'failed';
        events.publish('onFail');
        // any additional logic to handle the CMP failing to load
    }

    function onTimeout() {
        window.debug.log('CMP timeout');
        _loadState = 'failed';
        events.publish('onFail');
        // any additional logic to handle the CMP timing out during load
    }

    function sdkLoaded() {
        window.debug.log('otBannerSdk.js script has loaded');
        events.publish('onSdkLoaded');
    }

    function init(consentRequired, oneTrustTemplateName, showConsentBanner, scriptTimeout) {
        var clickEvent;

        _loadState = 'loading';
        _isConsentRequired = consentRequired;
        _oneTrustTemplateName = oneTrustTemplateName;
        _showConsentBanner = showConsentBanner;

        if (_showConsentBanner) {
            document.addEventListener('click', function(e) {
                var wrapper = e.target.closest('#onetrust-consent-sdk');

                if (!wrapper || _loadState === 'loaded') {
                    return;
                }

                if (hasFailedToLoad()) {
                    // teardown CMP immediately if script has already failed to load
                    _teardown();
                    triggerConsentChange();

                    return;
                }

                // capture only the first click event
                if (!clickEvent && (e.target.nodeName === 'BUTTON' || e.target.nodeName === 'A')) {
                    clickEvent = e;
                    window.debug.log('A click was queued up: ' + e.target.id);
                }

                events.once('onLoad', false, function() {
                    if (clickEvent) {
                        // replay clicks
                        clickEvent.target.click();
                        window.debug.log('A click was replayed: ' + clickEvent.target.id);
                    }
                });
                events.once('onFail', false, function() {
                    _teardown();
                    triggerConsentChange();
                });

                setTimeout(function() {
                    if (isLoading()) {
                        // if CMP is still loading at this time, then error out
                        onTimeout();
                    }
                }, scriptTimeout);
            }, { once: true });
        }

        if (!Mntl.CMP.isOptInConsent()) {
            // if it is not opt-in consent then mark CMP as loaded immediately
            onLoad();
        }
    }

    return {
        broadcastPreexistingConsent: _broadcastPreexistingConsent,
        hasFailedToLoad,
        hasTargetingConsent,
        init,
        hasRejectedUserConsent,
        isConsentRequired,
        isAlertBoxClosed,
        isLoading,
        isOptInConsent,
        onLoad,
        onError,
        onTimeout: onTimeout,
        onConsentChange,
        onSdkLoaded,
        sdkLoaded,
        showBannerOneTrustFallback: _showBannerOneTrustFallback,
        templateName,
        trackBannerLogicScenarios: _trackBannerLogicScenarios,
        triggerConsentChange
    };
})(Mntl.utilities || {}, Mntl.fnUtilities || {});
