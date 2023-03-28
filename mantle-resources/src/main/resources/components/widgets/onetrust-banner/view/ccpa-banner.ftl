<#compress>
<div id="onetrust-consent-sdk">
    <div class="onetrust-pc-dark-filter ot-hide ot-fade-in"></div>
    <div id="onetrust-banner-sdk" data-tracking-container="true" class="otFlat bottom vertical-align-content <#if model.showConsentBanner>show-banner</#if>" role="dialog" aria-describedby="onetrust-policy-text" tabindex="0">
        <div class="ot-sdk-container">
            <div class="ot-sdk-row">
                <div id="onetrust-group-container" class="ot-sdk-eight ot-sdk-columns">
                    <div class="banner_logo"></div>
                    <div id="onetrust-policy">
                        <!-- Mobile Close Button -->
                        <div id="onetrust-close-btn-container-mobile" class="ot-hide-large">
                            <button class="onetrust-close-btn-handler onetrust-close-btn-ui banner-close-button ot-mobile ot-close-icon" aria-label="Close" tabindex="0"></button>
                        </div>
                        <!-- Mobile Close Button END-->
                        <p id="onetrust-policy-text">${model.oneTrustDomainData.culture.DomainData.AlertNoticeText}</p>
                    </div>
                </div>
                <div id="onetrust-button-group-parent" class="ot-sdk-three ot-sdk-columns">
                    <div id="onetrust-button-group">
                        <button id="onetrust-pc-btn-handler" tabindex="0" class="cookie-setting-link">${model.oneTrustDomainData.culture.DomainData.AlertMoreInfoText}</button>
                        <button id="onetrust-accept-btn-handler" tabindex="0">${model.oneTrustDomainData.culture.DomainData.AlertAllowCookiesText}</button>
                    </div>
                </div><!-- Close Button -->
                <div id="onetrust-close-btn-container" class="ot-sdk-one ot-sdk-column ot-hide-small">
                    <button class="onetrust-close-btn-handler onetrust-close-btn-ui banner-close-button onetrust-lg ot-close-icon" aria-label="Close" tabindex="0"></button>
                </div><!-- Close Button END-->
            </div>
        </div>
    </div>
</div>
</#compress>