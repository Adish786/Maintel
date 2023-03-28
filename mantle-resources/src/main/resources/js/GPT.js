window.Mntl = window.Mntl || {};
window.googletag = window.googletag || {};

googletag.cmd = googletag.cmd || [];

Mntl.GPT = (function() {
    const _kwOnly = Mntl.utilities.getQueryParams().kw_only;
    const _testSite = Mntl.utilities.getQueryParams().adTestSite;
    let _slotCount = 0;
    const _adTiers = {};
    const _callbackQueue = {};
    const _wildcardCallbacks = {};
    const _definedSlots = {};
    const displayOnOptions = ['displayOnScroll'];
    const satisfiedEvents = {}; // used for managing displayOnOptions
    const fallbackFloor = {
        floor: '50',
        id: 'FALLBACK'
    };
    const noFloorForSlotsContaining = [
        'bankingtable',
        'brokertable',
        'button',
        'homepagespotlight',
        'logo',
        'mortgagetable',
        'native',
        'oop',
        'pageviewtracker',
        'partnerlinks',
        'textnote',
        'therapytable',
        'video'
    ];
    let allEventsSatisfied = false;
    let thirdPartyReady = false;
    let thirdPartyReadySlotQueue = [];
    let _config = {
        isMobile: false,
        pageTargeting: {
            ugc: '0',
            dc_ref: encodeURIComponent(window.location.href), // eslint-disable-line camelcase
            hgt: String(window.screen.height),
            wdth: String(window.screen.width),
            path: window.location.pathname.split('/').filter((p) => p)
        },
        baseSlotTargeting: { pc: docCookies.getItem('pc') },
        initialSlots: [],
        auctionFloors: {},
        useAuctionFloorSearch: false,
        utils: {},
        dfpId: '',
        displayOnScroll: false, // displayOn* properties are used to gate displaying the initial slots until the events are satisfied
        displayOnConsent: false 
    };
    const lmdAdSlotCounters = {
        'mob-square-fixed': 0,
        'square-fixed': 0,
        'leaderboard-fixed': 0
    }

    const uniqueAdSizes = {
        /*
        <slot_id>: {                    - Slot id to match
            increment: <boolean>,       - Increment height for each instance of the match
            matchExact: <boolean>,      - Match slot id exactly or use indexOf (contains)
            base: [ <width>, <height> ] - Size to add to slot (prior to incrementing)
        }
        */
        leaderboard: {
            increment: true,
            matchExact: false,
            base: [ 728, 90 ]
        },
        square: {
            increment: true,
            matchExact: false,
            base: [ 300, 250 ]
        },
        'mob-square-fixed-1': {
            increment: false,
            matchExact: true,
            base: [ 300, 600 ]
        }
    };
    const uniqueSlotSizeInstances = {};

    function removeUnallowedCharacters(value) {
        if (value === null) {
            return '';
        }
        let cleanValue = '';
        let argReturnedAsString = false;

        if (Array.isArray(value)) {
            // for arrays, use recursive loop
            cleanValue = value.map((val) => removeUnallowedCharacters(val));
        } else if (typeof value === 'string') {
            // if any targeting values are passed in that contain '+', '&' or '&amp;', they are converted to 'and'
            cleanValue = value.replace(/&amp;|\+|&/g, 'and');
            // if any targeting values are passed in that contain special characters, they're stripped
            cleanValue = cleanValue.replace(/[?'"’=!#*~;^()<>[\]\s]/gi, '');
            argReturnedAsString = true;
        } else if (typeof value === 'number' || typeof value === 'boolean') {
            //converts booleans and numbers to strings
            cleanValue = String(value);
        }
        if (argReturnedAsString && value !== cleanValue) {
            window.debug.log(`Targeting value "${value}" was incorrectly formatted, converted to "${cleanValue}"`);
        }
        
        return cleanValue;
    };

    function getTestIds() {
        // get testIds from data-attrs on HTML element
        const ids = document.documentElement.getAttribute('data-ab');

        // change testIds to array; ad call expects array of strings
        return ids ? ids.split(',') : [];
    }

    function getDebugPageTargeting() {
        const queryParams = Mntl.utilities.getQueryParams();
        const kwParam = queryParams.kw || queryParams.adTestKeyValues;
        
        if (!kwParam) return {};

        const kwSettings = kwParam.split('-');
        const debugPageTargeting = {};

        kwSettings.forEach((setting) => {
            const [key, value] = setting.split(',')

            if (value) {
                debugPageTargeting[key] = value; 
            } else { 
                debugPageTargeting.kw = key;
            }
        });

        return debugPageTargeting;
    }

    /*
     * This function is used externally by the verticals to sort ads for loading order.
     * If the verticals completely transition off the legacy article templates
     * we can deprecate and remove this function.
     */
    function sortSlotsByPriority(slots) {
        // Sort slots according to priority
        return slots.sort(function(a, b) {
            if (!a.targeting.priority && !b.targeting.priority) {
                return 0; // If neither slot has priority, they're equal
            }
            if (a.targeting.priority && !b.targeting.priority) {
                return -1; // If a has priority and b doesn't, a wins
            }
            if (b.targeting.priority && !a.targeting.priority) {
                return 1; // If b has priority and a doesn't, b wins
            }
            if (a.targeting.priority > b.targeting.priority) {
                return 1; // Both a and b must have priority; If a has larger priority, b wins (lower priority is better)
            }
            if (b.targeting.priority > a.targeting.priority) {
                return -1; // If b has larger priority, a wins (lower priority is better)
            } else {
                return 0; // Else b and a must have equal priority
            }
        });
    }

    function getGptSlotFromSlot(slot) {
        const { gptSlot } = _definedSlots[slot.config.id];

        return gptSlot;
    }

    function getGptSlotsFromSlots(slots) {
        const gptSlots = [];
        let i;
        let len;

        for (i = 0, len = slots.length; i < len; i++) {
            const gptSlot = getGptSlotFromSlot(slots[i]);

            if (gptSlot) {
                gptSlots.push(gptSlot);
            }
        }

        return gptSlots;
    }

    function clearSlots(slots) {
        const gptSlots = getGptSlotsFromSlots(slots);

        if (!gptSlots.length) {
            return;
        }

        googletag.cmd.push(function() {
            window.debug.log('Clearing slots', gptSlots);
            googletag.pubads().clear(gptSlots);
        });
    }

    function destroySlots(slots) {
        const gptSlots = getGptSlotsFromSlots(slots);
        let i;
        let len;

        if (!gptSlots.length) {
            return;
        }

        for (i = 0, len = slots.length; i < len; i++) {
            if (_definedSlots[slots[i].config.id]) {
                window.debug.log('Deleting slot ' + slots[i].config.id);
                delete _definedSlots[slots[i].config.id];
                _slotCount--;
            }
        }

        googletag.cmd.push(function() {
            googletag.destroySlots(gptSlots);
        });
    }

    function getSlotById(id) {
        return _definedSlots[id];
    }

    function destroySlotById(slotElementId) {
        const slotsToDestroy = _definedSlots[slotElementId];

        destroySlots([slotsToDestroy]);
    }

    function _splitSlotsOnRenderTiming(slotObjs, thirdPartyReady) { 
        return slotObjs.reduce((slots, slot) => {
            if (slot.config.waitForThirdParty && !thirdPartyReady) {
                slots[0].push(getGptSlotFromSlot(slot));
            } else {
                slots[1].push(getGptSlotFromSlot(slot));
            }

            return slots;
        }, [[], []]); // [renderAfterThirdPartyReady, renderNow] 
    }

    function _renderSlots(slots) {
        const gptSlots = getGptSlotsFromSlots(slots);

        if (!gptSlots.length) {
            return;
        }

        if (_config.pageTargeting.kw === 'fakeAds' && window.location.search.indexOf('state=PREVIEW') !== -1) {
            gptSlots.forEach(function(slot) {
                const domId = slot.getSlotId().getDomId();
                const adDiv = document.getElementById(domId);
                const maxAdSize = slot.getSizes().reduce(function(accumulator, currentValue) {
                    if (typeof currentValue === 'object') {
                        if ((currentValue.getHeight() * currentValue.getWidth()) > (accumulator.height * accumulator.width)) {
                            return {
                                height: currentValue.getHeight(),
                                width: currentValue.getWidth()
                            };
                        } else {
                            return accumulator;
                        }
                        // if "fluid" given it's a string so assume last largest area ad or default
                    } else {
                        return accumulator;
                    }
                }, {
                    // Using a default 50x50 ad if only a "fluid" ad is given. There's no way to determine the ad size set as "fluid".
                    height: 50,
                    width: 50
                });
                const fakeAd = document.createElement('div');

                // If this ever happens you are requesting an ad that is not on the page and you should fix that.
                if (adDiv === null) {
                    return;
                }

                fakeAd.style.backgroundColor = '#e8e8e8';
                fakeAd.style.width = maxAdSize.width + 'px';
                fakeAd.style.height = maxAdSize.height + 'px';
                adDiv.parentNode.setAttribute('data-ad-height', maxAdSize.height);
                adDiv.parentNode.setAttribute('data-ad-width', maxAdSize.width);
                adDiv.appendChild(fakeAd);
            });
        } else {
            if (_kwOnly) {
                gptSlots.forEach(function(gptSlot) {
                    // remove all slot-level targeting
                    gptSlot.clearTargeting();
                });
                // remove all page-level targeting except kw_only
                googletag.pubads().clearTargeting();
                googletag.pubads().setTargeting('kw_only', _kwOnly);
            }

            if (Mntl.RTB) {
                Mntl.RTB.bidz.setFloors(slots, _config.useLmdFormat);
            }

            Mntl.Targeting.removeBlocklistedSlotTargeting(gptSlots);
            
            const [renderAfterThirdPartyReady, renderNow] = _splitSlotsOnRenderTiming(slots, thirdPartyReady);

            if (renderNow.length > 0) {
                googletag.cmd.push(() => {
                    window.debug.log('Rendering slots', renderNow);
                    // disable auto-updating of correlator value on refresh() and expose an updatePageview() function
                    // that updates the correlator value
                    googletag.pubads().refresh(renderNow, { changeCorrelator: false });
                });
            }

            if (renderAfterThirdPartyReady.length > 0) {
                thirdPartyReadySlotQueue = thirdPartyReadySlotQueue.concat(renderAfterThirdPartyReady);
            }
        }
    }

    function _displaySlots(slots) {
        const displayIntervals = [];
        const displayedSlots = [];
        let displayTimeout;
        let i;
        let len;

        window.debug.log('Slots to display:', slots);
        for (i = 0, len = slots.length; i < len; i++) {
            (function(ind) {
                displayIntervals.push(setInterval(function() {
                    const slot = slots[ind];
                    const slotId = slot.config.id;

                    // Make sure the slot element exists on the page before calling googletag.display()
                    if (!document.getElementById(slotId)) {
                        return;
                    }

                    // Save the slot on the element in case we need to access later (e.g. like destroying slots)
                    document.getElementById(slotId).slotObj = {
                        config: slot.config,
                        updateTargeting: slot.updateTargeting
                    };

                    clearInterval(displayIntervals[ind]);

                    // Call googletag.display()
                    // This only marks it ready to render
                    // googletag.pubads().refresh() actually renders the ad on the page
                    window.debug.log('Found slot ' + slotId + '. Calling googletag.display()');
                    googletag.display(slotId);

                    slot.displayState = 'displayed';

                    // If not in single request mode, call refresh on each slot individually
                    // Else, keep track of slots so we can refresh all slots at once
                    if (!_config.singleRequest) {
                        _renderSlots([slot]);
                    } else {
                        displayedSlots.push(slot);
                        if (displayedSlots.length === slots.length) {
                            clearTimeout(displayTimeout);
                            window.debug.log('Single request mode: all slots found, now calling refresh');
                            _renderSlots(slots);
                        }
                    }
                }, 25));
            }(i));
        }

        displayTimeout = setTimeout(function() {
            let j;
            let jEnd;

            // Clear all intervals
            for (j = 0, jEnd = displayIntervals.length; j < jEnd; j++) {
                clearInterval(displayIntervals[j]);
            }
            // If in single request mode, display the slots we did find
            if (_config.singleRequest && displayedSlots.length !== slots.length) {
                window.debug.log('Single request mode: all slots not found, calling refresh on found slots');

                _renderSlots(displayedSlots);
            }
        }, 10000);
    }

    function _refreshSlots(slots) {
        const rtbSlots = [];
        const nonRtbSlots = [];

        if (!slots.length) {
            return;
        }

        if (Mntl.RTB) {
            slots.forEach(function(slot) {
                if (slot.config.rtb) {
                    rtbSlots.push(slot);
                } else {
                    nonRtbSlots.push(slot);
                }
            });
            window.debug.log('Passing slot ids to Mntl RTB refresh', rtbSlots);
            Mntl.RTB.refresh(rtbSlots, function() {
                window.debug.log('Running Mntl RTB refresh callback and refreshing slots', rtbSlots);
                _renderSlots(rtbSlots);
            });
            window.debug.log('Bypassing RTB and refreshing slots', nonRtbSlots);
            _displaySlots(nonRtbSlots);
        } else {
            window.debug.log('Refreshing slots', slots);
            _renderSlots(slots);
        }
    }

    function _displaySlotsAllEventsSatisfied(slots) {
        const slotsToDisplay = [];
        const slotsToRefresh = [];
        let i;
        let len;

        // If slots specified, determine what to refresh and what to display for the first time
        for (i = 0, len = slots.length; i < len; i++) {
            // Display if we haven't already called or attempted to call googletag.display() on the slot
            if (slots[i].displayState !== 'displayed' && slots[i].displayState !== 'displaying') {
                slots[i].displayState = 'displaying';
                slotsToDisplay.push(slots[i]);
                // Else, if we've already called googletag.display on the slot, refresh it
            } else if (slots[i].displayState === 'displayed') {
                slotsToRefresh.push(slots[i]);
            }
        }

        // Refresh any existing slots
        if (slotsToRefresh.length) {
            _refreshSlots(slotsToRefresh);
        }
        // Display any new slots
        if (!slotsToDisplay.length) {
            return;
        }
        googletag.cmd.push(function() {
            const rtbSlots = [];
            const nonRtbSlots = [];

            if (Mntl.RTB && window.location.search.indexOf('google_nofetch') === -1) {
                slots.forEach(function(slot) {
                    if (slot.config.rtb) {
                        rtbSlots.push(slot);
                    } else {
                        nonRtbSlots.push(slot);
                    }
                });

                if (rtbSlots.length) {
                    window.debug.log('Passing slots to Mntl RTB', rtbSlots);
                    Mntl.RTB.initDisplayAndOutstreamSlots(rtbSlots, function() {
                        window.debug.log('Running Mtnl RTB init callback');
                        _displaySlots([...rtbSlots, ...nonRtbSlots]);
                    });
                } else {
                    window.debug.log('Bypassing RTB and displaying slots', nonRtbSlots);
                    _displaySlots(nonRtbSlots);
                }
            } else {
                _displaySlots(slotsToDisplay);
            }
        });
    }

    // used to check if a displayOn* option applies to this page
    function _eventApplies(event) {
        return typeof event === 'string' && displayOnOptions.indexOf(event) !== -1 && _config[event];
    }

    function _eventSatisfied(event) {
        return !_eventApplies(event) || satisfiedEvents[event];
    }

    // used to gate display of slots until all the events are satisfied
    function _satisfiedEventHandler(satisfiedEvent, slots) {
        satisfiedEvents[satisfiedEvent] = true; // record the event
        if (displayOnOptions.every(_eventSatisfied)) {
            allEventsSatisfied = true;
            _displaySlotsAllEventsSatisfied(slots);
        }
    }

    function displaySlots(slots) {
        let key;

        // If slots not specified, use previously defined slots
        if (slots === null || typeof slots === 'undefined') {
            slots = [];
            for (key in _definedSlots) {
                slots.push(_definedSlots[key]);
            }
        }

        // If an ad was already requested, reset the slot targeting (clears off header bidding targeting)
        slots.forEach((slot) => {
            if (slot.requested) {
                window.debug.log(`Refreshing ad slot: ${slot.id}`);
                slot.resetTargeting();
            }
        });


        // Don't return if displaySlots is called with an empty slot array. We must set up the below event listeners even if no initial
        // ads are displayed on the page so that allEventsSatisfied's value can be properly updated and allow lazy loaded ads to display
        if (!slots.length) {
            window.debug.error('No slots to display');
        }

        if (!allEventsSatisfied && displayOnOptions.some(_eventApplies)) {
            if (_eventApplies('displayOnScroll')) {
                window.addEventListener('scroll', _satisfiedEventHandler.bind(null, 'displayOnScroll', slots), { once: true });
            }
            if (_eventApplies('displayOnConsent')) {
                Mntl.CMP.onConsentChange(_satisfiedEventHandler.bind(null, 'displayOnConsent', slots), true);
            }
        } else {
            _displaySlotsAllEventsSatisfied(slots);
        }
    }

    function _validTargetingValue(value) {
        // If an array, make sure it has valid members
        if (Array.isArray(value) && value.length) {
            return value.map((val) => typeof val !== 'undefined' && val !== null && val !== '').length;
        }

        return typeof value !== 'undefined' && value !== null && value !== '';
    }

    function _cleanTargeting(targetingObj) {
        const targetingDenyList = {
            dd: ['path', 'pv', 'mtax'],
            lmd: ['dc_ref']
        };
        const activeTargetingDenyList = targetingDenyList[_config.useLmdFormat ? 'lmd' : 'dd'];

        Mntl.fnUtilities.iterate(targetingObj, (targeting, key) => {
            if (activeTargetingDenyList.indexOf(key) > -1) {
                delete targeting[key];
            }
        });
    }

    function _createSlotCallbackQueue(queue, slotId) {
        queue[slotId] = {
            fn: [],
            rendered: false
        };
    }

    function _buildCategory() {
        const slotTargeting = _config.baseSlotTargeting;
        // defaults to homepage on homepages, otherwise empty
        const defaultValue = window.location.pathname === '/' ? 'homepage': '';
        // use tax1 if available, otherwise use tax2 
        const tax = slotTargeting.tax1 || defaultValue;
        const categoryBits = tax.split('_');
        const category = categoryBits[categoryBits.length - 1].replace(/ /g, '');

        return removeUnallowedCharacters(category);
    }

    function _mapLmdType(type) {
        const lmdTemplateTypeMap = {
            taxonomy: 'category',
            bio: 'category',
            list: 'slideshow',
            howto: 'project',
            jwplayer: 'video'
        };

        type = type.replace(' ','');

        return lmdTemplateTypeMap[type] || type || 'none';
    }

    function getSitePath(useOxygen = _config.useOxygen, pageConfig = _config) {
        let site;

        if (useOxygen) {
            const domain = Mntl.utilities.getDomain();

            site = `ddm.${pageConfig.isMobile ? domain.replace('.com', '.mob') : domain}`;
        } else {
            site = `${pageConfig.lmdSiteCode}.mdp.${pageConfig.isMobile ? 'mob' : 'com'}`;
        }

        return site;
    }

    function getPageType() {
        return _config.baseSlotTargeting.type;
    }

    function _mapOxygenType(templateName, category) {
        if (category === 'homepage') {
            return 'taxonomy';
        }

        const allowedTypes = [
            'taxonomy',
            'taxonomysc',
            'structuredcontent',
            'listsc',
            'recipesc'
        ];

        return allowedTypes.indexOf(templateName) !== -1 ? templateName : 'other';
    }

    function _mapPageType(config) {
        const { 
            useOxygen,
            templateName,
            baseSlotTargeting,
            pageTargeting
        } = config;

        return useOxygen ? _mapOxygenType(templateName, pageTargeting.category) : _mapLmdType(baseSlotTargeting.type);
    }

    function getGptUrl(slotConfig, slotTargeting, pageTargeting, pageConfig) {
        let gptUrl;

        if(pageConfig.useOxygen || pageConfig.useLmdFormat) {
            const { dfpId } = pageConfig;
            const tier = 'tier1';
            const { category } = pageTargeting;
            const { type } = slotTargeting;
            const site = getSitePath(pageConfig.useOxygen, pageConfig);

            gptUrl = (slotConfig.type === 'native') ? `${dfpId}/${site}/ntv1` :
                `${dfpId}/${site}/${tier}/${type}/${category}`;
        } else {
            gptUrl = pageConfig.utils.buildGptUrl(slotConfig, slotTargeting, pageTargeting, pageConfig);
        }

        // Override top level ad unit per URL param
        if (_testSite) {
            const gptUrlBits = gptUrl.split('/');

            gptUrlBits[1] = _testSite;
            gptUrl = gptUrlBits.join('/');
        }

        return gptUrl;
    }

    function Slot() {
        this.config = {};
        this.targeting = {}; // all targeting currently set on a slot
        this.baseTargeting = {}; // targeting that persistes for every slot request - is not cleared off by Slot.clearTargeting()
        this.gptSlot = null;
        this.el = null;
        this.displayState = null; // "displayed" or "displaying"

        return this;
    }

    Slot.prototype.getFloorConfig = function() {
        if (!_config.useAuctionFloorSearch) {
            return {
                floor: '',
                id: 'SKIPFLOOR'
            };
        }

        let floorKey = this.config.id;
        const floorKeyParts = floorKey.split('-');
        let floorInfo = _config.auctionFloors.other || fallbackFloor;

        // Determine if a floor should be fetched for this slot
        const shouldHaveFloor = noFloorForSlotsContaining
            .filter((slotIdPart) => floorKey.indexOf(slotIdPart) !== -1)
            .length === 0;

        if (!shouldHaveFloor) {
            return {
                id: 'NOFLOOR',
                floor: ''
            };
        }

        // Check for a key that matches a portion of the ID
        for (let i = floorKeyParts.length; i > 0; i--) {
            floorKey = floorKeyParts.slice(0, i).join('-');
            if (typeof _config.auctionFloors[floorKey] !== 'undefined') {
                floorInfo = _config.auctionFloors[floorKey];
                break;
            }
        }

        return floorInfo;
    };

    Slot.prototype.getFloorValueCents = function() {
        const floorInfo = this.getFloorConfig();
        const floorPriceCents = Number(floorInfo.floor);

        return floorInfo.floor !== '' && !isNaN(floorPriceCents) &&
            floorPriceCents;
    };

    Slot.prototype.setFloorTargeting = function() {
        if (!_config.useAuctionFloorSearch) return;

        const floorInfo = this.getFloorConfig();

        this.targeting = Mntl.fnUtilities.deepExtend(this.targeting, {
            floor_id: floorInfo.id, // eslint-disable-line camelcase
            floor: floorInfo.floor
        });
    };

    function needsUniqueSlot(id) {
        const uniqueAdSizesKeys = Object.keys(uniqueAdSizes);
        const result = uniqueAdSizesKeys.filter((key) => {
            const {matchExact} = uniqueAdSizes[key];
            
            return (matchExact && key === id) || (!matchExact && id.indexOf(key) > -1);
        });

        return result;
    }

    Slot.prototype.updateSizes = function() {
        needsUniqueSlot(this.config.id)
            .forEach((slotType) => {
                if (!uniqueSlotSizeInstances.hasOwnProperty(slotType)) {
                    uniqueSlotSizeInstances[slotType] = new Mntl.UniqueSlotSize(slotType, uniqueAdSizes[slotType]);
                }
                
                this.config.sizes.push(uniqueSlotSizeInstances[slotType].getUniqueSize());
            });
    };

    Slot.prototype.byElement = function(el, overrideId, targeting) {
        if (lmdAdSlotCounters.hasOwnProperty(el.id)) {
            // we have an lmd ad slot we need to use a counter to append to the ad slot
            lmdAdSlotCounters[el.id] = lmdAdSlotCounters[el.id] + 1;
            overrideId = `${el.id}-${lmdAdSlotCounters[el.id]}`;
        }
        
        const slotTargeting = {
            pos: el.getAttribute('data-pos'),
            priority: Number(el.getAttribute('data-priority'))
        };

        if (!_config.useAuctionFloorSearch) {
            slotTargeting.floor_id = el.getAttribute('data-auction-floor-id'); // eslint-disable-line camelcase
            slotTargeting.floor = el.getAttribute('data-auction-floor-value');
        }
        
        this.config.id = overrideId || el.getAttribute('id');
        this.config.type = el.getAttribute('data-type');
        this.config.sizes = Mntl.fnUtilities.safeJsonParse(el.getAttribute('data-sizes').replace(/'/g, '"'));
        this.config.rtb = el.getAttribute('data-rtb') === 'true';
        this.config.waitForThirdParty = el.getAttribute('data-wait-for-third-party') === 'true';
        this.targeting = targeting || {};
        this.targeting = Mntl.fnUtilities.deepExtend(this.targeting, Mntl.fnUtilities.safeJsonParse(el.getAttribute('data-targeting')));
        this.targeting = Mntl.fnUtilities.deepExtend(this.targeting, slotTargeting);
        this.setFloorTargeting();
        this.setEl(el);
        this.updateSizes();
        this.defineGptSlot();
        this.pushMetrics();

        return this;
    };

    Slot.prototype.byConfig = function(config, targeting, gptSlot, el) {
        let slotEl = el || null;

        this.config = config;
        this.targeting = targeting;
        this.setFloorTargeting();
        this.gptSlot = gptSlot || null;

        if (lmdAdSlotCounters.hasOwnProperty(this.config.id)) {
            // this will find the first element with this id.  Since the incrementor hasn't been
            // added yet, there could be multiple slots with this id; this will retrieve the first
            // slot with this id
            slotEl = document.getElementById(this.config.id);
            
            lmdAdSlotCounters[this.config.id] = lmdAdSlotCounters[this.config.id] + 1;
            this.config.id = `${this.config.id}-${lmdAdSlotCounters[this.config.id]}`;
        }

        if (slotEl) {
            this.setEl(slotEl);
        }
        this.updateSizes();
        this.defineGptSlot();
        this.pushMetrics();

        return this;
    };

    Slot.prototype.pushMetrics = function() {
        Mntl.AdMetrics.pushMetrics('slotCreated', {
            instigator: this.config.id,
            metrics: {
                floors: {
                    floor: this.targeting.floor,
                    floorId: this.targeting.floor_id
                }
            }
        });
    }

    Slot.prototype.setEl = function(el) {
        this.el = el;
        el.setAttribute('id', this.config.id);
        el.slotObj = {
            config: this.config,
            updateTargeting: this.updateTargeting
        };
    };

    Slot.prototype.updateTargeting = function(newTargeting, updateBase = true) {
        if (!this.gptSlot) {
            window.debug.error('No GPT slot defined');

            return;
        }

        Mntl.fnUtilities.iterate(newTargeting, (targeting, key) => {
            const val = targeting[key];

            if (_validTargetingValue(val)) {
                this.targeting[key] = val;
                this.gptSlot.setTargeting(key, removeUnallowedCharacters(val));
                if (updateBase) {
                    this.baseTargeting[key] = val;
                }
            } else {
                delete this.targeting[key];
                if (updateBase) {
                    delete this.baseTargeting[key];
                }
                if (
                    this.gptSlot.getTargeting(key) instanceof Array &&
                    this.gptSlot.getTargeting(key).length > 0
                ) {
                    this.gptSlot.clearTargeting(key);
                }
            }
        });
    };

    Slot.prototype.resetTargeting = function() {
        this.targeting = {};
        this.gptSlot && this.gptSlot.clearTargeting(); // if there is no gptSlot, error will be logged in updateTargeting
        this.updateTargeting(this.baseTargeting);
    };

    Slot.prototype.defineGptSlot = function() {
        if (!this.config.id) {
            window.debug.error('Error instantiating slot: no id provided');

            return null;
        }
        if (_definedSlots[this.config.id]) {
            window.debug.error(`Error instantiating slot ${this.config.id}: slot already exists`);

            return _definedSlots[this.config.id];
        }
        _definedSlots[this.config.id] = this;

        googletag.cmd.push(() => this.googletagDefineGptSlot());

        return this;
    };

    Slot.prototype.googletagDefineGptSlot = function() {
        // Extend slot targeting with base slot targeting
        this.targeting = Mntl.fnUtilities.deepExtend(this.targeting, _config.baseSlotTargeting);
        this.targeting.tile = `${_slotCount++}`;

        // Add Slot ID to slot level targeting as per GLBE-6559
        _adTiers[this.config.type] = _adTiers[this.config.type] || 1;
        this.config.tier = `${_adTiers[this.config.type]++}`;
        if (_adTiers[this.config.type] > 4) {
            _adTiers[this.config.type] = 1;
        }

        if (typeof this.config.id === 'string' && this.config.id.indexOf('dynamic-inline') !== -1) {
            this.targeting.slot = 'dynamic-inline';
        } else {
            this.targeting.slot = this.config.id;
        }

        const gptUrl = getGptUrl(this.config, this.targeting, _config.pageTargeting, _config);

        window.debug.log(`Defining gpt slot with url: ${gptUrl} ...`);

        // Define gpt slot and save it on the Slot instance
        this.gptSlot = this.config.type === 'outofpage' ? googletag.defineOutOfPageSlot(gptUrl, this.config.id) : googletag.defineSlot(gptUrl, this.config.sizes, this.config.id);

        window.debug.log('Gpt slot defined', this.gptSlot);

        if (_config.pageTargeting.companion) {
            googletag.pubads().collapseEmptyDivs(true);
            window.debug.log('Adding companionAds service');
            this.gptSlot.addService(googletag.companionAds());
        }

        // Set slot targeting values
        Mntl.fnUtilities.iterate(this.targeting, (slotTargeting, key) => {
            const val = slotTargeting[key];

            if (_validTargetingValue(val)) {
                window.debug.log(`Setting slot targeting key ${key} as ${val}`);
                this.gptSlot.setTargeting(key, removeUnallowedCharacters(val));
                this.baseTargeting[key] = val;
            }
        });

        window.debug.log('Adding pubads service');
        this.gptSlot.addService(googletag.pubads());

        if (!_callbackQueue[this.config.id]) {
            _createSlotCallbackQueue(_callbackQueue, this.config.id);
        }
    };

    function _onImpressionViewable(e) {
        const id = e.slot.getSlotId().getDomId();
        const el = document.getElementById(id);
        const adViewableEvent = new CustomEvent('adViewable', { bubbles: true });

        el.dispatchEvent(adViewableEvent);
    }

    function _onSlotRequested(e) {
        const id = e.slot.getSlotId().getDomId();
        const el = document.getElementById(id);
        const slotRequestedEvent = new CustomEvent('slotRequested', { bubbles: true });
        const slotObj = Mntl.GPT.getSlotById(id);
        const metrics = {
            tier: slotObj.config.tier,
            gam: {
                slotRequested: {
                    timedMetric: true,
                    timestamp: Date.now()
                }
            }
        };

        if (slotObj.targeting.rord) {
            metrics.rord = slotObj.targeting.rord;

            if (slotObj.targeting.refresh) {
                metrics.refresh = slotObj.targeting.refresh;
            }
        }

        Mntl.AdMetrics.pushMetrics('slotMetric', {
            instigator: id,
            metrics
        });

        el.dispatchEvent(slotRequestedEvent);
    }

    function _onSlotRenderEnded(e) {
        const id = e.slot.getSlotId().getDomId();
        const el = document.getElementById(id);
        const supportsFluid = e.slot.getSizes().some((size) => size === 'fluid'); // i.e. determine if 'fluid' is in the array.
        const containerEl = el.parentElement;
        const cb = _callbackQueue[id];
        let i;
        let len;
        const adRenderedEvent = new CustomEvent('adRendered', { bubbles: true });

        window.debug.log('Slot render ended', id);
        window.debug.log('Slot supportsFluid equals', supportsFluid);

        const metrics = {
            gam: {
                advertiserId: e.advertiserId,
                creativeId: e.sourceAgnosticCreativeId,
                creativeTemplateId: e.creativeTemplateId,
                lineItemId: e.sourceAgnosticLineItemId,
                dimensions: e.size ? `${e.size[0]}x${e.size[1]}` : null,
                slotRenderEnded: {
                    timedMetric: true,
                    timestamp: Date.now()
                }
            }
        }

        if (typeof el.slotObj === 'object') {
            _definedSlots[el.id].requested = true
            metrics.floors = {
                bz: _definedSlots[el.id].targeting.bz,
                bzr: _definedSlots[el.id].targeting.bzr
            }
        }

        Mntl.AdMetrics.pushMetrics('slotMetric', {
            instigator: id,
            metrics
        });

        // @internal may be deprecated by flexible ads
        if (e.size && e.size.length) {
            // Add data attributes for css targeting
            if ((e.size[0] === 0) && (e.size[1] === 0) && supportsFluid) {
                containerEl.setAttribute('data-ad-width', 'fluid');
                containerEl.setAttribute('data-ad-height', 'fluid');
            } else {
                containerEl.setAttribute('data-ad-width', e.size[0]);
                containerEl.setAttribute('data-ad-height', e.size[1]);
            }
        }

        // Added for BB locking
        el.dispatchEvent(adRenderedEvent);

        // Create args for this slot's callback functions
        cb.rendered = true;
        cb.args = {
            id,
            slot: e.slot,
            lid: e.lineItemId,
            size: e.size,
            isEmpty: e.isEmpty
        };

        // Run callback functions
        if (cb.fn.length > 0) {
            for (i = 0, len = cb.fn.length; i < len; i++) {
                cb.fn[i](cb.args);
            }
        }
        // Run any matching wildcard callback functions
        Mntl.fnUtilities.iterate(_wildcardCallbacks, (_wildcardCbs, regexString) => {
            if (id.match(new RegExp(regexString)) !== null) {
                _wildcardCbs[regexString].forEach((wildcardCallback) => {
                    wildcardCallback(cb.args);
                });
            }
        });
    }

    function _onSlotOnload(e) {
        const id = e.slot.getSlotId().getDomId();

        Mntl.AdMetrics.pushMetrics('slotMetric', {
            instigator: id,
            metrics: {
                gam: {
                    slotOnload: {
                        timedMetric: true,
                        timestamp: Date.now()
                    }
                }
            }
        });
    }

    function _onSlotResponseReceived(e) {
        const id = e.slot.getSlotId().getDomId();

        Mntl.AdMetrics.pushMetrics('slotMetric', {
            instigator: id,
            metrics: {
                gam: {
                    slotResponseReceived: {
                        timedMetric: true,
                        timestamp: Date.now()
                    }
                }
            }
        });
    }

    function updatePageview() {
        googletag.cmd.push(() => {
            // call any googletag methods for a new pageview here
            googletag.pubads().updateCorrelator();
        });
    }

    function updateBaseSlotTargeting(slotTargeting, updateDefinedSlots, updateDisplayedSlots) {
        // update targeting for any newly defined slots from now on
        Mntl.fnUtilities.iterate(slotTargeting, (slotTarget, key) => {
            if (_validTargetingValue(slotTarget[key])) {
                _config.baseSlotTargeting[key] = slotTarget[key];
                window.debug.log(`Setting slot targeting ${key} to ${slotTarget[key].toString()}`);
            } else if (_config.baseSlotTargeting[key]) {
                delete _config.baseSlotTargeting[key];
                window.debug.log(`Clearing slot targeting key ${key}`);
            }
        });

        // update targeting for already defined/rendered slots
        if (updateDefinedSlots) {
            Object.keys(_definedSlots).forEach((definedSlotId) => {
                const definedSlot = _definedSlots[definedSlotId];

                if (definedSlot && (updateDisplayedSlots || !definedSlot.displayState)) {
                    definedSlot.updateTargeting(slotTargeting); // handles additions, changes, and deletions itself
                }
            });
        }
    }

    function updatePageTargeting(pageTargeting) {
        const arrayTargetingValues = ['mtax', 'path'];
        const blocklist = Mntl.Targeting.getBlocklist();
        
        googletag.cmd.push(() => {
            Mntl.fnUtilities.iterate(pageTargeting, (targeting, key) => {

                // Do not add page level targeting keys that are in the blocklist
                if (blocklist.includes(key)) {
                    Mntl.Targeting.logBlocklistedKeys(key);

                    return;
                }

                let val = targeting[key];
                
                if (_validTargetingValue(val)) {
                    val = removeUnallowedCharacters(val);

                    if (!Array.isArray(val) || (Array.isArray(val) && arrayTargetingValues.indexOf(key) === -1)) {
                        val = val.toString()
                    }

                    window.debug.log(`Setting page targeting key ${key} to ${val}`);
                    googletag.pubads().setTargeting(key, val);
                }
            });
        });
    }

    function getPageTargeting(){
        return _config.pageTargeting;
    }

    function addRequestSizeToSlot(slotId, size) {
        const gptSlot = Mntl.fnUtilities.getDeepValue(_definedSlots, slotId, 'gptSlot');

        if (!gptSlot) {
            return;
        }

        // create a 'sizes' array with the existing sizes
        const sizes = gptSlot.getSizes().map((slot) => (slot === 'fluid' ? 'fluid' : [slot.getWidth(), slot.getHeight()]));

        const mapping = googletag.sizeMapping()
            .addSize([0, 0], sizes.concat([size])) // add new size to the existing sizes
            .build();

        gptSlot.defineSizeMapping(mapping);
    }

    function removeRequestSizeFromSlot(slotId, size) {
        const slot = _definedSlots[slotId];
        const gptSlot = slot ? slot.gptSlot : null;
        let refreshSlot = false;
        let i;

        if (!gptSlot) {
            return;
        }

        const slotSizes = gptSlot.getSizes();

        for (i = 0; i < slotSizes.length; i++) {
            if (slotSizes[i].getWidth() === size[0] && slotSizes[i].getHeight() === size[1]) {
                refreshSlot = true;
                slotSizes.splice(i, 1);
            }
        }
        if (refreshSlot) {
            gptSlot.defineSizeMapping(slotSizes);
            _refreshSlots([slot]);
        }
    }

    function registerCallback(slotIds, fn) {
        window.debug.log('Registering callback', slotIds, fn);

        function handleCallback(slotId) {
            if (_callbackQueue.hasOwnProperty(slotId)) {
                _callbackQueue[slotId].fn.push(fn);
            } else {
                _callbackQueue[slotId] = {};
            }

            if (_callbackQueue[slotId].rendered) {
                fn(_callbackQueue[slotId].args);
            }
        }

        function handleWildcard(regexString) {
            if (_wildcardCallbacks.hasOwnProperty(regexString)) {
                _wildcardCallbacks[regexString].push(fn);
            } else {
                _wildcardCallbacks[regexString] = [fn];
            }
        }

        slotIds.forEach((slotId) => {
            let regexString;
            let match;

            if (slotId.indexOf('*') > -1) {
                regexString = slotId.replace('*', '(?:(?:.+)|^|$)');
                handleWildcard(regexString);
                Mntl.fnUtilities.iterate(_callbackQueue, (_cbQueue, id) => {
                    match = id.match(new RegExp(regexString));
                    if (match !== null && match.length) {
                        handleCallback(id);
                    }
                });
            } else {
                if (!_callbackQueue.hasOwnProperty(slotId)) {
                    _createSlotCallbackQueue(_callbackQueue, slotId);
                }
                handleCallback(slotId);
            }
        });
    }

    function isMobile() {
        return _config.isMobile;
    }

    function _serializeTargetingValues(targetingValues) {
        const serialized = [];
        let key;

        for (key in targetingValues) {
            serialized.push(`${key}=${encodeURIComponent(targetingValues[key])}`);
        }

        // Escape the value twice to match how it was set from Google
        return encodeURIComponent(serialized.join('&'));
    }

    function serializePageTargeting() {
        return _serializeTargetingValues(_config.pageTargeting);
    }

    function serializeBaseSlotTargeting() {
        return _serializeTargetingValues(_config.baseSlotTargeting);
    }

    function serializeAllTargeting() {
        return _serializeTargetingValues(
            Object.assign({}, _config.baseSlotTargeting, _config.pageTargeting)
        );
    }

    function isSingleRequest() {
        return _config.singleRequest;
    }

    function getUtils() {
        return _config.utils;
    }

    function getDfpId() {
        return _config.dfpId;
    }

    function getPublisherProvidedId() {
        return _config.publisherProvidedId;
    }

    function getUseLmdFormat() {
        return _config.useLmdFormat;
    }

    function getUseOxygen() {
        return _config.useOxygen;
    }

    function getUseInfiniteRightRail() {
        return _config.useInfiniteRightRail;
    }

    function getBundlePrebid() {
        return _config.bundlePrebid;
    }

    function getVideoTargeting() {
        const videoTargeting = {
            ..._config.baseSlotTargeting,
            ..._config.pageTargeting,
            id: _config.baseSlotTargeting.id,        
            muid: getPublisherProvidedId(),
            type: _config.baseSlotTargeting.type
        }

        return videoTargeting;
    }

    function _setPersonalization() {
        googletag.cmd.push(() => {
            if (!Mntl.CMP) {
                return;
            }

            const targeting = {};
            const hasTargetingConsent = Mntl.CMP.hasTargetingConsent();

            if (Mntl.CMP.isConsentRequired()) {
                targeting.adconsent = hasTargetingConsent ? 'y' : 'n';
    
                if (!_config.useLmdFormat || _config.geo.isInEurope) {
                    googletag.pubads().setPrivacySettings({ nonPersonalizedAds: !hasTargetingConsent });
                    window.debug.log(`Mntl.GPT: Non-Lmd or isInEurope (hasTargetingConsent: ${hasTargetingConsent}), setting non-personalized ads mode: ${!hasTargetingConsent}`);
                } else if (_config.geo.isInUsa) {
                    // although as it stands this [isInUsa] will only currently be reached by California/US visitors, we are putting this check in
                    // in order to future proof for when Mntl.CMP has been opened up to all of the US and not just California
                    // Mntl.CMP may at that time may be able to tell us if the user is anywhere in the US, but to ensure this is future proofed, we are
                    // adding it this way for now.
                    googletag.pubads().setPrivacySettings({ 'restrictDataProcessing': !hasTargetingConsent });
                    window.debug.log(`Mntl.GPT: isInUsa (hasTargetingConsent: ${hasTargetingConsent}), setting restrict data processing mode: ${!hasTargetingConsent}`);
                }
            } else {
                targeting.adconsent = 'na';
            }
            updateBaseSlotTargeting(targeting, true, true);
        });
    }

    function _getPageViewCount() {
        const expirationMs = 43200000, // 12 hour expiration
            currentTime = Date.now();
        let expired = false, 
            pv = 0;

        try{
            // if no session is set or the TTL has expired, create localStorage.ddmAdSessionTTL for 12 hours in the future
            if (!localStorage.ddmAdSessionTTL || currentTime > localStorage.ddmAdSessionTTL) {
                expired = true;
                localStorage.setItem('ddmAdSessionTTL', currentTime + expirationMs);
            }

            // retrieve the page count from local storage
            if (!expired && localStorage.getItem('ddmAdPageCount') !== null) {
                pv = parseInt(localStorage.ddmAdPageCount, 10);
            }
            // increment the page count
            pv++;
            // save the incremented page count back to localStorage
            localStorage.setItem('ddmAdPageCount', pv);
        } catch(e) {
            debug.log('Mntl.GPT: error loading pv from localStorage', e);

        }

        return pv;
    }

    
    function _configureRefresh() {
        const refreshSelectors = `
            [data-timed-refresh],
            [data-refresh-after-slot-rendered],
            [data-timeout-refresh-once-only]
        `;

        const refreshAds = document.querySelectorAll(refreshSelectors);

        [...refreshAds].forEach((refreshAd) => {
            // Mntl.RefreshableAds will only exist if there is a refreshable ad on the page
            if (Mntl.RefreshableAds) {
                const {
                    timedRefresh, 
                    refreshAfterSlotRendered, 
                    timeoutRefreshOnceOnly
                } = refreshAd.dataset;


                if (timedRefresh || refreshAfterSlotRendered) {
                    const refreshableAdManager = Mntl.RefreshableAds.getRefreshableAdManager();
                    let refreshSettings = {};

                    if (_config.useLmdFormat) {
                        refreshableAdManager.exposeKarmaAPI();
                    }
            
                    if (timedRefresh) {            
                        refreshSettings.timedRefresh = {
                            ms: timedRefresh,
                            once: Boolean(timeoutRefreshOnceOnly)
                        }
                    }
            
                    if (refreshAfterSlotRendered) {
                        refreshSettings.afterAdSlotRenders = { elementId: refreshAfterSlotRendered }
                    }
            
                    refreshableAdManager.addRefreshableAd(refreshAd, refreshSettings);
                }
            }
        })
    }

    function init(config) {
        const slots = [];
        let i;
        let len;

        _config = Mntl.fnUtilities.deepExtend(_config, config);

        // Set default single request mode if not set already
        if (typeof _config.singleRequest === "undefined") {
            _config.singleRequest = false;
        }

        window.debug.log('Mntl.GPT initialized with config', _config);

        if (Mntl.CMP) {
            if (Mntl.CMP.isOptInConsent()) {
                // only add the displayOnConsent to list of events that must be satisfied if a user is in a opt-in region (EU/gdpr)
                displayOnOptions.push('displayOnConsent');
            } else {
                // if we are not in an opt in geolocation we can set personalization immediately
                _setPersonalization();
            }
            // Check for consent change
            Mntl.CMP.onConsentChange(_setPersonalization);
        }

        if (_config.useLmdFormat) {
            _config.pageTargeting.pv = _getPageViewCount();
        }

        _config.pageTargeting.category = _buildCategory();

        Object.assign(_config.pageTargeting, getDebugPageTargeting());

        // Add a default floor if it is missing
        if (typeof _config.auctionFloors.other === 'undefined') {
            _config.auctionFloors.other = fallbackFloor;
        }

        // Set muid targeting if available
        if (_config.publisherProvidedId) {
            _config.pageTargeting.muid = _config.publisherProvidedId;
        }

        // Map 'type' to lmd value
        _config.baseSlotTargeting.type = _mapPageType(_config);

        // Remove deny-listed targeting keys
        _cleanTargeting(_config.pageTargeting);

        updatePageTargeting(_config.pageTargeting);

        // Define any slots initially present on the page
        for (i = 0, len = _config.initialSlots.length; i < len; i++) {
            slots.push(new Slot().byConfig(_config.initialSlots[i].config, _config.initialSlots[i].targeting, _config.initialSlots[i].gptSlot));
        }

        googletag.cmd.push(function() {
            googletag.pubads().collapseEmptyDivs(false);

            window.debug.log('Adding impressionViewable listener');
            googletag.pubads().addEventListener('impressionViewable', _onImpressionViewable);

            window.debug.log('Adding slotRequested listener');
            googletag.pubads().addEventListener('slotRequested', _onSlotRequested);

            window.debug.log('Adding slotRenderEnded listener');
            googletag.pubads().addEventListener('slotRenderEnded', _onSlotRenderEnded);

            googletag.pubads().addEventListener('slotOnload', _onSlotOnload);
            googletag.pubads().addEventListener('slotResponseReceived', _onSlotResponseReceived);

            window.debug.log('Enabling async rending');
            googletag.pubads().enableAsyncRendering();

            // Disable initial load, we will use refresh() to fetch ads
            // display() will just register the slot as ready, but will not fetch ads for it
            window.debug.log('Disabling initial load');
            googletag.pubads().disableInitialLoad();

            // Turn on single request mode if configured
            if (_config.singleRequest) {
                window.debug.log('Enabling single request mode');
                googletag.pubads().enableSingleRequest();
            }

            window.debug.log('Enabling services');
            googletag.enableServices();

            if (_config.publisherProvidedId) {
                googletag.pubads().setPublisherProvidedId(_config.publisherProvidedId);
            }
        });

        window.addEventListener('readyForThirdPartyTracking', () => {
            thirdPartyReady = true;
            if (thirdPartyReadySlotQueue.length) {
                window.debug.log('Rendering slots after readyForThirdPartyTracking event fired', thirdPartyReadySlotQueue);
                googletag.cmd.push(() => googletag.pubads().refresh(thirdPartyReadySlotQueue, { changeCorrelator: false }));
                thirdPartyReadySlotQueue = [];
            }
        }, { once: true });

        displaySlots(slots);

        Mntl.utilities.ready(_configureRefresh);
    }

    function getFloorIdByAdUnitType(type) {
        /* 
            Follows the same pattern of what we do with our other ad units that are generated on 
            the front end (like with load and destroy) and don't have a floor id (since floors come from the server side)
            of getting the last floor id of that ad unit type and copying it to the ad units created on the front end
        */
        const adUnitByWrapperType = document.querySelectorAll(`[id^=${type}]`);

        if(adUnitByWrapperType.length > 0) {
            const { auctionFloorId } = adUnitByWrapperType[adUnitByWrapperType.length-1].dataset;

            return auctionFloorId;
        }
    }

    function getFloorValueByAdUnitType(type) {
        /* 
            Follows the same pattern of what we do with our other ad units that are generated on 
            the front end (like with load and destroy) and don't have a floor value (since floors come from the server side)
            of getting the last floor value of that ad unit type and copying it to the ad units created on the front end
        */
        const adUnitByWrapperType = document.querySelectorAll(`[id^=${type}]`);

        if(adUnitByWrapperType.length > 0) {
            const { auctionFloorValue } = adUnitByWrapperType[adUnitByWrapperType.length-1].dataset;

            return auctionFloorValue;
        }
    }

    return {
        init,
        getGptUrl,
        serializeAllTargeting,
        serializePageTargeting,
        serializeBaseSlotTargeting,
        sortSlotsByPriority,
        isSingleRequest,
        isMobile,
        getSlotById,
        updateBaseSlotTargeting,
        updatePageTargeting,
        getPageTargeting,
        registerCallback,
        removeUnallowedCharacters,
        Slot,
        getTestIds,
        displaySlots,
        clearSlots,
        destroySlots,
        addRequestSizeToSlot,
        removeRequestSizeFromSlot,
        updatePageview,
        getGptSlotFromSlot,
        getGptSlotsFromSlots,
        getPublisherProvidedId,
        getPageType,
        getSitePath,
        getUtils,
        getDebugPageTargeting,
        destroySlotById,
        getDfpId,
        getFloorIdByAdUnitType,
        getFloorValueByAdUnitType,
        getUseLmdFormat,
        getUseOxygen,
        getUseInfiniteRightRail,
        getVideoTargeting,
        getBundlePrebid
    };
})();
