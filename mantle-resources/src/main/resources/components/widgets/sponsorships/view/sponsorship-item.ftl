<#assign showTitleAndHasContent = model.showTitle && model.title?has_content />
<#assign showDisclaimerAndHasContent = model.showDisclaimer && model.disclaimerText?has_content />

<@component tag="">
    <@optionalTag class="sponsorship-item__description" tag="${(showTitleAndHasContent && showDisclaimerAndHasContent)?then('div', '')}">
        <#if showTitleAndHasContent>
            <span class="sponsorship-item__title">${model.title}</span>
        </#if>

        <#if showDisclaimerAndHasContent>
            <span class="sponsorship-item__disclaimer" data-tooltip="${model.disclaimerText}">${model.disclaimer}</span>
        </#if>
    </@optionalTag>

    <@a href="${model.url}" class="sponsorship-item__link" data_tracking_container="true">
        <@location name="image" tag="" />
    </@a>

    <#if model.showSeparator && model.separator?has_content>
        <span class="sponsorship-item__separator">${model.separator}</span>
    </#if>
</@component>