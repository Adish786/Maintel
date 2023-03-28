(function(domUtils) {
    var matchesMethod = typeof document.body.msMatchesSelector === 'undefined' ? 'matches' : 'msMatchesSelector';
    var stylePreventer;
    var stylePreventerConfig = {
        attributes: true,
        subtree: true,
        attributesFilter: ['style']
    };
    var translatePxDims = {
        '970x90': '10x1',
        '728x90': '8x1',
        '970x250': '4x1',
        '300x250': '1x1'
    };

    /**
     * Check for the correct event.data format
     * @param  {String}  origin Origin of request
     * @return {Boolean}        [description]
     * @internal (event.origin === window.location.origin) for all post messages
     * so not a viable filter
     */
    function _isExpectedOrigin(event) {
        return typeof event.data !== 'string' && event.data.hasOwnProperty('ratio');
    }

    /**
     * Belt-and-suspenders way to match event source to frameElement, even if the frameElement
     * property is restricted by the browser for security reasons
     * @param {Object} event    window.postMessage event
     * @return {?HTMLIFrameElement} the frameElement that triggered the postMessage event (or null if no match could be found)
     */
    function _matchFrame(event) {
        var iframes = document.getElementsByTagName('IFRAME');
        var testWindow = event.source;
        var foundIframe;
        var childWindow;
        var i = 0;
        var ii = iframes.length;

        // check for non-restricted frameElement property
        if (event.source.frameElement) {
            window.debug.log('found frame on the event:', event.source.frameElement);

            return event.source.frameElement;
        }

        // bubble up
        do {
            childWindow = testWindow;
            testWindow = testWindow.parent;
        }
        while (testWindow !== window && testWindow !== null) ;

        // check against extant iframes
        for (i, ii; i < ii; i++) {
            window.debug.log('iframes[i].contentWindow', iframes[i].contentWindow, 'event.source', event.source);
            if (iframes[i].contentWindow === childWindow) {
                foundIframe = iframes[i];
                break;
            }
        }

        return foundIframe || null;
    }

    /**
     * Method removes style set by third parties
     * Stop gap between here and setting SafeFrame in GPT.js
     * @param  {[type]} el [description]
     * @return {[type]}    [description]
     */
    function _killExternalStyle(el) {
        el.removeAttribute('style');
        el.removeAttribute('width');
        el.removeAttribute('height');
        delete el.dataset.speclessAd;
        delete el.dataset.specless;
    }

    /**
     * Method replaces data attributes that contain ad's fixed width/height values with "flex"
     * @param {HTMLIFrameElement} frame
     */
    function _resetDimensionDataAttrs(frame) {
        domUtils.setDataAttrs({
            adHeight: 'flex',
            adWidth: 'flex'
        }, frame.closest('[data-ad-height][data-ad-width]'));
    }

    stylePreventer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            _killExternalStyle(mutation.target);
        });
    });

    /**
     * Handle message Events from ad partners
     * @param       {Object}  event
     * @return      {Boolean}
     */
    function _handleMessage(event) {
        var frame;
        var ratio;
        var dataset = {};

        if (_isExpectedOrigin(event)) {
            frame = _matchFrame(event);
            // only apply to ads that we've marked as flexible, even if ads in other slots start signaling
            if (frame && frame[matchesMethod]('.mntl-flexible-ad iframe')) {
                ratio = event.data.ratio.toString().replace(/[^\dx]/g, '');
                dataset.iabcAspect = translatePxDims[ratio] || ratio;
                dataset.iabContainer = frame.dataset.isSafeframe === 'true' ? 'sf-flex' : 'flex';

                domUtils.setDataAttrs(dataset, frame.parentNode);
                _resetDimensionDataAttrs(frame); // replace (our) data-ad-width/data-ad-height values with "flex"

                // take out 3rd-party style values...
                [frame, frame.parentNode, frame.parentNode.parentNode].forEach(_killExternalStyle);

                // ... and prevent them from trying again
                stylePreventer.observe(frame.parentNode.parentNode, stylePreventerConfig);

                // respond to the triggering ad that the ratio has been set
                event.source.postMessage({'ratio-set': frame.id}, event.origin);

                window.debug.log([
                    'Flexible Ad Success: data attribute ratio set to:',
                    dataset.iabcAspect,
                    'for frame id:',
                    frame.id
                ].join(' '));
            }
        }
    }

    window.addEventListener('message', _handleMessage, false);
}(window.Mntl.domUtilities || {}));
