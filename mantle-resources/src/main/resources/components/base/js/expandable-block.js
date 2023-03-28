window.Mntl = window.Mntl || {};

Mntl.ExpandableBlock = (function(utils, fnUtils) {
    const EXPANDED_CLASS = 'is-expanded';

    function open(block) {
        block.classList.add(EXPANDED_CLASS);
    }

    function close(block) {
        block.classList.remove(EXPANDED_CLASS);
    }

    function toggle(block) {
        block.classList.toggle(EXPANDED_CLASS);
    }

    function init($container) {
        // TODO: Deprecate this in 3.15 for final axe of jquery cleanup.
        const container = ($container && $container.jquery) ? $container[0] : $container;

        let blocks = [];

        if (container.classList.contains('mntl-expandable-block')) {
            blocks.push(container);
        } else {
            blocks = blocks.concat(fnUtils.toArray(container.getElementsByClassName('mntl-expandable-block')));
        }

        blocks.forEach((block) => {
            const toggleContent = block.getElementsByClassName('toggle-content');

            if (toggleContent.length) {
                toggleContent[0].addEventListener('click', (e) => {
                    e.preventDefault();
                    toggle(block);
                });
            }
        });
    }

    utils.readyAndDeferred(init);

    return {
        open,
        close,
        toggle
    };
})(Mntl.utilities || {}, Mntl.fnUtilities || {});
