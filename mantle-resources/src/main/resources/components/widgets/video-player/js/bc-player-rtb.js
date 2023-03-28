window.Mntl = window.Mntl || {};

Mntl.BCPlayerRtb = (function(document) {

    let bidTargeting;

    function _handleRTBRequest() {
        const videoSlot = {
            config: {
                id: 'preroll',
                sizes: [[640, 360]],
                type: 'video'
            }
        };

        try {
            Mntl.RTB.initVideoSlot(videoSlot)
                .then((bids) => {
                    bidTargeting = bids;
                })
                .catch(() => {
                    debug.log('Mntl.RTB.BCPlayerRtb: Call to Mntl.RTB.initVideoSlot() failed; promise rejected');
                });
        } catch(e) {
            debug.log('Mntl.RTB.BCPlayerRtb: Call to Mntl.RTB.initVideoSlot() failed');
        }
    }

    function resetBidTargeting() {
        bidTargeting = [{}];
        if (!Mntl.RTB || !document.querySelector('.jumpstart-js-wrapper')) {
            return;
        }
        _handleRTBRequest();
    }

    function getBidTargeting() {
        function combineObjsReducer(a, b) {

            return {
                ...a,
                ...b
            }
        }

        return bidTargeting.reduce(combineObjsReducer);
    }

    resetBidTargeting();

    return {
        getBidTargeting,
        resetBidTargeting
    }

}(window.document));