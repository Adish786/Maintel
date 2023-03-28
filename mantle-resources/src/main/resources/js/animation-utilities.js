window.Mntl = window.Mntl || {};

Mntl.animationUtilities = (function() {
    /**
     * Scroll to a position on the page.
     * @param {string} direction 'up' or 'down'
     * @param {number} anchorPoint the desired Y coordinate to scroll into view
     */
    function scrollY(direction, anchorPoint) {
        let start;

        function scrollFrame(timestamp) {
            if (typeof start === 'undefined') {
                start = timestamp;
            }

            const elapsedTime = timestamp - start;
            let nextYCoordinate = window.pageYOffset;

            if (direction === 'up') {
                nextYCoordinate -= (0.1 * elapsedTime);
            } else {
                nextYCoordinate += (0.1 * elapsedTime);
            }

            window.scrollTo(0, nextYCoordinate);

            if (direction === 'up' && (nextYCoordinate > anchorPoint)) {
                window.requestAnimationFrame(scrollFrame);
            } else if (direction === 'down' && (nextYCoordinate < anchorPoint)) {
                window.requestAnimationFrame(scrollFrame);
            }
        }

        window.requestAnimationFrame(scrollFrame);
    }

    return { scrollY };
})();
