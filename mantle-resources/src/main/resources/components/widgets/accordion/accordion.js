window.Mntl = window.Mntl || {};

// Global due to edge cases with commerce + vertical overriting state on each other by
// Including the script multiple times on the page
Mntl.AccordionEventListeners = Mntl.AccordionEventListeners || {};

Mntl.Accordion = Mntl.Accordion || (function($, utils, animationUtils) {
    // TODO: Deprecated for 3.14. Remove for GLBE-8105
    const $window = $(window);

    /**
     * Helper function to scroll so that entire drawer is in view when opening accordion drawer.
     * @param {domElement} accordionItem the domElement of the accordionItem
     * @param {domElement} accordion  the domElement of the accordion component
     */
    function _scrollAccordionInView(accordionItem, accordion) {
        const accordionBody = accordionItem.getElementsByClassName('accordion__body')[0];
        // TODO: Deprecated for 3.14. Remove for GLBE-8105
        const $accordion = $('.accordion');

        // Set timeout to wait for drawer to open before getting body offset top
        setTimeout(() => {
            const windowHeight = window.innerHeight;
            const scrollBottom = window.pageYOffset + windowHeight;
            // TODO: Deprecated for 3.14. Adjust for GLBE-8105
            // Support offsetting fullscreen accordion to leave space at the bottom, typically used for adhesive ad.
            const fullscreenBottomOffset = (accordion.dataset.fullscreenBottomOffset || $accordion.data('fullscreen-bottom-offset')) || 0;
            const bodyBottom = accordionBody.offsetTop + accordionBody.offsetHeight + fullscreenBottomOffset;
            const bodyScrollTop = bodyBottom - windowHeight;

            if (scrollBottom < bodyBottom) {
                window.requestAnimationFrame(animationUtils.scrollY.bind(null, 'up', bodyScrollTop));
            }
        }, 30);
    }

    function init() {
        const activeClass = 'is-active';
        const accordions = document.getElementsByClassName('js-accordion');
        // This class must be added in the vertical's component definition
        // (XML) to take advantage of the built-in scrolling code
        const scrollClass = 'js-scroll-sections';

        // TODO: Deprecated for 3.14. Remove for GLBE-8105
        const $event = { type: 'mntl.accordion.toggle' };

        [...accordions].forEach((accordion) => {
            const accordionItems = accordion.getElementsByClassName('js-accordion-item');
            const accordionTriggers = accordion.getElementsByClassName('js-accordion-trigger');

            // We only want the eventListerner bound once
            // and readyAndDeferred calls init mutiple times
            // so ensures we only bind event listener once at the first invoke.

            // GLBE-8516 - We also track trigger length since there is an edge case with litho/commerce + multiple accordions
            // where some accordions load initially without the triggers and then
            // subsequent loads have the proper data
            if (!(accordion.id in Mntl.AccordionEventListeners) && accordionTriggers.length > 0) {
                [...accordionTriggers].forEach((accordionTrigger) => {
                    const currentItem = utils.getClosestMatchingParent(accordionTrigger, '.js-accordion-item');

                    accordionTrigger.addEventListener('click', () => {
                        // When clicking an accordion item, close other drawers and open current drawer.
                        // and toggle existing drawer if clicked again
                        [...accordionItems].forEach((item) => {
                            if ((item === currentItem) && !(currentItem.classList.contains(activeClass))) {
                                item.classList.add(activeClass);
                            } else {
                                item.classList.remove(activeClass);
                            }
                        });

                        // TODO: Deprecated for 3.14. Remove for GLBE-8105
                        $event.target = currentItem;
                        $event.isOpened = currentItem.classList.contains(activeClass);
                        $window.trigger($event);

                        if (accordion.classList.contains(scrollClass)) {
                            _scrollAccordionInView(currentItem, accordion);
                        }
                    });
                });

                Mntl.AccordionEventListeners[accordion.id] = true;
            }
        });
    }

    return { init };
})(window.jQuery || {}, window.Mntl.utilities || {}, window.Mntl.animationUtilities || {});

Mntl.utilities.readyAndDeferred(Mntl.Accordion.init);
