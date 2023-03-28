window.Mntl = window.Mntl || {};

Mntl.MasonryList = (function() {
    /**
     * Justify columns immediately
     * Left over function from IE11 cleanup. Could be removed in a future refactor pass if we wanted to keep this js
     * @param   {Object} config     Justification options
     * @return  {Undefined}
     */
    function _initJustifiedColumns(config) {
        new window.JustifiedColumns(config); // eslint-disable-line  no-new
    }

    /**
     * Setup the masonry-list, its styling, and layout
     * @param   {HTMLDomElement}    element        Masonry-list element
     * @param   {Object}    justifyConfig   Justification options
     * @return  {Undefined}
     */
    function _setup(element, justifyConfig) {
        const IMAGE_SELECTOR = 'img';
        const images = element.getElementsByTagName(IMAGE_SELECTOR);
        const config = Object.assign({
            grid: element,
            stretch: IMAGE_SELECTOR,
            shrink: false
        }, justifyConfig || {});
        // Determine which heightProp to use, should be same for all images
        const heightProp = justifyConfig && config.shrink && images[0].matches(config.stretch) ? 'maxHeight' : 'height';
        const _getImageHeight = (function(imgWidth) {
            return function(ratio) {
                return `${imgWidth / ratio}px`;
            };
        }(images.length ? images[0].offsetWidth : 0)); // columns will all be same width, so we can get away with only evaluating offsetWidth for first image and passing to closure

        /**
         * Set height attribute on given element, meant to minimize re-draws
         * @param       {Object} image DOM Element
         * @return      {Undefined}
         */
        function _setStyle(image) {
            const ratio = image.getAttribute('data-dim-ratio') || 1;

            image.style[heightProp] = _getImageHeight(ratio);
        }

        // Always set height for seamless lazy loading
        [...images].forEach((img) => {
            _setStyle(img);
        });

        // Only initialize Justifier if a config has been passed to init()
        if (justifyConfig && window.getComputedStyle(element).columnCount) {
            Mntl.utilities.onFontLoad(_initJustifiedColumns.bind(null, config));
        }
    }

    function init(element, justifyConfig) {
        if (!element.dataset.masonryListInitialized) {
            _setup(element, justifyConfig);
        }

        // Prevent masonry-list from re-initializing
        element.dataset.masonryListInitialized = true;
    }

    return { init };
})();
