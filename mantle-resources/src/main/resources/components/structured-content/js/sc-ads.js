window.Mntl = window.Mntl || {};

Mntl.SCAds = (function(utils, domUtils, fnUtils, gpt, Maybe) {
    const CLASS_STICKY = 'scads-stick-in-parent';
    const CLASS_PLACED = 'scads-ad-placed';

    /**
     * Determines whether there is a position available for the current ad location
     * Functions differently based on index based positioning vs pixel based positioning
     * @param   {Number} location
     * @param   {Number} position
     * @return  {Boolean}
     */
    let _adSlotHasAvailablePosition = (function() {
        const indexElements = document.querySelectorAll('[data-item-index]');
        const adPositionType = indexElements.length > 0 ? 'index' : 'pixel';

        /**
         * For pixel based positioning, the second and subsequent ads will be inserted
         * up to 50px above the requested position
         * @param   {Number} location topOffset of location
         * @param   {Number} position desired topOffset of Ad
         * @returns {Boolean}
         */
        function secondComparison(location, position) {
            return location >= (position - 50);
        }

        /**
         * For pixel based positioning, the first pass will be 0 and an ad can only be placed
         * in a slots at or below the requested position
         * @param   {Number} location topOffset of location
         * @param   {Number} position desired topOffset of Ad
         * @returns {Boolean}
         */
        function firstComparison(location, position) {
            if (location >= position) {
                _adSlotHasAvailablePosition = secondComparison;

                return true;
            }

            return false;
        }

        /**
         * For index based positioning, does Ad location match Index position
         * @param {Number}  location Index of Ad location
         * @param {Number}  position desired index location of Ad
         * @returns {Boolean}
         */
        function indexComparison(location, position) {
            return (location === position);
        }

        return adPositionType === 'pixel' ? firstComparison : indexComparison;
    }());

    /**
     * Set the vertical position of ads in the notional right rail. top &
     * remainingRailHeight are tracked separately because top is an offset from
     * the closest parent set to position: relative and remainingRailHeight
     * is a calculation of available space.
     * @param {Number} min            minimum ad height
     * @param {Number} max            maximum ad height
     * @param {Number} padding        space between ads
     * @param {Number} adLabelHeight
     * @return {Object} An interface to set ad position in rail
     */
    function _makeRightRailInterface(min, max, padding, adLabelHeight, adBottomOffset) {
        const maybeRightRail = Maybe.of(document.getElementsByClassName('right-rail')[0]);
        const minPlusPad = min + padding;
        let railHeight = null;
        let top = null;

        // Save the offset element. Return a function that gets the correct topPosition.
        const _getStartTop = (function() {
            const element = Maybe.of(document.getElementsByClassName('right-rail__offset')[0]).orElse({offsetTop: 0});

            return () => element.offsetTop - adLabelHeight;
        }());

        // Return the total height of the article
        function _getRightRailHeight() {
            return maybeRightRail.flatMap(domUtils.outerHeight) - adBottomOffset;
        }

        /**
         * Remove all ad sizes larger than the available space from the  wrapper's
         * data-size attribute
         * @param  {Element}  slot  Ad slot container
         * @param  {Number}  space  Remaining space available in right rail
         */
        function _removeSizes(slot, space) {
            const wrapper = slot.querySelector('.wrapper[data-sizes]');
            const changeSizes = fnUtils.pipe(JSON.parse,
                (a) => a.filter(
                    (elem) => elem[1] < space || isNaN(elem[1])
                ),
                JSON.stringify);

            wrapper.dataset.sizes = changeSizes(wrapper.dataset.sizes);
        }

        return {
            reset: () => { // called on resize event
                top = _getStartTop();
                railHeight = _getRightRailHeight();
            },
            set(el) {
                const maxH = Maybe.of(domUtils.getData('height', el)).orElse(max);
                const maxHPadding = maxH + padding;

                if (top === null || railHeight === null) {
                    top = _getStartTop();
                    railHeight = _getRightRailHeight();
                }

                const remainingRailHeight = railHeight - top;

                if (remainingRailHeight < minPlusPad) {
                    el.classList.add('is-hidden'); // hide visible components that doesn't fit the rail.

                    return false;
                } else if (remainingRailHeight < maxHPadding) {
                    _removeSizes(el, remainingRailHeight);
                    el.style.height = 'auto';
                } else {
                    el.classList.add(CLASS_STICKY);
                }

                el.style.top = [top, 'px'].join('');
                top += maxHPadding; // set next top position (i.e. billboard 3 onwards)

                return true;
            }
        };
    }

    /**
     * Get All elements .mntl-sc-block-adslot from the provided element
     * @param       {Element} el
     * @return      {Mixed}   el  null || HTMLCollection
     */
    function _getAdSlotBlockElements(el) {
        const adSlotBlockElements = el.getElementsByClassName('mntl-sc-block-adslot');

        return adSlotBlockElements.length ? adSlotBlockElements : null;
    }

    /**
     * Get ALL child elements of .js-scads-inline-content
     * @param       {Element} el
     * @return      {Mixed}       null || HTMLCollectionN
     */
    function _getInlineContent(el) {
        return Maybe.of(el.getElementsByClassName('js-scads-inline-content')[0])
            .map((element) => element.children)
            .flatMap((els) => (els.length ? els : null));
    }

    /**
     * Get all ad units in the supplied Element
     * @param       {Element} el
     * @return      {Mixed} Element || null
     */
    function _getAdElements(el) {
        const ads = el.getElementsByClassName('scads-to-load');

        return ads.length ? ads : null;
    }

    /**
     * Move selected ad content from `.js-scads-inline-content` into `mntl-sc-block-adslot`s
     * It is rare, but an ad slot block can have more than 1 ad
     * @param {Object[]}  slots Objects holding the targeted adSlotBlockEl and content of an ad
     */
    function _insertSlots(slots) {
        slots.forEach((slot) => {
            if (slot.adSlotBlockEl) {
                slot.adSlotBlockEl.insertAdjacentElement('afterbegin', slot.content);

                slot.ads.flatMap((cads) => {
                    Array.prototype.forEach.call(cads, (ad) => {
                        const [gptAdWrapper] = ad.querySelectorAll('.mntl-gpt-adunit .wrapper[data-sizes]');

                        if (gptAdWrapper) {
                            if (slot.lazyLoad) {
                                gptAdWrapper.parentElement.classList.add('js-lazy-ad');
                            } else {
                                gptAdWrapper.parentElement.classList.add('js-immediate-ad');
                            }
                        }

                        ad.classList.add(CLASS_PLACED);
                    });
                });
            }
        });
    }

    /**
     * Place content in slots and optionally populate the right rail
     * Ads/Content (`.js-scads-inline-content.children`) are placed in actual positions
     * (`.mntl-sc-block-adslot`) when the `offsetTop` of the position meets or
     * exceeds the ideal position (`data-sc-ad-positions`). If there is a right
     * rail matching will end early when there is no more room.
     * @see _adSlotHasAvailablePosition() for more information on matching
     * @param       {Array}  positions  desired pixel positions
     * @param       {Array}  adSlotBlockElements  Available mntl-sc-block-adslot DOM elements
     * @param       {HTMLCollection}  content  elements to place
     * @param       {Boolean} hasRail  true if a right rail is present
     * @param       {Boolean} optimizeSetup  true if ads should be inserted all at once
     * @return      {Boolean}
     */
    function _setup(positions, adSlotBlockElements, content, hasRail, rightRail, optimizeSetup, loadImmediateCount) {
        let slotOffset = 0;
        let contentIndex = 0;
        const matchedSlots = [];
        let currentAdSlotBlockEl;
        let currentContent;
        let currentAds;
        let adUnitStyles;
        let currentAdSlotCount = 0;

        // Return an error if we can't find the required setup items
        if ([positions, adSlotBlockElements, content].indexOf(null) > -1) {
            return window.debug.error('Failed attempt to use Mntl.SCAds.');
        }

        while (adSlotBlockElements.length && positions.length && content.length && contentIndex < content.length) {
            currentAdSlotBlockEl = adSlotBlockElements.shift();

            const location = currentAdSlotBlockEl.dataset.itemIndex ?
                parseInt(currentAdSlotBlockEl.dataset.itemIndex, 10) :
                currentAdSlotBlockEl.offsetTop + slotOffset;

            if (_adSlotHasAvailablePosition(location, positions[0])) {
                const lazyLoad = ++currentAdSlotCount > loadImmediateCount;

                currentContent = content[contentIndex];
                currentAds = Maybe.of(_getAdElements(currentContent));

                if (hasRail) {
                    currentAds = currentAds.map((cads) =>
                        Array.prototype.filter.call(cads, (el) => {
                            if (!el.classList.contains('right-rail__item')) {
                                return true; // not for the right rail so keep this item
                            }

                            return rightRail.set(el); // keep this item only if it can be placed in the rail
                        })
                    );
                }

                matchedSlots.push({
                    adSlotBlockEl: currentAdSlotBlockEl,
                    content: currentContent,
                    ads: currentAds,
                    lazyLoad
                });

                if (optimizeSetup) { // Estimate how many pixels the content will be pushed down by, instead of placing the ad
                    adUnitStyles = getComputedStyle(currentContent.querySelector('.mntl-gpt-adunit') || currentContent);
                    slotOffset += currentContent.offsetHeight + parseInt(adUnitStyles.marginTop, 10) + parseInt(adUnitStyles.marginBottom, 10);
                    contentIndex++;
                } else {
                    _insertSlots(matchedSlots);
                    matchedSlots.shift();
                }

                positions.shift();
            }
        }

        if (optimizeSetup) { // Place ads all at once to avoid style recalculations
            _insertSlots(matchedSlots);
        }

        return true;
    }

    /**
     * Recalculates the items in the right rail
     * @param  {Object} rightRail Right rail interface instance
     */
    function _recalculateRightRail(rightRail) {
        const placedAds = document.getElementsByClassName(CLASS_PLACED);

        rightRail.reset();
        Array.prototype.forEach.call(placedAds, (el) => {
            if (el.classList.contains('right-rail__item')) {
                rightRail.set(el);
            }
        });
    }

    /**
     * Return a resize handler to deal with right rail
     * @param  {Object} rightRail Right rail interface instance
     * @return {Function}
     */
    function _handleResize(rightRail, hasRail) {
        let hadRail = hasRail();
        const placedAds = document.getElementsByClassName(CLASS_PLACED);
        const railLockEvent = new CustomEvent('mntl.rightrail.lock');

        return function handleResize() {
            if (hadRail && !hasRail()) { // big -> small
                hadRail = false;
                Array.prototype.forEach.call(placedAds, (el) => {
                    el.style.top = 'auto';
                    el.classList.remove('is-hidden');
                });
            } else if (!hadRail && hasRail()) { // small -> big
                hadRail = true;
                _recalculateRightRail(rightRail);
                document.body.dispatchEvent(railLockEvent);
            }
        };
    }

    /**
     * Locates all ad slots that have been marked with the .js-immediate-ad class and
     * class gpt.displaySlots to load them
     */
    function _loadImmediateAds() {
        const adsToLoad = Array.from(document.querySelectorAll('.js-immediate-ad .wrapper'));
        const slots = [];

        adsToLoad.forEach((ad) => {
            slots.push(new gpt.Slot().byElement(ad));
        });
        gpt.displaySlots(slots);
    }

    // Add Event Listeners
    utils.onLoad(() => {
        const maybeMntlScPage = Maybe.of(document.querySelectorAll('[data-sc-content-positions]')[0]);

        const maybeMntlArticlePage = Maybe.of(document.querySelector('.mntl-article'));

        const curriedGetData = fnUtils.curry(domUtils.getData);
        const adLocationsContainer = Maybe.of(document.getElementsByClassName('sc-ad-container')[0] || document.getElementsByClassName('mntl-sc-page')[0]);
        const rightRailInterface = _makeRightRailInterface(
            maybeMntlScPage.map(curriedGetData('scMinTrackHeight')).orElse(250),
            maybeMntlScPage.map(curriedGetData('scMaxTrackHeight')).orElse(0),
            maybeMntlScPage.map(curriedGetData('scAdTrackSpacing')).orElse(100),
            maybeMntlScPage.map(curriedGetData('scAdLabelHeight')).orElse(0),
            maybeMntlScPage.map(curriedGetData('scAdBottomOffset')).orElse(0)
        );

        const mntlArticleRightRailLocationIsPresent = document.querySelector('.article-right-rail') && document.querySelector('.article-right-rail').offsetHeight > 0;

        if (!mntlArticleRightRailLocationIsPresent) {
            // We do not have the sc-ads generated rail if we're using the CSS Grid layout
            const hasRail = (function(breakpoint) {
                return () => window.matchMedia(['(min-width: ', breakpoint, ')'].join('')).matches;
            }(maybeMntlScPage.flatMap(curriedGetData('scBreakpoint'))));

            // Place content in slots and populate the right rail
            _setup(
                maybeMntlScPage.flatMap(curriedGetData('scContentPositions')),
                adLocationsContainer.map(_getAdSlotBlockElements).flatMap(fnUtils.toArray),
                maybeMntlArticlePage.isNothing() ? maybeMntlScPage.flatMap(_getInlineContent) : maybeMntlArticlePage.flatMap(_getInlineContent),
                hasRail(),
                rightRailInterface,
                maybeMntlScPage.flatMap(curriedGetData('scOptimizeSetup')),
                maybeMntlScPage.map(curriedGetData('scLoadImmediate')).orElse(0)
            );

            _loadImmediateAds();
            Mntl.LazyAds.init();

            // Recalculate Right Rail Handlers for resize or embed reflow
            window.addEventListener('resize', Mntl.throttle(_handleResize(rightRailInterface, hasRail), 50));
            window.addEventListener('embedReflow', () => {
                if (hasRail()) {
                    _recalculateRightRail(rightRailInterface);
                }
            });
        }
    }); // END utils.onLoad
})(window.Mntl.utilities || {}, window.Mntl.domUtilities || {}, window.Mntl.fnUtilities || {}, window.Mntl.GPT || {}, window.Mntl.Maybe || {});