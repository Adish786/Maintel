(function() {
    const openCoverageButton = document.querySelector('.js-coverage-trigger');
    const coverageList = document.querySelector('.js-coverage-list-open');
    const closeCoverageButton = document.querySelector('.js-coverage-list-close');

    openCoverageButton.addEventListener('click', () => {
        coverageList.classList.remove('is-hidden');
    });
    closeCoverageButton.addEventListener('click', () => {
        coverageList.classList.add('is-hidden');
    });
}());
