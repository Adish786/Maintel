window.Mntl = window.Mntl || {};

Mntl.Chop = (function(window, domUtils) {
    // sadly, the below is necessary: window.CSS.supports('--a') returns a false negative in Safari,
    // other IE checks return false positives for Edge, and this pairs with the FTL ternary.
    const chopHeightStyle = window.navigator.userAgent.indexOf('Trident') === -1 ? '--chop-height' : 'max-height';
    const matchesFn = [
        'matches',
        'webkitMatchesSelector',
        'msMatchesSelector',
        'mozMatchesSelector',
        'oMatchesSelector'
    ]
        .reduceRight(function(acc, fn) {
            return typeof document.body[fn] === 'function' ? fn : acc;
        });

    function Chop(chop, chopContent, chopHeight) {
        let chopHeightInPx;

        // store elements and bind event listeners
        this.chop = chop;
        this.chop.addEventListener('mntl:chopOpen', this.open.bind(this));

        const {dataset: { chopContentId }} = chop;

        this.chopContent = chopContent || (chopContentId ? document.getElementById(chopContentId) : domUtils.closestPreviousSibling((el) => el[matchesFn]('.chop-content'), chop));

        if (this.chopContent === null) {
            return false;
        }

        this.chopContent.classList.add('chop-content');
        this.chopContentStyle = getComputedStyle(this.chopContent);

        [this.chopTrigger] = this.chop.getElementsByClassName('chop-trigger');
        this.chopTrigger.addEventListener('click', () => {
            this.chop.dispatchEvent(new CustomEvent('mntl:chopOpen', {bubbles: true}));
        });

        // determine actual chop height, correcting for source and format
        if (!chopHeight) {
            chopHeight = this.chop.dataset.chopHeight || this.chopContentStyle.getPropertyValue(chopHeightStyle) || 1000;
        }
        if (!isNaN(chopHeight)) { // numeric height value (assumed to be in pixels)
            this.ensureHeightStyle(`${chopHeight}px`);
            chopHeightInPx = parseInt(chopHeight, 10); // guarantee an actual number instead of only numeric
        } else { // height has specific units
            this.ensureHeightStyle(chopHeight);
            if (chopHeight.indexOf('px') !== -1) {
                chopHeightInPx = parseInt(chopHeight, 10); // parseInt will drop the suffix
            } else {
                // have to calculate the actual height in order to compare with inner height below
                this.chopContent.classList.add('is-chopped');
                chopHeightInPx = this.chopContent.offsetHeight;
            }
        }

        // auto-open chop if the content is too short
        if (chopHeightInPx > this.chopContent.scrollHeight * 0.95) {
            this.open();
        } else {
            this.chop.classList.remove('is-hidden');
            this.chopContent.classList.add('is-chopped');
            this.chop.dispatchEvent(new CustomEvent('mntl:chopArticle', { // TODO: this is bad
                bubbles: true
            }));
        }
    }

    Chop.prototype.ensureHeightStyle = function(chopHeight) {
        const chopHeightValue = this.chopContentStyle.getPropertyValue(chopHeightStyle);

        if (!chopHeightValue || chopHeightValue === '' || chopHeightValue === 'none') {
            this.chopContent.style.setProperty(chopHeightStyle, chopHeight);
        }
    };

    Chop.prototype.open = function() {
        this.chopContent.classList.remove('is-chopped');
        this.chopContent.style.removeProperty(chopHeightStyle);
        this.chop.classList.add('is-hidden');
    };

    /**
     * Instantiate new Chop
     * @param  {object} chop chop container; contains fade and the location of the chop-trigger
     * @param  {number} chopContent the truncated element that will be expanded on chopOpen (optional, falls back to search for elementId specified in data-chop-content-id or previous .chop-content siblings)
     * @return {object} chopHeight the height of chopContent (optional, falls back to data-chop-height or 1000px)
     */
    function makeChop(chop, chopContent, chopHeight) {
        if (!chop || !(chop.jquery || chop instanceof HTMLElement)) {
            window.debug.error('Must pass a valid jQuery or HTMLElement object as the first argument to Mntl.Chop.init');

            return false;
        }
        if (chop.jquery) {
            chop = chop[0];
        }
        if (chopContent && chopContent.jquery) {
            chopContent = chopContent[0];
        }

        return new Chop(chop, chopContent, chopHeight);
    }

    function init(container) {
        const chopSelector = '.comp.mntl-chop';

        if (container.jquery) {
            container = container[0];
        }

        (container[matchesFn](chopSelector) ? [container] : [].slice.call(container.querySelectorAll(chopSelector)))
            .filter((element) => element.dataset.chopHeight && element.dataset.chopHeight !== '')
            .forEach((element) => {
                makeChop(element);
            });
    }

    return {
        init,
        makeChop
    };
}(window, Mntl.domUtilities || {}));

Mntl.utilities.readyAndDeferred(Mntl.Chop.init);
