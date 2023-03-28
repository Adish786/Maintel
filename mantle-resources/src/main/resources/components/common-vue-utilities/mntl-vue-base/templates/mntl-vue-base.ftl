<#if model.appId??>
    <#assign appId = model.appId + utils.parseInstanceId(manifest.instanceId)  />
<#else>
    <#assign appId = utils.camelCaseInstanceId(manifest.instanceId) />
</#if>

<#assign loadingText = model.loadingText!"Loading shell for ${appId} vue props component in Globe." />

<div id="${appId}">
    ${loadingText}
</div>
