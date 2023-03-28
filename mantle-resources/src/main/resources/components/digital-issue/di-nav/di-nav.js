(function() {
    const html = document.querySelector('html');
    const nav = document.querySelector('.mntl-di-nav');
    const navHeader = nav.querySelector('.mntl-di-nav__header');
    const multipleSectionTitles = [...nav.querySelectorAll('.mntl-di-nav__multiple-section-title-wrapper')];

    const EXPANDED_CLASS = 'is-expanded';
    const ACTIVE_CLASS = 'is-active';

    /**
     * Toggle the display of the digital issue nav
     *
     * @return {undefined}
     */
    function toggleNav() {
        nav.classList.toggle(ACTIVE_CLASS);
        html.classList.toggle('mntl-di-nav-active');
    }

    /**
     * Hide/show an accordion section (mobile) if it's been clicked. Hide the current one
     * if a new one is opened instead.
     *
     * @param e the click event on an accordion section
     *
     * @return {undefined}
     */
    function handleAccordionClick(sectionTitle) {
        const openAccordion = nav.querySelector(`.mntl-di-nav__section--multiple.${EXPANDED_CLASS}`);
        const targetAccordion = sectionTitle.parentElement;

        if (openAccordion && (openAccordion !== targetAccordion)) {
            openAccordion.classList.remove(EXPANDED_CLASS);
        }

        targetAccordion.classList.toggle(EXPANDED_CLASS);
    }

    // Toggle mobile accordions on title click
    multipleSectionTitles.forEach((sectionTitle) => {
        sectionTitle.addEventListener('click', handleAccordionClick.bind(null, sectionTitle));
    });

    // Close nav with esc
    document.addEventListener('keydown', (e) => {
        if (nav.classList.contains(ACTIVE_CLASS) && e.key === 'Escape') {
            toggleNav();
        }
    });

    // Close nav on body click or outside focus
    ['click', 'focusin'].forEach((event) => {
        document.addEventListener(event, (e) => {
            if (nav.classList.contains(ACTIVE_CLASS) && !nav.contains(e.target)) {
                toggleNav();
            }        
        });
    });

    // Toggle nav on click
    navHeader.addEventListener('click', toggleNav);
})();