<#assign classes = "">
<#list manifest.component.referenceIdChain as reference><#assign classes>${classes} ${reference}</#assign></#list>
<#if model.class?has_content>
    <#if model.class?is_sequence>
        <#list model.class as class>
            <#assign classes = classes + " " + class />
        </#list>
    <#else>
        <#assign classes = classes + " " + model.class />
    </#if>
</#if>
<#assign href = "${(model.link.uri)!(model.uri)!'#'}" />
<#assign text = "${(model.link.text)!(model.text)!''}" />
<#assign defaultSafelisting = !(href?starts_with('#')) />
<@a
    href="${href}"
    id="${manifest.instanceId}"
    class="${classes}"
    rel="${(model.rel)!''}"
    target="${((model.link.external)!(model.external)!false)?then('_blank','')}"
    data_tracking_container="${(model.trackingContainer!true)?c}"
    safelist=model.safelist!defaultSafelisting
    external=model.external
    tag="${(model.tag)!''}"
><#compress>
    <#if text?has_content>
        <span class="link__wrapper">${text}</span>
    </#if>
    <@location name="content" tag="" />
</#compress></@a>
