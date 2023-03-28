(function(Mntl) {
    const nativeAdContainer = document.querySelector('.mntl-native');
    const adBlock = nativeAdContainer.querySelector('.mntl-native__adunit');
    const adWrapper = adBlock.querySelector('.wrapper');
    const mntlScPage = document.querySelector('.mntl-sc-page');
    const chop = document.querySelector('.mntl-chop');
    const { selector } = nativeAdContainer.dataset;

    function manualPlaceAd() {
        const insertBeforeEl = mntlScPage.querySelector(selector);

        // position the ad
        if (insertBeforeEl) {
            mntlScPage.insertBefore(nativeAdContainer, insertBeforeEl);
        }
    }

    function manualRequestAd() {
        const slot = new Mntl.GPT.Slot().byElement(adWrapper);

        adBlock.classList.add('is-requested');
        Mntl.GPT.displaySlots([slot]);
    }

    function onNativeAdRender(nativeAd) {
        if (nativeAd.isEmpty) {
            nativeAdContainer.style.setProperty('--native-ad-height', 'auto');
        }
    }

    function isSCAd() {
        return (
            adBlock.classList.contains('scads-to-load') &&
            adBlock.closest('.js-scads-inline-content, .article-right-rail, [data-right-rail-index]') !== null
        ) ||
        adBlock.classList.contains('scads-ad-placed');
    }

    /**
     * 1. check if chop button is in use, true when chop button exist and not hidden
     * 2. skip if non page body defers the init. if ancestor is mntl-sc-page and contains js-scads-inline-content
     * then early return as sc-ads.js will handle loading.
     * 3. if data-manual-load-on-scroll is set to true, then load on user scroll
     * 4. if manual load but data-manual-load-on-scroll is not true, then request the ad immediately
     */
    function init($el) {
        const hasChop = chop ? !chop.classList.contains('is-hidden') : false; /* 1 */
        // TODO: Deprecate this in 3.15 for final axe of jquery cleanup.
        const el = ($el && $el.jquery) ? $el[0] : $el;

        if (isSCAd() || el !== document.body) {
            return; /* 2 */
        }

        if (selector) {
            manualPlaceAd();
        }

        if (hasChop) {
            chop.addEventListener('mntl:chopOpen', manualRequestAd);
        } else if (nativeAdContainer.dataset.manualLoadOnScroll === 'true') {
            window.addEventListener('scroll', manualRequestAd, { once: true }); /* 3 */
        } else {
            manualRequestAd(); /* 4 */
        }
    }

    Mntl.utilities.readyAndDeferred(init);
    Mntl.GPT.registerCallback(['native'], onNativeAdRender);
})(window.Mntl || {});
