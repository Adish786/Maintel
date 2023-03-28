<#if (model.image.objectId)?has_content && (model.image.width)?has_content && (model.image.height)?has_content>
,"image": {
    <#assign dimRatio=(model.image.width/model.image.height) />
    <#assign resizeWidth=(model.image.width >= 1500 && !utils.uncappedImageWidthsEnabled())?then(1500, model.image.width)?round />
    <#assign resizeHeight=(model.image.width >= 1500 && !utils.uncappedImageWidthsEnabled())?then(1500 / dimRatio, model.image.height)?round />
    "@type": "ImageObject",
    <#if utils.uncappedImageWidthsEnabled()>
        "url": "<@thumborUrl img=model.image maxWidth=(model.image.width)!10000 filters=model.filters ignoreMaxBytes=true />",
    <#else>
        "url": "<@thumborUrl img=model.image maxWidth=1500 filters=model.filters />",
    </#if>
    "height": ${resizeHeight?c},
    "width": ${resizeWidth?c}
}
<#elseif (model.image.objectId)?has_content>
,"image": {
    "@type": "ImageObject",
    <#if utils.uncappedImageWidthsEnabled()>
        "url": "<@thumborUrl img=model.image maxWidth=(model.image.width)!10000 filters=model.filters ignoreMaxBytes=true />"
    <#else>
        "url": "<@thumborUrl img=model.image maxWidth=1500 filters=model.filters />"
    </#if>
}
<#elseif (model.image.url)?has_content>
,"image": {
    "@type": "ImageObject",
    "url": "${model.image.url?json_string}"
}
</#if>