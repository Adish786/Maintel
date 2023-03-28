/**
 * Automatically adjusts the height of the DataWrapper iframe base on it's content.
 * Get all iframe blocks from the page and listens to DataWrapper message which
 * broadcasts the correct height based on its width.
 */

(function() {
    var iframes = document.getElementsByClassName('mntl-sc-block-iframe__uri');

    window.addEventListener('message', function(message) {
        var value;

        if (typeof message.data['datawrapper-height'] === 'object') {
            for (value in message.data['datawrapper-height']) {
                Array.prototype.forEach.call(iframes, function(iframe) {
                    if (iframe.dataset.src.indexOf('datawrapper') !== -1 && iframe.dataset.src.indexOf(value) !== -1) {
                        iframe.style.height = message.data['datawrapper-height'][value] + 'px';
                    }
                });
            }
        }
    });
})();
