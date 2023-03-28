/* eslint no-undefined:"off", no-unused-vars:"off", no-empty-function:"off", no-unused-expressions: "off" */
describe('Mntl.AdMetrics', () => {
    let Mntl;

    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: '/base/mantle-resources/src/main/resources/static/js/adMetricsTracker.js' },
            done
        );
    }

    before(() => {
        fixture.setBase('mantle-resources/test/spec/fixtures');
    });

    beforeEach((done) => {
        Mntl = window.Mntl || {};

        Mntl.PubSub = function() {
            return {
                what: sinon.stub(),
                addEvent: sinon.stub(),
                deleteEvent: sinon.stub(),
                publish: sinon.stub(),
                subscribe: sinon.stub().callsFake(() => ({ unSubscribe: sinon.stub()})),
                on: sinon.stub(),
                once: sinon.stub()
            };
        };

        load(done);
    });

    // Metrics class
    describe('Metrics Class', () => {
        const data = {
            slotId: 'leaderboard-flex-1',
            docId: 'abcd1234',
            requestId: 'efgh5678',
            isFirst: false
        };

        describe('constructor', () => {
            it('should log the initial metrics', () => {
                const metrics = new Mntl.AdMetrics.Metrics('slot', 123456789, data, {});

                expect(metrics._metrics).to.deep.equal(data);
            });
        });

        describe('logMetrics', () => {
            let metrics;
        
            beforeEach(() => {
                metrics = new Mntl.AdMetrics.Metrics('slot', 123456789, data, {});
            });

            it('should log a category with a primitive value', () => {
                const newMetricData = { tier: '2' };

                metrics.logMetrics(newMetricData);

                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    ...newMetricData
                });
            });

            it('should log a category with an array value', () => {
                const newMetricData = { initialSlotIds: ['leaderboard-flex-1', 'square-fixed-1'] };

                metrics.logMetrics(newMetricData);

                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    ...newMetricData
                });
            });

            it('should log a category with a primitive metric value', () => {
                const newMetricData = { gam: { creativeId: 'theCreativeId' }};
                
                metrics.logMetrics(newMetricData);

                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    ...newMetricData
                });
            });

            it('should log a category with a timed metric object value', () => {
                const newMetricData = {
                    gam: {
                        slotResponseReceived: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        }
                    }
                };

                metrics.logMetrics(newMetricData);

                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    ...newMetricData
                });
            });

            it('should merge metrics for an existing category and metric and not overwrite', () => {
                const newMetricData1 = {
                    gam: {
                        slotResponseReceived: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        },
                        foo: { bar: 'abc' }
                    }
                };

                const newMetricData2 = {
                    gam: {
                        dimensions: "970x250",
                        lineItemId: 6034687086,
                        slotRenderEnded: {
                            timedMetric: true,
                            timestamp: 1665511690693
                        },
                        foo: { baz: 123 }
                    }
                };

                metrics.logMetrics(newMetricData1);
                metrics.logMetrics(newMetricData2);

                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    gam: {
                        dimensions: "970x250",
                        lineItemId: 6034687086,
                        slotRenderEnded: {
                            timedMetric: true,
                            timestamp: 1665511690693
                        },
                        slotResponseReceived: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        },
                        foo: {
                            bar: 'abc',
                            baz: 123
                        }
                    }
                });
            });
        });

        // Skipping flakey test.. @waydestover to fix. https://dotdash.atlassian.net/browse/REVDEV-956
        describe.skip('setPosition', () => {
            before((done) => {
                fixture.load('gpt-slot.html');
                load(done); 
            });

            it('should add the position (relative to page, viewport) and dimensions (ad, page, viewport) to the slot metrics', () => {
                const testSlotPositionData = { // Position is set in fixtures/gpt-slot.html
                    slotId: 'leaderboard-fixed-1',
                    docId: 'abcd1234',
                    requestId: 'efgh5678',
                    isFirst: false
                };

                const expectedPosition = {
                    page: [100, 10],
                    viewport: [100, 10]
                };

                const expectedSize = {
                    ad: [728, 90],
                    page: [784, 0],
                    viewport: [800, 600]
                }

                const metrics = new Mntl.AdMetrics.Metrics('slot', 1665511681149, testSlotPositionData, {});
                metrics.setPosition();

                expect(metrics._metrics.position).to.deep.equal(expectedPosition);
                expect(metrics._metrics.size).to.deep.equal(expectedSize);
            });
        });

        describe('_formatTimedMetrics', () => {
            let metrics;
        
            beforeEach(() => {
                metrics = new Mntl.AdMetrics.Metrics('slot', 1665511681149, data, {});
            });

            it('should merge start and finish entries and format timed metrics with a startTime and runTime', () => {
                const timedMetricData = {
                    partnerBidTiming: [
                        {
                            name: 'pubmatic',
                            timedMetric: true,
                            start: 1665511688149,
                        },
                        {
                            name: 'pubmatic',
                            timedMetric: true,
                            finish: 1665511688550
                        }
                    ]
                };

                metrics.logMetrics(timedMetricData);
                metrics._formatTimedMetrics('partnerBidTiming', 'array');
                expect(metrics._metrics.partnerBidTiming).to.deep.equal(
                    [{
                        name: 'pubmatic',
                        startTime: 7000,
                        runTime: 401
                    }]
                );
            });

            it('should format timed metrics with a timestamp', () => {
                const timedMetricData = {
                    gam: {
                        slotResponseReceived: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        }
                    }
                };

                metrics.logMetrics(timedMetricData);
                metrics._formatTimedMetrics('gam', 'object');
                expect(metrics._metrics.gam.slotResponseReceived).to.equal(9874);
            });

            it('handles the error if a metric with timedMetric true does not have valid data and sets value to null', () => {
                const badTimedMetricData = {
                    gam: {
                        slotResponseReceived: {
                            timedMetric: true,
                            foo: 'bar'
                        }
                    }
                };

                metrics.logMetrics(badTimedMetricData);
                metrics._formatTimedMetrics('gam', 'object');
                expect(metrics._metrics.gam.slotResponseReceived).to.be.null;
            });

            it('sets properties of "name", "bidSize", "bidAmount", "startTime" and "runTime" on each object within the partnerBids array', () => {
                const bidMetricData = {
                    partnerBids: [{
                        name: 'pubmatic',
                        bidSize: '300x250',
                        bidAmount: '1.50',
                        timedMetric: true,
                        start: 1665511688149,
                        finish: 1665511688550
                    },
                    {
                        name: 'rubicon',
                        bidSize: '300x600',
                        bidAmount: '2.50',
                        timedMetric: true,
                        start: 1665511688150,
                        finish: 1665511688650
                    }]
                };

                metrics.logMetrics(bidMetricData);
                metrics._formatTimedMetrics('partnerBids', 'array');
                expect(metrics._metrics.partnerBids).to.deep.equal(
                    [{
                        name: 'pubmatic',
                        bidSize: '300x250',
                        bidAmount: '1.50',
                        startTime: 7000,
                        runTime: 401
                    },
                    {
                        name: 'rubicon',
                        bidSize: '300x600',
                        bidAmount: '2.50',
                        startTime: 7001,
                        runTime: 500
                    }]
                );
            });
        });

        describe('requiredMetricsComplete', () => {
            let metrics;
        
            beforeEach(() => {
                metrics = new Mntl.AdMetrics.Metrics('slot', 1665511681149, data, { gam: ['slotOnload', 'slotRenderEnded'] });
            });

            it('should return false if required metrics do not exist', () => {
                expect(metrics.requiredMetricsComplete()).to.be.false;
            });

            it('should return false if only a partial set of required values are set', () => {
                metrics.logMetrics({
                    gam: {
                        slotOnload: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        }
                    }
                });
                
                expect(metrics.requiredMetricsComplete()).to.be.false;
            });

            it('should return true if all required values are set', () => {
                metrics.logMetrics({
                    gam: {
                        slotOnload: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        },
                        slotRenderEnded: {
                            timedMetric: true,
                            timestamp: 1665511688550
                        }
                    }
                });
                
                expect(metrics.requiredMetricsComplete()).to.be.true;
            });
        });

        describe('removeCategories', () => {
            let metrics;
        
            beforeEach(() => {
                metrics = new Mntl.AdMetrics.Metrics('slot', 1665511681149, data, {});
            });

            it('should remove specified categories from the metrics object', () => {
                metrics.logMetrics({
                    tier: 1,
                    gam: {
                        slotOnload: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        },
                        slotRenderEnded: {
                            timedMetric: true,
                            timestamp: 1665511688550
                        }
                    },
                    foo: 'bar'
                });
                metrics.removeCategories(['gam', 'foo']);
                expect(metrics._metrics).to.deep.equal({
                    ...data,
                    tier: 1
                });
            });
        });

        describe('pushMetrics', () => {
            let metrics;
            let fetch;
            let debugError;

            function postOk (body) {
                //the fetch API returns a resolved window Response object
                const mockResponse = new window.Response(JSON.stringify(body), {
                    status: 200,
                    headers: { 'Content-type': 'application/json' }
                });
              
                return Promise.resolve(mockResponse);
            }

            function postNotOk (body) {
                //the fetch API returns a resolved window Response object
                const mockResponse = new window.Response(JSON.stringify(body), {
                    status: 500,
                    headers: { 'Content-type': 'application/json' }
                });
              
                return Promise.resolve(mockResponse);
            }
              
            const mockSlotMetrics = {
                "slotId": "leaderboard-flex-1",
                "docId": "4774501",
                "requestId": "n5a461629e9584a549b5f409b514a770715"
            };
        
            beforeEach(() => {
                const dataWithTimedMetric = {
                    ...data,
                    gam: {
                        slotOnload: {
                            timedMetric: true,
                            timestamp: 1665511691023
                        },
                        slotRenderEnded: {
                            timedMetric: true,
                            timestamp: 1665511688550
                        }
                    }
                };

                fetch = sinon.stub(window, 'fetch');
                debugError = sinon.stub(window.debug, 'error');

                metrics = new Mntl.AdMetrics.Metrics('slot', 1665511681149, dataWithTimedMetric, {});
                metrics._formatTimedMetrics = sinon.spy(() => {});
            });
              
            afterEach(() => {
                window.fetch.restore();
                window.debug.error.restore();
            });

            it('should call _formatTimedMetrics, call fetch to push metrics and set metricsPushed to true', async () => {
                fetch.returns(postOk(mockSlotMetrics));
                await metrics.pushMetrics();
                expect(metrics._formatTimedMetrics).to.have.been.called;
                expect(fetch).to.have.been.called;
                expect(metrics.metricsPushed).to.be.true;
                expect(debugError).to.not.have.been.called;
            });

            it('should handle any non 200 responses as an error and still set metricsPushed to true', async () => {
                fetch.returns(postNotOk(mockSlotMetrics));
                await metrics.pushMetrics();
                expect(metrics._formatTimedMetrics).to.have.been.called;
                expect(fetch).to.have.been.called;
                expect(metrics.metricsPushed).to.be.true;
                expect(debugError).to.have.been.called;
            });

            it('should handle any thrown fetch errors and still set metricsPushed to true', async () => {
                fetch.throws();
                await metrics.pushMetrics();
                expect(metrics._formatTimedMetrics).to.have.been.called;
                expect(fetch).to.have.been.called;
                expect(metrics.metricsPushed).to.be.true;
                expect(debugError).to.have.been.called;
            });
        });
    });

    // AdMetricsManager class
    describe('AdMetricsManager Class', () => {

        describe('constructor', () => {
            it('should set the _navigationStart value', () => {
                const adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                expect(adMetricsManager._navigationStart).to.be.above(0);
            });
        });

        describe('init', () => {
            let adMetricsManager;
        
            beforeEach(() => {
                adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();
            });

            it('should listen for all page events and the first slot\'s events if not tracking slot metrics', () => {
                const settings = {
                    trackPageMetrics: true,
                    trackSlotMetrics: false,
                    sendMetricsToKafka: false,
                    useLocalOrion: false
                };

                adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
                
                expect(adMetricsManager._events.subscribe).to.have.been.calledWith('pageMetric', false, sinon.match.any, 'on');
                expect(adMetricsManager._events.subscribe).to.have.been.calledWith('slotCreated', false, sinon.match.any, 'once');
            });

            it('should listen for all page events and events for all slots if tracking slot metrics', () => {
                const settings = {
                    trackPageMetrics: true,
                    trackSlotMetrics: true,
                    sendMetricsToKafka: false,
                    useLocalOrion: false
                };

                adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
                
                expect(adMetricsManager._events.subscribe).to.have.been.calledWith('pageMetric', false, sinon.match.any, 'on');
                expect(adMetricsManager._events.subscribe).to.have.been.calledWith('slotCreated', false, sinon.match.any, 'on');
            });
        });

        describe('_createSlotMetric', () => {
            let adMetricsManager;
        
            beforeEach(() => {
                adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                const settings = {
                    trackPageMetrics: true,
                    trackSlotMetrics: true,
                    sendMetricsToKafka: false,
                    useLocalOrion: false
                };

                adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
            });

            it('should set _firstSlotId if none yet specified', () => {
                adMetricsManager._createSlotMetric({ instigator: 'slotA' });

                expect(adMetricsManager._firstSlotId).to.equal('slotA');
            });

            it('should not change _firstSlotId if already set', () => {
                adMetricsManager._createSlotMetric({ instigator: 'slotA' });
                adMetricsManager._createSlotMetric({ instigator: 'slotB' });

                expect(adMetricsManager._firstSlotId).to.equal('slotA');
            });

            it('should listen for all slot metric events for new slot id', () => {
                adMetricsManager._createSlotMetric({ instigator: 'slotA' });
                expect(adMetricsManager._events.subscribe).to.have.been.calledWith('slotMetric', 'slotA', sinon.match.any, 'on');
            });
        });

        describe('_pageMetricsComplete', () => {
            let adMetricsManager;
        
            beforeEach(() => {
                adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                const settings = {
                    trackPageMetrics: true,
                    trackSlotMetrics: true,
                    sendMetricsToKafka: false,
                    useLocalOrion: false
                };

                adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
            });

            it('should call _pushMetrics for the page metrics instance and unSubscribe from future events', () => {
                adMetricsManager._pageMetrics.pushMetrics = sinon.stub();
                adMetricsManager._pageMetricsComplete();
                expect(adMetricsManager._subscriptions.page.unSubscribe).to.have.been.called;
                expect(adMetricsManager._pageMetrics.pushMetrics).to.have.been.called;
            });
        });

        describe('_logSlotMetrics', () => {
            let adMetricsManager;
        
            beforeEach(() => {
                adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                const settings = {
                    trackPageMetrics: true,
                    trackSlotMetrics: true,
                    sendMetricsToKafka: false,
                    useLocalOrion: false
                };

                adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
            });

            it('should logMetrics and not call pushMetrics and removeCategories if requiredMetricsComplete is false', () => {
                adMetricsManager._slotMetrics.slotA = {
                    logMetrics: sinon.stub(),
                    pushMetrics: sinon.stub(),
                    requiredMetricsComplete: sinon.stub().callsFake(() => (false)),
                    removeCategories: sinon.stub()
                };

                adMetricsManager._logSlotMetrics({
                    instigator: 'slotA',
                    metrics: { gam: { creativeId: 'abcdefg' } }
                });

                expect(adMetricsManager._slotMetrics.slotA.logMetrics).to.have.been.calledWith({ gam: { creativeId: 'abcdefg' } });
                expect(adMetricsManager._slotMetrics.slotA.requiredMetricsComplete).to.have.been.called;
                expect(adMetricsManager._slotMetrics.slotA.pushMetrics).to.not.have.been.called;
                expect(adMetricsManager._slotMetrics.slotA.removeCategories).to.not.have.been.called;
            });

            it('should call logMetrics, pushMetrics, setPosition and removeCategories for the slot if required metrics are complete', () => {
                adMetricsManager._slotMetrics.slotA = {
                    logMetrics: sinon.stub(),
                    pushMetrics: sinon.stub(),
                    requiredMetricsComplete: () => (true),
                    removeCategories: sinon.stub(),
                    setPosition: sinon.stub()
                };

                adMetricsManager._logSlotMetrics({
                    instigator: 'slotA',
                    metrics: { gam: { creativeId: 'abcdefg' } }
                });

                expect(adMetricsManager._slotMetrics.slotA.logMetrics).to.have.been.calledWith({ gam: { creativeId: 'abcdefg' } });
                expect(adMetricsManager._slotMetrics.slotA.pushMetrics).to.have.been.called;
                expect(adMetricsManager._slotMetrics.slotA.removeCategories).to.have.been.called;
                expect(adMetricsManager._slotMetrics.slotA.setPosition).to.have.been.called;
            });

            it('should call _pageMetricsComplete if requiredMetricsComplete is true and this is the first slot', () => {
                adMetricsManager._slotMetrics.slotA = {
                    logMetrics: sinon.stub(),
                    pushMetrics: sinon.stub(),
                    requiredMetricsComplete: () => (true),
                    removeCategories: sinon.stub(),
                    setPosition: sinon.stub()
                };
                adMetricsManager._pageMetricsComplete = sinon.stub();
                adMetricsManager._firstSlotId = 'slotA';

                adMetricsManager._logSlotMetrics({
                    instigator: 'slotA',
                    metrics: { gam: { creativeId: 'abcdefg' } }
                });

                expect(adMetricsManager._pageMetricsComplete).to.have.been.called;
            });

            it('should log isFirst to false when a slot\'s metrics are completed and pushed', () => {
                adMetricsManager._slotMetrics.slotA = {
                    logMetrics: sinon.stub(),
                    pushMetrics: sinon.stub(),
                    requiredMetricsComplete: () => (true),
                    removeCategories: sinon.stub(),
                    setPosition: sinon.stub()
                };
                adMetricsManager._pageMetricsComplete = sinon.stub();
                adMetricsManager._firstSlotId = 'slotA';

                adMetricsManager._logSlotMetrics({
                    instigator: 'slotA',
                    metrics: { gam: { creativeId: 'abcdefg' } }
                });

                expect(adMetricsManager._slotMetrics.slotA.logMetrics).to.have.been.calledWith({ isFirst: false });
            });

            it('should not call _pageMetricsComplete if requiredMetricsComplete is true and this is not the first slot', () => {
                adMetricsManager._slotMetrics.slotB = {
                    logMetrics: sinon.stub(),
                    pushMetrics: sinon.stub(),
                    requiredMetricsComplete: () => (true),
                    removeCategories: sinon.stub(),
                    setPosition: sinon.stub()
                };
                adMetricsManager._pageMetricsComplete = sinon.stub();
                adMetricsManager._firstSlotId = 'slotA';

                adMetricsManager._logSlotMetrics({
                    instigator: 'slotB',
                    metrics: { gam: { creativeId: 'abcdefg' } }
                });

                expect(adMetricsManager._pageMetricsComplete).to.not.have.been.called;
            });
        });

        describe('pushMetrics', () => {
            let adMetricsManager;

            describe('tracking page and slot metrics', () => {
                beforeEach(() => {
                    adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                    const settings = {
                        trackPageMetrics: true,
                        trackSlotMetrics: true,
                        sendMetricsToKafka: false,
                        useLocalOrion: false
                    };
    
                    adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
                });
    
                it('should push metrics if the visitor is having their metrics tracked', () => {
                    adMetricsManager.pushMetrics('slotMetric', {
                        instigator: 'slotA',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events.publish).to.have.been.calledWith('slotMetric', {
                        instigator: 'slotA',
                        metrics: { foo: 'bar' }
                    });
                });
    
                it('should not push metrics if the metric type is not valid', () => {
                    adMetricsManager.pushMetrics('slot', {
                        instigator: 'slotA',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events.publish).to.not.have.been.called;
                });
    
                it('should not push metrics if the instigator key value pair is missing', () => {
                    adMetricsManager.pushMetrics('slotMetric', { metrics: { foo: 'bar' } });
                    
                    expect(adMetricsManager._events.publish).to.not.have.been.called;
                });
    
                it('should not push metrics if the metric key value pair is missing', () => {
                    adMetricsManager.pushMetrics('slotMetric', { instigator: 'slotA' });
                    
                    expect(adMetricsManager._events.publish).to.not.have.been.called;
                });
            });

            describe('tracking only page metrics', () => {
                beforeEach(() => {
                    adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                    const settings = {
                        trackPageMetrics: true,
                        trackSlotMetrics: false,
                        sendMetricsToKafka: false,
                        useLocalOrion: false
                    };
    
                    adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
                });

                it('should push metrics if metrics are for the first slot', () => {
                    adMetricsManager._firstSlotId = 'slotA';
                    adMetricsManager.pushMetrics('slotMetric', {
                        instigator: 'slotA',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events.publish).to.have.been.called;
                });

                it('should not push metrics if metrics are for a slot that is not the first slot', () => {
                    adMetricsManager._firstSlotId = 'slotA';
                    adMetricsManager.pushMetrics('slotMetric', {
                        instigator: 'slotB',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events.publish).to.not.have.been.called;
                });
            });

            describe('all metric tracking turned off', () => {
                beforeEach(() => {
                    adMetricsManager = new Mntl.AdMetrics.AdMetricsManager();

                    const settings = {
                        trackPageMetrics: false,
                        trackSlotMetrics: false,
                        sendMetricsToKafka: false,
                        useLocalOrion: false
                    };
    
                    adMetricsManager.init(settings, 'docId', 'requestId', ['slotA', 'slotB'], Date.now());
                });

                it('adMetricsManager._events should not exist and no metrics will be pushed', () => {
                    adMetricsManager.pushMetrics('pageMetric', {
                        instigator: 'RTB',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events).to.be.undefined;
                });

                it('adMetricsManager._events should not exist and no metrics will be pushed', () => {
                    adMetricsManager._firstSlotId = 'slotA';
                    adMetricsManager.pushMetrics('slotMetric', {
                        instigator: 'slotA',
                        metrics: { foo: 'bar' }
                    });
                    
                    expect(adMetricsManager._events).to.be.undefined;
                });
            });
        });
    });

    // Library init
    describe('Mntl.AdMetrics.init', () => {

        afterEach((done) => {
            Mntl.utilities.getQueryParams.restore();
            done();
        });

        it('should initialize the _adMetricsManager to track page and slot metrics', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1100' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: true,
                sendMetricsToKafka: false,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to track page metrics only', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1000' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: false,
                sendMetricsToKafka: false,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to not track metrics', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '0000' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: false,
                trackSlotMetrics: false,
                sendMetricsToKafka: false,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to not track metrics', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '0100' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: false,
                trackSlotMetrics: false,
                sendMetricsToKafka: false,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to send metrics to kafka', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1110' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: true,
                sendMetricsToKafka: true,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to not send metrics to kafka', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1100' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: true,
                sendMetricsToKafka: false,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to use local orion', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1111' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: true,
                sendMetricsToKafka: true,
                useLocalOrion: true
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });

        it('should initialize the _adMetricsManager to not use local orion', () => {
            // 11 = page metrics on, slot metrics on
            // 10 = page metrics on, slot metrics off
            // 00 = all metrics off
            // 01 = will also set all metrics off - can't track slot metrics without page metrics
            sinon.stub(Mntl.utilities, 'getQueryParams').callsFake(() => ({ adMetricTracking: '1110' }));
            Mntl.AdMetrics._adMetricsManager.init = sinon.stub();

            Mntl.AdMetrics.init('docId', 'requestId', ['slotA', 'slotB'], 1665511691023);

            const settings = {
                trackPageMetrics: true,
                trackSlotMetrics: true,
                sendMetricsToKafka: true,
                useLocalOrion: false
            };

            expect(Mntl.AdMetrics._adMetricsManager.init).to.have.been.calledWith(settings, sinon.match.any, sinon.match.any, sinon.match.any, sinon.match.any);
        });
    });
});