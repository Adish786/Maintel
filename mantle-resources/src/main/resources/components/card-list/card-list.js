(function(animationUtils) {
    const articleListsContainer = document.getElementsByClassName('mntl-taxonomysc-article-list-group')[0];
    const scrollOffset = parseInt(articleListsContainer.dataset.scrollOffset, 10) || 0;

    document.addEventListener('click', (e) => {
        const button = e.target.closest('.js-chop-button');

        if (!button) {
            return;
        }

        const container = button.closest('.mntl-card-list');
        const chunk = container.dataset.chunk;
        const items = container.getElementsByClassName('is-hidden');
        const firstNewItem = items[0];

        if (items.length) {
            for (let i = 0; i < Math.min(chunk, items.length); i++) {
                items[i].classList.remove('is-hidden');
            }
        }

        if (!items.length || items.length < chunk) {
            button.parentElement.removeChild(button); // IE11 does not support ChildNode.remove() https://developer.mozilla.org/en-US/docs/Web/API/ChildNode/remove
        }

        const scrollTo = window.pageYOffset + firstNewItem.getBoundingClientRect().top - scrollOffset;

        animationUtils.scrollY('down', scrollTo);
    });
})(Mntl.animationUtilities);

