window.Mntl = window.Mntl || {};

Mntl.AdMetrics = (function() {
    class Metrics {
        /**
         * This class handles the format and organization of the data, and the logic to push the data to
         * an endpoint.  It does not listen to events or handle when the data should be pushed.
         * 
         * @param {string} type What type of metric: page, slot
         * @param {integer} navigationStart The navigation start which all timed metrics will use at its starting point
         * @param {object} initialMetrics Initial metric data to be tracked
         * @param {object} requiredMetrics Specify which metrics are required before pushing the metrics
         * @param {boolean} sendMetricsToKafka Whether orion should push the metrics to kafa
         */
        constructor(type, navigationStart, initialMetrics = {}, requiredMetrics = {}, sendMetricsToKafka = true, useLocalOrion = false) {
            this._type = type;
            this._navigationStart = navigationStart;
            this._metrics = {};
            this.logMetrics(initialMetrics);
            this._requiredMetrics = requiredMetrics;
            this._sendMetricsToKafka = sendMetricsToKafka;
            this._postEndpoint = useLocalOrion ? `http://localhost:8000/${this._type}Data` : `/api/orion/${this._type}Data`;
            this.metricsPushed = false;
        }

        /**
         * Since null and array both have typeof value === 'object', this is a helper function
         * to verify if a param is actually an object
         * 
         * @param {*} value A variable to determine whether or not it is null, an array, a true object or another type
         * @returns {string} The type of the variable (array, object, null, undefined, etc)
         */
        _getSpecificType(value) {
            if (value === null) {
                return null;
            } else if (Array.isArray(value)) {
                return 'array';
            }

            return typeof value;
        }

        /**
         * Merge objects in an array according to a property name.
         * Used to merge timing metrics since the start and end finish of a
         * given opperation are both added to timed metric arrays
         * 
         * @param {objecArray} An array of objects to be processed
         * @param {property} A property name by which to merge objects (e.g. 'name')
         * @returns {array} Array of combined objects by property name
         */
        _mergeObjectsInArray(objectArray, property) {
            const arrayWithMergedObjects = [];

            objectArray.forEach((obj) => {
                const index = arrayWithMergedObjects.findIndex((processedObj) => processedObj[property] === obj[property]);

                if (index > -1) {
                    arrayWithMergedObjects[index] = {
                        ...arrayWithMergedObjects[index], 
                        ...obj
                    };
                } else {
                    arrayWithMergedObjects.push(obj);
                }
            });

            return arrayWithMergedObjects;
        }

        /**
         * Checks a list of preset categories that can contain timed metrics.
         * This reduces the number of iterations that need to be performed in _formatTimedMetrics()
         * 
         * @param {category} An array of objects to be checked
         * @param {type} The data type of the category to be checked (array or object)
         * @returns {boolean} Should the category be processed as a timed metric
         */
        _containsTimedMetrics(category, type) {
            const timedMetricsToFormat = {
                array: ['libraryBidTiming', 'libraryLoad', 'partnerBids'],
                object: ['gam']
            };

            return timedMetricsToFormat[type] && timedMetricsToFormat[type].includes(category);
        }

        /**
         * Loops through all entries in a catagroey and if the metric has the property of
         * timedMetric = true, the value of the metric is formatted into one of the following:
         *    For arrays:
         *      this._metrics[category][metric].startTime = startTimeFromNavStartInMs
         *      this._metrics[category][metric].runTime = totalTimeFromStartTimeInMs
         *
         *    For objects:
         *      this._metrics[category][metric] = timeFromNavStartInMs
         * Note: timed metrics are set up for metrics at the [category][metric] level, and not directly
         * on the [category].
         * 
         * @param {category} The category to be processed
         * @param {type} The data type of the category to be processed (array or object)
         */
        _formatTimedMetrics(category, type) {
            switch(type) {
                case 'array': {

                    // Combine separate entries (start and end) for each measured timing
                    const categoryDataMerged = this._mergeObjectsInArray(this._metrics[category], 'name');
                    const formattedArray = [];

                    for (const index in categoryDataMerged) {
                        const metricData = categoryDataMerged[index];
                        const formattedObject = {};

                        try {
                            if (metricData && metricData.timedMetric) {
                                formattedObject.name = metricData.name;

                                if ('start' in metricData) {
                                    const startTime = metricData.start ? metricData.start - this._navigationStart : null;
                                    const totalTime = metricData.finish ? metricData.finish - metricData.start : -1;

                                    formattedObject.startTime = startTime;
                                    formattedObject.runTime = totalTime >= 0 ? totalTime : null;
                                }
                            }

                            // For partnerBids, add bidAmount and bidSize to formattedObject
                            if (category === 'partnerBids') {
                                formattedObject.bidAmount = metricData.bidAmount;
                                formattedObject.bidSize = metricData.bidSize;
                            }

                        } catch (error) {
                            window.debug.error(`Mntl.adMetrics _formatTimedMetrics.  Could not format timed metric for [category: ${category}] | [metric: ${metricData.name}]`, { ...this }, error);
                            formattedObject.startTime = null;
                            formattedObject.runTime = null;
                        }
                        formattedArray.push(formattedObject);
                    }
                    this._metrics[category] = formattedArray;

                    break;
                }

                case 'object': {
                    const categoryData = this._metrics[category];

                    for (const metric in categoryData) {
                        try {
                            const metricData = categoryData[metric];

                            if (metricData && metricData.timedMetric) {
                                if (!metricData.timestamp) {
                                    throw new Error('Timestamp not provided for timedMetric');
                                }
                                this._metrics[category][metric] = metricData.timestamp - this._navigationStart;
                            }
                        } catch (error) {
                            window.debug.error(`Mntl.adMetrics _formatTimedMetrics.  Could not format timed metric for [category: ${category}] | [metric: ${metric}]`, { ...this }, error);
                            this._metrics[category][metric] = null;
                        }
                    }
                }
            }
        }

        /**
         * Loop through all required metrics and if any of them do not exist in the metrics object
         * return false.  The value of the metric can be null, but the metric key must exist.
         * 
         * @returns {boolean} True if all required metrics exist 
         */
        requiredMetricsComplete() {
            let requiredComplete = true;

            for (const category in this._requiredMetrics) {
                if (this._metrics[category]) {
                    this._requiredMetrics[category].forEach((metric) => {
                        if (!(metric in this._metrics[category])) {
                            requiredComplete = false;
                        }
                    });
                } else {
                    requiredComplete = false;
                }
            }

            return requiredComplete;
        }

        /**
         * Logs data into this._metrics, merges with existing data instead of overwriting
         * 
         * @param {object} metrics See wiki documentation for allowed formats
         *                          https://dotdash.atlassian.net/wiki/spaces/REVDEV/pages/3206217820/Orion+Ad+Metrics+Tracking+Library 
         */
        logMetrics(metrics) {
            for (const category in metrics) { // ie libraryBidTiming
                const categoryData = metrics[category];

                if (this._getSpecificType(categoryData) === 'object') {
                    for (const metric in categoryData) { // ie prebid
                        const metricsData = categoryData[metric];
    
                        if (this._getSpecificType(metricsData) === 'object') {
                            this._metrics[category] = this._metrics[category] || {};
                            this._metrics[category][metric] = {
                                ...this._metrics[category][metric],
                                ...metricsData
                            }
                        } else {
                            if (!this._metrics[category]) {
                                this._metrics[category] = {};
                            }
                            this._metrics[category][metric] = metricsData;
                        }
                    }
                } else if (Array.isArray(categoryData) && Array.isArray(this._metrics[category])) {
                    this._metrics[category] = [...this._metrics[category], ...categoryData];
                } else {
                    this._metrics[category] = categoryData;
                }
            }

            window.debug.log(`Mntl.adMetrics, Type: ${this._type} ${this._type === 'slot' ? `[${this._metrics.slotId}]` : ''} logMetrics`, { ...metrics });
        }

        /**
         * Determines to distances from the top and left of the page, as well as the width and height 
         * of the document and sets them as {position}. 
         * Note: the document size is recalculated each time to account for changes in its size througout
         * the lifecycle of the pageview
         */
        setPosition() {
            const id = this._metrics.slotId;
            const iframe = document.querySelector(`#${id} iframe`);
            const rect = iframe.getBoundingClientRect();
            const body = document.body;
            const x = Math.floor(rect.x);
            const y = Math.floor(rect.y);

            this.logMetrics({
                position: {
                    page: [
                        x + Math.floor(window.scrollX),
                        y + Math.floor(window.scrollY)
                    ],
                    viewport: [
                        x,
                        y
                    ]
                },
                size: {
                    ad: [
                        Math.floor(rect.width),
                        Math.floor(rect.height)
                    ],
                    page: [
                        Math.floor(body.offsetWidth),
                        Math.floor(body.offsetHeight)
                    ],
                    viewport: [
                        window.innerWidth,
                        window.innerHeight
                    ]
                }
            });
        }
    
        /**
         * This should only be called when all metrics for this instance have been received.
         * Before pushing the metrics, run the method to format all timed metrics.
         */
        async pushMetrics() {
            for (const category in this._metrics) {
                const type = this._getSpecificType(this._metrics[category]);

                if (this._containsTimedMetrics(category, type)) {
                    this._formatTimedMetrics(category, type);
                }
            }

            try {
                const body = JSON.stringify({
                    ...this._metrics,
                    send: this._sendMetricsToKafka
                });

                const response = await fetch(this._postEndpoint, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json' },
                    body
                });

                if (response.status === 200) {
                    const responseJson = await response.json();

                    window.debug.log(`Mntl.adMetrics, Type: ${this._type} ${this._type === 'slot' ? `[${this._metrics.slotId}]` : ''} pushMetrics SUCCESS`, responseJson);
                } else {
                    // any other response - throw an error that it wasn't successfully pushed
                    throw Error(`Received a non-200 response status: [${response.status}]`);
                }
            } catch(err) {
                window.debug.error(`Mntl.adMetrics, Type: ${this._type} ${this._type === 'slot' ? `[${this._metrics.slotId}]` : ''} pushMetrics ERROR: `, err.message);
            }

            this.metricsPushed = true;
        }

        /**
         * Remove categories and their data from the metrics object. This is used to clear out values for a
         * metric object that will need to log metrics more than once (for ex. a refreshable slot)
         * 
         * @param {array} categories A list of categories to remove from the metrics object
         */
        removeCategories(categories) {
            categories.forEach((category) => {
                delete this._metrics[category];
            });

            window.debug.log(`Mntl.adMetrics, Type: ${this._type} ${this._type === 'slot' ? `[${this._metrics.slotId}]` : ''} removeCategories`, categories, { ...this._metrics });
        }
    }

    class AdMetricsManager {
        /**
         * This class creates instances of the Metrics class, subscribes to events, and orchestrates
         * when to call the metric instances' pushMetrics method.
         * Tracks the navigationStart time which is used as the base time for all Metrics instances.
         */
        constructor() {
            this._subscriptions = {};
            this._excludeSlotIds = ['oop', 'native'];
            this._firstSlotId = '';
            this._navigationStart = parseInt(Date.now() - window.performance.now());
        }

        /**
         * Initialize the AdMetricManager, create initial Metrics instance and set up events to listen to.
         * 
         * @param {object} settings Object with settings for trackPageMetrics, trackSlotMetrics, sendMetricsToKafka, useLocalOrion
         * @param {string} docId The document Id being viewed
         * @param {string} requestId The request Id of the user
         * @param {array} initialSlotIds Which slot ids were loaded immediately on page load
         * @param {integer} adServiceRunningTimestamp When did the Mantle ad service start running
         */
        init(settings, docId, requestId, initialSlotIds, adServiceRunningTimestamp) {
            this._trackPageMetricEvents = settings.trackPageMetrics;
            this._trackSlotMetricEvents = settings.trackSlotMetrics;
            this._sendMetricsToKafka = settings.sendMetricsToKafka;
            this._useLocalOrion = settings.useLocalOrion;
            this._docId = docId;
            this._requestId = requestId;

            if(this._trackPageMetricEvents) {
                this._events = new Mntl.PubSub(['pageMetric', 'slotCreated', 'slotMetric']);
                this._requiredSlotMetrics = { gam: ['slotOnload', 'slotRenderEnded'] };
                this._pageMetrics = new Metrics('page', this._navigationStart, {
                    docId,
                    requestId,
                    initialSlotIds,
                    adServiceRunning: adServiceRunningTimestamp - this._navigationStart
                }, {}, this._sendMetricsToKafka, this._useLocalOrion);
                this._subscriptions.page = this._events.subscribe('pageMetric', false, (e) => this._pageMetrics.logMetrics(e.metrics), 'on');
                const createSlotFrequency = this._trackSlotMetricEvents ? 'on' : 'once';
    
                this._slotMetrics = {};
                this._events.subscribe('slotCreated', false, (e) => this._createSlotMetric(e), createSlotFrequency);
            }
        }

        /**
         * This is triggered when a slotCreated event occurs. Tracks the slots initial metric data and
         * tracks if this is the first ad slot being loaded.
         * The PubSub does not take into account the instigator, so all subscriptions to events must
         * remain "on".
         * 
         * @param {object} e The data from the event coming from the PubSub library. 
         *                      e.instigator - the slot id
         *                      e.metrics - metrics data object to be logged
         */
        _createSlotMetric(e) {
            window.debug.log('Mntl.adMetrics, adMetricsManager._createSlotMetric', e.instigator, e);
            if (!this._firstSlotId) {
                this._firstSlotId = e.instigator;
            }

            const createNewSlotData = {
                slotId: e.instigator,
                ...e.metrics,
                docId: this._docId,
                requestId: this._requestId,
                isFirst: this._firstSlotId === e.instigator
            };
            
            this._slotMetrics[e.instigator] = new Metrics('slot', this._navigationStart, createNewSlotData,
                this._requiredSlotMetrics, this._sendMetricsToKafka, this._useLocalOrion);
            this._subscriptions[e.instigator] = this._events.subscribe('slotMetric', e.instigator, (e) => this._logSlotMetrics(e), 'on');
        }
        
        /**
         * Push the page metrics and unsubscribe from listening to pageMetric events
         */
        _pageMetricsComplete() {
            window.debug.log('Mntl.adMetrics, adMetricsManager._pageMetricsComplete');
            this._pageMetrics.pushMetrics();
            this._subscriptions.page.unSubscribe();
        }

        /**
         * Log the slot metrics.  If all required metrics have been logged, pushMetrics. 
         * If this is the first slot on the page, and all required metrics have been logged,
         * page metrics are also complete. If the first ad is a refreshable ad, we need to ensure we don't
         * push the page metrics more than once. Only push page metrics if they haven't yet been pushed.
         * 
         * @param {object} e The data from the event coming from the PubSub library. 
         *                      e.instigator - the slot id
         *                      e.metrics - metrics data object to be logged
         */
        _logSlotMetrics(e) {
            window.debug.log('Mntl.adMetrics, adMetricsManager._logSlotMetrics', e.instigator, { ...e.metrics });
            this._slotMetrics[e.instigator].logMetrics(e.metrics);
            if (this._slotMetrics[e.instigator].requiredMetricsComplete()) {
                // Once all required metrics are complete for a slot, get the position of the ad.
                // This should correlate to the last event to fire, which should be slotOnload
                this._slotMetrics[e.instigator].setPosition();

                this._slotMetrics[e.instigator].pushMetrics();
                if (e.instigator === this._firstSlotId && !this._pageMetrics.metricsPushed) {
                    this._pageMetricsComplete();
                }

                // reset metrics to prepare in case slot is refreshable
                this._slotMetrics[e.instigator].removeCategories(['gam', 'tier', 'libraryBidTiming', 'partnerBidTiming', 'partnerBids']);
                this._slotMetrics[e.instigator].logMetrics({ isFirst: false });

            }
        }

        /**
         * The code using this library does not need to know if the page is being tracked or not,
         * it will call the method to pushMetrics, but only if this is a visitor that we are tracking
         * will we actually publish it to the PubSub.
         * 
         * @param {string} type 'pageMetric, 'slotCreated', 'slotMetric'
         * @param {object} data The metrics data to publish
         *                          data.instigator - library called from or slotId
         *                          data.metrics - metrics data object to be logged
         */
        pushMetrics(type, data) {
            try {
                if (!data.instigator) throw new Error('Missing instigator in data param');
                if (!data.metrics) throw new Error('Missing metrics in data param');

                if (this._trackPageMetricEvents) {
                    switch(type) {
                        case 'pageMetric':
                            this._events.publish(type, data);
                            break;
                        case 'slotCreated':
                        case 'slotMetric': {
                            if (!this._excludeSlotIds.includes(data.instigator)) {
                                const isFirstSlot = ((this._firstSlotId === '') || (data.instigator === this._firstSlotId));
    
                                if (this._trackSlotMetricEvents || isFirstSlot) {
                                    this._events.publish(type, data);
                                }
                            }
                            break;
                        }
                        default:
                            throw new Error(`Invalid Metric type when calling pushMetrics: ${type}`);
                    }
                }
            } catch (error) {
                window.debug.error('Mntl.adMetricsManager, adMetricsManager.pushMetrics', type, { ...data }, error);
            }
        }
    }

    const _adMetricsManager = new AdMetricsManager();

    /**
     * The query param adMetricTracking has four flags to control the following settings of this library:
     *  1. trackPageMetrics - if 0, don't collect page metrics, also don't collect slot metrics (required to use queryparam overrides)
     *  2. trackSlotMetrics - if tracking page metrics, and this is 0, only the first slot's data will be tracked (default 0)
     *  3. sendMetricsToKafka - when data is pushed to orion, if this is 0, orion will not send the data onto kafka (default 0)
     *  4. useLocalOrion - to test using a locally running orion set this to 1 (default 0)
     * 
     * If the query param doesn't exist, null will be returned and settings will not be overridden
     * 
     * @returns {object} Null if there are no override settings, or an object with the override settings to use
     */
    function getOverrideSettings() {
        const trackMetricsOverride = Mntl.utilities.getQueryParams().adMetricTracking;
        let overrideSettings = null;

        if(trackMetricsOverride) {
            const trackPageMetrics = Boolean(Number(trackMetricsOverride.charAt(0)));
            const trackSlotMetrics = trackPageMetrics && Boolean(Number(trackMetricsOverride.charAt(1)));
            const sendMetricsToKafka = trackPageMetrics && Boolean(Number(trackMetricsOverride.charAt(2)));
            const useLocalOrion = Boolean(Number(trackMetricsOverride.charAt(3)));

            overrideSettings = {
                trackPageMetrics,
                trackSlotMetrics,
                sendMetricsToKafka,
                useLocalOrion
            }
        }

        return overrideSettings;
    }

    /**
     * Exposed function to initialize the Ad Metrics Manager.  If the adMetricsManager has already been
     * initialized, do not initialize it again.
     * 
     * @param {string} docId The document Id being viewed
     * @param {string} requestId The request Id of the user
     * @param {array} initialSlotIds Which slot ids were loaded immediately on page load
     * @param {integer} adServiceRunningTimestamp When did the Mantle ad service start running
     */
    function init(docId, requestId, initialSlotIds, adServiceRunningTimestamp) {
        const adMetricsManagerExists = _adMetricsManager.docId;

        if (!adMetricsManagerExists) {
            // Future goal is to have server side sampling, so if the library is included we know we want to track the metrics
            // currently set to 0.1% sampling rate (note: also starting off being throttled with the global proctor test orion)
            let settings = {
                trackPageMetrics: (Math.floor(Math.random()*100) <= 0.1),
                sendMetricsToKafka: true,
                useLocalOrion: false
            };

            // keeping a "trackSlotMetrics" in case we want to switch to a subset when collecting all slot metrics
            // but for now, if we are tracking page metrics, then we also track all slot metrics
            settings.trackSlotMetrics = settings.trackPageMetrics;

            const overrideSettings = getOverrideSettings();

            settings = {
                ...settings,
                ...overrideSettings
            };

            _adMetricsManager.init(settings, docId, requestId, initialSlotIds, adServiceRunningTimestamp);
        }
    }

    /**
     * Exposed function to push metrics.
     * 
     * @param {string} type 'pageMetric, 'slotCreated', 'slotMetric'
     * @param {object} data The metrics data to publish
     *                          data.instigator - library called from or slotId
     *                          data.metrics - metrics data object to be logged
     */
    function pushMetrics(type, data) {
        _adMetricsManager.pushMetrics(type, data);
    }

    return {
        init,
        pushMetrics,
        // The following are exposed for unit test purposes and should NOT be used outside of unit tests
        AdMetricsManager,
        Metrics,
        _adMetricsManager
    };
})();
