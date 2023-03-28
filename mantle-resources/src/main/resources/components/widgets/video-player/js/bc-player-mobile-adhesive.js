window.Mntl = window.Mntl || {};

(function (utils) {
    const ADHESIVE_SHOWS_IN_MILLS = 20000; // 20 seconds
    let primaryVideo = document.querySelector('.article__primary-video .jumpstart-js');
    const broadVideo = document.querySelector('.mntl-bcplayer-broad-mobile .jumpstart-js');
    const hasBroadVideo = document.body.contains(broadVideo);
    const hasPrimaryVideo = document.body.contains(primaryVideo);
    let primaryVideoLoaded = false;
    let replacedAdWithVideo = false;
    let stickyAd = null;

    function getReplacementHTML(title) {
        let replacementHtml = '<div class="bc-player-mobile-adhesive__inner">';

        replacementHtml += `<div class="bc-player-mobile-adhesive__video"></div>`;
        replacementHtml += `<div class="bc-player-mobile-adhesive__side"><span class="bc-player-mobile-adhesive__side-title">${title}</span></div>`;
        replacementHtml += '<button class="bc-player-mobile-adhesive__close" aria-label="Close">';
        replacementHtml += '<span class="bc-player-mobile-adhesive__close-inner">';
        replacementHtml += '<svg class="icon icon-close"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-close"></use></svg>';
        replacementHtml += '</span>';
        replacementHtml += '</button>';
        replacementHtml += '</div>';

        return replacementHtml;
    }

    function setVideoDataAttributes(video, videoWrapper) {
        // AXIS-3565 We want to set the mute flags here explicitly. If the source video is an autoplay video
        // the browser will auto mute the audio and cause the bug outlined in AXIS-3565 because we broke autoplay video policies.
        // Since the adhesive video is a muted autoplay video we'll need to add these states so that the video player can toggle the sound again.
        video.classList.add('jumpstart-video-muted');
        video.setAttribute('data-muted', 'true');
        video.removeAttribute('data-preventstart');
        video.setAttribute('data-is-adhesive', 'true'); // for ads
        video.setAttribute('data-mobile-autoplay', 'true');
        videoWrapper.classList.remove('is-hidden');
    }

    function handleEvents(adContainer) {
        const videoInner = adContainer.querySelector('.bc-player-mobile-adhesive__inner');
        const video = videoInner.querySelector('div[id^="jumpstart_player"]');

        let isFullscreen = false;

        // Handle change on video to update video title on broadvideo
        if (hasBroadVideo) {
            video.addEventListener('jumpstart:core-player-metadata-available', (e) => {
                document.querySelector('.bc-player-mobile-adhesive__side-title').innerText = e.target.metadata.$name;
            });
        }

        // Handle click events
        videoInner.addEventListener('click', (e) => {
            // Handle close
            if (e.target.closest('.bc-player-mobile-adhesive__close')) {
                window.jumpstart.pauseVideoOrAd(video);

                // Send event tracking for close player only if BV
                if (hasBroadVideo) {
                    utils.pushToDataLayer({
                        event: "videoEvent",
                        eventAction: "Close Video Player",
                        eventCategory: 'jumpstartPlayer',
                        eventLabel: video.getAttribute('data-video-id'),
                        nonInteraction: false
                    })();
                }

                adContainer.remove();
            } else {
                // Handle fullscreen
                isFullscreen = video.getState('isFullscreen');

                if (!isFullscreen) {
                    window.jumpstart.fullscreen(video);
                }
            }
        });
    }

    //Initiates the video replacement on the Sticky Ad Container
    function initVideoReplacement(stickyAd, videoWrapper, videoTitle) {
        const adContainer = stickyAd.parentNode;
        const video = videoWrapper.querySelector('video');
       
        setVideoDataAttributes(video, videoWrapper);

        adContainer.className = 'bc-player-mobile-adhesive';
        adContainer.innerHTML = getReplacementHTML(videoTitle);

        // Append player to ad container
        adContainer.querySelector('.bc-player-mobile-adhesive__video').appendChild(videoWrapper);

        // Load player
        window.jumpstart.initializePlayer(video);
        handleEvents(adContainer);
    }

    function replaceAdWithVideo() {

        if (stickyAd && ((hasPrimaryVideo && primaryVideoLoaded) || hasBroadVideo) && !replacedAdWithVideo) {
            window.setTimeout(() => {
                const videoWrapper = (hasPrimaryVideo) ? document.querySelector('.article__primary-video-mobile-adhesive .jumpstart-js-wrapper') : document.querySelector('.mntl-bcplayer-broad-mobile .jumpstart-js-wrapper');
                const videoTitle = (hasPrimaryVideo && primaryVideoLoaded) ? primaryVideo.querySelector('video').getAttribute('title') : '';
                
                Mntl.GPT.destroySlotById(stickyAd.id);

                initVideoReplacement(stickyAd, videoWrapper, videoTitle);

                replacedAdWithVideo = true;

            }, ADHESIVE_SHOWS_IN_MILLS);
        }
    }

    window.addEventListener('adRendered', (event) => {
        const adElement = event.target;

        if (adElement.id === 'mob-adhesive-banner-fixed') {
            stickyAd = adElement;

            replaceAdWithVideo();
        }
    });

    if (primaryVideo) {
        primaryVideo.addEventListener('jumpstart:core-player-prepared', () => {
            primaryVideo = document.querySelector('.article__primary-video .jumpstart-js');
            primaryVideoLoaded = true;
            replaceAdWithVideo();
        });
    }

})(window.Mntl.utilities || {});