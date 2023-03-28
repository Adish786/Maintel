(function(utils){
    let digiohScriptLoaded = false;
    let digiohScriptLoading = false;

    function overwriteIframeTitle(mutationList, embedTitle, observer) {
        for (const mutation of mutationList) {
            if (mutation.type === 'childList') {
                const parentNode = mutationList[0].target;
                
                // Grab iframe from parentNode
                const iframe = parentNode.querySelector('iframe');

                if (iframe !== null) {
                    // Overwrite iframe title attr with title from Selene
                    iframe.title = embedTitle;
                    observer.disconnect();

                    // Early return, prevent further looping for other mutations
                    return;
                }
            }
        }
    }

    function observeChildNodeChanges(embed) {
        const config = { 
            childList: true, 
            subtree: true 
        };

        /* Explicitly use arrow function for the callback executed on 
        observed node to pass in embed title to overwrite iframe title */
        const observer = new MutationObserver((mutationList) => {
            overwriteIframeTitle(mutationList, embed.dataset.title, observer);
        });

        observer.observe(embed, config);
    }

    function observerCallback(entries, observer) {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                const digiohEmbed = entry.target;
                
                if (!digiohScriptLoaded && !digiohScriptLoading) {
                    digiohScriptLoading = true;

                    utils.loadExternalJS({ src: "https://www.lightboxcdn.com/static/lightbox_dotdash.js" }, () => {
                        digiohScriptLoaded = true;
                        observer.unobserve(digiohEmbed);
                    })
                } else {
                    observer.unobserve(digiohEmbed);
                }
            }
        });
    }

    function init() {
        const digiohEmbeds = document.querySelectorAll('.mntl-sc-block-digiohembed__container');
        const options = { rootMargin: '300px' };
        const observer = new IntersectionObserver(observerCallback, options);

        digiohEmbeds.forEach((embed) => {
            observer.observe(embed);
            observeChildNodeChanges(embed);   
        });
    }

    init();
})(Mntl.utilities || {});