(function(utils, globalAuth0) {
    /********************************************
     * Component globals
     ********************************************/
    let auth0LibLoaded = false;
    let auth0Client = null;

    /********************************************
     * Component functions
     ********************************************/
    function getAttributes(el) {
        const auth0Enabled = el.getAttribute('data-auth0-enabled') === 'true';
        const auth0Domain = el.getAttribute('data-auth0-domain');
        const auth0Client = el.getAttribute('data-auth0-client-id');
        const auth0Regsource = el.getAttribute('data-auth0-regsource');
        const auth0LogoutUrl = el.getAttribute('data-auth0-logout-url');
        const auth0Audience = el.getAttribute('data-auth0-audience');

        if (auth0Enabled && auth0Domain && auth0Client && auth0LogoutUrl && auth0Audience) {
            const attributes = {
                auth0Enabled,
                auth0Domain,
                auth0Client,
                auth0LogoutUrl,
                auth0Audience
            };

            if (auth0Regsource) {
                attributes.auth0Regsource = auth0Regsource;
            }

            return attributes;
        }

        return false;
    }

    function loadLib() {
        document.body.classList.add('auth0-lib-loading');

        return new Promise((resolve) => {
            utils.loadExternalJS({ src: `${utils.getStaticPath()}/static/mantle/static/libs/js/auth0-spa-js.production.js` }, () => {
                document.body.classList.remove('auth0-lib-loading');
                resolve(true);
            });
        });
    }

    async function getAccessToken() {
        try {
            const accessToken = await auth0Client.getTokenSilently();

            globalAuth0.accessToken = accessToken;

            return accessToken;
        } catch (error) {
            console.error('error thrown by getTokenSilently', error);

            globalAuth0.accessToken = null;

            throw new Error(error);
        }
    }

    async function instantiateAuth0(attributes) {
        if (!auth0LibLoaded) {
            auth0LibLoaded = true;
            await loadLib();
        }

        if (!auth0Client) {
            const e = new CustomEvent(globalAuth0.CLIENT_INITED_EVENT_NAME);

            if (attributes.auth0Enabled) {
                e.enabled = true;

                // eslint-disable-next-line no-undef
                auth0Client = await createAuth0Client({
                    domain: attributes.auth0Domain,
                    // eslint-disable-next-line camelcase
                    client_id: attributes.auth0Client,
                    audience: attributes.auth0Audience,
                    useRefreshTokens: true,
                    cacheLocation: 'localstorage'
                });

                // Put this on the global var to be accessed by other components.
                globalAuth0.auth0Client = auth0Client;
                globalAuth0.auth0Attributes = attributes;
                globalAuth0.getAccessToken = getAccessToken;
            } else {
                e.enabled = false;
            }

            // Fire an event after auth0 is either instaniated
            // or let dependent components know auth0 is disabled.
            document.dispatchEvent(e);
        }
    }

    function loginTracking(attributes, userProfile) {
        const isRegistration = userProfile['http://meredith/isRegistration'] === 'true';

        // Click tracking event
        const clickTracking = {
            event: 'transmitNonInteractiveEvent',
            eventCategory: 'Login & Registration',
            eventAction: 'Login',
            eventLabel: userProfile['http://meredith/connectionStrategy'],
            metric11: '1'
        };

        if (isRegistration) {
            clickTracking.eventAction = 'Registrations';
        }

        if (attributes.auth0Regsource) {
            clickTracking.registrationSource = attributes.auth0Regsource;
        }
        const trackLogin = utils.pushToDataLayer(clickTracking);

        trackLogin();
    }

    async function launchSignIn(attributes) {
        if (attributes) {
            await instantiateAuth0(attributes);
        } else {
            return console.error('Unable to find required auth0 parameters for instantiation.');
        }

        try {
            if (attributes.auth0Regsource) {
                await auth0Client.loginWithPopup({ regsource: attributes.auth0Regsource });
            } else {
                await auth0Client.loginWithPopup();
            }

            globalAuth0.accessToken = await getAccessToken();

            const userProfile = await auth0Client.getIdTokenClaims();

            document.documentElement.classList.add('signed-in');

            let signinType = 'social';

            if (userProfile.sub && userProfile.sub.startsWith('auth0')) {
                signinType = 'email';
            }

            loginTracking(attributes, userProfile);

            const formData = new FormData();

            const userHashId = userProfile['http://meredith/HashId'];

            globalAuth0.userHashId = userHashId;

            formData.append('type', signinType);
            formData.append('hashId', userHashId);
            formData.append('idToken', userProfile.__raw);

            // GLBE-9646 sets timeout at 30 seconds for extremely slow connections to complete the ajax registration call to the server
            utils.ajaxPromisePost('/auth0/login', formData, 30000)
                .then(async () => {
                    document.dispatchEvent(new CustomEvent(globalAuth0.LOG_IN_EVENT_NAME, { detail: { userHashId } }));

                    // If the user was redirected here for login, send them to the authRedirectUrl
                    const authRedirectUrl = localStorage.getItem('authRedirectUrl');

                    if(authRedirectUrl){
                        localStorage.removeItem('authRedirectUrl');
                        window.location = authRedirectUrl;
                    }
                })
                .catch((error) => {
                    console.error('/auth0/login post error', error);
                });

        } catch (e) {
            console.error('Unable to log in', e);
        }
    }

    async function setUpSignIn(el) {
        const attributes = getAttributes(el);

        if (!attributes) {
            return;
        }

        el.addEventListener('click', async (e) => {
            e.preventDefault();

            await launchSignIn(attributes);
        });
    }

    async function setUpSignOut(el) {
        const attributes = getAttributes(el);

        if (!attributes) {
            return;
        }

        el.addEventListener('click', async (e) => {
            e.preventDefault();

            await instantiateAuth0(attributes);

            await auth0Client.logout({returnTo: attributes.auth0LogoutUrl});
        });
    }

    /********************************************
     * Component initialization code.
     ********************************************/
    function init(context) {
        const localContext = (context && context.jquery) ? context[0] : context;

        const auth0Links = localContext.querySelectorAll('.mntl-auth0');

        // Initiate sign in if triggerLogin hash is detected
        if(window.location.hash.includes('triggerLogin') && auth0Links[0]){
            const attributes = getAttributes(auth0Links[0]);

            launchSignIn(attributes);
        }

        // STAR-54 Check cookie to see whether the user is logged in to fight browser cache
        const ddmaccountCookie = window.docCookies.getItem('ddmaccount');

        if (ddmaccountCookie) {
            document.documentElement.classList.add('signed-in');
        } else {
            document.documentElement.classList.remove('signed-in');
        }

        auth0Links.forEach((auth0Link) => {
            if (auth0Link.getAttribute('data-auth0-type')) {
                switch (auth0Link.getAttribute('data-auth0-type')) {
                    case 'signin':
                        setUpSignIn(auth0Link);
                        break;
                    case 'signout':
                        setUpSignOut(auth0Link);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    utils.readyAndDeferred(init);

    // Listen to this event to initialize auth0 if requested by dependent components.
    document.addEventListener(globalAuth0.CLIENT_INIT_EVENT_NAME, async (data) => {
        let { auth0Link } = data.detail;

        if (!auth0Link) {
            auth0Link = document.querySelector('.mntl-auth0');
        }

        // Get attributes off the first link
        const attributes = getAttributes(auth0Link);

        if (attributes) {
            await instantiateAuth0(attributes);
            await getAccessToken();
        } else {
            console.error('Unable to find required auth0 parameters for instantiation.');
        }
    });
}(window.Mntl.utilities || {}, window.Mntl.auth0 || {}));