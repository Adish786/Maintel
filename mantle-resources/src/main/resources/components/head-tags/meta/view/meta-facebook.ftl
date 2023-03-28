<!-- Facebook Open Graph Tags -->
<meta property="fb:app_id" content="${model.appId}" />
<meta property="og:type" content="${model.type}" />
<meta property="og:site_name" content="${model.siteName}" />
<meta property="og:url" content="${model.url?html}" />
<#if model.title?has_content>
	<meta property="og:title" content="${model.title?replace("\"", "&quot;")}" />
</#if>
<#if model.description?has_content>
	<meta property="og:description" content="${model.description?replace("\"", "&quot;")}" />
</#if>
<#if (model.image.objectId)?has_content>
	<#if ((model.useUncappedImage)!false)>
    	<meta property="og:image" content="<@thumborUrl img=model.image maxWidth=(model.image.width)!10000 filters=model.filters ignoreMaxBytes=utils.uncappedImageWidthsEnabled() />" />
    <#else>
        <meta property="og:image" content="<@thumborUrl img=model.image maxWidth=1500 filters=model.filters />" />
    </#if>
</#if>
<#if (model.opinionEnabled)!false && (model.opinion)?has_content>
    <meta property="article:opinion" content="${model.opinion?c}" />
</#if>
<#-- FB allows for multiple article:author tags containing FB profile IDs or URLs -->
<#if model.socialPresences?has_content>
	<#list model.socialPresences as presence>
		<#if presence.network == "facebook">
			<meta property="article:author" content="${presence.url}" />
		</#if>
	</#list>
</#if>