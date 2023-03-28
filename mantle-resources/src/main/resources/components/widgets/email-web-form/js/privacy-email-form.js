(function(utils) {
    /**
     * Helper function that sets up a change event listener on the select element to update text in another dom element
     * for users to use as a template for emailing to the privacy email manually
     */
    function setupSelectTextChange() {
        const [selectElement] = document.getElementsByClassName('mntl-email-form__select');

        // Update text in a div following the select options changes
        selectElement.addEventListener('change', () => {
            const dataSelector = `[data-select-form="${selectElement.value}"]`;
            const textFields = document.querySelectorAll(dataSelector);

            [...textFields].forEach((textField) => {
                textField.innerText = selectElement.options[selectElement.selectedIndex].text.toLowerCase();
            });
        });
    }

    /**
     * Helper function that sets up the privacy form checkbox to enable/disable the submit button state.
     * Users are expected to have "agreed" to the terms before submitting a privacy request.
     */
    function setupFormCheckbox() {
        const [formCheckbox] = document.getElementsByClassName('mntl-email-form__checkbox');

        formCheckbox.addEventListener('change', () => {
            const formButton = document.getElementById('mntl-email-form__button--submit');

            if (formCheckbox.checked) {
                formButton.removeAttribute('disabled');
            } else {
                formButton.setAttribute('disabled', true);
            }
        });
    }

    /**
     * Helper function that creates a generic div for the form response message to the user
     * @param {string} messageClass additional class for the message div
     * @param {string} messageText
     */
    function createMessageDiv(messageClass, messageText) {
        const messageDiv = document.createElement('div');

        messageDiv.classList.add('mntl-email-form__message', messageClass);
        messageDiv.innerText = messageText;

        return messageDiv;
    }

    /**
     * Helper function that sets up the privacy form submit action
     */
    function setupForm() {
        const [formElement] = document.getElementsByClassName('mntl-privacy-email-form');

        formElement.addEventListener('submit', (event) => {
            event.preventDefault();
            const button = document.getElementById('mntl-email-form__button--submit');

            button.setAttribute('disabled', true);

            const formData = new FormData(formElement);

            // GLBE-9646 sets timeout at 10 seconds for extremely slow connections to complete the ajax registration call to the server
            utils.ajaxPromisePost(formElement.action, formData, 10000)
                .then(() => {
                    const [formInstructionsElem] = document.getElementsByClassName('mntl-email-form__manual-instructions');
                    const message = 'Thank you for submitting your request under the California Consumer Privacy Act (CCPA). We will contact you via the email you provided soon.';
                    const messageClass = 'mntl-email-form--success';
                    const messageDiv = createMessageDiv(messageClass, message);

                    formInstructionsElem.innerHTML = '';
                    formElement.innerHTML = '';
                    formElement.append(messageDiv);
                })
                .catch(() => {
                    const errMessage = 'Error: Email could not be sent. Please try again later or manually send an email to ccpaprivacy@dotdash.com using the instructions below.';
                    const errMessageClass = 'mntl-email-form--error';
                    const messageDiv = createMessageDiv(errMessageClass, errMessage);

                    formElement.append(messageDiv);
                    button.setAttribute('disabled', false);
                });
        });
    }

    function init() {
        setupSelectTextChange();
        setupFormCheckbox();
        setupForm();
    }

    init();
}(window.Mntl.utilities || {}));
