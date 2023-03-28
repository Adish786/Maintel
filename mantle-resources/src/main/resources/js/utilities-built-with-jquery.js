(function(window, $) {
    window.Mntl = window.Mntl || {};

    const jQueryUtilities = (function() {
        function loadStyleSheet(path, callback) {
            var doc;
            var ss;
            var ref;
            var refs;
            var sheets;
            var $link = $('<link rel="stylesheet" type="text/css" href="' + path + '"/>');

            // eslint-disable-next-line consistent-return
            function onloadcssdefined(cb) {
                var resolvedHref = ss.href;
                var i = sheets.length;

                while (i--) {
                    if (sheets[i].href === resolvedHref) {
                        return cb();
                    }
                }
                setTimeout(function() {
                    onloadcssdefined(cb);
                });
            }

            if (window.Modernizr.hasEvent('load', $link)) {
                // Use onLoad callback for modern browsers
                if (callback) {
                    $link.on('load', callback);
                }
                $('head').append($link);
            } else {
                // Fallback for browsers that don't support onLoad on a link element (i.e. Android native browser)
                doc = window.document;
                ss = doc.createElement('link');
                refs = (doc.body || doc.getElementsByTagName('head')[0]).childNodes;
                ref = refs[refs.length - 1];

                sheets = doc.styleSheets;
                ss.rel = 'stylesheet';
                ss.href = path;
                // temporarily set media to something inapplicable to ensure it'll fetch without blocking render
                ss.media = 'only x';

                // Inject link
                // Note: the ternary preserves the existing behavior of "before" argument, but we could choose to change the argument to "after" in a later release and standardize on ref.nextSibling for all refs
                // Note: `insertBefore` is used instead of `appendChild`, for safety re: http://www.paulirish.com/2011/surefire-dom-element-insertion/
                ref.parentNode.insertBefore(ss, (ref.nextSibling));
                // A method (exposed on return object for external use) that mimics onload by polling until document.styleSheets until it includes the new sheet.
                // once loaded, set link's media back to `all` so that the stylesheet applies once it loads
                ss.onloadcssdefined = onloadcssdefined;
                onloadcssdefined(function() {
                    ss.media = 'all';
                    if (callback) {
                        callback();
                    }
                });
            }
        }

        /**
         * Run callbacks on document.ready and deferred event
         * @param  {Function} callback
         * @return {undefined}
         */
        function readyAndDeferred(callback) {
            const $body = $('body');

            // ready
            $(callback.bind($body, $body));

            // and defered
            $(document).on('defer-batch-complete', function(e, $components) {
                $components.forEach(function($component) {
                    callback.call($component, $component);
                });
            });
        }

        /**
         * Check element visibility based on delta being in view
         * For article, only vertical visibility matters. Horizontal is commented out.
         * @param  {object}  $elem                 element to find
         * @param  {number|float}  visibleFraction percent in viewport
         * @param  {number}  offset                arbitrary px offset
         * @return {Boolean}                       true if > visibleFraction is found in the viewport
         */
        function isElementVisibleY($elem, visibleFraction, offset) {
            var elem = $elem.length ? $elem[0] : $elem;
            var elementHeight = elem.offsetHeight;
            var elementOffsetY = 0;
            var elementBottom;
            var visibleY;
            var visiblePctY;
            var parent = elem;

            offset = offset || 0;
            visibleFraction = typeof visibleFraction !== 'number' ? 0 : visibleFraction;

            if ($elem.length > 1) {
                window.debug.warn('More than 1 element passed to isElementVisibleY(), running method against the first element only');
            }

            // Loop through parent DOM elements to handle statically placed elements
            while (parent && parent !== document.body) {
                elementOffsetY += parent.offsetTop;
                parent = parent.offsetParent;
            }
            elementBottom = elementOffsetY + elementHeight;

            // Calculate visible portion of DOM element.
            visibleY = Math.max(
                0,
                Math.min(
                    elementHeight,
                    // +fixedMastHeight // scrolled amt + windowHeight - headerOffset
                    window.pageYOffset + window.innerHeight - elementOffsetY + offset,
                    // -fixedMastHeight // (Height + setDistance from Top of doc) - scrolledAmt - headerOffset
                    elementBottom - window.pageYOffset - offset
                )
            );

            visiblePctY = visibleY / elementHeight;

            return visiblePctY > visibleFraction;
        }

        function getDocumentBaseUrl() {
            return $(document)
                .find('meta[property="og:url"]')
                .attr('content') || [location.protocol, '//', location.host, location.pathname].join('');
        }

        function getDocumentSocialTitle() {
            return $(document)
                .find('meta[itemprop=name]')
                .attr('content') || '';
        }

        function getDocumentMetaDescription() {
            return $(document)
                .find('meta[name=description]')
                .attr('content') || '';
        }

        function scrollToElement($element, callback, offset) {
            offset = offset || 0;
            $('html, body').animate({ scrollTop: $element.offset().top - offset}, 500, callback);
        }

        // Alphabetize Public API
        return {
            getDocumentBaseUrl,
            getDocumentMetaDescription,
            getDocumentSocialTitle,
            isElementVisibleY,
            loadStyleSheet,
            readyAndDeferred,
            scrollToElement
        };
    }());

    Mntl.utilities = Mntl.utilities ? Object.assign(Mntl.utilities, jQueryUtilities) : jQueryUtilities;
}(window, window.jQuery));
