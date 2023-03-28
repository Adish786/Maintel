window.Mntl = window.Mntl || {};

window.Mntl.InfiniteRightRailManager = (function () {
    class RightRailAdUnit {
        constructor(billboardAdBlueprint, noSticky, rightRailAdUnitIndex, currentRightRailSpace, offset, adsPossibleFromResizedPage) {
            this.DOMElement = billboardAdBlueprint;
            this.billboardContainer = this.DOMElement.querySelector('.mntl-billboard');
            this.wrapper = this.DOMElement.querySelector('.wrapper');
            this.offset = offset;
            this.updateDataAttributesForBillboardBlueprint(rightRailAdUnitIndex, adsPossibleFromResizedPage);

            if(noSticky) {
                window.debug.log("Mntl.InfiniteRightRailManager: No room a full 300x600 container, but enough room to place a 300x250 ad unit");
                Mntl.RightRail.removeStickySettings(this.DOMElement.querySelector('.right-rail__item'), currentRightRailSpace);
            }
        }

        /** 
         *  Take an exisiting square-fixed ad unit on the page and strips out the data 
         *  attributes/ids unique to that ad unit or indicating that the ad has been 
         *  requested/rendered 
         * 
         *  @param {Number} rightRailAdUnitIndex index that the generated square-fixed ad unit should have
         */ 
        updateDataAttributesForBillboardBlueprint(rightRailAdUnitIndex, adsPossibleFromResizedPage) {
            // removes iframe from ad unit
            this.wrapper.innerHTML = "";
            delete this.wrapper.dataset.googleQueryId;

            this.DOMElement.removeAttribute('id');
            this.DOMElement.querySelector('.right-rail__item').removeAttribute('id');

            this.DOMElement.querySelector('.right-rail__item').style.height = "";

            this.billboardContainer.style.transform = "";
            this.billboardContainer.classList.remove("is-requested");
            this.billboardContainer.classList.remove('js-immediate-ad');
            this.billboardContainer.classList.add('js-lazy-ad');

            if(adsPossibleFromResizedPage) {
                this.billboardContainer.dataset.infiniteAdFromResize = "";
            }

            delete this.billboardContainer.dataset.adWidth;
            delete this.billboardContainer.dataset.adHeight;

            delete this.wrapper.dataset.rightRailAdDestroyed;

            this.DOMElement.id = `infinite-right-rail-block-${rightRailAdUnitIndex}`;
            this.billboardContainer.id = `infinite-ad-unit-container-${rightRailAdUnitIndex}`;
            this.billboardContainer.classList.add('infinite-ad-unit-container');

            this.wrapper.id = Mntl.GPT.getUseLmdFormat() ? "square-fixed" : "billboard";

            // Make sure sizes match square fixed in case our blueprint ad unit ends up being a square-flex ad unit
            this.wrapper.dataset.sizes = '[[300, 250],[299, 251],"fluid"]';
        }

        loadBillboardImmediately() {
            this.billboardContainer.classList.remove('js-lazy-ad');
            this.billboardContainer.classList.add('js-immediate-ad');

            Mntl.GPT.displaySlots([new Mntl.GPT.Slot().byElement(this.wrapper)]);
        }

        placeInRightRail() {
            window.debug.log(`Mntl.InfiniteRightRailManager: Placing ${this.wrapper.id}`);

            document.querySelector('.js-scads-inline-content').appendChild(this.DOMElement);
            const isInView = Mntl.utilities.isElementVisibleY(this.billboardContainer, '', this.offset);
            const isAboveViewport = this.billboardContainer.getBoundingClientRect().top <= 0;

            if(isInView) {
                this.loadBillboardImmediately();
            } else {
                if(isAboveViewport) {
                    window.debug.log(`Mntl.InfiniteRightRailManager: Skipped Infinite Right Rail ad`);

                    this.billboardContainer.classList.add('infinite-lazy-ad-skipped');
                    this.wrapper.removeAttribute('id');
                    // fake slotRequestedEvent trigger
                    const fakeSlotRequestedEvent = new CustomEvent('slotRequested', {detail: { skipDestroy: true }});

                    this.wrapper.dispatchEvent(fakeSlotRequestedEvent);
                }
                Mntl.LazyAds.init();
            }
        }
    }

    class InfiniteRightRailManager {
        // handle deletion and infinite scroll

        constructor (initialRightRailSpace, currentRightRailSpace, adBottomOffset) {
            this.rightRailTotalHeight = initialRightRailSpace;
            this.currentRightRailSpace = currentRightRailSpace;
            this.adBottomOffset = adBottomOffset;
            this.usesResizeInfiniteAds = false;

            const billboardGroup = document.querySelectorAll('.article-right-rail .mntl-right-rail .mntl-block .scads-to-load');

            this.billboardAdBlueprint = billboardGroup[billboardGroup.length - 1].parentElement.cloneNode(true);
            this.offset = document.querySelector('html').dataset.lazyOffset || 200;

            this.maxHeightPadding = window.Mntl.RightRail.getMaxHeightPadding(this.billboardAdBlueprint);
            this.minHeightPadding = window.Mntl.RightRail.getMinHeightPadding();

            this.rightRailAdUnitIndex = 0;

            this.attemptToPlaceNewRightRailAd();

            document.body.addEventListener('mntl.infiniterightrail.enable.expand', this.enableRighRailResizeObserver.bind(this), { once: true });

            document.body.dispatchEvent(new CustomEvent('mntl.infiniterightrail.loaded'));
        }

        enableRighRailResizeObserver() {
            const resizeObserver = new ResizeObserver(this.rightRailIncreasedHeight.bind(this));

            resizeObserver.observe(document.querySelector('.article-right-rail'));
        }

        rightRailIncreasedHeight() {
            
            const rightRail = document.querySelector('.article-right-rail');

            const newRightRailHeight = rightRail.offsetHeight - this.adBottomOffset;

            if(newRightRailHeight > this.rightRailTotalHeight) {
                if(!this.usesResizeInfiniteAds) {
                    this.usesResizeInfiniteAds = true;
                }

                this.currentRightRailSpace += newRightRailHeight - this.rightRailTotalHeight;

                this.rightRailTotalHeight = newRightRailHeight;
                
                window.debug.log(`Mntl.InfiniteRightRailManager: Usable right rail space is now ${this.currentRightRailSpace}px`);

                if(this.mostRecentInfiniteAdHasLoaded()) {
                    this.attemptToPlaceNewRightRailAd();
                    Mntl.LazyAds.init();
                }
            }
        }

        mostRecentInfiniteAdHasLoaded() {
            const getInfiniteAdUnitContainers = document.querySelectorAll('.infinite-ad-unit-container');

            if(getInfiniteAdUnitContainers.length === 0) {
                return true;
            }

            const mostRecentInfiniteAd = getInfiniteAdUnitContainers[getInfiniteAdUnitContainers.length - 1];

            return mostRecentInfiniteAd.classList.contains('is-requested') || mostRecentInfiniteAd.classList.contains('js-immediate-ad') || mostRecentInfiniteAd.classList.contains('infinite-lazy-ad-skipped');
        }

        attemptToPlaceNewRightRailAd() {
            const {
                roomForAd, 
                roomForStickyFunctionality 
            } = Mntl.RightRail;

            if (this.usesResizeInfiniteAds && roomForStickyFunctionality(this.maxHeightPadding, this.currentRightRailSpace, this.offset)) {
                this.placeNewRightRailBillboard(false);

                window.debug.log(`Mntl.InfiniteRightRailManager: Decrementing space by ${this.maxHeightPadding}px`);

                this.currentRightRailSpace -= this.maxHeightPadding;
            } else if (!this.usesResizeInfiniteAds && roomForAd(this.currentRightRailSpace)) {
                const isSticky = roomForStickyFunctionality(this.maxHeightPadding, this.currentRightRailSpace, this.offset);

                this.placeNewRightRailBillboard(!isSticky);

                const decrementRightRailBy = (isSticky ? this.maxHeightPadding : this.minHeightPadding);

                window.debug.log(`Mntl.InfiniteRightRailManager: Decrementing space by ${decrementRightRailBy}`);

                this.currentRightRailSpace -= decrementRightRailBy;
            } else {
                window.debug.log(`Mntl.InfiniteRightRailManager: No room for a new Desktop Load and Destroy Ad unit`);
            }

            window.debug.log(`Mntl.InfiniteRightRailManager: Only ${this.currentRightRailSpace}px of space remains of ${this.rightRailTotalHeight}px`);

        }

        _destroyRightRailAd() {
            const billboardToDestroy = document.querySelectorAll('.article-right-rail .billboard:not(.infinite-lazy-ad-skipped) .wrapper');

            // handle if the billboard group has only 7 or less ad units set from the vertical end
            if(billboardToDestroy.length >= 8) {
                Mntl.GPT.destroySlotById(billboardToDestroy[0].id);
                billboardToDestroy[0].parentElement.remove();
            }

            this.attemptToPlaceNewRightRailAd();
        }

        placeNewRightRailBillboard(noSticky) {
            const newBillboard = new RightRailAdUnit(this.billboardAdBlueprint.cloneNode(true), noSticky, ++this.rightRailAdUnitIndex, this.currentRightRailSpace, this.usesResizeInfiniteAds);

            let resolveAdDestruction;

            const destroyRightRailAd = new Promise((resolve) => {
                resolveAdDestruction = resolve;
                newBillboard.wrapper.addEventListener('slotRequested', resolveAdDestruction);
            });

            destroyRightRailAd.then((lastBillboard) => {
                if(!lastBillboard.detail || !lastBillboard.detail.skipDestroy) {
                    this._destroyRightRailAd(lastBillboard);
                } else {
                    this.attemptToPlaceNewRightRailAd();
                }
                newBillboard.wrapper.removeEventListener('slotRequested', resolveAdDestruction);
            }).catch((err) => console.log(err));

            newBillboard.placeInRightRail();

            Mntl.ScLockBillboards.addStickyBillboardListener([newBillboard.wrapper]);
        }
    }

    return InfiniteRightRailManager;
})();