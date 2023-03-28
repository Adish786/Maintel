// eslint-disable-next-line no-unused-vars
window.Mntl = window.Mntl || {};

(function() {
    let buttons = document.getElementsByClassName('mntl-guide-crumb__button');

    function openDropdown(button) {
        button.parentNode.classList.add('is-active');
    }

    function closeDropdown(button) {
        button.parentNode.classList.remove('is-active');
    }

    function init(button) {
        button.addEventListener('click', (e) => {
            if (!button.parentNode.classList.contains('is-active')) {
                openDropdown(button);
            } else if (!e.target.classList.contains('js-accordion-item')) {
                closeDropdown(button);
            }
        });

        document.addEventListener('click', (e) => {
            if (button.parentNode.classList.contains('is-active') &&
                !button.parentNode.contains(e.target)) {
                closeDropdown(button);
            }
        });
    }

    if (!buttons.length) {
        return;
    }

    for (let i = 0; i < buttons.length; i++) {
        init(buttons[i]);
    }
})();
