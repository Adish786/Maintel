<#--  this DOM is copied from OneTrust's banner and is expected to be replaced entirely in phase 2  -->

<#compress>
<@location name="top" tag="" />
<div id="onetrust-consent-sdk">
    <div id="onetrust-banner-sdk" data-tracking-container="true" class="otFlat vertical-align-content ot-iab-2 bottom <#if model.showConsentBanner>show-banner</#if>">
        <div class="ot-sdk-container">
            <div class="ot-sdk-row">
                <div id="onetrust-group-container" class="ot-sdk-eight ot-sdk-columns">
                    <div id="onetrust-policy">
                        <#if model.oneTrustDomainData.culture.DomainData.BannerTitle?has_content>
                            <h3 id="onetrust-policy-title">${model.oneTrustDomainData.culture.DomainData.BannerTitle}</h3>
                        </#if>
                        <#-- Mobile Close Button -->
                        <div id="onetrust-close-btn-container-mobile" class="ot-hide-large">
                            <button class="onetrust-close-btn-handler onetrust-close-btn-ui banner-close-button ot-mobile ot-close-icon" aria-label="${model.oneTrustDomainData.culture.DomainData.BannerCloseButtonText}"></button>
                        </div>
                        <#-- Mobile Close Button END-->

                        <#-- this was originally a <p> tag but needed to be a <div> so it could render properly -->
                        <div id="onetrust-policy-text">
                            ${model.oneTrustDomainData.culture.DomainData.AlertNoticeText}
                            <#if model.oneTrustDomainData.culture.DomainData.BannerIABPartnersLink?has_content>
                                <a class="onetrust-vendors-list-handler" role="button" href="javascript:void(0)">${model.oneTrustDomainData.culture.DomainData.BannerIABPartnersLink}</a>
                            </#if>
                        </div>
                        <div class="ot-dpd-container">
                            <#if model.oneTrustDomainData.culture.DomainData.BannerDPDTitle?has_content>
                                <h3 class="ot-dpd-title">${model.oneTrustDomainData.culture.DomainData.BannerDPDTitle}</h3>
                            </#if>
                            <div class="ot-dpd-content">
                                <#if model.oneTrustDomainData.culture.DomainData.BannerDPDDescription?has_content>
                                    <p class="ot-dpd-desc">${model.oneTrustDomainData.culture.DomainData.BannerDPDDescription[0]}
                                        <a class="onetrust-vendors-list-handler" role="button" href="javascript:void(0)" tabindex="0">${model.oneTrustDomainData.culture.DomainData.BannerIABPartnersLink}</a>
                                    </p>
                                </#if>
                            </div>
                        </div>
                    </div>
                    <@location name="center" tag="" />
                </div>
                <div id="onetrust-button-group-parent" class="ot-sdk-three ot-sdk-columns">
                    <div id="onetrust-button-group">
                        <button id="onetrust-accept-btn-handler" title="${model.oneTrustDomainData.culture.DomainData.AlertAllowCookiesText} Cookies">${model.oneTrustDomainData.culture.DomainData.AlertAllowCookiesText}</button>
                        <button id="onetrust-pc-btn-handler" tabindex="0" class="cookie-setting-link">${model.oneTrustDomainData.culture.DomainData.AlertMoreInfoText}</button>
                    </div>
                </div>

                <#-- Close Button -->
                <div id="onetrust-close-btn-container" class="ot-sdk-one ot-sdk-column ot-hide-small">
                    <button class="onetrust-close-btn-handler onetrust-close-btn-ui banner-close-button onetrust-lg ot-close-icon" aria-label="${model.oneTrustDomainData.culture.DomainData.BannerCloseButtonText}"></button>
                </div>
                <#-- Close Button END-->
            </div>
        </div>

        <@location name="bottom" tag="" />
    </div>
    <div class="onetrust-pc-dark-filter ot-fade-in"></div>
</div>
</#compress>