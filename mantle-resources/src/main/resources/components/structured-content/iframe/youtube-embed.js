(function() {
    var youtubeIframes = document.querySelectorAll("iframe[data-src^='https://www.youtube.com']");

    Array.prototype.forEach.call(youtubeIframes, function(iframe) {
        // create new container for the youtube iframe to prevent caption from getting messed up by the absolute position of the iframe
        var youtubeContainer = document.createElement('div');
        var iframeParent = iframe.parentElement;
        var citeNode = iframeParent.getElementsByClassName('mntl-sc-block-iframe__caption')[0];

        youtubeContainer.classList.add('mntl-sc-block-iframe__youtube');
        youtubeContainer.appendChild(iframe);
        iframeParent.insertBefore(youtubeContainer, citeNode);
    });
})();
