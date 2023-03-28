(function(utils) {
    /**
     * A function to push data from the accordionItem to the dataLayer. This is strictly for FAQ Blocks.
     *
     * @param {domElement} accordionItem  the domElement of the accordionItem.
     */
    function pushItemToDataLayer(accordionItem) {
        const itemState = accordionItem.classList.contains('is-active') ? 'Close' : 'Open';
        const faqEl = accordionItem.closest('.mntl-sc-block-faq');
        const event = 'transmitInteractiveEvent';
        const eventCategory = faqEl.getAttribute('data-tracking-id') || 'FAQ';
        const eventAction = faqEl.getAttribute('data-click-action') || `FAQ Click ${itemState}`;
        const eventLabel = faqEl.getAttribute('data-click-label') || accordionItem.querySelector('.accordion__title').innerText;

        const fireEvent = utils.pushToDataLayer({});

        fireEvent({
            event,
            eventCategory,
            eventAction,
            eventLabel
        });
    }

    /**
     * Initiate Here.
     */
    function init() {
        const faqAccordionsTriggers = document.querySelectorAll('.mntl-sc-block-faq .js-accordion-trigger');

        if (faqAccordionsTriggers.length) {
            [...faqAccordionsTriggers].forEach((trigger) => {
                const currentItem = utils.getClosestMatchingParent(trigger, '.js-accordion-item');

                trigger.addEventListener('click', () => {
                    pushItemToDataLayer(currentItem);
                });
            });
        }
    }

    init();
})(Mntl.utilities || {});
