/* eslint no-use-before-define: 0 */

window.Mntl = window.Mntl || {};

Mntl.domUtilities = (function() {
    /**
     * Loop backwards over previous siblings of the provided element and return
     * the first that passes the provided test. Includes self.
     * @param  {Function} predicate function to run on siblings, should return a boolean
     * @param  {Element}  element   the starting element
     * @return {Element}            first match or null
     */
    function closestPreviousSibling(predicate, element) {
        if (element === null) {
            return null;
        }

        return predicate(element) ? element : closestPreviousSibling(predicate, element.previousElementSibling);
    }

    /**
     * Take an object specification and returns a nested DOM tree. Recursively
     * walks children. All node args should be well formed
     * {type: 'String', props: {}, children: []}
     * @param  {Object} node
     * @property {string} node.type valid html tag
     * @property {object} node.props key value pairs to assign as properties on tag
     * @property {array}  node.children array of node objects as described above
     * @return {DOMTree}
     */
    function createEl(node) {
        var el;

        if (typeof node === 'string') {
            return document.createTextNode(node);
        }

        el = document.createElement(node.type);

        setProps(el, node.props);

        node.children
            .map(createEl)
            .forEach(el.appendChild.bind(el));

        return el;
    }

    /**
     * Return the given data attribute of the given element
     * @param  {String}  key camel case data attrib
     * @param  {Element} el  DOM Element
     * @return {Any}         Data attribute value or Null
     */
    function getData(key, el) {
        var data = el.dataset
            ? el.dataset[key]
            : el.getAttribute(['data-', key.replace(
                /([A-Z])/g,
                function(a) {
                    return ['-', a.toLowerCase()].join('');
                })].join(''));

        if (typeof data === 'undefined') {
            return data;
        } else if (data === 'null') {
            return null;
        }

        return Mntl.fnUtilities.safeJsonParse(data) === null ? data : Mntl.fnUtilities.safeJsonParse(data);
    }

    /**
     * Replacement for window.location.origin which is still not stable in IE11.
     * @link https://developer.mozilla.org/en-US/docs/Web/API/Window/location
     * @return {String}
     */
    function getResourceRootUrl() {
        return [window.location.protocol, '//', window.location.host].join('');
    }

    /**
     * Get the height of the given element with top and bottom margins
     * @param  {Element} el
     * @return {Number}
     */
    function outerHeight(el) {
        var style = window.getComputedStyle(el);

        return Math.ceil(
            ['height', 'paddingTop', 'paddingBottom', 'borderTopWidth', 'borderBottomWidth', 'marginTop', 'marginBottom'].reduce(function(acc, rule) {
                return acc + parseFloat(style[rule]);
            }, 0)
        );
    }

    /**
     * Add object to element data attributes
     * @param       {Object} data camelcase the keys!
     * @param       {Element} el  DOM element
     */
    function setDataAttrs(data, el) {
        var key;

        for (key in data) {
            try {
                el.dataset[key] = data[key];
            } catch (error) {
                window.debug.error('Mntl.domUtilities.setDataAttrs: ' + key + ' is a bad key. Pass keys in camelCase');
            }
        }
    }

    /**
     * Set a single property on the provided element. Use this method in loops
     * or to spawn other methods like:
     * var setHref = setProp.bind(null, 'href');
     * var addMyClass = setProp.bind(null, 'class', 'my-class');
     * @param {string} name  property name
     * @param {string} value property value
     * @param {Element} el   element to apply property to
     */
    function setProp(name, value, el) {
        if (name === 'className') {
            el.className = value;
        } else {
            el.setAttribute(name, value);
        }
    }

    /**
     * Set many props on a given element
     * @param {Element} el
     * @param {object} props flat object of key value pairs to set on provided element
     */
    function setProps(el, props) {
        Object.keys(props).forEach(function(name) {
            setProp(name, props[name], el);
        });
    }

    /**
     * Helper function to scroll and then execute a callback
     * @param {number} offset - An offset number to where the page should scroll to upon click from the current location 
     * @param {Function} callback - Custom callback that you can provide that should run after the scroll
     */
    function scrollToWithCallback(offset, callback) {
        // If offset is not a number, don't execute
        if (!isNaN(offset)) {
            const fixedOffset = offset.toFixed();

            function onScroll() {
                if (window.pageYOffset.toFixed() === fixedOffset) {
                    window.removeEventListener('mntl.scroll', onScroll);
                    callback();
                }
            }

            window.addEventListener('mntl.scroll', onScroll);
            onScroll();
            window.scrollTo({
                top: offset,
                behavior: 'smooth'
            });
        }
    };

    // Alphabetize Public API
    return {
        closestPreviousSibling,
        createEl,
        getData,
        getResourceRootUrl,
        outerHeight,
        scrollToWithCallback,
        setDataAttrs,
        setProp,
        setProps
    };
})();
