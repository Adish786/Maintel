window.Mntl = window.Mntl || {};

(function() {
    var els = document.getElementsByClassName('mntl-notification-banner');

    function setDismissedCookie(el) {
        var cookiePrefix = el.dataset.cookiePrefix ? el.dataset.cookiePrefix : '';

        if (cookiePrefix.length) {
            docCookies.setItem(cookiePrefix + '_notification_banner_dismissed', 'true', null, '/', Mntl.utilities.getDomain());
        } else {
            docCookies.setItem('notification_banner_dismissed', 'true', null, '/', Mntl.utilities.getDomain());
        }
    }

    function handleClose(el) {
        el.classList.add('is-closed');

        if (el.dataset.showUntilDismissed === 'true') {
            setDismissedCookie(el);
        }
    }

    Mntl.fnUtilities.toArray(els).forEach(function(el) {
        var close = el.getElementsByClassName('js-banner-dismiss')[0];

        close.addEventListener('click', handleClose.bind(null, el), { once: true });
    });
})();
