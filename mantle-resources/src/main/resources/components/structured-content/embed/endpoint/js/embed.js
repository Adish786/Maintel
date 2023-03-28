window.Mntl = window.Mntl || {};

/**
 * Embeds are intended to be served by the /embed endpoint inside an iframe.
 * One of the problems of serving content inside an iframe is knowing how to size the iframe.
 * Although the [oEmbed](https://oembed.com) spec requires communicating the width/height of the embed
 * we've observed that some providers (e.g. twitter, instagram) do not communicate the height.
 * To solve this problem we compute the height of the content inside the iframe and
 * communicate it back to the parent so that it can resize the iframe accordingly.
 * Unless the embed provider fires a custom event that tells us when the content is ready
 * we compute and communicate the height on page load.
 *
 * Counterpart of mantle-resources/src/main/resources/components/structured-content/embed/embed.js
 */
(function(utils) {
    const template = document.querySelector('.mntl-embed-endpoint-template');
    const component = document.querySelector('.mntl-embed-endpoint-content');
    const provider = component && component.getAttribute('data-provider');
    const params = utils.getQueryParams();

    function computeHeight() {
        if (!template || !component) {
            return 0; // "collapse" the iframe on failure
        }

        // use the height of the template instead of the component because something
        // might creep into the document outside the component or to capture the
        // height of any margin/padding on outter elements, e.g. body.
        return template.getBoundingClientRect().height;
    }

    // Note from GLBE-9000. Possible oddities with the messaging system with Tiktok only on Safari?
    // Unknown what exact Safari feature causes issue but disabling this code solves GLBE-9000 at the /embed
    // end point but we fail to update the parent iframe when looking at this in content
    function postMessage() {
        const data = {
            embedHeight: computeHeight(), // the new height of the content inside the iframe
            embedId: params.id // the ID of the embed component that spawned this iframe
        };

        window.parent.postMessage(data, '*');
    }

    utils.onLoad(() => {
        const observedProviders = ['facebook-page', 'facebook-post', 'facebook-video', 'instagram', 'reddit', 'tiktok', 'twitter'];

        if (observedProviders.indexOf(provider) !== -1 || provider.includes('iframely')) {
            const config = {
                attributes: true,
                characterData: true,
                characterDataOldValue: true,
                childList: true,
                subtree: true,
                attributeFilter: ['height', 'style']
            };

            const observer = new MutationObserver((mutations) => {
                mutations.forEach(() => {
                    postMessage();
                });
            });

            observer.observe(component, config);
        }

        postMessage();
    });
})(Mntl.utilities || {});
