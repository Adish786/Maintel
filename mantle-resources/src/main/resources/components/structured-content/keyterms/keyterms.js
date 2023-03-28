(function() {
    const leftArrow = document.getElementsByClassName('mntl-carousel__arrow')[0];
    const rightArrow = document.getElementsByClassName('mntl-carousel__arrow')[1];

    document.addEventListener('click', (event) => {
        const previousTerm = document.querySelector('.mntl-sc-block-keyterms__nav-item.is-active');

        if (!event.target.closest('.mntl-sc-block-keyterms__nav-item')) {
            return;
        }

        event.preventDefault();

        const item = event.target.classList.contains('mntl-sc-block-keyterms__nav-item') ? event.target : event.target.closest('mntl-sc-block-keyterms__nav-item');

        if (previousTerm !== null && item !== null) {
            previousTerm.classList.remove('is-active');
            item.classList.add('is-active');
        }

        if (item) {
            const index = parseInt(item.dataset.index, 10);

            Mntl.carouselWidget.scrollToItemInCarousel(0, index);
        }
    });

    function changeActiveTab(direction) {
        const previousTerm = document.querySelector('.mntl-sc-block-keyterms__nav-item.is-active');
        let newTermIndex = 0;

        if (previousTerm !== null) {
            if (direction === 'left') {
                newTermIndex = parseInt(previousTerm.dataset.index, 10) - 1;
            } else if (direction === 'right') {
                newTermIndex = parseInt(previousTerm.dataset.index, 10) + 1;
            }

            let newTerm = document.querySelector(`button[data-index="${newTermIndex}"]`);

            previousTerm.classList.remove('is-active');
            newTerm.classList.add('is-active');
        }
    }

    if (typeof leftArrow !== 'undefined') {
        leftArrow.addEventListener('click', () => {
            if (leftArrow.classList.contains('is-active')) {
                changeActiveTab('left');
            }
        });

        rightArrow.addEventListener('click', () => {
            if (rightArrow.classList.contains('is-active')) {
                changeActiveTab('right');
            }
        });
    }
})();
