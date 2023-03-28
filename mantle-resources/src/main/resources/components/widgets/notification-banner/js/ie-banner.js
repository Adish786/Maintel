// separate IE banner js to make it compatible with older IE browsers
var Mntl = window.Mntl || {};

(function() {
    var banner = document.querySelectorAll('.ie-notification-banner')[0];
    var close = banner.querySelectorAll('.ie-js-banner-dismiss')[0];

    function setDismissedCookie() {
        docCookies.setItem('ie_notification_banner_dismissed', 'true', null, '/', Mntl.utilities.getDomain());
    }

    function handleClose(el) {
        el.className += ' is-closed';
        setDismissedCookie();
    }

    function handleScroll(el) {
        if (document.documentElement.scrollTop > el.offsetHeight) {
            handleClose(el);
        }
    }

    close.addEventListener('click', handleClose.bind(null, banner), { once: true });
    window.addEventListener('scroll', handleScroll.bind(null, banner));
})();
