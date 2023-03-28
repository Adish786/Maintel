(function() {
    function init() {
        var galleries = document.getElementsByClassName('mntl-sc-block-gallery');
        var i;

        function _getTransitionDuration(slidesLength) {
            switch (true) {
                case (slidesLength >= 20):
                    return 1;
                case (slidesLength >= 10 && slidesLength < 20):
                    return 0.75;
                default:
                    return 0.5;
            }
        }

        function _setup(gallery) {
            var LANDSCAPE_ASPECT_RATIO = 1.5;
            var slideContainer = gallery.getElementsByClassName('slides')[0];
            var slideContainerHeight = slideContainer.offsetWidth / LANDSCAPE_ASPECT_RATIO;
            var slides = gallery.getElementsByClassName('mntl-sc-block-galleryslide');
            var carousel = gallery.getElementsByClassName('carousel')[0];
            var carouselItems = gallery.getElementsByClassName('carousel__item');
            var slidesInViewNum = Number(carousel.dataset.carouselSize);
            var slidesInView = [];
            var ii;

            if (slides.length <= slidesInViewNum) {
                carousel.classList.add('no-scroll');
            }

            slideContainer.style.height = slideContainerHeight + 'px';
            slides[0].classList.add('current');
            gallery.getElementsByClassName('slide-counter__current')[0].innerText = '1';
            gallery.getElementsByClassName('slide-counter__total')[0].innerText = 'of ' + slides.length;
            gallery.getElementsByClassName('slide-caption__desc')[0].innerHTML = slides[0].getElementsByTagName('img')[0].dataset.caption;
            gallery.getElementsByClassName('slide-caption__owner')[0].innerHTML = slides[0].getElementsByTagName('img')[0].dataset.owner;
            carousel.dataset.translateAmount = 0;

            // Set slide return animation duration
            carousel.dataset.translateDuration = _getTransitionDuration(slides.length);

            // iterate over gallery slides and set data-index
            for (ii = 0; ii < slides.length; ii++) {
                slides[ii].dataset.slide = carouselItems[ii].dataset.slide = ii;
                carouselItems[ii].style.width = (100 / slidesInViewNum) + '%';

                if (ii < slidesInViewNum) {
                    slidesInView.push(ii);
                }
            }

            carousel.dataset.slidesInView = JSON.stringify(slidesInView);
            carouselItems[0].classList.add('active');
        }

        for (i = 0; i < galleries.length; i++) {
            _setup(galleries[i]);
        }
    }

    function _navigate(next, current) {
        var gallery = current.closest('.mntl-sc-block-gallery');
        var previous = gallery.getElementsByClassName('previous')[0];
        var currentSlide = Number(current.dataset.slide);
        var nextSlide = Number(next.dataset.slide);
        var counter;
        var caption;
        var owner;
        var carouselItems = gallery.getElementsByClassName('carousel__item');

        // applying this class on click prevents transitions from being rendered when initially loaded
        gallery.classList.add('js-animatable');

        if (previous) {
            previous.classList.remove('previous');
        }

        current.classList.remove('current');
        current.classList.add('previous');
        next.classList.add('current');

        counter = gallery.getElementsByClassName('slide-counter__current')[0];
        caption = gallery.getElementsByClassName('slide-caption__desc')[0];
        owner = gallery.getElementsByClassName('slide-caption__owner')[0];
        counter.innerText = nextSlide + 1;
        caption.innerHTML = next.getElementsByTagName('img')[0].dataset.caption;
        owner.innerHTML = next.getElementsByTagName('img')[0].dataset.owner;

        carouselItems[currentSlide].classList.remove('active');
        carouselItems[nextSlide].classList.add('active');
    }

    function _prevSlide(gallery) {
        var slides = gallery.getElementsByClassName('mntl-sc-block-galleryslide');
        var current = gallery.getElementsByClassName('current')[0];
        var currentSlide = Number(current.dataset.slide);
        var previousSlide = currentSlide === 0 ? slides.length - 1 : currentSlide - 1;
        var carousel = gallery.getElementsByClassName('carousel')[0];
        var carouselItems = gallery.getElementsByClassName('carousel__item');
        var slidesInView = JSON.parse(carousel.dataset.slidesInView);
        var slideHolder = gallery.querySelector('.carousel__slider');
        var translateAmount = Number(carousel.dataset.translateAmount);
        var slidesInViewNum = Number(carousel.dataset.carouselSize);
        var k;

        _navigate(slides[previousSlide], current);

        if (currentSlide > 0 && currentSlide === slidesInView[0]) {
            // if first slide in view, animation shifts one slide backwards
            slidesInView.pop();
            slidesInView.unshift(previousSlide);
            translateAmount += carouselItems[0].offsetWidth;
            slideHolder.style.transform = 'translateX(' + translateAmount + 'px)';
            slideHolder.style.transition = 'transform .5s ease-out';
            carousel.dataset.slidesInView = JSON.stringify(slidesInView);
            carousel.dataset.translateAmount = translateAmount;
        } else if (currentSlide === 0) {
            // if first slide, animation skips to last slide
            slidesInView = [];

            for (k = 0; k < slidesInViewNum; k++) {
                slidesInView.push(slides.length - slidesInViewNum + k);
            }

            if (slides.length > slidesInViewNum) {
                translateAmount -= (slides.length - slidesInViewNum) * carouselItems[0].offsetWidth;
            } else {
                translateAmount = 0;
            }

            slideHolder.style.transform = 'translateX(' + translateAmount + 'px)';
            slideHolder.style.transition = 'transform ' + carousel.dataset.translateDuration + 's ease-out';
            carousel.dataset.slidesInView = JSON.stringify(slidesInView);
            carousel.dataset.translateAmount = translateAmount;
        }
    }

    function _nextSlide(gallery) {
        var slides = gallery.getElementsByClassName('mntl-sc-block-galleryslide');
        var current = gallery.getElementsByClassName('current')[0];
        var currentSlide = Number(current.dataset.slide);
        var nextSlide = currentSlide === slides.length - 1 ? 0 : currentSlide + 1;
        var carousel = gallery.getElementsByClassName('carousel')[0];
        var carouselItems = gallery.getElementsByClassName('carousel__item');
        var slidesInView = JSON.parse(carousel.dataset.slidesInView);
        var slideHolder = gallery.querySelector('.carousel__slider');
        var translateAmount = Number(carousel.dataset.translateAmount);
        var slidesInViewNum = Number(carousel.dataset.carouselSize);
        var j;

        _navigate(slides[nextSlide], current);

        if (currentSlide < slides.length - 1 && currentSlide === slidesInView[slidesInView.length - 1]) {
            // if last slide in view, animation shifts one slide forward
            slidesInView.shift();
            slidesInView.push(nextSlide);
            translateAmount -= carouselItems[0].offsetWidth;
            slideHolder.style.transform = 'translateX(' + translateAmount + 'px)';
            slideHolder.style.transition = 'transform .5s ease-out';
            carousel.dataset.slidesInView = JSON.stringify(slidesInView);
            carousel.dataset.translateAmount = translateAmount;
        } else if (currentSlide === slides.length - 1) {
            // if last slide, animation resets to first slid
            slidesInView = [];

            for (j = 0; j < slidesInViewNum; j++) {
                slidesInView.push(j);
            }

            slideHolder.style.transform = 'translateX(0px)';
            slideHolder.style.transition = 'transform ' + carousel.dataset.translateDuration + 's ease-out';
            carousel.dataset.slidesInView = JSON.stringify(slidesInView);
            carousel.dataset.translateAmount = 0;
        }
    }

    function _handleTouchStart(e) {
        var slideContainer = e.target.closest('.slides');

        if (slideContainer) {
            slideContainer.dataset.firstTouchX = e.touches[0].clientX;
            slideContainer.dataset.firstTouchY = e.touches[0].clientY;
        }
    }

    function _handleTouchMove(e) {
        var gallery = e.target.closest('.mntl-sc-block-gallery');
        var slideContainer = e.target.closest('.slides');
        var firstTouchX;
        var firstTouchY;
        var lastTouchX;
        var lastTouchY;

        if (!slideContainer) {
            return;
        }

        firstTouchX = Number(slideContainer.dataset.firstTouchX);
        firstTouchY = Number(slideContainer.dataset.firstTouchY);
        lastTouchX = e.touches[0].clientX;
        lastTouchY = e.touches[0].clientY;

        if (Math.abs(lastTouchX - firstTouchX) > Math.abs(lastTouchY - firstTouchY)) {
            if (lastTouchX - firstTouchX > 0) {
                // rightward swipe on slide
                _prevSlide(gallery);
            } else {
                // leftward swipe on slide
                _nextSlide(gallery);
            }
        }

        // reset values
        slideContainer.dataset.firstTouchX = null;
        slideContainer.dataset.firstTouchY = null;
    }

    init();

    document.addEventListener('touchstart', _handleTouchStart, false);
    document.addEventListener('touchmove', _handleTouchMove, false);

    document.addEventListener('click', function(e) {
        var gallery = e.target.closest('.mntl-sc-block-gallery');
        var left = e.target.closest('.left-arrow');
        var right = e.target.closest('.right-arrow');
        var current;
        var carouselItem = e.target.closest('.carousel__item');
        var index;

        if (!gallery || (!left && !right && !carouselItem)) {
            return;
        }

        if (left) {
            _prevSlide(gallery);
        } else if (right) {
            _nextSlide(gallery);
        } else if (carouselItem) {
            index = Number(carouselItem.dataset.slide);
            current = gallery.getElementsByClassName('current')[0];
            _navigate(gallery.getElementsByClassName('mntl-sc-block-galleryslide')[index], current);
        }
    });
})();
