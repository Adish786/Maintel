window.Mntl = window.Mntl || {};

Mntl.TocList = (function() {
    function init($container) {
        var tocSelector = '.comp.mntl-toc-list';
        var $tocs = $container.is(tocSelector) ? $container : $container.find(tocSelector);
        var offset = 20;

        if ($tocs.length !== 1) {
            return;
        }

        function scrollToHeading(currentHash) {
            var $el = $container.find($('#' + currentHash.replace('#', '')));

            if ($el.length > 0) {
                Mntl.utilities.scrollToElement($el, null, offset);
            }
        }

        $tocs.each(function() {
            var $toc = $(this);

            $toc.on('click', 'a', function() {
                // Bind events, ie "open chop", "close TOC"...
                $toc.trigger('toc:goToHash');

                if ($toc.hasClass('js-toc-scroll')) {
                    scrollToHeading($(this).attr('href'));
                }
            });
        });
    }

    return { init };
})();

Mntl.utilities.readyAndDeferred(Mntl.TocList.init);
