Mntl.ScLockBillboards = (function(utils, throttle) {
    const mntlSCPage = document.querySelectorAll('[data-sc-content-positions]')[0];
    const stickyOffset = mntlSCPage ? (parseInt(mntlSCPage.dataset.scStickyOffset, 10) || 0) : 0;
    const billboardAds = {};

    /**
     * Internal method to determine if a right rail exists using a matchMedia test
     * Depends on breakpoints provided in XML from our mntl-sc-page components (maybe different per vertical/template)
     *
     * @return {Boolean}
     */
    function _hasRail() {
        return mntlSCPage ? window.matchMedia(`(min-width: ${mntlSCPage.dataset.scBreakpoint})`).matches : false;
    }

    // Variable to track previous rail state in resizing
    let previousRailState = _hasRail();

    /**
     * Invokes the stickybits clean up and applies the appropate translateY styles
     * to keep the ad at the bottom of the track
     *
     * @param {event} event event object passed back through the event callbacks
     */
    function _unstickBillboards(event) {
        for (const key in billboardAds) {
            const billboard = document.getElementById(key);

            if ((billboardAds[key].stickToBottom !== 'not-stuck') && (event.detail.scrollTop > billboardAds[key].stickyRemovalThresholdHeight)) {
                billboardAds[key].stickyBits.cleanup();
                // This is delta of the total billboard ad track height (total space height) - the height of the billboard
                const translateY = billboardAds[key].billboardTrackHeight - billboard.offsetHeight;

                billboard.style.transform = `translateY(${translateY}px)`;
                delete billboardAds[key];
            }
        }
    }

    /**
     * The internal method used to apply the sticky styles
     *
     * In the verticals the structure of the billboard ads and rail video players are as follows
     *
     * mntl-sc-sticky-billboard component (container div that styles for stickiness with stickybits)
     *     |-> .mntl-billboard div that uses either mntl-gpt-adunit (or dynamic or lazy ad) or mntl-rail-video
     *         |-> iframe containing the ad (if using mntl-gpt-adunit)
     *
     * @param {*} billboardMntlContentDiv dom element of the billboard content (defined as the component that uses gpt-adunit.ftl or rail-video.ftl)
     */
    function applySticky(billboardMntlContentDiv) {
        const billboardVerticalStickyDiv = utils.getClosestMatchingParent(billboardMntlContentDiv, '.mntl-sc-sticky-billboard');

        if (billboardVerticalStickyDiv) {
            const totalBillboardHeight = billboardMntlContentDiv.offsetHeight;
            const billboardTrackHeight = billboardVerticalStickyDiv.offsetHeight;

            // the values used for stickyRemovalThresholdHeight is relative to the window.scrollTop
            // i.e. we need to do calculations of the element relative to the document top
            const billboardTopFromDocument = billboardVerticalStickyDiv.getBoundingClientRect().top + window.scrollY;

            billboardAds[billboardMntlContentDiv.id] = {
                stickyBits: stickybits(`#${billboardMntlContentDiv.id}`, { stickyBitStickyOffset: stickyOffset }),
                billboardTrackHeight,
                // stickyOffset also used to account for nav bar height and some wiggle room down the ad to do unsticking logic
                // this assumes that the verticals maintain a stickyOffset for the header
                stickyRemovalThresholdHeight: billboardTopFromDocument + billboardTrackHeight - totalBillboardHeight - stickyOffset,
                stickToBottom: billboardVerticalStickyDiv.dataset.stickToBottom
            };
        }
    }

    /**
     * Callback function to handle and style the billboard ads when rendered.
     * Meant to be used with the custom adRendered event
     *
     * @param {object} event
     */
    function _handleStickyRender(event) {
        const billboardMntlContentDiv = utils.getClosestMatchingParent(event.currentTarget, '.mntl-gpt-adunit');

        applySticky(billboardMntlContentDiv);
    }

    function removeStickyEvent(billboard) {
        const wrapper = billboard.querySelector('.billboard .wrapper');

        wrapper.removeEventListener('adRendered', _handleStickyRender);

        if(billboardAds[billboard.id]) {
            billboardAds[billboard.id].stickyBits.cleanup();
        }

        delete billboardAds[billboard.id];

    }

    /**
     * Helper function to remove right rail styles and event listeners for the sticky billboard
     * (i.e. handles the big -> small transition and forces mobile styles)
     */
    function _setBigtoSmallSettings() {
        const billboards = document.querySelectorAll('.billboard .wrapper');
        const stuckInDom = document.querySelectorAll('.mntl-sc-sticky-billboard.scads-ad-placed .mntl-sc-sticky-billboard-ad, .mntl-sc-sticky-billboard.rightrail-ad-placed .mntl-sc-sticky-billboard-ad');

        previousRailState = _hasRail();

        // Remove sticky and tracking events if we plan to go to mobile view
        [...billboards].forEach((billboard) => {
            billboard.removeEventListener('adRendered', _handleStickyRender);
        });

        for (const key in billboardAds) {
            billboardAds[key].stickyBits.cleanup();
            delete billboardAds[key];
        }

        // Clear styles on resize
        [...stuckInDom].forEach((el) => {
            el.style.transform = 'none';
        });

        window.removeEventListener('mntl.scroll', _unstickBillboards);
    }

    /**
      * Helper function to reapply right rail styles and event listeners for the sticky billboard
      * (ie handles the small -> big transition)
      */
    function _setSmallToBigSettings() {
        const billboards = document.querySelectorAll('.billboard .wrapper');
        const stuckInDom = document.querySelectorAll('.mntl-sc-sticky-billboard.scads-ad-placed .mntl-sc-sticky-billboard-ad, .mntl-sc-sticky-billboard.rightrail-ad-placed .mntl-sc-sticky-billboard-ad');

        const railVideos = document.querySelectorAll('.mntl-rail-video');

        previousRailState = _hasRail();

        [...billboards].forEach((billboard) => {
            billboard.addEventListener('adRendered', _handleStickyRender);
        });

        // Remove sticky and tracking events if we plan to go to mobile view
        [...railVideos].forEach(applySticky);

        // loop over placed and apply stickybits
        [...stuckInDom].forEach((el) => {
            // have to clear any previous transform styles when 'unsticky'

            el.style.transform = '';

            applySticky(el);
        });

        window.addEventListener('mntl.scroll', _unstickBillboards);
    }

    /**
     * Return a function that handles resize events. The function acts if when
     * the rail state changes (exists -> not exists)
     */
    function _handleResize() {
        if (previousRailState && !_hasRail()) {
            _setBigtoSmallSettings();
        } else if (!previousRailState && _hasRail()) {
            _setSmallToBigSettings();
        }
    }

    function _addRailVideoSticky() {
        const railVideos = document.querySelectorAll('.mntl-rail-video');

        [...railVideos].forEach(applySticky);
    }

    function addStickyBillboardListener(billboards) {
        [...billboards].forEach((billboard) => {
            // adRendered event is bound to the ad slot divs per GPT.js
            // where ad slot div is the inner most <div> i.e. <div id="billboard" ... >,
            // or <div id="billboard2" ... >, etc
            billboard.addEventListener('adRendered', _handleStickyRender);
        });
    }

    /**
     * Helper function that defines all the event listeners needed to be attached
     * to elements (billboards) in the right rail of a structured content page
     */
    function _addStickyEventListeners() {
        // .wrapper class selector used to fetch all ad unit divs and this
        // class is set in the gpt-adunit.ftl (i.e. this is what we control in mantle)
        const billboards = document.querySelectorAll('.billboard .wrapper');

        // We wait for the leaderboard to render before calculations for sticky are applied to rail video
        const leaderboard = document.getElementById('leaderboard');

        addStickyBillboardListener(billboards);

        // Check in case we dont have a leaderboard
        if (leaderboard) {
            leaderboard.addEventListener('adRendered', _addRailVideoSticky);
        }

        // Class mutation can't be observed until https://github.com/yowainwright/stickybits/issues/163 is resolved
        window.addEventListener('mntl.scroll', _unstickBillboards);
    }

    /**
     * init function to define all event listeners needed to be added when this lock-billboard
     * component is used
     */
    function init() {
        // If we have the root element the events are safe
        if (mntlSCPage) {
            // Attach handler if the right rail exists
            if (_hasRail()) {
                _addStickyEventListeners();
            }

            document.addEventListener('mntl.contentResize', throttle(_handleResize, 50));
        }

        // Listens for arbitrary right rail lock events on document body defined by sc-ads.js
        document.body.addEventListener('mntl.rightrail.lock', _handleResize);
    }

    init();

    return { 
        addStickyBillboardListener,
        applySticky, 
        removeStickyEvent 
    }
}(window.Mntl.utilities || {}, window.Mntl.throttle));
