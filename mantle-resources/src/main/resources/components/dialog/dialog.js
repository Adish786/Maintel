(function() {
    function setupDataLayerEvents(dialog) {
        const isITO = Boolean(dialog.getAttribute('data-campaign-trigger-inactivity-timer'));
        const isNewsletter = Boolean(dialog.querySelector('.newsletter'));

        if (isITO) {
            const campaignName = dialog.getAttribute('data-campaign-name');
            const regSourceId = dialog.getAttribute('data-campaign-regsource');

            return {
                show: window.Mntl.utilities.pushToDataLayer({
                    event: 'transmitNonInteractiveEvent',
                    eventCategory: 'ITO',
                    eventAction: 'Modal Prompt',
                    eventLabel: campaignName,
                    registrationSource: regSourceId
                }),
                hide: window.Mntl.utilities.pushToDataLayer({
                    event: 'transmitInteractiveEvent',
                    eventCategory: 'ITO',
                    eventAction: 'Closed Modal',
                    eventLabel: campaignName,
                    registrationSource: regSourceId
                })
            }
        }

        if (isNewsletter) {
            const newsletterForm = dialog.querySelector('.newsletter__form');
            const regSourceId = newsletterForm ? newsletterForm.getAttribute('data-regsource') : '';

            return {
                show: window.Mntl.utilities.pushToDataLayer({
                    event: 'transmitNonInteractiveEvent',
                    eventCategory: 'Newsletter',
                    eventAction: 'Newsletter Modal Impression',
                    eventLabel: '',
                    registrationSource: regSourceId
                })
            }
        }
    }

    function setupListeners(a11yDialog, dialog) {
        const dataLayerEvents = setupDataLayerEvents(dialog);

        dialog.addEventListener('show', () => {
            document.documentElement.classList.add('dialog-is-shown');
            if (dataLayerEvents && dataLayerEvents.show) dataLayerEvents.show();
        });

        dialog.addEventListener('hide', () => {
            const mntlDialogCampaign = document.querySelector('.mntl-dialog--campaign');

            document.documentElement.classList.remove('dialog-is-shown');
            if (mntlDialogCampaign) mntlDialogCampaign.classList.remove('dialog--visible');
            if (dataLayerEvents && dataLayerEvents.hide) dataLayerEvents.hide();
        });

        // Adding this event listener so defer/ajax loaded content inside dialog could
        // provide a link with `data-dialog-hide` to close the current dialog.
        dialog.addEventListener('click', (event) => {
            if (event.target && event.target.hasAttribute('data-dialog-hide')) {
                a11yDialog.hide();
            }
        });
    }

    function setupInactivityTrigger(a11yDialog, dialog) {
        if (!dialog.hasAttribute('data-campaign-trigger-inactivity-timer')) return;

        let inactivityDialog;
        let inactivityTriggerDefaultConfig = {
            suppressionSelectors: '.jwplayer, .jumpstart-js, .jumpstart-gif-js',
            suppressionPercentage: 0,
            suppressionHook() {
                return;
            }
        };

        const timer = Number(dialog.getAttribute('data-campaign-trigger-inactivity-timer'));

        function setCampaignCookies() {
            const thirtyDays = 2592000;
            const ddmCampaignExtendedValue = window.docCookies.getItem('ddmCampaignExtended') || 0

            window.docCookies.setItem('ddmCampaignSession', '', null, '/', window.Mntl.utilities.getDomain());
            window.docCookies.setItem('ddmCampaignExtended', Number(ddmCampaignExtendedValue) + 1, thirtyDays, '/', window.Mntl.utilities.getDomain());
        }

        function isPartiallyInViewport(element, percentage) {
            const rect = element.getBoundingClientRect();

            if (!rect.height || !rect.width) return

            const threshold = 1 - percentage;
            const height = rect.height * threshold;
            const top = rect.top + height;
            const bottom = rect.bottom - height;
            const viewportHeight = window.innerHeight || document.documentElement.clientHeight;

            return top >= 0 && bottom <= viewportHeight;
        }

        function isSuppressed() {
            if (window.Mntl && window.Mntl.Dialog && window.Mntl.Dialog.inactivityTrigger) inactivityTriggerDefaultConfig = {
                ...inactivityTriggerDefaultConfig,
                ...window.Mntl.Dialog.inactivityTrigger
            };

            const {
                suppressionSelectors,
                suppressionPercentage,
                suppressionHook
            } = inactivityTriggerDefaultConfig;

            if (suppressionHook && suppressionHook()) return true;

            const elements = document.querySelectorAll(suppressionSelectors);

            return Array.from(elements).some((e) => isPartiallyInViewport(e, suppressionPercentage));
        }

        function dialogTimer() {
            clearTimeout(inactivityDialog);

            inactivityDialog = setTimeout(() => {
                if (window.docCookies.hasItem('ddmCampaignSession') || window.docCookies.hasItem('hid')) {
                    document.removeEventListener('mousemove', dialogTimer);
                    document.removeEventListener('scroll', dialogTimer);

                    return;
                };

                if (isSuppressed()) return;

                const mntlDialogCampaign = document.querySelector('.mntl-dialog--campaign');

                mntlDialogCampaign.classList.add('dialog--visible');
                a11yDialog.show();
                setCampaignCookies();

            }, timer);
        }

        dialog.addEventListener('show', () => {
            document.removeEventListener('mousemove', dialogTimer);
            document.removeEventListener('scroll', dialogTimer);
        });

        document.addEventListener('mousemove', dialogTimer);
        document.addEventListener('scroll', dialogTimer);

        dialogTimer();
    }

    function setupDialogs() {

        function initializeA11yDialog(dialog) {
            const a11yDialog = new A11yDialog(dialog);

            setupListeners(a11yDialog, dialog);
            setupInactivityTrigger(a11yDialog, dialog);
        }

        function setupMutationCallback(dialog) {
            return function (mutations, observer) {
                mutations.forEach((mutation) => {
                    if (mutation.type === 'childList') {
                        initializeA11yDialog(dialog);
                        observer.disconnect();
                    }
                });
            }
        }

        const dialogs = document.querySelectorAll('.dialog');

        dialogs.forEach((dialog) => {
            if (dialog.innerHTML) {
                initializeA11yDialog(dialog);
            } else {
                const observer = new MutationObserver(setupMutationCallback(dialog));
                const config = { childList: true };

                observer.observe(dialog, config);
            }
        });
    }

    function detectUserScrollbar() {
        // In order to better fix CARBON-578
        // we must detect the user's scrollbar width as on mac the user may have scrollbar disabled.
        const scroller = document.scrollingElement;
        const root = document.documentElement;

        // Force scrollbars to display
        scroller.style.setProperty('overflow', 'scroll');

        // Wait for next from so scrollbars appear
        requestAnimationFrame(() => {
            const scrollbarWidth = window.innerWidth - scroller.clientWidth;

            // Width of the scrollbar
            root.style.setProperty('--scrollbar-width', `${scrollbarWidth}px`);

            // Reset overflow
            scroller.style.setProperty('overflow', '');
        });
    }

    function init() {
        setupDialogs();
        detectUserScrollbar();
    }

    Mntl.utilities.onLoad(init);
}());