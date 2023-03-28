(function(Mntl, $) {
    function initSlideshows() {
        $('.mntl-slideshow').on('click', '.mntl-slideshow__control', function(e) {
            var $control = $(this);

            $(e.delegateTarget).trigger({
                type: 'Mntl.slide',
                disabled: $control.attr('data-disabled') === 'true',
                direction: $control.attr('data-direction')
            });
        });
    }

    Mntl.utilities.readyAndDeferred(initSlideshows);
}(window.Mntl || {}, window.jQuery || {}));
