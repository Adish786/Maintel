(function(utils) {
    const recipeReviewBar = document.querySelector('.js-recipe-review-bar');
    const reviewsBlock = document.querySelector('.recipe-ugc-wrapper');
    const fixedLeaderboardHeight = document.querySelector('.mntl-leaderboard-fixed-0') === null ? 0 : document.querySelector('.mntl-leaderboard-fixed-0').offsetHeight; // height of fixed leaderboard on recipeSC templates
    const journeyTopNavHeight = document.querySelector('.mntl-di-nav') === null ? 0 : document.querySelector('.mntl-di-nav').offsetHeight; // height of journey top nav

    function scrollToReviews() {

        const headerHeightCollapsed = 64; // Height of header when it is collapsed
        const scrollPadding = 32 // Arbitrary padding between header nav and review section.
        const scrollTo = window.pageYOffset + reviewsBlock.getBoundingClientRect().top - (headerHeightCollapsed + scrollPadding + fixedLeaderboardHeight + journeyTopNavHeight);


        window.scrollTo({
            top: scrollTo,
            left: 0,
            behavior: 'smooth'
        });
    }

    if (reviewsBlock) {
        const trackReviewsButtonClick = utils.pushToDataLayer({
            event: 'transmitInteractiveEvent',
            eventCategory: 'User Actions',
            eventAction: 'Content Action Taken',
            eventLabel: 'Top Nav - Jump to Reviews - Reviews'
        });

        const trackReviewStarsButtonClick = utils.pushToDataLayer({
            event: 'transmitInteractiveEvent',
            eventCategory: 'User Actions',
            eventAction: 'Content Action Taken',
            eventLabel: 'Top Nav - Jump to Reviews - Stars'
        });

        let addListener = false;

        if (!recipeReviewBar.classList.contains('read-only')
            || (recipeReviewBar.classList.contains('read-only') && recipeReviewBar.classList.contains('has-comments'))) {
            addListener = true;
        }

        if (addListener) {
            const commentCount = recipeReviewBar.querySelector('.mntl-recipe-review-bar__comment-count');
            const ratingCount = recipeReviewBar.querySelector('.mntl-recipe-review-bar__rating');
            const starRating = recipeReviewBar.querySelector('.mntl-recipe-review-bar__star-rating');

            if (commentCount) {
                commentCount.addEventListener('click', () => {
                    trackReviewsButtonClick();
                    scrollToReviews();
                });
            }

            if (ratingCount && starRating) {
                [ratingCount, starRating].forEach((element) => element.addEventListener('click', () => {
                    trackReviewStarsButtonClick();
                    scrollToReviews();
                }));
            }
        }
    }
})(window.Mntl.utilities || {});
