<#macro thumborUrl img maxWidth maxHeight=0 fitInStyle="" forceSize=false cropSetting="" filters=[imageFilter.noUpscale()]>
<#compress>
	<#if img?is_hash>
		<#assign imageUrl = img.objectId />
	<#else>
		<#assign imageUrl = img />
	</#if>
	${utils.getThumborUrl(imageUrl, maxWidth, maxHeight, fitInStyle, forceSize, requestContext, cropSetting, filters)}
</#compress>
</#macro>

<@thumborUrl img=model.img maxWidth=model.width maxHeight=model.height filters=model.filters />