window.Mntl = window.Mntl || {};

(function(){
    let cheetahScriptLoaded = false;
    let cheetahScriptLoading = false;

    function observerCallback(entries, observer) {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                const cheetahEmbed = entry.target;

                if (!cheetahScriptLoaded && !cheetahScriptLoading) {
                    cheetahScriptLoading = true;

                    Mntl.utilities.loadExternalJS({ src: "https://us-d.wayin.com/ui/ngx.embed.min.js" }, () => {
                        cheetahScriptLoaded = true;
                        cheetahEmbed.src = cheetahEmbed.dataset.src;
                        observer.unobserve(cheetahEmbed);
                    });

                } else {
                    cheetahEmbed.src = cheetahEmbed.dataset.src;
                    observer.unobserve(cheetahEmbed);
                }
            }
        });
    }

    function init() {
        const cheetahEmbeds = document.querySelectorAll('.mntl-sc-block-cheetahembed__iframe');
        const options = { rootMargin: '300px' };
        const observer = new IntersectionObserver(observerCallback, options);

        cheetahEmbeds.forEach((embed) => {
            observer.observe(embed);
        });
    }

    init();
})();