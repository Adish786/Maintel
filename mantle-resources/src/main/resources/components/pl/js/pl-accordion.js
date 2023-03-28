window.PatternLibrary = window.PatternLibrary || {};

window.PatternLibrary.Accordion = (function() {
    const CLASS_OPENED = 'is-open';
    const CLASS_CLOSED = 'is-closed';
    const TOGGLE_ACCORDION = 'toggle.accordion';
    const DATA_ACCORDION_CLOSED = 'accordion-closed';

    function Accordion(accordion, headers, contents) {
        this.accordion = accordion;
        this.headers = headers;
        this.contents = contents;
        this.addEventListeners();
        this.scrollToSelectedItem();
    }

    Accordion.prototype.scrollToSelectedItem = function() {
        const sidebar = document.querySelector('.mntl-pl-sidebar')
        const selectedItem = sidebar.querySelector('.pl-categories__link.is-selected:not(.accordion__trigger)')

        if (!selectedItem) return

        const { top } = selectedItem.getBoundingClientRect()

        sidebar.scrollTop = top - 50
    }

    Accordion.prototype.addEventListeners = function() {
        for (let i = 0; i < this.contents.length; i++) {
            this.contents[i].addEventListener(TOGGLE_ACCORDION, this.toggleContent);
            this.headers[i].addEventListener('click', this.triggerToggleContent);
        }
    };

    Accordion.prototype.triggerToggleContent = function(event) {
        const target = event.currentTarget;

        event.preventDefault();
        target.classList.toggle(CLASS_OPENED);
        target.nextElementSibling.dispatchEvent(new CustomEvent(TOGGLE_ACCORDION));

        if (target.getAttribute(DATA_ACCORDION_CLOSED) === '1') {
            target.setAttribute(DATA_ACCORDION_CLOSED, '0');
        } else {
            target.setAttribute(DATA_ACCORDION_CLOSED, '1');
        }
    };

    Accordion.prototype.toggleContent = function(event) {
        event.stopPropagation();
        event.target.classList.toggle(CLASS_CLOSED);
    };

    function init(element, headers, contents) {
        new Accordion(element, headers, contents); // eslint-disable-line no-new
    }

    return { init };
}());

(function() {
    const accordion = document.getElementsByTagName('ul')[0]; // First list is the top level list for PL accordion
    const headers = accordion.getElementsByClassName('accordion__trigger');
    const contents = accordion.getElementsByClassName('accordion__content');

    window.PatternLibrary.Accordion.init(accordion, headers, contents);
}());
