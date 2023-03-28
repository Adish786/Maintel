window.Mntl = window.Mntl || {};

Mntl.Maybe = (function() {
    /**
     * Use this construct to handle values that could be `null` or `undefined`
     * @see sc-ads.js for use
     * @see bottom of this file for more info
     * @param       {Any} v type to store in maybe
     * @constructor
     */
    function Maybe(v) {
        this._v = v;
    }

    /**
     * Static: Returns a new Maybe
     * @param  {[type]} v type to store in `Maybe`
     * @return {Object}   New Maybe
     */
    Maybe.of = function(v) {
        return new Maybe(v);
    };

    /**
     * Determines if the stored value is `null` or `undefined`
     * @type {[type]}
     */
    Maybe.prototype.isNothing = function() {
        return this._v === null || typeof this._v === 'undefined';
    };

    /**
     * Apply the supplied function to the stored value and return a new Maybe
     * of the result
     * @param  {Function} fn function to apply against the stored value
     * @return {Object}      Maybe
     */
    Maybe.prototype.map = function(fn) {
        if (this.isNothing()) {
            return Maybe.of(null);
        }

        return Maybe.of(fn(this._v));
    };

    /**
     * Apply the supplied function to the stored value and return the result
     * @internal Also known as chain in some implementations
     * @param  {Function} fn function to apply against the stored value
     * @return {Any}
     */
    Maybe.prototype.flatMap = function(fn) {
        if (this.isNothing()) {
            return null;
        }

        return fn(this._v);
    };

    /**
     * Returns the supplied default when the stored value is null | undefined.
     * Otherwise returns the stored value.
     * @param  {Any} def
     * @return {Any}
     */
    Maybe.prototype.orElse = function(def) {
        return this.isNothing() ? def : this._v;
    };

    return Maybe;
}());

/**
 * Unexpected null and undefined values are the common cause of type errors like
 * this: Uncaught TypeError: Cannot read property 'toString' of [undefined|null]
 *
 * These errors are commonly thrown because we've assumed the existence of some
 * value and immediately started calling methods on it.
 *
 * Ex: element.dataset.foo.split(','); // if foo does not exist split will throw a type error
 *
 * Maybe is used to wrap a value that coule be null or undefined and allow you
 * to attempt to apply functions to the stored value without throwing type errors.
 *
 * Ex: Maybe.of(element.dataset.foo).flatMap(foo => foo.split(','));
 *
 * This will not throw a type error because flatMap returns null if element.dataset.foo
 * is undefined; the split function is never run.
 *
 * But we can't be sure that document.getElementById('bar') is not null since
 * getElementById will return a found element or null.
 *
 * Ex: Maybe.of(document.getElementById('bar')) // contains null or an element
 *       .map(el => el.dataset.foo)             // contains a value of undefined
 *       .flatMap(foo => foo.split(','));       // returns a split value or null
 *
 * Maybe allows us to stop interrupting our code with a bunch of null checks but
 * in the end you may still need a final null check to exit your code. Or you
 * may wish to replace the null value with a sensible default.
 */
