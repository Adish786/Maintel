window.Mntl = window.Mntl || {};

Mntl.carouselWidget = (function(throttle) {
    const scrollToItemFunctions = [];

    function initCarousel(carousel) {
        const [carouselItemContainer] = carousel.getElementsByClassName('mntl-carousel__items');
        const allCarouselItems = carousel.getElementsByClassName('mntl-carousel__item');
        const [carouselItem] = allCarouselItems;
        const carouselItemMargin = parseFloat(getComputedStyle(carouselItem).marginRight);
        const [leftArrow, rightArrow] = carousel.getElementsByClassName('mntl-carousel__arrow');
        const indicators = carousel.getElementsByClassName('mntl-carousel__indicator-item');
        const hasIndicator = indicators.length !== 0;
        const [linkedJourney] = carousel.getElementsByClassName('mntl-carousel--linked-journey');
        const isJourneyCarousel = carousel.classList.contains('mntl-carousel--journey'); // some rules apply strictly to journey carousel
        let carouselItemWidth = carouselItem.offsetWidth + carouselItemMargin;
        let currentIndex = carouselItemContainer.querySelector('.is-active') ? parseInt(carousel.querySelector('.is-active').getAttribute('data-ordinal'), 10) : 0;
        let itemViewportWidth = Math.max(Math.floor(carouselItemContainer.offsetWidth / carouselItemWidth), 1);
        let translateDistance;

        function updateCurrentIndex(newIndex) {
            if (hasIndicator) {
                const activeClass = 'mntl-carousel__indicator-item--active';

                indicators[currentIndex].classList.remove(activeClass);
                indicators[newIndex].classList.add(activeClass);
            }

            currentIndex = newIndex;
        }

        function slideItems() {
            const lastItemIndex = allCarouselItems.length;

            if (lastItemIndex > itemViewportWidth) {
                // We have items to slide to
                if ((lastItemIndex - currentIndex) > itemViewportWidth) {
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
                    updateCurrentIndex(lastItemIndex - itemViewportWidth);

                    leftArrow.classList.add('is-active');
                    rightArrow.classList.remove('is-active');

                    // To completely right align last card, need to:
                    // 1. Slide left by - left over width after subtracting total cards that can fit at once in the viewport width from total viewport width
                    // 2. Slide right by - carousel item margin (so that there is no white space on the right side at all)
                    translateDistance = -Math.abs(carouselItemWidth * currentIndex) + (carouselItemContainer.offsetWidth - (itemViewportWidth * carouselItemWidth) + carouselItemMargin);
                }

                [...allCarouselItems].forEach((carouselItemInList) => {
                    carouselItemInList.style.transform = `translateX(${translateDistance}px)`;
                });
            } else {
                // Not enough cards to fit whole viewport - left align cards
                updateCurrentIndex(0);

                leftArrow.classList.remove('is-active');
                rightArrow.classList.remove('is-active');

                [...allCarouselItems].forEach((carouselItemInList) => {
                    carouselItemInList.style.transform = 'translateX(0px)';
                });
            }
        }

        function scrollToItem(index) {
            updateCurrentIndex(index);
            slideItems();
        }

        window.addEventListener('resize', throttle(() => {
            const newcarouselItemWidth = carouselItem.offsetWidth + parseFloat(getComputedStyle(carouselItem).marginRight);
            const newItemViewportWidth = Math.max(Math.floor(carouselItemContainer.offsetWidth / newcarouselItemWidth), 1);

            if (newItemViewportWidth !== itemViewportWidth || newcarouselItemWidth !== carouselItemWidth) {
                itemViewportWidth = newItemViewportWidth;
                carouselItemWidth = newcarouselItemWidth;
                slideItems();
            }
        }, 200));

        updateCurrentIndex(currentIndex);

        if (typeof leftArrow === 'undefined') {
            // Mobile
            if (typeof linkedJourney === 'undefined' && isJourneyCarousel) {
                currentIndex++;
            }

            carouselItemContainer.scrollTo(carouselItemWidth * currentIndex, 0);

            if (hasIndicator) {
                carouselItemContainer.addEventListener('scroll', throttle(() => {
                    const scrollX = carouselItemContainer.scrollLeft + (carouselItemContainer.offsetWidth / 4);
                    const scrollToIndex = Math.round(scrollX / carouselItemWidth);

                    if (currentIndex !== scrollToIndex && scrollToIndex >= 0 && scrollToIndex < allCarouselItems.length) {
                        updateCurrentIndex(scrollToIndex);
                    }
                }, 200));
            }
        } else {
            // Desktop
            if (itemViewportWidth <= 1 && currentIndex >= 0 && carousel.getElementsByClassName('mntl-carousel--journey').length) {
                // Special case - if we can only show one JOURNEY card, start showing the read next card
                currentIndex += 1;
            }

            slideItems();

            carouselItemContainer.classList.add('js-animatable');

            leftArrow.addEventListener('click', () => {
                if (leftArrow.classList.contains('is-active')) {
                    updateCurrentIndex(currentIndex - 1);
                    slideItems();
                }
            });

            rightArrow.addEventListener('click', () => {
                if (rightArrow.classList.contains('is-active')) {
                    updateCurrentIndex(currentIndex + 1);
                    slideItems();
                }
            });
        }

        return scrollToItem;
    }

    function init($container) {
        const container = $container instanceof window.jQuery ? $container[0] : $container;
        const carousels = container.getElementsByClassName('mntl-carousel');

        [...carousels].forEach((carousel) => {
            const scrollToItem = initCarousel(carousel);

            scrollToItemFunctions.push(scrollToItem);
        });
    }

    function scrollToItemInCarousel(carouselIndex, itemIndex) {
        scrollToItemFunctions[carouselIndex](itemIndex);
    }

    return {
        init,
        scrollToItemInCarousel
    };
})(Mntl.throttle);

Mntl.utilities.readyAndDeferred(Mntl.carouselWidget.init);
