window.addEventListener('readyForThirdPartyTracking', () => {
    (function() {
        Mntl.utilities.loadExternalJS({
            src: 'https://cdn.p-n.io/pushly-sdk.min.js?domain_key=${model.pushlyDomainKey}',
            async: true
        });
        
        window.PushlySDK = window.PushlySDK || [];
        
        // eslint-disable-next-line prefer-rest-params
        function pushly() { window.PushlySDK.push(arguments) }
        pushly('load', {
            domainKey: '${model.pushlyDomainKey}',
            sw: '/pushly-sdk-worker.js'
        });
    })();
});