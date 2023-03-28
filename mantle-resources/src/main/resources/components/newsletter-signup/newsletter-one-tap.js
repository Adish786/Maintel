window.Mntl = window.Mntl || {};

Mntl.OneTap = (function($) {
    function init($container) {
        const $onetaps = $container.find('.mntl-newsletter-one-tap__button[href][data-encoded=true]');

        if ($onetaps.length) {
            $onetaps.each(function() {
                const $onetap = $(this);

                $onetap.click(function(e) {
                    e.preventDefault();
                    window.location = window.atob($(this).attr('href'));
                });
            });
        }
    }

    return { init };
})(window.jQuery || {});

Mntl.utilities.readyAndDeferred(Mntl.OneTap.init);
