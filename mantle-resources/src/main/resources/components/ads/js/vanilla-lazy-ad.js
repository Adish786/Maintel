// eslint-disable-next-line no-var
var Mntl = window.Mntl || {};

Mntl.LazyAds = (function(fnUtils) {
    // Common classes used throughout the lazy-ad file.
    const IS_REQUESTED = 'is-requested';
    let dynamicCounter = 0;
    let lazyAds = [];
    const nextAd = {
        element: null,
        top: null,
        offset: null,
        isDynamic: false
    };
    const slot = {
        adSlot: null,
        element: null,
        id: ''
    };
    let loadAdOnScroll; // Done to not trigger eslint no-use-before-define
    let attachedListener = false;

    /**
     * Helper function to sort ad elements based on offset tops.
     * The way we pass these sorting functions is into the array sort function as a callback i.e. const myArray = [ ... ]; myArray.sort(_adsort);
     *
     * @param {array element} a
     * @param {array element} b
     * @returns {int} 0 or 1 to determine how to sort things
     */
    function _adSort(a, b) {
        const aAdTop = a.getBoundingClientRect().top;
        const bAdTop = b.getBoundingClientRect().top;

        if (aAdTop === bAdTop) {
            return 0;
        }

        return aAdTop < bAdTop ? -1 : 1;
    }

    /**
     * Helper function to determine if the next ad element is not requested (does not contain the is-requested class/element not present)
     * @returns {boolean}
     */
    function _isNextAdNotRequested() {
        const isAdRequested = nextAd.element ? nextAd.element.classList.contains(IS_REQUESTED) : false;

        return !isAdRequested;
    }

    /**
     * Gets the next ad from the $lazyAds array and sets it to be the $nextAd to be loaded, or
     * sets $nextAd to an empty array if there are no more ads in the $lazyAds queue.
     */
    function _setNextAd() {
        if (lazyAds.length > 0) {
            [nextAd.element] = lazyAds.splice(0, 1); // get the next lazy ad and remove it from the set
            nextAd.offset = parseInt(nextAd.element.dataset.offset, 10); // save nextAdOffset
            nextAd.top = nextAd.element.getBoundingClientRect().top; // save its top position

            if (!attachedListener) {
                window.addEventListener('scroll', loadAdOnScroll);
                attachedListener = true;
            }
        } else {
            nextAd.element = null;
            nextAd.offset = null;
            nextAd.top = null;
            window.removeEventListener('scroll', loadAdOnScroll);
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

        if (_isNextAdNotRequested()) {

            if(!nextAd.element.querySelector('.infinite-lazy-ad-skipped')) {
                const slotTargeting = nextAd.element.dataset.targeting || {};

                // check if this slot has a dynamic ID
                nextAd.isDynamic = nextAd.element.dataset.isDynamic || false;
    
                slot.element = nextAd.element.querySelector('.wrapper');
                slot.id = nextAd.isDynamic ? `${slot.element.id}${dynamicCounter}` : null; // if we have a dynamic slot, increment the ID (since IDs must be unique)
    
                if (nextAd.isDynamic) {
                    slotTargeting.dord = ++dynamicCounter;
                }
    
                nextAdSlot = new Mntl.GPT.Slot().byElement(slot.element, slot.id, slotTargeting);
    
                nextAd.element.classList.add(IS_REQUESTED);
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

    function _handleNextAdRender() {
        if (_isNextAdNotRequested()) {
            let offset = document.querySelector('html').dataset.lazyOffset || 200;
            let percentageInView = '';
            let minOffsetFromViewport = window.innerHeight + offset;

            if (nextAd.element.dataset.lazyPercentageView) {
                offset = 0;
                percentageInView = Number(nextAd.element.dataset.lazyPercentageView);
                minOffsetFromViewport = window.innerHeight - (percentageInView * nextAd.element.offsetHeight);
            }

            const isNextAdVisible = Mntl.utilities.isElementVisibleY(nextAd.element, percentageInView, offset);
            // Determined by window scroll position + height + offset >= nextAd top value.
            const isWindowScrolledPastNextAdTopThreshold = (window.pageYOffset + minOffsetFromViewport) >= nextAd.top;
            // Load the next ad once it's visible (or within an offset), or if we've already scrolled past it, and if it hasn't already been requested
            const loadNextAd = (isNextAdVisible || isWindowScrolledPastNextAdTopThreshold);

            /* 
                if we scrolled past the next ad to lazy load and its an infinite ad, we want to skip rendering the ad
                and we also want the slotRequest event to fire and we want to move on to the next lazy loaded ad by calling _getNextAdSlot
            */
            if(nextAd.element.classList.contains('infinite-ad-unit-container') && isWindowScrolledPastNextAdTopThreshold && !isNextAdVisible) {
                window.debug.log(`Mntl.LazyAd: Skipped Infinite Right Rail ad thats already been placed on the page`);

                nextAd.element.classList.add('infinite-lazy-ad-skipped');
                slot.adSlot = _getNextAdSlot();
            } else if (loadNextAd) {
                slot.adSlot = _getNextAdSlot();
                Mntl.GPT.displaySlots([slot.adSlot]);
            }
        }
    }

    /**
     * If there is a next ad to load, and it is either in the visible range, or if it has been scrolled past
     * and not yet loaded, the ad element is displayed
     * @param {Event} e
     */
    loadAdOnScroll = function() {
        if(Mntl.GPT.getUseInfiniteRightRail() && nextAd.element.classList.contains('infinite-ad-unit-container')) {
            _onScrollEnd().then(_handleNextAdRender);
        } else {
            _handleNextAdRender();
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
            if (nextAd.ad && !(nextAd.element.classList.contains(IS_REQUESTED))) {
                slots.push(_getNextAdSlot());
            }
        }

        if (slots.length) {
            Mntl.GPT.displaySlots(slots);
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

    /**
     * getLazyAds
     * gets the main array of lazy ad objects within the targeted element,
     * ads are sorted in order of top/offset of the ad @see _adSort
     *
     * @param {object} targeted or parent element
     * @return {array} of sorted ad elements
     */
    function getLazyAds(element) {
        const scopedlazyAds = Array.from(element.querySelectorAll('.js-lazy-ad:not(.is-requested, .infinite-lazy-ad-skipped)'));
        let sortedAds = [];

        if (scopedlazyAds.length > 0) {
            // Merge lazy ads with scoped ads (and de dupe it) into a sortedAds variable
            sortedAds = lazyAds.concat(scopedlazyAds.filter((ad) => lazyAds.indexOf(ad) < 0));

            // Merge next ad into the sortedAds variable if its not already in it
            if (nextAd.element && scopedlazyAds.indexOf(nextAd.element) < 0) {
                sortedAds = sortedAds.concat(nextAd.element);
            }

            sortedAds = sortAdsByPostion(sortedAds);
        }

        return sortedAds;
    }

    /**
     * Finds all lazy ads in the body, sets the next ad and the event listener to load ads on scroll.
     * This function is called multiple times, including from sc-ads.js when it has moved the sc ads into the
     * body of the page.
     */
    function init() {
        lazyAds = getLazyAds(document.body);
        _setNextAd();
    }

    function addLazyAd(ad) {
        lazyAds.push(ad);
        lazyAds = sortAdsByPostion(lazyAds);
        _setNextAd();
    }

    function removeLazyAd(adId) {
        lazyAds = lazyAds.filter((ad) => adId !== ad.id);
        lazyAds = sortAdsByPostion(lazyAds);
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
})(window.Mntl.fnUtilities || {});

Mntl.utilities.readyAndDeferred(Mntl.LazyAds.init);
