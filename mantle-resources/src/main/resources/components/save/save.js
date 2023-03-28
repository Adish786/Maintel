(function(utils) {
    // Click tracking event
    function saveEventTracking(trackingOn) {
        let eventLabel = 'Recipe Save Heart';

        if (trackingOn === 'card') {
            eventLabel = 'Recipe Card Save Heart';
        }

        utils.pushToDataLayer({
            event: 'transmitInteractiveEvent',
            eventCategory: 'User Action',
            eventAction: 'Content Action Taken',
            eventLabel,
            nonInteraction: 0
        })();
    }


    function deleteEventTracking(trackingOn) {
        let eventLabel = 'Recipe Remove Heart';

        if (trackingOn === 'card') {
            eventLabel = 'Recipe Card Remove Heart';
        }

        utils.pushToDataLayer({
            event: 'transmitInteractiveEvent',
            eventCategory: 'User Action',
            eventAction: 'Content Action Taken',
            eventLabel,
            nonInteraction: 0
        })();
    }


    function isLoggedIn() {
        return window.docCookies.getItem('ddmaccount');
    }

    function postToBookmarksEndpoint(type, accessToken, docId) {
        return new Promise((resolve, reject) => {
            // Submit data to BE
            const formData = new FormData();

            formData.append('docId', docId);
            formData.append('accessToken', accessToken);

            // GLBE-9646 sets timeout at 30 seconds for extremely slow connections to complete the ajax registration call to the server
            Mntl.utilities.ajaxPromisePost(`/bookmarks/${type}`, formData, 30000)
                .then(() => {
                    // These endpoints do not send a payload back. Good http status is enough.
                    resolve(true);
                })
                .catch((error) => reject(error));
        });
    }

    function getAccessToken(auth0Link) {
        return new Promise((resolve, reject) => {
            // Always fetch a new token instead of using existing in case of token expiration
            if (Mntl.auth0.auth0Client) {
                Mntl.auth0.getAccessToken().then(() => {
                    if (Mntl.auth0.accessToken) {
                        resolve(Mntl.auth0.accessToken);
                    } else {
                        reject('unable to get token');
                    }
                })
                    .catch(() => {
                        reject('unable to get token');
                    });
            } else {
                document.addEventListener(Mntl.auth0.CLIENT_INITED_EVENT_NAME, () => {
                    Mntl.auth0.getAccessToken().then(() => {
                        if (Mntl.auth0.accessToken) {
                            resolve(Mntl.auth0.accessToken);
                        } else {
                            reject('unable to get token');
                        }
                    })
                        .catch(() => {
                            reject('unable to get token');
                        });
                });
                document.dispatchEvent(new CustomEvent(Mntl.auth0.CLIENT_INIT_EVENT_NAME, { detail: { auth0Link } }));
            }
        });
    }

    function getAttributes(saveButton) {
        const docId = saveButton.getAttribute('data-doc-id');
        const parent = saveButton.closest('.mntl-save');
        const loginLink = parent.querySelector('.mntl-save__auth0');
        const checkOnLoad = saveButton.getAttribute('data-check-on-load');
        const trackingOn = saveButton.getAttribute('data-tracking-on');

        return {
            docId,
            parent,
            loginLink,
            checkOnLoad,
            trackingOn
        }
    }

    async function saveBookmark(saveButton) {
        const {
            docId,
            parent,
            loginLink,
            trackingOn
        } = getAttributes(saveButton);

        document.body.classList.add('save-loading');
        parent.classList.add('saved');

        try {
            const accessToken = await getAccessToken(loginLink);
            const response = await postToBookmarksEndpoint('save', accessToken, docId);

            saveEventTracking(trackingOn);

            document.body.classList.remove('save-loading');
            if (response) {
                const dialog = document.querySelector('.mntl-save__confirmation-dialog');
                const a11yDialog = new A11yDialog(dialog);

                a11yDialog.show();
            }
        } catch(error) {
            parent.classList.remove('saved');
            document.body.classList.remove('save-loading');
            console.error('Unable to save bookmark', error);
        }
    }

    async function deleteBookmark(saveButton) {
        const {
            docId,
            parent,
            loginLink,
            trackingOn
        } = getAttributes(saveButton);

        document.body.classList.add('save-loading');

        try {
            const accessToken = await getAccessToken(loginLink);
            const response = await postToBookmarksEndpoint('delete', accessToken, docId);

            deleteEventTracking(trackingOn);

            document.body.classList.remove('save-loading');
            if (response) {
                parent.classList.remove('saved');
            }
        } catch(error) {
            document.body.classList.remove('save-loading');
            console.error('Unable to delete bookmark', error);
        }
    }

    async function saveClickEvent(e) {
        e.preventDefault();

        const { currentTarget } = e;

        currentTarget.disabled = true;

        const {
            loginLink,
            parent
        } = getAttributes(currentTarget);

        if (isLoggedIn()) {
            if (parent.classList.contains('saved')) {
                await deleteBookmark(currentTarget);
            } else {
                await saveBookmark(currentTarget);
            }
        } else {
            currentTarget.setAttribute('data-clicked', true);

            loginLink.click();
        }
        currentTarget.disabled = false;
    }

    async function checkIfSaved(saveButton) {
        const {
            docId,
            parent,
            loginLink,
            checkOnLoad
        } = getAttributes(saveButton);

        if (isLoggedIn() && checkOnLoad === 'true') {
            // Remove the attribute so it doesn't check again
            saveButton.removeAttribute('data-check-on-load');

            try {
                const accessToken = await getAccessToken(loginLink);
                const response = await postToBookmarksEndpoint('get', accessToken, docId);

                if (response) {
                    parent.classList.add('saved');
                } else {
                    parent.classList.remove('saved');
                }
            } catch(e) {
                console.log("Bookmark not found for this recipe");
            }
        }
    }

    function init() {
        const saveButtons = document.querySelectorAll('.mntl-save__link');

        saveButtons.forEach((saveButton) => {
            saveButton.addEventListener('click', saveClickEvent);

            if (saveButton.getAttribute('data-check-on-load') === 'true') {
                checkIfSaved(saveButton);
            }
        });

        // If the user logs in after page loads, check save state on all save links
        document.addEventListener(Mntl.auth0.LOG_IN_EVENT_NAME, () => {
            saveButtons.forEach(async (saveButton) => {
                if (saveButton.getAttribute('data-clicked')) {
                    saveButton.removeAttribute('data-clicked');
                    await saveBookmark(saveButton);
                } else {
                    checkIfSaved(saveButton);
                }
            });
        });
    }

    utils.readyAndDeferred(init);
})(window.Mntl.utilities || {});