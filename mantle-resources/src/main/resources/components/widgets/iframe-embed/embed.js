/* 
 *Embed script for 3rd party sites to load iframe
 * and listen to height change to resize iframe
*/

(function() {
    var scripts = document.getElementsByTagName('script');
    var parent;
    var iframe;
    var embedId;
    var vertical;

    function receiveMessage(evt) {
        var iframes = document.getElementsByTagName('iframe');

        [].forEach.call(iframes, function(el) {
            if (el.getAttribute('id') === evt.data.embedId) {
                el.style.height = evt.data.height + 10 + 'px';
            }
        });
    }

    [].forEach.call(scripts, function(el) {
        if (el.getAttribute('data-type') === 'dotdash-tool') {
            parent = el.parentElement;
            iframe = document.createElement('iframe');
            embedId = el.getAttribute('id');
            vertical = el.getAttribute('data-vertical');
            iframe.src = 'https://www.' + vertical + '.com/tools/' + embedId;
            iframe.style.border = 'none';
            iframe.id = embedId;
            iframe.style.width = '100%';

            if (!el.nextSibling || el.nextSibling.id !== embedId) {
                parent.insertBefore(iframe, el.nextSibling);
            }
        }
    });

    window.addEventListener('message', receiveMessage, false);
})();
