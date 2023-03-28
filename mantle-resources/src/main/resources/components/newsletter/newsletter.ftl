<@component>
    <form class="newsletter__form js-newsletter-form" action="" novalidate data-regsource="${model.accountRegistration.id}">
        <#if model.logoIcon?has_content>
            <div class="newsletter__logo">
                <@svg name=model.logoIcon />
            </div>
        </#if>

        <#if model.isTitleEnabled && model.accountRegistration.title?has_content>
            <p class="newsletter__title">
                ${model.accountRegistration.title}
            </p>
        </#if>

        <#if model.isSubtitleEnabled && model.accountRegistration.subtitle?has_content>
            <p class="newsletter__subtitle">
                ${model.accountRegistration.subtitle}
            </p>
        </#if>

        <#if model.accountRegistration.description?has_content>
            <p class="newsletter__description ${model.descriptionClass!''}">
                ${model.accountRegistration.description}
            </p>
        </#if>

        <div class="newsletter__email-address-wrapper js-email-input-wrapper">
            <label for="${manifest.instanceId}-email" class="newsletter__email-address ${model.labelClass!''}">Email address</label>
            <input type="email" name="email" id="${manifest.instanceId}-email" class="newsletter__email-address-input js-email-input" placeholder="${model.emailPlaceholder!'yourname@example.com'}" required />

            <#-- TODO: The div below is to mimic location div when we move message-banner component to mantle -->
            <div class="js-email-input-error">
                <#-- TODO: in the future message-banner component should be moved from carbon into mantle to be used here -->
                <div class="message-banner message-banner--error is-input-error is-hidden">
                    <@svg name=model.errorIcon classes="message-banner__prefix-icon message-banner__icon" />
                    <span class="message-banner__text"></span>
                    <button class="message-banner__close-button js-message-banner-close" aria-label="Close Message">
                        <@svg name=model.closeIcon classes="message-banner__icon" />
                    </button>
                </div>
            </div>
        </div>

        <#if model.displayDOBPicker == true>
            <div class="newsletter__date-picker js-date-picker">
                <p class="newsletter__date-picker-title ${model.datePickerTitleClass!''}">
                    Due Date/Child's Birthday
                    <span class="newsletter__optional-label">
                        (optional)
                    </span>
                </p>

                <div class="newsletter__date-picker-container">
                    <div class="newsletter__date-picker-labels">
                        <label for="${manifest.instanceId}-month" class="newsletter__date-picker-month-label ${model.labelClass!''}">Month</label>
                        <label for="${manifest.instanceId}-day" class="newsletter__date-picker-day-label ${model.labelClass!''}">Day</label>
                        <label for="${manifest.instanceId}-year" class="newsletter__date-picker-year-label ${model.labelClass!''}">Year</label>
                    </div>
                    <div class="newsletter__date-picker-inputs">
                        <div class="newsletter__date-picker-month-input-wrapper">
                            <input type="number" name="month" id="${manifest.instanceId}-month" class="newsletter__date-picker-month-input js-month-input" placeholder="MM" min="1" max="12" required />
                        </div>
                        <div class="newsletter__date-picker-day-input-wrapper">
                            <input type="number" name="day" id="${manifest.instanceId}-day" class="newsletter__date-picker-day-input js-day-input" placeholder="DD" min="1" max="31" required />
                        </div>
                        <div class="newsletter__date-picker-year-input-wrapper">
                            <input type="number" name="year" id="${manifest.instanceId}-year" class="newsletter__date-picker-year-input js-year-input" placeholder="YYYY" min="1900" max="2100" required />
                        </div>
                    </div>
                </div>

                <input id="${manifest.instanceId}-year" class="js-newsletter__date-picker-date-input" type="date" pattern="\d{2}-\d{2}-\d{4}" min="1900-01-01" max="2100-12-31" hidden />

                <#-- TODO: The div below is to mimic location div when we move message-banner component to mantle -->
                <div class="js-date-picker-error">
                    <#-- TODO: in the future message-banner component should be moved from carbon into mantle to be used here -->
                    <div class="message-banner message-banner--error is-input-error is-hidden">
                        <@svg name=model.errorIcon classes="message-banner__prefix-icon message-banner__icon" />
                        <span class="message-banner__text"></span>
                        <button class="message-banner__close-button js-message-banner-close" aria-label="Close Message">
                            <@svg name=model.closeIcon classes="message-banner__icon" />
                        </button>
                    </div>
                </div>
            </div>
        </#if>

        <div class="newsletter__subscriptions-container js-subscriptions">
            <#assign isSubscriptionListHidden = model.isSubscriptionListHidden!false/>
            <div class="newsletter__subscriptions-wrapper ${isSubscriptionListHidden?then('is-hidden','')}">
                <div class="newsletter__subscriptions-title ${model.subscriptionsClass!''}">${model.subscriptionsHeading}</div>
                <ul class="newsletter__subscriptions-list">
                    <#list model.accountRegistration.registrationSourceTargets as target>
                        <#if target?? && target.type?has_content>
                            <#if target.type == "newsletter">
                                <li class="newsletter__subscriptions-item">
                                    <input type="checkbox"
                                        name="newsletterObjectIds"
                                        value="${target.uid}"
                                        id="${manifest.instanceId}-${target.uid}"
                                        class="newsletter__checkbox"
                                        data-title="${target.title}"
                                        <#if (target.precheck)!false>checked</#if> />
                                    <label class="newsletter__label ${model.checkboxLabelClass!''}" for="${manifest.instanceId}-${target.uid}">${target.title}</label>
                                </li>
                            <#elseif target.type == "objector">
                                <#-- Objectors are brand-level content flags. Currently we are submitting them as hidden fields to auto opt-in the users.
                                * Acquisition = we can email you from the brand to promotion the purchase of Meredith products (magazines, paid products, etc.)
                                * Relationship Builder = we can email you from the brand to promote content/newsletters/affliate_offers/product updates/feature updates/brand updates/etc.
                                Improve/promote your relationship with the brand (the reason for the name of the field)
                                -->
                                <input type="hidden"
                                    name="objectorObjectIds"
                                    value="${target.uid}" />
                            </#if>
                        </#if>
                    </#list>
                </ul>
            </div>

            <div class="js-subscriptions-error">
                <div class="message-banner message-banner--error is-input-error is-hidden">
                    <@svg name=model.errorIcon classes="message-banner__prefix-icon message-banner__icon" />
                    <span class="message-banner__text"></span>
                    <button class="message-banner__close-button js-message-banner-close" aria-label="Close Message">
                        <@svg name=model.closeIcon classes="message-banner__icon" />
                    </button>
                </div>
            </div>
        </div>

        <#--  Disable the submission button until the submission event listener is ready in newsletter.js  -->
        <button type="submit" name="button" class="newsletter__email-address-button js-submit-button" disabled>
            ${(model.accountRegistration.submitButtonText)!'I\'m In!'}
        </button>

        <#if model.closeText?has_content>
            <button data-dialog-hide type="button" class="newsletter__close-button">
                ${(model.closeText)}
            </button>
        </#if>

    </form>

    <div class="newsletter__form-success is-hidden js-newsletter-form-success">
        <#if model.isSuccessViewCustom>
            <@location name="success-content" tag="" />
        <#else>
            <@svg name=model.successIcon />

            <p class="newsletter__form-success-message ${model.successMessageClass!''}">
                ${model.successMessage}
            </p>

            <button data-dialog-hide class="newsletter__form-success-button js-newsletter-success-button">
                Continue Reading
            </button>
        </#if>
    </div>
</@component>