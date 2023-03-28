/* eslint no-use-before-define: 0 */

window.Mntl = window.Mntl || {};

Mntl.fnUtilities = (function() {
    /**
     * Run all async functions in the array and fire a callback when all the callbacks have fired.
     * @param  {Function[]} fns array of function taking a callback
     * @param  {Function} cb callback that will be called when all functions have called their callback
     * @return {undefined}
     */
    function all(fns, cb) {
        var length = fns.length;

        fns.forEach(function(fn) {
            fn(function() {
                if (--length === 0) {
                    cb();
                }
            });
        });
    }

    /**
     * Return a function named 'c' until all the arguments required by 'fn' have
     * been gathered into 'args' then return the result of 'fn'
     * @param  {Function} fn
     * @param  {Integer}  arity    optional: number of arguments fn expects
     * @return {Function | Result}
     */
    function curry(fn, arity) {
        var ar = arity || fn.length;

        if (ar < 1) {
            throw new TypeError('Mntl.fnUtilities.curry expects the provided function to have at least one argument, or set arity explicity');
        }

        return function curried() {
            var args = Array.prototype.slice.call(arguments);

            if (args.length >= ar) {
                return fn.apply(null, args);
            }

            return function c() {
                return curried.apply(null, args.concat(Array.prototype.slice.call(arguments)));
            };
        };
    }

    /**
     * Merge the properties of the right hand argument into the left
     * !!! Method mutates left hand argument
     * @param  {Object} destination
     * @param  {Object} source
     * @return {Object}
     */
    function deepExtend(destination, source) {
        var property;

        for (property in source) {
            if (source[property] && source[property].constructor &&
                source[property].constructor === Object) {
                destination[property] = destination[property] || {};
                deepExtend(destination[property], source[property]);
            } else {
                destination[property] = source[property];
            }
        }

        return destination;
    }

    /**
     * Given an object or array and any number of keys as arguments return a
     * "null-safe" deeply nested value
     * @param  {Object|Array} object
     * @param  {Mixed}        arguments[1]...
     * @return {Mixed}        the deep value
     */
    function getDeepValue(object) {
        var keys = Array.prototype.slice.call(arguments, 1);

        return keys.reduce(function(acc, key) {
            return typeof acc !== 'undefined'
                ? acc === null ? void 0 : acc[key] // void 0 === undefined
                : acc;
        }, object);
    }

    /**
     * Return the value supplied to it
     * @param  {Any} a
     * @return {Any}
     */
    function _identity(a) {
        return a;
    }

    /**
     * Runs the supplied callback against each own shallow property of the supplied object.
     * `callbackFn` takes two arguments. The first is the supplied object it's
     * being called against and the second is the current key.
     * @param  {Object}   obj        Subject of callback
     * @param  {Function} callbackFn
     * @return {Undefined}
     */
    function iterate(obj, callbackFn) {
        var prop;

        for (prop in obj) {
            if (obj.hasOwnProperty(prop)) {
                callbackFn(obj, prop);
            }
        }
    }

    /**
     * Run the supplied functions against all items in an object returning a new
     * object with keys and values transformed by the supplied functions
     * @param  {object}   obj  object to map
     * @param  {Function} fnk  key transformer
     * @param  {Function} fnv  value transformer
     * @return {object}
     */
    function mapObject(obj, fnk, fnv) {
        var functionK = fnk || _identity;
        var functionV = fnv || _identity;

        return Object.keys(obj).reduce(function(acc, key) {
            var keyPrime = functionK(key);

            if (keyPrime) {
                acc[keyPrime] = functionV(obj[key]);
            }

            return acc;
        }, {});
    }

    /**
     * Creates an object composed of the picked object properties.
     * @param  {object}   obj   object to pick
     * @param  {array}    keys  keys to pick from object
     * @return {object}
     */
    function pickObject(obj, keys) {
        return mapObject(obj, function(key) {
            return (keys || []).indexOf(key) !== -1 ? key : null;
        });
    }

    /**
     * Execute the given function one time. Future called return undefined
     * @param  {Function} fn a function to be executed once
     * @return {Function}
     */
    function once(fn) {
        var isDone = false;

        return function() {
            return isDone ? void 0 : ((isDone = true), fn.apply(this, arguments));
        };
    }

    /**
     * Execute a promise once. Succeeding calls return the promise (pending or resolved) that was passed on the first call
     * @param  {Function} fn a function that when called returns a Promise
     * @return {Promise}
     */
    function promiseOnce(promiseWrappedInFunction) {
        const initialPromise = promiseWrappedInFunction;

        let resolvedPromise = null;

        return function() {
            if (!resolvedPromise) {
                resolvedPromise = initialPromise();
            }

            return resolvedPromise;
        };
    }

    /**
     * Save an array of functions to run first to last on a given value
     * @return {Function} unary function
     */
    function pipe() {
        var fns = toArray(arguments);
        var allFns = fns.reduce(function(acc, fn) {
            return acc && typeof fn === 'function';
        }, true);

        if (!allFns) {
            throw new TypeError('All arguments provided to Mntl.fnUtilities.pipe must be functions');
        }

        return function p(a) {
            return fns.reduce(function(acc, fn) {
                return fn(acc);
            }, a);
        };
    }

    /**
     * Return Parsed JSON or null if the attempt throws an error
     * @param  {JSON}  obj
     * @return {Mixed}     Null or Object
     */
    function safeJsonParse(obj) {
        var result;

        try {
            result = JSON.parse(obj);
        } catch (e) {
            result = null;
        }

        return result;
    }

    /**
     * Convert supplied argument to array by calling Array.slice on it
     * @param  {Array-Like} el
     * @param  {Number}     from  Optional. Leave this many items off the front of the array.
     * @return {Array}
     */
    function toArray(el, fr) {
        var from = fr || 0;

        return Array.prototype.slice.call(el, from);
    }

    /**
     * Return the provided string with white space deleted from the beginning and
     * end and all new line characters replaced with a space.
     * @param  {String} text
     * @return {String}
     */
    function trimAllWhitespace(text) {
        return text ? text.trim().replace(/\n/g, ' ') : '';
    }

    // Alphabetize Public API
    return {
        all,
        curry,
        deepExtend,
        getDeepValue,
        iterate,
        mapObject,
        once,
        pickObject,
        pipe,
        promiseOnce,
        safeJsonParse,
        toArray,
        trimAllWhitespace
    };
})();
