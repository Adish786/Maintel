(function($, Maybe, fnUtils, domUtils, throttle) {
    var MaybeMntlScPage = Maybe.of(document.querySelectorAll('[data-sc-content-positions]')[0]);
    var dataStickyOffset = MaybeMntlScPage.map(fnUtils.curry(domUtils.getData)('scStickyOffset')).orElse(0);
    var $body = $('body');

    /**
     * Does the stored value pass the matchMedia test
     * @param       {Element}  el
     * @return      {Boolean}
     */
    function _hasRail(el) {
        return window.matchMedia(['(min-width: ', el.dataset.scBreakpoint, ')'].join('')).matches;
    }

    /**
     * unlock billboard when it reaches end of sticky ad track (do not lock again
     * when scrolling up)
     */
    function _handleBottom() {
        var $this = $(this);

        $this.closest('.billboard-sticky').addClass('scads-stuck-bottom');
        $this
            .trigger('sticky_kit:detach')
            .css({
                position: 'absolute',
                bottom: 0,
                width: $this.parents('.spacer').width()
            });
    }

    /**
     * Stick ads when they're rendered
     * @return {Undefined}
     */
    function _handleAdRender() {
        var $billboard = $(this).closest('.billboard');
        var $billboardSticky = $billboard.closest('.billboard-sticky');

        if ($billboardSticky.hasClass('scads-stick-in-parent') && $billboard.outerHeight() < $billboardSticky.data('height') && !$billboardSticky.hasClass('scads-stuck-bottom')) {
            $billboardSticky.css('height', $billboardSticky.data('height'));
            $billboard.stick_in_parent({
                parent: $billboardSticky.data('parent') || '.billboard-sticky',
                offset_top: dataStickyOffset, // eslint-disable-line camelcase
                spacer: '.spacer'
            });

            /* If data-stick-to-bottom='not-stuck' is set on the billboard, _handleBottom is not triggered
               and billboard remains sticky on scroll up */
            if ($billboardSticky.data('stick-to-bottom') !== 'not-stuck') {
                $billboard.on('sticky_kit:bottom', _handleBottom);
                $body.trigger('sticky_kit:recalc'); // Triggering a recalc during ad render as values are cached for sticky kit and may not be accurate here
            }
        }
    }

    /**
     * Return a function that handles resize events. The function acts if when
     * the rail state changes (exists -> not exists)
     * @constructor
     * @return {Function} handleResize
     */
    function _handleResize() {
        var hasRail = MaybeMntlScPage.flatMap(_hasRail);
        var stuckInDom = document.getElementsByClassName('scads-stick-in-parent');

        return function handleResize() {
            if (hasRail && !MaybeMntlScPage.flatMap(_hasRail)) { // big -> small
                hasRail = false;
                // deactivate
                $body.off('adRendered', '.billboard .wrapper', _handleAdRender); // stop listening
                // loop over pinned stuff and detach
                Array.prototype.forEach.call(stuckInDom, function(el) {
                    el.style.height = 'auto';
                    $(el.getElementsByClassName('billboard')[0])
                        .trigger('sticky_kit:detach')
                        .css({
                            position: 'static',
                            bottom: 'auto'
                        })
                        .parent('.spacer')
                        .attr('style', '');
                });
            } else if (!hasRail && MaybeMntlScPage.flatMap(_hasRail)) { // small -> big
                hasRail = true;
                // activate
                $body.on('adRendered', '.billboard .wrapper', _handleAdRender); // start listening
                // loop over placed and attach
                Array.prototype.forEach.call(stuckInDom, function(el) {
                    el.classList.remove('scads-stuck-bottom'); // reset sticky elements.
                    _handleAdRender.apply(el.getElementsByClassName('wrapper')[0]);
                });
            }
        };
    }

    // If we have the root element the events are safe
    if (!MaybeMntlScPage.isNothing()) {
        // Attach handler if the right rail exists
        if (MaybeMntlScPage.flatMap(_hasRail)) {
            $body.on('adRendered', '.billboard .wrapper', _handleAdRender);
        }

        $(window).on('resize', throttle(_handleResize(), 50));
    }

    /**
     * Listens for arbitrary right rail lock events on document body. To trigger:
     *
     *     var e = new CustomEvent('mntl.rightrail.lock');
     *     document.body.dispatchEvent(e);
     *
     * @return {[type]}   [description]
     */
    document.body.addEventListener('mntl.rightrail.lock', _handleResize());
}(window.jQuery || {}, window.Mntl.Maybe || {}, window.Mntl.fnUtilities || {}, window.Mntl.domUtilities || {}, window.Mntl.throttle));
