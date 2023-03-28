(function($) {
    const dialogLinks = document.querySelectorAll('.dialog-link');

    dialogLinks.forEach((dialogLink) => {
        dialogLink.addEventListener('click', (event) => {
            event.preventDefault();
            // Allow deferred content inside dialog to load.
            $('[data-defer="loadDialogContent"]').each((index, el) => {
                const $el = $(el);

                if ($el.html() === '') {
                    $el.trigger('loadDialogContent');
                }
            });
        });
    });
}(window.jQuery || {}));