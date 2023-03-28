window.Mntl = window.Mntl || {};
window.dataLayer = window.dataLayer || [];

(function(fnUtils, Maybe) {
    function addAncestorId(el) {
        return el.getAttribute('id');
    }

    function getOrdinal(el) {
        return el.dataset.ordinal || '';
    }

    function getTrackingContainerId(el) {
        var trackingContainerId = '';

        if (el.dataset.trackingContainer === 'true') {
            trackingContainerId = el.dataset.trackingId || el.getAttribute('id');
        }

        return trackingContainerId;
    }

    /**
     * [doesLinkLeavePage description]
     * @param  {Element} el
     * @param  {String} location
     * @return {Boolean}
     */
    function doesLinkLeavePage(el, location) {
        // because IE doesn't do window.location.origin
        // && chrome returns the port with window.location.host
        var origin = [window.location.protocol, '//', window.location.hostname].join('');

        if (location.indexOf(origin) !== 0 || /blank/.test(el.getAttribute('target'))) {
            return true;
        }

        return false;
    }

    function getDataAttributes(el, data) {
        var _data = data;

        Array.prototype.forEach.call(el.attributes, function(attr) {
            var val;
            var name;

            if (attr.name.indexOf('data-') === 0) {
                val = attr.value;
                name = attr.name.replace(/-([a-z])/g, function(g) {
                    return g[1].toUpperCase();
                });
                _data[name] = val;
            }
        });

        return _data;
    }

    function getAttribute(el, attr) {
        return el.getAttribute(attr) || '';
    }

    function getFullUrl(el) {
        var url = getAttribute(el, 'href');

        // convert relative to absolute
        if (url.indexOf('//') === 0) {
            url = window.location.protocol + url;
        } else if (url.indexOf('/') === 0) {
            url = window.location.protocol + '//' + window.location.host + url;
        }

        return url;
    }

    function getChildURL(el) {
        return Maybe.of(el.querySelector(el.dataset.useChildUrl)).map(getFullUrl)
            .orElse('');
    }

    function getLinkText(el, e) {
        if (e.target !== el && (fnUtils.trimAllWhitespace(e.target.innerText) !== '' || fnUtils.trimAllWhitespace(e.target.textContent.replace(/<[^>]*>?/gm, '')) !== '')) {
            return fnUtils.trimAllWhitespace(e.target.innerText || e.target.textContent.replace(/<[^>]*>?/gm, ''));
        } else {
            return el ? fnUtils.trimAllWhitespace(el.innerText || el.textContent.replace(/<[^>]*>?/gm, '') || '') : '';
        }
    }

    Mntl.clickTracking = {
        getPixelsFromContent: function(elCoordinates) {
            var main = document.querySelector('main') || document.getElementById('main');
            var mainCoordinates;

            if (main) {
                mainCoordinates = Mntl.clickTracking.getAbsoluteCoordinates(main);
                mainCoordinates.top = window.scrollY;

                return {
                    top: elCoordinates.top - mainCoordinates.top,
                    left: elCoordinates.left - mainCoordinates.left
                };
            } else {
                return {
                    top: '',
                    left: ''
                };
            }
        },
        getAbsoluteCoordinates: function(el) {
            var boundingRect = el.getBoundingClientRect();
            var scrollY = window.scrollY;
            var scrollX = window.scrollX;

            return {
                left: boundingRect.left + scrollX,
                top: boundingRect.top + scrollY
            };
        },
        trackClick: function(e, el, ordinal, trackingContainerId, ancestorIds) {
            var data = {
                event: 'linkClick',
                linkId: getAttribute(el, 'id'),
                linkText: getAttribute(el, 'data-click-action') || getLinkText(el, e),
                linkLabel: getAttribute(el, 'title') || getAttribute(el, 'aria-label'),
                pageWidth: document.body.clientWidth,
                pageHeight: document.body.clientHeight
            };
            var elWindowOffset = Mntl.clickTracking.getAbsoluteCoordinates(el);
            var elMainOffset = Mntl.clickTracking.getPixelsFromContent(elWindowOffset);

            if ((el.getAttribute('href') || '')[0] === '#') {
                data.linkTargetURL = el.getAttribute('href');
                data.linkTargetType = 'onpage';
            } else if (el.dataset.useChildUrl) {
                data.linkTargetURL = getChildURL(el);
                data.linkTargetType = data.linkTargetURL !== '' ? 'offpage' : '';
            } else if (el.nodeName.toLowerCase() === 'button' || el.getAttribute('data-click-tracked') === 'true') {
                data.linkTargetURL = '';
                data.linkTargetType = '';
            } else {
                data.linkTargetURL = getFullUrl(el);
                data.linkTargetType = 'offpage';
            }

            data.dataOrdinal = ordinal;
            data.linkContainerId = trackingContainerId;
            data.domAncestorIds = ancestorIds.reverse();
            data.pixelsFromTopOfPage = elWindowOffset.top;
            data.pixelsFromLeftOfPage = elWindowOffset.left;
            data.pixelsFromTopOfMainContainer = elMainOffset.top;
            data.pixelsFromLeftOfMainContainer = elMainOffset.left;

            data = getDataAttributes(el, data);

            return Mntl.clickTracking.fireEvent(data, e, el);
        },
        searchDom: function(e) {
            var path = e.path || null;
            var el = e.target;
            var linkTarget = null;
            var ancestorIds = [];
            var ordinal = '';
            var trackingContainerId = '';
            var i;

            function shouldNotTrack(element) {
                return element.dataset.clickTracked === 'false';
            }

            function extractValues(element) {
                if (shouldNotTrack(element)) {
                    return false;
                }

                if (element.hasAttribute) {
                    if (ordinal === '') {
                        ordinal = getOrdinal(element);
                    }

                    if (trackingContainerId === '') {
                        trackingContainerId = getTrackingContainerId(element);
                    }

                    if (element.hasAttribute('id')) {
                        ancestorIds.push(addAncestorId(element));
                    }

                    if ((element.nodeName === 'A' || element.nodeName === 'BUTTON' || element.getAttribute('data-click-tracked') === 'true') && linkTarget === null) {
                        linkTarget = element;
                    }
                }

                return true;
            }

            // If returning from the tracking event, exit out of function
            if (e.target.wasTracked) {
                return true;
            }

            if (Modernizr.touchevents && Mntl.clickTracking.dragging === true) {
                return true;
            }

            if (shouldNotTrack(el)) {
                return false;
            }

            if (path && path.length > 2) {
                for (i = 0; i < path.length - 2; i++) {
                    if (!extractValues(path[i])) {
                        return false;
                    }
                }
            } else {
                while (el.parentNode !== null && el.nodeName !== null) {
                    if (!extractValues(el)) {
                        return false;
                    }
                    el = el.parentNode;
                }
            }

            if (linkTarget !== null) {
                return Mntl.clickTracking.trackClick(e, linkTarget, ordinal, trackingContainerId, ancestorIds);
            }

            return false;
        },
        fireEvent: function(data, e, link) {
            var location = data.linkTargetURL;
            var target = e.target;
            var leavesPage = doesLinkLeavePage(link, location);
            var timeout;

            function callback() {
                document.body.removeEventListener('click-sent', callback, false);
                window.clearTimeout(timeout);
                target.wasTracked = true;
                try {
                    target.click();
                } catch (error) {
                    target = target.closest('A');
                    target.wasTracked = true;
                    target.click();
                }
            }

            if (data.linkTargetType === 'offpage' && !e.ctrlKey && !e.shiftKey && !e.metaKey && !leavesPage && link.nodeName.toLowerCase() !== 'button' && e.which === 1) {
                // Wait for custom event sent from GTM to signal request complete, safe to unload
                document.body.addEventListener('click-sent', callback, false);

                timeout = setTimeout(callback, 2000);
                window.dataLayer.push(data);
                e.preventDefault();
            } else {
                return window.dataLayer.push(data);
            }

            return false;
        },
        setDragging: function() {
            Mntl.clickTracking.dragging = true;
        },
        endDragging: function() {
            Mntl.clickTracking.dragging = false;
        },
        dragging: false,
        init: function() {
            var el = document.body || window;

            el.addEventListener('click', Mntl.clickTracking.searchDom, true);
        }
    };

    Mntl.clickTracking.init();
})(window.Mntl.fnUtilities || {}, window.Mntl.Maybe || {});
