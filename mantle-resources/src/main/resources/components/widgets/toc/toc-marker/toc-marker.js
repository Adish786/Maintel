Mntl = window.Mntl || {};

(function() {
    const header = document.querySelector('.header');
    const headerHeight = header ? header.offsetHeight : 0;
    const widget = document.querySelector('.sticky-toc-widget');
    const nav = widget.querySelector('.mntl-toc');
    const drawerTrigger = nav.getElementsByClassName('js-sticky-toggle')[0];
    const listItems = Array.prototype.slice.call(nav.querySelectorAll('.mntl-toc__list-item-link') || []);
    let isScrolling = false;

    if (!nav) {
        return;
    }

    function setActiveItem(itemsArr, activeItem, activeIndex) {
        let itemsLength = itemsArr.length;
        let progressPercent = ((activeIndex + 1) / itemsLength * 100);

        itemsArr.forEach((item, index) => {
            item.classList.remove('active');

            if (index < activeIndex) {
                item.classList.add('is-passed');
            } else {
                item.classList.remove('is-passed');
            }
        });

        drawerTrigger.classList.remove('is-progress-10', 'is-progress-30', 'is-progress-50', 'is-progress-70', 'is-progress-90', 'is-progress-100');
        activeItem.classList.add('active');

        if (progressPercent <= 20) {
            drawerTrigger.classList.add('is-progress-10');
        } else if (progressPercent <= 40) {
            drawerTrigger.classList.add('is-progress-30');
        } else if (progressPercent <= 60) {
            drawerTrigger.classList.add('is-progress-50');
        } else if (progressPercent <= 80) {
            drawerTrigger.classList.add('is-progress-70');
        } else if (activeIndex + 1 === itemsLength) {
            drawerTrigger.classList.add('is-progress-100');
        } else {
            drawerTrigger.classList.add('is-progress-90');
        }
    }

    /**
     * calculate the position we want to scroll to of the target element minus height of fixed elements
     *
     * @param {Elem} target
     * @return {Number}
     */
    function getTargetPos(target) {
        return target.getBoundingClientRect().top + window.pageYOffset - headerHeight;
    }

    function init() {
        // Add click handlers to each nav item
        listItems.forEach((item, index) => {
            item.addEventListener('click', () => {
                setActiveItem(listItems, item, index);
            });
        });
    }

    /**
     * sets the current nav item as active depending on the user's scroll position
     */
    function setActiveNav() {
        let target;
        let scrollPos;
        let i;

        for (i = listItems.length - 1; i >= 0; i--) {
            target = listItems[i].href.substring(listItems[i].href.indexOf('#'));
            scrollPos = getTargetPos(document.querySelector(target));

            if (!isScrolling) {
                // check if the top of the window is above the scroll position of the last nav item (give 1px allowance for rounding)
                if (window.pageYOffset >= scrollPos - 10) {
                    if (!listItems[i].classList.contains('active')) {
                        setActiveItem(listItems, listItems[i], i);
                    }

                    return;
                }
            }
        }
    }

    nav.addEventListener('mntl-toc:goToHash', () => {
        isScrolling = true;

        setTimeout(() => {
            isScrolling = false;
        }, 1000);
    });

    init();
    window.addEventListener('mntl.scroll', setActiveNav);
})();
