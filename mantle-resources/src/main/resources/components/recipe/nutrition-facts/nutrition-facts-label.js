(function () {
    const toggleButton = document.querySelector('.mntl-nutrition-facts-label__button');
    const tableContainer = document.querySelector('.mntl-nutrition-facts-label__wrapper');

    function toggleNutritionFactsLabel() {
        if (toggleButton.classList.contains('mntl-nutrition-facts-label__button--show')) {
            tableContainer.classList.remove('mntl-nutrition-facts-label__wrapper--collapsed');
        } else {
            tableContainer.classList.add('mntl-nutrition-facts-label__wrapper--collapsed');
        }

        toggleButton.classList.toggle('mntl-nutrition-facts-label__button--show');
        toggleButton.classList.toggle('mntl-nutrition-facts-label__button--hide');
    }

    toggleButton.addEventListener('click', toggleNutritionFactsLabel);
})();