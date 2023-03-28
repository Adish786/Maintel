<#assign logoutUrl><@absoluteHref href="${model.logoutUrl}" /></#assign>
<@component tag="${model.tag!'a'}" data\-auth0\-logout\-url="${logoutUrl}">
    <#if (model.prefixIcon?has_content) >
        <@svg name=model.prefixIcon />
    </#if>
    <#if model.cta?has_content>
        ${model.cta}
    </#if>
</@component>