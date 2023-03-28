window.Mntl = window.Mntl || {};

Mntl.StarRating = (function() {
    let initFlag = false;

    /**
      * Generic helper function used to set the state if the previous rating stars
      * with respect to to the current star element.
      *
      * @param {dom element} currentStar the current active star element
      * @param {string} addClass the class to add (singluar), leave blank/set to null to ignore step
      * @param {string} removeClass the class to remove (singlar), leave blank/set to null to ignore step
      */
    function setPreviousStars(currentStar, addClass, removeClass) {
        let previousStar = currentStar.previousElementSibling;

        while (previousStar) {
            if (addClass) {
                previousStar.classList.add(addClass);
            }

            if (removeClass) {
                previousStar.classList.remove(removeClass);
            }

            previousStar = previousStar.previousElementSibling;
        }
    }

    /**
     * Generic helper function used to set the state if the next rating stars
     * with respect to to the current star element.
     *
     * @param {dom element} currentStar the current active star element
     * @param {string} addClass the class to add (singluar), leave blank/set to null to ignore step
     * @param {string} removeClass the class to remove (singlar), leave blank/set to null to ignore step
     */
    function setNextStars(currentStar, addClass, removeClass) {
        let nextStar = currentStar.nextElementSibling;

        while (nextStar) {
            if (addClass) {
                nextStar.classList.add(addClass);
            }

            if (removeClass) {
                nextStar.classList.remove(removeClass);
            }

            nextStar = nextStar.nextElementSibling;
        }
    }

    function init() {
        const starRatings = document.getElementsByClassName('mntl-star-rating');

        if (!initFlag) {
            initFlag = true;

            [...starRatings].forEach((starRating) => {
                const stars = starRating.getElementsByTagName('a');

                [...stars].forEach((star) => {
                    const mntlRatingUpdateEvent = new CustomEvent('mntlRatingUpdate', {
                        detail: {
                            type: 'starRating',
                            rating: star.dataset.rating,
                            docId: starRating.dataset.docId
                        }
                    });

                    if (window.Modernizr && !window.Modernizr.touchevents) {
                        star.addEventListener('mouseover', () => {
                            star.classList.add('hover');
                            star.classList.remove('no-hover');
                            setPreviousStars(star, 'hover', 'no-hover');
                            setNextStars(star, 'no-hover', 'hover');
                        });

                        star.addEventListener('mouseleave', () => {
                            [...stars].forEach((rating) => {
                                rating.classList.remove('hover', 'no-hover');
                            });
                        });
                    }

                    star.addEventListener('click', (event) => {
                        event.preventDefault();

                        star.classList.add('active');
                        setPreviousStars(star, 'active', null);
                        setNextStars(star, null, 'active');

                        document.dispatchEvent(mntlRatingUpdateEvent);
                    });
                });
            });
        }
    }

    return { init };
})();

Mntl.utilities.readyAndDeferred(Mntl.StarRating.init);
