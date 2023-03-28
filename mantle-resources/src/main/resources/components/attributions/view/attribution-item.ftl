<@component class="${(model.date?has_content)?then('mntl-attribution__item--has-date', '')}">
    <@location name="beforeText" tag="" />

    <#if model.overrideFirstNonAuthorDescriptor && (model.isFirstItemInNonAuthorGroup || !(model.dataTriggerLink?has_content))>
        <@location name="non-author-descriptor-override" tag="" />
    <#elseif model.descriptor?has_content>
        <span class="mntl-attribution__item-descriptor">${model.descriptor}</span>
    </#if>

    <@optionalTag tag="${model.dataTooltip?has_content?then('div', '')}" data_tooltip="${model.dataTooltip!''}" data_inline_tooltip="true">

        <#--  If model.dataTriggerLink is null or '' then the data-trigger-link attribute will not be added in the html  -->
        <@a class="mntl-attribution__item-name" href="${model.uri!''}" safelist=true data_trigger_link="${model.dataTriggerLink!''}" forceNoSponsored=true>${model.name}</@a><#if model.isCommaSeparatedListItem><span>,</span></#if>

        <@location name="tooltip" tag="" />

    </@optionalTag>

    <#if model.postscript?has_content>
        <div class="mntl-attribution__item-postscript">${model.postscript}</div>
    </#if>
</@component>

<#if model.date?has_content>
    <div class="mntl-attribution__item-date<#if model.isNewDocument?has_content> mntl-attribution__item-date--new</#if>">${model.dateText!''}${model.date}</div>
</#if>