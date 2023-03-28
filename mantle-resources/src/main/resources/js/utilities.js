/* eslint no-use-before-define: 0 */
(function(window) {
    const mntlUtilities = (function() {
        window.Mntl = window.Mntl || {};

        // Vue specific properties
        window.Mntl.VueInGlobe = window.Mntl.VueInGlobe || {};
        window.Mntl.VueInGlobe.apps = window.Mntl.VueInGlobe.apps || {};
        window.Mntl.VueInGlobe.comps = window.Mntl.VueInGlobe.comps || {};

        // if ?jsdebug=true is passed as a param, debug mode.
        (function() {
            function __blank() {} // eslint-disable-line no-empty-function

            if (!window.debug) {
                window.Mntl.DEBUG = location.search.indexOf('jsdebug=true') !== -1 ? true : false;
                if (window.Mntl.DEBUG && window.console) { // && console.warn && console.error
                    window.debug = {
                        log: window.console.log.bind(window.console, 'Mntl: %s'), // '%s, %s'
                        error: window.console.error.bind(window.console, 'Mntl: error: %s'),
                        info: window.console.info.bind(window.console, 'Mntl: info: %s'),
                        warn: window.console.warn.bind(window.console, 'Mntl: warn: %s')
                    };
                } else {
                    window.debug = { // Call blank function.
                        log: __blank,
                        error: __blank,
                        info: __blank,
                        warn: __blank
                    };
                }
                window.debug.info('debug loaded from Mantle utilities.js');
            }
        }());


        /**
         * ajaxPromiseGetCall is a generic get function that takes in a url and error message
         * and submits a get request. This returns a promise that can be resolved or rejected
         * as needed
         *
         * @param       {String}  url endpoint
         * @param       {String}  errorMsg you want to give out if the endpoint errors out
         * @param       {number} timeout in milliseconds
         */
        function ajaxPromiseGetCall(url, errorMsg, timeoutLength) {
            const webRequestPromise = new Promise((resolve, reject) => {
                const httpRequest = new XMLHttpRequest();

                httpRequest.open('GET', url);

                // GLBE-9646 - We set a minimum of 1 second as network round trip time of under 1 seconds to mantle from a site starts being unstable
                // This also makes this non breaking for verticals that set timeoutLengths very small when the bug was identified in GLBE-9645
                // Breaking changes only seen on the get call
                if(timeoutLength && timeoutLength >= 1000) {
                    httpRequest.timeout = timeoutLength;
                    httpRequest.ontimeout = () => {
                        reject('Ajax GET call timed out');
                    };
                }

                httpRequest.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

                httpRequest.onload = function() {
                    if (httpRequest.status === 200) {
                        resolve(httpRequest.response);
                    } else {
                        reject(httpRequest.statusText);
                    }
                };

                httpRequest.onerror = function() {
                    reject(errorMsg);
                };

                httpRequest.send();
            });

            return webRequestPromise;
        }

        /**
         * ajaxPromisePostCall is a generic get function that takes in a url and error message
         * and submits a get request. This returns a promise that can be resolved or rejected
         * as needed
         *
         * @param       {String}  url endpoint
         * @param       {Object}  data you want to give to be submitted in form data
         * @param       {number} timeout in milliseconds
         * @param       {Object of Arrays} traditionalData you want to submit in the form (if you want repeating keys for different values)
         */
        function ajaxPromisePost(url, data, timeoutLength, traditionalData = {}, urlEncode=false) {
            const webRequestPromise = new Promise((resolve, reject) => {
                const httpRequest = new XMLHttpRequest();

                httpRequest.open('POST', url);

                if (timeoutLength) {
                    httpRequest.timeout = timeoutLength;
                    httpRequest.ontimeout = () => {
                        reject('Ajax POST call timed out');
                    };
                }

                httpRequest.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

                httpRequest.onload = function() {
                    if (httpRequest.status === 200) {
                        resolve(httpRequest.response);
                    } else {
                        reject(httpRequest.statusText);
                    }
                };

                httpRequest.onerror = function() {
                    reject(`Error getting data at ${url}`);
                };

                if (data instanceof FormData) {
                    data.append('CSRFToken', window.Mntl.csrf() || '');

                    httpRequest.send(data);
                } else {
                    const formData = new FormData();

                    Object.keys(data).forEach((key) => {
                        formData.append(key, data[key]);
                    });

                    Object.keys(traditionalData).forEach((key) => {
                        traditionalData[key].forEach((data) => {
                            formData.append(key, data);

                        })
                    });

                    formData.append('CSRFToken', window.Mntl.csrf() || 'INVALID');

                    // For deferred endpoint and other endpoint the expected output is a url encoded payload
                    // which breaks regression. By default formData passes in a multipart-formdata format which works in serving assets
                    // but these two follow up tickets were made to follow up and allow us to always serve multipart-formdata in our POST
                    // requests. When we are done we can delete the block of code needed for this check and any code that uses this branching path
                    // can be refactored
                    //
                    // https://dotdash.atlassian.net/browse/GLBE-9380 and https://dotdash.atlassian.net/browse/GLBE-9381
                    if(urlEncode) {
                        const urlEncodedData = new URLSearchParams(formData);

                        httpRequest.setRequestHeader('Content-Type' , 'application/x-www-form-urlencoded');
                        httpRequest.send(urlEncodedData);
                    } else {
                        httpRequest.send(formData);
                    }
                }
            });

            return webRequestPromise;
        }

        /**
         * ajaxPromisePut is a generic put function that takes in a url, data, and content type
         * and submits a put request. This returns a promise that can be resolved or rejected
         * as needed
         *
         * @param       {String}  url endpoint
         * @param       {Object}  data you want to give
         * @param       {String}  contentType value for request header
         * @param       {Number}  timeoutLength in milliseconds
        */
        function ajaxPromisePut(url, data, contentType, timeoutLength) {
            const webRequestPromise = new Promise((resolve, reject) => {
                const httpRequest = new XMLHttpRequest();

                httpRequest.open('PUT', url);

                if (timeoutLength) {
                    httpRequest.timeout = timeoutLength;
                    httpRequest.ontimeout = () => {
                        reject('Ajax PUT call timed out');
                    };
                }

                if (contentType) {
                    httpRequest.setRequestHeader('Content-Type', contentType);
                }

                httpRequest.onload = () => {
                    if (httpRequest.status === 200) {
                        resolve(httpRequest.response);
                    } else {
                        reject(httpRequest.statusText);
                    }
                };

                httpRequest.onerror = () => {
                    reject(`Error getting data at ${url}`);
                };

                httpRequest.send(data);
            });

            return webRequestPromise;
        }

        /**
         * Gets domain from window.location.hostname
         * @return {String} as verywell.com
         */
        function getDomain() {
            const hostname = window.location.hostname;

            return hostname.substring(hostname.lastIndexOf('.', hostname.lastIndexOf('.') - 1) + 1);
        }

        /**
         * VanillaJS version of jQuery's .parent() when using a given selector
         * @param {DOM element node} element
         * @param {string} selector (e.g. .myFavouriteClass)
         */
        function getClosestMatchingParent(element, selector) {
            let currentElement = element.parentNode;

            while (currentElement !== document) {
                if (currentElement.matches(selector)) {
                    return currentElement;
                }

                currentElement = currentElement.parentNode;
            }

            return null;
        }

        /**
         * Parse the supplied queryString or if no argument is supplied parses
         * window.location.search. Flattens arrays to their first value.
         * @param  {[type]} queryString
         * @return {Object}
         */
        function getQueryParams(queryString) {
            const params = querystring.parse(queryString);
            let param;

            // Copy functionality of iterate() in fn-utilities.js in order to get around the dependency on it
            for (param in params) {
                if (params.hasOwnProperty(param)) {
                    if (params[param] instanceof Array) {
                        params[param] = params[param][0];
                    }
                }
            }

            return params;
        }

        /**
         * Runs matchMedia against values stored in the global breakpoints object and
         * returns the last matching key.
         * @return {String} [description]
         */
        function getW() {
            const bp = window.breakpoints;
            let arg;
            let key;
            let k;
            let w = false;

            for (key in bp) {
                if (typeof bp[key].arg === 'undefined') {
                    continue;
                }
                k = bp[key].altKey || key;
                arg = `(${bp[key].arg}: ${bp[k].width})`;
                if (window.matchMedia(arg).matches) {
                    w = key;
                }
            }

            return w;
        }

        /**
         * Prepend '/static/mantle/' to the value of the `data-resource-version` attribute
         * on the html tag.
         * @return {String}
         */
        function getStaticPath() {
            return `/static/${resourceVersion()}`;
        }

        /**
         * Returns whether the canonical url should have a www subdomain
         * @return {Boolean}
         */
        function hasWwwSubdomain() {
            const hostname = window.location.hostname;

            return hostname.startsWith('www.') || hostname.startsWith('www-');
        }

        /**
         * Returns whether the canonical url should have a www subdomain
         * @return {Boolean}
         */
        function hasWwwSubdomain() {
            const hostname = window.location.hostname;

            return hostname.startsWith('www.') || hostname.startsWith('www-');
        }

        /**
         * Match the supplied argument against the current domain
         * This method is only used by qaErrorLogger.js.
         * @param  {String}  url
         * @return {Boolean}
         */
        function isCurrentDomain(url) {
            let domain;

            // find & remove protocol and get domain
            if (url.indexOf('://') > -1) {
                domain = url.split('/')[2];
            } else {
                domain = url.split('/')[0];
            }

            // find & remove port number
            domain = domain.split(':')[0];

            if (domain.indexOf(getDomain()) > -1) {
                return true;
            }

            return false;
        }

        /**
         * Inject a script tag into the head of the current document
         *
         * 1. This is a dynamically created script and is async by definition
         * (https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script)
         *
         * @param  {Object}   paramData Flat map of attributes to add to the tag
         * @param  {Function} cb        Optional callback to be fired when the script loads
         * @return {undefined}
         */
        function loadExternalJS(paramData, cb) {
            const script = document.createElement('script'); // 1
            let key;

            script.type = 'text/javascript';

            for (key in paramData) {
                script.setAttribute(key, paramData[key]);
            }

            /* Removing null id attributes to avoid duplicate ids. */
            if (script.hasAttribute('id') && !paramData.id) {
                script.removeAttribute('id');
            }

            if (cb) {
                scriptOnLoad(script, cb);
            }

            document.getElementsByTagName('head')[0].appendChild(script);
        }

        /**
         * Poll the document font status and fire a callback when it reads loaded or
         * when the provided timeout it crossed
         * @param  {Function}   callback
         * @param  {Number}     timeout  Polling Limit
         * @return {undefined}
         */
        function onFontLoad(callback, timeout) {
            const endTime = Date.now() + (timeout || 3000);
            let intervalID;

            function updateStatus() {
                const status = document.fonts.status;
                const complete = status === 'loaded' || status === 'error';

                if (complete || Date.now() > endTime) {
                    clearInterval(intervalID);
                    window.debug.log(`onFontLoad: ${(complete ? status : 'timed out')}`);
                    callback();
                }
            }

            if (document.fonts) {
                intervalID = setInterval(updateStatus, 200);
            } else {
                window.debug.log('onFontLoad browser unsupported');
                callback();
            }
        }

        /**
         * Fire the supplied callback if the doucment load is complete or add a load
         * listener with the callback
         * @param  {Function} callback
         * @param  {Boolean}  bubble   set true if you want the event to bubble
         * @return {undefined}
         */
        function onLoad(callback, bubble) {
            if (document.readyState === 'complete') {
                callback();
            } else {
                window.addEventListener('load', callback, bubble || false);
            }
        }

        /**
         * Open the provided link in a new window
         * @param  {String} link url to load
         * @return {[type]}      https://developer.mozilla.org/en-US/docs/Web/API/Window/open
         */
        function openWindow(link) {
            const w = 835;
            const h = 500;
            const scrollbars = 'yes';
            const left = (screen.width / 2) - (w / 2);
            const top = (screen.height / 2) - (h / 2);

            return window.open(
                link,
                'shareWindow',
                `toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=${scrollbars}, resizable=yes, copyhistory=no, width=${w}, height=${h}, top=${top}, left=${left}`
            );
        }

        /**
         * Create a function that pushes a set of constants to the dataLayer in addition to other properties
         * @param  {Object} constants set of constant properties
         * @return {Function}
         */
        function pushToDataLayer(constants) {
            return function(variables) {
                window.dataLayer = window.dataLayer || [];
                window.dataLayer.push(
                    Object.assign(
                        {},
                        constants,
                        variables
                    )
                );
            };
        }

        /**
         * Run callbacks on DOMContentLoaded or immediately if DOMContentLoaded already fired
         * @param  {Function} callback
         * @return {undefined}
         */
        function ready(callback) {
            if (document.readyState === 'loading') { // Loading hasn't finished yet
                document.addEventListener('DOMContentLoaded', callback);
            } else { // `DOMContentLoaded` has already fired
                callback();
            }
        }

        /**
         * Get data attributes from HTML element
         * @param  {String} module Optional modifier
         * @return {String}        Data attribute value
         */
        function resourceVersion(module) {
            const attr = `${(module ? `data-${module}-` : 'data-')}resource-version`;

            return document.querySelector('HTML').getAttribute(attr);
        }

        /**
         * Add an onload event handler to a script tag
         * @param  {Element}  script   <script>
         * @param  {Function} callback
         * @return {Boolean}
         * @internal This function is only used by loadExternalJS()
         */
        function scriptOnLoad(script, callback, args) {
            args = args || [];

            if (!script && !callback) {
                window.debug.error('Mntl.utilities.scriptOnLoad() needs script and callback arguments');

                return false;
            }

            if (script.dataset.loaded) { // Already loaded (see loaded.js)
                callback.apply(script, args);
            } else if (script.readyState) { // IE 10
                script.onreadystatechange = function() {
                    if (script.readyState === 'scriptAvailable' || script.readyState === 'complete' || script.readyState === 'loaded') {
                        script.onreadystatechange = null;
                        callback.apply(script, args);
                    }
                };
            } else { // Others
                script.onload = function() {
                    callback.apply(script, args);
                };
            }

            return false;
        }

        /**
         * Like scriptOnLoad but for a collection of scripts
         * @param  {Element[]}  scripts
         * @param  {Function} callback
         * @return {Boolean}
         */
        function scriptsOnLoad(scripts, callback, args) {
            if (scripts.length) {
                const curriedScriptLoader = window.Mntl.fnUtilities.curry(scriptOnLoad, 2); // scriptOnLoad(script, callback, args) => curriedScriptLoader(script)(callback)
                const scriptLoaders = Array.prototype.map.call(scripts, (script) => curriedScriptLoader(script)); // script => scriptLoader(callback)

                window.Mntl.fnUtilities.all(scriptLoaders, () => {
                    callback.apply(scripts, args || []);
                });

                return true;
            }

            return false;
        }

        /**
         * Helper function to assign vue app object to the global name space
         * Vue object normally obfuscated by Webpack
         * @param  {string}  appName - String representation of the Vue Object (the value in the import statement) needed for assumptions in mntl-vue-base.evaluated.js
         * @param  {object} vueApp - Vue Object representing the final vue app (the value in the import statement)
         * @param  {object} datastore - e.g. vuex data store or vue compatible data store, default is null to not use.
         */
        function initVueApp(appName, vueApp, datastore=null) {
            Mntl.VueInGlobe.apps[appName] = window.Mntl.VueInGlobe.apps[appName] || {};
            Mntl.VueInGlobe.apps[appName].baseApp = vueApp;

            if (datastore) {
                Mntl.VueInGlobe.apps[appName].datastore = datastore;
            }
        }

        // Alphabetize Public API
        return {
            ajaxPromiseGetCall,
            ajaxPromisePost,
            ajaxPromisePut,
            getClosestMatchingParent,
            getDomain,
            getQueryParams,
            getW,
            getStaticPath,
            hasWwwSubdomain,
            isCurrentDomain,
            initVueApp,
            loadExternalJS,
            onFontLoad,
            onLoad,
            openWindow,
            pushToDataLayer,
            ready,
            resourceVersion,
            scriptOnLoad,
            scriptsOnLoad
        };
    }());

    window.Mntl.utilities = Object.assign({}, window.Mntl.utilities || {}, mntlUtilities);
}(window));
