window.Mntl = window.Mntl || {};

(function($) {
    const $heightChangeListener = $('#height-change-listener');
    const $document = $(document);
    let prevHeight = $document.height();
    const throttleResize = Mntl.throttle((e) => {
        const contentResizeEvent = $.extend({}, e);
        const newHeight = $document.height();
        const heightChange = prevHeight ? newHeight - prevHeight : 0;
        const vanillaHeightChangeEvent = new CustomEvent('mntl.contentResize');

        if (heightChange) {
            contentResizeEvent.type = 'mntl.contentResize';
            contentResizeEvent.heightChange = heightChange;
            contentResizeEvent.newContentHeight = newHeight;
            contentResizeEvent.prevContentHeight = prevHeight;

            $heightChangeListener.parent().trigger(contentResizeEvent);
            document.dispatchEvent(vanillaHeightChangeEvent);
        }

        prevHeight = newHeight;
    }, 20);

    $($heightChangeListener[0].contentWindow).on('resize', throttleResize);
})(window.jQuery || {});
