window.Mntl = window.Mntl || {};

(function(utils, fnUtils) {
    const EMAIL_REQUIRED = 'Please enter your email address';
    const EMAIL_MALFORMED = 'Please provide your email address in format: yourname@example.com';
    const EMAIL_INVALID = 'Please try a different email address';
    const UNEXPECTED_ERROR = 'Unexpected Error. Try again.';

    function getFormValues(containerElement, querySelector, dataAttribute) {
        const values = containerElement.querySelectorAll(querySelector);
        const results = [];
        const attributeResults = [];

        values.forEach((value) => {
            let hasValue = false;

            if (value.type.toLowerCase() === 'checkbox' && value.checked) {
                results.push(value.value);
                hasValue = true;
            }

            if (value.type.toLowerCase() === 'hidden' && value.value) {
                results.push(value.value);
                hasValue = true;
            }

            const attributeValue = value.getAttribute(dataAttribute);

            if (hasValue && attributeValue) {
                attributeResults.push(attributeValue);
            }
        });

        if (results.length && attributeResults.length) {
            return {
                results,
                attributeResults
            }
        }

        return results;
    }

    function jsonToQueryString(json) {
        return Object.keys(json)
            .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(json[key])}`)
            .join('&');
    }

    function showError(textEl, wrapperEl, bannerEl, message) {
        textEl.innerHTML = message;
        wrapperEl.classList.add('has-error');
        bannerEl.classList.remove('is-hidden');
    }

    function hideError(wrapperEl, bannerEl, input) {
        wrapperEl.classList.remove('has-error');
        bannerEl.classList.add('is-hidden');

        if (input) {
            input.value = '';
        }
    }

    function setHidCookie(hashId) {
        const now = new Date();
        const expirationDate = new Date(now);

        expirationDate.setDate(now.getDate() + 365);
        window.docCookies.setItem('hid', hashId, expirationDate, '/', utils.getDomain());
    }

    function setupDataLayerEvents(dialog, regSourceId) {
        const isITO = Boolean(dialog.getAttribute('data-campaign-trigger-inactivity-timer'));

        if (isITO) {
            const campaignName = dialog.getAttribute('data-campaign-name');

            return {
                click() {
                    utils.pushToDataLayer({
                        event: 'transmitInteractiveEvent',
                        eventCategory: 'ITO',
                        eventAction: 'Clicked CTA',
                        eventLabel: campaignName,
                        registrationSource: regSourceId
                    })()
                },
                linkClick(newsletterLink) {
                    const linkText = fnUtils.trimAllWhitespace(newsletterLink.innerText);
                    const linkUrl = newsletterLink.getAttribute('href');

                    utils.pushToDataLayer({
                        event: 'transmitInteractiveEvent',
                        eventCategory: 'ITO',
                        eventAction: `Clicked Success Modal CTA`,
                        eventLabel: `{${campaignName}, ${linkText}, ${linkUrl}}`,
                        registrationSource: regSourceId
                    })();
                },
                success({eventLabel}) {
                    utils.pushToDataLayer({
                        event: 'transmitInteractiveEvent',
                        eventCategory: 'ITO',
                        eventAction: 'Newsletter Signup',
                        eventLabel: `${campaignName} - ${eventLabel}`,
                        registrationSource: regSourceId
                    })()
                }
            }
        }

        return {
            error: utils.pushToDataLayer({
                event: 'transmitNonInteractiveEvent',
                eventCategory: 'Newsletter',
                eventAction: 'Newsletter Modal Error',
                registrationSource: regSourceId
            }),
            success: utils.pushToDataLayer({
                event: 'transmitInteractiveEvent',
                eventCategory: 'Newsletter',
                eventAction: 'Newsletter Signup',
                registrationSource: regSourceId
            })
        }
    }

    function initEvents(containerElement) {
        const newsletterForm = containerElement.querySelector('.js-newsletter-form');
        const newsletterFormSuccess = containerElement.querySelector('.js-newsletter-form-success');
        const newsletterDialog = containerElement.closest('.mntl-dialog');
        const regSourceId = newsletterForm.getAttribute('data-regsource');
        const dataLayerEvents = setupDataLayerEvents(newsletterDialog, regSourceId);

        // email input
        const emailInput = containerElement.querySelector('.js-email-input');
        const emailInputError = containerElement.querySelector('.js-email-input-error .message-banner');
        const emailInputErrorText = containerElement.querySelector('.js-email-input-error .message-banner__text');
        const emailInputWrapper = containerElement.querySelector('.js-email-input-wrapper');
        const emailInputErrorClose = containerElement.querySelector('.js-email-input-error .js-message-banner-close');
        const submitButton = containerElement.querySelector('.js-submit-button');

        // date picker
        const datePicker = containerElement.querySelector('.js-date-picker');
        let monthInput;
        let dayInput;
        let yearInput;
        let datePickerError;
        let datePickerErrorText;
        let datePickerErrorClose;

        // subscriptions container
        const subscriptionsContainer = containerElement.querySelector('.js-subscriptions');
        let subscriptionsCheckboxes;
        let subscriptionsError;
        let subscriptionsErrorText;
        let subscriptionsErrorClose;

        if (subscriptionsContainer) {
            subscriptionsCheckboxes = subscriptionsContainer.querySelectorAll('.newsletter__checkbox');
            subscriptionsError = subscriptionsContainer.querySelector('.js-subscriptions-error .message-banner');
            subscriptionsErrorText = subscriptionsError.querySelector('.message-banner__text');
            subscriptionsErrorClose = subscriptionsError.querySelector('.js-message-banner-close');
        }

        if (datePicker) {
            monthInput = datePicker.querySelector('.js-month-input');
            dayInput = datePicker.querySelector('.js-day-input');
            yearInput = datePicker.querySelector('.js-year-input');
            datePickerError = datePicker.querySelector('.js-date-picker-error .message-banner');
            datePickerErrorText = datePickerError.querySelector('.message-banner__text');
            datePickerErrorClose = datePickerError.querySelector('.js-message-banner-close');
        }

        // When modal is closed, reset to default.
        newsletterDialog.addEventListener('hide', () => {
            newsletterForm.classList.remove('is-hidden');
            newsletterFormSuccess.classList.add('is-hidden');
            emailInput.value = '';
        });

        emailInput.addEventListener('focus', () => {
            hideError(emailInputWrapper, emailInputError, emailInput);
        });

        emailInputErrorClose.addEventListener('click', (event) => {
            event.preventDefault();
            hideError(emailInputWrapper, emailInputError, emailInput);
        });

        emailInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                submitButton.click();
            }
        });

        if (datePicker) {
            [monthInput, dayInput, yearInput].forEach((input) => {
                input.addEventListener('focus', () => {
                    hideError(datePicker, datePickerError, input);
                });

                input.addEventListener('keydown', (e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        submitButton.click();
                    }
                });
            });

            datePickerErrorClose.addEventListener('click', (event) => {
                event.preventDefault();

                [monthInput, dayInput, yearInput].forEach((input) => {
                    hideError(datePicker, datePickerError, input);
                });
            });
        }

        if (subscriptionsContainer) {
            subscriptionsCheckboxes.forEach((checkbox) => {
                checkbox.addEventListener('change', () => {
                    hideError(subscriptionsContainer, subscriptionsError, null);
                });
            });

            subscriptionsErrorClose.addEventListener('click', (event) => {
                event.preventDefault();
                hideError(subscriptionsContainer, subscriptionsError, null);
            })
        }

        newsletterForm.addEventListener('submit', (event) => {
            event.preventDefault();

            if (dataLayerEvents.click) dataLayerEvents.click();

            const newsletterPayload = {
                modelId: 'accountRegistration',
                regSourceId
            }
            let inputErrors = false;

            if (datePicker) {
                const dateInput = datePicker.querySelector('.js-newsletter__date-picker-date-input');

                dateInput.value = `${yearInput.value.padStart(4, '0')}-${monthInput.value.padStart(2, '0')}-${dayInput.value.padStart(2, '0')}`;
                const date = dateInput.value;
                const emptyValues = yearInput.value === "" && monthInput.value === "" && dayInput.value === "";

                // The date picker field is optional. Only process field if values are not empty.
                if (!emptyValues) {
                    if (!isNaN(Date.parse(date)) && dateInput.validity.valid) {
                        newsletterPayload.birthDate = date;
                    } else {
                        inputErrors = true;
                        showError(datePickerErrorText, datePicker, datePickerError, "Please enter a valid date");
                    }
                }
            }

            if (!emailInput.validity.valid) {
                inputErrors = true;
                let errorMessage = null;

                if (emailInput.validity.valueMissing) {
                    errorMessage = EMAIL_REQUIRED;
                } else if(emailInput.validity.typeMismatch) {
                    errorMessage = EMAIL_MALFORMED;
                }

                if (errorMessage) {
                    showError(emailInputErrorText, emailInputWrapper, emailInputError, errorMessage);
                    if (dataLayerEvents.error) dataLayerEvents.error({ eventLabel: errorMessage });
                }
            }

            let newsletterTitles = [];

            if (subscriptionsContainer) {
                const newsletters = getFormValues(subscriptionsContainer, '.newsletter__checkbox', 'data-title');
                let objectIds = [];

                if ('results' in newsletters) {
                    objectIds = newsletters.results;
                }
                if ('attributeResults' in newsletters) {
                    newsletterTitles = newsletters.attributeResults;
                }

                if (!objectIds.length && !newsletterTitles.length) {
                    inputErrors = true;
                    showError(subscriptionsErrorText, subscriptionsContainer, subscriptionsError, 'Please select a newsletter');
                } else {
                    newsletterPayload.newsletterObjectIds = objectIds.join(',');
                }
            }

            if (!inputErrors) {
                const objectors = getFormValues(containerElement, 'input[name="objectorObjectIds"]');

                if (objectors) {
                    newsletterPayload.objectorObjectIds = objectors.join(',');
                }
                newsletterPayload.email = emailInput.value;

                const getUrl = `/servemodel/model.json?${jsonToQueryString(newsletterPayload)}`;

                // GLBE-9646 sets timeout at 10 seconds for extremely slow connections to complete the ajax registration call to the server
                utils.ajaxPromiseGetCall(getUrl, 'error', 10000)
                    .then((response) => {
                        try {
                            const result = JSON.parse(response);

                            if (result.emailReputation === 'DO_NOT_EMAIL') {
                                showError(emailInputErrorText, emailInputWrapper, emailInputError, EMAIL_INVALID);
                                if (dataLayerEvents.error) dataLayerEvents.error({ eventLabel: EMAIL_INVALID });
                            } else if (result.emailReputation === 'SAFE_TO_EMAIL') {
                                // Tracking event
                                if (dataLayerEvents.success) dataLayerEvents.success({ eventLabel: newsletterTitles.join('|') });
                                // Setting hid cookie
                                if (result.hashId) {
                                    setHidCookie(result.hashId);
                                }
                                newsletterForm.classList.add('is-hidden');
                                newsletterFormSuccess.classList.remove('is-hidden');
                            } else {
                                showError(emailInputErrorText, emailInputWrapper, emailInputError, UNEXPECTED_ERROR);
                            }
                        } catch (error) {
                            showError(emailInputErrorText, emailInputWrapper, emailInputError, UNEXPECTED_ERROR);
                        }
                    })
                    .catch(() => {
                        showError(emailInputErrorText, emailInputWrapper, emailInputError, UNEXPECTED_ERROR);
                    });
            }
        });

        // Enable the submission button once the submission event listener is ready
        submitButton.removeAttribute('disabled');

        // Custom link tracking for success dialog.
        const successLinks = containerElement.querySelectorAll('.js-newsletter-form-success a');

        successLinks.forEach((successLink) => {
            successLink.addEventListener('click', () => {
                dataLayerEvents.linkClick(successLink);
            });
        });
    }

    const newsletterElements = document.querySelectorAll('.newsletter');

    for (const newsletterElement of newsletterElements) {
        const interval = setInterval(() => {
            if (!newsletterElement.querySelector('.js-newsletter-form')) return;
            initEvents(newsletterElement);
            clearInterval(interval);
        }, 500);
    }

})(window.Mntl.utilities || {}, window.Mntl.fnUtilities || {});