(function(utils) {
    const nutritionLink = document.querySelector('.mntl-recipe-details__nutrition-link');
    const nutritionBlock = document.querySelector('.mntl-nutrition-facts');
    const fixedLeaderboardHeight = document.querySelector('.mntl-leaderboard-fixed-0') === null ? 0 : document.querySelector('.mntl-leaderboard-fixed-0').offsetHeight; // height of fixed leaderboard on recipeSC templates
    const journeyTopNavHeight = document.querySelector('.mntl-di-nav') === null ? 0 : document.querySelector('.mntl-di-nav').offsetHeight; // height of journey top nav

    if (!nutritionLink) {
        return;
    }

    const trackJumpToNutritionFactsClick = utils.pushToDataLayer({
        event: 'transmitInteractiveEvent',
        eventAction: 'Jump to Nutrition Facts',
        eventCategory: 'User Action',
        eventLabel: window.location.href,
        eventValue: 0,
        nonInteraction: 0
    });

    nutritionLink.addEventListener('click', () => {

        trackJumpToNutritionFactsClick();

        const headerHeight = document.querySelector('.header').offsetHeight;
        const scrollPadding = 32;

        const scrollTo = window.pageYOffset + nutritionBlock.getBoundingClientRect().top - (headerHeight + scrollPadding + fixedLeaderboardHeight + journeyTopNavHeight);

        window.scrollTo({
            top: scrollTo,
            left: 0,
            behavior: 'smooth'
        });
    });
})(window.Mntl.utilities || {});