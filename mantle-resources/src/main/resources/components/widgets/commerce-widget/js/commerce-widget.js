window.Mntl = window.Mntl || {};

(function($) {
    Mntl.utilities.readyAndDeferred(function($container) {
        $container = $container.is('.mntl-commerce-widget') ? $container : $container.find('.mntl-commerce-widget');

        function handleCommerceContainerClicks(e) {
            var btn = $(this).find('.mntl-commerce-btn')
                .first()[0];

            e.preventDefault();
            btn.setAttribute('data-click-tracked', false);
            btn.click();
            btn.removeAttribute('data-click-tracked');
        }

        $container.on('click', handleCommerceContainerClicks);

        $container.find('.mntl-commerce-btn').on('click', function(e) {
            e.stopPropagation();
        });
    });
})(window.jQuery || {});
