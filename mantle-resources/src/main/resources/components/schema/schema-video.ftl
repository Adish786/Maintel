<script type="application/ld+json">
<#compress>
<#escape x as x?json_string>
{
    "@context": "http://schema.org",
    "@type": "VideoObject",
    "description": "${(model.document.description!'')}",
    "name": "${model.document.title}",
    <#if (model.document.getImageForUsage('PRIMARY').objectId)?has_content>
        "thumbnailUrl": "<@thumborUrl img=model.document.getImageForUsage('PRIMARY') maxWidth=model.document.getImageForUsage('PRIMARY').width maxHeight=model.document.getImageForUsage('PRIMARY').height ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />",
    </#if>
    "uploadDate": "${model.document.dates.displayed}",
    "embedUrl": "//players.brightcove.net/${model.accountId}/${model.playerId}_default/index.html?videoId=ref:${model.document.refId}"
}
</#escape>
</#compress>
</script>
