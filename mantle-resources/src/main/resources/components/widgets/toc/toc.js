(function(Mntl, $) {
    Mntl.utilities.readyAndDeferred(function($context) {
        var toc = $context[0].querySelector('.mntl-toc');
        var EXPANDED_CLASS = 'mntl-toc--expanded';
        var offset;
        var button;

        function isExpanded() {
            return toc.classList.contains(EXPANDED_CLASS);
        }

        function toggleExpansion() {
            toc.classList.toggle(EXPANDED_CLASS);

            button.innerHTML = button.dataset[isExpanded() ? 'expandedText' : 'collapsedText'];
        }

        if (!toc) {
            return;
        }

        button = toc.querySelector('.js-mntl-toc-toggle');
        offset = toc.dataset.offset || 0;

        if (button) {
            button.addEventListener('click', toggleExpansion, false);
            document.addEventListener('click', function(e) {
                if (isExpanded() && !toc.contains(e.target)) {
                    toggleExpansion();
                }
            }, false);
        }

        $(toc).on('click', '.js-mntl-toc-link', function(e) {
            var anchor = document.getElementById(this.attributes.href.value.substring(1));

            e.preventDefault();

            toc.dispatchEvent(new CustomEvent('mntl-toc:goToHash'));

            if (anchor) {
                Mntl.utilities.scrollToElement($(anchor), null, offset);
                window.location.hash = this.attributes.href.value;
            }
        });
    });
}(window.Mntl || {}, window.jQuery || {}));
