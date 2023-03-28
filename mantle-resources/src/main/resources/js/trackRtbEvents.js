window.Mntl = window.Mntl || {};

Mntl.RTBTracking = (function() {
    'use strict';

    const { navigationStart } = performance.timing;
    
    const rtbData = {
        event: 'analyticsEvent',
        eventCategory: 'RTB Timing',
        eventAction: 'Load',
        nonInteraction: true,
        eventLabel: {}
    };

    const bidderAndPluginNameMap = {
        amazon: 'a', // Plugin and Bidder
        appnexus: 'an', // Bidder
        criteo: 'c', // Bidder
        prebid: 'p', // Plugin
        ias: 'ia', // Bidder
        ixid: 'ix', // Plugin
        index: 'i', // Bidder
        onemobile: 'om', // Bidder
        pubmatic: 'pb', // Bidder
        rubicon: 'r' // Bidder
    };

    let performanceSubscription;

    /**
     * Handle rtb events from RTB.js
     * @param {*} obj {
     *       event: 'll' | 'bt' | 'tw',
     *       name: 'plugin or bidder name',
     *       status: 'start' | 'finish'
     *       timestamp: Integer (optional)
     *   }
     */
    function _handleRtbPerformanceEvent(obj) {
        const nameMap = bidderAndPluginNameMap[obj.name];

        rtbData.eventLabel[obj.event] = rtbData.eventLabel[obj.event] || {};
        rtbData.eventLabel[obj.event][nameMap] = rtbData.eventLabel[obj.event][nameMap] || {};
        rtbData.eventLabel[obj.event][nameMap][obj.status] = obj.timestamp || (Date.now() - navigationStart);
    }

    function _sendToDataLayer() {
        document.removeEventListener('RTBSlotsBidded', _sendToDataLayer);
        performanceSubscription.unSubscribe();

        const eventLabel = {};

        for (const category in rtbData.eventLabel) {
            eventLabel[category] = [];
            for (const name in rtbData.eventLabel[category]) {
                const startTime = rtbData.eventLabel[category][name].start;
                const finishTime = rtbData.eventLabel[category][name].finish;
                const totalTime = finishTime ? finishTime - startTime : null;

                const datum = {
                    p: name,
                    s: startTime
                };

                if (totalTime !== null) {
                    datum.d = totalTime;
                }

                eventLabel[category].push(datum);
            }
        }
        rtbData.eventLabel = JSON.stringify(eventLabel);
        dataLayer.push(rtbData);
    }

    // Add all of the event listeners
    function init() {
        performanceSubscription = Mntl.RTB.subscribePerformance('performanceTracking', 'RTB', _handleRtbPerformanceEvent, 'on');
        document.addEventListener('RTBSlotsBidded', _sendToDataLayer);
    }

    return { init };
}());
