(function() {
    const rollawayBlock = document.querySelector('.mntl-rollaway-block');
    const rollawayContent = rollawayBlock.children;
    const spacer = document.querySelector('.mntl-rollaway-block + .mntl-rollaway-spacer');
    const timingCollapse = rollawayBlock.dataset.timing;
    const collapsepoint = rollawayBlock.dataset.collapsepoint;
    let canCollapse = false;

    /**
     * Handle Scroll Events
     * @param  {Object}    e Event Object
     * @return {Undefined}
     */
    function rollawayBlockScrollHandler(e) {
        const isCollapsing = 'is-collapsing';
        const isScrolling = 'is-scrolling';

        function combinedCheck() { // Returns if the conditions are true to start scrolling effect
            const scrollHeight = rollawayBlock.dataset.scrollheight;

            return (canCollapse && e.detail.scrollTop > scrollHeight);
        }

        if (combinedCheck()) {
            rollawayBlock.classList.add(isScrolling);

            if (e.detail.scrollTop >= collapsepoint) {
                rollawayBlock.classList.add(isCollapsing);
            } else {
                rollawayBlock.classList.remove(isCollapsing);
            }
        } else {
            rollawayBlock.classList.remove(isScrolling);
        }
    }

    /**
    * Handles height attribute on 'rollaway-spacer'
    * @return {Undefined}
    */
    function rollawayHeightHandler() {
        const spacerHeight = rollawayContent[0].offsetHeight;

        spacer.style.height = spacerHeight;
    }

    /*
     * Handles the closing/removing rollaway
     * Adds 'rollaway-container-empty' class to parent element for journey implementation
     * @return {Undefined}
    */
    function rollawayClose() {
        spacer.remove();
        rollawayBlock.parentElement.classList.add('rollaway-container-empty');
        rollawayBlock.remove();
        window.removeEventListener('resize', rollawayHeightHandler);
        window.removeEventListener('mntl.scroll', rollawayBlockScrollHandler);
    }

    /**
     * Handle Close Events
     * @return {Undefined}
    */
    function rollawayBlockCloseHandler() {
        rollawayBlock.addEventListener('click', () => {
            rollawayHeightHandler();
            if (rollawayBlock.children.length === 0) {
                rollawayClose();
            }
        });
    }

    /**
     * Handle timer
     * @return {Undefined}
     */
    function collapseSetter() {
        canCollapse = true;
    }

    // Add event handlers
    if (rollawayBlock) {
        rollawayBlockCloseHandler();
        window.addEventListener('resize', rollawayHeightHandler);
        window.addEventListener('mntl.scroll', rollawayBlockScrollHandler);
        setTimeout(collapseSetter, timingCollapse);
    }
})();
