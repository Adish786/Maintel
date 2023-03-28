window.Mntl = window.Mntl || {};

Mntl.journeyCarouselWidget = (function() {
    const carousel = document.getElementsByClassName('mntl-journey-carousel__items')[0];
    const carouselItem = document.getElementsByClassName('mntl-journey-carousel__item')[0];
    const carouselItemMargin = parseFloat(getComputedStyle(carouselItem).marginRight);
    const carouselItemWidth = carouselItem.offsetWidth + carouselItemMargin;
    const leftArrow = document.getElementsByClassName('mntl-journey-carousel__arrow')[0];
    const rightArrow = document.getElementsByClassName('mntl-journey-carousel__arrow')[1];
    const linkedJourney = document.getElementsByClassName('mntl-journey-carousel--linked-journey')[0];
    let currentIndex = document.querySelector('.card--current-document .card') ? parseInt(document.querySelector('.card--current-document .card').getAttribute('data-ordinal'), 10) - 1 : -1;
    let itemViewportWidth = Math.floor(carousel.offsetWidth / carouselItemWidth);
    let translateDistance;

    function slideItems() {
        const allCarouselItems = document.getElementsByClassName('mntl-journey-carousel__item');
        const lastJourneyIndex = allCarouselItems.length;

        if (lastJourneyIndex > itemViewportWidth) {
            // We have items to slide to
            if ((lastJourneyIndex - currentIndex) > itemViewportWidth) {
                // Not at the end
                if (currentIndex > 0) {
                    leftArrow.classList.add('is-active');
                } else {
                    leftArrow.classList.remove('is-active');
                }
                rightArrow.classList.add('is-active');

                translateDistance = -Math.abs(carouselItemWidth * currentIndex);
            } else {
                // At the end, right align last card
                currentIndex = lastJourneyIndex - itemViewportWidth;
                leftArrow.classList.add('is-active');
                rightArrow.classList.remove('is-active');

                // To completely right align last card, need to:
                // 1. Slide left by - left over width after subtracting total cards that can fit at once in the viewport width from total viewport width
                // 2. Slide right by - carousel item margin (so that there is no white space on the right side at all)
                translateDistance = -Math.abs(carouselItemWidth * currentIndex) + (carousel.offsetWidth - (itemViewportWidth * carouselItemWidth) + carouselItemMargin);
            }

            [...allCarouselItems].forEach((carouselItemInList) => {
                carouselItemInList.style.transform = `translateX(${translateDistance}px)`;
            });
        } else {
            // Not enough cards to fit whole viewport - left align cards
            currentIndex = 0;

            leftArrow.classList.remove('is-active');
            rightArrow.classList.remove('is-active');

            [...allCarouselItems].forEach((carouselItemInList) => {
                carouselItemInList.style.transform = 'translateX(0px)';
            });
        }
    }

    window.onresize = Mntl.throttle(() => {
        let newItemViewportWidth = Math.floor(carousel.offsetWidth / carouselItemWidth);

        if (newItemViewportWidth !== itemViewportWidth) {
            itemViewportWidth = newItemViewportWidth;
            slideItems();
        }
    }, 200);

    function initJourneyCarousel() {
        const carouselContainer = document.getElementsByClassName('mntl-journey-carousel')[0];

        if (typeof leftArrow !== 'undefined') {
            // Desktop
            if (itemViewportWidth <= 1 && currentIndex >= 0) {
                // Special case - if we can only show one card, start showing the next journey document card
                currentIndex += 1;
            }

            slideItems();

            carousel.classList.add('js-animatable');

            leftArrow.addEventListener('click', () => {
                if (leftArrow.classList.contains('is-active')) {
                    currentIndex -= 1;
                    slideItems();
                }
            });

            rightArrow.addEventListener('click', () => {
                if (rightArrow.classList.contains('is-active')) {
                    currentIndex += 1;
                    slideItems();
                }
            });
        } else {
            // Mobile
            if (typeof linkedJourney === 'undefined') {
                currentIndex++;
            }
            carouselContainer.classList.add('allow-free-scroll');
            carousel.scrollTo(carouselItemWidth * currentIndex, 0);
        }
    }

    initJourneyCarousel();
})();
