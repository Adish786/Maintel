window.Mntl = window.Mntl || {};

Mntl.LazyAds = (function($, fnUtils) {
    const $body = $('body');
    let dynamicCounter = 0;
    let $lazyAds = [];
    let $nextAd = [];
    let nextAdTop;
    let isDynamic;
    let slotEl;
    let slotId;
    let slot;
    let loadAdOnScroll;
    let attachedListener = false;
    const $chop = $('.mntl-chop');

    function _adSort(a, b) {
        a.adTop = a.adTop || $(a).offset().top;
        b.adTop = b.adTop || $(b).offset().top;
        if (a.adTop === b.adTop) {
            return 0;
        }

        return a.adTop < b.adTop ? -1 : 1;
    }

    /**
     * Gets the next ad from the $lazyAds array and sets it to be the $nextAd to be loaded, or
     * sets $nextAd to an empty array if there are no more ads in the $lazyAds queue.
     */
    function _setNextAd() {
        if ($lazyAds.length) {
            $nextAd = $($lazyAds.splice(0, 1)); // get the next lazy ad and remove it from the jquery set
            $nextAd.slotOffset = parseInt($nextAd.data('offset'), 10);
            nextAdTop = $nextAd.offset().top; // save its top position

            if (!attachedListener) {
                window.addEventListener('mntl.scroll', loadAdOnScroll);
                attachedListener = true;
            }
        } else {
            $nextAd = [];
            window.removeEventListener('mntl.scroll', loadAdOnScroll);
            attachedListener = false;
        }
    }

    /**
     * Gets the next ad slot element to be loaded, or null if there are no ad slots remaining.
     *
     * @returns {HTMLElement}   nextAdSlot || null
     */
    function _getNextAdSlot() {
        let nextAdSlot = null;

        if ($nextAd.length > 0 && !($nextAd.hasClass('is-requested'))) {

            if(!$nextAd.hasClass('infinite-lazy-ad-skipped')) {
                const slotTargeting = $nextAd.data('targeting') || {};

                // check if this slot has a dynamic ID
                isDynamic = $nextAd.data('is-dynamic') || false;
    
                [slotEl] = $nextAd.find('.wrapper');
                slotId = isDynamic ? slotEl.id + dynamicCounter : null; // if we have a dynamic slot, increment the ID (since IDs must be unique)
    
                if (isDynamic) {
                    slotTargeting.dord = ++dynamicCounter;
                }
    
                nextAdSlot = new Mntl.GPT.Slot().byElement(slotEl, slotId, slotTargeting);
    
                $nextAd.addClass('is-requested');
            }

            _setNextAd();
        }

        return nextAdSlot;
    }

    function _onScrollEnd () {
        let lastChangedFrame = 0;
        let lastX = window.scrollX;
        let lastY = window.scrollY;
    
        return new Promise((resolve) => {
            function tick(frames) {
                if (frames >= 500 || frames - lastChangedFrame > 15) {
                    resolve()
                } else {
                    if (window.scrollX != lastX || window.scrollY != lastY) {
                        lastChangedFrame = frames;
                        lastX = window.scrollX;
                        lastY = window.scrollY;
                    }
                    requestAnimationFrame(tick.bind(null, frames + 1));
                }
            }
            tick(0);
        });
    }

    function _handleNextAdRender (e) {
        if ($nextAd.length > 0 && !($nextAd.hasClass('is-requested'))) {
            let offset = $('html').data('lazy-offset') || 200;
            let percentageInView = '';
            let minOffsetFromViewport = window.innerHeight + offset;

            if ($nextAd[0].dataset.lazyPercentageView) {
                offset = 0;
                percentageInView = Number($nextAd[0].dataset.lazyPercentageView);
                minOffsetFromViewport = window.innerHeight - (percentageInView * $nextAd[0].offsetHeight)
            }

            // Load the next ad once it's visible (or within an offset), or if we've already scrolled past it, and if it hasn't already been requested
            const isNextAdVisible = Mntl.utilities.isElementVisibleY($nextAd, percentageInView, offset);
            const isWindowScrolledPastNextAdTopThreshold = (e.detail.scrollTop + minOffsetFromViewport) >= nextAdTop;

            const loadNextBillboardGroupAd = (isNextAdVisible || isWindowScrolledPastNextAdTopThreshold);
            
            /* 
                if we scrolled past the next ad to lazy load and its an infinite ad, we want to skip rendering the ad
                and we also want the slotRequest event to fire and we want to move on to the next lazy loaded ad by calling _getNextAdSlot
            */
            if($nextAd.hasClass('infinite-ad-unit-container') && isWindowScrolledPastNextAdTopThreshold && !isNextAdVisible) {

                window.debug.log(`Mntl.LazyAd: Skipped Infinite Right Rail ad thats already been placed on the page`);

                const fakeSlotRequestedEvent = new CustomEvent('slotRequested', {detail: { skipDestroy: true }});

                $nextAd.addClass('infinite-lazy-ad-skipped');

                const wrapper = $nextAd[0].querySelector('.wrapper');

                wrapper.removeAttribute('id');

                wrapper.dispatchEvent(fakeSlotRequestedEvent);

                slot = _getNextAdSlot();
            } else if (loadNextBillboardGroupAd) {
                slot = _getNextAdSlot();
                Mntl.GPT.displaySlots([slot]);
            }
        }
    }

    /**
     * If there is a next ad to load, and it is either in the visible range, or if it has been scrolled past
     * and not yet loaded, the ad element is displayed
     * @param {Event} e
     */
    loadAdOnScroll = function(e) {
        if(Mntl.GPT.getUseInfiniteRightRail() && $nextAd.hasClass('infinite-ad-unit-container')) {
            _onScrollEnd().then(() => {
                _handleNextAdRender(e);
            });
        } else {
            _handleNextAdRender(e);
        }
    };


    /**
    * Loads x number of next ad slots at once
    *
    * @param  {Integer}    adCountToLoad How many ad slots to load
    * @return {Undefined}
    */
    function _loadNextAdsByCount(adCountToLoad) {
        const slots = [];

        for (let i = 0; i < adCountToLoad; i++) {
            if ($nextAd.length > 0 && !($nextAd.hasClass('is-requested'))) {
                slots.push(_getNextAdSlot());
            }
        }

        if (slots.length) {
            Mntl.GPT.displaySlots(slots);
        }
    }

    /**
     * setLazyAds
     * sets the main array of jquery lazy ad objects within the targeted scope,
     * ads are sorted in order of top/offset of the ad @see _adSort
     *
     * @param {object} targeted scope
     * @return void
     */
    function setLazyAds($scope) {
        const $scopedLazyAds = $scope.find('.js-lazy-ad').not('.is-requested')
            .not('.infinite-lazy-ad-skipped');
        const chopBottom = $chop.length > 0 ? $chop.offset().top + $chop.height() : 0;
        const bodyTop = document.body.getBoundingClientRect().top;

        if ($scopedLazyAds.length) {
            $lazyAds = $.uniqueSort($.makeArray($.merge($.merge($lazyAds, $scopedLazyAds), $nextAd))).filter(function filterVisibleAds(el) {
                // remove ads under a chop
                const notUnderChop = el.closest('.is-chopped') === null || chopBottom > $(el).offset().top;
                // remove ads that are sitting in a negative position (sc-ads that are lazy but haven't been placed into the content of the page)
                const placedInContent = el.getBoundingClientRect().top >= bodyTop;

                return notUnderChop && placedInContent;
            })
                .sort(_adSort);

            $lazyAds.forEach(function deleteCachedTopPosition(ad) {
                // get rid of any cached top positions on these ads, since the layout may change before next execution
                delete ad.adTop;
            });
        }
    }

    function sortAdsByPostion(adsList) {
        const bodyTop = document.body.getBoundingClientRect().top;
        const chop = document.querySelector('.mntl-chop');
        const chopBottom = chop ? chop.getBoundingClientRect().bottom : 0;

        return adsList.filter((ad) => {
            const adTop = ad.getBoundingClientRect().top;
            // keep ads that are not in a chop or above the mntl-chop
            const notUnderChop = ad.closest('.is-chopped') === null || chopBottom > adTop;
            // keep ads that are placed in content (i.e. below the body top)
            // ads that are in a negative position are sc-ads that are lazy but
            // haven't been placed into the content of the page
            const placedInContent = adTop >= bodyTop;

            return notUnderChop && placedInContent;
        }).sort(_adSort);
    }

    function addLazyAd(ad) {
        $lazyAds.push(ad);
        $lazyAds = sortAdsByPostion($lazyAds);
        _setNextAd();
    }

    function removeLazyAd(adId) {
        $lazyAds = $lazyAds.filter((ad) => adId !== ad.id);
        $lazyAds = sortAdsByPostion($lazyAds);
        _setNextAd();
    }

    /**
     * Finds all lazy ads in the body, sets the next ad and the event listener to load ads on scroll.
     * This function is called multiple times, including from sc-ads.js when it has moved the sc ads into the
     * body of the page.
     */
    function init() {
        setLazyAds($body);
        _setNextAd();
    }

    // We always want to recalculate the ad list and nextAd settings as of GLBE-6929 mntl-chop content will have ads and
    // the chop unfurled will update this list
    document.addEventListener('mntl.contentResize', init);

    /**
     * Listens for 'mntl.scads.get' events on document body to load next x number of ads.
     * This is used for when a chop button is clicked and the chop area is opened.
     *   To trigger:
     *     const e = new CustomEvent('mntl.scads.get');
     *     e.data = { getScads: 2 };
     *     document.body.dispatchEvent(e);
     */
    document.body.addEventListener('mntl.scads.get', (e) => {
        const adCountToLoad = Number(fnUtils.getDeepValue(e, 'data', 'getScads')) || 0;

        // chop has opened, so reinitialize the lazy load ads
        init();
        _loadNextAdsByCount(adCountToLoad);
    });

    return {
        init,
        addLazyAd,
        removeLazyAd
    };
})(window.jQuery || {}, window.Mntl.fnUtilities || {});

Mntl.utilities.readyAndDeferred(Mntl.LazyAds.init);
