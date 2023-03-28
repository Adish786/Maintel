<@component tracking_container=true style=getDigitalIssueCssVariables(model.digitalIssueCssVariables)>
    <@location name="background" tag="" />
    <@location name="anchor" tag="" />

    <#if model.title?has_content>
        <h2 class="mntl-di-module__title ${model.titleClass!'type--unicorn'}">
            ${model.title}
        </h2>
    </#if>

    <@location name="cards" tag="" />
</@component>