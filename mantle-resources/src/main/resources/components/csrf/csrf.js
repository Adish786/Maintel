window.Mntl = window.Mntl || {};
window.Mntl.csrfInit = ((utils) => {
    let _token = null;

    /**
     * HTML forms are hardcoded with the hidden input parameter. Reworking them to attach the token dynamically
     * would require a breaking change that is hard to justify. Replacing the value with a new value is not
     * a big deal especially because the format of the parameter is standardized across all forms.
     */
    function _updateReferences() {
        const selector = 'input[type="hidden"][name="CSRFToken"]';

        window.debug.log('Mntl.csrf: updating token references.');
        window.document.querySelectorAll(selector).forEach((e) => e.setAttribute('value', _token));
    }

    function _reset(token) {
        if (_token === token) {
            window.debug.log('Mntl.csrf: token reset with the same value.');
        } else {
            _token = token;
            _updateReferences();
        }
    }

    /**
     * It's possible to have a stale token when the page is served by the browser's disk cache.
     * In this case, either we're dealing with an entirely new session and the browser has not yet made
     * contact with the server prompting the re-creation of the session cookies, OR the session cookies
     * are already present but the cached page was from a prior session.
     */
    function _refreshStaleToken() {
        const cookieValue = window.docCookies.getItem('CSRFToken');
        const isNewSession = !window.docCookies.getItem('Mint');
        const isPageFromPriorSession = !isNewSession && cookieValue && cookieValue !== _token;

        if (isPageFromPriorSession) {
            window.debug.log('Mntl.csrf: resetting token from prior session.');
            _reset(cookieValue);
        } else if (isNewSession) {
            // There's a potential race condition here. Because we have two layers of async calls
            // (ready and ajax), it's possible the token will be requested before we have the response.
            // On the bright side, this risk only exists once per new session because after that we'll
            // receive the new value from the cookie immediately (see above). Eliminating this request
            // is not possible without introducing breaking changes because `Mntl.csrf()` would have
            // to be rewritten to return a Promise instead of the actual token. Even then, it's not
            // totally possible until jQuery is eliminated because the ajaxPrefilter defined in
            // csrf-jquery.js does not support blocking for async calls.
            utils.ready(() => {
                window.debug.log('Mntl.csrf: making contact to create new session.');
                utils.ajaxPromiseGetCall(`/csrf-session/refresh?et=${new Date().getTime()}`, // timestamp param to thwart caching
                    'failed to refresh session', 30000)
                    .then((response) => {
                        try {
                            const jsonResponse = JSON.parse(response);

                            if (jsonResponse.csrfToken) {
                                window.debug.log('Mntl.csrf: resetting token with new session.');
                                _reset(jsonResponse.csrfToken);
                            } else {
                                window.debug.error('Mntl.csrf: server responded with an invalid token.');
                            }
                        } catch(e) {
                            window.debug.error('Mntl.csrf: failed to reset token with new session.', e);
                        }
                    })
                    .catch((e) => window.debug.error(`Mntl.csrf: ${e}.`));
            });
        }
    }

    function _csrf() {
        window.debug.log('Mntl.csrf: providing token.');
        if (_token === null) {
            window.debug.error('Mntl.csrf: token requested but was never initialized.');
        }

        return _token;
    }

    function _init(token, sessionCookiesEnabled) {
        window.debug.log('Mntl.csrf: initializing token.');
        if (_token === null) {
            _token = token;
            if (sessionCookiesEnabled) {
                window.debug.log('Mntl.csrf: checking for stale token.');
                _refreshStaleToken();
            }
        } else {
            window.debug.error('Mntl.csrf: token can only be initialized once.');
        }

        return _csrf;
    }

    return _init;
})(window.Mntl.utilities);