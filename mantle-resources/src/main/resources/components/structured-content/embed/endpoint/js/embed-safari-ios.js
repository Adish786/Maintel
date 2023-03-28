window.Mntl = window.Mntl || {};

/**
 * Hack alert.
 *
 * This is to address tiktok being cut off in iOS only. Case: GLBE-8266 as of Jan 2021
 *
 * Upon observation of our content in the /embed endpoint tiktok's embed script used to adjust the
 * height is not setting the correct height for their content. (It sometimes works on iOS if validation is requested)
 * Our messaging system sees and sets the height that tiktok provides.
 *
 * For some reason on iOS tiktok only sets the truncated height. On Desktop or Android phones, Tiktok's javascript triggers
 * additional height changes that our messages can pick up and set the correct height (i.e. on IOS the number of message events
 * is less than what was observed on desktop)
 *
 * Why does this timeout work? Tiktok's embed.js aggressively keeps their sizes. Upon modification to the size
 * once the video has loaded will have the embed.js force a resize calculation and correct itself.
 *
 * A timeout after the video has loaded with a height modification will retrigger Tiktok's recalculation to the right iframe size.
 *
 * This is super fragile and only a TEMP solution before we investigate Embedly with additional content OR
 * we have a new solution for Embedded content.
 *
 */
(function(utils) {
    const [component] = document.getElementsByClassName('mntl-embed-endpoint-content');
    const provider = component && component.getAttribute('data-provider');
    const tiktok = provider.includes('tiktok');

    if (tiktok) {
        utils.onLoad(() => {
            let iframeLoaded = false;
            const config = {
                attributes: true,
                characterData: true,
                characterDataOldValue: true,
                childList: true,
                subtree: true,
                attributeFilter: ['height', 'style']
            };
            const observer = new MutationObserver((mutations) => {
                const [tiktokIframe] = document.getElementsByTagName('iframe');

                mutations.forEach(() => {
                    // Depending on how slow the connection is, Tiktok may not have loaded the iframe
                    // containing the tiktok video. The placeholder block loaded by Tiktok is
                    // entirely replaced by an iframe (not sure why? since they delete a bunch of placeholder HTML
                    // to inject an iframe but ok...)
                    if (tiktokIframe && !iframeLoaded) {
                        iframeLoaded = true;

                        setTimeout(() => {
                            // It doesn't matter what height is used, Tiktok's included js
                            // forces a new height if it detects a change in the iframe height.
                            tiktokIframe.style.height = '1px';
                        }, 4000);

                        observer.disconnect();
                    }
                });
            });

            observer.observe(component, config);
        });
    }
})(Mntl.utilities || {});
