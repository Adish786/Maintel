// This code recreates the functionality of srcset and sizes that img tags have, for video tags
// it also lazyloads the videos by default if the browser supports the IntersectionObserver
(function(Mntl) {
    // function that will iterate through each video on the page and add the correct src or data-src based on format and srcset/sizes calculations.
    // these calculations mimic the browser's calculations for img srcset and sizes
    // if the browser supports IntersectionObserver then data-src will be added for lazy loading, if not then the src will be added immediately to the video
    // if the video did not have srcset or sizes specified it will have the data-src attribute which will be used to set the src
    function addSrcForVideos(videos, windowDPR, viewportWidth, observer) {
        const WEBM = 'webm';
        const MP4 = 'mp4';

        videos.forEach((video) => {
            let type;

            if (video.hasAttribute(`data-srcset-${WEBM}`)) {
                type = WEBM;
            } else if (video.hasAttribute(`data-srcset-${MP4}`)) {
                type = MP4;
            } else if (video.hasAttribute('data-src')) { // if there is no data-srcset-* attribute there will be a data-src attribute
                if (!observer) {
                    video.src = video.dataset.src;
                } else {
                    observer.observe(video);
                }

                return;
            } else {
                // this should not be possible, but added as a general fallback in case someone uses the 'mntl-gif__video' class by accident
                window.debug.error('The mntl-gif__video class has been applied to a non-gif component. This is a reserved class, please remove it.');

                return;
            }

            const srcset = video.getAttribute(`data-srcset-${type}`).split(', ');
            const sizes = video.getAttribute('data-sizes').split(', ');
            let matchingQueryWidth = sizes.pop();

            const srcsetItems = srcset.map(((srcsetItem) => {
                const [url, srcsetVideoWidth] = srcsetItem.split(' ');

                return {
                    url,
                    srcsetVideoWidth
                };
            }));

            // the values in sizes need to be in the format (QUERY) Xpx or (QUERY) Xvw or (QUERY) calc(X)
            const mediaQueries = sizes.map(((size) => {
                const splitIndex = size.indexOf(')') + 1;
                const query = size.slice(0, splitIndex);
                const width = size.slice(splitIndex);

                return {
                    query,
                    width
                };
            }));

            // get the width of the first matching media query from the sizes supplied, could be in px or vw units
            // eslint-disable-next-line object-curly-newline
            mediaQueries.some(({ query, width }) => {
                if (window.matchMedia(query).matches) {
                    matchingQueryWidth = width;

                    return true;
                }

                return false;
            });

            // set initial slotWidth equal to the current viewport width, this will be the fallback value in case the css calc() function is used
            // if calc function is used then the srcset matching process changes to compare the viewport width against the srcset url widths
            // this means performance benefits will only be seen at viewports smaller than the maxWidth specified for the srcset
            let slotWidth = viewportWidth;

            // if sizes does not use the css calc() function then update slotWidth to correct value from sizes
            if (matchingQueryWidth.indexOf('calc') === -1) {
                const isPx = matchingQueryWidth.slice(-2) === 'px';
                const widthValue = parseInt(matchingQueryWidth.slice(0, -2), 10);

                // we must take the window devicePixelRatio into account since we must recreate how the browser chooses the correct srcset url
                slotWidth = (isPx ? widthValue : widthValue * viewportWidth / 100) * windowDPR;
            }

            // Description copied from MDN docs, the below code recreates this process https://developer.mozilla.org/en-US/docs/Learn/HTML/Multimedia_and_embedding/Responsive_images#resolution_switching_different_sizes
            // So, with these attributes in place, the browser will:
            // Look at its device width.
            // Work out which media condition in the sizes list is the first one to be true.
            // Look at the slot size given to that media query.
            // Load the image referenced in the srcset list that has the same size as the slot or, if there isn't one, the first image that is bigger than the chosen slot size
            srcsetItems.some(({ url, srcsetVideoWidth }, i, arr) => { // eslint-disable-line object-curly-newline
                const videoWidth = parseInt(srcsetVideoWidth.slice(0, -1), 10);
                const lastSrcsetItem = i === arr.length - 1;

                // choose the first video that has a width equal to or greater than the slot
                // edge case if none of the srcset images are large enough and lastSrcsetItem is true then it will add the last srcset item since it is closest to the size needed
                if (videoWidth >= slotWidth || lastSrcsetItem) {
                    if (!observer) {
                        video.src = url;
                    } else {
                        video.dataset.src = url;
                        observer.observe(video);
                    }

                    return true;
                }

                return false;
            });
        });
    }

    function observerCallback(entries, observer) {
        entries.forEach((entry) => {
            const target = entry.target;

            if (entry.isIntersecting) {
                target.src = target.dataset.src;
                observer.unobserve(target);
            }
        });
    }


    let observer = null;

    if (IntersectionObserver) {
        const options = {
            root: null,
            rootMargin: '0px',
            threshold: 0.15
        };

        observer = new IntersectionObserver(observerCallback, options);
    }

    function init() {
        const videos = document.querySelectorAll('.mntl-gif__video');
        const windowDPR = window.devicePixelRatio;
        const viewportWidth = window.innerWidth;

        // script can load on pages that do not have videos, so we should exit the script
        if (videos.length === 0) {
            return;
        }

        // this will add the correct src or data-src attribute to each video based on it's data-srcset-* and data-src attribute
        addSrcForVideos(videos, windowDPR, viewportWidth, observer);
    }
    
    Mntl.utilities.readyAndDeferred(init);
})(window.Mntl || {});