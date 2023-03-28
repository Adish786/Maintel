(function(googletag) {
    // Assumption that only one leaderboard header included on the page (top leaderboard ad)
    const leaderboard = document.querySelector('.mntl-leaderboard-header');
    // Classes to toggle rollaway leaderboard header behaviour
    const notSticky = 'not-sticky';
    const isCollapsed = 'is-collapsed';

    // Values needed to determine sticky behaviour
    const collapsePoint = leaderboard.dataset['collapse-point'] || 1;
    let flexAdObserver;
    let leaderboardLoaded = false;


    /**
     * Helper function to determine if the leaderboard is sticky. Determinate on the non-sticky class being applied
     * @returns {boolean}
     */
    function isSticky() {
        return !leaderboard.classList.contains(notSticky);
    }

    /**
     * Execute a series of class operations to remove the 'lock' of the leaderboard (i.e. remove the 'sticky' features)
     * @return {Undefined}
     */
    function removeStickyFeatures() {
        leaderboard.classList.add(notSticky);
        leaderboard.classList.remove(isCollapsed);
    }

    /**
     * Helper function for the the time out and ad impression event.
     * Note this is code sensitive to both events since both events can occur in any order on the page
     * if this function ever needs to be refactored.
     * @return {Undefined}
     */
    function toggleStickyFeatures() {
        leaderboardLoaded = true;

        if ((window.scrollY > collapsePoint) && isSticky()) {
            leaderboard.classList.add(isCollapsed);
        } else {
            removeStickyFeatures();
        }
    }

    /**
     * Handle Scroll Events and removes sticky classes for the leaderboard per business spec
     * @param  {Object}    e Event Object
     * @return {Undefined}
     */
    function leaderboardScrollHandler(e) {
        // If the leaderboard has loaded and the user hasn't scrolled very far don't
        // have a sticky leaderboard
        if (leaderboardLoaded && (e.detail.scrollTop < collapsePoint)) {
            removeStickyFeatures();
            window.removeEventListener('mntl.scroll', leaderboardScrollHandler);
        }
    }

    /**
     * Handle google impression Event. Will collapse the ad when the ad is viewable.
     * @param  {Object} event
     * @return {Undefined}
     */
    function impressionHandler(event) {
        if (event.slot.getSlotElementId() === 'leaderboard' || event.slot.getSlotElementId() === 'leaderboard-flex-1') {
            toggleStickyFeatures();
        }
    }

    /**
     * Handle adRendered Event
     * @param  {Object} e event
     * @return {Undefined}
     */
    function adRenderedHandler(e) {
        window.debug.log('leaderboard adRendered', e);
        flexAdObserver.observe(e.target.firstElementChild, {
            attributes: true,
            attributeFilter: ['data-iabc-aspect', 'data-iab-container']
        });
    }

    // Add event handlers
    if (leaderboard) {
        flexAdObserver = new MutationObserver((mutations) => {
            // Selector used to only target adjacent spacer for the mntl-leaderboard-header
            const adSpacer = document.querySelector('.mntl-leaderboard-header + .js-rollaway-spacer');

            adSpacer.getAttribute(mutations.reduce((attrs, mutation) => {
                attrs[mutation.attributeName] = mutation.target.getAttribute(mutation.attributeName);

                return attrs;
            }, {}));
        });

        leaderboard.addEventListener('adRendered', adRenderedHandler, {once: true});

        window.addEventListener('mntl.scroll', leaderboardScrollHandler);

        googletag.cmd.push(() => {
            googletag.pubads().addEventListener('impressionViewable', impressionHandler);
        });

        const { timeout } = leaderboard.dataset;
        const timeoutInt = timeout ? parseInt(timeout.replace(/[^0-9]/g, ''), 10) : 5000;

        setTimeout(toggleStickyFeatures, timeoutInt);
    }
})(window.googletag || {});
