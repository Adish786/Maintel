window.Mntl = window.Mntl || {};

Mntl.Deferred = (function($) {
    const batch = {
        timer: null,
        components: [],
        pauseRequests: false
    }
    const config = {
        // Set windowHeight on the Mntl.deferred.configs object to reuse
        windowHeight: window.innerHeight
    };
    const resources = {
        loadedCSS: null,
        loadedJS: null,
        loadedSVG: null
    }

    const btf = [];
    const requestedComponents = [];
    let componentIdSuffix = 1; // this starts at 1 because component id suffix defaults to '-0'
    let isScrollListenerSet = false;
    let cachedDOMParser; // only assigned if needed

    function setBtfVals() {
        btf.forEach((component) => {
            component.pos = component.$component.offset().top;
        });
    }

    // --- Mntl.Deferred Batching Logic Starts ---
    // TODO: Consider splitting this out into a seperate file that gets imported here.
    /**
     * Helper function to get the unsuffexed Id of a component block
     * (removing the last dash and number e.g. component-1-0-1 goes to component-1)
     * 
     * TODO: Consider moving this to a utility file for dom parsing?
     * 
     * @param {string} id 
     * @returns {string} unsuffexed Id
     */
    function getUnsuffixedId(id) {
        return (typeof id === 'string' ? id.replace(/(_[0-9]+)(-[0-9]+)?(-[0-9]+)?$/, '$1') : '');
    }

    // TODO: Need to refactor this so that parts of this code is testable per deferred.spec.js notes
    function batchTimerCallback() {
        let deferParams = {};
        const contextComponents = {};
        let context;
        const componentCounts = {};
        const forceCisComponents = [];

        window.clearTimeout(batch.timer);

        if (batch.components.length > 0) {
            batch.pauseRequests = true;
            batch.components.forEach(($component) => {
                const id = $component.attr('id') || '';
                let unsuffixedId = getUnsuffixedId(id);
                let deferParam = $component.data('defer-params');
                let componentId;

                if (deferParam) {
                    if (typeof deferParam === 'string') {
                        deferParam = JSON.parse(deferParam);
                    }
                    // On every loop deferParams accumulates the params from this
                    // and all previous loops. Limitation as we have to batch all components in 
                    // each call even though it seems incorrect.
                    deferParams = $.extend({}, deferParams, deferParam);
                }

                // if defer div doesn't have Globe-generated id, give it one
                // TODO: Split this into a new function
                if (!id) {
                    componentId = $component.data('type');
                    componentCounts[componentId] = (componentId in componentCounts) ? (componentCounts[componentId] + 1) : 1;
                    $component.addClass(componentId);
                    unsuffixedId = `${componentId}_${componentCounts[componentId]}`;
                    $component.attr('id', `${unsuffixedId}-${componentIdSuffix}`);
                    forceCisComponents.push(unsuffixedId);
                }
                contextComponents[unsuffixedId] = $component;
            });

            context = new Mntl.Deferred.RequestContext(contextComponents, null, null, null, null, forceCisComponents);
            Mntl.Deferred.requestComponents(context, deferParams);
            debug.log(`batchTimer - deferring ${batch.components.join(', ')}`);
            batch.components = [];
        }
    }

    /**
     * Helper function to add components to the batch, or delay adding to the batch to process
     * the deferred components for loading
     * 
     * @param {array} components array of elements that represent the components of interest
     */
    function addToBatch(component) {
        debug.log(`pause requests is ${batch.pauseRequests.toString()}`);

        // Block any requests coming in if there is a request already being made
        if (batch.pauseRequests) {
            $(document).one('defer-batch-complete', () => {
                Mntl.Deferred.addToBatch(component);
            });
        } else {
            if (batch.timer) {
                window.clearTimeout(batch.timer);
            }

            batch.components = batch.components.concat(component);
            batch.timer = window.setTimeout(batchTimerCallback, 20);
        }
    }
    // --- Mntl.Deferred Batching Logic Ends ---

    function _normalizeResourcePath(resourcePath) {
        return resourcePath.replace(/^\/[^\/]+\/[^\/]+/, '');
    }

    function _updateLoadedResourceMap(resourceMap, additions) {
        additions.forEach((resource) => {
            resourceMap[_normalizeResourcePath(resource)] = '';
        });
    }

    /**
     * Helper function that handles inlineJS and evals them
     * as we parse through the array of scripts
     * 
     * @param {array} scripts 
     */
    function handleInlineJS(scripts) {
        scripts.forEach((js) => {
            window.eval(js);
        })
    }

    function _stripHash(url) {
        const hashIndex = url.indexOf('#');
        let _url = url;

        if (hashIndex > 0) {
            _url = url.substring(0, hashIndex);
        }

        return _url;
    }

    function _getScripts(scripts, callback) {
        let progress = 0;
        const { length } = scripts;

        function internalCallback() {
            if (++progress === length) {
                callback();
            }
        }

        if (length > 0) {
            const scriptPromises = [];

            scripts.forEach((script) => {
                const scriptUrl = _stripHash(script);
                const errorMsg = `Failed to request deferred script at ${script}`;

                scriptPromises.push(Mntl.utilities.ajaxPromiseGetCall(scriptUrl, errorMsg, 60000));
            });

            Promise.all(scriptPromises)
                .then((scripts) => { 
                    scripts.forEach((js) => {
                        // Create an inline script to guarentee synchronous loading
                        // As all dynamic script loading from ajax calls are async
                        // and we can run into ordering issues (e.g. vue bugs for example)
                        const scriptElement = document.createElement("script");

                        scriptElement.type = 'text/javascript';
                        scriptElement.text = js;
                        document.head.appendChild(scriptElement);
                        internalCallback();
                    })
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            callback();
        }
    }

    /**
     * Add style rules to style tag
     * @param       {String} css
     * @return      {Undefined}
     */
    function handleInlineCSS(css) {
        if (css) {
            document.querySelector('STYLE').innerHTML += css;
        }
    }

    /**
     * Add named symbols to SVG tag
     * @param   {String} svg
     * @return  {Undefined}
     */
    function handleInlineSVG(svg) {
        let svgDocument;
        let svgWrapper;
        let svgDefinition;

        if (svg) {
            // first we need to convert our string to real DOM elements
            // we can't use SVGElement.prototype.innerHTML because it does not work on IE11
            cachedDOMParser = cachedDOMParser || new DOMParser();
            svgDocument = cachedDOMParser.parseFromString(
                // we need to have a root element with namespace set properly or else the parsed elements will be
                // type Element instead of SVG*Element which is what we need to reference the svg via use element
                `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">${svg}</svg>`,
                'image/svg+xml');
            if (svgDocument) {
                svgDefinition = document.querySelector('svg.mntl-svg-resource > defs');
                if (!svgDefinition) {
                    svgWrapper = document.createElement('svg');
                    svgWrapper.addClass('mntl-svg-resource');
                    svgDefinition = document.createElement('defs');
                    svgWrapper.appendChild(svgDefinition);
                    document.body.insertBefore(svgWrapper, document.body.firstChild);
                }

                while (svgDocument.documentElement.firstChild) {
                    // adopt all the children of the wrapping root element (above)
                    svgDefinition.appendChild(document.adoptNode(svgDocument.documentElement.firstChild));
                }
            }
        }
    }

    function _loadComponent(componentId, componentData, allInlineScripts, htmlCallback, $component) {
        const { 
            inlineScripts, 
            inlineStylesheets 
        } = componentData;

        // insert inline CSS
        if (inlineStylesheets) {
            inlineStylesheets.forEach((style) => {
                // Server constraints require an empty object to be appended to this list, so must ignore last item via this check
                if (style && style.stylesheet) {
                    debug.log(`Inline style: ${style.stylesheet}`);
                    handleInlineCSS(style.content);
                }
            });
        }

        if (inlineScripts) {
            inlineScripts.forEach((script) => {
                // Server constraints require an empty object to be appended to this list, so must ignore last item via this check
                if (script && script.script) {
                    debug.log(`Inline script: ${script.script}`);
                    allInlineScripts.push(script.content);
                }
            })
        }

        // call html overload if passed; otherwise append html
        if (htmlCallback) {
            htmlCallback(componentId, componentData.html);
        } else {
            $component.html(componentData.html);
        }
    }

    /**
     * Loop over style sheet list and load. Fire callback when all are loaded.
     * @param       {Array}   stylesheets [src strings]
     * @param       {Function} callback
     * @return      {Undefined}
     */
    function _getExternalCSS(stylesheets, callback) {
        let progress = 0;
        const { length } = stylesheets;

        function internalCallback() {
            if (++progress === length) {
                callback();
            }
        }

        if (length > 0) {
            stylesheets.forEach((css) => {
                const styleSheetElement = document.querySelector(`link[href="${css}"]`);

                // Check if stylesheet is on page already
                if (styleSheetElement) {
                    internalCallback();
                } else {
                    Mntl.utilities.loadStyleSheet(css, internalCallback);
                }
            });
        } else {
            callback();
        }
    }

    // TODO: Need to refactor this so that parts of this code is testable per deferred.spec.js notes
    function _successCallback(data, htmlCallback, componentIdMap) {
        try {
            const jsonData = JSON.parse(data);
            const { 
                externalScripts, 
                externalStylesheets, 
                inlineSVGs 
            } = jsonData;
            const $components = [];
            let allInlineScripts = [];
            let $component;

            // Load external styles, continue with loading deferred component once those have loaded
            _getExternalCSS(externalStylesheets, () => {
                let keys;

                // insert inline SVGs
                if (inlineSVGs && inlineSVGs.length > 0) {
                    inlineSVGs.forEach((inlineSVG) => {
                        debug.log(`Inline svg: ${inlineSVG}`);
                        handleInlineSVG(inlineSVG.content);
                        _updateLoadedResourceMap(resources.loadedSVG, [inlineSVG.svg]);
                    });
                }

                for (const key in jsonData) {
                    if (jsonData.hasOwnProperty(key) && key !== 'externalScripts' && key !== 'externalStylesheets' && key !== 'inlineSVGs') {
                        if (componentIdMap[key] && componentIdMap[key] !== null) {
                            $component = componentIdMap[key];
                        } else {
                            keys = Object.keys(componentIdMap);
                            for (let k = 0; k < keys.length; k++) {
                                if (key.indexOf(keys[k]) === 0 && componentIdMap[keys[k]] !== null) {
                                    $component = componentIdMap[keys[k]];
                                    break;
                                }
                            }
                        }

                        if (!$component) {
                            $component = $(`#${key}`);
                        }

                        _loadComponent(key, jsonData[key], allInlineScripts, htmlCallback, $component);

                        if (htmlCallback && $component.length === 0) {
                            $component = $(`#${key}`);
                        }

                        $components.push($component);
                        $component = null;
                    }
                }

                _updateLoadedResourceMap(resources.loadedCSS, externalStylesheets);

                // Load all external scripts
                _getScripts(externalScripts, () => {
                    handleInlineJS(allInlineScripts);
                    allInlineScripts = [];

                    for (let i = 0; i < $components.length; i++) {
                        // TODO: instead of $components use the vanilla JS variable when we do jquery cleanup in part 3-5
                        $components[i][0].classList.remove('defer-hidden')
                        $components[i][0].dispatchEvent(new CustomEvent('deferred-loaded'));
                        debug.log('triggering load for ', $components[i]);
                    }

                    _updateLoadedResourceMap(resources.loadedJS, externalScripts);

                    // Re-calc btf vals in case relative positioning has changed
                    Mntl.Deferred.setBtfVals();
                    batch.pauseRequests = false;
                    $(document).trigger('defer-batch-complete', [$components]);
                });
            });
        } catch (error) {
            console.error('Unexpected response for deferred component API: ', error);
        }
    }

    // TODO: Should this be moved to utilities.js?
    function appendPath(url, path) {
        if (path) {
            if (url.indexOf('?') > -1) {
                return `${url}&${path}`;
            }

            return `${url}?${path}`;

        }

        return url;

    }

    // Preserve any query parameters in the current url
    function getQueryParams() {
        const RESERVED_QUERY_PARAMS = ['et'];
        const path = window.location.search.substring(1);
        const queryParams = {};
        let pathParts;
        let i;
        let pair;

        if (path) {
            pathParts = path.split('&');

            for (i = 0; i < pathParts.length; i++) {
                pair = pathParts[i].split('=');
                if (pair[0].indexOf('globe') === 0 || RESERVED_QUERY_PARAMS.indexOf(pair[0]) !== -1) {
                    queryParams[pair[0]] = (pair[1] !== null && typeof pair[1] !== 'undefined' ? pair[1] : '');
                }
            }

            return queryParams;
        }

        return {};
    }

    function overloadDeferredRequestUrl(url, path) {
        // Set default request URL to go to current location at subdomain (j, www, etc)
        const DEFAULT_REQUEST_URL = Mntl.domUtilities.getResourceRootUrl() + window.location.pathname;
        // TODO: should the host chnage to a resourceDomain (eg j. or something else -- maybe vertical specific?)?
        // Include encoded url path to query string
        const requestUrl = url || DEFAULT_REQUEST_URL;
        let requestPath;

        url = url || window.location.href;
        requestPath = path ? `${path}&` : '';
        requestPath += `url=${encodeURIComponent(url)}`;

        return appendPath(requestUrl, requestPath);
    }

    function RequestContext(componentIds, urlOverload, skipGenerify, htmlCallback, isPageview, forceCisComponents) {
        this.componentIds = {};
        this.forceCisComponents = forceCisComponents || [];
        this.urlOverload = urlOverload || null;
        this.skipGenerify = skipGenerify || false;
        this.htmlCallback = htmlCallback || null;
        this.isPageview = isPageview || false;

        this.setComponentIds(componentIds);
    }

    /*
     *  @param components should look like
     *  {
     *      componentRequested: domNode,
     *      componentRequested: domNode,
     *      ...
     *  }
     *
     *    domNode is optional
     *
     *  If only a string of a component id (eg "article")
     *  or an array of component ids (eg ["article", "leaderboard"]
     *  are requested, they are re-mapped to expected input value described above
     *
     * */
    RequestContext.prototype.setComponentIds = function(components) {
        const self = this;
        const typeString = typeof components;
        const componentMap = {};
        let i = 0;

        if (typeString !== 'undefined') {
            if (Array.isArray(components)) {
                for (i; i < components.length; i++) {
                    componentMap[components[i]] = null;
                }
            } else if (typeString === 'string') {
                const cs = components.split(',');

                for (i; i < cs.length; i++) {
                    componentMap[cs[i].trim()] = null;
                }
            } else if (typeString === 'object') {
                Mntl.fnUtilities.iterate(components, (comps, key) => {
                    componentMap[key] = comps[key] instanceof $ ? comps[key] : $(`#${comps[key].id}`);

                    // TODO: Replace to vanilla js in round 3-5 jQuery Cleanup
                    // We cannot remove this as the request context auto wraps things into jQuery which impacts components using readyAndDeferred
                    // componentMap[key] = component[key].jquery ? component[key][0] : document.querySelector(`#${component[key].id}`);
                });
            }
            self.componentIds = componentMap;
        }
    };

    /**
     * Helper function that determines the css resources and loads them into the resources object
     * for fetching
     * 
     * @returns {undefined}
     */
    function _getLoadedCss() {
        if (resources.loadedCSS === null) {
            const styles = document.querySelectorAll('link[data-glb-css]');

            resources.loadedCSS = {};
            styles.forEach((style) => {
                const href = style.getAttribute('href');

                if (typeof href === 'string') {
                    resources.loadedCSS[_normalizeResourcePath(href)] = '';
                }
            });
        }

        return Object.keys(resources.loadedCSS);
    }

    /**
     * Helper function that determines the js resources and loads them into the resources object
     * for fetching
     * 
     * @returns {undefined}
     */
    function _getLoadedJs() {
        if (resources.loadedJS === null) {
            const scripts = document.querySelectorAll('script[data-glb-js]');

            resources.loadedJS = {};
            scripts.forEach((script) => {
                const src = script.getAttribute('src');

                if (typeof src === 'string') {
                    resources.loadedJS[_normalizeResourcePath(src)] = '';
                }
            });
        }

        return Object.keys(resources.loadedJS);
    }

    /**
     * Helper function that determines the svg resources and loads them into the resources object
     * for fetching
     * 
     * @returns {undefined}
     */
    function _getLoadedSvg() {
        if (resources.loadedSVG === null) {
            const svgSymbols = document.querySelectorAll('svg.mntl-svg-resource > defs > symbol[id]');

            resources.loadedSVG = {};
            svgSymbols.forEach((svg) => {
                const id = svg.getAttribute('id');

                resources.loadedSVG[id] = '';
            });
        }

        return Object.keys(resources.loadedSVG);
    }

    function requestComponents(context, deferParams = {}) {
        const componentIds = Object.keys(context.componentIds).join();
        const formData = {
            globeDeferVersion: '2',
            cr: componentIds,
            loaded_cr: requestedComponents.join(','), // eslint-disable-line camelcase
            cis: componentIdSuffix++,
            force_cis: context.forceCisComponents.join(','), // eslint-disable-line camelcase
            pv: context.isPageView
        };
        const traditionalData = {
            loaded_css: _getLoadedCss(), // eslint-disable-line camelcase
            loaded_js: _getLoadedJs(), // eslint-disable-line camelcase
            loaded_svg: _getLoadedSvg() // eslint-disable-line camelcase
        }

        Object.assign(formData, getQueryParams(), deferParams);

        if (context.urlOverload && context.skipGenerify) {
            context.urlOverload = _stripHash(context.urlOverload);
        }

        let url = (context.skipGenerify && context.urlOverload) ? context.urlOverload : overloadDeferredRequestUrl(context.urlOverload);

        url = _stripHash(url);

        // Make note of components that have been requested to prevent double requesting resources on subsequent requests
        Mntl.fnUtilities.iterate(context.componentIds, (components, key) => {
            if (requestedComponents.indexOf(key) === -1) {
                requestedComponents.push(key);
            }
        });


        Mntl.utilities.ajaxPromisePost(url, formData, 30000, traditionalData, true)
            .then((data) => _successCallback(data, context.htmlCallback, context.componentIds))
            .catch((error) => console.error('Error requesting deferred components with: ', error));

    }

    function cleanupEvents(elem, event, fn) {
        // TODO: swap elem to pass in the non jquery form directly
        elem[0].addEventListener('deferred-loaded', () => {
            elem[0].removeEventListener(event, fn);
        }, { once: true });
    }

    function _loadComponentsOnScroll(e) {
        btf.forEach((el) => {
            if (!el.$component.length) return;
            if (el.rendered) return;

            const defaultOffset = 300;
            const offset = el.customOffset || defaultOffset;
            const isScrollNearElement = (el.scrollTop && e.detail.scrollTop >= el.scrollTop) || (e.detail.scrollTop + Mntl.Deferred.config.windowHeight + offset >= el.pos);

            if (!isScrollNearElement) return;

            Mntl.Deferred.addToBatch(el.$component);
            el.rendered = true;

            const isAllElementsRendered = !btf.filter((el) => !el.rendered).length;

            if (isAllElementsRendered) window.removeEventListener('mntl.scroll', _loadComponentsOnScroll);
        })
    };

    function deferScrolledComponents() {
        if (!btf.length) return;
        if (isScrollListenerSet) return;

        btf.sort((a, b) => a.pos - b.pos);

        window.addEventListener('mntl.scroll', _loadComponentsOnScroll);
        isScrollListenerSet = true;
    }

    function customDeferredEventListener(e) {
        const $component = $(e.target); // TODO: Transition to non $ component in cleanup

        Mntl.Deferred.addToBatch($component);
    }

    /**
     * Helper function to determine the viewport bottom of the window to be used
     * for comparisons with deferred components on scroll
     * 
     * @param {int} offset defaults to 200 (units in px but calculated as an int)
     * @returns 
     */
    function calculateViewportBottom(offset = 200) {
        const windowPostion = window.scrollY;
        const { windowHeight } = Mntl.Deferred.config;

        return (windowPostion + windowHeight + offset);
    }

    function init(container) {
        const DEFERRED_SELECTOR = '[data-defer]';
        const components = [];
        const viewportBottom = calculateViewportBottom();
        const $container = container.jquery ? container : $(container);

        // Find all deferred placeholders
        // and add 1) ATF scroll items and all other defertypes to array
        //         2) BTF scroll items added to separate array to attach listener to scroll to load when coming to view
        $container.find(DEFERRED_SELECTOR).each((index, elem) => {
            const $component = $(elem); // TODO: Temp variable to start the transition away from jquery
            const component = elem; // TODO: Transition to non $ component in cleanup
            const deferType = component.dataset.defer || 'none';
            const pos = $component.offset().top; // TODO: Transition to non $ component in cleanup
            let btfEntry = {};

            // For scroll elements, add ids for above-the-fold items to array
            if (deferType === 'scroll') {
                if (viewportBottom >= pos) {
                    components.push($component);
                } else {
                    const deferOffset = component.dataset.scrollDeferOffset;
                    const customOffset = deferOffset ? parseInt(deferOffset, 10) : false;
                    const scrollTop = component.dataset.scrollDeferScrolltop || false;

                    $component.addClass('defer-hidden'); // TODO: Transition to non $ component in cleanup
                    btfEntry = {
                        $component, // TODO: Transition to non $ component in cleanup
                        pos,
                        rendered: false,
                        customOffset,
                        scrollTop
                    };

                    btf.push(btfEntry);
                }
            } else if (deferType === 'load') {
                // Add on-load deferred ids to array
                components.push($component);
            } else {
                // TODO: Replace to vanilla js in round 3-5 jQuery Cleanup
                // We cannot remove this as the request context auto wraps things into jQuery which impacts components using readyAndDeferred
                // element.addEventListener(deferType, customDeferredEventListener);

                // Every other data-defer type should just load on data-defer event fire
                $(document).on(deferType, `[data-defer=${deferType}]`, customDeferredEventListener); // Deprecated

                // TODO: mntl-name used to start the transition to vanilla JS in non breaking changes.
                // Final clean up here when all components are ok
                // or in the next breaking changes
                const deferTypeEventName = `mntl-${deferType}`;

                document.querySelectorAll(`[data-defer=${deferType}]`).forEach((deferElements) => {
                    deferElements.addEventListener(deferTypeEventName, customDeferredEventListener)
                });

                cleanupEvents($component, deferType, customDeferredEventListener); // Deprecated
            }
        });
        if (components.length > 0) {
            Mntl.Deferred.addToBatch(components);
        }
        deferScrolledComponents();
    }

    function removeEventListener(context, event, component, fn) {
        $(context).off(event, component, fn);
    }

    // Set up to re-calc this height on a throttled resize
    window.addEventListener('resize', Mntl.throttle(() => {
        Mntl.Deferred.config.windowHeight = window.innerHeight;
    }, 50));

    // Recompute deferred component positions on content resize
    document.addEventListener('mntl.contentResize', () => {
        Mntl.Deferred.setBtfVals();
    });

    return {
        calculateViewportBottom,
        config,
        getUnsuffixedId,
        handleInlineCSS,
        handleInlineJS,
        handleInlineSVG,
        init,
        RequestContext,
        requestComponents,
        addToBatch,
        setBtfVals,
        removeEventListener
    };
}(window.jQuery || {}));

Mntl.utilities.readyAndDeferred(Mntl.Deferred.init);
Mntl.utilities.ready(Mntl.Deferred.init.bind(null, document.head));
