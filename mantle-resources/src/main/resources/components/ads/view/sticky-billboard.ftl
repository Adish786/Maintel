<#assign dataHeight = 600 />
<#if (model.rightRailTrackHeight?is_number)>
    <#assign dataHeight = model.rightRailTrackHeight?c />
</#if>

<#assign styleHeight = "" />
<#if (requestContext.userAgent.deviceCategory != 'tablet')>
    <#if (model.rightRailTrackHeight?is_number)>
        <#assign styleHeight = "height: " + model.rightRailTrackHeight?c + "px;" />
    <#elseif ((model.rightRailTrackHeight)?has_content)>
        <#assign styleHeight = "height: " + model.rightRailTrackHeight + ";" />
    </#if>
</#if>

<@component data\-height="${dataHeight}" style="${styleHeight}">
    <@location name="ad" tag="" />
</@component>
