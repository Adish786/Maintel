window.Mntl = window.Mntl || {};

Mntl.Tooltip = (function(utils, fnUtils, domUtils, doc) {
    const DYNAMIC_TOOLTIP_ID = 'mntl-dynamic-tooltip';
    let defaultPositionX = 'auto';
    let defaultPositionY = 'auto';
    const initDynamicTooltipElement = fnUtils.once(() => {
        let tooltipElement = doc.getElementById(DYNAMIC_TOOLTIP_ID);

        if (!tooltipElement) {
            tooltipElement = domUtils.createEl({
                type: 'DIV',
                props: { // eslint-disable-line quote-props
                    id: DYNAMIC_TOOLTIP_ID,
                    className: DYNAMIC_TOOLTIP_ID,
                    'data-tracking-container': true
                },
                children: []
            });
            doc.body.appendChild(tooltipElement);
        }
    });
    const trackDynamicTooltipOpen = utils.pushToDataLayer({
        event: 'mntlDynamicTooltip',
        eventCategory: 'mntl-dynamic-tooltip Click',
        eventAction: 'tooltip-open'
    });

    function getTooltipPositionX(tooltip, container) {
        let percentX;

        // inline-level tooltip triggers can wrap to multiple lines - using offsetLeft ensures we get the left position of the first border box
        // instead of the left position of the bounding rectangle which becomes the left border of the wrapped text. For more see this link:
        // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/offsetLeft
        if (tooltip.hasAttribute('data-inline-tooltip')) {
            percentX = container ? (tooltip.offsetLeft - container.offsetLeft) / container.offsetWidth : tooltip.offsetLeft / window.innerWidth;
        } else {
            percentX = container ? (tooltip.getBoundingClientRect().left - container.getBoundingClientRect().left) / container.offsetWidth : tooltip.getBoundingClientRect().left / window.innerWidth;
        }

        if (percentX < 0.33) {
            return 'left';
        } else if (percentX < 0.67) {
            return 'center';
        }

        return 'right';
    }

    function getTooltipPositionY(tooltip, container) {
        let percentY;

        if (container) {
            percentY = (tooltip.getBoundingClientRect().bottom - container.getBoundingClientRect().top) / container.offsetHeight;
        } else {
            percentY = tooltip.getBoundingClientRect().top / window.innerHeight;
        }

        if (percentY < 0.5) {
            return 'bottom';
        }

        return 'top';
    }

    function setTooltipPosition(tooltip) {
        let container = tooltip.closest('[data-contain-tooltips]');

        tooltip.dataset.tooltipPositionX = (defaultPositionX === 'auto') ? getTooltipPositionX(tooltip, container) : defaultPositionX;
        tooltip.dataset.tooltipPositionY = (defaultPositionY === 'auto') ? getTooltipPositionY(tooltip, container) : defaultPositionY;
    }

    function init(config) {
        const tooltips = doc.querySelectorAll('[data-tooltip]');

        defaultPositionX = fnUtils.getDeepValue(config, 'defaultPositionX') || 'auto';
        defaultPositionY = fnUtils.getDeepValue(config, 'defaultPositionY') || 'auto';

        fnUtils.toArray(tooltips).forEach(setTooltipPosition);
    }

    /**
     * Create a dynamic full-feature tooltip.
     * @param element          the element that will trigger display of the tooltip
     * @param config           the configuration object (see notes below)
     *
     * NOTES:
     *
     * `config` object specifies how the tooltip is configured; it has the following properties:
     *     `contentFactory`    [required] a function that will provide the content for the tooltip
     *     `tracking`          [optional] an object that will overlay the default tooltip tracking properties
     *     `secondaryTriggerEl` [optional] an additional element that will trigger the tooltip but in the context of `element` (so the tooltip will still point to `element`)
     *
     * The contentFactory function is called _every_ time the tooltip is displayed.
     * The value returned by contentFactory can be a string or DOM Element.
     * For optimal performance a DOM Element is preferred. Note also that
     * the DOM Element will be attached to the body so be sure to clone it
     * if the reference node is already on the page.
     */
    function createDynamicTooltip(element, config) {
        const triggerText = fnUtils.trimAllWhitespace(element.innerText);
        const secondaryTriggerElement = config.secondaryTriggerEl;

        // NOTE: DO NOT BE TEMPTED TO INIT THE FIRST TIME THE TOOLTIP IS DISPLAYED
        // it needs to be done _before_ to ensure that CSS transitions work as expected
        // see https://stackoverflow.com/questions/24148403/trigger-css-transition-on-appended-element
        initDynamicTooltipElement();
        element.classList.add(`${DYNAMIC_TOOLTIP_ID}--trigger`);

        function tooltip() {
            const tooltipContent = config.contentFactory();
            let tooltipElement = doc.getElementById(DYNAMIC_TOOLTIP_ID);

            // if needed move the tooltip so that it's a child of the trigger element
            if (tooltipElement.parentNode !== element) {
                tooltipElement = element.appendChild(tooltipElement);
            }

            setTooltipPosition(element);

            if (typeof tooltipContent === 'string') {
                tooltipElement.innerHTML = tooltipContent;
                // make sure it's something to which we can add a class
                if (!fnUtils.getDeepValue(tooltipElement, 'firstChild', 'classList')) {
                    tooltipElement.innerHTML = ''; // re-do it with a proper element
                    tooltipElement.appendChild(domUtils.createEl({
                        type: 'DIV',
                        props: {},
                        children: [tooltipContent]
                    }));
                }
            } else {
                tooltipElement.innerHTML = ''; // clear out the previous contents
                tooltipElement.appendChild(tooltipContent);
            }

            tooltipElement.firstChild.classList.add(`${DYNAMIC_TOOLTIP_ID}--content`);

            trackDynamicTooltipOpen(Object.assign({}, { eventLabel: triggerText }, config.tracking));
        }

        element.addEventListener('mouseenter', tooltip);

        if (secondaryTriggerElement) {
            secondaryTriggerElement.addEventListener('mouseenter', tooltip);
        }
    }

    return {
        // for static text-only tooltips driven by data-tooltip attribute
        init,
        getTooltipPositionX,
        getTooltipPositionY,

        // for dynamic tooltips containing full-featured markup generated on the fly
        createDynamicTooltip
    };
}(Mntl.utilities || {}, Mntl.fnUtilities || {}, Mntl.domUtilities || {}, window.document));
