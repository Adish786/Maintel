(function(utils) {
    const articleFeedbacks = document.getElementsByClassName('mntl-article-feedback');
    const THUMBS_UP = 'THUMBS_UP';
    const THUMBS_DOWN = 'THUMBS_DOWN';
    let hasSubmitted = false;

    /**
     * showSuccessMessage - helper function used to reveal the sucess message div
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     */
    function showSuccessMessage(articleFeedback) {
        const successMessage = articleFeedback.getElementsByClassName('js-success-section')[0];

        successMessage.classList.remove('is-hidden');
    }

    /**
     * hideFeedbackSection - helper function used to hide the feedback section
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     */
    function hideFeedbackSection(articleFeedback) {
        const feedbackSection = articleFeedback.getElementsByClassName('js-feedback-section')[0];

        feedbackSection.classList.add('is-hidden');
    }

    /**
     * submitFeedback function used to pass the form and send it to our submit end point. Will resolve visual components
     * on sucess or failure
     *
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     * @param {String} thumbsSignal the signal used to pass down to the submit action
     * @param {String} comment the comment used to pass down to the submit action
     */
    function submitFeedback(articleFeedback, thumbsSignal, comment) {
        const feedbackForm = articleFeedback.getElementsByClassName('js-feedback-form')[0];
        const docIdInput = feedbackForm.getElementsByClassName('js-doc-id')[0];

        // Data for POST request
        const formURL = feedbackForm.getAttribute('action');
        const formData = {
            docId: `${docIdInput.value}`,
            thumbsSignal,
            comment: comment || ''
        };

        // GLBE-9646 sets timeout at 10 seconds for extremely slow connection for internal tool 
        utils.ajaxPromisePost(formURL, formData, 10000)
            .then(() => {
                showSuccessMessage(articleFeedback);
                hideFeedbackSection(articleFeedback);
                articleFeedback.classList.add(`article-feedback--${thumbsSignal.toLowerCase().replace('_', '-')}`);
            })
            .catch(() => {
                articleFeedback.classList.add('article-feedback--error');
            });
    }

    /**
     * setupRatingsButtons - Helper function that sets up the thumbs up/down ratings buttons
     * for the article feedback component
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     */
    function setupRatingsButtons(articleFeedback) {
        const jsRatingButtons = articleFeedback.getElementsByClassName('js-rating-button');
        // js-ratings-section wraps the text prompt and the thumbs up/ thumbs down button and will be hidden on click
        const ratingSection = articleFeedback.getElementsByClassName('js-rating-section')[0];
        const feedbackSection = articleFeedback.getElementsByClassName('js-feedback-section')[0];
        const feedbackForm = articleFeedback.getElementsByClassName('js-feedback-form')[0];

        [...jsRatingButtons].forEach((button) => {
            const thumbsSignal = button.dataset.thumbsSignal;

            button.addEventListener('click', () => {
                ratingSection.classList.add('is-hidden');

                if (thumbsSignal === THUMBS_UP) {
                    submitFeedback(articleFeedback, THUMBS_UP);
                } else {
                    // Unhide feedback form for users to submit
                    feedbackSection.classList.remove('is-hidden');
                    feedbackForm.classList.remove('is-hidden');
                }
            }, { once: true });
        });
    }

    /**
     * setupSubmitFeedbackButton - helper function used to setup the submit feedback buttons
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     */
    function setupSubmitFeedbackButton(articleFeedback) {
        // This element may optionally be there in the vertical
        const jsSubmitFeedbackButtons = articleFeedback.getElementsByClassName('js-submit-feedback-button');

        [...jsSubmitFeedbackButtons].forEach((button) => {
            button.addEventListener('click', () => {
                submitFeedback(articleFeedback, THUMBS_DOWN, button.innerText);
            }, { once: true });
        });
    }

    /**
     * setupForm - helper function used to setup the form and form buttons if the open form button prompts are clicked
     * @param {HTMLCollection Object} articleFeedback the HTMLCollection element of the specific article feedback
     * component. This is passed down to support the ability to instansiate multiple article feedback components at once.
     */
    function setupForm(articleFeedback) {
        const openFormButtons = articleFeedback.getElementsByClassName('js-open-form-button');
        const feedbackForm = articleFeedback.getElementsByClassName('js-feedback-form')[0];

        [...openFormButtons].forEach((button) => {
            button.addEventListener('click', () => {
                button.classList.add('article-feedback__open-form-button--active');
                feedbackForm.classList.remove('is-hidden');
            },
            { once: true });
        });
    }

    /**
     * formSubmit - eventHandler used for the submit action for the feedback form
     * @param {event object} event
     */
    function formSubmit(event) {
        const feedbackForm = event.currentTarget;
        const feedbackText = feedbackForm.getElementsByClassName('js-feedback-text')[0].value;
        const articleFeedback = utils.getClosestMatchingParent(feedbackForm, '.mntl-article-feedback');

        event.preventDefault();

        if (!hasSubmitted && feedbackText) {
            hasSubmitted = true;
            submitFeedback(articleFeedback, THUMBS_DOWN, feedbackText);
        }
    }

    // Setup block
    [...articleFeedbacks].forEach((articleFeedback) => {
        const feedbackForm = articleFeedback.getElementsByClassName('js-feedback-form')[0];

        setupRatingsButtons(articleFeedback);
        setupSubmitFeedbackButton(articleFeedback);
        setupForm(articleFeedback);
        feedbackForm.addEventListener('submit', formSubmit);
    });
})(window.Mntl.utilities || {});
