window.Mntl = window.Mntl || {};

(function(utils) {
    const lrsIngredients = document.querySelector('.mntl-lrs-ingredients');
    //eslint-disable-next-line object-curly-newline
    const { verticalPrefix, loadOffset } = lrsIngredients.dataset;
    const lrsScript = `https://cdn.polaris.me/cva/lrs/c/${verticalPrefix}-calvera.js`;

    let lrsScriptLoaded = false;
    let lrsScriptLoading = false;

    function handleIntersection(entries, observer) {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {

                if (!lrsScriptLoaded && !lrsScriptLoading) {
                    lrsScriptLoading = true;

                    const externalJSData = {
                        src: lrsScript,
                        defer: true
                    };
        
                    utils.loadExternalJS(externalJSData, () => {
                        lrsScriptLoaded = true;
                        observer.unobserve(lrsIngredients);
                    });
                }
            }
        });
    }

    function init() {
        if (verticalPrefix) {
            //To be set by proctor test or 300px by default
            const rootMarginValue = (loadOffset) ? `${loadOffset}px` : '300px';
            const options = { rootMargin: rootMarginValue };
            const observer = new IntersectionObserver(handleIntersection, options);

            observer.observe(lrsIngredients);
        } else {
            console.error('Vertical prefix not set, cannot fetch appropriate calvera script');
        }
    }

    init();
})(window.Mntl.utilities || {});
