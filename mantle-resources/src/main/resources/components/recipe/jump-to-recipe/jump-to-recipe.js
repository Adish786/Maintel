(function (window) {
    const jumpToRecipeButton = document.querySelector('.mntl-jump-to-recipe__button');
    const fixedLeaderboardHeight = document.querySelector('.mntl-leaderboard-fixed-0') === null ? 0 : document.querySelector('.mntl-leaderboard-fixed-0').offsetHeight; // height of fixed leaderboard on recipeSC templates
    const journeyTopNavHeight = document.querySelector('.mntl-di-nav') === null ? 0 : document.querySelector('.mntl-di-nav').offsetHeight; // height of journey top nav

    function scrollToRecipe() {
        const headerHeight = document.querySelector('.header').offsetHeight;
        const scrollPadding = 32;
        const ingredientsSection = document.querySelector('.mntl-structured-ingredients');
        const scrollOffset = ingredientsSection.getBoundingClientRect().top + window.pageYOffset - (headerHeight + scrollPadding + fixedLeaderboardHeight + journeyTopNavHeight);

        window.scrollTo({
            top: scrollOffset,
            left: 0,
            behavior: 'smooth'
        });
    };

    jumpToRecipeButton.addEventListener('click', scrollToRecipe);
})(window || {});
