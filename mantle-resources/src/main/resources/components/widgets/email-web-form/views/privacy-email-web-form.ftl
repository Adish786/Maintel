<@component tag="form" action="${model.actionEndpoint!''}" method="POST">
    <@location name="header" tag="" />

    <#-- TODO: TMog will become User ID Cookie in the future so we should flag this. -->
    <input type="hidden" name="TMog" value="${model.userId!''}"></input>

    <#if (model.requestOptions)?has_content >
        <p class="mntl-email-form__text">
            Please select which request you'd like to make:
        </p>

        <select class="mntl-email-form__select" name="Request Type" aria-label="Choose your request type">
            <#list model.requestOptions as option, text>
                <option class="mntl-email-form__select--options" value="${option}">${text}</option>
            </#list>
        </select>
    </#if>

    <p class="mntl-email-form__text">
        You can select one of two options for submitting your request.
    </p>

    <div class="mntl-email-form__background">
        <div class="mntl-email-form__instructions--numbered">
            <p class="mntl-email-form__step">
                1. Fill out this form.
            </p>

            <input class="mntl-email-input__name" type="text" name="name" placeholder="Name" aria-label="Input your name" required></input>
            <input class="mntl-email-input__email" type="email" name="email" placeholder="Email Address" aria-label="Input your email address" required></input>

            <p class="mntl-email-form__text">
                We will send your information or a deletion confirmation to this email address. We’ll use your email address for this purpose only.
            </p>

            <p class="mntl-email-form__flexblock">
                <input class="mntl-email-form__checkbox" type="checkbox" id="agreement" name="agreement">
                <label for="agreement">
                    I affirm that the information provided on this form is accurate, that I am the person whose name appears
                    on the form, and that I am a California resident. I understand that ${model.vertical!'Dotdash'}, a Dotdash company, will contact me
                    at the email address provided to verify I am the person making this request.
                </label>
            </p>

            <button id="mntl-email-form__button--submit" class="mntl-button mntl-email-form__button" type="submit" disabled>
                Submit
            </button>
        </div>
    </div>

    <@location name="footer" tag="" />
</@component><!--end mntl-email-form-->

<div class="mntl-email__block mntl-email-form__manual-instructions">
    <div class="mntl-email-form__background">
        <p class="mntl-email-form__step">
            2. Or send us an email.
        </p>

        <p class="mntl-email-form__text">
            Please copy this auto-generated message into an email body and send it to
            <a href="mailto:ccpaPrivacy@dotdash.com">ccpaPrivacy@dotdash.com</a>
            with the subject “CCPA Information/Deletion Request”.
        </p>

        <p class="mntl-email-form__example">
            Hello -
            <#if (model.requestOptions)?has_content >
                I am making a request to <span id="mntl-email-form__example--option-text" class="mntl-option__text" data-select-form="Request Type">${(model.requestOptions?values[0]!'')?lower_case}</span>.
            </#if>
            My TMog value is <span class="mntl-email-form__text--tmog">${model.userId!''}</span>. I am making my request from ${model.vertical!'Dotdash'}. I affirm that
            the information in this email is accurate, that I am the owner of this email account, and that I am a
            California resident. I understand that ${model.vertical!'Dotdash'}, a Dotdash company, will contact me
            at this email address to verify I am the person making this request. Thank you.
        </p>
    </div>
</div>