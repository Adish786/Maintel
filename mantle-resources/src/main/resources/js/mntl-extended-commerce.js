/* global Promise:true */
/* eslint no-use-before-define: ["error", { "functions": false }] */
window.Mntl = window.Mntl || {};

if (!window.Mntl.ExtCommerce) {
    window.Mntl.ExtCommerce = (function ($, utils, domUtils, fnUtils) {
        const processedBlocks = [];

        const actions = {
            headerClick: 'header click',
            imageClick: 'image click',
            buttonClick: 'button click',
            jumpLinkClick: 'jump-link click'
        };

        const targets = {
            extendedCommerceContainer: 'js-extended-commerce__section',
            extendedCommerceBlock: 'js-extended-commerce__block',
            header: 'js-extended-commerce__header',
            image: 'js-extended-commerce__image',
            defaultImage: 'js-extended-commerce__default-image',
            button: 'js-extended-commerce__button',
            jumpLink: 'js-extended-commerce__jump-link'
        };

        const classes = {
            primeButton: 'extended-commerce--prime-eligible',
            link: 'extended-commerce__link'
        };

        const updateHref = domUtils.setProp.bind(null, 'href');

        const defaultClickCategory = 'commerce-widget Click';
        const defaultClickEvent = 'transmitInteractiveEvent';

        const fireEvent = utils.pushToDataLayer({
            eventValue: '',
            nonInteraction: false
        });

        /*
            Separates Affiliate links by type.
            @param {*} container - The DOM object containing the commerce buttons with `[data-retailer-type]`.
        */
        function getAffiliateLinks(container) {
            const rawButtons = container.querySelectorAll(`.${targets.button}[data-retailer-type]`);

            // Used by DataOps for tracking purposes.
            const affiliateLinks = {
                event: 'transmitNonInteractiveEvent',
                catagory: 'Commerce Links',
                action: 'Send Impressions',
                label: {}
            };

            rawButtons.forEach((item) => {
                if (affiliateLinks.label[item.dataset.retailerType]) {
                    affiliateLinks.label[item.dataset.retailerType].push(item.href);
                } else {
                    affiliateLinks.label[item.dataset.retailerType] = [];
                    if (typeof affiliateLinks.label[item.dataset.retailerType] !== 'undefined') {
                        affiliateLinks.label[item.dataset.retailerType].push(item.href);
                    }
                }
            });

            affiliateLinks.label = JSON.stringify(affiliateLinks.label);

            fireAffiliateLinkTracking(affiliateLinks);
        }

        /*
            Sends sorted affiliate links to GA.
            @param {object} affiliateLinks - Object containing
        */
        function fireAffiliateLinkTracking(affiliateLinks) {
            fireClickTrackingEvent(affiliateLinks.event, affiliateLinks.catagory, affiliateLinks.action, affiliateLinks.label);
        }

        /**
         * readyAndDeferred callback, runs every time the code is included or a deferred block loads
         * @param {*} $container - The dom object loading - body or the deferred block
         */
        function init($container) {
            // unwrap stupid jQuery
            const container = $container.jquery ? $container[0] : $container;

            /* Allow domain override by providing data attributes, mostly used for testing external component */
            const block = container.querySelector('.mntl-external-component');
            const commerceDomain = block ? block.dataset.commerceDomain : null;
            const commercePathname = block ? block.dataset.commercePathname : null;
            const uuidIndex = block ? parseInt(block.dataset.uuidIndex) : null;
            const newBlocks = registerNewBlocks(container);

            if (Object.keys(newBlocks).length > 0) {
                getRetailerInformation(commerceDomain, commercePathname)
                    .then((data) => {
                        processSections(data, newBlocks, uuidIndex);
                        getAffiliateLinks(container);
                    });
            }
        }

        /*
            When initialized, on load or deferred, loops over commerce sections and adds the new blocks to the commerceBlock object
            @param {*} container - The DOM object - body or deferred block
        */
        function registerNewBlocks(container) {
            const containerSections = container.getElementsByClassName(targets.extendedCommerceContainer);
            let section;
            let sectionBlocks;
            const newBlocks = {};

            for (let i = 0; i < containerSections.length; i++) {
                section = containerSections[i];
                sectionBlocks = section.getElementsByClassName(targets.extendedCommerceBlock);

                // confirm that the new section has blocks, and that they haven't been processed already
                if (sectionBlocks.length > 0 && (processedBlocks.indexOf(section) < 0)) {
                    newBlocks[section.id] = {};
                    newBlocks[section.id].clickCategory = section.getAttribute('data-click-category') || defaultClickCategory;
                    newBlocks[section.id].clickEvent = section.getAttribute('data-click-event') || defaultClickEvent;
                    newBlocks[section.id].blocks = sectionBlocks;
                    window.Mntl.ExtCommerceConfig.retailerLimit = Math.max(window.Mntl.ExtCommerceConfig.retailerLimit, section.getAttribute('data-retailer-limit'));
                }
            }

            return newBlocks;
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
         * @param {string} domain
         * @param {string} pathname
         */
        function getRetailerInformation(domain, pathname) {
            const params = window.Mntl.utilities.getQueryParams();

            const blockDomain = domain || window.Mntl.utilities.getDomain();
            const urlParamParts = ['et', 'state', 'priorityRetailerDomains', 'safelistRetailerDomains', 'limit', 'filterOOS'];

            const pathName = pathname || window.location.pathname;
            let url;

            if (window.Mntl.ExtCommerceConfig.priorityDomains) {
                params.priorityRetailerDomains = window.Mntl.ExtCommerceConfig.priorityDomains.toString();
            }

            if (window.Mntl.ExtCommerceConfig.safelistDomains) {
                params.safelistRetailerDomains = window.Mntl.ExtCommerceConfig.safelistDomains.toString();
            }

            if (window.Mntl.ExtCommerceConfig.retailerLimit) {
                params.limit = window.Mntl.ExtCommerceConfig.retailerLimit;
            }

            if (window.Mntl.ExtCommerceConfig.filterOOS === false) {
                params.filterOOS = window.Mntl.ExtCommerceConfig.filterOOS;
            }

            url = [
                '/servemodel/model.json?modelId=gatherListOfListRetailers&url=',
                window.location.protocol,
                window.Mntl.utilities.hasWwwSubdomain() ? '//www.' : '//',
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
                            document.dispatchEvent(new CustomEvent('commerce-info-loaded', [data]));
                            resolve(data);
                        })
                        .catch((err) => {
                            window.debug.error('Error retrieving commerce data from the server: ', err);
                            reject(err);
                        });
                } else {
                    reject('improperly configured external component: missing path override');
                }
            }));
        }

        // loop through new block sections and update them with the dataset
        /*
            Loops through the new block sections and updates them using the dataset.
            @params {Object} data - Object containing retailer information data.
            @params {Object} newBlocks - Newly created blocks from the registerNewBlocks function
            @params {Number} extIdx - Index of product in the doc if viewing external PRB on EC
        */
        function processSections(data, newBlocks, extIdx) {
            if (Number.isFinite(extIdx)) {
                data = data.splice(extIdx, 1);
            }

            Object.keys(newBlocks).forEach((section) => {
                data.forEach((commerceData, blockIndex) => {
                    if (blockIndex < newBlocks[section].blocks.length) {
                        updateCommerceItems(commerceData, newBlocks[section], blockIndex);
                    }
                });
                processedBlocks.push(section);
            });
        }

        /**
         *
         * @param {Object}  commerceData    collection of commerce data for the given item
         * @param {Object}  sectionData     data from the applicable extended-commerce__section
         * @param {integer} blockIndex      index of the commerce item from the list of items
         */
        function updateCommerceItems(commerceData, sectionData, blockIndex) {
            const commerceBlock = sectionData.blocks[blockIndex];

            const commerceHeader = commerceBlock.getElementsByClassName(targets.header)[0];
            const commerceButtons = commerceBlock.getElementsByClassName(targets.button);
            const commerceImage = commerceBlock.getElementsByClassName(targets.image)[0];
            const commerceJumpLink = commerceBlock.getElementsByClassName(targets.jumpLink)[0];

            const useExternalImage = commerceBlock.dataset.useExternalImage || true;

            // generate a list of button/data pairs
            Array.prototype.slice.call(commerceButtons).forEach((commerceButton, i) => {
                updateButton(commerceButton, commerceData[i], sectionData);
            });

            // if we have image data place the image, add event handlers
            if (commerceImage && (useExternalImage === true)) {
                updateImage(commerceImage, commerceData, commerceButtons[0], sectionData);
            }

            // get the header and add event handlers
            if (commerceHeader) {
                updateHeader(commerceHeader, commerceButtons[0], sectionData);
            }

            // get the jumplink and add event handlers
            if (commerceJumpLink) {
                updateJumpLink(commerceJumpLink, sectionData);
            }
        }

        /**
         *
         * @param  {Element} button         given commerce button to update
         * @param  {Object}  item           commerce information for a given item
         * @param  {object}  sectionData    data from the applicable extended-commerce__section
         */
        function updateButton(button, item, sectionData) {
            button.dataset.clickTracked = false; // Disable the default click tracking for the buttons

            // For non-amazon btns, save the original href (since it can be changed after a click)
            if (!/amazon\.com/.test(button.href)) {
                button.dataset.href = button.href;
            }

            // Attach any commerce button event listeners
            attachButtonEvents(button, sectionData);

            // Insert any price information
            updatePriceInformation(button, item);

            // Read other meta info
            updateSkimlinksAttribution(button, item);

            // Update button url with appropriate affiliate url and fragment
            updateButtonUrl(button, item);

            // ...and run post-processing
            window.Mntl.externalizeLinks.processLink($(button));
        }

        /**
         * Iterates through a list of button event definitions and applies them to the given button
         * @param  {Element} button         given commerce button to update
         * @param  {Object}  sectionData    data from the applicable extended-commerce__section
         */
        function attachButtonEvents(button, sectionData) {
            let events;
            let eventKey;

            // change href on click if click href is present
            button.replaceHref = function () {
                if (this.dataset.clickHref) {
                    this.href = this.dataset.clickHref;
                }
            };

            // Save a reference to the click track handler so we can unbind it later
            button.clickTrackHandler = function () {
                let action = actions.buttonClick;
                const groupedContainer = this.closest('[data-grouping]');

                if (groupedContainer) {
                    action = `${groupedContainer.dataset.grouping} ${actions.buttonClick}`;
                }

                // change href on click if click href is present
                this.replaceHref();
                fireClickTrackingEvent(sectionData.clickEvent, sectionData.clickCategory, action, this.dataset.href || this.href);
            };

            // list of events to attach to each button
            events = {
                click: button.clickTrackHandler,
                contextmenu: button.replaceHref // replaces href on right click
            };

            // Attach event handlers
            for (eventKey in events) {
                button.addEventListener(eventKey, events[eventKey]);
            }
        }

        /**
         * @param {string} event
         * @param {string} category
         * @param {string} action
         * @param {string} label
         */
        function fireClickTrackingEvent(event, category, action, label) {
            fireEvent({
                event,
                eventCategory: category,
                eventAction: action,
                eventLabel: label
            });
        }

        /**
         * if present, configures the button with the best price stored in the commerce model
         * @param  {Element} button given commerce button to update
         * @param  {Object}  item   commerce information for a given item
         */
        function updatePriceInformation(button, item) {
            if (!window.Mntl.ExtCommerceConfig.pricingDisabled && fnUtils.getDeepValue(item, 'commerceModel', 'bestPrice')) {
                button.dataset.commercePrice = item.commerceModel.bestPrice;
            }
        }

        /**
         * if present configures the button with a skimlinks url
         * @param  {Element} button given commerce button to update
         * @param  {Object}  item   commerce information for a given item
         */
        function updateSkimlinksAttribution(button, item) {
            if (fnUtils.getDeepValue(item, 'commerceModel', 'bestPrice') && item.type === 'SKIMLINKS') {
                if (fnUtils.getDeepValue(item, 'commerceModel', 'deeplink')) {
                    if (fnUtils.getDeepValue(window, 'skimlinks_settings', 'skimlinks_tracking')) {
                        button.dataset.clickHref = encodeURI(`${item.commerceModel.deeplink}&xcust=${window.skimlinks_settings.skimlinks_tracking}`);
                    } else {
                        button.dataset.clickHref = item.commerceModel.deeplink;
                    }
                }
            }
        }

        /**
         * Update commerce button with the relevant affiliate url and fragment
         * @param  {Element} button given commerce button to update
         * @param  {Object} item    commerce information for a given item
         */
        function updateButtonUrl(button, item) {
            const href = (fnUtils.getDeepValue(item, 'commerceModel', 'url') || button.getAttribute('href'));
            const fragment = button.getAttribute('data-commerce-fragment');

            updateHref(appendFragment(href, fragment), button);
        }

        /**
         * Append fragment to href if one does not already exist
         * @param  {string} href        url to add fragment to
         * @param  {string} fragment    href with appended fragment
         */
        function appendFragment(href, fragment) {
            if (href !== null && fragment !== null && href.indexOf('#') === -1) {
                return `${href}#${fragment}`;
            }

            return href;
        }

        /**
         * Add click handler to commerce header
         * @param  {Element} header         h# to add click event to
         * @param  {Element} button         button to make target of click
         * @param  {Object}  sectionData    data from the applicable extended-commerce__section
         */
        function updateHeader(header, button, sectionData) {
            let action = actions.headerClick;
            const groupedContainer = button.closest('[data-grouping]');

            if (groupedContainer) {
                action = `${groupedContainer.dataset.grouping} ${actions.headerClick}`;
            }

            if (header !== null) {
                header.classList.add(classes.link);

                header.addEventListener('click', (e) => {
                    handleSurrogateButtonClicks.call(button, e, action, sectionData);
                });
            }
        }

        /**
         * Update or add product image
         * @param  {Element} image          image to target
         * @param  {Array}   items          array of items with commerce information for the given item
         * @param  {Element} button         target commerce button
         * @param  {Object}  sectionData    data from the applicable extended-commerce__section
         */
        function updateImage(image, items, button, sectionData) {
            let action = actions.imageClick;

            const groupedContainer = button.closest('[data-grouping]');
            const amazonItem = items.find((item) => item.type === 'AMAZON');
            const src = fnUtils.getDeepValue(amazonItem, 'commerceModel', 'imageUrl');

            if (groupedContainer) {
                action = `${groupedContainer.dataset.grouping} ${actions.imageClick}`;
            }

            if (src && image.classList.contains(targets.defaultImage)) {
                image.setAttribute('src', src);
                image.removeAttribute('srcset');
                image.classList.remove(targets.defaultImage);
            }

            image.classList.add(classes.link);
            image.addEventListener('click', (e) => {
                handleSurrogateButtonClicks.call(button, e, action, sectionData);
            });
        }

        /**
         * Adds click tracking for jump-links
         * @param {Element} jumpLink        jumplink element
         * @param {Object}  sectionData     data from the applicable extended-commerce__section
         */
        function updateJumpLink(jumpLink, sectionData) {
            let action = actions.jumpLinkClick;
            const urlHash = jumpLink.getAttribute('href');
            const groupedContainer = jumpLink.closest('[data-grouping]');

            if (groupedContainer) {
                action = `${groupedContainer.dataset.grouping} ${actions.jumpLinkClick}`;
            }

            jumpLink.addEventListener('click', () => {
                fireClickTrackingEvent(sectionData.clickEvent, sectionData.clickCategory, action, urlHash);
            });
        }

        /**
         * Handle click on arbitary elements. Function should only be applied with
         * .bind so it can reference the actual button to act on from the element
         * on which the click handler is/was assigned
         * @param {Event} event
         * @param {string} eventAction
         */
        function handleSurrogateButtonClicks(event, eventAction, sectionData) {
            // If the clicked element is an anchor tag, respect the tag url.
            if (event.target.tagName === 'A') {
                return;
            }

            // Unbind the click handler from the commerce button so the click tracking event isn't fired twice
            this.removeEventListener('click', this.clickTrackHandler);

            // change href on click if click href is present
            this.replaceHref();

            // CMRC-349 //
            // For some unholy reason, skimlinks doesn't pick up the surrogate button
            // click if it happens sooner than 200ms after original click.
            setTimeout(() => {
                // Fire surrogate click event
                this.dispatchEvent(new MouseEvent('click', { 'cancelable': true }));

                // Fire our custom click tracking event
                fireClickTrackingEvent(sectionData.clickEvent, sectionData.clickCategory, eventAction, this.href);

                // Rebind the click handler
                this.addEventListener('click', this.clickTrackHandler);
            }, 250);
        }

        return { init };
    })(window.jQuery || {}, window.Mntl.utilities || {}, window.Mntl.domUtilities || {}, window.Mntl.fnUtilities || {});
}

window.Mntl.utilities.readyAndDeferred(window.Mntl.ExtCommerce.init);
