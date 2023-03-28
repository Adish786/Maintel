window.Mntl = window.Mntl || {};

(function(utils) {
    const JS_VERSION_PARAM = 'jumpstart_version';
    const params = new Proxy(new URLSearchParams(window.location.search), { get: (searchParams, prop) => searchParams.get(prop) });
    let jumpstartScript = 'https://cdn.video.meredithcorp.io/jumpstart/v4/js/jumpstart.min.js';

    function setJsVersionCookie(scriptUrl) {
        window.docCookies.setItem(JS_VERSION_PARAM, scriptUrl, '', '/', utils.getDomain());
    }

    if (params[JS_VERSION_PARAM]) {
        setJsVersionCookie(params[JS_VERSION_PARAM]);
    }

    if (window.docCookies.getItem(JS_VERSION_PARAM)) {
        const jumpstartBundleBase = 'https://cdn.video.meredithcorp.io/v4/bundles';
        const jumpstartBundleVersion = decodeURIComponent(window.docCookies.getItem(JS_VERSION_PARAM));

        jumpstartScript = `${jumpstartBundleBase}/${jumpstartBundleVersion}`;
    }
    // Load video script
    utils.loadExternalJS({ src: jumpstartScript });
})(window.Mntl.utilities || {});