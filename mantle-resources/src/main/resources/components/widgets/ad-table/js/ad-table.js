window.Mntl = window.Mntl || {};

Mntl.AdTables = (function() {
    const EVENT = (window.Modernizr && window.Modernizr.touchevents) ? 'touchstart' : 'click';
    const INITIALIZED_CLASS_NAME = 'mntl-ad-table__disclosure--initialized';
    const DISCLOSURE_ACTIVE_CLASS_NAME = 'mntl-ad-table__disclosure--active';

    function init($container) {
        const container = $container.jquery ? $container[0] : $container;
        const adTables = container.matches('.mntl-ad-table') ? [container] : container.querySelectorAll('.mntl-ad-table:not(.mntl-ad-table__column)');

        adTables.forEach((adTable) => {
            const disclosureEl = adTable.querySelector('.mntl-ad-table__disclosure');

            if (!disclosureEl.classList.contains(INITIALIZED_CLASS_NAME)) {
                // display column ads
                Mntl.GPT.displaySlots(Mntl.fnUtilities.toArray(adTable.querySelectorAll('.mntl-gpt-dynamic-adunit .wrapper')).map((el) => new Mntl.GPT.Slot().byElement(el)));

                // handle disclosure
                adTable.querySelector('.mntl-ad-table__disclosure-label').addEventListener(EVENT, () => {
                    disclosureEl.classList.toggle(DISCLOSURE_ACTIVE_CLASS_NAME);
                });

                adTable.querySelector('.mntl-ad-table__disclosure-close').addEventListener(EVENT, () => {
                    disclosureEl.classList.remove(DISCLOSURE_ACTIVE_CLASS_NAME);
                });

                disclosureEl.classList.add(INITIALIZED_CLASS_NAME);
            }
        });
    }

    return { init };
}());

Mntl.utilities.readyAndDeferred(Mntl.AdTables.init);
