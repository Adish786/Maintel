window.addEventListener('readyForThirdPartyTracking', () => {
    (function(w, ...args) {
        w.hj = w.hj || function() {
            (w.hj.q = w.hj.q || []).push(args);
        };
        w._hjSettings = {
            hjid: '${model.siteID?js_string}',
            hjsv: 6
        };
        Mntl.utilities.loadExternalJS({
            // eslint-disable-next-line prefer-template
            src: 'https://static.hotjar.com/c/hotjar-' + w._hjSettings.hjid + '.js?sv=' + w._hjSettings.hjsv,
            async: true
        });
    })(window);
});

