window.Mntl = window.Mntl || {};

(function(utils) {
    let jumpstartLoaded = false;
    const allVideoWrapperElements = document.querySelectorAll('.mntl-bcplayer');
    const broadVideoWrapperElement = document.querySelector('.mntl-bcplayer-broad');
    const { clickLoad } = allVideoWrapperElements[0].querySelector('video').dataset;

    // Loads the Jumpstart video script.
    function loadVideoScript() {
        if (jumpstartLoaded) {
            return;
        }

        const JS_VERSION_PARAM = 'jumpstart_version';
        const params = new Proxy(new URLSearchParams(window.location.search), { get: (searchParams, prop) => searchParams.get(prop) });
        let jumpstartScript = 'https://cdn.video.meredithcorp.io/jumpstart/v4/js/jumpstart.min.js';

        jumpstartLoaded = true;

        function setJsVersionCookie(scriptUrl) {
            window.docCookies.setItem(JS_VERSION_PARAM, scriptUrl, '', '/', utils.getDomain());
        }

        if (params[JS_VERSION_PARAM]) {
            setJsVersionCookie(params[JS_VERSION_PARAM]);
        }

        if (window.docCookies.getItem(JS_VERSION_PARAM)) {
            const jumpstartBundleBase = 'https://cdn.video.meredithcorp.io/v4/bundles';
            const jumpstartBundleVersion = decodeURIComponent(window.docCookies.getItem(JS_VERSION_PARAM));

            jumpstartScript = `${jumpstartBundleBase}/${jumpstartBundleVersion}`;
        }

        // Load video script
        utils.loadExternalJS({ src: jumpstartScript });
    }

    // Given a video DOM element with the required data attributes, lazy load a player.
    function initializePlayer(player) {
        function loadVideo(player) {
            if (!player) {
                return;
            }

            const playElement = player.nextElementSibling;

            player.removeAttribute('data-preventstart');
            window.jumpstart.initializePlayer(player);

            // If play element exists remove it so that the native video play elements take over.
            if (playElement) {
                playElement.remove();
            }
        }

        if (window.jumpstart) {
            loadVideo(player);
        } else {
            window.addEventListener('jumpstartLoad', () => {
                loadVideo(player);
            });
        }

        loadVideoScript();
    }

    // Loads lazy video objects that come into view.
    function observerCallback(entries, observer) {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                const videoBlock = entry.target;
                const player = videoBlock.querySelector('.jumpstart-js');

                videoBlock.classList.remove('jumpstart-js-wrapper--lazy');

                observer.unobserve(videoBlock);

                if (!jumpstartLoaded && videoBlock.classList.contains('jumpstart-native-wrapper')) {
                    loadVideoScript();
                } else if (player) {
                    initializePlayer(player);
                }
            }
        });
    }

    function initializeBcVideo() {
        // Immediately load script to load all instant video blocks if they exist.
        const videoBlock = document.querySelector('.jumpstart-js-wrapper:not(.jumpstart-js-wrapper--lazy):not(.is-hidden)');

        if (videoBlock) {
            loadVideoScript();
        }

        // Otherwise lazy load script and video objects as they come into view.
        const allVideoBlocks = document.querySelectorAll('.jumpstart-js-wrapper--lazy');

        const {
            stickyAutoPlayVideoBlocks,
            lazyVideoBlocks
        } = [...allVideoBlocks].reduce((acc, block) => {
            const video = block.querySelector('video');

            if (video.dataset.autoplay === 'true' && video.dataset.stickyplay === 'true') {
                acc.stickyAutoPlayVideoBlocks.push(block)
            } else {
                acc.lazyVideoBlocks.push(block)
            }

            return acc;
        }, {
            stickyAutoPlayVideoBlocks: [],
            lazyVideoBlocks: []
        });

        if (stickyAutoPlayVideoBlocks && stickyAutoPlayVideoBlocks.length) {
            stickyAutoPlayVideoBlocks.forEach((block) => {
                const autoplayVideo = block.querySelector('video.jumpstart-js');

                initializePlayer(autoplayVideo);
            });

        }

        if (lazyVideoBlocks && lazyVideoBlocks.length) {
            const observerOptions = { root: null };
            const observer = new IntersectionObserver(observerCallback, observerOptions);

            lazyVideoBlocks.forEach((block) => {
                if (block) {
                    observer.observe(block);
                }
            });
        }
    }

    /**
     * Helper function used by the init() to setup the mobile video experience to load
     * only by click.
     */
    function setupInitializationByClick() {
        allVideoWrapperElements.forEach((videoWrapperElem) => {
            const videoElement = videoWrapperElem.querySelector('.jumpstart-js');
            const playElement = videoWrapperElem.querySelector('.jumpstart-play-placeholder');

            // Absent when using the minimal native player
            if (playElement) {
                playElement.addEventListener('click', () => {
                    initializePlayer(videoElement);
                    playElement.classList.add('loading'); // Setup "loading" spinner
                    playElement.querySelector('button').remove(); // Remove inner play button
                });
            }
        });
    }

    /**
     * Push broadvideo close events to the dataLayer
     * 
     * @param {string} videoId Id of the jumpstart videoPlayer
     */
    function broadVideoCloseEventTracking(videoId) {
        utils.pushToDataLayer({
            event: "videoEvent",
            eventAction: "Close Video Player",
            eventCategory: 'jumpstartPlayer',
            eventLabel: videoId,
            nonInteraction: false
        })();
    }

    /**
     * Helper function used by the init() to check the window scroll depth if there is
     * a broad video player on the page, and only load the player if we are past the scroll
     * threshold. Also sets up closing click events on the broad video player.
     */

    function checkPixelDepthAndInitializePlayer() {
        const videoPlayer = broadVideoWrapperElement.querySelector('.jumpstart-js');
        const videoCloseButton = broadVideoWrapperElement.querySelector('.mntl-bcplayer-broad__title-icon--close');

        function initializeBroadVideo() {
            broadVideoWrapperElement.classList.remove('is-hidden');

            window.jumpstart.initializePlayer(videoPlayer);

            // Handle closing video player
            videoCloseButton.addEventListener('click', () => {
                // Re-declaring the variable videoplayer to get the associated data and functions available after jumpstart.initializePlayer
                const videoPlayer = broadVideoWrapperElement.querySelector('.jumpstart-js');

                window.jumpstart.pauseVideoOrAd(videoPlayer);
                broadVideoWrapperElement.classList.add('is-hidden');

                broadVideoCloseEventTracking(videoPlayer.getAttribute('data-video-id'));
            });
        }

        if (window.scrollY > broadVideoWrapperElement.dataset.pixelDepth) {
            if (window.jumpstart) {
                initializeBroadVideo();
            } else {
                window.addEventListener('jumpstartLoad', () => {
                    initializeBroadVideo();
                });
            }

            window.removeEventListener('mntl.scroll', checkPixelDepthAndInitializePlayer);
        }
    }

    function setupVideoPoster() {
        const observerOptions = { rootMargin: '250px' };
        // Use a single observer for slight performance gains when observing multiple elements on the page
        const observer = new IntersectionObserver((entries, observer) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    const videoElement = entry.target;
                    const {poster} = videoElement.dataset;

                    if (poster) {
                        videoElement.poster = poster;
                        delete videoElement.dataset.poster; // Delete the data-poster attribute directly from the element
                    }

                    observer.unobserve(videoElement);
                }
            });
        }, observerOptions);

        allVideoWrapperElements.forEach((videoWrapperElem) => {
            const videoElement = videoWrapperElem.querySelector('.jumpstart-js');

            // If we set the data-poster field then set it on the poster field to control it via lazy loading
            // AXIS-3545 explored using lazysizes but we found the image was loading multiple times
            // So we ended up using an intersection observer to load 1 image properly
            //
            // Absent when using the minimal native player
            if (videoElement && videoElement.dataset.poster) {
                observer.observe(videoElement);
            }
        });
    }

    function init() {
        setupVideoPoster();

        if (clickLoad === 'true') {
            setupInitializationByClick();
        } else {
            window.addEventListener('readyForDeferredScripts', () => {
                if (broadVideoWrapperElement) {
                    window.addEventListener('mntl.scroll', checkPixelDepthAndInitializePlayer);
                }

                initializeBcVideo();
            });
        }
    }

    init();

})(window.Mntl.utilities || {});
