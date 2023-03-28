/* eslint no-use-before-define: 0 */
window.Mntl = window.Mntl || {};

Mntl.JWPlayer = (function(utils, fnUtils) {
    const _existingMediaIds = [];
    const _playerData = [];
    const _events = { // jwPlayer Events: https://developer.jwplayer.com/jw-player/docs/developer-guide/api/javascript_api_introduction/#cheat-sheet-reference
        playlist() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Video Load',
                nonInteraction: true
            });
        },
        seek(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Seek start',
                eventValue: Math.round(e.position),
                nonInteraction: false
            });
            this.__dotDashPushToDataLayer({
                eventAction: 'Seek end',
                eventValue: Math.round(e.offset),
                nonInteraction: false
            });
        },
        adPlay(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Ad Play',
                nonInteraction: e.oldstate !== 'paused'
            });
        },
        adPause() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Ad Pause',
                nonInteraction: false
            });
        },
        play(e) {
            // in the context of this function, `this` refers to the instance of the player
            const playReason = this.__dotDashStarted ? e.playReason : 'external';
            const mediaType = this.__dotDashComplete ? 'Replay' : playReason;

            this.__dotDashPushToDataLayer({
                eventAction: ['Media', mediaType].join(' '),
                nonInteraction: e.playReason === 'autostart'
            });
            this.__dotDashComplete = false;
            // Differentiate the very first time the video plays with 'external' instead of playReason
            this.__dotDashStarted = true;
        },
        pause() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Media Pause',
                eventValue: 151,
                nonInteraction: false
            });
        },
        setupError(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Setup Error',
                eventOther: _getErrorInfo(e),
                nonInteraction: true
            });
        },
        playAttemptFailed(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Play Failed',
                eventOther: _getErrorInfo(e),
                nonInteraction: e.playreason === 'interaction'
            });
        },
        error(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Error',
                eventOther: _getErrorInfo(e),
                nonInteraction: true
            });
        },
        fullscreen(e) {
            this.__dotDashPushToDataLayer({
                eventAction: e.fullscreen ? 'Fullscreen Entered' : 'Fullscreen Exited',
                nonInteraction: false
            });
        },
        volume(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Volume change',
                eventValue: e.volume,
                nonInteraction: false
            });
        },
        mute(e) {
            this.__dotDashPushToDataLayer({
                eventAction: e.mute ? 'Mute' : 'Unmute',
                nonInteraction: false
            });
        },
        ready() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Player Load',
                nonInteraction: true
            });
        },
        complete() {
            // in the context of this function, `this` refers to the instance of the player
            this.__dotDashComplete = true;
            this.__dotDashPushToDataLayer({
                eventAction: 'Media Complete',
                nonInteraction: true
            });
        },
        // This may fire multiple times for a single ad tag if Google IMA is being used.
        // IIFE creates a function that will prevent multiple calls
        aderror: (function() {
            let tag;

            return function(e) {
                if (tag !== e.tag) {
                    tag = e.tag;
                    this.__dotDashPushToDataLayer({
                        eventAction: 'prerollerror',
                        eventOther: e.message,
                        nonInteraction: true
                    });
                }
            };
        }()),
        adError(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'Ad Error',
                eventOther: e.message,
                nonInteraction: true
            });
        },
        adImpression() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Ready For Preroll',
                nonInteraction: true
            });
        },
        adBreakStart() {
            this.__dotDashPushToDataLayer({
                eventAction: 'Ad Break Start',
                nonInteraction: true
            });
        },
        firstFrame(e) {
            this.__dotDashPushToDataLayer({
                eventAction: 'First Frame',
                eventValue: e.loadTime,
                nonInteraction: true
            });
        }
    };

    /*
     * getErrorInfo as per https://developer.jwplayer.com/jw-player/docs/developer-guide/api/errors-reference/
     * @param {Object} error object
     * @return {String} error concated string for GA
     */
    function _getErrorInfo(error) {
        return ['Message:', error.message, 'Code:', error.code, 'Source:', error.sourceError].join(' ');
    }

    /**
     * Instantiate the player and keep track of it in the _playerData array
     * @param       {Object} options user options
     * @param       {Object} setup   JWPlayer setup
     * @param       {Object} mediaSettings    mediaSettings @see _getMediaSettings
     * @return      {Boolean}
     */
    function _initPlayer(options, setup, mediaSettings) {
        _playerData.push(_instantiatePlayer(options, setup, mediaSettings));

        return true;
    }

    function _videoHasAdvertising(setupConfig) {
        return setupConfig.advertising.hasOwnProperty('schedule') && setupConfig.advertising.hasOwnProperty('client');
    }

    function _getCustParams(bids) {
        const custParams = [];

        bids.forEach((bid) => {
            for (const [key, value] of Object.entries(bid)) {
                if (value) {
                    let paramValue = value;

                    if (Array.isArray(value)) {
                        paramValue = value.join(',');
                    }
                    custParams.push(`${encodeURIComponent(key)}=${encodeURIComponent(paramValue)}`);
                }
            }
        });

        return custParams.join('&');
    }

    function _handleRTBRequest(options, tagBeforeBids) {
        let tag = tagBeforeBids;

        return new Promise((resolve) => {
            if (options.useTAMforVideoAds) {
                const videoSlot = {
                    config: {
                        id: options.divId,
                        sizes: [options.adSizes],
                        type: 'video'
                    }
                };

                return Mntl.RTB.initVideoSlot(videoSlot)
                    .then((bids) => {
                        if (bids.length > 0) {
                            const custParams = _getCustParams(bids);

                            tag = _mergeCustParams(custParams, tag);
                        }
                        resolve(tag);
                    });
            }

            return resolve(tag);
        });
    }

    function _mergeCustParams(newCustParams, tag) {
        const tagUrl = new URL(tag);
        const searchParams = new URLSearchParams(tagUrl.search);
        const existingCustParams = searchParams.get('cust_params');

        searchParams.set('cust_params', `${existingCustParams}&${newCustParams}`);
        tagUrl.search = searchParams;

        return tagUrl.toString();
    }

    /**
     * Create a JWPlayer Instance
     * @param       {Object} options user options
     * @param       {Object} setup   JWPlayer setup
     * @param       {Object} mediaSettings  media settings @see _getMediaSettings
     * @return      {Object}
     */
    function _instantiatePlayer(options, setup, mediaSettings) {
        const instance = window.jwplayer(options.divId);
        const videoId = setup.mediaid || setup.playlistUrl || 'noVideoIdFound';

        // instantiate the player
        const playerInstance = instance.setup(Object.assign(mediaSettings, setup));

        if (_videoHasAdvertising(setup)) {
            playerInstance.setPlaylistItemCallback((playlistItemConfig) =>
                new Promise((resolve, reject) => {
                    if (options.skipExistingMediaContent && _existingMediaIds.includes(playlistItemConfig.mediaid)) {
                        /* To move on to the next video in our playlist we need to reject the promise
                           https://developer.jwplayer.com/jwplayer/docs/jw8-javascript-api-reference#jwplayersetplaylistitemcallbackcallback
                        */
                        return reject();
                    }

                    return _handleRTBRequest(options, setup.advertising.schedule[0].tag).then((tag) => {
                        const modifiedplaylistItem = Object.assign({}, playlistItemConfig, {
                            adschedule: [{
                                tag,
                                offset: 'pre'
                            }]
                        });

                        playerInstance.removePlaylistItemCallback();
                        resolve(modifiedplaylistItem);
                    })
                        .catch(() => playlistItemConfig); // If RTB bidding fails, use unmodified playlist item config.
                }));
        }

        _addCCButton(instance);
        _initEvents(instance, options.eventCategoryType, videoId);
        _setupAutoplay(options.divId, options.playInView, instance);
        _setupLooping(instance);

        if (typeof setup.playlistUrl !== 'undefined') {
            const currentVideoTitle = document.querySelector(setup.playlistTitleSelector);
            const currentVideoDescription = document.querySelector(setup.playlistDescriptionSelector);
            const currentVideoLink = document.querySelector(setup.playlistLinkSelector);

            instance.on('playlistItem', (nextVideo) => {
                if (currentVideoTitle) {
                    currentVideoTitle.innerHTML = nextVideo.item.title;
                }

                if (currentVideoDescription) {
                    currentVideoDescription.innerHTML = nextVideo.item.socialDesc || nextVideo.item.description;
                }

                if (currentVideoLink) {
                    currentVideoLink.href = nextVideo.item.articleUrl;
                }
            });
        }

        if (
            typeof setup.trackingCodes !== 'undefined' &&
            setup.trackingCodes.length > 0
        ) {
            instance.once('play', () => {
                setup.trackingCodes.forEach((trackingCode) => {
                    let element;
                    let scriptContent;

                    if (trackingCode.type === 'script') {
                        element = document.createElement('script');
                        element.type = 'text/javascript';

                        if (trackingCode.src) {
                            element.src = trackingCode.src;
                        }

                        if (trackingCode.content) {
                            scriptContent = document.createTextNode(trackingCode.content);
                            element.appendChild(scriptContent);
                        }

                        document.getElementsByTagName('body')[0].appendChild(element);
                    } else {
                        element = trackingCode.content;

                        // if sponsored block data doesn't exist then this field is ''
                        if (element !== '') {
                            document.getElementsByTagName('body')[0].insertAdjacentHTML('beforeend', element);
                        }
                    }
                });
            });
        }

        return instance;
    }

    /**
     * Polls player during playback to report percent played
     * @param       {Object} instance JWPlayer Instance
     * @return      {Undefined}
     */
    function _reportPlayProgress(instance) {
        let progress = 10;
        let duration;
        let timer;

        function startPolling() {
            timer = window.setInterval(() => {
                const percentPlayed = (instance.getPosition() / duration) * 100;

                if (percentPlayed >= progress) {
                    instance.__dotDashPushToDataLayer({
                        eventAction: 'Percent played',
                        eventValue: progress,
                        nonInteraction: true
                    });
                    progress += 10;
                    if (progress > 90) {
                        window.clearInterval(timer);
                    }
                }
            }, 1000);
        }

        instance.on('play', () => {
            duration = instance.getDuration();
            startPolling();
        });

        instance.on('pause complete', () => {
            window.clearInterval(timer);
        });

        instance.on('seek', (e) => {
            const percentPlayed = Math.floor((instance.getPosition() / duration) * 10) * 10; // round down to the nearest 10%

            if (e.offset - e.position > 0 && percentPlayed > progress) {
                progress = percentPlayed;
                instance.__dotDashPushToDataLayer({
                    eventAction: 'Percent played',
                    eventValue: progress,
                    nonInteraction: true // seek is an interaction but this is an update to percent played
                });
            }
        });
    }

    /**
     * Report events after each time in the supplied array has elapsed
     * @param       {Object}   instance  JWPlayer Instance
     * @param       {[Number]} [timeSec] array of times to report
     * @return      {Undefined}
     */
    function _reportSecondsEvents(instance, timeSec) {
        const times = [].concat(timeSec).sort();
        let timer;

        function startPolling() {
            timer = window.setInterval(() => {
                if (instance.getPosition() >= times[0]) {
                    instance.__dotDashPushToDataLayer({
                        eventAction: ['First', times.shift(), 'Seconds Played'].join(' '),
                        nonInteraction: true
                    });
                    if (!times.length) {
                        window.clearInterval(timer);
                    }
                }
            }, 50);
        }

        instance.on('play', () => {
            if (times.length) {
                startPolling();
            }
        });

        instance.on('pause complete', () => {
            window.clearInterval(timer);
        });
    }

    /**
     * Pause all other instances of players on the page when playing any single player
     * @param       {Object}   instance  JWPlayer Instance
     * @return      {Undefined}
     */
    function _pauseOtherPlayers(instance) {
        function pauseOtherPlayers() {
            _playerData.forEach((playerInstance) => {
                if (playerInstance.id !== instance.id && playerInstance.getState() === 'playing') {
                    playerInstance.pause();
                }
            });
        }

        instance.on('play', pauseOtherPlayers);
        instance.on('adPlay', pauseOtherPlayers);
    }

    /**
     * Add a custom CC button that toggles captions on/off, rather than the default behavior of displaying the caption window
     * @param       {Object}   instance  JWPlayer Instance
     * @return      {Undefined}
     */
    function _addCCButton(instance) {
        // Wait for captionsList to be loaded
        instance.on('captionsList', () => {
            const activeClass = 'cc-icon--active';

            // getCaptionsList() returns an array of captions, with the first item being 'off'. A length > 1 tells us the video has a caption track added
            if (instance.getCaptionsList().length > 1) {
                // Add our custom button (default btn is hidden in CSS)
                instance.addButton(
                    '<svg class="jw-svg-icon jw-svg-icon-cc-on" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><use xlink:href="#jw-player-cc"></use></svg>',
                    'Closed Captions',
                    (e) => {
                        e.currentTarget.classList.toggle(activeClass);
                        instance.setCurrentCaptions(instance.getCurrentCaptions() ? 0 : 1);
                    },
                    'cc-icon',
                    `cc-icon ${(instance.getCurrentCaptions() ? activeClass : '')}`
                );
            }
        });
    }

    /**
     * Attach all event handlers
     * @return {Undefined}
     */
    function _initEvents(instance, eventCategoryType, videoId) {
        instance.__dotDashPushToDataLayer = utils.pushToDataLayer({
            event: 'videoEvent',
            eventCategory: (`JWPlayer ${eventCategoryType}`).trim(),
            eventLabel: videoId
        });

        if (instance.getConfig().pauseotherplayers) {
            _pauseOtherPlayers(instance);
        }

        _reportPlayProgress(instance);
        _reportSecondsEvents(instance, [10]); // report :10sec event
        Object.keys(_events) // get all own keys from object as an array
            .forEach((key) => { // loop keys array and attach events
                instance.on(key, _events[key].bind(instance));
            });
    }

    /**
     * Custom autoplay when video is in viewport. Used when JW's built in just
     * won't do
     * @param       {String}  wrapperId     Element ID
     * @param       {Integer} percentInView Play when this percent is in the viewport
     * @param       {Object}  instance      JWPlayer Instance
     */
    function _setupAutoplay(wrapperId, percentInView, instance) {
        let inView;

        if ([0, 50].indexOf(percentInView) < 0) { // Not 0 or 50
            inView = utils.isElementVisibleY.bind(
                null,
                document.getElementById(wrapperId).parentNode, // JW pulls shennanigans on the container so listen on the parent
                percentInView / 100,
                0
            );

            instance.once('ready', () => {
                if (inView()) {
                    instance.play();
                } else {
                    window.addEventListener('mntl.scroll', function handleScroll() {
                        if (inView()) {
                            instance.play();
                            window.removeEventListener('mntl.scroll', handleScroll);
                        }
                    });
                }
            });
        }
    }

    /**
     * Custom looping because JW's built in shows black frame in between loops
     * @param       {Object}  instance      JWPlayer Instance
     */
    function _setupLooping(instance) {
        if (!instance.getConfig().repeat) {
            return;
        }

        instance.once('play', () => {
            const duration = instance.getDuration();
            const loopPoint = duration - 0.5;

            instance.on('time', (e) => {
                if (e.position >= loopPoint) {
                    instance.seek(0);
                }
            });
        });
    }

    /**
     * Resolve settings to optimal defaults.
     * @param       {Integer} playInView 1-100[%]
     * @param       {Object} setup       JWPlayer Setup
     * @param       {Array of Strings} trackingCodes  passed trackingCodes for jwPlayer
     * @return      {Object}             Corrected Setup
     */
    function _resolveSettings(playInView, setup, trackingCodes) {
        const resolvedSetup = Object.assign({}, setup);

        if (setup.autostart) {
            if (!playInView) {
                window.debug.error(
                    'Mntl.JWPlayer does not know how to handle autostart = ',
                    setup.autostart,
                    ' and playOnPercentInView = ',
                    playInView,
                    '. Video will autoplay. Try changing autostart to true and playOnPercentInView to a number from 1-100.'
                );
            } else if (playInView === 50) { // use built in
                resolvedSetup.autostart = 'viewable';
            } else {
                resolvedSetup.autostart = false;
            }
        }

        if (typeof trackingCodes !== 'undefined') {
            resolvedSetup.trackingCodes = trackingCodes.map((trackingCode) => {
                const resolvedTrackingCode = {};
                const scriptRegex = '<script .+?>';
                const srcRegex = 'src="(.+?)"';
                const scriptContentRegex = '\>(.*?)\<';
                const scriptTags = trackingCode.match(scriptRegex);
                const scriptSrc = trackingCode.match(srcRegex);
                const scriptContent = trackingCode.match(scriptContentRegex);

                if (scriptTags === null) {
                    // Assume other tags we just inject on the page (e.g. img tag)
                    resolvedTrackingCode.type = 'tag';
                    resolvedTrackingCode.content = trackingCode;
                } else {
                    resolvedTrackingCode.type = 'script';

                    if (scriptSrc !== null) {
                        resolvedTrackingCode.src = scriptSrc[1];
                    }
                    // HTML5 security specs will not evaluate script tags inserted as an HTML string
                    // So need to separate this out and recreate the node down stream to run the js in this inline script tag

                    if (scriptContent !== null) {
                        resolvedTrackingCode.content = scriptContent[1];
                    }
                }

                return resolvedTrackingCode;
            });
        }

        return resolvedSetup;
    }

    /**
     * Build Media Settings object from data, video options, and setup
     * @param       {Object} data       JWPlayer Delivery API Response
     * @param       {Object} options    JWPlayer options
     * @param       {Object} setup      JWPlayer setup
     * @return      {Object}            Returns object to be used with _instantiatePlayer @see _instantiatePlayer
     */
    function _getMediaSettings(data, options, setup) {
        let videoMediaSettings = {};
        const tracks = data.playlist[0].tracks;

        const {
            displayMode, isPlaylist, mediaSize, showCaptions, useMotionThumbnails
        } = options;

        const { playlistShelfId } = setup;


        if (mediaSize) {
            videoMediaSettings = _getBestMediaMatch(data.playlist[0].sources, mediaSize);
        } else {
            // Would like to only pass this for source.
            videoMediaSettings.sources = data.playlist[0].sources;
        }

        if (fnUtils.getDeepValue(tracks, 0, 'kind') === 'captions' && showCaptions) { // check to make sure there is a caption track and that showCaptions is on
            tracks[0].default = true; // set 'default' to true to make the first track appear automatically
        }

        videoMediaSettings.tracks = tracks;
        videoMediaSettings.image = data.playlist[0].image;

        if (useMotionThumbnails) {
            videoMediaSettings.images = data.playlist[0].images;
        }

        if (isPlaylist) {
            videoMediaSettings.playlist = data.playlist;
            videoMediaSettings.related = {
                displayMode,
                oncomplete: 'none',
                selector: (typeof playlistShelfId === 'undefined') ? '' : `#${playlistShelfId}`
            };
        }

        return videoMediaSettings;
    }

    /**
     * Get best media match for video from video sources returned from JWPlayer
     * @param       {Array} videoSources    Array of video sources from JWPlayer
     * @param       {Integer} mediaSize     media size provided from init options
     * @return      {Object}                Returns object with file and type of best match video.  If no exact width
     *                                      match exists, the last source will be returned with best quality video
     */
    function _getBestMediaMatch(videoSources, mediaSize) {
        const mediaMatch = {};

        for (let i = 0; i < videoSources.length; i++) {
            const src = videoSources[i];

            if (src.hasOwnProperty('width')) {
                mediaMatch.file = src.file;
                mediaMatch.type = src.type;
                if (src.width === mediaSize) {
                    break;
                }
            }
        }

        return mediaMatch;
    }

    /**
     * Load player and video data
     * @param  {[type]} options   [description]
     * @param  {[type]} setup     [description]
     * @return {Undefined}
     */
    function init(options, setup) {
        if (!options.isPlaylist) {
            _existingMediaIds.push(setup.mediaid);
        }
        const lazyVideoOptions = {
            rootMargin: options.intersectionMargin,
            threshold: 0
        };
        let lazyVideoObserver;

        function _init() {
            function _requestVideo() {
                const jwVideoRequest = new XMLHttpRequest();
                const apiReq = options.isPlaylist ? 'playlists' : 'media';
                const mediaId = options.isPlaylist ? setup.playlistUrl.substring(setup.playlistUrl.lastIndexOf('/') + 1) : setup.mediaid;
                const jwVideoEndpoint = `https://cdn.jwplayer.com/v2/${apiReq}/${mediaId}`;

                window.jwplayer.key = options.accountKey;

                jwVideoRequest.open('GET', jwVideoEndpoint, true);

                jwVideoRequest.onload = function() {
                    let data;

                    if (this.status === 200) {
                        try {
                            data = JSON.parse(this.responseText);
                            _initPlayer(options,
                                _resolveSettings(options.playInView, setup, options.trackingCodes),
                                _getMediaSettings(data, options, setup)
                            );
                        } catch (error) {
                            window.debug.error(`Mntl.JWPlayer returned an unexpected response with ${error}`);
                        }
                    } else {
                        window.debug.error(`Mntl.JWPlayer did not return a video. Error is ${this.statusText}`);
                    }
                };

                jwVideoRequest.onerror = function() {
                    window.debug.error(`Mntl.JWPlayer did not return a video. Error is ${this.statusText}`);
                };

                jwVideoRequest.send();
            }

            utils.loadExternalJS(
                {
                    src: [
                        '//content.jwplatform.com/libraries/',
                        options.playerId, '.js'
                    ].join('')
                },
                _requestVideo
            );
        }

        /**
         * _readyInit function. Sets defer or execute immediately depending on options.
         * @return {Undefined}
        */
        function _readyInit() {
            if (options.defer) {
                utils.onLoad(_init);
            } else {
                _init();
            }
        }

        function _setUpVideoObserver() {
            if ('IntersectionObserver' in window) {
                lazyVideoObserver = new IntersectionObserver(
                    (entries) => {
                        entries.forEach((video) => {
                            if (video.isIntersecting) {
                                _readyInit();
                                lazyVideoObserver.unobserve(video.target);
                            }
                        });
                    }, lazyVideoOptions
                );

                lazyVideoObserver.observe(document.getElementById(options.divId));
            } else {
                _readyInit();
            }
        }

        function _handleRequiredAdConsent() {
            return new Promise((resolve) => {
                if (_videoAdRequiresUserConsent()) {
                    // no need to wait for consent to change if user is in an opt-out region
                    if (Mntl.CMP.isOptInConsent()) {
                        Mntl.CMP.onConsentChange(() => {
                            resolve(Mntl.CMP.hasTargetingConsent());
                        });
                    } else {
                        resolve(Mntl.CMP.hasTargetingConsent());
                    }
                } else {
                    resolve(true);
                }
            });
        }

        function _videoAdRequiresUserConsent() {
            return Mntl.CMP && Mntl.CMP.isConsentRequired();
        }

        function _setPersonalizedAdsSetting(showPersonalizedAds) {
            if (!showPersonalizedAds) {
                setup.advertising.bids = null;
                setup.advertising.schedule.forEach((scheduleItem) => {
                    scheduleItem.tag = _setRequestNonPersonalizedAds(scheduleItem.tag);
                });
            }
        }

        function _setRequestNonPersonalizedAds(tag) {
            return `${tag}&npa=1`;
        }

        if (_videoHasAdvertising(setup)) {
            _handleRequiredAdConsent()
                .then(_setPersonalizedAdsSetting)
                .finally(() => {
                    _setUpVideoObserver();
                });
        } else {
            _setUpVideoObserver();
        }
    }

    return { init };
}(Mntl.utilities || {}, Mntl.fnUtilities || {}));
