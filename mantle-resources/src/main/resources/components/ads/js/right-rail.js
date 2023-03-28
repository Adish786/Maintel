window.Mntl = window.Mntl || {};

Mntl.RightRail = (function(utils, domUtils, fnUtils, gpt, Maybe) {
    const CLASS_PLACED = 'rightrail-ad-placed';
    const curriedGetData = fnUtils.curry(domUtils.getData);
    
    // unusedAds will be an array of objects like so
    // {
    //   ad: DOMElement of the ad,
    //   index: Integer of placed in right rail
    // }
    let unusedAds = []; 
    let totalRightRailSpace;
    let currentRightRailSpace;

    // These are the values read from the server to setup the right rail ads. They should be avaliable upfront at
    // XML -> HTML Run Time. These values are moved up here so that we can abstract helper functions
    // that may need to recalculate the right rail due to changes on page (FELIX-78).
    let adBottomOffset;
    let adTrackSpacing;
    let minTrackHeight; 
    let maxTrackHeight;
    let inlineAdPositions;
    let loadImmediateCount; 

    /**
     * Remove all ad sizes larger than the available space from the  wrapper's
     * data-size attribute
     * @param  {Element}  slot  Ad slot container
     * @param  {Number}  space  Remaining space available in right rail
     */
    function removeSizes(slot, space) {
        const wrapper = slot.querySelector('.wrapper[data-sizes]');

        // FELIX-79: If there is no wrapper found the only ad slot that was put on the page 
        // is a non dynamic ad (e.g. flex-1) or one of the ads set as an initial ad. This comes 
        // from the initial work in REVDEV-607 that ported settings to be hardcoded in code instead
        // of setting it as properties in XML. You can review gpt-adunit.ftl to confirm the behaviour 
        // of this function.
        if (wrapper) {
            const changeSizes = fnUtils.pipe(JSON.parse,
                (a) => a.filter(
                    (elem) => elem[1] < space || isNaN(elem[1])
                ),
                JSON.stringify);
    
            wrapper.dataset.sizes = changeSizes(wrapper.dataset.sizes);
        }
    }

    function _positionAdsByPixel(adContainer, position, adSlotBlocks) {
        for (const adSlotBlock of adSlotBlocks) {
            if (adSlotBlock.offsetTop >= position - 50) {
                adSlotBlock.appendChild(adContainer);
                break;
            }
        }
    }

    function _positionAdsByIndex(adContainer, position, indexElements) {
        indexElements.forEach((indexElement) => {
            if (Number(indexElement.dataset.itemIndex) === position) {
                indexElement.appendChild(adContainer);
            }
        });
    }

    /**
     * Mantain pixel or index based positioning for native and dynamic inline ad unit containers originating from inside the right rail
     */
    function _appendAdToInlineContent(adContainer, position, adSlotBlocks) {
        const indexElements = [...adSlotBlocks].filter((adSlotBlock) => adSlotBlock.hasAttribute('data-item-index'));
        const adPositionType = indexElements.length > 0 ? 'index' : 'pixel';

        if (adPositionType === 'index') {
            _positionAdsByIndex(adContainer, position, indexElements);
        } else {
            _positionAdsByPixel(adContainer, position, adSlotBlocks);
        }
    }

    function removeStickySettings(rightRailItem, usableRightRailSpace) {
        rightRailItem.style.height = 'auto';
        rightRailItem.dataset.noSticky = '';
        removeSizes(rightRailItem, usableRightRailSpace);
    }

    function _setAdLoadType(item, indexOfItemInRightRail) {
        const gptAdWrapper = item.querySelector('.mntl-gpt-adunit .wrapper[data-sizes]');

        if (gptAdWrapper) {
            if (indexOfItemInRightRail + 1 > loadImmediateCount) {
                gptAdWrapper.parentElement.classList.add('js-lazy-ad');
            } else {
                gptAdWrapper.parentElement.classList.add('js-immediate-ad');
            }
        }
    }

    function getMaxHeightPadding(item) {
        const maxHeight = Maybe.of(domUtils.getData('height', item)).orElse(maxTrackHeight);

        return maxHeight + adTrackSpacing;
    }

    function getMinHeightPadding() {
        return minTrackHeight + adTrackSpacing;
    }

    function roomForAd(usableRightRailSpace) {
        return usableRightRailSpace >= getMinHeightPadding();
    }

    function roomForStickyFunctionality(maxHPadding, usableRightRailSpace) {
        return usableRightRailSpace >= maxHPadding;
    }

    function calculateTotalRightRailSpace() {
        const rightRail = document.querySelector('.article-right-rail');

        // adBottomOffset is the space to account for other elements in the right rail that can eat up space (e.g. button ads)
        // TODO: Improvment would be to dynamically determine this if possible
        return rightRail.offsetHeight - adBottomOffset;
    }

    /**
     * Helper function to determine if an ad should be moved inline content for the ad
     * 
     * @param {DOMElement} ad
     * @returns {boolean}
     */
    function isInlineAd(ad) {
        // Current classes that we look for if the ad should be inline the content well
        const isNative = ad.classList.contains('mntl-native');
        const isDynamicInline = ad.classList.contains('dynamic-inline');

        return (isNative || isDynamicInline);
    }

    /**
     * Helper function to set an inline ad
     * 
     * @param {DOMElement} ad 
     * @param {int} index 
     */
    function setInlineAd(ad, index) {
        if (inlineAdPositions && (inlineAdPositions.length > 0) && inlineAdPositions[index]) {
            const adSlotBlocks = [...document.querySelectorAll('.mntl-sc-block-adslot')];
            const position = inlineAdPositions[index];

            ad.dataset.rightRailIndex = ad.dataset.rightRailIndex || index;

            _appendAdToInlineContent(ad, position, adSlotBlocks.slice(index));
    
            // dynamic-inline ads should always be lazy loaded
            if (ad.classList.contains('mntl-native') && ad.querySelector('.scads-to-load')) {
                _setAdLoadType(ad, index);
            }
        } else {
            console.error('An inline ad is being set when not enough ad slots/positions are avaliable.');
        }
    }

    /**
     * Helper function that handles the logic to set an ad in the Right Rail
     * 
     * @param {DOMElement} ad element of the ad unit
     * @param {Interger} index of the ad unit
     * @param {*} recalculate 
     */
    function setAdInRightRail(ad, index, recalculate = false) {
        const maxHeightPadding = getMaxHeightPadding(ad);
        const isFirstAdInRail = (index === 0);

        if (roomForAd(currentRightRailSpace)) {
            // FELIX-79: In the case of the firstAdRail we want to have sc-lock-billboard.js handle
            // the stickiness. If we hit an edge case where the largest ad renders but there's not 
            // enough space then the sticky libary will handle it. In most cases a smaller ad loads. 
            // Refer to the billboard settings in sticky-billboard.xml to understand the thresholds
            // and refer to the largest size/height to mark the edge cases.
            // 
            // For this FELIX case we observe that the ad was allocated 1090px but the total height of the
            // right rail space is less than 1090px causing the bug in FELIX-79. Hence this comment
            // and addition of skipping the first ad in the right rail.
            //
            // TODO: It'll be good to seperate stickiness from here to be fully only handled by sc-lock-billboards
            // to have seperation of concerns for file.
            if (!roomForStickyFunctionality(maxHeightPadding, currentRightRailSpace) && !isFirstAdInRail) {
                removeStickySettings(ad, currentRightRailSpace);
            }

            currentRightRailSpace -= maxHeightPadding;

            if (!isFirstAdInRail) {
                ad.style.marginTop = `${adTrackSpacing}px`;
            }

            ad.classList.add(CLASS_PLACED);
            _setAdLoadType(ad, index);

            if (recalculate) {
                const nextEmptyRightRailSlot = document.querySelector('.mntl-right-rail .sc-billboard-group > .mntl-block:empty');

                if (nextEmptyRightRailSlot) {
                    nextEmptyRightRailSlot.appendChild(ad);
                } else {
                    console.error('We are attempting to append another ad in a block that cannot exist, we should review why we have more ads than blocks avaliable');
                }
            }
        } else {
            unusedAds.push({ 
                ad, 
                index // to preserve the index of the right rail position after initial call and recalculation
            });

            if (ad.parentNode) {
                ad.parentNode.innerHTML = ''; // empty innerHTML to allow for :empty selector in recalculation
            }
        }

    }

    /*
        If the contentwell increases from an iframe loading on the page, we want to allow the last right rail item 
        to regain its sticky container. Since stickybits has already initialized the container as having a height of 300, we need to 
        remove the event listener set by sc-lock-billboard.js and re-add it after we set its height and transform values
    */
    function resetStickyContainer(lastRighRailItem) {
        const adWithoutStickyContainer = lastRighRailItem.querySelector('.mntl-sc-sticky-billboard-ad');

        Mntl.ScLockBillboards.removeStickyEvent(adWithoutStickyContainer);

        lastRighRailItem.style.height = `${lastRighRailItem.dataset.height}px`;
        adWithoutStickyContainer.style.transform = 'none';
        Mntl.ScLockBillboards.applySticky(adWithoutStickyContainer);
        delete lastRighRailItem.dataset.noSticky;
    }

    function updateRightRailSpace() {
        if (totalRightRailSpace) {
            const previousTotalRightRailSpace = totalRightRailSpace;
            const newTotalRightRailSpace = calculateTotalRightRailSpace();
            const differenceInRightRailSpace = newTotalRightRailSpace - previousTotalRightRailSpace;

            // Update values with new page growth from embeds, iframes, etc
            totalRightRailSpace = newTotalRightRailSpace;
            currentRightRailSpace = currentRightRailSpace + differenceInRightRailSpace;

            const lastRighRailItem = document.querySelector('[data-no-sticky]');


            if(lastRighRailItem && (lastRighRailItem.offsetHeight < differenceInRightRailSpace)) {
                resetStickyContainer(lastRighRailItem);
            }

        } else {
            console.error('The updateRightRailSpace function was called before the right rail has been set. This not intended and will output this error instead of updating the right rail.');
        }
    }

    function resizeHandler() {
        const localUnusedAds = unusedAds;

        // Reset unused ads global instance.
        unusedAds = [];
        updateRightRailSpace();

        localUnusedAds.forEach((item) => {
            if (isInlineAd(item.ad)) {
                setInlineAd(item.ad, item.index);
            } else {
                // If there are more ads left over after this loop then we will repopulate the global instance of
                // unusedAds in setAdInRightRail till the next event triggers
                setAdInRightRail(item.ad, item.index, true);
            }
        });
    }

    // This setup function should be executed once to setup both the right rail and inline ads that are loaded
    // in the billboard-group components. These components are currently defined in the vertical to determine what
    // to load.
    function _setupRightRailAndInlineAds() {
        const rightRail = document.querySelector('.article-right-rail');
        const rightRailItems = rightRail.querySelectorAll('.right-rail__item, .mntl-native, .dynamic-inline');

        totalRightRailSpace = currentRightRailSpace = calculateTotalRightRailSpace();

        for (const [index, item] of rightRailItems.entries()) {
            // FELIX-78 has abstracted this code for reuse in resize/reflow events
            if (isInlineAd(item)) {
                setInlineAd(item, index);
                continue;
            }

            setAdInRightRail(item, index, false);
        }
    }

    /**
     * Locates all ad slots that have been marked with the .js-immediate-ad class and
     * class gpt.displaySlots to load them
     */
    function _displayImmediateRightRailOriginatedAdUnits() {
        const rightRailAds = [...document.querySelectorAll('.article-right-rail .js-immediate-ad:not(.js-immediate-ad-loaded)')];
        const slots = [];

        const rightRailOriginatedAds = [...document.querySelectorAll('[data-right-rail-index] .js-immediate-ad:not(.js-immediate-ad-loaded)')];

        // Keep ad loading order
        rightRailOriginatedAds.forEach((originatedAd) => {
            rightRailAds.splice(Number(originatedAd.dataset.rightRailIndex), 0, originatedAd);
        });

        rightRailAds.forEach((item) => {
            item.classList.add('js-immediate-ad-loaded'); // To track loaded immediate-ads to prevent re-invoking display slots on set ads in the recalculate step

            const adsToLoad = item.querySelectorAll('.wrapper');

            adsToLoad.forEach((ad) => {
                slots.push(new gpt.Slot().byElement(ad));
            });
        });

        gpt.displaySlots(slots);
    }

    function _removeunPositionedRightRailOriginatedAds() {
        [...document.querySelectorAll('.article-right-rail [data-right-rail-index]')].forEach((unPositionedAd) => {
            const lazyAdContainer = unPositionedAd.querySelector('.js-lazy-ad');

            if(lazyAdContainer){
                Mntl.LazyAds.removeLazyAd(lazyAdContainer.id);
            }
            unPositionedAd.remove();
        });
    }

    function _init(maybeMntlScPage, curriedGetData) {
        adBottomOffset = maybeMntlScPage.map(curriedGetData('scAdBottomOffset')).orElse(0);
        adTrackSpacing = maybeMntlScPage.map(curriedGetData('scAdTrackSpacing')).orElse(100);
        minTrackHeight = maybeMntlScPage.map(curriedGetData('scMinTrackHeight')).orElse(250);
        maxTrackHeight = maybeMntlScPage.map(curriedGetData('scMaxTrackHeight')).orElse(0);
        inlineAdPositions = maybeMntlScPage.flatMap(curriedGetData('scContentPositions'));
        loadImmediateCount = maybeMntlScPage.map(curriedGetData('scLoadImmediate')).orElse(0);

        _setupRightRailAndInlineAds();
        _removeunPositionedRightRailOriginatedAds();

        document.querySelector('.mntl-right-rail').style.display = 'block';

        // all lazy ad classes have been added and extra ads removed, reinitialize the lazy ad array
        Mntl.LazyAds.init();

        _displayImmediateRightRailOriginatedAdUnits();

        if(Mntl.GPT.getUseInfiniteRightRail()) {
            new Mntl.InfiniteRightRailManager(totalRightRailSpace, currentRightRailSpace, adBottomOffset);
        }

        // Debounced so that we do not trigger expensive height calculations on every resize event, just want the final one captured
        // This is especially noticable for pages with a ton of embeds as embeds grow and shrink in size (their code not ours :/)
        const resizeObserver = new ResizeObserver(Mntl.debounce(() => {
            resizeHandler();
            // Reinvoke these calls for resized ads so we render the ads
            // These 2 calls will boil down and have the trigger to display the slot at the end when we add more ads to the page
            _displayImmediateRightRailOriginatedAdUnits();
            Mntl.LazyAds.init();
        }, 1500));

        // Iframes specifically are targeted for FELIX-78 but this can be selected for something more generic, or a list of tags
        // causing CLS/reflow. But for the scope of limiting height recalculations to iframe
        const iframes = document
            .querySelector('.article-content')
            .querySelectorAll('iframe');

        if (iframes.length > 0) {
            iframes.forEach((iframe) => resizeObserver.observe(iframe));
        }
    }

    utils.onLoad(() => {
        const maybeMntlScPage = Maybe.of(document.querySelector('[data-sc-content-positions]'));
        const commerceLoaded = Mntl.Commerce && Mntl.Commerce.getIsCommerceInfoLoaded && Mntl.Commerce.getIsCommerceInfoLoaded();

        // When a grid item is present but is not being used in the grid layout, the offsetHeight property is 0
        const mntlArticleRightRailLocationIsPresent = document.querySelector('.article-right-rail').offsetHeight > 0;

        const hasRail = (function(breakpoint) {
            return () => mntlArticleRightRailLocationIsPresent && window.matchMedia(['(min-width: ', breakpoint, ')'].join('')).matches;
        }(maybeMntlScPage.flatMap(curriedGetData('scBreakpoint'))));

        if (hasRail()) {
            // if commerce images haven't fully loaded wait till the commerce info loaded event is fired
            if (document.querySelector('.mntl-sc-block-commerce__image') && !commerceLoaded) {
                $(document).on('commerce-info-loaded', () => _init(maybeMntlScPage, curriedGetData));
            } else {
                _init(maybeMntlScPage, curriedGetData);
            }
        }
    });

    return {
        removeStickySettings,
        removeSizes,
        getMaxHeightPadding,
        getMinHeightPadding,
        roomForAd,
        roomForStickyFunctionality
    }
})(window.Mntl.utilities || {}, window.Mntl.domUtilities || {}, window.Mntl.fnUtilities || {}, window.Mntl.GPT || {}, window.Mntl.Maybe || {});