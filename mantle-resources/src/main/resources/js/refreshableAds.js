window.Mntl = window.Mntl || {};
Mntl.RefreshableAds = (function() {
    let _refreshableAdManager;

    const adsAlwaysInView = new Set(['mob-adhesive-banner-fixed', 'adhesive', 'leaderboard-fixed-0']);

    class RefreshableAd {
        constructor(el, refreshSettings) {
            this._el = el;
            this._rord = 0;
            this._inFlight = false;
            
            if (refreshSettings.timedRefresh) {
                this._refreshType = 'timed';
            } else if (refreshSettings.afterAdSlotRenders) {
                this._refreshType = 'afterAdSlotRenders';
            } else {
                this._refreshType = null;
            }

            // a RefreshableAd can only have one type of refresh - timed or afterAdSlotRenders
            // if both are set, the ad will be a timed refresh
            switch (this._refreshType) {
                case 'timed':
                    this._timedRefresh = {
                        ...refreshSettings.timedRefresh,
                        refreshTimeoutRunning: false,
                        refreshTimeoutEnabled: true,
                        timer: parseInt(refreshSettings.timedRefresh.ms)/1000
                    }
                    this._el.addEventListener('adRendered', this._afterAdRenderedHandler.bind(this), 
                        { once: this._timedRefresh.once });

                    break;
                case 'afterAdSlotRenders':
                    this._afterAdSlotRenders = { ...refreshSettings.afterAdSlotRenders }
                    Mntl.GPT.registerCallback([this._afterAdSlotRenders.elementId], this._refreshMe.bind(this));
                    break;
            }
        }

        _afterAdRenderedHandler() {
            this._completeInFlight();
            this.startTimeout();
        }

        _isRefreshReady() {
            // ad must not be inFlight, enabled if a timed refresh, and in view
            const isTimedRefreshEnabled = this._timedRefresh && this._timedRefresh.refreshTimeoutEnabled;
            const isRefreshReadyStatus = !this._inFlight
                && (this._refreshType === 'afterAdSlotRenders' || isTimedRefreshEnabled)
                && this.isInView();

            return isRefreshReadyStatus;
        }

        _isTimerReady() {
            // initial request must already be complete (slotObj.requested must be True)
            // timer must be enabled
            // timer should not already be running
            // request shouldn't be in flight
            const slotObj = Mntl.GPT.getSlotById(this._el.id);

            const isTimerReadyStatus = Boolean(slotObj
                && slotObj.requested
                && this._timedRefresh.refreshTimeoutEnabled
                && !this._timedRefresh.refreshTimeoutRunning
                && !this._inFlight);

            return isTimerReadyStatus;
        }

        _timerCountdown() {
            if (this._timedRefresh.timer > 0) {
                this._timedRefresh.timer--;
            } else {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd._timerCountdown [${this._el.id}] calling refresh`, this);
                // by setting the timer to -1, we can differentiate between a timer that has fully counted down and refreshed an ad
                // and the edge case when a user triggers the pause timer when it is at the 0 mark
                this._timedRefresh.timer--;
                this._refreshMe();
            }
        }

        _stopTimeout() {
            // _stopTimeout clears the timer value so when it restarts it will begin with the full timer amount
            window.debug.log(`Mntl.RefreshableAds.RefreshableAd._stopTimeout [${this._el.id}] stopping timeout`, this);
            clearInterval(this._timedRefresh.interval);
            this._timedRefresh.interval = false;
            this._timedRefresh.timer = -1;
            this._timedRefresh.refreshTimeoutRunning = false;
        }

        _removeTimedRefresh() {
            window.debug.log(`Mntl.RefreshableAds.RefreshableAd._removeTimedRefresh [${this._el.id}] removing timed refresh`, this);
            this._timedRefresh = null;
            this._refreshType = null;
        }

        _startInFlight() {
            window.debug.log(`Mntl.RefreshableAds.RefreshableAd._startInFlight [${this._el.id}] setting inFlight to true`, this);
            this._inFlight = true;
        }

        _completeInFlight() {
            window.debug.log(`Mntl.RefreshableAds.RefreshableAd._completeInFlight [${this._el.id}] setting inFlight to false`, this);
            this._inFlight = false;
        }

        _refreshMe() {
            if (this._refreshType === 'timed') {
                // we stop the timeout regardless of whether or not the ad is refreshReady
                // the timeout will start up again when a trigger such as the page getting back into
                // focus is triggered
                this._stopTimeout();
            }

            if (this._isRefreshReady()) {
                this._rord += 1;

                const slotObj = Mntl.GPT.getSlotById(this._el.id);

                slotObj.updateTargeting({ rord: this._rord });

                // only need to update the refresh:'timed' slot targeting value on the first timed refresh 
                // as it will be included on every following timed refresh as well.
                if (this._rord === 1 && this._refreshType === 'timed') {
                    slotObj.updateTargeting({ refresh: 'timed' });
                }

                this._startInFlight();
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd._refreshMe [${this._el.id}] refreshing ad`, this);
                Mntl.GPT.displaySlots([slotObj]);

                if (this._refreshType === 'timed' && this._timedRefresh.once) {
                    window.debug.log(`Mntl.RefreshableAds.RefreshableAd._refreshMe [${this._el.id}] timedRefresh.once is true, removing refresh`, this);
                    this._removeTimedRefresh();
                }
            }
        }

        // Public functions used outside of the RefreshableAd class
        isInView() {
            let inView = false;

            if (document.visibilityState === 'visible'){
                // adhesive and leaderboard-fixed-0 are always in view
                // adhesive for L-DD sites, and mob-adhesive-banner-fixed for L-MD sites
                if (adsAlwaysInView.has(this._el.id)) {
                    inView = true;
                } else {
                    // for other ad types - we need to check:
                    // - Is ad in the viewport?
                    // - Is the ad covered by something else?
                }
            }

            return inView;
        }

        isRefreshTimeoutEnabled() {
            return (this._refreshType === 'timed' && this._timedRefresh.refreshTimeoutEnabled);
        }

        pauseTimeout() {
            if (this._refreshType === 'timed') {
                // pauseTimeout keeps the timer value so it can be restarted
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.pauseTimeout [${this._el.id}] pausing timeout: ${this._timedRefresh.timer} seconds`, this);
                clearInterval(this._timedRefresh.interval);
                this._timedRefresh.interval = false;
                this._timedRefresh.refreshTimeoutRunning = false;
            }
        }

        startTimeout() {
            if (this._refreshType === 'timed') {
                if (this._isTimerReady()) {
                    this._timedRefresh.refreshTimeoutRunning = true;
                    // if timer still has a count, restart from that count otherwise reset to full timer amount
                    // if the previous timer had completed its run and refreshed the ad, the timer would be at -1
                    if (this._timedRefresh.timer >= 0) {
                        window.debug.log(`Mntl.RefreshableAds.RefreshableAd.startTimeout [${this._el.id}] restarting refresh timer: ${this._timedRefresh.timer} seconds`, this);
                    } else {
                        this._timedRefresh.timer = parseInt(this._timedRefresh.ms)/1000
                        window.debug.log(`Mntl.RefreshableAds.RefreshableAd.startTimeout [${this._el.id}] starting refresh timer: ${this._timedRefresh.timer} seconds`, this);
                    }
                    this._timedRefresh.interval = setInterval(this._timerCountdown.bind(this), 1000);
                }
            } else {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.startTimeout [${this._el.id}] could not start - not a timedRefresh ad`, this);
            }
        }

        disableTimedRefresh() {
            if (this._refreshType === 'timed') {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.disableTimedRefresh [${this._el.id}] disabling timed refresh`, this);
                this.pauseTimeout();
                this._timedRefresh.refreshTimeoutEnabled = false;
            } else {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.disableTimedRefresh [${this._el.id}] could not disable - not a timedRefresh ad`, this);
            }
        }

        disableAfterAdSlotRendersRefresh() {
            if (this._refreshType === 'afterAdSlotRenders') {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.disableAfterAdSlotRendersRefresh [${this._el.id}] disabling afterAdSlotRenders refresh`, this);
                this._refreshType += '-disabled';
            } else {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.disableAfterAdSlotRendersRefresh [${this._el.id}] could not disable - not an afterAdSlotRenders ad`, this);
            }
        }

        reEnableTimedRefresh() {
            if (this._refreshType === 'timed') {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.reEnableTimedRefresh [${this._el.id}] enabling timed refresh`, this);
                this._timedRefresh.refreshTimeoutEnabled = true;
                this.startTimeout();
            } else {
                window.debug.log(`Mntl.RefreshableAds.RefreshableAd.reEnableTimedRefresh [${this._el.id}] could not enable - not a timedRefresh ad`, this);
            }
        }
    }

    class RefreshableAdManager {
        constructor() {
            this._ads = {};
            this._listeners = ['scroll', 'focus', 'blur', 'click', 'windowFocusChange'];
            this._listenersActive = false;
            this._debouncedCheckTimedRefreshAds = null;
            this._browserWindowFocused = true;

            this._addXDomainListener();

            this._setUpDebouncedCheckTimedRefreshAds();
            this._setUpViewabilityDetection();
        }

        _getActiveTimedRefreshAds() {
            // check if ad slot is refresh timeout enabled
            const activeTimedRefreshAds = Object.keys(this._ads).filter((slotId) => this._ads[slotId].isRefreshTimeoutEnabled());

            return activeTimedRefreshAds;
        }

        _setUpDebouncedCheckTimedRefreshAds() {
            function debounce(fn, wait) {
                let timeout;

                return function debouncedFunction(...args) {
                    function later() {
                        clearTimeout(timeout);
                        fn(...args);
                    };

                    clearTimeout(timeout);
                    timeout = setTimeout(later, wait);
                };
            };

            this._debouncedCheckTimedRefreshAds = debounce(this.checkTimedRefreshAds.bind(this), 250);
        }

        _setUpViewabilityDetection() {
            const supportsViewabilityDetection = typeof document.body === "object" && typeof document.elementFromPoint === "function" && typeof addEventListener === "function";

            if (supportsViewabilityDetection) {
                window.addEventListener('blur', this._setBrowserWindowFocus.bind(this, false));
                window.addEventListener('focus', this._setBrowserWindowFocus.bind(this, true));
            } else {
                // If we can't detect visibility because the browser does not support the methods we use, we default to false which disables timed refresh.
                this._browserWindowFocused = false;
            }
        }

        _setBrowserWindowFocus(isInFocus) {
            const windowFocusChangeEvent = new Event('windowFocusChange');
            
            this._browserWindowFocused = isInFocus;
            window.dispatchEvent(windowFocusChangeEvent);
        }

        _addXDomainListener() {
            /* Add X-domain listener */
            window.addEventListener('message', (e) => {
                if (typeof e.data === 'string' && e.data.indexOf('karmaStopRefresh') !== -1) {
                    try {
                        // the slot id does not come with "div-gpt-" prepended, 
                        // so we do not need to remove like in the other karma api functions
                        const data = JSON.parse(e.data);
                        const slotId = data.slot;
                        const refreshableAdManager = Mntl.RefreshableAds.getRefreshableAdManager();
                        const refreshableAd = refreshableAdManager.getRefreshableAd(slotId);
                        
                        if (refreshableAd) {
                            refreshableAd.disableTimedRefresh();
                            refreshableAd.disableAfterAdSlotRendersRefresh();
                        }
                    } catch (err) {
                        window.debug.log(`Mntl.RefreshableAds.RefreshableAdManager X-domain listener err: ${err.toString()}`);
                    }
                }
            }, false);
        }

        // Public functions used outside of the RefreshableAd class
        addRefreshableAd(el, refreshSettings) {
            const refreshableAd = new RefreshableAd(el, refreshSettings);

            this._ads[el.id] = refreshableAd;
            this.checkTimedRefreshAds();
        }

        getRefreshableAd(slotId) {
            return this._ads[slotId];
        }

        checkTimedRefreshAds() {
            const timedRefreshSlotIds = this._getActiveTimedRefreshAds();

            timedRefreshSlotIds.forEach((slotId) => {
                const ad = this._ads[slotId];

                if (this._browserWindowFocused && ad.isInView()){
                    // startTimeout will only start the timeout if all conditions are met
                    ad.startTimeout();
                } else {
                    //  ad is not visible, pause the timer if it is running
                    ad.pauseTimeout();
                }
            });

            if (timedRefreshSlotIds.length > 0 && !this._listenersActive) {
                // Add listeners
                window.debug.log('Mntl.RefreshableAds.RefreshableAdManager.checkTimedRefreshAds adding event listeners', this);
                this._listenersActive = true;
                this._listeners.forEach((listener) => {
                    // useCapture: true. This way clicks are registered even if the target is removed (e.g. a close button on a modal)
                    window.addEventListener(listener, this._debouncedCheckTimedRefreshAds, {
                        passive: true,
                        capture: true
                    });
                });
            } else if (timedRefreshSlotIds.length <=0 && this._listenersActive) {
                // Remove listeners if there are no timed refresh ad slots
                window.debug.log('Mntl.RefreshableAds.RefreshableAdManager.checkTimedRefreshAds removing event listeners', this);
                this._listenersActive = false;
                this._listeners.forEach((listener) => {
                    window.removeEventListener(listener, this._debouncedCheckTimedRefreshAds, {
                        passive: true,
                        capture: true
                    });
                })
            }
        }

        exposeKarmaAPI() {
            function reEnableTimedRefreshOnSlot(karmaSlotId) {
                const slotId = karmaSlotId.replace('div-gpt-', '');
                const refreshableAdManager = Mntl.RefreshableAds.getRefreshableAdManager();
                const refreshableAd = refreshableAdManager.getRefreshableAd(slotId);

                if (refreshableAd) {
                    refreshableAd.reEnableTimedRefresh();
                    refreshableAdManager.checkTimedRefreshAds();
                } else {
                    window.debug.log(`Mntl.RefreshableAds.RefreshableAdManager karmaAPI reEnableTimedRefreshOnSlot ${slotId} does not exist.`);
                }
            }

            function disableTimedRefreshOnSlot(karmaSlotId) {
                const slotId = karmaSlotId.replace('div-gpt-', '');
                const refreshableAdManager = Mntl.RefreshableAds.getRefreshableAdManager();
                const refreshableAd = refreshableAdManager.getRefreshableAd(slotId);

                if (refreshableAd) {
                    refreshableAd.disableTimedRefresh();
                    refreshableAdManager.checkTimedRefreshAds();
                } else {
                    window.debug.log(`Mntl.RefreshableAds.RefreshableAdManager karmaAPI disableTimedRefreshOnSlot ${karmaSlotId} does not exist.`);
                }
            }

            window.karma = {
                reEnableTimedRefreshOnSlot,
                disableTimedRefreshOnSlot
            };
        }
    }

    function getRefreshableAdManager() {
        return _refreshableAdManager;
    }

    function init() {
        _refreshableAdManager = new RefreshableAdManager();
    }

    init();

    return {
        getRefreshableAdManager,
        RefreshableAd
    }
})();