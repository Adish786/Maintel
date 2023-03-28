window.Mntl = window.Mntl || {};

Mntl.MadeIt = (function() {
    let initFlag = false;

    function init() {
        const madeIts = document.getElementsByClassName('.mntl-made-it');

        if (!initFlag) {
            initFlag = true;

            [...madeIts].forEach((madeIt) => {
                madeIt.getElementsByTagName('a')[0].addEventListener('click', (event) => {
                    event.preventDefault();
                    madeIt.classList.toggle('active');

                    const mntlRatingUpdateEvent = new CustomEvent('mntlRatingUpdate', {
                        detail: {
                            type: 'madeIt',
                            rating: madeIt.classList.contains('active'),
                            docId: madeIt.dataset.docId
                        }
                    });

                    document.dispatchEvent(mntlRatingUpdateEvent);
                });
            });
        }
    }

    return { init };
})();

Mntl.utilities.readyAndDeferred(Mntl.MadeIt.init);
