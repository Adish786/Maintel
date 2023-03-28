window.Mntl = window.Mntl || {};

Mntl.stickyTocWidget = (function() {
    const header = document.querySelector('.header');
    const stickyContainerSelector = '.js-sticky-container';
    const stickyWidgetSelector = '.sticky-toc-widget';
    const stickyContainerElem = document.querySelector(stickyContainerSelector);
    const stickyWidget = stickyContainerElem.querySelector(stickyWidgetSelector);
    const stickyToggle = stickyWidget.querySelector('.js-sticky-toggle');
    const appearElem = document.getElementsByClassName('js-toc-appear')[0] || stickyWidget;
    const desktopMatcher = window.matchMedia(`(min-width: ${stickyWidget.dataset.desktopBp})`);
    const stickyWidgetWrapper = stickyWidget.querySelector('.toc-wrapper');
    let headerHeight = header ? header.offsetHeight : 0;
    let stickyWidgetHeight;
    let stickybitsInstance;
    let appearElemBottom;
    let wasMobile = false;
    let resizeObserver;// eslint-disable-line no-unused-vars
    let isFixed = false;
    let isMobileToggleVisible = false;

    function getHeaderHeight() {
        const headerOffsetTop = header.getBoundingClientRect().top || 0;

        headerHeight = header ? header.offsetHeight : 0;

        return Math.max(headerHeight + headerOffsetTop, 0);
    }

    function adjustNavPos() {
        /* Adjust Nav for vertical header */
        stickyWidget.querySelector('.mntl-toc__inner').style.marginTop = `${getHeaderHeight()}px`;
    }

    function adjustTogglePos() {
        stickyToggle.style.top = `${getHeaderHeight()}px`;
    }

    function captureBottomPos() {
        /* Capture bottom scroll position of widget */
        appearElemBottom = window.pageYOffset + appearElem.getBoundingClientRect().bottom - headerHeight;
    }

    function collapseDrawers() {
        stickyWidget.classList.remove('is-expanded');
    }


    /* Sticky TOC only appears when scroll position is below the static TOC
     * 1. When TOC is "fixed", set a sticky widget height to keep constant spacing between "detached" and "fixed" states
     */
    function stickTOC() {
        /* Make Fixed */
        isFixed = true;
        stickyWidgetHeight = stickyWidgetWrapper.offsetHeight;
        stickyWidget.classList.add('is-fixed', 'is-expanded');
        adjustNavPos();
        stickyWidget.style.height = `${stickyWidgetHeight}px`; /* 1 */
        captureBottomPos();
    }

    function unstickTOC() {
        /* Make Detached */
        isFixed = false;
        collapseDrawers();
        stickyWidget.querySelector('.mntl-toc__inner').style.marginTop = 0;
        stickyWidget.style.height = 'auto'; /* 1 */
        stickyWidget.classList.remove('is-fixed');
        captureBottomPos();
    }

    function handleScroll() {
        const scrollPos = window.scrollY || window.scrollTop;

        isMobileToggleVisible = stickyToggle.classList.contains('is-visible');

        if (scrollPos > appearElemBottom) {
            if (!isMobileToggleVisible) {
                stickyToggle.classList.add('is-visible');
            }
        } else if (isMobileToggleVisible) {
            stickyToggle.classList.remove('is-visible');
            unstickTOC();
        }

        setTimeout(() => {
            if (isMobileToggleVisible && headerHeight !== header.offsetHeight) {
                adjustTogglePos();
            }
        }, 300);
    }

    function toggleDrawer() {
        isFixed && stickyWidget.classList.toggle('is-expanded');
    }

    /*
     * Mobile Toggle
     * 1. On initial toggle click, move the TOC node and open the drawer.
     * 2. Subsequent click, toggle open and close drawers.
     */
    function handleMobileToggle() {
        if (isFixed) {
            toggleDrawer(); /* 2 */
        } else {
            stickTOC(); /* 1 */
        }
    }

    function handleTapOffCollapse(e) {
        /* Collapse nav if tapped away */
        if (!stickyWidget.querySelector('.mntl-toc__list').contains(e.target) &&
            !stickyWidget.querySelector('.mntl-toc__inner').contains(e.target) &&
            stickyWidget.classList.contains('is-expanded')) {
            collapseDrawers();
        }
    }

    function setupMobile() {
        wasMobile = true;
        captureBottomPos();
        adjustTogglePos();
        stickyWidget.querySelector('.mntl-toc-toggle').addEventListener('click', captureBottomPos);
        stickyWidget.querySelector('.js-toc-toggle').addEventListener('click', captureBottomPos);

        /* Set Height of StickyWidget */
        stickyWidgetHeight = stickyWidgetWrapper.offsetHeight;

        /* Handle expand/collapse of TOC nav */
        stickyWidget.querySelectorAll('.link__wrapper').forEach((item) => {
            item.addEventListener('click', toggleDrawer);
        });

        stickyToggle.addEventListener('click', handleMobileToggle);

        document.querySelector('body').addEventListener('click', handleTapOffCollapse);
        window.addEventListener('scroll', handleScroll);
    }

    /**
     * Add extra 15 pixels padding so the widget doesn't touch the header
     */
    function initStickWidget() {
        const finalVerdictContentBlock = document.querySelector('.theme-finalverdict');
        const stickyWidgetCta = stickyWidget.querySelector('.sticky-toc-widget__cta') || null;

        /* Set correct Header height for when window or elements change size */
        getHeaderHeight();

        if (desktopMatcher.matches) {
            wasMobile = false;
            /* Remove mobile functionality if necessary */
            stickyWidget.classList.remove('is-fixed');
            window.removeEventListener('scroll', handleScroll);

            /* Set rail height if there is a Final Verdict Content block */
            if (stickyWidgetCta && finalVerdictContentBlock) {
                stickyContainerElem.style.height = `${finalVerdictContentBlock.offsetTop}px`;
            }

            /* Stickybits uses position: sticky which requires the sticky container to be the direct DOM parent.
             * If the element we designated as the container via class `.js-sticky-container` is not the direct parent,
             * then we need to check if they are top aligned so that the sticking will start at same position, provided
             * height: 100% is set on the parent in the CSS. If they are not top aligned, then we need to explicitly
             * set the height of the parent element to account for the top position difference.
             */
            if (stickyContainerElem !== stickyWidget.parentElement
                && stickyContainerElem.offsetTop !== stickyWidget.parentElement.offsetTop) {
                const stickyParentHeight = stickyContainerElem.offsetHeight - stickyWidget.parentElement.offsetTop;

                stickyWidget.parentElement.style.height = `${stickyParentHeight}px`;
            }

            stickybitsInstance = stickybits(stickyWidgetSelector, { stickyBitStickyOffset: headerHeight + 15 });
        } else {
            /* Undo tablet landscape and desktop settings */
            if (stickybitsInstance) {
                stickybitsInstance.cleanup();
            }

            if (stickyWidget.parentElement.style.height !== '') {
                stickyWidget.parentElement.style.height = '';
            }

            !wasMobile && setupMobile(); /* hasMobile flag prevents setupMobile from running multiple times on resize */
        }
    }

    function init($context) {
        // TODO: Deprecate this in 3.15 for final axe of jquery cleanup.
        const context = $context.jquery ? $context[0] : $context;
        let resizeThrottle;

        initStickWidget();

        if (typeof ResizeObserver !== 'undefined') { /* IE 11 is not supported */
            resizeObserver = new ResizeObserver(() => { // eslint-disable-line no-unused-vars
                clearTimeout(resizeThrottle);
                resizeThrottle = setTimeout(initStickWidget, 30);
            });
            
            resizeObserver.observe(context);

            if (header){
                resizeObserver.observe(header);
            }
        }
    }

    window.onresize = initStickWidget;

    desktopMatcher.addListener(() => {
        initStickWidget();
    });

    return { init };
}());

Mntl.utilities.readyAndDeferred(Mntl.stickyTocWidget.init);
