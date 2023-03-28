window.Mntl = window.Mntl || {};

Mntl.SocialShare = (function(utils, Maybe) {
    const domain = utils.getDomain();
    let _networks;

    function _truncate(title) {
        return title.length > 80 ? title.substring(0, 80) + '..' : title;
    }

    _networks = {
        WHATSAPP: function(shareContainer, shareButton) {
            const body = shareContainer.dataset.whatsappBody || _truncate(shareContainer.dataset.title) + ' ' + shareButton.dataset.href;

            return 'whatsapp://send?text=' + encodeURIComponent(body);
        },
        SMS: function(shareContainer, shareButton) {
            // iOS 8+ uses '&' for query string separator
            const separator = /iPad|iPhone|iPod/.test(navigator.userAgent) ? '&' : '?';
            const body = shareContainer.dataset.smsBody || _truncate(shareContainer.dataset.title) + ' ' + shareButton.dataset.href;

            return 'sms:' + separator + 'body=' + encodeURIComponent(body);
        },
        EMAILSHARE: function(shareContainer, shareButton) {
            const subject = shareContainer.dataset.emailSubject || 'Shared from ' + domain + ': ' + _truncate(shareContainer.dataset.title);
            const body = shareContainer.dataset.emailBody || shareContainer.dataset.description + ' Read More: ' + shareButton.dataset.href;

            return 'mailto:?subject=' + encodeURIComponent(subject) + '&body=' + encodeURIComponent(body);
        }
    };

    function buildNonHttpShareUrl(shareButton) {
        const network = shareButton.dataset.network.toUpperCase();

        return network in _networks ? _networks[network](shareButton.closest('.mntl-social-share'), shareButton) : '';
    }

    function _handleClick(shareButton) {
        let shareHref = shareButton.dataset.href;

        // if shareHref starts with domain.com, socialLink service did not add url prefix
        // and we need to do it with JS (this happens for non-http protocols like email, SMS, and whatsapp)
        if (shareHref.indexOf(domain) !== -1 && (shareHref.indexOf('?') === -1 || shareHref.indexOf(domain) < shareHref.indexOf('?'))) {
            window.location = buildNonHttpShareUrl(shareButton);
        } else {
            // Workaround to set Pinterest media param when document does not have a primary image.
            if (shareButton.classList.contains('share-link-pinterest') && shareHref.indexOf('&media=') === -1 && document.querySelector('meta[property="og:image"]')) {
                shareHref += '&media=' + encodeURIComponent(document.querySelector('meta[property="og:image"]').getAttribute('content'));
            }
            utils.openWindow(shareHref);
        }
    }

    function init($container) {
        const container = $container.jquery ? $container[0] : $container;

        container.addEventListener('click', function(e) {
            Maybe.of(e.target.classList.contains('share-link') ? e.target : e.target.closest('share-link'))
                .map(_handleClick);
        });
    }

    return {
        init,
        buildNonHttpShareUrl
    };
})(window.Mntl.utilities || {}, window.Mntl.Maybe || {});

Mntl.utilities.readyAndDeferred(Mntl.SocialShare.init);
