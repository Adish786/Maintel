window.Mntl = window.Mntl || {};

Mntl.Commerce = (function($, utils, domUtils, fnUtils) {
    const commerceScBlocks = []; // set by init();
    const updateHref = domUtils.setProp.bind(null, 'href');
    const clickCategory = 'commerce-widget Click';
    const clickEvent = 'transmitInteractiveEvent';
    const fireEvent = utils.pushToDataLayer({
        eventValue: '',
        nonInteraction: false
    });
    let isLoaded = false;

    /**
     * Helper function to determine if the commerce info is loaded for other components like right-rail that listen
     * for the commerce component to load.
     * @returns {boolean}
     */
    function getIsCommerceInfoLoaded() {
        return isLoaded;
    }

    /**
     * Test an element for a single class
     * @param  {string}   className
     * @param  {Element}  el
     * @return {Boolean}
     */
    function hasClass(className, el) {
        return el.classList.contains(className);
    }

    const isHeading = hasClass.bind(null, 'mntl-sc-block-heading');

    function updatePriceInformation(commerceItem, commerceButton) {
        if (!Mntl.Commerce.pricingDisabled && fnUtils.getDeepValue(commerceItem, 'commerceModel', 'bestPrice')) {
            commerceButton.dataset.commercePrice = commerceItem.commerceModel.bestPrice;
        }
    }

    function updateMetaInformation(commerceItem, commerceButton) {
        const deepLink = fnUtils.getDeepValue(commerceItem, 'commerceModel', 'deeplink');
        const bestPrice = fnUtils.getDeepValue(commerceItem, 'commerceModel', 'bestPrice');
        const skimlinksTracking = fnUtils.getDeepValue(window, 'skimlinks_settings', 'skimlinks_tracking');

        /**
         *  Skimlinks Attribution
         *  We are checking for bestPrice and deepLink so that the deepLink
         *  is only applied if a bestPrice is available. We also check if
         *  skimkinks and add them if they are.
         **/
        if (bestPrice && commerceItem.type === 'skimlinks' && deepLink) {
            if (skimlinksTracking) {
                commerceButton.dataset.clickHref = encodeURI(`${commerceItem.commerceModel.deeplink}&xcust=${window.skimlinks_settings.skimlinks_tracking}`);
            } else {
                commerceButton.dataset.clickHref = commerceItem.commerceModel.deeplink;
            }
        }
    }

    function fireClickTrackingEvent(eventAction, eventLabel, itemIndex, eventCategory, event) {
        fireEvent({
            event,
            eventCategory,
            eventAction,
            eventLabel,
            eventOther: `list item ${parseInt(itemIndex, 10) + 1}`
        });
    }

    /**
     * Iterates through a list of button event definitions and applies them to the given button
     */
    function attachButtonEvents(commerceButton, index, itemClickCategory, itemClickEvent) {
        const buttonText = commerceButton.innerText.trim();

        // change href on click if click href is present
        commerceButton.replaceHref = function() {
            if (this.dataset.clickHref) {
                this.href = this.dataset.clickHref;
            }
        };

        // Save a reference to the click track handler so we can unbind it later
        commerceButton.clickTrackHandler = function() {
            fireClickTrackingEvent(buttonText, this.dataset.href || this.href, index, itemClickCategory, itemClickEvent);
            // change href on click if click href is present
            this.replaceHref();
        };

        const events = {
            click: commerceButton.clickTrackHandler,
            contextmenu: commerceButton.replaceHref
        };

        // Attach event handlers
        for (const eventKey in events) {
            commerceButton.addEventListener(eventKey, events[eventKey]);
        }
    }

    /**
     * Handle click on arbitary elements. Function should only be applied with
     * .bind so it can reference the actual button to act on from the element
     * on which the click handler is/was assigned
     * @return {undefined} [description]
     */
    function handleSurrogateButtonClicks(event, eventAction, itemClickCategory, itemClickEvent) {
        // If the clicked element is an achor tag, respect the tag url.
        if (event.target.tagName === 'A') {
            return;
        }

        // Unbind the click handler from the commerce button so the click tracking event isn't fired twice
        this.removeEventListener('click', this.clickTrackHandler);

        // Fire our custom click tracking event
        fireClickTrackingEvent(eventAction, this.href, this.dataset.itemIndex, itemClickCategory, itemClickEvent);

        // CMRC-349 //
        // For some unholy reason, skimlinks doesn't pick up the surrogate button
        // click if it happens sooner than 200ms after original click.
        setTimeout(() => {
            // Fire surrogate click event
            this.dispatchEvent(new MouseEvent('click', { 'cancelable': true }));

            // Rebind the click handler
            this.addEventListener('click', this.clickTrackHandler);
        }, 250);
    }

    /**
     * Add click handler to commerce header
     * @param  {Element} header         h# to add click event to
     * @param  {Element} commerceButton button to make target of click
     * @return {undefined}
     */
    function updateHeader(header, commerceButton, itemClickCategory, itemClickEvent) {
        if (header !== null) {
            header.classList.add('mntl-sc-block-heading--link');
            header.addEventListener('click', (e) => {
                handleSurrogateButtonClicks.call(commerceButton, e, commerceButton.dataset.headerText, itemClickCategory, itemClickEvent);
            });
        }
    }

    /**
     * Update or add product image
     * @param  {string}  src src attribute
     * @param  {Element} commerceButton starting element
     * @return {undefined}
     */
    function updateImage(src, commerceButton, itemClickCategory, itemClickEvent) {
        const parentPreviousEl = domUtils.closestPreviousSibling((el) => hasClass('mntl-sc-block-image', el) || isHeading(el), commerceButton.parentNode);
        const figure = commerceButton.parentNode.getElementsByClassName('mntl-sc-block-commerce__image')[0] || null;
        const caption = figure && figure.querySelector('.mntl-figure-caption');
        const img = figure && figure.getElementsByTagName('IMG')[0];

        if (!figure) {
            return;
        }

        // If parentPreviousEl is an img, use the src from that instead of from commerceModel
        if (hasClass('mntl-sc-block-image', parentPreviousEl)) {
            const prevImg = parentPreviousEl.getElementsByTagName('IMG')[0];
            const imgSrcset = prevImg.dataset.srcset || prevImg.getAttribute('srcset');
            const imgSizes = prevImg.dataset.sizes || prevImg.getAttribute('sizes');
            const imgWidth = prevImg.getAttribute('width');
            const imgHeight = prevImg.getAttribute('height');
            const imgAlt = prevImg.getAttribute('alt');

            if (imgSrcset) {
                img.setAttribute('srcset', imgSrcset);
            }
            if (imgSizes) {
                img.setAttribute('sizes', imgSizes);
            }
            if (imgWidth) {
                img.setAttribute('width', imgWidth);
            }
            if (imgHeight) {
                img.setAttribute('height', imgHeight);
            }
            if (imgAlt) {
                img.setAttribute('alt', imgAlt);
            }
            src = prevImg.dataset.src || prevImg.getAttribute('src');
        } else if (typeof src !== 'string') {
            return;
        }

        const prevCaption = parentPreviousEl.querySelector('.mntl-figure-caption');

        if (prevCaption && caption) {
            figure.replaceChild(prevCaption, caption);
        }

        figure.classList.remove('figure-portrait');
        img.setAttribute('src', src);
        img.addEventListener('click', (e) => {
            handleSurrogateButtonClicks.call(commerceButton, e, [commerceButton.dataset.headerText, commerceButton.dataset.buttonText].join(' '), itemClickCategory, itemClickEvent);
        });

        if (parentPreviousEl !== null) {
            isHeading(parentPreviousEl) ?
                parentPreviousEl.insertAdjacentElement('afterend', figure) :
                parentPreviousEl.parentNode.replaceChild(figure, parentPreviousEl); // ChileNode.replaceWith() is still expermental
        }

        return;
    }

    /**
     * Append fragment to href if one does not already exist
     * @param  {string} href url to add fragment to
     * @param  {string} fragment button to make target of click
     * @return {string} href with appended fragment
     */
    function appendFragment(href, fragment) {
        if (href !== null && fragment !== null && href.indexOf('#') === -1) {
            return `${href}#${fragment}`;
        }

        return href;
    }

    function updateCommerceItems(commerceData, index) {
        const commerceBlock = commerceScBlocks[index];
        const commerceButtons = commerceBlock.getElementsByClassName('mntl-commerce-btn');
        const commerceButtonText = [];
        const itemClickCategory = commerceBlock.getAttribute('data-click-category') || clickCategory;
        const itemClickEvent = commerceBlock.getAttribute('data-click-event') || clickEvent;
        const useExternalImage = commerceBlock.dataset.useExternalImage || true;
        const linkClosestHeader = commerceBlock.dataset.linkClosestHeader || true;
        let firstButton;
        let firstItem;

        // For each commerce list item (each item in data)
        commerceData.forEach((commerceItem, i) => {
            const commerceButton = commerceButtons[i];
            const buttonText = commerceButton.innerText.trim();

            commerceButtonText.push(buttonText);
            commerceButton.dataset.clickTracked = false; // Disable the default click tracking for the buttons

            // For non-amazon btns, save the original href (since it can be changed after a click)
            if (!/amazon\.com/.test(commerceButton.href)) {
                commerceButton.dataset.href = commerceButton.href;
            }

            // Attach any commerce button event listeners
            attachButtonEvents(commerceButton, index, itemClickCategory, itemClickEvent);

            // Insert any price information
            updatePriceInformation(commerceItem, commerceButton);

            // Read other meta info (i.e skimlinks data, etc.)
            updateMetaInformation(commerceItem, commerceButton);

            // Update button url
            updateHref(
                appendFragment(
                    fnUtils.getDeepValue(commerceItem, 'commerceModel', 'url') || commerceButton.getAttribute('href'),
                    commerceButton.getAttribute('data-commerce-fragment')
                ),
                commerceButton
            );

            // ...and run post-processing
            Mntl.externalizeLinks.processLink($(commerceButton));

            // Save references to the first item
            if (i === 0) {
                firstButton = commerceButton;
                firstItem = commerceItem;
            }
        });

        const commerceHeader = domUtils.closestPreviousSibling((el) => isHeading(el), firstButton.parentNode);

        firstButton.dataset.headerText = commerceHeader ? commerceHeader.innerText : '';
        firstButton.dataset.buttonText = commerceButtonText.join(' ');
        firstButton.dataset.itemIndex = index;

        // if we have image data place the image, add event handlers
        if (useExternalImage === true) {
            updateImage(fnUtils.getDeepValue(firstItem, 'commerceModel', 'imageUrl'), firstButton, itemClickCategory, itemClickEvent);
        }
        // get the heading and add event handlers
        if (linkClosestHeader === true) {
            updateHeader(commerceHeader, firstButton, itemClickCategory, itemClickEvent);
        }
    }

    function urlParamPartsReducer(params) {
        return (acc, key) =>
            /*
                Note: The et and state values are being detached from the url and being passed on to the
                service as a parameter thus changing the ? to &
            */
            (params.hasOwnProperty(key) ? (acc = [acc, '&', key, '=', params[key]].join('')) : acc)
    }

    /**
     * Calls commerce task
     * gets all info for all commerce blocks
     */
    function getExtendedCommerceInfo(domain, pathname) {
        const params = Mntl.utilities.getQueryParams();
        const blockDomain = domain || Mntl.utilities.getDomain();
        const pathName = pathname || window.location.pathname;
        const urlParamParts = ['et', 'state', 'priorityRetailerDomains', 'safelistRetailerDomains'];

        if (Mntl.Commerce.priorityDomains) {
            params.priorityRetailerDomains = Mntl.Commerce.priorityDomains.toString();
        }

        if (Mntl.Commerce.safelistDomains) {
            params.safelistRetailerDomains = Mntl.Commerce.safelistDomains.toString();
        }

        const url = [
            '/servemodel/model.json?modelId=gatherListOfListCommerceInfo&url=',
            window.location.protocol,
            !Mntl.utilities.hasWwwSubdomain || Mntl.utilities.hasWwwSubdomain() ? '//www.' : '//',
            blockDomain,
            pathName,
            urlParamParts.reduce(urlParamPartsReducer(params), '')
        ].join('');

        return new Promise(((resolve, reject) => {
            /*
                Once path name resolves to proper path, the Ajax function fires off.
            */
            if (pathName.indexOf('/external-component') === -1) {
                window.Mntl.utilities.ajaxPromiseGetCall(url)
                    .then((data) => JSON.parse(data))
                    .then((data) => {
                        data.forEach(updateCommerceItems);
                        document.dispatchEvent(new CustomEvent('commerce-info-loaded', [data]));
                        isLoaded = true;
                        resolve(data);
                    })
                    .catch((err) => {
                        window.debug.error('Error retrieving commerce data from the server: ', err);
                        reject(err);
                    });
            }
        }));
    }

    function init($container) {
        // unwrap stupid jQuery
        const container = $container.jquery ? $container[0] : $container;

        /* Allow domain override by providing data attributes, mostly used for testing external component */
        const {
            commerceDomain,
            commercePathname
        } = container.dataset;

        // var in IIFE scope
        const containerScBlock = container.getElementsByClassName('mntl-sc-block-commerce');

        if (containerScBlock.length > 0) {
            Array.prototype.push.apply(commerceScBlocks, containerScBlock);
            getExtendedCommerceInfo(commerceDomain, commercePathname);
        }
    }

    return {
        init,
        getIsCommerceInfoLoaded
    };
})(window.jQuery || {}, Mntl.utilities || {}, Mntl.domUtilities || {}, Mntl.fnUtilities || {});

Mntl.utilities.readyAndDeferred(Mntl.Commerce.init);
