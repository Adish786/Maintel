window.Mntl = window.Mntl || {};

(function(utils, fnUtils, domUtils) {
    const isTouchDevice = window.Modernizr && window.Modernizr.touchevents;
    const refIdSet = [];

    function addCitationRefIds(container) {
        const citationSources = fnUtils.toArray(container.querySelectorAll('li.mntl-sources__source[id]'));

        citationSources.forEach((citation) => {
            if (refIdSet.indexOf(citation.id) === -1) {
                refIdSet.push(citation.id);
            }
        });
    }

    function handleUrlFragment(fragment) {
        const refId = fragment[0] === '#' ? fragment.substr(1) : null;
        const citationSource = refId && document.getElementById(refId);
        const articleSources = citationSource && citationSource.closest('.mntl-article-sources');
        let headerOffset;

        if (articleSources) {
            Mntl.ExpandableBlock.open(articleSources);
            headerOffset = articleSources.getAttribute('data-scroll-offset') || 0;
            window.location.hash = refId;
            citationSource.scrollIntoView();
            window.scrollBy(0, -headerOffset);

            return true;
        }

        return false;
    }

    function handleInlineCitationClick(event) {
        const inlineCitation = event.target;
        // The 'event' and 'eventCategory' tracking values should be the same as default values. The reason it's duplicated here is because we need to trigger an event with a custom 'eventAction' and 'eventLabel' value (we don't want what's provided by default with click tracking library)
        const trackAnchorClick = utils.pushToDataLayer({
            event: 'mntlDynamicTooltip',
            eventCategory: 'mntl-dynamic-tooltip Click',
            eventAction: 'inline-citation-tooltip-anchor',
            eventLabel: inlineCitation.innerText[0]
        });

        // Mntl.Tooltip may not be ready when the citation is created so we need to check it at the time of the click per GLBE-8711 findings
        const triggerTooltipOnClick = isTouchDevice && Mntl.Tooltip;

        // Only scroll down for non-touch devices or if tooltips not-enabled.
        if (!triggerTooltipOnClick && event.target.className.indexOf('mntl-inline-citation') !== -1) {
            if (handleUrlFragment(inlineCitation.getAttribute('data-id'))) {
                trackAnchorClick();
            }
        }
    }

    function setInlineCitation(placeholder, refId, number) {
        const element = domUtils.createEl({
            type: 'SPAN',
            props: { // eslint-disable-line quote-props
                className: 'mntl-inline-citation',
                'data-id': `#${refId}`
            },
            children: [number.toString()]
        });

        element.addEventListener('click', handleInlineCitationClick);
        placeholder.parentNode.replaceChild(element, placeholder);
        if (Mntl.Tooltip) {
            Mntl.Tooltip.createDynamicTooltip(element, {
                contentFactory() {
                    const tooltipContent = document.createElement('DIV');
                    const citationSourceClone = document.getElementById(refId).cloneNode(true);

                    // need to copy the children into the new div because the parent element of the citation source
                    // is an li element which wouldn't make sense outside the scope of a ol/ul element
                    while (citationSourceClone.firstChild) {
                        tooltipContent.appendChild(citationSourceClone.removeChild(citationSourceClone.firstChild));
                    }

                    return tooltipContent;
                },
                tracking: { eventAction: 'inline-citation-tooltip-open' }
            });
        }
    }

    function processInlineCitations(container) {
        const inlineCitations = fnUtils.toArray(container.querySelectorAll('span[data-cite]'));

        inlineCitations.forEach((inlineCitation) => {
            const refId = `citation-${inlineCitation.getAttribute('data-cite')}`;
            const refIdIndex = refIdSet.indexOf(refId);

            if (refIdIndex !== -1) {
                setInlineCitation(inlineCitation, refId, 1 + refIdIndex);
            }
        });
    }

    function hashHandler() {
        if (window.location.hash) {
            handleUrlFragment(window.location.hash);
        }
    }

    const initOnce = fnUtils.once(() => {
        hashHandler();
        window.addEventListener('hashchange', hashHandler, false);
    });

    function init($container) {
        // TODO: Deprecate this in 3.15 for final axe of jquery cleanup.
        const container = ($container && $container.jquery) ? $container[0] : $container;

        addCitationRefIds(container);
        processInlineCitations(container);

        initOnce();
    }

    utils.readyAndDeferred(init);
})(Mntl.utilities || {}, Mntl.fnUtilities || {}, Mntl.domUtilities || {});
