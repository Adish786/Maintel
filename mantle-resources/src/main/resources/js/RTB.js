window.Mntl = window.Mntl || {};

Mntl.RTB = (function(document, fnUtils, utils) {
    const performanceEvents = new Mntl.PubSub(['performanceTracking']);
    let _displayAndOutstreamBidMap = {}; // map of used plugins
    let _videoBidMap = {}; // map of used plugins for video
    // eslint-disable-next-line no-undef
    const _scriptStatus = new Map(); // {pluginName => loading or loaded}
    const _displayAndOutstreamTimeoutLength = [900, 500]; // [library load timeout, bidder timeout]
    const _videoBidTimeoutLength = 3000;
    let _scriptLoadTimedOut = false;
    let _bidderScriptsLoaded = false;
    let _waitForConsentResponseToLoadScripts = false;
    let _slotQueue;
    // These attributes could be moved into a single config object
    let _taxonomyStampValues = {};
    let latencyBuffer = 0;

    const bidderUrls = {
        amazon: '.amazon-adsystem.com/e/dtb/bid',
        appnexus: 'ib.adnxs.com/ut/v3/prebid',
        criteo: 'bidder.criteo.com/cdb',
        ias: 'pixel.adsafeprotected.com/services/pub',
        index: '.casalemedia.com/cygnus',
        onemobile: 'c2shb.ssp.yahoo.com/bidRequest',
        pubmatic: 'hbopenbid.pubmatic.com/translator',
        rubicon: '.rubiconproject.com/a/api/fastlane.json',
        ttd: 'direct.adsrvr.org/bid/bidder'
    };
    let initialHttpRequestsRecorded = false;

    let firstRecordedBidderSlots;

    /**
     * Checks whether consent is required with respect to a consent management platform (CMP).
     * @return {boolean}
     */
    function _isConsentRequired() {
        return Mntl.CMP && Mntl.CMP.isConsentRequired();
    }

    function getPrerollGPID() {
        const {
            getDfpId,
            getSitePath,
            getPageType
        } = Mntl.GPT;

        // if it's a video slot, alter the top-level ad unit to site.mdp.video (desktop) or site.mdp.video.mob (mobile)
        return `${getDfpId()}/${getSitePath()}/${getPageType()}/preroll`
            .replace('.mob', '.video.mob')
            .replace('.com', '.video');
    }

    /**
     * Returns a GPID for an ad slot.
     * @return {String}
     */
    function generateGpid(slot, useLmdFormat = Mntl.GPT.getUseLmdFormat(), useOxygen = Mntl.GPT.getUseOxygen()) {

        // generate and append slotId to the end of the ad unit
        let slotId = slot.config.id;

        const isPreroll = slot.config.id === 'preroll';

        let slotAdUnitPath = isPreroll ? getPrerollGPID() : slot.gptSlot.getAdUnitPath();

        if (!isPreroll) {
            // for legacy DD, just return the ad unit path for the slot
            if (!useLmdFormat && !useOxygen) {
                return slotAdUnitPath;
            }

            // replace pagetype 'none' or 'other' with 'na'
            const slotAdUnitBits = slotAdUnitPath.replace(/\/none|other\//, '/na/').split('/')
                .filter((bit) => bit);

            // remove the category from the end of the ad unit if it exists
            if (slotAdUnitBits.length > 4) {
                slotAdUnitBits.pop();
            }

            slotId = `div-gpt-${slotId}`;

            slotAdUnitBits.push(slotId);

            slotAdUnitPath = `/${slotAdUnitBits.join('/')}`;
        }

        return slotAdUnitPath; 
    }    
    
    // When plugins are run their type and id attributes have been merged with
    // the object and can therefore be access with the this keyword
    const Plugins = {
        noop: {
            preload() {
                return false;
            },
            src() {
                return false;
            },
            setup() { // slots, done
                return;
            }
        },
        /**
         * Index Exchange Identity Adapter
         * Intended to be used with Prebid.
         */
        ixid: {
            waitForConsentToLoad: true,
            scriptsLoaded: false,
            preload() {
                return false;
            },
            src() {
                if (Mntl.RTB.Plugins.prebid.hasBidder('ix')) {
                    // Note that there is race condition between loading this adapter and loading
                    // the prebid library. This is an accepted risk with the understanding that
                    // even though the adapter doesn't have to load _before_ prebid, once it does
                    // load it will automatically contribute user data to bid requests that follow.
                    debug.log('Mntl.RTB.ixid: loading index exchange identity adapter for prebid');

                    if (Mntl.GPT.getUseLmdFormat()) {
                        return '//js-sec.indexww.com/ht/p/184003-52190608802424.js';
                    }

                    return '//js-sec.indexww.com/ht/p/183710-16979354036509.js';
                }

                return false;
            },
            setup(slots, done) {
                // nothing to do here
                done();
            }
        },
        prebid: {
            waitForConsentToLoad: true,
            scriptsLoaded: false,
            config: {},
            priceGranularity: {},
            preload() {
                window.pbjs = window.pbjs || {};
                window.pbjs.que = window.pbjs.que || [];

                window.pbjs.que.push(() => {
                    window.pbjs.onEvent('bidResponse', this.setOutstreamRenderer);
                    window.pbjs.onEvent('bidWon', this.logPrebidWinner);
                    window.pbjs.aliasBidder('ix', 'roundel');

                    this.setCacheConfig();
                    this.setS2Sconfig();
                    this.setUserModuleConfig();
                    this.setPriceGranularityConfig();
                    this.setPageUrl();
                    this.setOrtb2FirstPartyData();
                    this.setOrtb2Device();
                    this.setIasRealTimeData();
                    this.setFloorConfig();
                    this.setCriteoSettings();

                    if (window.Mntl.GPT.getUseLmdFormat()) {
                        // Legacy MD only config
                        this.setBidCaching();
                    }
                    if (_isConsentRequired()) {
                        // no need to wait for consent to change if user is in an opt-out region
                        if (Mntl.CMP.isOptInConsent()) {
                            Mntl.CMP.onConsentChange(this.setPrebidDeviceAccess);
                        } else {
                            this.setPrebidDeviceAccess();
                        }
                    }
                    if (this.hasBidder('ix')) {
                        this.setIxFirstPartyData();
                    }

                    this.setBidderUserConfig();

                    if(window.Mntl.DEBUG) {
                        this.enableTestVideoBids();
                    }
                });
            },
            src() {
                if (!window.Mntl.GPT.getBundlePrebid()) {
                    let bundle = 'default';

                    if ((window.location.search || '').indexOf('globeResourceMinify=false') === -1) {
                        bundle += '/dist';
                    } else {
                        bundle += '/dev';
                    }
                    
                    return `${utils.getStaticPath()}/static/mantle/static/js/prebidjs/${bundle}/prebid.js`;
                }

                // Prebid is loaded in ads.xml
                return false;
            },
            setup(slots, done) {
                if (this.scriptsLoaded) {
                    const adUnits = this.getAdUnits(slots);
                    const isVideoSlot = (slots.length > 0 && slots[0].config.type === 'video');

                    if (adUnits.length) {
                        debug.log('Mntl.RTB.prebid: requesting bids for ad units:', adUnits);
                        window.pbjs.que.push(() => {
                            window.pbjs.requestBids({
                                timeout: isVideoSlot ? _videoBidTimeoutLength : _displayAndOutstreamTimeoutLength[1],
                                adUnits,
                                bidsBackHandler: () => {
                                    const targeting = this.getTargeting(isVideoSlot);

                                    if(!isVideoSlot) {
                                        this.trackPrebidMetrics(slots);
                                        this.addPrebidValuesToSlotTargeting(slots, targeting);
                                    }

                                    done({...isVideoSlot && targeting});
                                }
                            });
                        });
                    } else {
                        debug.log('Mntl.RTB.prebid: no ad units were mapped from slots:', slots);
                        done();
                    }
                } else {
                    done();
                }
            },
            getTargeting(isVideoSlot) {
                if(isVideoSlot) {
                    return window.pbjs.getAdserverTargetingForAdUnitCode('preroll');
                }

                return window.pbjs.getAdserverTargeting();
            },
            setOutstreamRenderer(bid) {
                // add renderer conditionally on bid
                if (bid && bid.bidder === 'grid' && bid.mediaType === 'video') {
                    bid.renderer = {
                        url: 'https://acdn.adnxs.com/video/outstream/ANOutstreamVideo.js',
                        // eslint-disable-next-line no-shadow
                        render: (bid) => {
                            const el = document.getElementById(bid.adUnitCode).querySelectorAll("div[id^='google_ads']");

                            // this hides google div container for outstream bids to remove unwanted space on page. Appnexus renderer creates a new iframe outside of google iframe to render the outstream creative.
                            if (el[0]) {
                                el[0].style.setProperty('display', 'none');
                            }

                            // deferring the video player js until we know we are serving an outstream ad.
                            Mntl.utilities.loadExternalJS({ src: bid.renderer.url }, function renderOutstreamVideo() {
                                // call renderer here
                                ANOutstreamVideo.renderAd({
                                    targetId: bid.adUnitCode,
                                    adResponse: bid.adResponse,
                                    rendererOptions: {
                                        // max width of 300 * (9/16) = 168.75 for mobile, max height of 250 * (16/9) = 443 for desktop
                                        width: window.innerWidth >= 443 ? 443 : 300,
                                        height: window.innerWidth >= 443 ? 250 : 168.75,
                                        // eslint-disable-next-line camelcase
                                        playback_method: 'auto_play_sound_off',
                                        playVideoVisibleThreshold: 20,
                                        nonViewableBehavior: 'pause',
                                        disableCollapse: true,
                                        allowFullscreen: false,
                                        disableTopBar: true,
                                        playerSkin: {
                                            controlBarColor: 'black',
                                            bigPlayButtonColor: 'white',
                                            controlBarControlsColor: 'white'
                                        }
                                    }
                                }, (adSlot, status) => {
                                    if (status === 'loaded') {
                                        const adSlotContainer = document.getElementById(adSlot);
                                        const videoPlaceholder = document.createElement('div');

                                        if (adSlotContainer) {
                                            adSlotContainer.insertBefore(videoPlaceholder, adSlotContainer.childNodes[0]);
                                            adSlotContainer.classList.add('video-ad');
                                            videoPlaceholder.classList.add('video-placeholder', `video-placeholder--${adSlot}`);
                                        }
                                    }
                                });
                            });
                        }
                    };
                } else {
                    bid.renderer = null;
                }
            },
            logPrebidWinner(bid) {
                debug.log(`Prebid winner: ${bid.bidder} on slot: ${bid.adUnitCode}`);
            },
            setCacheConfig() {
                this.setPrebidConfig({ cache: { url: 'https://prebid.adnxs.com/pbc/v1/cache' } });
            },
            setS2Sconfig() {
                // Override parameter is true
                this.setPrebidConfig({
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
                }, true);
            },
            setUserModuleConfig() {
                if (window.Mntl.GPT.getUseLmdFormat()) {
                    this.setPrebidConfig({
                        userSync: {
                            userIds: [
                                {
                                    name: 'identityLink',
                                    bidders: [ 'roundel' ],
                                    params: { pid: '13435' },
                                    storage: {
                                        type: 'html5',
                                        name: 'idl_env',
                                        expires: 30
                                    }
                                }, 
                                {
                                    name: 'unifiedId',
                                    params: { partner: 'uyuqun9' }
                                },
                                { name: 'criteo' }
                            ]
                        }
                    });
                }
            },
            setPriceGranularityConfig() {
                if (Object.keys(this.priceGranularity).length) {
                    debug.log('Mntl.RTB.prebid: setting custom price granularity', this.priceGranularity);
                    this.setPrebidConfig({ priceGranularity: this.priceGranularity });
                }
            },
            setPageUrl() {
                this.setPrebidConfig({ pageUrl: document.location.href });
            },
            setOrtb2FirstPartyData() {
                debug.log('Mntl.RTB.prebid: setting ortb2 First Party Data', this.getFirstPartyData());
                this.setPrebidConfig({ortb2: {site: {ext: {data: this.getFirstPartyData()}}}});
            },
            setOrtb2Device() {
                debug.log('Mntl.RTB.prebid: setting ortb2 isMobile', Mntl.GPT.isMobile() ? 1 : 0);
                this.setPrebidConfig({ortb2: {site: {mobile: Mntl.GPT.isMobile() ? 1 : 0}}});
            },
            setIasRealTimeData() {
                this.setPrebidConfig(
                    {
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
                    }
                )
            },
            setCriteoSettings() {
                window.pbjs.bidderSettings = window.pbjs.bidderSettings || {};
                window.pbjs.bidderSettings.criteo = { storageAllowed: true };
                this.setPrebidConfig({ 'criteo': { fastBidVersion: 'none' } });
            },
            setPrebidDeviceAccess() {
                if (!Mntl.CMP.hasTargetingConsent()) {
                    debug.log('Mntl.RTB.prebid: disabling device access due to lack of consent');
                    window.pbjs.que.push(() => {
                        Mntl.RTB.Plugins.prebid.setPrebidConfig({ deviceAccess: false });
                    });
                }
            },
            setBidCaching() {
                this.setPrebidConfig({
                    useBidCache: true,
                    bidCacheFilterFunction: (bid) => bid.mediaType !== 'video'
                });
            },
            setIxFirstPartyData() {
                const firstPartyData = this.getFirstPartyData() || {};

                if (Mntl.GPT.getUseLmdFormat()) {
                    // limit mtax to 600 terms and separate with dashes
                    const { mtax } = Mntl.GPT.getPageTargeting();
                    const msg = this.formatMsgForIx();

                    if (mtax) {
                        firstPartyData.mtax = mtax.slice(0, 599).join('-');
                    }

                    if (msg) {
                        firstPartyData.audSegs = msg;
                    }
                }

                debug.log('Mntl.RTB.prebid: setting index first party data', firstPartyData);
                this.setPrebidConfig({ ix: { firstPartyData } });
            },
            formatMsgForIx() {
                if (!localStorage.msg) {
                    return false;
                }

                let msg = JSON.parse(localStorage.msg);

                msg = msg.map((seg) => `(${seg})`);
                msg = msg.slice(0, 599).join(''); // Limit to 600 segments per legacy MD requirement

                return msg;
            },
            setBidderUserConfig() {
                try {
                    if (!localStorage.msg2) {
                        return false;
                    }
    
                    const bidders = [];
    
                    if (this.hasBidder('trustx')) {
                        bidders.push('trustx');
                    }
    
                    if (this.hasBidder('rubicon')) {
                        bidders.push('rubicon');
                    }
    
                    if (bidders.length > 0) {
                        const msg = JSON.parse(localStorage.msg2);
                        const config = {
                            bidders,
                            config: {
                                ortb2: {
                                    user: {
                                        data: [{
                                            name: 'dotdashmeredith.com',
                                            ext: { segtax: 4 },
                                            segment: msg.map((seg) => ({ id: seg }))
                                        }]
                                    }
                                }
                            }
                        };
        
                        debug.log('Mntl.RTB.prebid: setting bidder user data config with msg2 values', config);
        
                        window.pbjs.setBidderConfig(config);
                    }
                } catch(e) {
                    debug.log('Mntl.RTB: error loading msg2', e);

                    return false;
                }
            },
            getAdUnits(slots) {
                return slots.map(this.getAdUnitForSlot.bind(this))
                    .flat()
                    .filter((el) => el);
            },
            getAdUnitForSlot(slot) {
                const config = this.getConfigForSlot(slot);

                if (!config) {
                    return null;
                }

                const {
                    displayConfig,
                    instreamConfig,
                    outstreamConfig
                } = this.splitDisplayAndVideoBids(config);

                const gpidForSlot = generateGpid(slot);
                const displayAdUnit = (displayConfig.length > 0) && {
                    code: slot.config.id,
                    ortb2Imp: {
                        ext: {
                            gpid: gpidForSlot,
                            data: { pbadslot: gpidForSlot }
                        }
                    },
                    mediaTypes: {
                        banner: {
                            // sizes is specified by Prebid to only contain numbers so we are filtering out sizes like "fluid" here
                            sizes: slot.config.sizes.filter((size) => Array.isArray(size) && size.every((elem) => typeof elem === 'number'))
                        }
                    },
                    bids: displayConfig
                };

                const instreamAdUnit = (instreamConfig.length > 0) && {
                    code: slot.config.id,
                    mediaTypes: {
                        video: {
                            api: [1, 2],
                            context: 'instream',
                            linearity: 1,
                            mimes: [
                                'video/mp4',
                                'video/webm',
                                'application/javascript'
                            ],
                            minduration: 0,
                            maxduration: 60,
                            playerSize: [640, 360],
                            protocols: [2, 3, 5, 6]
                        }
                    },
                    ortb2Imp: {
                        ext: {
                            gpid: gpidForSlot,
                            data: { pbadslot: gpidForSlot }
                        }
                    },
                    bids: instreamConfig.map((config) => {
                        config.params = {
                            ...config.params,
                            size: [640, 360],
                            video: {
                                mimes: [
                                    'video/mp4',
                                    'video/webm',
                                    'application/javascript'
                                ],
                                minduration: 0,
                                maxduration: 60,
                                protocols: [2, 3, 5, 6]
                            }
                        }

                        return config;
                    })
                };

                const outstreamAdUnit = (outstreamConfig.length > 0) && {
                    code: slot.config.id,
                    mediaTypes: {
                        video: {
                            context: 'outstream',
                            playerSize: [[640, 480]]
                        }
                    },
                    bids: outstreamConfig,
                    // blank object needs to exist to prevent fallback to default renderer of bidder
                    renderer: {}
                };

                const floorPriceCents = slot.getFloorValueCents();

                if (floorPriceCents !== false) {
                    [ displayAdUnit, instreamAdUnit, outstreamAdUnit ]
                        .filter((unit) => unit)
                        .forEach((unit) => {
                            unit.floors = {
                                currency: 'USD',
                                schema: { fields: [ 'adUnitCode' ] },
                                values: { [unit.code]: floorPriceCents / 100 }
                            };
                        });
                }

                return [displayAdUnit, instreamAdUnit, outstreamAdUnit];
            },
            splitDisplayAndVideoBids(config) {

                const configMap = {
                    display: [],
                    instream: [],
                    outstream: []
                }

                if (config) {
                    for (const configItem of config) {
                        const type = configItem.params.type || 'display';
                        const configItemCopy = fnUtils.deepExtend({}, configItem);

                        delete configItemCopy.params.type; // prevent type value from being passed into pbjs.requestBids

                        configMap[type].push(configItemCopy);
                    }
                }

                return {
                    displayConfig: configMap.display,
                    instreamConfig: configMap.instream,
                    outstreamConfig: configMap.outstream
                };
            },
            getConfigForSlot(slot) {
                let config = null;

                if (this.config) {
                    let selectionId = slot.config.id;

                    if (window.Mntl.GPT.getUseLmdFormat()) {
                        selectionId = `${selectionId.replace(/-\d+$/, '')}/tier${slot.config.tier}`;

                        if(slot.config.type === 'video') {
                            slot.config.id = selectionId = 'preroll';
                        }
                    }
                    config = this.config[selectionId] || null;
                }

                if (config) {
                    const firstPartyData = this.getFirstPartyData();

                    config = config.map((bidderConfig) => {
                        if (bidderConfig.bidder === 'grid') {
                            bidderConfig.params.keywords = firstPartyData;
                        }

                        return bidderConfig;
                    })

                    const hasCriteoPgBidder = config.filter((bidder) => bidder.bidder === 'criteopg').length > 0;

                    // criteopg bids need to be sent as bidder: criteo, but with a different params: include a zoneId instead of networkId
                    if (hasCriteoPgBidder) {
                        config = config.concat(this.getCriteoPGBidders(slot));
                        config = config.filter((bidder) => bidder.bidder !== 'criteopg');
                    }
                } else {
                    debug.log('Mntl.RTB.prebid: no config for', slot.config.id, 'slot:', slot);
                }

                return config;
            },
            setFloorConfig() {
                this.setPrebidConfig({ floors: {} });
            },
            enableTestVideoBids () {
                this.setPrebidConfig({
                    debugging: {
                        enabled: true,
                        intercept: [
                            { when: { adUnitCode: 'preroll' } }
                        ]
                    }
                })
            },
            getFirstPartyData() {
                const firstPartyData = fnUtils.pickObject(_taxonomyStampValues, ['tax0', 'tax1', 'tax2', 'tax3']);
                const pcCookie = window.docCookies.getItem('pc');

                if (pcCookie) {
                    firstPartyData.pc = pcCookie;
                }

                return firstPartyData;
            },
            getCriteoPGBidders(slot) {
                const criteoPGsizeZone = {
                    '160x600': '1591637',
                    '300x250': '1591635',
                    '300x600': '1591638',
                    '320x50': '1591639',
                    '728x90': '1591636',
                    '970x250': '1591640'
                };

                const criteoPGbidders = slot.config.sizes.reduce((bidders, dimensions) => {
                    const size = `${dimensions[0]}x${dimensions[1]}`;
                    const zoneId = criteoPGsizeZone[size];

                    if (typeof zoneId !== 'undefined') {
                        bidders.push({
                            bidder: 'criteo',
                            params: { zoneId }
                        });
                    }

                    return bidders;
                }, []);

                return criteoPGbidders;
            },
            setConfig(config) {
                this.config = Object.assign(this.config, config);
            },
            setPrebidConfig(config, override=null) {
                // Optional override parameter to force setConfig
                Object.entries(config).forEach(([key, value]) => {
                    if (window.pbjs.getConfig(key) && !override) {
                        return window.pbjs.mergeConfig({[key]: value});
                    }

                    return window.pbjs.setConfig({[key]: value});
                });
            },
            setPriceGranularity(priceGranularity) {
                if (priceGranularity && priceGranularity.buckets) {
                    // string values need to be converted to numbers
                    this.priceGranularity.buckets = priceGranularity.buckets.map((bucket) => fnUtils.mapObject(bucket, null, (str) => Number(str)));
                }
            },
            hasBidder(bidderCode) {
                const config = this.config || {};

                return Object.keys(config).some((key) => config[key].some(({bidder}) => bidder === bidderCode));
            },
            addPrebidValuesToSlotTargeting(slots, prebidTargeting) {
                // NOTE that we could set the targeting for GPT automatically by calling the following function:
                //      pbjs.setTargetingForGPTAsync([codeArr])
                // We intentionally set the targeting manually for each slot below to remain consistent with other RTB plugins.
                slots.forEach((slot) => {
                    const slotHasTargeting = prebidTargeting && prebidTargeting[slot.config.id] && Object.keys(prebidTargeting[slot.config.id]).length;

                    if (slotHasTargeting) {
                        debug.log('Mntl.RTB: updating', slot.config.id, 'slot', slot, 'with prebid targeting', prebidTargeting);
                        slot.updateTargeting(prebidTargeting[slot.config.id], false);
                    } else {
                        debug.log('Mntl.RTB: no prebid targeting for', slot.config.id, 'slot', slot);
                    }
                })
            },
            trackPrebidMetrics(slots) {

                slots.forEach((slot) => {
                    const slotId = slot.config.id;
                    const { bids } = window.pbjs.getBidResponsesForAdUnitCode(slotId);
                    const nobids = window.pbjs.getNoBidsForAdUnitCode ? window.pbjs.getNoBidsForAdUnitCode(slotId).bids : [];


                    const partnerBids = bids.map((partnerData) => {
                        const data = {
                            name: partnerData.bidder,
                            bidSize: partnerData.adserverTargeting.hb_size,
                            bidAmount: partnerData.adserverTargeting.hb_pb,
                            timedMetric: true,
                            start: partnerData.requestTimestamp,
                            finish: partnerData.responseTimestamp
                        };

                        return data;
                    });

                    const partnerNoBids = nobids.map((partnerData) => {
                        const data = {
                            name: partnerData.bidder,
                            bidSize: null,
                            bidAmount: null,
                            timedMetric: true,
                            start: null
                        };

                        return data;
                    });

                    const metricData = {
                        instigator: slotId,
                        metrics: {partnerBids: [...partnerBids, ...partnerNoBids]}
                    };
                    
                    Mntl.AdMetrics.pushMetrics('slotMetric', metricData);
                });
            }
        },
        amazon: {
            waitForConsentToLoad: true,
            scriptsLoaded: false,
            preload() {
                return false;
            },
            src() {
                return '//c.amazon-adsystem.com/aax2/apstag.js';
            },
            getAmazonBids(apstagSlots, isVideoSlot) {
                return new Promise((resolve) => {
                    window.apstag.fetchBids({
                        slots: apstagSlots,
                        timeout: isVideoSlot ? _videoBidTimeoutLength : _displayAndOutstreamTimeoutLength[1]
                    }, (bids) => {
                        resolve(bids);
                    });
                }).catch((err) => {
                    console.log(err)
                })
            },
            setup(slots, done) {
                let apstagSlots = [];
                const isVideoSlot = (slots.length > 0 && slots[0].config.type === 'video');

                if (!this.scriptsLoaded || !window.apstag) {
                    done();

                    return;
                }

                // Initialize amazon api
                this.initialize();

                if (isVideoSlot) {
                    apstagSlots = [this.getVideoApstagSlot(slots[0])];
                } else {
                    // Clear any previous amazon targeting in case of refresh
                    this.clearTargeting(slots);
                    apstagSlots = this.getDisplayApstagSlots(slots);

                    // Determine which slots to bid on

                    // Return early if no slots to bid on
                    if (apstagSlots.length === 0) {
                        done();

                        return;
                    }
                }

                window.debug.log('Mntl.RTB.amazon: setup: fetching apstag bids', apstagSlots);

                this.getAmazonBids(apstagSlots, isVideoSlot).then((bids) => {
                    window.debug.log('Mntl.RTB.amazon: setup: apstag bid response received', bids);

                    if (!isVideoSlot) {
                        this.addApstagValuesToSlotTargeting(slots, bids);
                    }

                    done({...this.getVideoTargeting(bids, isVideoSlot)});
                });
            },
            getVideoTargeting(bids, isVideoSlot) {
                return isVideoSlot && bids[0] && bids[0].targeting;
            },
            addApstagValuesToSlotTargeting(slots, bids) {
                // Add bid targeting values to slots
                slots.forEach(function(slot) {
                    bids.forEach(function(bid) {

                        let targeting;

                        if (bid.slotID === slot.config.id) {
                            targeting = bid.targeting || {
                                amznbid: bid.amznbid,
                                amzniid: bid.amzniid,
                                amznsz: bid.amznsz,
                                amznp: bid.amznp
                            };
                            window.debug.log(`Mntl.RTB.amazon: setup: setting targeting on slot ${slot.config.id}`, targeting);

                            slot.updateTargeting(targeting, false);
                        }
                    });
                });
            },
            initialize: fnUtils.once(function() {
                const initConfig = {
                    deals: true,
                    pubID: Mntl.GPT.getUseOxygen() ? '3446' : this.id, // For the useOxygen bucket, always use the LMD TAM id
                    adServer: 'googletag',
                    videoAdServer: 'DFP',
                    section: this.amazonConfigs.amazonSection || '',
                    params: {
                        ...this.amazonConfigs.mapTaxValues,
                        si_pagegroup: Mntl.GPT.getPageTargeting().category // eslint-disable-line camelcase
                    },
                    blockedBidders: this.amazonConfigs.blockedBidders || [],
                    sis_sitesection: this.amazonConfigs.amazonSection ? `${utils.getDomain()}:${this.amazonConfigs.amazonSection}` : '', // eslint-disable-line camelcase
                    schain: {
                        complete: 1,
                        ver: '1.0',
                        nodes: []
                    }
                };

                window.apstag.init(initConfig);
                if (Mntl.DEBUG) {
                    window.apstag.debug('enable');
                }
                window.debug.log('Mntl.RTB.amazon: setup: apstag initialized');
            }),
            clearTargeting(slots) {
                slots.forEach((slot) => {
                    const targetingKeys = window.apstag.targetingKeys(slot.config.id);

                    if (Array.isArray(targetingKeys)) {
                        slot.updateTargeting(
                            targetingKeys.reduce((acc, key) => {
                                acc[key] = '';

                                return acc;
                            }, {}), false
                        );
                    }
                });
            },

            getVideoApstagSlot(slot) {
                return {
                    mediaType: 'video',
                    slotID: slot.config.id.includes('mntl-rail-jwplayer') ? 'video-rail' : 'video',
                    sizes: slot.config.sizes,
                    slotName: getPrerollGPID(),
                    slotParams: {
                        // Ignored because request object must use an _
                        // eslint-disable-next-line camelcase
                        fb_pid: this.amazonConfigs.mapFBValues[slot.config.id] || this.amazonConfigs.mapFBValues.default || ''
                    }
                };
            },

            getDisplayApstagSlots(slots) {
                return slots.map(function(slot) {
                    const obj = {
                        slotID: slot.config.id,
                        // Exclude non-array sizes and sizes with 1px width or height
                        sizes: slot.config.sizes.filter((size) => size.join && size.indexOf(1) === -1)
                    };

                    if (Mntl.GPT.getUseLmdFormat()) {
                        obj.slotName = generateGpid(slot);
                    }

                    obj.slotParams = {
                        // Ignored because request object must use an _
                        // eslint-disable-next-line camelcase
                        fb_pid: this.amazonConfigs.mapFBValues[slot.config.id] || this.amazonConfigs.mapFBValues.default || ''
                    };

                    const floorValueCents = slot.getFloorValueCents();

                    if (floorValueCents !== false) {
                        obj.floor = {
                            value: floorValueCents,
                            currency: 'USD'
                        };
                    }

                    return obj;
                }, this)
                    // Exclude slots with all sizes filtered out
                    .filter((slot) => slot.sizes.length > 0);
            },
            amazonConfigs: {}
        },
        msg: {
            waitForConsentToLoad: false,
            scriptsLoaded: false,
            localStorageEmpty: true,
            preload() {
                try {
                    const msgDate = window.localStorage.msg_date;

                    // ttl: 3 days
                    if (!msgDate || new Date().getTime() - msgDate > 3 * 24 * 60 * 60 * 1000) {
                        delete window.localStorage.msg;
                        delete window.localStorage.msg2;
                        delete window.localStorage.msg_date;
                    } else {
                        this.localStorageEmpty = false;
                    }
                } catch(e) {
                    debug.log('Mntl.RTB: error loading msg', e);
                }

                return false;
            },
            src() {
                const muid = Mntl.GPT.getPublisherProvidedId();
                const path = `https://d30qdagvt44524.cloudfront.net/production/segments?muid=${muid}`;

                // If there is no muid or localStorage is not empty, return false which bypasses loading the resource
                return Boolean(muid) && this.localStorageEmpty && path;
            },
            setup(slots, done) {
                // nothing to do here
                done();
            }
        }
    };

    // Private Methods
    /**
    *  bidz - Imported from legacy Meredith KARMA ad library. Takes highest non-deal bid and
    *  rounds up to the nearest increment per the ganularityMap. This value is applied
    *  as an AdX floor.
    **/
    const bidz = {
        granularityMap: [
            {
                low: 5,
                high: 999,
                increment: 5
            },
            {
                low: 1000,
                high: 1999,
                increment: 10
            },
            {
                low: 2000,
                high: 999999,
                increment: 25
            }
        ],
        floorInflatePercentage: 10,
        incrementizeBid(val, map, alwaysRoundUp) {
            function applyIncrement(val, increment, alwaysRoundUp) {
                let multiplier;

                if (alwaysRoundUp) {
                    multiplier = Math.floor(val / increment) + 1;
                } else {
                    multiplier = Math.round(val / increment);
                }

                return multiplier * increment;
            };

            let modifiedVal;

            const floor = map[0].low,
                ceiling = map[map.length - 1].high;

            map.forEach((bucket) => {
                if (val >= bucket.low && val <= bucket.high) {
                    modifiedVal = applyIncrement(val, bucket.increment, alwaysRoundUp);
                }
            });

            if (!modifiedVal) {
                // TWhen passed through incrementizeBid, bids of 0 stay at 0
                if (val === 0) {
                    modifiedVal = 0;
                // When passed through incrementizeBid, anything above 0 but below the floor should be set to the floor value
                } else if (val < floor) {
                    modifiedVal = floor;
                // When passed through incrementizeBid, anything above the ceiling should be set to the ceiling value
                } else if (val > ceiling) {
                    modifiedVal = ceiling;
                }
            }

            return Math.floor(modifiedVal);
        },
        getHighestNonDealBid(slot) {
            const {targeting} = slot;
            const bids = [];

            Object.keys(targeting).forEach((key) => {
                if (targeting.hasOwnProperty(key) && key.indexOf('hb_pb_') > -1) {
                    const bidder = key.substring(6);
                    const isPMP = targeting.hasOwnProperty(`hb_deal_${bidder}`);

                    if (!isPMP) {
                        bids.push(parseFloat(targeting[key]));
                    }
                }
            });

            return bids.sort().reverse()[0];
        },
        determineAdXFloor(slot) {
            let floor = (this.getHighestNonDealBid(slot) * 100) || 0;

            if (floor > 0) {
                floor = floor * ((this.floorInflatePercentage / 100) + 1);
            }
            // If an a9 bid is returned and no prebid bids are returned, the floor should be set at 015
            if (this.a9BidReturned(slot) && floor < 15) {
                floor = 14;
            }

            floor = Math.floor(floor * 100) / 100;

            return this.incrementizeBid(floor, this.granularityMap, true);
        },
        convertToThreeDigitString(number) {
            // zero pads a number to three digits, e.g. 1 -> 001, 10 -> 010, 250 -> 250
            const string = String(number);

            if (string.length === 1) {
                return `00${string}`;
            } else if (string.length === 2) {
                return `0${string}`;
            }

            return string;
        },
        a9BidReturned(slot) {
            return (slot.targeting.amznbid && slot.targeting.amznbid.length > 1) ? 1 : 0;
        },
        setAdXFloor(slot) {
            // sets bidz-related targeting values: bz for the AdX fooor, bzr for a boolean
            const adXFloor = this.determineAdXFloor(slot);
            // if only a9 is returned, bzr=1 and bz=000
            // if no bids are returned and a9 returns a "no bid" value of 0, 1 or 2, then bzr=0 and bz=000
            const bidzReturned = adXFloor === 0 ? 0 : 1;

            slot.updateTargeting({
                'bz': this.convertToThreeDigitString(adXFloor),
                'bzr': bidzReturned.toString()
            }, false);
        },
        setFloors (slots, isEnabled) {
            // for each slot that has header bidding enabled, a targeting parameter exists called 'bz' with a numeric value
            // bz values are not set if karmaConfig.hb.bidz.percentage === 0
            // if bidz is not enabled (karmaConfig.hb.bidz.enabled) bz and bzr are not set on any slots
            // if no bids are returned, bz=000 is passed on a slot
            // if no bids are returned, bzr=0 is passed on a slot
            // if bids are returned, bzr=1 is passed on a slot
            if (!slots.length || !isEnabled) {

                return;
            }
            slots.forEach((slot) => {
                if (slot.config.rtb) {
                    this.setAdXFloor(slot);
                }
            });
        }
    }

    function _recordTimeOnWireMetrics() {
        if (!initialHttpRequestsRecorded) {
            try {
                initialHttpRequestsRecorded = true;
                debug.log('Mntl.RTB: recording HTTP request times', Date.now());
                const performanceEntries = performance.getEntriesByType('resource');

                for (const name in bidderUrls) {
                    const resourceTiming = performanceEntries
                        .filter((r) => r.name.indexOf(bidderUrls[name]) !== -1)[0];

                    if (typeof resourceTiming !== 'undefined') {
                        performanceEvents.publish('performanceTracking', {
                            instigator: 'RTB',
                            event: 'tw',
                            name,
                            status: 'start',
                            timestamp: Math.round(resourceTiming.startTime)
                        });
                        performanceEvents.publish('performanceTracking', {
                            instigator: 'RTB',
                            event: 'tw',
                            name,
                            status: 'finish',
                            timestamp: Math.round(resourceTiming.responseEnd)
                        });
                    }
                }
            } catch (e) {
                debug.log('Mntl.RTB: error accessing http time on wire', e);
            }
        }
    }

    /**
     * This provides a done function to each bidder Plugins[bidder].setup(...)
     * Ultimately calls GPT _displaySlots function (passed in init) when all libs
     * have loaded or the timeout has elapsed.
     * Wrapper function for final callback, executed within each bidder script
     * Run the callback if either a) all scripts have returned or b) the RTB
     * timeout has elapsed
     * @param       {Object}   rtbAction @see RtbAction
     * @param       {Boolean}  force
     * @return      {function}
     */
    function _getGPTCallback(rtbAction, force) {
        return function() {
            rtbAction.decrementCount();

            debug.log('Mntl.RTB: gpt rtb callback', _displayAndOutstreamBidMap.size - rtbAction.count, _displayAndOutstreamBidMap.size, Date.now());

            if ((force && rtbAction.count > 0) || rtbAction.count === 0) {
                const slots = rtbAction.gptSlots.map((slot) => slot.config.id);

                _recordTimeOnWireMetrics();

                debug.log('Mntl.RTB: executing ad call, forced: ', force, Date.now());
                debug.log('Mntl.RTB: triggering RTBSlotsBidded event for', rtbAction.gptSlots);
                document.dispatchEvent(new CustomEvent('RTBSlotsBidded', {
                    bubbles: true,
                    detail: {
                        force,
                        slots
                    }
                }));
                rtbAction.callback();
                rtbAction.reset();
                _slotQueue.go();
            }
        };
    }

    /**
     * Return a new object containing the Plugins that match the setup information
     * passed from the server (rtbConfig) merged with the plugins stored in the
     * Plugins object
     * ex. extend({}, Plugins.amazon, config);
     * Where Plugins.amazon contains { setup(), src(), preload() } and config
     * contains { type: amazone, id: 1839849 }
     * @param       {[Object]} rtbConfig @see initBidders
     * @return      {Object}
     */
    function _extendPlugins(rtbConfig) {
        return rtbConfig.reduce(function extendPluginConfig(acc, config) {
            if (Plugins[config.type] && config.id !== null) {
                acc[config.type] = Object.assign({}, config, Plugins[config.type]);
            }

            return acc;
        }, {});
    }

    /**
     * load all the third party scripts and set a timeout timer to stop loading if time exceeds
     * specified time allotment
     * @param       {Object} bidMap config indicating which scripts we want to load
     */
    function _loadScripts(bidMap) {
        return new Promise((resolve) => {
            let count = bidMap.size;
            let timer;

            /**
             * Clear out the timeout timer for loading scripts and run go() on the _slotQueue
             */
            function proceedWithSlotQueue() {
                clearTimeout(timer);
                resolve();
            }

            /**
             * Keep track of the bidder of the loaded script, and proceed with running. When the last
             * bidder's scripts have been loaded, the toggle to track scripts loaded is set to true and
             * proceeds with running the slot queue function @see proceedWithSlotQueue
             * @param {string} bidder
             */
            function countDown(bidder) {
                _scriptStatus.set(bidder, 'loaded');

                if (--count === 0) {
                    _bidderScriptsLoaded = true;
                    proceedWithSlotQueue();
                    debug.log('Mntl.RTB: triggering RTBPluginsLoaded event');
                    document.dispatchEvent(new CustomEvent('RTBPluginsLoaded', { bubbles: true }));
                }
            }

            /**
             * Runs the preload script and loads the script returned from the bidder src() for the specified bidMap key
             * When script is loading or has been loaded, update the _scriptStatus @see countDown
             * @param {string} key
             */
            function loadScript(key) {
                if (_scriptStatus.has(key)) {
                    bidMap[key].scriptsLoaded = true;

                    return;
                }

                _scriptStatus.set(key, 'loading');

                bidMap[key].preload();

                const src = bidMap[key].src();
                let jsToLoad = {};

                if (src) {
                    performanceEvents.publish('performanceTracking', {
                        instigator: 'RTB',
                        event: 'll',
                        name: bidMap[key].type,
                        status: 'start'
                    });

                    Mntl.AdMetrics.pushMetrics('pageMetric', {
                        instigator: 'RTB',
                        metrics: {
                            libraryLoad: [
                                {
                                    name: bidMap[key].type,
                                    timedMetric: true,
                                    start: Date.now()
                                }
                            ]
                        }
                    });

                    debug.log('Mntl.RTB.Plugins.', bidMap[key].type, ' load start', Date.now());

                    jsToLoad = {
                        async: true,
                        src
                    };

                    /* Script should not be assigned null id */
                    if (bidMap[key].scriptId) {
                        jsToLoad.id = bidMap[key].scriptId;
                    }

                    utils.loadExternalJS(jsToLoad, () => {
                        performanceEvents.publish('performanceTracking', {
                            instigator: 'RTB',
                            event: 'll',
                            name: bidMap[key].type,
                            status: 'finish'
                        });

                        Mntl.AdMetrics.pushMetrics('pageMetric', {
                            instigator: 'RTB',
                            metrics: {
                                libraryLoad: [
                                    {
                                        name: bidMap[key].type,
                                        timedMetric: true,
                                        finish: Date.now()
                                    }
                                ]
                            }
                        });

                        debug.log('Mntl.RTB.Plugins.', bidMap[key].type, ' loaded', Date.now());
                        document.dispatchEvent(new CustomEvent(`RTBPluginLoad:${bidMap[key].type}`, { bubbles: true }));
                        countDown(bidMap[key].type);
                    });
                } else {
                    debug.log('Mntl.RTB.Plugins.', bidMap[key].type, ' loaded(no src)', Date.now());
                    countDown(bidMap[key].type);
                }
                bidMap[key].scriptsLoaded = true;
            }

            /**
             * Sets a timeout for loading the scripts so bids can start being made for ad slots.
             * The timeout gets set immediately, unless it is necessary to wait for consent to load scripts.
             * The timeout function will set the _scriptLoadTimedOut to true and will proceed with
             * running the slot queue function  @see proceedWithSlotQueue
             */
            function _setBidderScriptsTimeout() {
                function clearTimeoutAndProceed() {
                    _scriptLoadTimedOut = true;
                    proceedWithSlotQueue();
                }

                if (_waitForConsentResponseToLoadScripts) {
                    Mntl.CMP.onConsentChange(() => {
                        debug.log('Mntl.RTB.Plugins: library load timeout', _displayAndOutstreamTimeoutLength[0], Date.now());
                        timer = setTimeout(clearTimeoutAndProceed, _displayAndOutstreamTimeoutLength[0]);
                    });
                } else {
                    debug.log('Mntl.RTB.Plugins: library load timeout', _displayAndOutstreamTimeoutLength[0], Date.now());
                    timer = setTimeout(clearTimeoutAndProceed, _displayAndOutstreamTimeoutLength[0]);
                }
            }

            bidMap.keys.forEach((key) => {
                if (_isConsentRequired() && bidMap[key].waitForConsentToLoad) {
                    // no need to wait for consent to change if user is in an opt-out region
                    if (Mntl.CMP.isOptInConsent()) {
                        _waitForConsentResponseToLoadScripts = true;
                        Mntl.CMP.onConsentChange(() => {
                            if (Mntl.CMP.hasTargetingConsent()) {
                                loadScript(key);
                            }
                        });
                    } else if (Mntl.CMP.hasTargetingConsent()) {
                        loadScript(key);
                    }
                } else {
                    loadScript(key);
                }
            });

            _setBidderScriptsTimeout();
        });
    }

    /**
     * Load bidder libraries if consent is not required, or if consent has been given
     * @return  {Undefined}
     */
    const _loadDisplayAndOutstreamScripts = fnUtils.promiseOnce(() => _loadScripts(_displayAndOutstreamBidMap));

    const _loadVideoScripts = fnUtils.promiseOnce(() => _loadScripts(_videoBidMap));

    function _getValidBidderSizes({config}) {
        return config.sizes.filter((size) => Array.isArray(size) && size.indexOf(1) === -1);
    }

    /**
     * Store slots and provides methods to interact with them
     * Singleton
     * @type {Object}
     */
    _slotQueue = {
        _queue: [],
        push(rtbAction) {
            this._queue.push(rtbAction);
            this.go();
        },
        go() {
            let rtba;

            if ((_bidderScriptsLoaded || _scriptLoadTimedOut) &&
                this._queue.length > 0) {
                rtba = this._queue.shift();

                debug.log('Mntl.RTB:', rtba.caller, ' RTB wait for ', _displayAndOutstreamTimeoutLength[1] + latencyBuffer, Date.now());
                rtba.timeout = window.setTimeout(_getGPTCallback(rtba, true), _displayAndOutstreamTimeoutLength[1] + latencyBuffer);

                debug.log('Mntl.RTB: requesting bids ', _scriptStatus, ' for ', rtba.gptSlots, Date.now());

                const isFirstRecordedSlot = !firstRecordedBidderSlots && rtba.gptSlots.filter((gptSlot) => _getValidBidderSizes(gptSlot).length > 0).length > 0;

                firstRecordedBidderSlots = firstRecordedBidderSlots || isFirstRecordedSlot;

                // For each bidder in the _displayAndOutstreamBidMap
                // eslint-disable-next-line prefer-arrow-callback
                _displayAndOutstreamBidMap.keys.forEach(function setupBidder(key) {
                    const { type } = _displayAndOutstreamBidMap[key];

                    debug.log('Mntl.RTB.Plugins.', type, '.setup (', rtba.caller, ')', _displayAndOutstreamBidMap[key], Date.now());

                    const metricDetails = {
                        instigator: null,
                        metrics: {
                            libraryBidTiming: [
                                {
                                    name: key,
                                    timedMetric: true,
                                    start: Date.now()
                                }
                            ]
                        }
                    };

                    rtba.gptSlots.forEach(({ config: { id } }) => {
                        metricDetails.instigator = id;
                        Mntl.AdMetrics.pushMetrics('slotMetric', metricDetails);    
                    });
                    

                    if (isFirstRecordedSlot) {
                        performanceEvents.publish('performanceTracking', {
                            instigator: 'RTB',
                            event: 'bt',
                            name: type,
                            status: 'start'
                        });
                    }

                    _displayAndOutstreamBidMap[key].setup(rtba.gptSlots, () => {

                        const metricDetails = {
                            instigator: null,
                            metrics: {
                                libraryBidTiming: [
                                    {
                                        name: key,
                                        timedMetric: true,
                                        finish: Date.now()
                                    }
                                ]
                            }
                        };
    
                        rtba.gptSlots.forEach(({ config: { id } }) => {
                            metricDetails.instigator = id;
                            Mntl.AdMetrics.pushMetrics('slotMetric', metricDetails);    
                        });

                        if (isFirstRecordedSlot) {
                            performanceEvents.publish('performanceTracking', {
                                instigator: 'RTB',
                                event: 'bt',
                                name: type,
                                status: 'finish'
                            });
                        }
                        _getGPTCallback(rtba)();
                    });
                });
            }
        }
    };

    /**
     * Stores data for each set of slots RTB is called on. Data is needed throughout
     * phases of the program to track the callback provided by _getGPTCallback
     * @param       {[Object]} gptSlots
     * @param       {Function} callback
     * @param       {String}   caller   used in debug logs
     * @constructor
     */
    function RtbAction(gptSlots, callback, caller) {
        this.gptSlots = gptSlots;
        this.callback = callback;
        this.count = _displayAndOutstreamBidMap.size;
        this.timeout = null;
        this.caller = caller;
    }

    RtbAction.prototype.reset = function() {
        this.count = 0;
        clearTimeout(this.timeout);
    };

    RtbAction.prototype.decrementCount = function() {
        this.count = this.count - 1;
    };

    // Public API

    /**
     * Merge bidder config with Plugin code and save to _displayAndOutstreamBidMap
     * @param  {[Object]} rtbConfig {type: "bidderName", id: "id"}
     * @return {[type]}
     */
    function initDisplayAndOutstreamBidders(rtbConfig) {
        _displayAndOutstreamBidMap = _extendPlugins(rtbConfig);
        _displayAndOutstreamBidMap.keys = Object.keys(_displayAndOutstreamBidMap);
        _displayAndOutstreamBidMap.size = _displayAndOutstreamBidMap.keys.length;
    }


    function initVideoBidders(videoRTBConfig) {
        _videoBidMap = _extendPlugins(videoRTBConfig);
        _videoBidMap.keys = Object.keys(_videoBidMap);
        _videoBidMap.size = _videoBidMap.keys.length;
    }

    /**
     * Start Real Time Bidding (RTB)
     * @param  {[Object]}  gptSlots array of slot configuration objects
     * @param  {Function}  fn       callback, calls GPT _displaySlots @see Mntl.GPT
     * @return {Undefined}
     */
    function initDisplayAndOutstreamSlots(gptSlots, fn) {
        const rtbAction = new RtbAction(gptSlots, fn, 'init');

        _loadDisplayAndOutstreamScripts().then(() => {
            _slotQueue.push(rtbAction);
        });
    }

    function initVideoSlot(videoSlot) {
        return _loadVideoScripts().then(() => {
            const targetingValues = [];

            const videoRTBActions = _videoBidMap.keys.map((key) => new Promise((resolve) => {
                _videoBidMap[key].setup([videoSlot], resolve);
            }).then((bidTargeting) => {
                if(bidTargeting) {
                    targetingValues.push(bidTargeting);
                }

                return targetingValues;
            }));

            const videoRTBTimeout = new Promise((resolve) => {
                window.setTimeout(resolve, _videoBidTimeoutLength);
            });

            return Promise.any([Promise.all(videoRTBActions), videoRTBTimeout]).then(() => targetingValues);
        });
    }

    /**
     * Refresh Real TIme Bidding (RTB)
     * Since _initialize will only fire once and bidder.setup calls have been
     * dis-entangled from _load init and refresh actually to the same thing
     * @param  {[Object]}  gptSlots array of slot configuration objects
     * @param  {Function}  fn       callback, calls GPT _renderSlots @see Mntl.GPT
     * @return {Undefined}
     */
    function refresh(gptSlots, fn) {
        const rtbAction = new RtbAction(gptSlots, fn, 'refresh');

        _slotQueue.push(rtbAction);
    }

    // Getters and Setters
    function getTimeoutLength() {
        return _displayAndOutstreamTimeoutLength[1];
    }

    function setTimeoutLength(timeoutLength) {
        if (Array.isArray(timeoutLength)) {
            Object.assign(_displayAndOutstreamTimeoutLength, timeoutLength); // mutates _displayAndOutstreamTimeoutLength
        } else {
            _displayAndOutstreamTimeoutLength[1] = timeoutLength;
        }
    }

    function setTaxonomyStampValues(taxonomyStampValues) {
        _taxonomyStampValues = taxonomyStampValues;
    }

    function setLatencyBuffer(num) {
        latencyBuffer = num;
    }

    // Events
    // Load libs early if possible
    utils.onLoad(() => {
        // check if bidders have been initialized
        if (_displayAndOutstreamBidMap.size) {
            _loadDisplayAndOutstreamScripts();
        }

        if (_videoBidMap.size) {
            _loadVideoScripts();
        }
    });

    return {
        setTaxonomyStampValues,
        initDisplayAndOutstreamSlots,
        initDisplayAndOutstreamBidders,
        initVideoSlot,
        initVideoBidders,
        refresh,
        Plugins,
        subscribePerformance: performanceEvents.subscribe.bind(performanceEvents),
        getTimeoutLength,
        setTimeoutLength,
        setLatencyBuffer,
        bidz,
        generateGpid
    };
}(window.document, window.Mntl.fnUtilities || {}, window.Mntl.utilities || {}));
// keep an eye on tracking
