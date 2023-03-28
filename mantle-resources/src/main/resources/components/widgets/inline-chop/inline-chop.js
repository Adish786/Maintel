/**
 * Inline Chop
 * 1. Inline chop button should show the hidden content.
 * 2. Inline chop button will be hidden once the rest of the content is visible.
 */

(function($, Mntl) {
    function init() {
        $('.js-inline-chop').one('click', '.js-inline-chop-expand', function() {
            $(this).hide() /* 2 */
                .closest('.js-inline-chop')
                .find('.js-inline-chop-content')
                .removeClass('inline-chop-content--hidden'); /* 1 */

            $(document).trigger('inline-chop-expanded');

            return false;
        });
    }
    Mntl.utilities.readyAndDeferred(init);
})(window.jQuery || {}, window.Mntl || {});
