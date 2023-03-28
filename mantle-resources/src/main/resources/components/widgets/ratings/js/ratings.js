window.Mntl = window.Mntl || {};

Mntl.Ratings = (function(utils) {
    let initFlag = false; // Used since init is ran multiple times in observation ??

    function init($container) {
        const ENDPOINT = '/ugc-rating';

        if (!initFlag) {
            initFlag = true;
            document.addEventListener('mntlRatingUpdate', (event) => {
                event.stopPropagation();
                const data = event.detail;

                window.debug.log('Attempting UGC rating update', data.type, data.rating, data.docId);

                utils.ajaxPromisePost(ENDPOINT, data, 60000)
                    .then(() => {
                        const mntlRatingUpdateSuccessEvent = new CustomEvent('mntlRatingUpdateSuccess', {detail: data});

                        window.debug.log('Successful UGC rating update', data.type, data.rating, data.docId);
                        document.dispatchEvent(mntlRatingUpdateSuccessEvent);

                        // TODO: Deprecate for 3.13. Left in for GLBE-8299 to resolve in mantle 3.13. Deleting this jQuery
                        // event is breaking on health and should be resolved in 3.13 instead.
                        $container.trigger('mntlRatingUpdateSuccess', [data.type, data.rating, data.docId]);
                    })
                    .catch((err) => {
                        const mntlRatingUpdateErrorEvent = new CustomEvent('mntlRatingUpdateError', {detail: data});

                        window.debug.error('Error while attempting UGC rating update', err);
                        document.dispatchEvent(mntlRatingUpdateErrorEvent);
                    });
            });
        }
    }

    return { init };
})(window.Mntl.utilities || {});

Mntl.utilities.readyAndDeferred(Mntl.Ratings.init);
