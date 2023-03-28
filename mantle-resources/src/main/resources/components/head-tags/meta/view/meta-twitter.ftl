<!-- Twitter Cards --> 
<meta name="twitter:card" content="${model.card}" />
<meta name="twitter:site" content="${model.site}" />
<#if model.title?has_content>
	<meta name="twitter:title" content="${model.title?replace("\"", "&quot;")}" />
</#if>
<#if model.description?has_content>
	<meta name="twitter:description" content="${model.description?replace("\"", "&quot;")}" />
</#if>
<#if (model.image.objectId)?has_content>
    <#if ((model.useUncappedImage)!false)>
        <meta name="twitter:image" content="<@thumborUrl img=model.image maxWidth=(model.image.width)!10000 filters=model.filters ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />" />
    <#else>
        <meta name="twitter:image" content="<@thumborUrl img=model.image maxWidth=1500 filters=model.filters />" />
    </#if>
</#if>
