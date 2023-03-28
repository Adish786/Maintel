if ('serviceWorker' in navigator) {
    window.addEventListener('load', function() {
        navigator.serviceWorker.register('/homescreen-sw.js').then(function(registration) {
            window.debug.log('ServiceWorker registration successful with scope: ', registration.scope);
        }, function(err) {
            window.debug.log('ServiceWorker registration failed: ', err);
        });
    });
}

window.addEventListener('beforeinstallprompt', function(e) {
    var eventName = 'analyticsEvent';
    var eventLabel = 'addToHomeScreen';

    window.debug.log('User sees add to home screen prompt');
    if (window.dataLayer) {
        window.dataLayer.push({
            event: eventName,
            eventCategory: 'Impression',
            eventAction: 'Render',
            eventLabel: eventLabel,
            eventOther: ''
        });
    }
    e.userChoice.then(function(choiceResult) {
        var eventAction = choiceResult.outcome === 'dismissed' ? 'Dismiss' : 'Add';

        window.debug.log('User responded to add to home screen banner with the following action: ', eventAction);
        if (window.dataLayer) {
            window.dataLayer.push({
                event: eventName,
                eventCategory: 'Click',
                eventAction: eventAction,
                eventLabel: eventLabel,
                eventOther: ''
            });
        }
    });
});
