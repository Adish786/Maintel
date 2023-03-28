/* eslint-disable camelcase */
/* eslint no-unused-expressions: "off" */
describe('Mntl.RTB', () => {
    let Mntl;

    function load(done) {
        Mntl.utilities.loadExternalJS(
            { src: '/base/mantle-resources/src/main/resources/static/js/RTB.js' },
            done
        );
    }

    // Helper methods for testing plugins
    function Slot(config, targeting) {
        this.config = config;
        this.updateTargeting = sinon.spy();
        this.gptSlot = { getAdUnitPath: () => `/test/path/${config.id}` };
        this.targeting = targeting;
        this.getFloorValueCents = () => 50;
    }

    function getTestSlots() {
        return [
            new Slot({
                id: 'leaderboard',
                sizes: [[728, 90], [970, 250], [970, 90], [468, 60], [1, 1]]
            }),
            new Slot({
                id: 'billboard',
                sizes: [[300, 250], [300, 600], [160, 600], [300, 1050], [1, 2]]
            }),
            new Slot({
                id: 'dynamic-inline1',
                sizes: [[300, 250], [300, 600], [160, 600], [300, 1050], [1, 2]]
            }),
            new Slot({
                id: 'dynamic-inline2',
                sizes: [[300, 250], [300, 600], [160, 600], [300, 1050], [1, 2]]
            }),
            new Slot({
                id: 'leaderboardac',
                sizes: [[728, 90], [970, 250], 'fluid']
            })
        ];
    }

    function getLmdGpidSlots() {
        return [
            {
                config: {
                    id: 'square-flex-1',
                    sizes: [[300, 250], [300, 600], [300, 1050]]
                },
                gptSlot: { getAdUnitPath: () => '3865/hlt.mdp.com/tier1/none/Wellness' },
                getFloorValueCents: () => 50,
                expectedLmdGpid: '/3865/hlt.mdp.com/tier1/na/div-gpt-square-flex-1'
            },
            {
                config: {
                    id: 'mob-square-flex-1',
                    sizes: [[300, 250]]
                },
                gptSlot: { getAdUnitPath: () => '/3865/hlt.mdp.mob/tier1/article' },
                getFloorValueCents: () => 50,                   
                expectedLmdGpid: '/3865/hlt.mdp.mob/tier1/article/div-gpt-mob-square-flex-1'
            }
        ];
    }

    const videoSlot = {
        config: {
            sizes: [[300, 170]],
            type: 'video',
            params: { type: 'instream' },
            id: 'preroll'
        },
        getFloorValueCents: () => false
    };

    const railVideoSlot = {
        config: {
            sizes: [[300, 170]],
            type: 'video',
            id: 'mntl-rail-jwplayer'
        },
        getFloorValueCents: () => false
    };

    beforeEach(() => {
        window.docCookies = { getItem: sinon.stub() };
        Mntl = window.Mntl || {};
        Mntl.GPT = {};
        Mntl.GPT.isMobile = () => false;
        Mntl.GPT.updatePageTargeting = () => true;
        Mntl.GPT.getPageTargeting = () => ({ category: 'testCategory' });
        Mntl.GPT.getUseLmdFormat = () => false;
        Mntl.GPT.getUseOxygen = () => false;
        Mntl.GPT.getBundlePrebid = () => false;
        Mntl.GPT.getPageType = () => 'article';
        Mntl.GPT.getSitePath = () => 'hlt.mdp.com';
        Mntl.GPT.getDfpId = () => '3865';
        Mntl.PubSub = function() {
            return {
                what: sinon.stub(),
                addEvent: sinon.stub(),
                deleteEvent: sinon.stub(),
                publish: sinon.stub(),
                subscribe: sinon.stub(),
                on: sinon.stub(),
                once: sinon.stub()
            };
        };

        Mntl.CMP = {
            isConsentRequired: sinon.stub(),
            hasTargetingConsent: sinon.stub(),
            onConsentChange: sinon.stub(),
            isOptInConsent: sinon.stub()
        };

        Mntl.AdMetrics = { pushMetrics: sinon.stub() };

        cmpTestUtils = {
            gdpr() {
                Mntl.CMP.isConsentRequired.returns(true);
            },
            setTargetingConsent(consent) {
                Mntl.CMP.hasTargetingConsent.returns(consent);
            },
            setIsOptInConsent(isOptInConsent) {
                Mntl.CMP.isOptInConsent.returns(isOptInConsent);
            }
        }
    });

    // init(gptSlots, fn)
    describe('init', () => {
        it.skip('init calls private method and is untestable');
    });

    // refresh(gptSlots, fn)
    describe('refresh', () => {
        it.skip('refresh calls a private method and is untestable');
    });

    describe('rtb timeout configurations', () => {
        beforeEach((done) => {
            load(done);
        });

        it('should get the default RTB timeout', () => {
            expect(Mntl.RTB.getTimeoutLength()).to.equal(500);
        });

        it('should set the RTB timeout', () => {
            const oneSecond = 1000;

            Mntl.RTB.setTimeoutLength(oneSecond);
            expect(Mntl.RTB.getTimeoutLength()).to.equal(oneSecond);
        });
    });

    describe('Plugins', () => {
        const expectedNetworks = ['noop', 'prebid', 'amazon', 'ixid'];

        it('Should be an object of networks', () => {
            expect(Mntl.RTB.Plugins)
                .to.be.an('object')
                .and.to.contain.all.keys(expectedNetworks);
        });

        // noop
        describe('Plugins.noop', () => {
            it('Should be an object with a setup method', () => {
                expect(Mntl.RTB.Plugins.noop)
                    .to.be.an('object')
                    .and.to.contain.all.keys('preload', 'src', 'setup');
            });
        });

        // index identity
        describe('index identity (ixid)', () => {
            it('Should load ixid when prebid contains IX', () => {
                cmpTestUtils.setIsOptInConsent(false);
                Mntl.RTB.Plugins.prebid.setConfig({
                    leaderboard: [{ bidder: 'ix' }]
                });
                expect(Mntl.RTB.Plugins.ixid.src()).to.equal('//js-sec.indexww.com/ht/p/183710-16979354036509.js');
            });

            it('Should not load ixid when prebid does not contain IX', () => {
                cmpTestUtils.setIsOptInConsent(false);
                Mntl.RTB.Plugins.prebid.setConfig({
                    leaderboard: [{ bidder: 'rubicon' }]
                });
                expect(Mntl.RTB.Plugins.ixid.src()).to.equal(false);
            });
        });

        // Amazon
        describe('Amazon', () => {
            let displayApstagSlots;
            let videoApstagSlot;
            let railVideoApstagSlot;

            beforeEach((done) => {
                displayApstagSlots = [{
                    slotID: 'leaderboard',
                    sizes: [[728, 90], [970, 250], [970, 90], [468, 60]],
                    floor: {
                        currency: 'USD',
                        value: 50
                    },
                    slotParams: { fb_pid: 'thisIsMyTestDefaultID' } // eslint-disable-line camelcase
                }, {
                    slotID: 'billboard',
                    sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]],
                    floor: {
                        currency: 'USD',
                        value: 50
                    },
                    slotParams: { fb_pid: 'thisIsMyTestDefaultID' } // eslint-disable-line camelcase
                }, {
                    slotID: 'dynamic-inline1',
                    sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]],
                    floor: {
                        currency: 'USD',
                        value: 50
                    },
                    slotParams: { fb_pid: 'thisIsMyTestDefaultID' } // eslint-disable-line camelcase
                }, {
                    slotID: 'dynamic-inline2',
                    sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]],
                    floor: {
                        currency: 'USD',
                        value: 50
                    },
                    slotParams: { fb_pid: 'thisIsMyTestDefaultID' } // eslint-disable-line camelcase
                }, {
                    slotID: 'leaderboardac',
                    sizes: [[728, 90], [970, 250]],
                    floor: {
                        currency: 'USD',
                        value: 50
                    },
                    slotParams: { fb_pid: 'thisIsMyTestDefaultID' } // eslint-disable-line camelcase
                }];

                videoApstagSlot = {
                    mediaType: 'video',
                    slotID: 'video',
                    sizes: [[300, 170]],
                    slotName: '3865/hlt.mdp.video/article/preroll',
                    slotParams: {
                        // eslint-disable-next-line camelcase
                        fb_pid: 'thisIsMyTestDefaultID'
                    }
                };

                railVideoApstagSlot = {
                    mediaType: 'video',
                    slotID: 'video-rail',
                    sizes: [[300, 170]],
                    slotName: '3865/hlt.mdp.video/article/preroll',
                    slotParams: {
                        // eslint-disable-next-line camelcase
                        fb_pid: 'thisIsMyTestDefaultID'
                    }
                };

                window.apstag = {
                    init: sinon.stub(),
                    debug: sinon.stub(),
                    fetchBids: sinon.stub(),
                    targetingKeys: () => ['amzniid', 'amznbid', 'deal_id_123_300x300amzniid']
                };
                load(done);
            });

            afterEach(() => {
                delete window.apstag;
            });

            it('Should use the correct src', () => {
                expect(Mntl.RTB.Plugins.amazon.src()).to.equal('//c.amazon-adsystem.com/aax2/apstag.js');
            });

            describe('setup', () => {
                let slots;
                let callback;

                beforeEach(() => {
                    callback = sinon.spy();
                    slots = getTestSlots();
                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    Mntl.RTB.Plugins.amazon.scriptsLoaded = true;
                });

                it('Should fetch bids for slots', () => {
                    Mntl.RTB.Plugins.amazon.setup(slots, callback);
                    expect(window.apstag.fetchBids).to.have.been.calledWith({
                        slots: displayApstagSlots,
                        timeout: sinon.match.number
                    }, sinon.match.func);
                });

                it('Should return early if no slots to bid on', () => {
                    slots = [];
                    Mntl.RTB.Plugins.amazon.setup(slots, callback);

                    expect(window.apstag.fetchBids).to.have.not.been.called;
                    expect(callback).to.have.been.called;
                });

                it('Should set bid targeting on slots', () => {
                    /* 
                        With amazon using a promise to get its targeting instead of a callback, 
                        we want to run the test at the point the 'done' callback has been fired
                    */
                    Mntl.RTB.Plugins.amazon.setup(slots, () => {
                        slots.forEach((slot) => {
                            if ((slot.config.id === 'billboard') || (slot.config.id === 'leaderboard')) {
                                expect(slot.updateTargeting).to.have.been.calledWith({
                                    amznbid: 'amznbid',
                                    amzniid: 'amzniid'
                                });
                            }
                        });
                    });
                    window.apstag.fetchBids.callArgWith(1, [{
                        slotID: 'leaderboard',
                        targeting: {
                            amznbid: 'amznbid',
                            amzniid: 'amzniid'
                        }
                    }, {
                        slotID: 'billboard',
                        targeting: {
                            amznbid: 'amznbid',
                            amzniid: 'amzniid'
                        }
                    }]);
                });

                it('should not request bids if scriptsLoaded is false and should fire callback', () => {
                    Mntl.RTB.Plugins.amazon.scriptsLoaded = false;
                    Mntl.RTB.Plugins.amazon.setup(slots, callback);

                    expect(window.apstag.fetchBids).to.have.not.been.called;
                    expect(callback).to.have.been.called;
                });
            });

            describe('initialize', () => {
                it('Should initialize amazon api', () => {
                    Mntl.RTB.Plugins.amazon.amazonConfigs.amazonSection = 'TaxonomyHeading';
                    Mntl.RTB.Plugins.amazon.id = 123;
                    Mntl.RTB.Plugins.amazon.initialize();
                    expect(window.apstag.init).to.have.been.calledWith({
                        deals: true,
                        pubID: 123,
                        adServer: 'googletag',
                        videoAdServer: 'DFP',
                        section: 'TaxonomyHeading',
                        params: { si_pagegroup: 'testCategory'}, // eslint-disable-line camelcase
                        blockedBidders: [],
                        sis_sitesection: 'localhost:TaxonomyHeading', // eslint-disable-line camelcase
                        schain: {
                            complete: 1,
                            ver: '1.0',
                            nodes: []
                        }
                    });
                });

                it('Should not reinitialize amazon api', () => {
                    Mntl.RTB.Plugins.amazon.id = 123;
                    Mntl.RTB.Plugins.amazon.initialize();
                    Mntl.RTB.Plugins.amazon.initialize();
                    expect(window.apstag.init).to.have.been.calledOnce;
                });

                it('Should enable amazon debug mode only in Mntl debug mode', () => {
                    const prev = Mntl.DEBUG;

                    Mntl.DEBUG = true;
                    Mntl.RTB.Plugins.amazon.id = 123;
                    Mntl.RTB.Plugins.amazon.initialize();
                    expect(window.apstag.debug).to.have.been.calledWith('enable');
                    Mntl.DEBUG = prev;
                });
            });

            describe('clearTargeting', () => {
                const slots = getTestSlots();

                slots.forEach((slot) => {
                    slot.targeting = {
                        amzniid: 12234,
                        amznbid: 'biddy',
                        deal_id_123_300x300amzniid: '378q34890743q089', // eslint-disable-line camelcase
                        aolbid: '123',
                        mpalias: 'abc'

                    };
                    slot.updateTargeting = function(newTargeting) {
                        Object.keys(newTargeting).forEach((key) => {
                            if (!newTargeting[key]) {
                                delete slot.targeting[key];
                            }
                        });
                    };
                });

                it('Should clear any previous amazon targeting', () => {
                    Mntl.RTB.Plugins.amazon.clearTargeting(slots);
                    slots.forEach((slot) => {
                        expect(slot.targeting).to.deep.equal({
                            aolbid: '123',
                            mpalias: 'abc'
                        });
                    });
                });
            });

            // TODO: Enable and fix in GLBE-8677
            describe('getDisplayApstagSlots', () => {
                it('Should filter out sizes with 1px and filter out slots with no sizes', () => {
                    const slots = getTestSlots();

                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;

                    slots.push(new Slot({
                        id: 'toBeFilteredOut',
                        sizes: [[1, 2]]
                    }));

                    expect(Mntl.RTB.Plugins.amazon.getDisplayApstagSlots(slots)).to.deep.equal(displayApstagSlots);
                });
                it('should have a slotName property on each Amazon slot if getUseLmdFormat is true that matches the slot\'s GPID', () => {
                    const slots = getLmdGpidSlots();
                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;

                    Mntl.GPT.getUseLmdFormat = () => true;
                    const slotsForTam = Mntl.RTB.Plugins.amazon.getDisplayApstagSlots(slots);

                    for (let i=0, l=slotsForTam.length; i<l; i++){
                        const tamSlot = slotsForTam[i];
                        const slot = slots[i];

                        expect(tamSlot.slotName).to.equal(Mntl.RTB.generateGpid(slot, true));
                    }
                });
            });

            // TODO: Enable and fix in GLBE-8677
            describe('getVideoApstagSlot', () => {
                it('Should create the video slot we need to to initiate bids', () => {
                    const slot = videoSlot;
                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    
                    expect(Mntl.RTB.Plugins.amazon.getVideoApstagSlot(slot)).to.deep.equal(videoApstagSlot);
                });

                it('Should create the video slot we need to to initiate bids with slotID for right rail video', () => {
                    const slot = railVideoSlot;

                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    expect(Mntl.RTB.Plugins.amazon.getVideoApstagSlot(slot)).to.deep.equal(railVideoApstagSlot);
                });
            });

            // TODO: Enable and fix in GLBE-8677
            describe('mapFBValues', () => {
                it('Should set a slotParams field in the slot config if mapFBValues is set on display', () => {
                    const slots = getTestSlots();

                    displayApstagSlots[0].slotParams.fb_pid = 'thisIsMyTestLeaderboardID'; // eslint-disable-line camelcase

                    const testFBMap = {
                        default: 'thisIsMyTestDefaultID',
                        leaderboard: 'thisIsMyTestLeaderboardID'
                    };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;

                    expect(Mntl.RTB.Plugins.amazon.getDisplayApstagSlots(slots)).to.deep.equal(displayApstagSlots);
                });

                it('Should set a slotParams field in the slot config if mapFBValues is set on video', () => {
                    const slot = videoSlot;

                    videoApstagSlot.slotParams.fb_pid = 'thisIsMyTestVideoID'; // eslint-disable-line camelcase

                    const testFBMap = {
                        default: 'thisIsMyTestDefaultID',
                        preroll: 'thisIsMyTestVideoID'
                    };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    expect(Mntl.RTB.Plugins.amazon.getVideoApstagSlot(slot)).to.deep.equal(videoApstagSlot);
                });

                it('Should set mapFBValues to the default value when a key matching the slot id does not exist for display', () => {
                    const slots = getTestSlots();

                    displayApstagSlots[0].slotParams.fb_pid = 'thisIsMyTestDefaultID'; // eslint-disable-line camelcase

                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    expect(Mntl.RTB.Plugins.amazon.getDisplayApstagSlots(slots)).to.deep.equal(displayApstagSlots);
                });

                it('Should set mapFBValues to the default value when a key matching the slot id does not exist for video', () => {
                    const slot = videoSlot;

                    videoApstagSlot.slotParams.fb_pid = 'thisIsMyTestDefaultID'; // eslint-disable-line camelcase

                    const testFBMap = { default: 'thisIsMyTestDefaultID' };

                    Mntl.RTB.Plugins.amazon.amazonConfigs.mapFBValues = testFBMap;
                    expect(Mntl.RTB.Plugins.amazon.getVideoApstagSlot(slot)).to.deep.equal(videoApstagSlot);
                });
            });
        });

        // Prebid
        describe('prebid', () => {
            beforeEach('set config', () => {
                window.pbjs = {
                    que: {
                        push(fn) {
                            fn(); // execute immediately
                        }
                    },
                    aliasBidder: sinon.stub(),
                    onEvent: sinon.stub(),
                    getAdserverTargeting: sinon.stub(),
                    getAdserverTargetingForAdUnitCode: sinon.stub(),
                    // eslint-disable-next-line no-unused-labels
                    getBidResponsesForAdUnitCode: sinon.stub().callsFake(() => ({ bids: [] })),
                    requestBids: sinon.stub().yieldsTo('bidsBackHandler'),
                    getConfig: sinon.stub(),
                    setConfig: sinon.stub(),
                    mergeConfig: sinon.stub(),
                    setBidderConfig: sinon.stub()
                };
                Mntl.RTB.setTaxonomyStampValues({
                    foo: 'bar',
                    tax0: 'val0',
                    tax1: 'val1',
                    tax2: 'val2',
                    tax3: 'val3',
                    tax4: 'val4',
                    tax5: 'val5'
                });
                Mntl.RTB.Plugins.prebid.setConfig({
                    leaderboard: [{
                        bidder: 'foo',
                        params: {
                            param1: 'value1',
                            param2: 2
                        }
                    }, {
                        bidder: 'bar',
                        params: {
                            param1: 'value1',
                            param2: 3
                        }
                    }, {
                        bidder: 'ix',
                        params: {
                            param1: 'value3',
                            param2: 3
                        }
                    }],
                    billboard: [{
                        bidder: 'foo',
                        params: {
                            param1: 'value2',
                            param2: 2
                        }
                    }, {
                        bidder: 'bar',
                        params: {
                            param1: 'value2',
                            param2: 3
                        }
                    }, {
                        bidder: 'rubicon',
                        params: {
                            param1: 'value3',
                            param2: 3
                        }
                    }, {
                        bidder: 'pgRubicon',
                        params: {
                            param1: 'value4',
                            param2: 4
                        }
                    }, {
                        bidder: 'grid',
                        params: { param1: 1 }
                    }, {
                        bidder: 'criteopg',
                        params: {}
                    }, {
                        bidder: 'trustx',
                        params: {}
                    }],
                    leaderboardac: [{
                        bidder: 'foo',
                        params: {
                            param1: 'value1',
                            param2: 2
                        }
                    }, {
                        bidder: 'foo',
                        params: {
                            type: 'outstream',
                            param1: 'value1',
                            param2: 2
                        }
                    }],
                    preroll: [{
                        bidder: 'foo',
                        params: {
                            type: 'instream',
                            param1: 'value1',
                            param2: 2
                        }
                    }]
                });
                Mntl.RTB.Plugins.prebid.setPriceGranularity({
                    buckets: [{
                        max: '20',
                        increment: '0.05'
                    }]
                });
                Mntl.RTB.Plugins.prebid.setPrebidConfig = sinon.stub();
            });
            describe('src', () => {
                let getStaticPathStub;

                before(() => {
                    getStaticPathStub = sinon.stub(window.Mntl.utilities, 'getStaticPath');
                });

                after(() => {
                    getStaticPathStub.restore();
                });

                it('should return false if getBundlePrebid is true', () => {
                    Mntl.GPT.getBundlePrebid = () => true;
                    if (Mntl.GPT.getBundlePrebid()) {
                        expect(Mntl.RTB.Plugins.prebid.src()).to.equal(false);
                    }
                });

                it('should return prebid file if getBundlePrebid is false', () => {
                    Mntl.GPT.getBundlePrebid = () => false;
                    if (!Mntl.GPT.getBundlePrebid()) {
                        getStaticPathStub.returns('/static/VERTICAL-VERSION');
                        expect(Mntl.RTB.Plugins.prebid.src()).to.equal('/static/VERTICAL-VERSION/static/mantle/static/js/prebidjs/default/dist/prebid.js');
                    }
                });
            });
            describe('preload', () => {
                describe('on legacy DD', () => {
                    beforeEach('run preload function', () => {
                        Mntl.RTB.Plugins.prebid.preload();
                    });
                    it('should call setPrebidConfig with s2sConfig settings', () => {
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            s2sConfig: {
                                accountId: '7499',
                                bidders: ['pgRubicon'],
                                timeout: 500,
                                adapter: 'prebidServer',
                                enabled: true,
                                endpoint: 'https://pg-prebid-server.rubiconproject.com/openrtb2/auction',
                                syncEndpoint: 'https://pg-prebid-server.rubiconproject.com/cookie_sync',
                                extPrebid: {
                                    cache: { vastxml: {} },
                                    aliases: { pgRubicon: 'rubicon' }
                                }
                            }
                        });
                    });

                    it('should call setPrebidConfig with the custom price granularity', () => {
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            priceGranularity: {
                                buckets: [{
                                    increment: 0.05,
                                    max: 20
                                }]
                            }
                        });
                    });

                    it('should call setPrebidConfig with the global ortb2 first party data', () => {
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            ortb2: {
                                site: {
                                    ext: {
                                        data: {
                                            tax0: 'val0',
                                            tax1: 'val1',
                                            tax2: 'val2',
                                            tax3: 'val3'
                                        }
                                    }
                                }
                            }
                        });
                    });

                    it('should call setPrebidConfig with ias realTimeData configuration', () => {
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            realTimeData: {
                                dataProviders: [{
                                    name: 'ias',
                                    waitForIt: true,
                                    params: {
                                        pubId: 926268,
                                        keyMappings: { 
                                            adt: 'rtd_ias_adt',
                                            alc: 'rtd_ias_alc',
                                            dlm: 'rtd_ias_dlm',
                                            hat: 'rtd_ias_hat',
                                            off: 'rtd_ias_off',
                                            vio: 'rtd_ias_vio',
                                            drg: 'rtd_ias_drg',
                                            'ias-kw': 'rtd_ias_ias-kw',
                                            fr: 'rtd_ias_fr',
                                            vw: 'rtd_ias_vw',
                                            grm: 'rtd_ias_grm',
                                            pub: 'rtd_ias_pub',
                                            vw05: 'rtd_ias_vw05',
                                            vw10: 'rtd_ias_vw10',
                                            vw15: 'rtd_ias_vw15',
                                            vw30: 'rtd_ias_vw30',
                                            vw_vv: 'rtd_ias_vw_vv', // eslint-disable-line camelcase
                                            grm_vv: 'rtd_ias_grm_vv', // eslint-disable-line camelcase
                                            pub_vv: 'rtd_ias_pub_vv', // eslint-disable-line camelcase
                                            id: 'rtd_ias_id'
                                        }
                                    }
                                }]
                            }
                        })
                    });

                    it('should call setPrebidConfig with the first-party data when index is a bidder', () => {
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            ix: {
                                firstPartyData: {
                                    tax0: 'val0',
                                    tax1: 'val1',
                                    tax2: 'val2',
                                    tax3: 'val3'
                                }
                            }
                        });
                    });
                });

                describe('on mobile', () => {
                    it('should call setPrebidConfig and set ortb2.site.mobile to 1', () => {
                        Mntl.GPT.isMobile = () => true;
                        Mntl.RTB.Plugins.prebid.preload();
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            ortb2: {
                                site: { mobile: 1 }
                            }
                        });
                    });
                });


                describe('on desktop', () => {
                    it('should call setPrebidConfig and set ortb2.site.mobile to 0', () => {
                        Mntl.GPT.isMobile = () => false;
                        Mntl.RTB.Plugins.prebid.preload();
                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            ortb2: {
                                site: { mobile: 0 }
                            }
                        });
                    });
                });

                describe('on legacy MD', () => {
                    it('should call setPrebidConfig with bid caching enabled for display on LMD sites', () => { 
                        Mntl.GPT.getUseLmdFormat = () => true;
                        Mntl.RTB.Plugins.prebid.preload();

                        const isBidNotVideo = (bid) => bid.mediaType !== 'video';

                        expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({
                            useBidCache: true,
                            bidCacheFilterFunction: sinon.match(isBidNotVideo)
                        });
                    });
                });

                describe('with msg2 data', () => {
                    beforeEach('run preload function and set msg2', () => {
                        localStorage.msg2 = '["2880","2881"]'
                        Mntl.RTB.Plugins.prebid.preload();
                    });
                    
                    it('should call pbjs.setBidderConfig for TrustX and Rubicon with msg2 data if msg2 data exists ', () => {
                        const config = {
                            bidders: [ 'trustx', 'rubicon' ],
                            config: {
                                ortb2: {
                                    user: {
                                        data: [{
                                            name: 'dotdashmeredith.com',
                                            ext: { segtax: 4 },
                                            segment: [
                                                { id: '2880' }, 
                                                { id: '2881' }
                                            ]
                                        }]
                                    }
                                }
                            }
                        };

                        expect(window.pbjs.setBidderConfig).to.have.been.calledWith(config);
                    });
                });
            });
            describe('setup', () => {
                it('should request bids and update targeting when the slot is in the config', (done) => {
                    const slots = getTestSlots();

                    window.pbjs.getAdserverTargeting.returns({
                        leaderboard: { fooTargeting: 'fooValue' },
                        billboard: { barTargeting: 'barValue' }
                    });

                    Mntl.RTB.Plugins.prebid.scriptsLoaded = true;
                    Mntl.RTB.Plugins.prebid.setup(slots, () => {
                        expect(window.pbjs.requestBids).to.have.been.called;
                        expect(slots[0].updateTargeting).to.have.been.calledWith({ fooTargeting: 'fooValue' });
                        expect(slots[1].updateTargeting).to.have.been.calledWith({ barTargeting: 'barValue' });
                        expect(slots[2].updateTargeting).to.not.have.been.called;
                        expect(slots[3].updateTargeting).to.not.have.been.called;
                        done();
                    });
                });

                it('should not request bids when the slot is not in the config', (done) => {
                    Mntl.RTB.Plugins.prebid.setup([{ config: { id: 'bin' } }], () => {
                        expect(window.pbjs.requestBids).to.not.have.been.called;
                        done();
                    });
                });

                it('should not request bids when scriptsLoaded is false and should fire callback', () => {
                    const slots = getTestSlots();
                    const callback = sinon.spy();

                    Mntl.RTB.Plugins.prebid.scriptsLoaded = false;
                    Mntl.RTB.Plugins.prebid.setup(slots, callback);

                    expect(window.pbjs.requestBids).to.not.have.been.called;
                    expect(callback).to.have.been.called;
                });
            });
            describe('prebid setPrebidDeviceAccess', () => {
                it('should not call setPrebidConfig to disable device access when there is targeting consent', () => {
                    cmpTestUtils.setTargetingConsent(true);
                    cmpTestUtils.gdpr();
                    cmpTestUtils.setIsOptInConsent(true);
                    Mntl.RTB.Plugins.prebid.preload();
                    Mntl.CMP.onConsentChange.yield();
                    expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.not.been.calledWith({ deviceAccess: false });
                });
                it('should call setPrebidConfig to disable device access when there is no targeting consent', () => {
                    cmpTestUtils.setTargetingConsent(false);
                    cmpTestUtils.gdpr();
                    cmpTestUtils.setIsOptInConsent(true);
                    Mntl.RTB.Plugins.prebid.preload();
                    Mntl.CMP.onConsentChange.yield();
                    expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith({ deviceAccess: false });
                });
            });
            describe('setting of outstream renderer', () => {
                it('should add renderer when bidder is "grid" and mediaType is "video"', () => {
                    const bid = {
                        bidder: 'grid',
                        mediaType: 'video'
                    };

                    Mntl.RTB.Plugins.prebid.setOutstreamRenderer(bid);

                    expect(bid).to.have.property('renderer');
                    expect(bid.renderer.url).to.equal('https://acdn.adnxs.com/video/outstream/ANOutstreamVideo.js');
                });
                it('should set renderer to null when bidder is not "grid" and mediaType is not "video"', () => {
                    const bid = {};

                    Mntl.RTB.Plugins.prebid.setOutstreamRenderer(bid);

                    expect(bid.renderer).to.equal(null);
                });
            });
            describe('getAdUnits', () => {
                it('should return all the ad slots when all slots have a config', () => {
                    const slots = [new Slot({
                        id: 'billboard',
                        sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                    }),
                    new Slot({
                        id: 'leaderboard',
                        sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                    })];
                    const adUnits = Mntl.RTB.Plugins.prebid.getAdUnits(slots);

                    expect(adUnits.length).to.equal(2);
                });
                it('should filter out ad slots that are not defined in the config', () => {
                    const slots = [new Slot({
                        id: 'billboard',
                        sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                    }),
                    new Slot({
                        id: 'foo',
                        sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                    })];
                    const adUnits = Mntl.RTB.Plugins.prebid.getAdUnits(slots);

                    expect(adUnits.length).to.equal(1);
                })
            });
            describe('getAdUnitForSlot', () => {
                it('should return ad unit when the slot is present in the config', () => {
                    const slot = getTestSlots()[0];

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[0]).to.deep.equal({
                        code: 'leaderboard',
                        ortb2Imp: {
                            ext: {
                                gpid: '/test/path/leaderboard',
                                data: { pbadslot: '/test/path/leaderboard' }
                            }
                        },
                        mediaTypes: { banner: { sizes: [[728, 90], [970, 250], [970, 90], [468, 60], [1, 1]] } },
                        bids: [{
                            bidder: 'foo',
                            params: {
                                param1: 'value1',
                                param2: 2
                            }
                        }, {
                            bidder: 'bar',
                            params: {
                                param1: 'value1',
                                param2: 3
                            }
                        }, {
                            bidder: 'ix',
                            params: {
                                param1: 'value3',
                                param2: 3
                            }
                        }],
                        floors: {
                            currency: 'USD',
                            schema: { fields: [ 'adUnitCode' ] },
                            values: { 'leaderboard': 0.5 }
                        }
                    });
                });

                it('should return split out display configs as expected on adunits that also have outstream configs', () => {
                    const slot = getTestSlots()[4];

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[0]).to.deep.equal({
                        code: 'leaderboardac',
                        ortb2Imp: {
                            ext: {
                                gpid: '/test/path/leaderboardac',
                                data: { pbadslot: '/test/path/leaderboardac' }
                            }
                        },
                        mediaTypes: { banner: { sizes: [[728, 90], [970, 250]] } },
                        bids: [{
                            bidder: 'foo',
                            params: {
                                param1: 'value1',
                                param2: 2
                            }
                        }],
                        floors: {
                            currency: 'USD',
                            schema: { fields: [ 'adUnitCode' ] },
                            values: { 'leaderboardac': 0.5 }
                        }
                    });
                });

                it('should return split out instream configs on adunits that have them', () => {
                    const slot = videoSlot;

                    slot.config = {
                        ...slot.config,
                        sizes: [[640, 360]]
                    };

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[1]).to.deep.equal(
                        {
                            code: "preroll",
                            mediaTypes: {
                                video: {
                                    api: [
                                        1,
                                        2
                                    ],
                                    context: "instream",
                                    linearity: 1,
                                    mimes: [
                                        "video/mp4",
                                        "video/webm",
                                        "application/javascript"
                                    ],
                                    minduration: 0,
                                    maxduration: 60,
                                    playerSize: [
                                        640,
                                        360
                                    ],
                                    protocols: [
                                        2,
                                        3,
                                        5,
                                        6
                                    ]
                                }
                            },
                            ortb2Imp: {
                                ext: {
                                    gpid: "3865/hlt.mdp.video/article/preroll",
                                    data: { pbadslot: "3865/hlt.mdp.video/article/preroll" }
                                }
                            },
                            bids: [
                                {
                                    bidder: "foo",
                                    params: {
                                        param1: "value1",
                                        param2: 2,
                                        size: [
                                            640,
                                            360
                                        ],
                                        video: {
                                            mimes: [
                                                "video/mp4",
                                                "video/webm",
                                                "application/javascript"
                                            ],
                                            minduration: 0,
                                            maxduration: 60,
                                            protocols: [
                                                2,
                                                3,
                                                5,
                                                6
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    );
                });

                it('should return split out outstream configs on adunits that have them', () => {
                    const slot = getTestSlots()[4];

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[2]).to.deep.equal({
                        code: 'leaderboardac',
                        mediaTypes: {
                            video: {
                                context: 'outstream',
                                playerSize: [
                                    [
                                        640,
                                        480
                                    ]
                                ]
                            }
                        },
                        bids: [{
                            bidder: 'foo',
                            params: {
                                param1: 'value1',
                                param2: 2
                            }
                        }],
                        floors: {
                            currency: 'USD',
                            schema: { fields: [ 'adUnitCode' ] },
                            values: { leaderboardac: 0.5 }
                        },
                        renderer: {}
                    });
                });

                it('should return false when the slot does not have outstream configs', () => {
                    const slot = getTestSlots()[0];

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[1]).to.equal(false);
                });

                it('should return null when the slot is not in the config', () => {
                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot({ config: { id: 'bin' } })).to.equal(null);
                });

                it('should filter out non-numeric sizes', () => {
                    const slot = getTestSlots()[4]; // get leaderboardac ad slot

                    expect(Mntl.RTB.Plugins.prebid.getAdUnitForSlot(slot)[0].mediaTypes.banner.sizes).to.deep.equal([[728, 90], [970, 250]]);
                });
            });
            describe('getConfigForSlot', () => {
                it('should return config when the slot is present in the config', () => {
                    const slot = new Slot({
                        id: 'billboard',
                        sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                    });

                    expect(Mntl.RTB.Plugins.prebid.getConfigForSlot(slot)).to.deep.equal([{
                        bidder: 'foo',
                        params: {
                            param1: 'value2',
                            param2: 2
                        }
                    }, {
                        bidder: 'bar',
                        params: {
                            param1: 'value2',
                            param2: 3
                        }
                    }, {
                        bidder: 'rubicon',
                        params: {
                            param1: 'value3',
                            param2: 3
                        }
                    }, {
                        bidder: 'pgRubicon',
                        params: {
                            param1: 'value4',
                            param2: 4
                        }
                    }, {
                        bidder: 'grid',
                        params: {
                            param1: 1,
                            keywords: {
                                tax0: 'val0',
                                tax1: 'val1',
                                tax2: 'val2',
                                tax3: 'val3'
                            }
                        }
                    }, {
                        bidder: "trustx",
                        params: {}
                    }, {
                        bidder: 'criteo',
                        params: { zoneId: '1591635' }
                    }, {
                        bidder: 'criteo',
                        params: { zoneId: '1591638' }
                    }, {
                        bidder: 'criteo',
                        params: { zoneId: '1591637' }
                    }]);
                });
                it('should return null when the slot is not in the config', () => {
                    expect(Mntl.RTB.Plugins.prebid.getConfigForSlot({ config: { id: 'bin' } })).to.equal(null);
                });
            });
            describe('hasBidder', () => {
                it('should return true when the bidder is present in the config', () => {
                    expect(Mntl.RTB.Plugins.prebid.hasBidder('foo')).to.equal(true);
                });
                it('should return false when the bidder is not in the config', () => {
                    expect(Mntl.RTB.Plugins.prebid.hasBidder('bin')).to.equal(false);
                });
            });
            describe('setPriceGranularity', () => {
                it('should set price granularity from rtb definition and convert them to numbers from strings', () => {
                    Mntl.RTB.Plugins.prebid.setPriceGranularity({
                        "buckets": [
                            {
                                max: "20",
                                increment: "0.05"
                            },
                            {
                                max: "100",
                                increment: "0.5"
                            }
                        ]
                    });
                    expect(Mntl.RTB.Plugins.prebid.priceGranularity).to.deep.equal({
                        "buckets": [
                            {
                                max: 20,
                                increment: 0.05
                            },
                            {
                                max: 100,
                                increment: 0.5
                            }
                        ]
                    });
                });
            });
            describe('getFirstPartyData', () => {
                it('should return the right tax values', () => {
                    expect(Mntl.RTB.Plugins.prebid.getFirstPartyData()).to.deep.equal({
                        tax0: 'val0',
                        tax1: 'val1',
                        tax2: 'val2',
                        tax3: 'val3'
                    });
                });
                it('should include the pc cookie when it exists', () => {
                    window.docCookies.getItem.withArgs('pc').returns(42);
                    expect(Mntl.RTB.Plugins.prebid.getFirstPartyData()).to.deep.equal({
                        tax0: 'val0',
                        tax1: 'val1',
                        tax2: 'val2',
                        tax3: 'val3',
                        pc: 42
                    });
                });
            });
            describe('getCriteoPGBidders', () => {
                it('should return the mapped criteo PG bidder values based on the slot\'s sizes', () => {
                    const slot = {
                        config: {
                            id: 'billboard',
                            sizes: [[300, 250], [300, 600], [160, 600], [300, 1050]]
                        }
                    };

                    expect(Mntl.RTB.Plugins.prebid.getCriteoPGBidders(slot)).to.deep.equal([{
                        bidder: 'criteo',
                        params: { zoneId: '1591635' }
                    }, {
                        bidder: 'criteo',
                        params: { zoneId: '1591638' }
                    }, {
                        bidder: 'criteo',
                        params: { zoneId: '1591637' }
                    }]);
                });

                it('should return an empty array if no zone ids match the current slot\'s sizes', () => {
                    const slot = {
                        config: {
                            id: 'billboard',
                            sizes: [[999, 250], [999, 600], [999, 1050]]
                        }
                    };

                    expect(Mntl.RTB.Plugins.prebid.getCriteoPGBidders(slot)).to.have.lengthOf(0);
                });
            });
            describe('setPrebidConfig', () => {
                const prebidMockConfig = {
                    data: {
                        a: 1,
                        b: 2
                    }
                }

                it('should create a mock config with only value a and b', () => {
                    Mntl.RTB.Plugins.prebid.setPrebidConfig(prebidMockConfig);
                    expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith(prebidMockConfig);
                })

                it('should merge the mock config with values a, b, c', () => {
                    prebidMockConfig.data.c = 3
                    
                    Mntl.RTB.Plugins.prebid.setPrebidConfig(prebidMockConfig);
                    expect(Mntl.RTB.Plugins.prebid.setPrebidConfig).to.have.been.calledWith(prebidMockConfig);
                })
            })
        });

        // bidz
        describe('bidz', () => {
            describe('setFloors', () => {
                it('should have bz and bzr targeting if slot.config.rtb is true', () => {
                    const slot = new Slot({rtb: true}, {});

                    Mntl.RTB.bidz.setFloors([slot], true);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '000',
                        bzr: '0'
                    });
                });
                it('should not have bz or bzr targeting if slot.config.rtb is false', () => {
                    const slot = new Slot({rtb: false}, {});

                    Mntl.RTB.bidz.setFloors([slot], true);
                    expect(slot.updateTargeting).to.not.have.been.called;
                });
            });
            describe('setAdXFloor', () => {
                it('should have a bzr value of 1 and a bz value of 15 if a9 returns a bid but prebid does not', () => {
                    const slot = new Slot({}, { amznbid: 'anything' });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '015',
                        bzr: '1'
                    });
                });
                it('should have a bzr value of 1 and a bz value of 15 if a9 returns a bid but prebid returns only a PMP (deal) bid', () => {
                    const slot = new Slot({}, {
                        amznbid: 'anything',
                        hb_pb: 0.6,
                        hb_pb_rubicon: 0.6,
                        hb_deal_rubicon: 'anything'
                    });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '015',
                        bzr: '1'
                    });
                });
                it('should have a bzr value of 0 and a bz value of 0 if a9 and prebid do not return bids', () => {
                    const slot = new Slot({}, { notABid: 'anything' });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '000',
                        bzr: '0'
                    });
                });
                it('should have a bzr value of 0 and a bz value of 0 if a9 does not return a bid and prebid only returns a PMP (deal) bid', () => {
                    const slot = new Slot({}, {
                        hb_pb: 0.6,
                        hb_pb_rubicon: 0.6,
                        hb_deal_rubicon: 'anything'
                    });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '000',
                        bzr: '0'
                    });
                });
                it('should have a bzr value of 1 and a bz value of 045 if a9 does not return a bid and the hightest non-deal bid is .40', () => {
                    const slot = new Slot({}, {
                        hb_deal_rubicon: 'anything',
                        hb_pb: 0.6,
                        hb_pb_ix: 0.25,
                        hb_pb_onemobile: 0.4,
                        hb_pb_rubicon: 0.6
                    });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '045',
                        bzr: '1'
                    });
                });

                it('should have a bzr value of 1 and a bz value of 315 if a9 returns a bid and the hightest non-deal bid is 2.85', () => {
                    const slot = new Slot({}, {
                        amznbid: 'anything',
                        hb_pb: 0.6,
                        hb_pb_ix: 0.25,
                        hb_pb_onemobile: 0.4,
                        hb_pb_rubicon: 2.85
                    });

                    Mntl.RTB.bidz.setAdXFloor(slot);
                    expect(slot.updateTargeting).to.have.been.calledWith({
                        bz: '315',
                        bzr: '1'
                    });
                });
            });

            describe('a9BidReturned', () => {
                it('should return 1 when a slot has a targeting value of "amznbid"', () => {
                    const slot = new Slot({}, { amznbid: 'testBid' });

                    expect(Mntl.RTB.bidz.a9BidReturned(slot)).to.equal(1);
                });

                it('should return 0 when a slot has an "amznbid" targeting value that is only one character', () => {
                    const slot = new Slot({}, { amznbid: '2' });

                    expect(Mntl.RTB.bidz.a9BidReturned(slot)).to.equal(0);
                });

                it('should return 0 when a slot does not have a targeting value of "amznbid"', () => {
                    const slot = new Slot({}, { notAmznbid: 'testBid' });

                    expect(Mntl.RTB.bidz.a9BidReturned(slot)).to.equal(0);
                });
            });
            describe('convertToThreeDigitString', () => ({}));
            it('should convert a number less than 10 to a 3 digit string with two leading zeros', () => {
                expect(Mntl.RTB.bidz.convertToThreeDigitString(7)).to.equal('007');
            });

            it('should convert a number less than 100 to a 3 digit string with one leading zero', () => {
                expect(Mntl.RTB.bidz.convertToThreeDigitString(77)).to.equal('077');
            });

            it('should convert a number less than 1000 to a 3 digit string', () => {
                expect(Mntl.RTB.bidz.convertToThreeDigitString(777)).to.equal('777');
            });
            describe('getHighestNonDealBid', () => {
                it('should return the highest prebid bid value that is not a deal bid', () => {
                    const slot = new Slot({}, {
                        hb_pb: 0.6,
                        hb_pb_ix: 0.25,
                        hb_pb_onemobile: 0.4,
                        hb_pb_rubicon: 0.6
                    });

                    expect(Mntl.RTB.bidz.getHighestNonDealBid(slot)).to.equal(0.6);
                });
                it('should return the the value that matches "pb_hb" if there is no deal targeting', () => {
                    const slot = new Slot({}, {
                        hb_deal_rubicon: 'anything',
                        hb_pb: 0.6,
                        hb_pb_ix: 0.25,
                        hb_pb_onemobile: 0.4,
                        hb_pb_rubicon: 0.6
                    });

                    expect(Mntl.RTB.bidz.getHighestNonDealBid(slot)).to.equal(0.4);
                });
            });
            describe('incrementizeBid', () => {
                const map = [
                    {
                        low: 5,
                        high: 999,
                        increment: 5
                    },
                    {
                        low: 1000,
                        high: 1999,
                        increment: 10
                    }
                ];

                it('should round values between 5 and 999 to the nearest 5 per the map', () => {
                    expect(Mntl.RTB.bidz.incrementizeBid(12, map, true)).to.equal(15);
                });
                it('should round values between 1000 and 1999 to the nearest 10 per the map', () => {
                    expect(Mntl.RTB.bidz.incrementizeBid(1112, map, true)).to.equal(1120);
                });
                it('should round up if the third arg is true', () => {
                    expect(Mntl.RTB.bidz.incrementizeBid(5, map, true)).to.equal(10);
                });
                it('should not round up if the third arg is false', () => {
                    expect(Mntl.RTB.bidz.incrementizeBid(5, map, false)).to.equal(5);
                });
            });
        });

        describe('generateGpid', () => {
            const slots = [
                {
                    config: { id: 'square-flex-1' },
                    gptSlot: { getAdUnitPath: () => '3865/hlt.mdp.com/tier1/none/Wellness' },
                    expectedLmdGpid: '/3865/hlt.mdp.com/tier1/na/div-gpt-square-flex-1'
                },
                {
                    config: { id: 'mob-square-flex-1' },
                    gptSlot: { getAdUnitPath: () => '/3865/hlt.mdp.mob/tier1/article' },
                    expectedLmdGpid: '/3865/hlt.mdp.mob/tier1/article/div-gpt-mob-square-flex-1'
                }
            ];

            it('should return the legacy Meredith gpid format if useLmdFormat is true', () => {
                for (const slot of slots) {
                    expect(Mntl.RTB.generateGpid(slot, true)).to.equal(slot.expectedLmdGpid);
                }
            });

            it('should return expected legacy Meredith gpid format for instream', () => {
                expect(Mntl.RTB.generateGpid({config: { id: 'preroll' }}, true)).to.equal('3865/hlt.mdp.video/article/preroll');
            });

            it('should return the slot ad unit if useLmdFormat is false', () => {
                for (const slot of slots) {
                    expect(Mntl.RTB.generateGpid(slot, false)).to.equal(slot.gptSlot.getAdUnitPath());
                }
            });
        });        
    });
});
