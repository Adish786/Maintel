(function() {
    var hasLightbox = document.getElementsByClassName('mntl-lightbox')[0];

    /*
    If the first image has an overlay, disable lightbox for this image and all following images
    that are a part of the gallery experience by nature of being immediate next sibling elements
    */
    function _disableImageCarousels() {
        var imageOverlays;
        var currentBlock;
        var i;

        imageOverlays = document.getElementsByClassName('mntl-image-overlay');
        for (i = 0; i < imageOverlays.length; i++) {
            currentBlock = imageOverlays[i].closest('.mntl-sc-block-image').nextElementSibling;

            while (currentBlock && currentBlock.classList.contains('mntl-sc-block-image') && !currentBlock.getElementsByClassName('mntl-image-overlay')[0]) {
                currentBlock.getElementsByTagName('img')[0].dataset.imgLightbox = false;
                currentBlock = currentBlock.nextElementSibling;
            }
        }
    }

    function _removeOverlay(overlay) {
        overlay.classList.add('js-hidden');
    }

    /*
    There is a content requirement that the FIRST image in the carousel MUST be a sensitive image
    so that it will have the button to remove the image overlay from itself and ALL other carousel images.
    */
    function _handleImageCarousel(firstImage) {
        var currentImage = firstImage;
        var currentImageOverlay;

        // check if image has any immediate sibling elmeents that are also image
        while (currentImage && currentImage.classList.contains('mntl-sc-block-image')) {
            // allow lightbox mode on click once image overlay is removed
            currentImage.getElementsByTagName('img')[0].dataset.imgLightbox = 'true';

            currentImageOverlay = currentImage.getElementsByClassName('mntl-image-overlay')[0];

            if (currentImageOverlay) {
                _removeOverlay(currentImageOverlay);
            }

            currentImage = currentImage.nextElementSibling;
        }
    }

    function _handleImageClick(e) {
        var imageBlock;
        var imageOverlay;

        if (e.target.classList.contains('mntl-image-overlay__button')) {
            e.preventDefault();
            imageOverlay = e.target.closest('.mntl-image-overlay');
            imageBlock = e.target.closest('.mntl-sc-block-image');

            if (imageBlock && hasLightbox) {
                _handleImageCarousel(imageBlock);
            } else if (imageOverlay) {
                _removeOverlay(imageOverlay);
            }
        }
    }

    if (hasLightbox) {
        _disableImageCarousels();
    }
    document.body.addEventListener('click', _handleImageClick);
})();
